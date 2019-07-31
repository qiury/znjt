package com.znjt.service;

import com.znjt.dao.beans.ACCTransferIniBean;
import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.dao.impl.ACCTransferDao;
import com.znjt.dao.impl.GPSTransferDao;

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
public class ACCTransferService {
    private ACCTransferDao dao = new ACCTransferDao();

    /**
     * 获取没有上传的数据记录
     *
     * @param pageSize
     * @return
     */
    public List<ACCTransferIniBean> findUnUpLoadACCRecordDatas(String dbname, int pageSize) {
        List<ACCTransferIniBean> accTransferIniBeans = dao.findUnUpLoadACCRecordDatas(dbname, pageSize);
        return accTransferIniBeans;
    }


    /**
     * 更新GPS记录上传成功
     *
     * @param dbname
     * @param accTransferIniBeans
     */
    public void updateCurrentUpLoadedSuccessACCRescords(String dbname, List<ACCTransferIniBean> accTransferIniBeans) {
        dao.updateCurrentUpLoadedSuccessACCRescords(dbname, accTransferIniBeans);
    }


    /**
     * 上传记录到上游数据库
     *
     * @param dbname
     * @param accTransferIniBeans
     */
    public int upLoadACCRecordDatas2UpStream(String dbname, List<ACCTransferIniBean> accTransferIniBeans) {
        //对daaId进行填充
        Optional.ofNullable(accTransferIniBeans).ifPresent(rds -> {
            rds.forEach(item -> {
                if(Objects.isNull(item.getAccid())||!item.getAccid().contains("&")) {
                    item.setAccid(item.getStatus() + "&" + item.getAccid() + "&" + item.getId());
                }
            });

        });
        return dao.upLoadACCRecordDatas2UpStream(dbname, accTransferIniBeans);
    }

}
