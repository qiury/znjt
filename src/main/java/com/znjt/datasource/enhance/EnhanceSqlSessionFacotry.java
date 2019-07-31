package com.znjt.datasource.enhance;

import com.znjt.exs.ExceptionInfoUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 多数据的支持
 * Created by qiuzx on 2019-03-12
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
final public class EnhanceSqlSessionFacotry {
    private static final Logger logger = LoggerFactory.getLogger(EnhanceSqlSessionFacotry.class);
    private static final String CONFIGURATION_PATH = "db/mybatis/mybatis-config.xml";
    private static final Map<String, SqlSessionFactory> SQLSESSIONFACTORYS = new HashMap();

    /**
     * 根据指定的DataSourceEnvironment获取对应的SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory(String environment) {
        SqlSessionFactory sqlSessionFactory = SQLSESSIONFACTORYS.get(environment);
        if (!Objects.isNull(sqlSessionFactory)) {
            return sqlSessionFactory;
        } else {
            try {
                InputStream inputStream = Resources.getResourceAsStream(CONFIGURATION_PATH);
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, environment);
                logger.debug("Get {"+environment+"} SqlSessionFactory successfully.", environment);
            } catch (IOException e) {
                throw new RuntimeException("创建SqlSessionFactory失败",e);
            }
            SQLSESSIONFACTORYS.put(environment, sqlSessionFactory);
            return sqlSessionFactory;
        }
    }
}
