package com.znjt.dao.mapper;

import com.znjt.dao.beans.PCITransferIniBean;
import com.znjt.datasource.enhance.Mapper;

import java.util.List;

/**
 * Created by qiuzx on 2019-04-18
 * Company BTT
 * Depart Tech
 */
public interface PCITransferBeanMapper extends Mapper {
    /**
     * 获取没有上传的数据记录
     * @param pageSize
     * @return
     */
    List<PCITransferIniBean> findUnUpLoadPCIRecordDatas(int pageSize);
    void updateCurrentUpLoadedSuccessPCIRescords(List<PCITransferIniBean> pciTransferIniBeans);
    void upLoadPICRecordDatas2UpStream(List<PCITransferIniBean> iriTransferIniBeans);
}
