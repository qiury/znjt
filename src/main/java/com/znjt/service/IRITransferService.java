package com.znjt.service;

import com.znjt.dao.beans.IRITransferIniBean;
import com.znjt.dao.impl.IRITransferDao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-04-18
 * Company BTT
 * Depart Tech
 *
 * @author qiuzx
 */
public class IRITransferService {
    private IRITransferDao dao = new IRITransferDao();

    /**
     * 获取没有上传的数据记录
     *
     * @param pageSize
     * @return
     */
    public List<IRITransferIniBean> findUnUpLoadIRIRecordDatas(String dbname, int pageSize) {
        return dao.findUnUpLoadIRIRecordDatas(dbname, pageSize);
    }


    /**
     * 更新GPS记录上传成功
     *
     * @param dbname
     * @param iriTransferIniBeansTransferIniBeans
     */
    public void updateCurrentUpLoadedSuccessIRIRescords(String dbname, List<IRITransferIniBean> iriTransferIniBeansTransferIniBeans) {
        dao.updateCurrentUpLoadedSuccessIRIRescords(dbname, iriTransferIniBeansTransferIniBeans);
    }


    /**
     * 上传记录到上游数据库
     *
     * @param dbname
     * @param iriTransferIniBeans
     */
    public void upLoadACCRecordDatas2UpStream(String dbname, List<IRITransferIniBean> iriTransferIniBeans) {
        //对daaId进行填充
        Optional.ofNullable(iriTransferIniBeans).ifPresent(rds -> {
            rds.forEach(item -> {
                if(Objects.isNull(item.getDataid())||!item.getDataid().contains("&")) {
                    item.setDataid(item.getIrigpsid() + "&" + item.getGpsid() + "&" + item.getId());
                }
            });
            dao.upLoadIRIRecordDatas2UpStream(dbname, rds);
        });
    }

}
