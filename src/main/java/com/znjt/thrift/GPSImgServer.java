package com.znjt.thrift;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by qiuzx on 2019-03-27
 * Company BTT
 * Depart Tech
 */
public class GPSImgServer {
    private TServer server;

    /**
     * 启动Ser
     * @throws TTransportException
     */
    public void startSer(int port) {
        //创建一个非阻塞的ServerSocket
        TNonblockingServerSocket serverSocket = null;
        try {
            serverSocket = new TNonblockingServerSocket(port);
        } catch (TTransportException e) {
            e.printStackTrace();
            return;
        }
        //创建一个THsHaServer的参数对象，并设置里面的参数（serversocket，线程大小等）
        THsHaServer.Args arg = new THsHaServer.Args(serverSocket).minWorkerThreads(2).maxWorkerThreads(16);
        GPSImgService.Processor<GPSImageServiceImpl> processor = new GPSImgService.Processor<>(new GPSImageServiceImpl());

        //设置传输层（TFramedTransport专门为异步类型server服务）
        arg.transportFactory(new ExplainFactory());
        //设置协议层（使用压缩协议）
        arg.protocolFactory(new TCompactProtocol.Factory());
        //设置处理器工厂
        arg.processorFactory(new TProcessorFactory(processor));

        /*
          通过参数arg创建一个非阻塞的Server对象
          THsHaServer（Half Sync Half ASync）半同步半异步的Server
         */
        server = new THsHaServer(arg);
        server.setServerEventHandler(new TServerEventHandler() {
            @Override
            public void preServe() {
                System.out.println("Thrift Server Create Success listen at "+port);
            }

            @Override
            public ServerContext createContext(TProtocol tProtocol, TProtocol tProtocol1) {
                return null;
            }

            @Override
            public void deleteContext(ServerContext serverContext, TProtocol tProtocol, TProtocol tProtocol1) {

            }

            @Override
            public void processContext(ServerContext serverContext, TTransport tTransport, TTransport tTransport1) {

            }
        });
        //死循环
        server.serve();
    }

    /**
     * 停止
     */
    public void stopSer(){
        if(server!=null){
            server.stop();
        }
    }
}
