package com.znjt.dao.plugins;

import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by qiuzx on 2019-03-22
 * Company BTT
 * Depart Tech
 */
public class ComplexExecutor extends BatchExecutor {
    private ExecutorType originalExecutorType;

    public ComplexExecutor(Configuration configuration, Transaction transaction, ExecutorType originalExecutorType) {
        super(configuration, transaction);
        this.originalExecutorType =originalExecutorType;

    }

    @Override
    public int doUpdate(MappedStatement ms, Object parameterObject) throws SQLException {
        int result =super.doUpdate(ms,parameterObject);
        if(!MybatisExecutorContext.isBatchExecutorMode()){
            List<BatchResult> results=this.doFlushStatements(false);
            return results.get(0).getUpdateCounts()[0];
        }
        return result;
    }

    @Override
    public List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException {
        List<BatchResult> results =super.doFlushStatements(isRollback);
        MybatisExecutorContext.getCheckBatchResultHook().checkBatchResult(results);
        return results;
    }

    public ExecutorType getOriginalExecutorType() {
        return originalExecutorType;
    }
}
