package com.znjt.thrift;

import com.znjt.boot.Boot;
import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.exs.ExceptionInfoUtils;
import com.znjt.proto.INIRecord;
import com.znjt.service.GPSTransferService;
import com.znjt.utils.FileIOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-03-27
 * Company BTT
 * Depart Tech
 */
public class GPSImgClient {
    private Logger logger = LoggerFactory.getLogger(GPSImgClient.class);
    private String ip;
    private int port;
    private GPSImgService.Client Client;
    private TTransport transport;
    private GPSTransferService gpsTransferService;
    public GPSImgClient(GPSTransferService localTransferService,String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.gpsTransferService = localTransferService;
    }

    //实现复用
    public GPSImgService.Client getGPSImgServiceClient() {
        if (Client != null) {
            return Client;
        }
        //此处的传输协议和服务器端的要相同
        transport = new TFramedTransport(new TSocket(ip, port), Integer.MAX_VALUE);
        //协议层要和服务端一致
        TProtocol protocol = new TCompactProtocol(transport);
        try {
            //和服务器端建立连接
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
            return null;
        }
        /*
          客户端的代理对象
          客户端叫：Client
         */
        Client = new GPSImgService.Client(protocol);
        return Client;
    }

    private void closeTransport() {
        if (transport != null) {
            transport.close();
        }
    }
    public void uploadImgRecords(List<GPSTransferIniBean> datas) {
        GPSImgService.Client client = getGPSImgServiceClient();
        if (client != null) {
            try {
                Instant instant = Instant.now();
                ThriftMessageRequest thriftMessageRequest = new ThriftMessageRequest();
                List<GPSImgRecord> gpsImgRecords = new ArrayList<>();
                datas.forEach(data->{
                    gpsImgRecords.add(changeGPSTransferIniBean2GPSImgRecord(data));
                });
                thriftMessageRequest.setMessages(gpsImgRecords);
                ThriftMessageResponse response = client.transferGPSImgs(thriftMessageRequest);
                List<GPSImgRecord> messages = response.getMessages();
                doneThriftResponseDatas(messages);
                System.err.println("client 总计耗时 " + Duration.between(instant,Instant.now()).toMillis()+" ms");
            } catch (Exception ex) {
                Client = null;
                closeTransport();
                ex.printStackTrace();
            }
        }
    }

    /**
     * 处理同步请求的响应结果（更新到数据库）
     *
     * @param records
     */
    private void doneThriftResponseDatas(List<GPSImgRecord> records) {
        if (records == null) {
            logger.warn("处理同步请求结果的doneSyncResponseDatas()接收到Null的参数，无法处理..直接忽略");
            return;
        }
        final List<GPSTransferIniBean> dbs = new ArrayList<>();
        Optional.ofNullable(records).ifPresent(rds -> {
            rds.forEach(item -> {
                GPSTransferIniBean gtb = createGPSGpsTransferIniBeanFromThriftResponseRecord(item);
                Optional.ofNullable(gtb).ifPresent(x -> {
                    dbs.add(x);
                });
            });
        });
        if (dbs.size() > 0) {
            if(logger.isDebugEnabled()) {
                logger.debug("批量更新图像数据上传结果，影响记录数[ " + dbs.size() + " ]条");
            }
            gpsTransferService.updateCurrentUploadedSuccessGPSImgRecords(Boot.DOWNSTREAM_DBNAME, dbs);
        }
    }
    /**
     * 根据gpsrecord创建GPSTransferIniBean对象
     * @param gpsRecord
     * @return
     */
    private GPSTransferIniBean createGPSGpsTransferIniBeanFromThriftResponseRecord(GPSImgRecord gpsRecord){
        String cid = gpsRecord.getClient_record_id();
        String did = gpsRecord.getDataId();
        boolean file_err = gpsRecord.isFile_err();
        boolean opsres = gpsRecord.isServ_ops_res();
        GPSTransferIniBean gpsTransferIniBean = null;
        //后台成功进行了处理的记录才会被更新
        if(opsres) {
            gpsTransferIniBean = new GPSTransferIniBean();
            gpsTransferIniBean.setGpsid(cid);
            gpsTransferIniBean.setFile_err(file_err);
            gpsTransferIniBean.setImg_uploaded(opsres);
        }
        return  gpsTransferIniBean;
    }
    /**
     * 获取设备的身份描述信息
     *
     * @param item
     * @return
     */
    private String getDataid(GPSTransferIniBean item) {
        if (item != null) {
            return "读取_gpsid=" + item.getGpsid() + "_dataid=" + item.getDataid() + "_originalUrl=" + item.getOriginalUrl();
        }
        return "unknow device info";
    }

    private GPSImgRecord changeGPSTransferIniBean2GPSImgRecord(GPSTransferIniBean item) {
        String data_id = getDataid(item);
        String file_path = item.getOriginalUrl();
        String base_path = item.getBaseDir();
        GPSImgRecord record = null;
        String[] file_paths = null;
        if (StringUtils.isNotBlank(file_path)) {
            file_path = file_path.replaceAll("(\r\n|\r|\n|\n\r)", "");
            //多个路径是通过;分割的
            file_paths = file_path.split(";");
        }
        List<byte[]> imgs = null;
        if (file_paths != null) {
            imgs = getEachImgDatas(data_id, base_path, file_paths);
        }
        //计算丢失文件的个数
        int losted_size = 0;
        if (file_paths != null) {
            losted_size = file_paths.length - imgs.size();
        }
        if (Objects.isNull(imgs) || imgs.size() == 0) {
            record = new GPSImgRecord();
            record.setDataId(item.getDataid());
            record.setClient_record_id(item.getGpsid());
            record.setLosted_size(losted_size);
        } else {
            record = new GPSImgRecord();
            record.setDataId(item.getDataid());
            record.setClient_record_id(item.getGpsid());
            record.setLosted_size(losted_size);
            for (byte[] img : imgs) {
                record.addToImg_datas(ByteBuffer.wrap(img));
            }
        }
        return record;
    }

    /**
     * 获取跟定路径的所有文件的二进制字节数组
     *
     * @return
     */
    private List<byte[]> getEachImgDatas(String data_id, String base_dir, String[] paths) {
        List<byte[]> imgs = new ArrayList<>();
        Optional.ofNullable(paths).ifPresent(pts -> {
            for (String path : pts) {
                Optional.ofNullable(path).ifPresent(item -> {
                    if (StringUtils.isNotBlank(base_dir)) {
                        item = base_dir + item;
                    }
                    byte[] img = getEachImgData(data_id, item.trim());
                    Optional.ofNullable(img).ifPresent(image -> {
                        imgs.add(image);
                    });
                });
            }
        });
        return imgs;
    }

    /**
     * 获取二进制信息
     *
     * @param data_id
     * @param path
     * @return
     */
    private byte[] getEachImgData(String data_id, String path) {
        byte[] bytes = null;
        try {
            bytes = FileIOUtils.getImgBytesDataFromPath(path);
        } catch (Exception ex) {
            if (!(ex.getCause() instanceof FileNotFoundException)) {
                if (logger.isWarnEnabled()) {
                    logger.warn(data_id + " 失败. 原因：" + ExceptionInfoUtils.getExceptionCauseInfo(ex));
                }
            }
        }
        return bytes;
    }

    private void testNet() {
        GPSImgService.Client client = getGPSImgServiceClient();
        if (client != null) {
            try {

            } catch (Exception ex) {
                Client = null;
                ex.printStackTrace();
                closeTransport();
            }
        }

    }
}
