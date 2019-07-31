package com.znjt.datasource.enhance;

import com.znjt.datasource.DbUtil;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by qiuzx on 2019-03-13
 * Company BTT
 * Depart Tech
 */
public class EnhanceDbUtils {

    private static ThreadLocal<SqlSession> threadLocal=new ThreadLocal<SqlSession>();
    private static final Logger logger = LoggerFactory.getLogger(EnhanceDbUtils.class);
    private EnhanceDbUtils(){}


    /**
     * 返回SqlSession
     * @return
     */
    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory){
        SqlSession sqlSession=threadLocal.get();
        if(sqlSession==null){
            sqlSession=sessionFactory.openSession();
            threadLocal.set(sqlSession);
        }
        return sqlSession;
    }
    /**
     * 返回指定处理器类型的且不自动提交的SqlSession,
     */
    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory,ExecutorType executorType){
        return getSqlSession(sessionFactory,executorType,false);
    }
    /**
     * 返回指定处理器类型的SqlSession
     */
    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory,ExecutorType executorType,boolean isAutoCommit){
        SqlSession sqlSession = threadLocal.get();
        if(sqlSession==null){
            sqlSession=sessionFactory.openSession(executorType,isAutoCommit);
            threadLocal.set(sqlSession);
        }
        return sqlSession;
    }

    /**
     * 是否自动提交
     * @param isAutoCommit
     * @return
     */
    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory,boolean isAutoCommit) {
        SqlSession sqlSession=threadLocal.get();
        if(sqlSession==null){
            sqlSession=sessionFactory.openSession(isAutoCommit);
            threadLocal.set(sqlSession);
        }
        return sqlSession;
    }


    //关闭SqlSession
    public static void closeSession(){
        SqlSession sqlSession=threadLocal.get();
        if(sqlSession!=null){
            sqlSession.close();
            threadLocal.remove();
        }
    }

    public static void commit(){
        SqlSession sqlSession=threadLocal.get();
        if(sqlSession!=null){
            sqlSession.commit();
        }
    }

    /**
     * 打印批处理信息
     * @param results
     */
    public static void printBatchResults(List<BatchResult> results) {
        if (results.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Batch summary:\n");
            for (int i = 0; i < results.size(); i++) {
                BatchResult result = results.get(i);
                sb.append("Result ").append(i).append(":\t");
                sb.append(result.getSql().replaceAll("\n", "").replaceAll("\\s+", " ")).append("\t");
                sb.append("Update counts: ").append(Arrays.toString(result.getUpdateCounts())).append("\n");
            }
            logger.debug(sb.toString());
        }
    }
}
