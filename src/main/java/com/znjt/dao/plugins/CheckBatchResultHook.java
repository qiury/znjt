package com.znjt.dao.plugins;

import org.apache.ibatis.executor.BatchResult;

import java.util.List;

/**
 * Created by qiuzx on 2019-03-22
 * Company BTT
 * Depart Tech
 * 校验批处理结果
 */
public interface CheckBatchResultHook {
    boolean checkBatchResult(List<BatchResult> results);
}
