package com.znjt.service;

import com.znjt.dao.beans.PCITransferIniBean;
import com.znjt.dao.impl.PCITransferDao;
import com.znjt.dao.mapper.PCITransferBeanMapper;
import com.znjt.datasource.enhance.EnhanceDbUtils;
import com.znjt.datasource.enhance.EnhanceMapperFactory;
import org.apache.ibatis.session.SqlSession;

import java.lang.annotation.Retention;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-03-15
 * Company BTT
 * Depart Tech
 *
 * @author qiuzx
 */
public class PCITransferService {
    private PCITransferDao dao = new PCITransferDao();

    /**
     * 获取没有上传的数据记录
     *
     * @param pageSize
     * @return
     */
    public List<PCITransferIniBean> findUnUpLoadPCIRecordDatas(String dbname, int pageSize) {
        return dao.findUnUpLoadPCIRecordDatas(dbname, pageSize);
    }


    /**
     * 更新GPS记录上传成功
     *
     * @param dbname
     * @param pciTransferIniBeans
     */
    public void updateCurrentUpLoadedSuccessPCIRescords(String dbname, List<PCITransferIniBean> pciTransferIniBeans) {
        dao.updateCurrentUpLoadedSuccessPCIRescords(dbname, pciTransferIniBeans);
    }


    /**
     * 上传记录到上游数据库
     *
     * @param dbname
     * @param pciTransferIniBeans
     */
    public void upLoadPCIRecordDatas2UpStream(String dbname, List<PCITransferIniBean> pciTransferIniBeans) {
        //对daaId进行填充
        Optional.ofNullable(pciTransferIniBeans).ifPresent(rds -> {
            rds.forEach(item -> {
                if(Objects.isNull(item.getDataId())||!item.getDataId().contains("&")) {
                    item.setDataId(item.getPcigpsid() + "&" + item.getGpsid() + "&" + item.getId());
                }
            });
            dao.upLoadPCIRecordDatas2UpStream(dbname, rds);
        });
    }
    //根据id查询共计有多少条记录
    public long findTotalJobs(String dbname,long id){
        return dao.findTotalJobs(dbname, id);
    }

    public long findUnUpLoadPCIRecords(String dbname){
        return dao.findUnUpLoadPCIRecords(dbname);
    }

}
