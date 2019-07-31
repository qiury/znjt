package com.znjt.exs;

/**
 * Created by qiuzx on 2019-03-11
 * Company BTT
 * Depart Tech
 */
public class DBException extends RuntimeException {
    private String msg;
    public DBException(Throwable e,String cause){
        super(e);
        this.msg = cause;
    }
}
