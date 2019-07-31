package com.znjt.net;

import com.znjt.exs.ExceptionInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by qiuzx on 2019-03-11
 * Company BTT
 * Depart Tech
 */
public class NetStatusUtils {
    private static final Logger logger = LoggerFactory.getLogger(NetStatusUtils.class);
    private NetStatusUtils(){}
    private static Runtime runtime = Runtime.getRuntime();
    private static String ping_cmd = "ping -c ";

    static {
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")){
            ping_cmd = "ping -n  ";
        }
    }

    /**
     * 测试网络条件
     * @param address
     * @return
     */
    public static NetQuality getNetworkQuality(String address, int try_times){
        boolean net_ok = false;
        int fail_times = 0;
        NetQuality quality = NetQuality.NORMAL;
        LineNumberReader returnData = null;
        if(runtime!=null){
            try {
                Process exec = runtime.exec(ping_cmd + (try_times>0?try_times+" " : " 1 ") + address);
                InputStreamReader r = new InputStreamReader(exec.getInputStream());
                returnData = new LineNumberReader(r);
                String returnMsg="";
                String line = "";
                while ((line = returnData.readLine()) != null) {
                    if(line.contains("timeout")){
                        fail_times++;
                    }
                    logger.debug(line);
                }
                if(fail_times==0){
                    quality = NetQuality.GOOD;
                }else{
                    //成功率
                    int success_per =   (int)(((try_times-fail_times)*1.0/try_times)*100);
                    if(success_per>60){
                        quality = NetQuality.NORMAL;
                    }else if(success_per>0){
                        quality = NetQuality.BAD;
                    }else{
                        quality = NetQuality.BREOKEN;
                    }
                }
            }catch (Exception e){
                logger.warn(ExceptionInfoUtils.getExceptionCauseInfo(e));
            }finally {
                if(returnData!=null){
                    try {
                        returnData.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return quality;
    }
}
