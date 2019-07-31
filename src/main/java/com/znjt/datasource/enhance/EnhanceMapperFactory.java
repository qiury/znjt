package com.znjt.datasource.enhance;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * MapperFactory 创建一个Mapper实例 mapper
 * Created by qiuzx on 2019-03-12
 * Company BTT
 * Depart Tech
 */
final public class EnhanceMapperFactory {

    /**
     * 通过datasource 创建一个Mapper 的实现类 mapper
     */
    @SuppressWarnings("unchecked")
    public static <T> T createMapper(Class<? extends Mapper> clazz,SqlSession sqlSession) {
        Mapper mapper = sqlSession.getMapper(clazz);
        return (T) MapperProxy.bind(mapper, sqlSession);
    }

    /**
     * 根据数据源名称获取sqlsession
     * @param datasource
     * @param isAutoCommit
     * @param executorType
     * @return
     */
    public static SqlSession getMultiSqlSession(String datasource,boolean isAutoCommit, ExecutorType executorType){
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory(datasource);
        SqlSession sqlSession = EnhanceDbUtils.getSqlSession(sqlSessionFactory,executorType,isAutoCommit);
        return sqlSession;
    }

    /**
     * 根据数据源名称获取sqlsession
     * @param datasource
     * @param isAutoCommit
     * @return
     */
    public static SqlSession getMultiSqlSession(String datasource,boolean isAutoCommit){
        return getMultiSqlSession(datasource,isAutoCommit,ExecutorType.SIMPLE);
    }

    /**
     * Mapper代理: 执行 mapper 方法和关闭 sqlSession
     */
    private static class MapperProxy implements InvocationHandler {
        private Mapper mapper;
        private SqlSession sqlSession;

        private MapperProxy(Mapper mapper, SqlSession sqlSession) {
            this.mapper = mapper;
            this.sqlSession = sqlSession;
        }

        @SuppressWarnings("unchecked")
        private static Mapper bind(Mapper mapper, SqlSession sqlSession) {
            return (Mapper) Proxy.newProxyInstance(mapper.getClass().getClassLoader(),
                    mapper.getClass().getInterfaces(), new MapperProxy(mapper, sqlSession));
        }

        /**
         * 执行mapper方法并最终关闭sqlSession
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object object = null;
            try {
                object = method.invoke(mapper, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return object;
        }
    }
    /**
     * 获取数据源 datasource 的 SqlSessionFactory
     */
    private static SqlSessionFactory getSqlSessionFactory(String datasource) {
        return EnhanceSqlSessionFacotry.getSqlSessionFactory(datasource);
    }
}
