package com.znjt.dao.mapper;

import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.datasource.enhance.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by qiuzx on 2019-03-12
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public interface GPSTransferBeanMapper extends Mapper {
    /**
     * 获取没有上传的数据记录
     * @param pageSize
     * @return
     */
    List<GPSTransferIniBean> findUnUpLoadGPSRecordDatas(int pageSize);
    List<GPSTransferIniBean> findUnUpLoadGPSRecordDatasOnCondition(int pageSize);

    List<GPSTransferIniBean> findUnUpLoadGPSImgDatas(int pageSize);
    List<GPSTransferIniBean> findUnUpLoadGPSImgDatas4EvenOrOdd(@Param("pageSize") int pageSize,@Param("mod") int mod);

    void updateCurrentUpLoadedSuccessGPSRescords(List<GPSTransferIniBean> gpsTransferIniBeans);

    void updateCurrentUploadedSuccessGPSImgRecords(List<GPSTransferIniBean> gpsTransferIniBeans);

    void upLoadGPSRecordDatas2UpStream(List<GPSTransferIniBean> gpsTransferIniBeans);

    int updateGPSImgPath2DBRecord(GPSTransferIniBean gpsTransferIniBean);

    int updateBatchGPSImgPath2DBRecord(List<GPSTransferIniBean> gpsTransferIniBeans);

   // void batchInsertMonitorGPSDatas(List<GPSTransferIniBean> gpsTransferIniBeans);
}
