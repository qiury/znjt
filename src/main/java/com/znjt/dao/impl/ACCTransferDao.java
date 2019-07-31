package com.znjt.dao.impl;

import com.znjt.dao.beans.ACCTransferIniBean;
import com.znjt.dao.mapper.ACCTransferBeanMapper;
import com.znjt.datasource.enhance.EnhanceDbUtils;
import com.znjt.datasource.enhance.EnhanceMapperFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by qiuzx on 2019-03-15
 * Company BTT
 * Depart Tech
 */
public class ACCTransferDao {
    public List<ACCTransferIniBean> findUnUpLoadACCRecordDatas(String dbname, int pageSize) {
        List<ACCTransferIniBean> recordDatas = null;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, true);
            ACCTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(ACCTransferBeanMapper.class, sqlSession);
            recordDatas = mapper.findUnUpLoadACCRecordDatas(pageSize);
        }finally {
            EnhanceDbUtils.closeSession();
        }
        return recordDatas;
    }

    public int updateCurrentUpLoadedSuccessACCRescords(String dbname,List<ACCTransferIniBean> accTransferIniBeans) {
        int res = -1;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false);
            ACCTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(ACCTransferBeanMapper.class, sqlSession);
            res = mapper.updateCurrentUpLoadedSuccessACCRescords(accTransferIniBeans);
            sqlSession.commit();
        }  finally {
            EnhanceDbUtils.closeSession();
        }
        return res;
    }


    public int upLoadACCRecordDatas2UpStream(String dbname,List<ACCTransferIniBean> accTransferIniBeans) {
        int res = -1;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false, ExecutorType.BATCH);
            ACCTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(ACCTransferBeanMapper.class, sqlSession);
            res = mapper.upLoadACCRecordDatas2UpStream(accTransferIniBeans);
            sqlSession.commit();
        }finally {
            EnhanceDbUtils.closeSession();
        }
        return res;
    }
}
