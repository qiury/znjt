package com.znjt.dao.impl;

import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.dao.mapper.GPSTransferBeanMapper;
import com.znjt.datasource.enhance.EnhanceDbUtils;
import com.znjt.datasource.enhance.EnhanceMapperFactory;
import com.znjt.exs.DBException;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by qiuzx on 2019-03-15
 * Company BTT
 * Depart Tech
 */
public class GPSTransferDao {
    public List<GPSTransferIniBean> findUnUpLoadGPSRecordDatas(String dbname,int pageSize) {
        List<GPSTransferIniBean> recordDatas = null;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, true);
            GPSTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(GPSTransferBeanMapper.class, sqlSession);
            recordDatas = mapper.findUnUpLoadGPSRecordDatas(pageSize);
        } finally {
            EnhanceDbUtils.closeSession();
        }
        return recordDatas;
    }

    public List<GPSTransferIniBean> findUnUpLoadGPSRecordDatasOnCondition(String dbname,int pageSize) {
        List<GPSTransferIniBean> recordDatas = null;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, true);
            GPSTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(GPSTransferBeanMapper.class, sqlSession);
            recordDatas = mapper.findUnUpLoadGPSRecordDatasOnCondition(pageSize);
        } finally {
            EnhanceDbUtils.closeSession();
        }
        return recordDatas;
    }

    public List<GPSTransferIniBean> findUnUpLoadGPSImgDatas(String dbname,int pageSize) {
        List<GPSTransferIniBean> recordDatas = null;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, true);
            GPSTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(GPSTransferBeanMapper.class, sqlSession);
            recordDatas = mapper.findUnUpLoadGPSImgDatas(pageSize);
        }  finally {
            EnhanceDbUtils.closeSession();
        }
        return recordDatas;
    }

    public List<GPSTransferIniBean> findUnUpLoadGPSImgDatas4EvenOrOdd(String dbname,int pageSize,int mod) {
        List<GPSTransferIniBean> recordDatas = null;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, true);
            GPSTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(GPSTransferBeanMapper.class, sqlSession);
            recordDatas = mapper.findUnUpLoadGPSImgDatas4EvenOrOdd(pageSize,mod);
        }  finally {
            EnhanceDbUtils.closeSession();
        }
        return recordDatas;
    }



    public void updateCurrentUpLoadedSuccessGPSRescords(String dbname,List<GPSTransferIniBean> gpsTransferIniBeans) {
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false);
            GPSTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(GPSTransferBeanMapper.class, sqlSession);
            mapper.updateCurrentUpLoadedSuccessGPSRescords(gpsTransferIniBeans);
            sqlSession.commit();
        } finally {
            EnhanceDbUtils.closeSession();
        }
    }

    public void updateCurrentUploadedSuccessGPSImgRecords(String dbname,List<GPSTransferIniBean> gpsTransferIniBeans) {
        List<GPSTransferIniBean> recordDatas = null;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false);
            GPSTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(GPSTransferBeanMapper.class, sqlSession);
            mapper.updateCurrentUploadedSuccessGPSImgRecords(gpsTransferIniBeans);
            sqlSession.commit();
        } finally {
            EnhanceDbUtils.closeSession();
        }
    }

    public void upLoadGPSRecordDatas2UpStream(String dbname,List<GPSTransferIniBean> gpsTransferIniBeans) {
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false);
            GPSTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(GPSTransferBeanMapper.class, sqlSession);
            mapper.upLoadGPSRecordDatas2UpStream(gpsTransferIniBeans);
            sqlSession.commit();
        } finally {
            EnhanceDbUtils.closeSession();
        }
    }

    /**
     * 更新最新图像的路径到数据库
     * @param dbname
     * @param gpsTransferIniBean
     */
    public int updateGPSImgPath2DBRecord(String dbname,GPSTransferIniBean gpsTransferIniBean){
        int updated = 0;
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false);
            GPSTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(GPSTransferBeanMapper.class, sqlSession);
            updated = mapper.updateGPSImgPath2DBRecord(gpsTransferIniBean);
            sqlSession.commit();
        } finally {
            EnhanceDbUtils.closeSession();
        }
        return updated;
    }

    public void updateBatchGPSImgPath2DBRecord(String dbname,List<GPSTransferIniBean> gpsTransferIniBeans){
        try {
            SqlSession sqlSession = EnhanceMapperFactory.getMultiSqlSession(dbname, false);
            GPSTransferBeanMapper mapper = EnhanceMapperFactory.createMapper(GPSTransferBeanMapper.class, sqlSession);
            mapper.updateBatchGPSImgPath2DBRecord(gpsTransferIniBeans);
            sqlSession.commit();
        } finally {
            EnhanceDbUtils.closeSession();
        }
    }
}
