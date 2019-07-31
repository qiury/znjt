package com.znjt.boot;

import com.znjt.dao.beans.ACCTransferIniBean;
import com.znjt.rpc.TransporterServer;
import com.znjt.service.ACCTransferService;
import com.znjt.thrift.GPSImgServer;

import java.util.List;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-03-14
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public class ServerBoot {
    private static TransporterServer servers = new TransporterServer();
    //private static GPSImgServer thrift_server = new GPSImgServer();
    /**
     * 启动Server
     * @param port
     */
    public static void start_server(int port){
        if(servers!=null) {
            servers.startServer(port);
        }
    }

//    public static void start_thrift(int port){
//        if(thrift_server!=null){
//            thrift_server.startSer(port);
//        }
//    }
//    public static void stop_thrift(){
//        if(thrift_server!=null){
//            thrift_server.stopSer();
//        }
//    }

    /**
     * 关闭Server
     */
    public static void stop_server(){
        if(servers!=null){
            servers.shutdown();
        }
        //stop_thrift();
    }
}
