package com.znjt.dao.impl;

import com.znjt.dao.beans.IRITransferIniBean;
import com.znjt.dao.mapper.IRITransferBeanMapper;
import com.znjt.datasource.enhance.EnhanceDbUtils;
import com.znjt.datasource.enhance.EnhanceMapperFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by qiuzx on 2019-04-18
 * Company BTT
 * Depart Tech
 */
public class IRITransferDao {
    public List<IRITransferIniBean> findUnUpLoadIRIRecordDatas(String dbname, int pageSize) {
        List<IRITransferIniBean> recordDatas = null;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, true);
            IRITransferBeanMapper mapper = EnhanceMapperFactory.createMapper(IRITransferBeanMapper.class, sqlSession);
            recordDatas = mapper.findUnUpLoadIRIRecordDatas(pageSize);
        } finally {
            EnhanceDbUtils.closeSession();
        }
        return recordDatas;
    }

    public void updateCurrentUpLoadedSuccessIRIRescords(String dbname,List<IRITransferIniBean> iriTransferIniBeans) {
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false);
            IRITransferBeanMapper mapper = EnhanceMapperFactory.createMapper(IRITransferBeanMapper.class, sqlSession);
            mapper.updateCurrentUpLoadedSuccessIRIRescords(iriTransferIniBeans);
            sqlSession.commit();
        } finally {
            EnhanceDbUtils.closeSession();
        }
    }


    public void upLoadIRIRecordDatas2UpStream(String dbname,List<IRITransferIniBean> accTransferIniBeans) {
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false, ExecutorType.BATCH);
            IRITransferBeanMapper mapper = EnhanceMapperFactory.createMapper(IRITransferBeanMapper.class, sqlSession);
            mapper.upLoadIRIRecordDatas2UpStream(accTransferIniBeans);
            sqlSession.commit();
        }  finally {
            EnhanceDbUtils.closeSession();
        }
    }

    //根据id查询共计有多少条记录
    public long findTotalJobs(String dbname,long id){
        long res = -1;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false);
            IRITransferBeanMapper mapper = EnhanceMapperFactory.createMapper(IRITransferBeanMapper.class, sqlSession);
            res = mapper.findTotalJobs(id);
        }  finally {
            EnhanceDbUtils.closeSession();
        }
        return res;
    }

    public long findUnUpLoadIRIRecords(String dbname){
        long res = -1;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false);
            IRITransferBeanMapper mapper = EnhanceMapperFactory.createMapper(IRITransferBeanMapper.class, sqlSession);
            res = mapper.findUnUpLoadIRIRecords();
        }  finally {
            EnhanceDbUtils.closeSession();
        }
        return res;
    }
}
