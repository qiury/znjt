package com.znjt.service;

import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.dao.impl.GPSTransferDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-03-15
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public class GPSTransferService {
    private Logger logger = LoggerFactory.getLogger(GPSTransferService.class);
    private GPSTransferDao dao = new GPSTransferDao();
    private static String NO_IMAGE = "no image";

    /**
     * 获取没有上传的数据记录
     * @param pageSize
     * @return
     */
    public List<GPSTransferIniBean> findUnUpLoadGPSRecordDatas(String dbname,int pageSize){
        List<GPSTransferIniBean> gpsTransferIniBeans = dao.findUnUpLoadGPSRecordDatas(dbname,pageSize);
        //如果GPSTransferIniBean中的路径不是'no image'就设置未null
        Optional.ofNullable(gpsTransferIniBeans).ifPresent(gps->{
            gps.forEach(item->{
                if(!NO_IMAGE.equals(item.getOriginalUrl())){
                    item.setOriginalUrl(null);
                }
            });
        });
        return gpsTransferIniBeans;
    }

    /**
     * 获取没有上传的数据记录
     * @param pageSize
     * @return
     */
    public List<GPSTransferIniBean> findUnUpLoadGPSRecordDatasOnCondition(String dbname,int pageSize){
        List<GPSTransferIniBean> gpsTransferIniBeans = dao.findUnUpLoadGPSRecordDatasOnCondition(dbname,pageSize);
        //如果GPSTransferIniBean中的路径不是'no image'就设置未null
        Optional.ofNullable(gpsTransferIniBeans).ifPresent(gps->{
            gps.forEach(item->{
                if(!NO_IMAGE.equals(item.getOriginalUrl())){
                    item.setOriginalUrl(null);
                }
            });
        });
        return gpsTransferIniBeans;
    }

    /**
     * 获取没有上传图像但是已经上传了记录的数据
     * @param dbname
     * @param pageSize
     * @return
     */
    public List<GPSTransferIniBean> findUnUpLoadGPSImgDatas(String dbname,int pageSize) {
        return dao.findUnUpLoadGPSImgDatas(dbname,pageSize);
    }
/**
     * 获取没有上传图像但是已经上传了记录的数据
     * @param dbname
     * @param pageSize
     * @return
     */
    public List<GPSTransferIniBean> findUnUpLoadGPSImgDatas4EvenOrOdd(String dbname,int pageSize,int mod) {
        return dao.findUnUpLoadGPSImgDatas4EvenOrOdd(dbname,pageSize,mod);
    }

    /**
     * 更新GPS记录上传成功
     * @param dbname
     * @param gpsTransferIniBeans
     */
    public void updateCurrentUpLoadedSuccessGPSRescords(String dbname,List<GPSTransferIniBean> gpsTransferIniBeans){
        dao.updateCurrentUpLoadedSuccessGPSRescords(dbname,gpsTransferIniBeans);
    }

    /**
     * 更新记录中的图像上传成功
     * @param dbname
     * @param gpsTransferIniBeans
     */
    public void updateCurrentUploadedSuccessGPSImgRecords(String dbname,List<GPSTransferIniBean> gpsTransferIniBeans){
        Instant begin = Instant.now();
        dao.updateCurrentUploadedSuccessGPSImgRecords(dbname,gpsTransferIniBeans);
        if(logger.isDebugEnabled()){
            logger.debug("Client 批量更新 ["+gpsTransferIniBeans.size()+"]条图像上传标记到数据耗时：" + Duration.between(begin,Instant.now()).toMillis() + " ms");
        }
    }

    /**
     * 上传记录到上游数据库
     * @param dbname
     * @param gpsTransferIniBeans
     */
    public void upLoadGPSRecordDatas2UpStream(String dbname,List<GPSTransferIniBean> gpsTransferIniBeans){
        //将gpsid和status字段组合成dataid
        Optional.ofNullable(gpsTransferIniBeans).ifPresent(gbs->{
            gbs.forEach(item->{
                if(Objects.isNull(item.getDataid())||!item.getDataid().contains("&")) {
                    item.setDataid(item.getStatus() + "&" + item.getGpsid());
                }
            });
        });
        dao.upLoadGPSRecordDatas2UpStream(dbname,gpsTransferIniBeans);
    }



    public int updateGPSImgPath2DBRecord(String dbName,GPSTransferIniBean gpsTransferIniBean){
        return dao.updateGPSImgPath2DBRecord(dbName,gpsTransferIniBean);
    }

    public void updateBatchGPSImgPath2DBRecord(String dbName,List<GPSTransferIniBean> gpsTransferIniBeans){
        Instant begin = Instant.now();
        dao.updateBatchGPSImgPath2DBRecord(dbName,gpsTransferIniBeans);
        if(logger.isDebugEnabled()){
            logger.debug("Server 批量更新 ["+gpsTransferIniBeans.size()+"]条图像上传路径记录到数据耗时：" + Duration.between(begin,Instant.now()).toMillis() + " ms");
        }
    }
}
