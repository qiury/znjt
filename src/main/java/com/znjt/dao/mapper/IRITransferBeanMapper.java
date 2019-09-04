package com.znjt.dao.mapper;

import com.znjt.dao.beans.IRITransferIniBean;
import com.znjt.datasource.enhance.Mapper;

import java.util.List;

/**
 * Created by qiuzx on 2019-04-18
 * Company BTT
 * Depart Tech
 */
public interface IRITransferBeanMapper extends Mapper {
    /**
     * 获取没有上传的数据记录
     * @param pageSize
     * @return
     */
    List<IRITransferIniBean> findUnUpLoadIRIRecordDatas(int pageSize);
    void updateCurrentUpLoadedSuccessIRIRescords(List<IRITransferIniBean> iriTransferIniBeans);
    void upLoadIRIRecordDatas2UpStream(List<IRITransferIniBean> iriTransferIniBeans);

    //根据id查询共计有多少条记录
    long findTotalJobs(long id);
    long findUnUpLoadIRIRecords();
}
