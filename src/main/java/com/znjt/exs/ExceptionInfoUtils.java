package com.znjt.exs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Created by qiuzx on 2019-03-11
 * Company BTT
 * Depart Tech
 */
public class ExceptionInfoUtils {
    private ExceptionInfoUtils() {
    }
    /**
     * 获取异常堆栈信息
     * @param e
     * @return
     */
    public static String getExceptionCauseInfo(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } finally {
            if(pw!=null) {
                pw.close();
            }
        }
    }


}
