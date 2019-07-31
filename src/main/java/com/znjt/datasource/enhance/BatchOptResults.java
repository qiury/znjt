package com.znjt.datasource.enhance;

import org.apache.ibatis.executor.BatchResult;

import java.util.List;

/**
 * Created by qiuzx on 2019-03-13
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public class BatchOptResults {
    private int updated = 0;
    private List<BatchResult> batchResults;

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public List<BatchResult> getBatchResults() {
        return batchResults;
    }

    public void setBatchResults(List<BatchResult> batchResults) {
        this.batchResults = batchResults;
    }
}
