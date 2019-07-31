package com.znjt.thrift;

import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.exs.ExceptionInfoUtils;
import com.znjt.rpc.ImageUpLoadProcssor;
import com.znjt.rpc.SingleImgaeProcessResult;
import com.znjt.rpc.TransferProtoImpl4Server;
import com.znjt.service.GPSTransferService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Created by qiuzx on 2019-03-27
 * Company BTT
 * Depart Tech
 */
public class ImageProcessor4Thrift {
    private static Logger logger = LoggerFactory.getLogger(ImageProcessor4Thrift.class);
    static long received_total_size = 0;
    static long received_img_size = 0;
    private ImageProcessor4Thrift(){

    }

    public static List<GPSImgRecord> processGPSImageRecords(GPSTransferService gpsTransferService,List<GPSImgRecord> records){
        received_total_size = 0;
        List<GPSTransferIniBean> transferIniBeans = new ArrayList<>();
        List<GPSImgRecord> gpsImgRecords = new ArrayList<>();
        Optional.ofNullable(records).ifPresent(item->{
            item.forEach(record->{
                transferIniBeans.add(saveRecordRefImgs2DS(record));
            });
        });
        System.err.println("server img count is "+received_img_size+" received Byte size = " + received_total_size/(1024*1024) +"MB");
        received_img_size = 0;
        received_total_size = 0;
        if(transferIniBeans.size()>0){
            updateRecordImgInfo2DB(gpsTransferService,transferIniBeans,gpsImgRecords);
        }
        return gpsImgRecords;
    }

    private static void updateRecordImgInfo2DB(GPSTransferService gpsTransferService,List<GPSTransferIniBean> transferIniBeans,List<GPSImgRecord> gpsImgRecords){
        try {
            ImageUpLoadProcssor.doneImage2DB(gpsTransferService, transferIniBeans);
            //数据库更新成功，添加新的响应结果
            transferIniBeans.forEach(item -> {
                GPSImgRecord gpsImgRecord = new GPSImgRecord();
                gpsImgRecord.setFile_err(item.getTotalLostedSize()>0?true:false);
                gpsImgRecord.setServ_ops_res(true);
                gpsImgRecord.setClient_record_id(item.getClientRecordId());
                gpsImgRecord.setDataId(item.getDataid());
                gpsImgRecords.add(gpsImgRecord);
            });
        } catch (Exception ex) {
            logger.warn(ExceptionInfoUtils.getExceptionCauseInfo(ex));
            //数据库更新成功，添加新的响应结果
            transferIniBeans.forEach(item -> {
                GPSImgRecord gpsImgRecord = new GPSImgRecord();
                gpsImgRecord.setFile_err(false);
                gpsImgRecord.setServ_ops_res(false);
                gpsImgRecord.setDataId(item.getDataid());
                gpsImgRecord.setClient_record_id(item.getClientRecordId());
                gpsImgRecords.add(gpsImgRecord);
                ImageUpLoadProcssor.iterDelFiles(item.getOriginalUrl());
            });
        }
    }


    private static GPSTransferIniBean saveRecordRefImgs2DS(GPSImgRecord record){
        String dataId = record.getDataId();
        List<ByteBuffer> img_datas = record.getImg_datas();
        //客户端自身问题造成的数据丢失+本地原因丢失
        int losted_size = record.getLosted_size();
        String join_path = "";
        SingleImgaeProcessResult sipr = null;
        if(img_datas!=null){
            byte[] bytes;
            for(ByteBuffer bf:img_datas){
                bytes = new byte[bf.remaining()];
                bf.get(bytes, 0, bytes.length);

                received_total_size+=bytes.length;
                received_img_size++;

                System.err.println("single img size = " + bytes.length);
                sipr = ImageUpLoadProcssor.doneSingleImage2Disk(dataId,bytes,true);
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
        gpsTransferIniBean.setDataid(record.getDataId());
        gpsTransferIniBean.setOriginalUrl(join_path);
        gpsTransferIniBean.setBaseDir(TransferProtoImpl4Server.BASE_DIR);
        gpsTransferIniBean.setClientRecordId(record.getClient_record_id());
        //服务端和客户端共计丢失的总数
        gpsTransferIniBean.setTotalLostedSize(losted_size);
        gpsTransferIniBean.setFile_err(losted_size>0?true:false);
        return gpsTransferIniBean;
    }


}
