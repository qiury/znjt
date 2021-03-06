package com.znjt.utils;

import org.slf4j.Logger;

/**
 * Created by qiuzx on 2019-03-27
 * Company BTT
 * Depart Tech
 */
public class LoggerUtils {
    private  LoggerUtils(){}
    public static void debug(Logger logger,String msg){
        //如果不进行界别判断，虽然日志不会被输出，但是如果存在msg拼接的话，拼接工作已经完成！
        if(logger.isDebugEnabled()){
            logger.debug(msg);
        }
    }
    public static  void info(Logger logger,String msg){
        if(logger.isInfoEnabled()){
            logger.info(msg);
        }
    }
    public static void warn(Logger logger,String msg){
        if(logger.isWarnEnabled()){
            logger.warn(msg);
        }
    }
    public static void error(Logger logger,String msg){
        if(logger.isErrorEnabled()){
            logger.error(msg);
        }
    }

}
