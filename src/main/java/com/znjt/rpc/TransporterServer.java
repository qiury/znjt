package com.znjt.rpc;

import com.znjt.boot.Boot;
import com.znjt.exs.ExceptionInfoUtils;
import com.znjt.utils.FileIOUtils;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by qiuzx on 2019-03-14
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public class TransporterServer {
    private Logger logger = LoggerFactory.getLogger(TransporterServer.class);
    private Server server = null;
    private int RPC_PORT = 9898;
    private TransferProtoImpl4Server transporterServer;
    public static ThreadPoolExecutor upload_file_pool_executors = null;
    static {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(409600);
        int processors = Runtime.getRuntime().availableProcessors();
        upload_file_pool_executors = new ThreadPoolExecutor(processors,processors*2, 2, TimeUnit.MILLISECONDS,queue,new NameTreadFactory(),new CustomerIgnorePolicy());
    }

    static class NameTreadFactory implements ThreadFactory {
        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "server_upload-thread-" + mThreadNum.getAndIncrement());
            System.out.println(t.getName() + " has been created");
            return t;
        }
    }
    public static class CustomerIgnorePolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                //核心改造点，由blockingqueue的offer改成put阻塞方法
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // doLog(r, e);
        }

        private void doLog(Runnable r, ThreadPoolExecutor e) {
            // 可做日志记录等
            // System.err.println( r.toString() + " rejected");
            //System.out.println("completedTaskCount: " + e.getCompletedTaskCount());
        }
    }


    private void start() throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("Begin Start RPC Server At " + RPC_PORT + " .");
        }
        transporterServer = new TransferProtoImpl4Server();

        server = ServerBuilder.forPort(RPC_PORT)
                .addService(transporterServer)
                .maxInboundMessageSize(Boot.FRAME_MAX_SIXE)
                .executor(upload_file_pool_executors)
                .build();
        server.start();
        if (logger.isInfoEnabled()) {
            logger.info("RPC Server Started Bind At " + RPC_PORT + " .");
        }
        //程序退出时关闭资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (logger.isInfoEnabled()) {
                logger.info("shutting down gRPC server since JVM is shutting down");
            }
            shutdown();
            if (logger.isInfoEnabled()) {
                logger.info("server shut down");
            }
        }));
    }

    private void await4terminal() {
        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
        }
        if (logger.isInfoEnabled()) {
            logger.info("RPC Sever Stop Success");
        }
    }

    /**
     * 启动RPC Server
     *
     * @throws Exception
     */
    public void startServer(int port) {
        try {
            if(port>0) {
                RPC_PORT = port;
            }
            start();
            await4terminal();
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("RPC Server Started Failure Cause by " + ExceptionInfoUtils.getExceptionCauseInfo(e));
            }
        }
    }

    public void shutdown() {
        if (server != null) {
            if(logger.isInfoEnabled()) {
                logger.info("Begin to stop RPC Server...");
            }
            server.shutdown();
        }
        if(upload_file_pool_executors!=null){
            upload_file_pool_executors.shutdown();
        }
    }
}
