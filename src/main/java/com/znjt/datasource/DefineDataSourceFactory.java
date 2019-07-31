package com.znjt.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.znjt.exs.DBException;
import com.znjt.exs.ExceptionInfoUtils;
import org.apache.ibatis.datasource.DataSourceFactory;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.IntStream;

/**
 * Created by qiuzx on 2019-03-11
 * Company BTT
 * Depart Tech
 * @author qzx
 */

public class DefineDataSourceFactory extends DruidDataSourceFactory implements DataSourceFactory {
    private  Properties props;
    @Override
    public void setProperties(Properties properties) {
        this.props = properties;
    }

    @Override
    public DataSource getDataSource() {
        DataSource ds = null;
        try {
           ds = createDataSource(props);
  //         configDatasource((DruidDataSource)ds);
        } catch (Exception e) {
            throw new DBException(e,"初始化Durid连接池识别，请检查参数配置信息.错误信息："+ExceptionInfoUtils.getExceptionCauseInfo(e));
        }
        return ds;
    }

    /**
     * 配置额外的参数
     * @param dataSource
     */
    private void configDatasource(DruidDataSource dataSource){
//        String value = props.getProperty("druid.failFast");
//        if(Objects.nonNull(value)){
//            dataSource.setFailFast(Boolean.parseBoolean(value));
//        }
//        value = props.getProperty("druid.connectionErrorRetryAttempts");
//        if(Objects.nonNull(value)){
//            dataSource.setConnectionErrorRetryAttempts(Integer.parseInt(value));
//        }
//        value = props.getProperty("druid.breakAfterAcquireFailure");
//        if(Objects.nonNull(value)){
//            dataSource.setBreakAfterAcquireFailure(Boolean.parseBoolean(value));
//        }
//        value = props.getProperty("druid.timeBetweenConnectErrorMillis");
//        if(Objects.nonNull(value)){
//            dataSource.setTimeBetweenConnectErrorMillis(Long.parseLong(value));
//        }

    }

}
