package com.znjt.rpc;

import com.znjt.boot.Boot;
import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.exs.ExceptionInfoUtils;
import com.znjt.proto.*;
import com.znjt.service.GPSTransferService;
import com.znjt.utils.FileIOUtils;
import com.znjt.utils.LoggerUtils;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.channel.ChannelOption;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by qiuzx on 2019-03-14
 * Company BTT
 * Depart Tech
 *
 * @author qiuzx
 */
public class TransporterClientProxy {
    private final Logger logger = LoggerFactory.getLogger(TransporterClientProxy.class);
    private String addr;
    private int port;
    private int max_batch_size;
    private JobCounterStreamObserver responseStreamObserver;
    private TransferServiceGrpc.TransferServiceStub stub;
    private TransferServiceGrpc.TransferServiceBlockingStub blockingStub;
    private ManagedChannel managedChannel;
    private TransferRespJobUtils transferRespJobUtils;
    private Object monitor = new Object();
    private GPSTransferService gpsTransferService;
    private static int DEADLINE = 60;

    private long total_img_bytes_size = 0;
    private long total_img_count = 0;
    private final long unit = 1024 * 1024;
    private final int BUFSIZE = 512 * 1024;

    TransporterClientProxy(GPSTransferService localTransferService, String addr, int port, int max_batch_size) {
        this.addr = addr;
        this.port = port;
        gpsTransferService = localTransferService;
        this.max_batch_size = max_batch_size;
        transferRespJobUtils = new TransferRespJobUtils(localTransferService, monitor, max_batch_size);
    }

    /**
     * ManagedChannel对象重量级的，需要重用
     *
     * @return
     */
    private ManagedChannel getManagedChannel() {
        if (managedChannel == null) {
            synchronized (this) {
                if (managedChannel == null) {
                    //批量处理时存在4194304字节的限制（可以通过创建多个ManagedChannel，轮询使用提高效率）
                    //managedChannel = ManagedChannelBuilder.forAddress(addr, port).usePlaintext().build();

                    managedChannel = NettyChannelBuilder.forAddress(addr, port)
                            .negotiationType(NegotiationType.PLAINTEXT)
                            .keepAliveTime(5, TimeUnit.MINUTES)
                            .keepAliveTimeout(10, TimeUnit.SECONDS)
                            .withOption(ChannelOption.SO_SNDBUF, BUFSIZE)
                            .withOption(ChannelOption.SO_RCVBUF, BUFSIZE)
                            //.maxRetryAttempts()
                            //.usePlaintext() 过期，建议使用negotiationType
                            .keepAliveWithoutCalls(true)
                            .build();

                }
            }
        }
        //验证channel是否是正常未被关闭的
        if (managedChannel != null) {
            if (managedChannel.isShutdown() || managedChannel.isTerminated()) {
                managedChannel = getManagedChannel();
            }
        }
        return managedChannel;
    }

    /**
     * Stub对象是轻量级的，但是也是可以重用的
     *
     * @return
     */
    private TransferServiceGrpc.TransferServiceStub getAsyncStub() {
        if (stub == null) {
            synchronized (this) {
                if (stub == null) {
                    ManagedChannel channel = getManagedChannel();
                    if (channel != null) {
//                        if (!(channel.isTerminated() || channel.isShutdown())) {
//                            stub = TransferServiceGrpc.newStub(channel);
//                        }
                        //getManagedChannel中已经判断channel的有效性
                        stub = TransferServiceGrpc.newStub(channel);
                    }
                }
            }
        }
        //DEADLINE时间过期，我们可以为每个Stub配置deadline时间，么如果此stub被使用的时长超过此值（不是空闲的时间），将不能再发送请求，此时我们应该创建新的Stub
        //withDeadlineAfter() 会在原有的 stub 基础上新建一个 stub，然后如果我们为每次 RPC 请求都单独创建一个有设置 deadline 的 stub，就可以实现所谓单个 RPC 请求的 timeout 设置
        return stub.withDeadlineAfter(DEADLINE, TimeUnit.SECONDS);
        //return stub;
    }

    /**
     * 对象重用
     *
     * @return
     */
    private TransferServiceGrpc.TransferServiceBlockingStub getSyncStub() {
        if (blockingStub == null) {
            synchronized (this) {
                if (blockingStub == null) {
                    ManagedChannel channel = getManagedChannel();
                    if (channel != null) {
//                        if (!(channel.isTerminated() || channel.isShutdown())) {
//                            blockingStub = TransferServiceGrpc.newBlockingStub(channel);
//                        }
                        blockingStub = TransferServiceGrpc.newBlockingStub(channel);
                    }
                }
            }
        }
        return blockingStub.withDeadlineAfter(DEADLINE, TimeUnit.SECONDS);
    }

    /**
     *
     * @param total_byts
     * @param mins 毫秒数
     * @return
     */
//    private long calNetSpeed(long total_byts,long mins){
//        long speed = total_byts;
//        if(mins>0){
//            speed = total_byts*1000/mins;
//        }
//        return speed;
//    }

    /**
     * 同步批量上传数据
     *
     * @param datas
     */
    public UpLoadReson transferData2ServerBySync4Batch(List<GPSTransferIniBean> datas) {
        UpLoadReson upLoadReson = new UpLoadReson();

        if (datas != null && datas.size() > 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("开始组织需要上传的[" + datas.size() + "]含图像的记录给服务器");
            }
            Instant instant = Instant.now();
            SyncMulImgRequestWrapper wrapper = createMulRequest(datas);
            SyncMulImgRequest syncMulImgRequest = wrapper.getSyncMulImgRequest();
            TransferServiceGrpc.TransferServiceBlockingStub syncStub = getSyncStub();
            SyncMulImgResponse syncMulImgResponse = null;

            //同步保障只有一个线程在批量传输，放置网络阻塞
            synchronized (TransporterClientProxy.class) {
                try {
                    if (logger.isWarnEnabled()) {
                        logger.warn("开始上传[" + datas.size() + "]含图像的记录给服务器");
                    }
                    Instant beg = Instant.now();
                    syncMulImgResponse = syncStub.transporterMulBySync(syncMulImgRequest);
                    long cons = Duration.between(beg, Instant.now()).toMillis();

                    upLoadReson.setConsumeMins(cons);
                    upLoadReson.setTotalBytes(wrapper.getTotal_bys());
                    if (logger.isWarnEnabled()) {
                        logger.warn("服务器响应[" + datas.size() + "]含图像的记录处理完成，耗时[" + cons + "] ms.");
                    }
                } catch (StatusRuntimeException ex) {
                    boolean print = true;
                    String em = ex.getMessage();
                    if (StringUtils.isNotBlank(em) && em.contains("UNAVAILABLE: io exception")) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Server 服务不可用，请检查Server服务是否开启");
                        }
                        print = false;
                    }
                    if (print) {
                        logger.warn(ExceptionInfoUtils.getExceptionCauseInfo(ex));
                    }
                    throw ex;
                }
            }

            if (syncMulImgResponse.getDataType() == DataType.T_GPS) {
                List<GPSRecord> records = syncMulImgResponse.getGpsRecordList();
                int updates = doneSyncResponseDatas(records);
                upLoadReson.setUpdate_records(updates);
            }
            if (logger.isWarnEnabled()) {
                logger.warn("批量上传的后续完成，所有操作共计耗时[" + Duration.between(instant, Instant.now()).toMillis() + "] ms");
            }
        }
        return upLoadReson;
    }

    /**
     * 同步批量上传数据
     *
     * @param datas
     */
    public void transferData2ServerBySyncSingleRecord4Batch(List<GPSTransferIniBean> datas) {
        if (datas != null && datas.size() > 0) {
            if (logger.isWarnEnabled()) {
                logger.warn("{EatchSingleBatch}开始组织需要上传的[" + datas.size() + "]含图像的记录给服务器");
            }
            Instant instant = Instant.now();
            SyncMulSingleImgRequest syncMulSingleImgRequest = createMulSingleRequest(datas);
            TransferServiceGrpc.TransferServiceBlockingStub syncStub = getSyncStub();

            SyncMulSingleImgResponse syncMulSingleImgResponse = null;
            try {
                if (logger.isWarnEnabled()) {
                    logger.warn("{EatchSingleBatch}开始上传[" + datas.size() + "]含图像的记录给服务器");
                }
                Instant beg = Instant.now();
                syncMulSingleImgResponse = syncStub.transporterMulSingleBySync(syncMulSingleImgRequest);
                if (logger.isWarnEnabled()) {
                    logger.warn("{EatchSingleBatch}服务器响应[" + datas.size() + "]含图像的记录处理完成，耗时[" + Duration.between(beg, Instant.now()).toMillis() + "] ms.");
                }
            } catch (Exception ex) {
                boolean print = true;
                if (ex instanceof StatusRuntimeException) {
                    StatusRuntimeException se = (StatusRuntimeException) ex;
                    String em = se.getMessage();
                    if (StringUtils.isNotBlank(em) && em.contains("UNAVAILABLE: io exception")) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Server 服务不可用，请检查Server服务是否开启");
                        }
                        print = false;
                    }
                }
                if (print) {
                    logger.warn(ExceptionInfoUtils.getExceptionCauseInfo(ex));
                }
                return;
            }
            if (syncMulSingleImgResponse.getDataType() == DataType.T_GPS_SINGLE) {
                List<GPSSingleRecord> records = syncMulSingleImgResponse.getGpsSingleRecordList();
                doneSyncSingleResponseDatas(records);
            }
            if (logger.isWarnEnabled()) {
                logger.warn("{EatchSingleBatch}批量上传的后续完成，所有操作共计耗时[" + Duration.between(instant, Instant.now()).toMillis() + "] ms");
            }
        }
    }


    /**
     * 处理同步请求的响应结果（更新到数据库）
     *
     * @param records
     */
    private void doneSyncSingleResponseDatas(List<GPSSingleRecord> records) {
        if (records == null) {
            logger.warn("处理同步请求结果的doneSyncResponseDatas()接收到Null的参数，无法处理..直接忽略");
            return;
        }
        final List<GPSTransferIniBean> dbs = new ArrayList<>();
        Optional.ofNullable(records).ifPresent(rds -> {
            rds.forEach(item -> {
                GPSTransferIniBean gtb = transferRespJobUtils.createGPSGpsTransferIniBeanFromGPSSingleRecord(item);
                Optional.ofNullable(gtb).ifPresent(x -> {
                    dbs.add(x);
                });
            });
        });
        if (dbs.size() > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("批量更新图像数据上传结果，影响记录数[ " + dbs.size() + " ]条");
            }
            gpsTransferService.updateCurrentUploadedSuccessGPSImgRecords(Boot.DOWNSTREAM_DBNAME, dbs);
        }
    }

    /**
     * 处理同步请求的响应结果（更新到数据库）
     *
     * @param records
     */
    private int doneSyncResponseDatas(List<GPSRecord> records) {
        if (records == null) {
            logger.warn("处理同步请求结果的doneSyncResponseDatas()接收到Null的参数，无法处理..直接忽略");
            return 0;
        }
        final List<GPSTransferIniBean> dbs = new ArrayList<>();
        Optional.ofNullable(records).ifPresent(rds -> {
            rds.forEach(item -> {
                GPSTransferIniBean gtb = transferRespJobUtils.createGPSGpsTransferIniBeanFromGPSRecord(item);
                Optional.ofNullable(gtb).ifPresent(x -> {
                    dbs.add(x);
                });
            });
        });
        if (dbs.size() > 0) {
            gpsTransferService.updateCurrentUploadedSuccessGPSImgRecords(Boot.DOWNSTREAM_DBNAME, dbs);
            LoggerUtils.info(logger, "批量更新图像数据上传结果，影响记录数[ " + dbs.size() + " ]条");
        }
        return dbs.size();
    }

    /**
     * 同步上传测试数据，测试上传速度
     *
     * @param path
     */
    public void transferData2ServerBySync(String path) {
        SyncDataRequest request = createTestRequestObj(path);
        try {
            Instant instant = Instant.now();
            LoggerUtils.info(logger, "开始上传" + path + "到Savle...");
            TransferServiceGrpc.TransferServiceBlockingStub stubs = getSyncStub();
            SyncDataResponse response = stubs.transporterBySyncTest(request);
            LoggerUtils.info(logger, "上传一条记录到Savle...结束，共计耗时：" + Duration.between(instant, Instant.now()).toMillis() + " ms");
        } catch (StatusRuntimeException ex) {
            throw ex;
        }
    }

    /**
     * 同步方式发送上传数据的操作
     *
     * @param datas
     */
    public void transferData2ServerBySync(List<GPSTransferIniBean> datas) {
        if (datas != null && datas.size() > 0) {
            for (GPSTransferIniBean item : datas) {
                SyncDataRequest request = createRequestObj(item);
                try {
                    Instant instant = Instant.now();
                    LoggerUtils.info(logger, "开始上传一条记录的图像到Savle...包含 " + request.getGpsRecord().getImgDataCount() + " 张图像.");
                    TransferServiceGrpc.TransferServiceBlockingStub stubs = getSyncStub();
                    SyncDataResponse response = stubs.transporterBySync(request);
                    LoggerUtils.info(logger, "上传一条记录的图像到Savle...结束，共计耗时：" + Duration.between(instant, Instant.now()).toMillis() + " ms");
                    if (response.getDataType() == DataType.T_GPS) {
                        GPSRecord record = response.getGpsRecord();
                        Optional.ofNullable(record).ifPresent(rd -> {
                            doneSyncResponseDatas(Arrays.asList(rd));
                        });
                    }
                } catch (StatusRuntimeException ex) {
                    throw ex;
                }
            }
        }
    }

    /**
     * 创建同步批处理请求对象
     *
     * @param gpsTransferIniBeans
     * @return
     */
    private SyncMulSingleImgRequest createMulSingleRequest(List<GPSTransferIniBean> gpsTransferIniBeans) {
        List<GPSSingleRecord> gpsRecords = new ArrayList<>();
        gpsTransferIniBeans.forEach(item -> {
            gpsRecords.addAll(createGPSSingleRecordBeans(item));
        });
        return SyncMulSingleImgRequest.newBuilder().setDataTypeValue(DataType.T_GPS_SINGLE_VALUE).addAllGpsSingleRecord(gpsRecords).build();
    }

    /**
     * 创建同步批处理请求对象
     *
     * @param gpsTransferIniBeans
     * @return
     */
    private SyncMulImgRequestWrapper createMulRequest(List<GPSTransferIniBean> gpsTransferIniBeans) {
        SyncMulImgRequestWrapper wrapper = new SyncMulImgRequestWrapper();
        total_img_bytes_size = 0;
        total_img_count = 0;
        List<GPSRecord> gpsRecords = new ArrayList<>();
        Instant instant = Instant.now();
        gpsTransferIniBeans.forEach(item -> {
            gpsRecords.add(createGPSRecordBean(item));
        });
        LoggerUtils.info(logger, "共计读取图片 [" + total_img_count + "] 张,共计 [" + total_img_bytes_size / unit + "] MB，总计耗时 [" + Duration.between(instant, Instant.now()).toMillis() + "] ms");
        wrapper.setSyncMulImgRequest(SyncMulImgRequest.newBuilder().setDataType(DataType.T_GPS).addAllGpsRecord(gpsRecords).build());
        wrapper.setTotal_bys(total_img_bytes_size);
        return wrapper;
    }

    private SyncDataRequest createTestRequestObj(String path) {
        GPSRecord gpsRecord = createGPSRecordBean(path);
        return SyncDataRequest.newBuilder().setDataType(DataType.T_GPS_SINGLE_TEST).setGpsRecord(gpsRecord).build();
    }

    private SyncDataRequest createRequestObj(GPSTransferIniBean item) {
        GPSRecord gpsRecord = createGPSRecordBean(item);
        return SyncDataRequest.newBuilder().setDataType(DataType.T_GPS).setGpsRecord(gpsRecord).build();
    }

    private List<GPSSingleRecord> createGPSSingleRecordBeans(GPSTransferIniBean item) {
        List<GPSSingleRecord> gsrs = new ArrayList<>();
        String data_id = getDataid(item);
        String file_path = item.getOriginalUrl();
        String base_path = item.getBaseDir();
        String[] file_paths = null;
        if (StringUtils.isNotBlank(file_path)) {
            file_path = file_path.replaceAll("(\r\n|\r|\n|\n\r)", "");
            //多个路径是通过;分割的
            file_paths = file_path.split(";");
        }
        List<byte[]> imgs = null;
        GPSSingleRecord gpsSingleRecord = null;
        if (file_paths != null) {
            if (StringUtils.isBlank(base_path)) {
                base_path = "";
            }
            for (String fp : file_paths) {
                byte[] img = null;

                ImageInfoBean imageInfoBean = getEachImgData(data_id, base_path + fp, false);
                if (imageInfoBean != null) {
                    img = imageInfoBean.getImg();
                }
                if (img == null) {
                    gpsSingleRecord = GPSSingleRecord.newBuilder().setClientRecordId(item.getGpsid())
                            .setDataId(item.getDataid())
                            .setFileErr(true)
                            .build();
                } else {
                    gpsSingleRecord = GPSSingleRecord.newBuilder().setClientRecordId(item.getGpsid())
                            .setDataId(item.getDataid())
                            .setFileName(imageInfoBean.getFileName())
                            .setImgData(ByteStringUtils.changeBytes2ByteString(img))
                            .build();
                }
                gsrs.add(gpsSingleRecord);
            }
        }

        return gsrs;
    }

    private GPSRecord createGPSRecordBean(String filePath) {
        List<String> fileNames = null;
        List<byte[]> imgs = null;
        GPSRecord gpsRecord = null;
        if (filePath != null) {
            MultiImageInfoBean multiImageInfoBean = getSigleImgDatas(filePath);
            fileNames = multiImageInfoBean.getFileNames();
            imgs = multiImageInfoBean.getImgs();
            gpsRecord = GPSRecord.newBuilder().setClientRecordId("1")
                    .setDataId("1")
                    .setLostedSize(0)
                    .addAllFileNames(fileNames)
                    .addAllImgData(ByteStringUtils.changeBytesIter2ByteString(imgs))
                    .build();
            ;
        }

        return gpsRecord;
    }

    private GPSRecord createGPSRecordBean(GPSTransferIniBean item) {
        String data_id = getDataid(item);
        String file_path = item.getOriginalUrl();
        String base_path = item.getBaseDir();

        String[] file_paths = null;
        if (StringUtils.isNotBlank(file_path)) {
            file_path = file_path.replaceAll("(\r\n|\r|\n|\n\r)", "");
            //多个路径是通过;分割的
            file_paths = file_path.split(";");
        }
        List<byte[]> imgs = null;
        List<String> fileNames = null;
        if (file_paths != null) {
            MultiImageInfoBean multiImageInfoBean = getEachImgDatas(data_id, base_path, file_paths);
            fileNames = multiImageInfoBean.getFileNames();
            imgs = multiImageInfoBean.getImgs();
        }
        //计算丢失文件的个数
        int losted_size = 0;
        if (file_paths != null) {
            losted_size = file_paths.length - imgs.size();
        }

        GPSRecord gpsRecord = null;
        if (Objects.isNull(imgs) || imgs.size() == 0) {
            gpsRecord = GPSRecord.newBuilder().setClientRecordId(item.getGpsid())
                    .setDataId(item.getDataid())
                    .setLostedSize(losted_size)
                    .build();
        } else {
            gpsRecord = GPSRecord.newBuilder().setClientRecordId(item.getGpsid())
                    .setDataId(item.getDataid())
                    .setLostedSize(losted_size)
                    .addAllFileNames(fileNames)
                    .addAllImgData(ByteStringUtils.changeBytesIter2ByteString(imgs))
                    .build();
        }
        return gpsRecord;
    }

    /**
     * 获取设备的身份描述信息
     *
     * @param item
     * @return
     */
    private String getDataid(GPSTransferIniBean item) {
        if (item != null) {
            return "读取_gpsid=" + item.getGpsid() + "_dataid=" + item.getDataid();
        }
        return "unknow device info";
    }

    /**
     * 获取跟定路径的所有文件的二进制字节数组
     *
     * @return
     */
    private MultiImageInfoBean getSigleImgDatas(String filePath) {
        MultiImageInfoBean multiImageInfoBean = new MultiImageInfoBean();
        List<byte[]> imgs = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        ImageInfoBean imageInfoBean = getEachImgData("", filePath, true);

        Optional.ofNullable(imageInfoBean).ifPresent(iib -> {
            imgs.add(iib.getImg());
            fileNames.add(iib.getFileName());
            total_img_bytes_size += iib.getImg().length;
        });
        multiImageInfoBean.setFileNames(fileNames);
        multiImageInfoBean.setImgs(imgs);
        return multiImageInfoBean;
    }

    /**
     * 获取跟定路径的所有文件的二进制字节数组
     *
     * @return
     */
    private MultiImageInfoBean getEachImgDatas(String data_id, String base_dir, String[] paths) {
        MultiImageInfoBean multiImageInfoBean = new MultiImageInfoBean();
        List<byte[]> imgs = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        Optional.ofNullable(paths).ifPresent(pts -> {
            for (String path : pts) {
                Optional.ofNullable(path).ifPresent(item -> {
                    if (StringUtils.isNotBlank(base_dir)) {
                        item = base_dir + item;
                    } else {
                        LoggerUtils.debug(logger, "相对路径=[" + item + "]的记录，根目录值为空，");
                    }
                    total_img_count++;
                    ImageInfoBean imageInfoBean = getEachImgData(data_id, item.trim(), false);

                    Optional.ofNullable(imageInfoBean).ifPresent(iib -> {
                        imgs.add(iib.getImg());
                        fileNames.add(iib.getFileName());
                        total_img_bytes_size += iib.getImg().length;
                    });
                });
            }
        });
        multiImageInfoBean.setFileNames(fileNames);
        multiImageInfoBean.setImgs(imgs);
        return multiImageInfoBean;
    }

    /**
     * 获取二进制信息
     *
     * @param data_id
     * @param path
     * @return
     */
    private ImageInfoBean getEachImgData(String data_id, String path, boolean haExtension) {
        ImageInfoBean imageInfoBean = null;
        byte[] bytes = null;
        String fileName = null;
        try {
            bytes = FileIOUtils.getImgBytesDataFromPath(path);
            //获取到图片的情况下才解析文件名称
            if (bytes != null) {
                fileName = FileIOUtils.getImageFileName(path, haExtension);
                imageInfoBean = new ImageInfoBean(fileName, bytes);
            }
        } catch (Exception ex) {
            LoggerUtils.warn(logger, "读取[" + path + "] 失败. 原因：" + ExceptionInfoUtils.getExceptionCauseInfo(ex));
        }
        return imageInfoBean;
    }

    /**
     * 异步发送上传数据的操作
     *
     * @param datas
     */
    public void transferData2Server(List<GPSTransferIniBean> datas) {
        if (datas != null && datas.size() > 0) {
            JobCounterStreamObserver responseStreamObserver = createResponseStream();

            StreamObserver<SyncDataRequest> requestObserver = getAsyncStub().transporterByStream(responseStreamObserver);

            for (GPSTransferIniBean item : datas) {
                requestObserver.onNext(createRequestObj(item));
                long remain = responseStreamObserver.incNewJobCounter();
                if (logger.isDebugEnabled()) {
                    logger.debug("Inc Job .... 还剩下[" + remain + "]个图像上传请求没有收到服务端的响应");
                }
            }

            //尝试等待服务器端的响应全部返回,最大等待5S
            int wait_times = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                wait_times++;
                if (!responseStreamObserver.has_un_response_jobs() || wait_times > 60) {
                    System.err.println("服务端全部响应，退出等待服务端响应，继续上传剩余图像数据");
                    break;
                }
                System.err.println("尝试等待服务端响应所有请求.......第[" + wait_times + "]S,还剩下[" + responseStreamObserver.get_remain_un_response_jbos() + "]未响应");
            }
            //表示发送完毕
            requestObserver.onCompleted();
        }
    }

    /**
     * 关闭资源
     */
    private void shutdown() {
        if (managedChannel != null) {
            try {
                managedChannel.shutdown().awaitTermination(5, TimeUnit.MINUTES);
                logger.info("Client managedChannel closed!");
            } catch (InterruptedException e) {
            }
        }
        managedChannel = null;
        stub = null;
    }

    public void release() {
        if (transferRespJobUtils != null) {
            transferRespJobUtils.close();
        }
    }

    /**
     * 创建处理响应的回调对象
     *
     * @return
     */
    private JobCounterStreamObserver createResponseStream() {
        if (responseStreamObserver == null) {
            synchronized (this) {
                if (responseStreamObserver == null) {
                    responseStreamObserver = new JobCounterStreamObserver();
                }
            }
        }
        return responseStreamObserver;
    }

    private class JobCounterStreamObserver implements StreamObserver<SyncDataResponse> {
        //记录未得到响应结果的任务总数
        private AtomicLong un_response_jobs = new AtomicLong();

        @Override
        public void onNext(SyncDataResponse syncDataResponse) {
            transferRespJobUtils.addNewJob(syncDataResponse);
            long remain = decJobCounter();
            if (logger.isDebugEnabled()) {
                logger.debug("Dec Job .... 还剩下[" + remain + "]个图像上传请求没有收到服务端的响应");
            }
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onCompleted() {
            logger.debug("服务端处理本次请求的所有响应完毕...关闭流..");
        }

        /**
         * 是否存在未响应的job
         */
        public boolean has_un_response_jobs() {
            if (un_response_jobs.get() > 0) {
                return true;
            } else {
                return false;
            }
        }

        public long get_remain_un_response_jbos() {
            return un_response_jobs.get();
        }

        public long incNewJobCounter() {
            return un_response_jobs.incrementAndGet();
        }

        public long decJobCounter() {
            return un_response_jobs.decrementAndGet();
        }
    }
}
