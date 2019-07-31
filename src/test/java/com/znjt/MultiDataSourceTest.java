package com.znjt;

import com.znjt.datasource.enhance.EnhanceDbUtils;
import com.znjt.datasource.enhance.EnhanceMapperFactory;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qiuzx on 2019-03-11
 * Company BTT
 * Depart Tech
 * 手工清理导入未被使用的包 ctrl+alt+o
 * <p>
 * #select @@global.tx_isolation,@@tx_isolation;
 * #set session tx_isolation='read-committed';
 * #set GLOBAL tx_isolation='read-committed';
 */
public class MultiDataSourceTest {
    /**
     * 测试多数据的连接
     */
    @Test
    public void testMultiDataSource() {
        SqlSession sqlSession = null;
        try {
            sqlSession = EnhanceMapperFactory.getMultiSqlSession("db01", false);
            Assert.assertNotNull(sqlSession);
        } catch (Exception e) {

        } finally {
            EnhanceDbUtils.closeSession();
        }
    }

}
