package com.znjt.datasource;
import com.znjt.exs.ExceptionInfoUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by qiuzx on 2019-03-12
 * Company BTT
 * Depart Tech
 */
public class DbUtil {
    private static ThreadLocal<SqlSession> SQLSESSION_THREADLOCAL =new ThreadLocal<SqlSession>();
    private static final Logger logger = LoggerFactory.getLogger(DbUtil.class);
    private static SqlSessionFactory sessionFactory;
    private DbUtil(){}
    static{
        try {
            /**
             * 根据build的源码，在build方法的最后会关闭传入的inputstream
             * 因此此处无需再次关闭
             */
            InputStream in = Resources.getResourceAsStream("db/mybatis/mybatis-config.xml");
            sessionFactory = new SqlSessionFactoryBuilder().build(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回SqlSession
     * @return
     */
    public static SqlSession getSession(){
        SqlSession sqlSession= SQLSESSION_THREADLOCAL.get();
        if(sqlSession==null){
            sqlSession=sessionFactory.openSession();
            SQLSESSION_THREADLOCAL.set(sqlSession);
        }
        return sqlSession;
    }
    /**
     * 返回指定处理器类型的且不自动提交的SqlSession,
     */
    public static SqlSession getSession(ExecutorType executorType){
       return getSession(executorType,false);
    }
    /**
     * 返回指定处理器类型的SqlSession
     */
    public static SqlSession getSession(ExecutorType executorType,boolean isAutoCommit){
        SqlSession sqlSession = SQLSESSION_THREADLOCAL.get();
        if(sqlSession==null){
            sqlSession=sessionFactory.openSession(executorType,isAutoCommit);
            SQLSESSION_THREADLOCAL.set(sqlSession);
        }
        return sqlSession;
    }

    /**
     * 是否自动提交
     * @param isAutoCommit
     * @return
     */
    public static SqlSession getSqlSession(boolean isAutoCommit) {
        SqlSession sqlSession= SQLSESSION_THREADLOCAL.get();
        if(sqlSession==null){
            sqlSession=sessionFactory.openSession(isAutoCommit);
            SQLSESSION_THREADLOCAL.set(sqlSession);
        }
        return sqlSession;
    }

    //关闭SqlSession
    public static void closeSqlSession(){
        SqlSession sqlSession= SQLSESSION_THREADLOCAL.get();
        if(sqlSession!=null){
            sqlSession.close();
            SQLSESSION_THREADLOCAL.remove();
        }
    }

    public static void commit(){
        SqlSession sqlSession= SQLSESSION_THREADLOCAL.get();
        if(sqlSession!=null){
            sqlSession.commit();
        }
    }
}
