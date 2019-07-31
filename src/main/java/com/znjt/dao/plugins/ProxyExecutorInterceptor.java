package com.znjt.dao.plugins;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Proxy;
import java.util.Properties;

/**
 * Created by qiuzx on 2019-03-22
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public class ProxyExecutorInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor == false) {
            return target;
        }
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new ProxyExecutorHandler((Executor)target));
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
