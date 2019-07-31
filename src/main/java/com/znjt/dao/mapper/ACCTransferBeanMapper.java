package com.znjt.dao.mapper;

import com.znjt.dao.beans.ACCTransferIniBean;
import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.datasource.enhance.Mapper;

import java.util.List;

/**
 * Created by qiuzx on 2019-03-18
 * Company BTT
 * Depart Tech
 */
public interface ACCTransferBeanMapper extends Mapper {
    /**
     * 获取没有上传的数据记录
     * @param pageSize
     * @return
     */
    List<ACCTransferIniBean> findUnUpLoadACCRecordDatas(int pageSize);
    int updateCurrentUpLoadedSuccessACCRescords(List<ACCTransferIniBean> accTransferIniBeans);
    int upLoadACCRecordDatas2UpStream(List<ACCTransferIniBean> accTransferIniBeans);
}
