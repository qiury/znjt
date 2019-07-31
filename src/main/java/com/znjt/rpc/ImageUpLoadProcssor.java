package com.znjt.rpc;

import com.google.protobuf.ByteString;
import com.znjt.boot.Boot;
import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.exs.ExceptionInfoUtils;
import com.znjt.proto.GPSRecord;
import com.znjt.proto.GPSSingleRecord;
import com.znjt.service.GPSTransferService;
import com.znjt.utils.FileIOUtils;
import com.znjt.utils.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.znjt.rpc.TransferProtoImpl4Server.BASE_DIR;

/**
 * Created by qiuzx on 2019-03-20
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public class ImageUpLoadProcssor {
    private static Logger logger = LoggerFactory.getLogger(ImageUpLoadProcssor.class);
    private static long total_img_bytes_size = 0;
    private static long total_img_count = 0;

    private ImageUpLoadProcssor() {
    }


    /**
     * 处理文件上传和记录更新
     * @param gpsRecords
     * @param gpsTransferService
     * @return
     */
    public static List<GPSSingleRecord> processMultiSingleGPSRecord(List<GPSSingleRecord> gpsRecords, GPSTransferService gpsTransferService) {
        //存储对每一个GPSSingleRecord保存到磁盘的结果
        List<GPSTransferIniBean> gpsTransferIniBeans = new ArrayList<>();
        List<GPSSingleRecord> res_gps_records = new ArrayList<>();
        Optional.ofNullable(gpsRecords).ifPresent(gr->{
            gr.forEach(item->{
                Optional.ofNullable(item).ifPresent(grs->{
                    gpsTransferIniBeans.add(processGPSSingleRecord(grs,true));
                });
            });
        });

        //对生成的对象进行合并处理，合并后的对象重新放到参数集合中
        mergeGpsTransferIniBeans(gpsTransferIniBeans);

        try {
            doneImage2DB(gpsTransferService, gpsTransferIniBeans);
            //数据库更新成功，添加新的响应结果
            gpsTransferIniBeans.forEach(item -> {
                GPSSingleRecord record = GPSSingleRecord.newBuilder().setClientRecordId(item.getClientRecordId())
                        .setServOpsRes(true)
                        .setFileErr(item.getTotalLostedSize()>0?true:false)
                        .build();
                res_gps_records.add(record);
            });
        } catch (Exception ex) {
            logger.warn(ExceptionInfoUtils.getExceptionCauseInfo(ex));
            //数据库更新成功，添加新的响应结果
            gpsTransferIniBeans.forEach(item -> {
                GPSSingleRecord record = GPSSingleRecord.newBuilder().setClientRecordId(item.getClientRecordId())
                        .setServOpsRes(false).setFileErr(false).build();
                res_gps_records.add(record);
                iterDelFiles(item.getOriginalUrl());
            });
        }
        return res_gps_records;
    }

    /**
     * 根据dataid将数据进行合并
     * @param beans
     * @return
     */
    private static void mergeGpsTransferIniBeans(List<GPSTransferIniBean> beans){
        Map<String,GPSTransferIniBean> cache = new HashMap<>();
        Optional.ofNullable(beans).ifPresent(bs->{
            bs.forEach(bean->{
                GPSTransferIniBean tib = cache.get(bean.getDataid());
                if(tib==null){
                    cache.put(bean.getDataid(),bean);
                }else {
                    tib.setTotalLostedSize(tib.getTotalLostedSize()+bean.getTotalLostedSize());

                    String rel_path = bean.getOriginalUrl();

                    if(StringUtils.isNotBlank(rel_path)){
                        if(tib.getOriginalUrl()!=null){
                            tib.setOriginalUrl(tib.getOriginalUrl()+";"+rel_path);
                        }
                    }
                }
            });
        });
        beans.clear();//清楚以前的对象
        //将整理后的对象重新放回集合
        cache.entrySet().forEach(item->{
            beans.add(item.getValue());
        });
    }

        /**
         * 处理文件上传和记录更新
         * @param gpsRecords
         * @param gpsTransferService
         * @return
         */
    public static List<GPSRecord> processGPSRecord(List<GPSRecord> gpsRecords, GPSTransferService gpsTransferService) {
        //存储对每一个GPSRecord保存到磁盘的结果
        List<GPSTransferIniBean> gpsTransferIniBeans = new ArrayList<>();
        List<GPSRecord> res_gps_records = new ArrayList<>();
        Instant instant = Instant.now();
        total_img_bytes_size = 0;
        total_img_count = 0;
        //处理每一个GPSRecord对象
        Optional.ofNullable(gpsRecords).ifPresent(gr->{
            gr.forEach(item->{
                Optional.ofNullable(item).ifPresent(grs->{
                    gpsTransferIniBeans.add(processGPSRecord(grs,true));
                });
            });
        });
        LoggerUtils.info(logger,"2. 总计处理["+total_img_count+"]张图像，共计大小["+total_img_bytes_size/(1024*1024)+"]MB,磁盘IO操作共计耗时["+ Duration.between(instant,Instant.now()).toMillis()+"]ms");
        try {
            doneImage2DB(gpsTransferService, gpsTransferIniBeans);
            //数据库更新成功，添加新的响应结果
            gpsTransferIniBeans.forEach(item -> {
                GPSRecord record = GPSRecord.newBuilder().setClientRecordId(item.getClientRecordId())
                        .setServOpsRes(true)
                        .setFileErr(item.getTotalLostedSize()>0?true:false)
                        .build();
                res_gps_records.add(record);
            });
        } catch (Exception ex) {
            logger.warn(ExceptionInfoUtils.getExceptionCauseInfo(ex));
            //数据库更新成功，添加新的响应结果
            gpsTransferIniBeans.forEach(item -> {
                GPSRecord record = GPSRecord.newBuilder().setClientRecordId(item.getClientRecordId())
                        .setServOpsRes(false).setFileErr(false).build();
                res_gps_records.add(record);
                iterDelFiles(item.getOriginalUrl());
            });
        }

        //将结果返回给客户端
        return res_gps_records;
    }

    /**
     * 递归删除文件，文件路径通过;拼接
     * @param path
     */
    public static void iterDelFiles(String path){
        if(StringUtils.isNotBlank(path)){
            String[] subPaths = path.split(";");
            for (String sp : subPaths) {
                //删除磁盘上的图像数据
                FileIOUtils.deleteFile(BASE_DIR+sp);
            }
        }
    }
    /**
     * 处理测试上传单条GPSRecord
     * @param gpsRecords
     * @return
     */
    public static void processGPSRecordTest(GPSRecord gpsRecords) {
        String dataId = gpsRecords.getDataId();
        List<ByteString> imgDataList = gpsRecords.getImgDataList();
        List<String> imgFileNames = gpsRecords.getFileNamesList();
        if(imgDataList!=null){
            byte[] bytes;
            String fileName;
            for(int i = 0;i<imgDataList.size();i++){
                bytes = imgDataList.get(i).toByteArray();
                fileName = imgFileNames.get(i);
                doneSingleTestImage2Disk(fileName,bytes);
            }
        }
    }

    /**
     * 处理单条GPSRecord
     * @param gpsRecords
     * @return
     */
    public static GPSTransferIniBean processGPSRecord(GPSRecord gpsRecords,boolean use_org_file_name) {
        String dataId = gpsRecords.getDataId();
        List<ByteString> imgDataList = gpsRecords.getImgDataList();
        List<String> imgFileNames = gpsRecords.getFileNamesList();
        SingleImgaeProcessResult sipr = null;
        String join_path = "";
        //客户端自身问题造成的数据丢失+本地原因丢失
        int losted_size = gpsRecords.getLostedSize();
        if(imgDataList!=null){
            byte[] bytes;
            String fileName;
            for(int i = 0;i<imgDataList.size();i++){
                bytes = imgDataList.get(i).toByteArray();
                fileName = imgFileNames.get(i);
                if(!use_org_file_name){
                    fileName = dataId;
                }
                total_img_count++;
                total_img_bytes_size+=bytes.length;
                sipr = doneSingleImage2Disk(fileName,bytes,use_org_file_name);
                if(sipr.isPersistent()){
                    if(StringUtils.isBlank(join_path)){
                        join_path = sipr.getRelPath();
                    }else{
                        //将相对路径组合在一起
                        join_path = String.join(";",join_path,sipr.getRelPath());
                    }

                }else{
                    losted_size++;
                }
            }
        }
        GPSTransferIniBean gpsTransferIniBean = new GPSTransferIniBean();
        gpsTransferIniBean.setDataid(gpsRecords.getDataId());
        gpsTransferIniBean.setOriginalUrl(join_path);
        gpsTransferIniBean.setBaseDir(BASE_DIR);
        gpsTransferIniBean.setClientRecordId(gpsRecords.getClientRecordId());
        //服务端和客户端共计丢失的总数
        gpsTransferIniBean.setTotalLostedSize(losted_size);
        gpsTransferIniBean.setFile_err(losted_size>0?true:false);
        return gpsTransferIniBean;
    }

    /**
     * 处理单条GPSRecord
     * @param gpsSingleRecord
     * @return
     */
    public static GPSTransferIniBean processGPSSingleRecord(GPSSingleRecord gpsSingleRecord,boolean use_org_file_name) {
        String dataId = gpsSingleRecord.getDataId();
//        List<ByteString> imgDataList = Arrays.asList(gpsSingleRecord.getImgData());
//        List<String> imgFileNames = Arrays.asList(gpsSingleRecord.getFileName());
        ByteString img = gpsSingleRecord.getImgData();
        String imgFileName = gpsSingleRecord.getFileName();
        if(!use_org_file_name){
            imgFileName = dataId;
        }
        SingleImgaeProcessResult sipr = null;
        String join_path = null;
        boolean losted = true;
        //客户端自身问题造成的数据丢失+本地原因丢失
//        if(imgDataList!=null){
//            for (int i = 0; i < imgDataList.size(); i++) {
//                sipr = doneSingleImage2Disk(imgFileNames.get(i),imgDataList.get(i).toByteArray());
//                if(sipr.isPersistent()){
//                    losted = false;
//                    join_path = sipr.getRelPath();
//                }
//            }
//
//        }
        if(img!=null){
            sipr = doneSingleImage2Disk(imgFileName,img.toByteArray(),use_org_file_name);
            if(sipr.isPersistent()){
                losted = false;
                join_path = sipr.getRelPath();
            }
        }
        GPSTransferIniBean gpsTransferIniBean = new GPSTransferIniBean();
        gpsTransferIniBean.setDataid(gpsSingleRecord.getDataId());
        gpsTransferIniBean.setOriginalUrl(join_path);
        gpsTransferIniBean.setBaseDir(BASE_DIR);
        gpsTransferIniBean.setClientRecordId(gpsSingleRecord.getClientRecordId());
        //服务端和客户端共计丢失的总数
        gpsTransferIniBean.setTotalLostedSize(!losted?0:1);
        gpsTransferIniBean.setFile_err(!losted);
        return gpsTransferIniBean;
    }

    public static void doneSingleTestImage2Disk(String fileName,byte[] imgs){
        String path =BASE_DIR+File.separator+"received"+ File.separator + fileName;
        try {
            //保存失败会抛出异常
            FileIOUtils.saveBinaryImg2Disk(path, imgs);
            LoggerUtils.debug(logger,"将[" + imgs.length + "]字节图像数据写入磁盘["+path+"]");
        } catch (Exception ex) {
            LoggerUtils.warn(logger,ExceptionInfoUtils.getExceptionCauseInfo(ex));
            //如果操作失败就删除文件夹中的图像
            FileIOUtils.deleteFile(path);
        }
    }

    /**
     * 写入单个文件到磁盘
     * @param imgs
     * @return
     */
    public static SingleImgaeProcessResult doneSingleImage2Disk(String fileName, byte[] imgs,boolean use_org_file_name){
        String sub_path = FileIOUtils.createRelativePath4Image(fileName,use_org_file_name);
        String path = BASE_DIR + sub_path;
        boolean persistent = false;
        try {
            //保存失败会抛出异常
            FileIOUtils.saveBinaryImg2Disk(path, imgs);

            LoggerUtils.debug(logger,"将[" + imgs.length + "]字节图像数据写入磁盘["+path+"]");

            persistent = true;
        } catch (Exception ex) {
            LoggerUtils.warn(logger,ExceptionInfoUtils.getExceptionCauseInfo(ex));
            //如果操作失败就删除文件夹中的图像
            FileIOUtils.deleteFile(path);
        }
        return new SingleImgaeProcessResult(false,persistent,path,sub_path);
    }

    public static void doneImage2DB(GPSTransferService gpsTransferService, List<GPSTransferIniBean> gpsTransferIniBeans) {
        StringBuilder sb = new StringBuilder();

        gpsTransferIniBeans.forEach(item->{
            sb.append("开始更新本次图像上传记录到数据库：OriginalUrl=").append(item.getOriginalUrl()).append("\t");
        });

        LoggerUtils.info(logger,sb.toString());
        //批量更新数据
        gpsTransferService.updateBatchGPSImgPath2DBRecord(Boot.UPSTREAM_DBNAME, gpsTransferIniBeans);
    }
}
