package com.znjt.rpc;

import com.znjt.boot.Boot;
import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.exs.ExceptionInfoUtils;
import com.znjt.proto.DataType;
import com.znjt.proto.GPSRecord;
import com.znjt.proto.GPSSingleRecord;
import com.znjt.proto.SyncDataResponse;
import com.znjt.service.GPSTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by qiuzx on 2019-03-14
 * Company BTT
 * Depart Tech
 *
 * @author qiuzx
 */
public class TransferRespJobUtils {
    private Logger logger = LoggerFactory.getLogger(TransferRespJobUtils.class);
    private ExecutorService worker = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    private final ConcurrentLinkedQueue<SyncDataResponse> jobs = new ConcurrentLinkedQueue();
    private final List<SyncDataResponse> tasks = new ArrayList<>();
    private Object monitor = null;
    private int max_batch_size;
    private volatile boolean stop_task = false;
    private GPSTransferService localTransferService;

    public TransferRespJobUtils(GPSTransferService localTransferService, Object monitor, int max_batch_size) {
        this.localTransferService = localTransferService;
        this.monitor = monitor;
        this.max_batch_size = max_batch_size;
        startJob(createMonitorJob());
    }

    /**
     * 将响应的任务添加到任务队列
     *
     * @param response
     */
    public void addNewJob(SyncDataResponse response) {
        logger.info("服务端返回图像处理结果，将任务方法工作队列，唤醒工作线程处理响应结果....");
        jobs.offer(response);
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    /**
     * 创建一个监视job
     *
     * @return
     */
    private Runnable createMonitorJob() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!stop_task) {
                    try {
                        getJobs();
                        if(logger.isDebugEnabled()) {
                            logger.debug("客户端待处理的图像上传结果更新任务个数 = " + tasks.size());
                        }
                        if (tasks.size() > 0) {
                            List<GPSTransferIniBean> datas = new ArrayList<>();
                            GPSTransferIniBean gpsTransferIniBean = null;
                            for (SyncDataResponse request : tasks) {
                                //处理服务端发送的响应
                                DataType dataType = request.getDataType();
                                if (dataType == DataType.T_GPS) {
                                    GPSRecord gpsRecord = request.getGpsRecord();
                                    gpsTransferIniBean = createGPSGpsTransferIniBeanFromGPSRecord(gpsRecord);
                                    if(gpsTransferIniBean!=null){
                                        datas.add(gpsTransferIniBean);
                                    }
                                }
                            }
                            if(datas.size()>0){
                                if(logger.isDebugEnabled()) {
                                    logger.debug("客户端图像上传结果工作线程 : 开始更新Master数据库中记录的状态，任务个数="+datas.size());
                                }
                                localTransferService.updateCurrentUploadedSuccessGPSImgRecords(Boot.DOWNSTREAM_DBNAME,datas);
                            }
                        } else {
                            synchronized (monitor) {
                                try {
                                    monitor.wait();
                                } catch (InterruptedException e) {
                                    break;
                                }
                            }
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        logger.error(ExceptionInfoUtils.getExceptionCauseInfo(ex));
                    }
                }
            }
        };
        return runnable;
    }

    /**
     * 根据gpsrecord创建GPSTransferIniBean对象
     * @param gpsRecord
     * @return
     */
    public GPSTransferIniBean createGPSGpsTransferIniBeanFromGPSSingleRecord(GPSSingleRecord gpsRecord){
        String cid = gpsRecord.getClientRecordId();
        boolean file_err = gpsRecord.getFileErr();
        boolean opsres = gpsRecord.getServOpsRes();
        GPSTransferIniBean gpsTransferIniBean = null;
        //后台成功进行了处理的记录才会被更新
        if(opsres) {
            gpsTransferIniBean = new GPSTransferIniBean();
            gpsTransferIniBean.setGpsid(cid);
            gpsTransferIniBean.setFile_err(file_err);
            gpsTransferIniBean.setImg_uploaded(opsres);
        }
        return  gpsTransferIniBean;
    }
    /**
     * 根据gpsrecord创建GPSTransferIniBean对象
     * @param gpsRecord
     * @return
     */
    public GPSTransferIniBean createGPSGpsTransferIniBeanFromGPSRecord(GPSRecord gpsRecord){
        String cid = gpsRecord.getClientRecordId();
        String did = gpsRecord.getDataId();
        boolean file_err = gpsRecord.getFileErr();
        boolean opsres = gpsRecord.getServOpsRes();
        GPSTransferIniBean gpsTransferIniBean = null;
        //后台成功进行了处理的记录才会被更新
        if(opsres) {
            gpsTransferIniBean = new GPSTransferIniBean();
            gpsTransferIniBean.setGpsid(cid);
            gpsTransferIniBean.setFile_err(file_err);
            gpsTransferIniBean.setImg_uploaded(opsres);
        }
        return  gpsTransferIniBean;
    }
    /**
     * 开始任务
     */
    private void startJob(Runnable runnable) {
        worker.submit(runnable);
    }

    /**
     * 批量获取任务
     *
     * @return
     */
    public List<SyncDataResponse> getJobs() {
        tasks.clear();
        if (!jobs.isEmpty()) {
            int index = 0;
            SyncDataResponse response;
            int try_times = 1;
            while (index < max_batch_size) {
                if(jobs.isEmpty()){
                    /*
                      因为服务端是逐个返回处理结果的，
                      因此在没有获取到任务时，尝试等待一段时间，
                      尽量批处理任务.
                      注意：这个等待时间不能太长，否则会造成客户端重复上传的问题出现
                     */
                   // wait4uint();
                    try_times--;
                    if(try_times<=0){
                        break;
                    }
                    continue;
                }
                response = jobs.poll();
                if(response==null){
                    break;
                }
                tasks.add(response);
                index++;
            }
        }
        return tasks;
    }

    /**
     * 等待一个单元
     */
    private void wait4uint(){
        try {
            Thread.sleep(200);
        }catch (Exception ex){
        }
    }

    /**
     * 获取单个任务
     *
     * @return
     */
    public SyncDataResponse getSignalJob() {
        tasks.clear();
        if (!jobs.isEmpty()) {
            return jobs.poll();
        }
        return null;
    }

    public void close() {
        stop_task = true;
        synchronized (monitor){
            monitor.notifyAll();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("开始执行关闭客户端处理图像上传后更新数据库的任务线程[shutdown],最长等待60S");
        }
        if (worker != null) {
            try {
                worker.shutdown();
               if(worker.awaitTermination(60, TimeUnit.SECONDS)){
                   if (logger.isDebugEnabled()) {
                       logger.debug("关闭客户端处理图像上传后更新数据库的任务线程--完成");
                   }
               }else {
                   if (logger.isDebugEnabled()) {
                       logger.debug("等待关闭客户端处理图像上传后更新数据库的任务线程--超时[shutdownNow],强行关闭");
                   }
                   worker.shutdownNow();
               }
            } catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("执行关闭客户端处理图像上传后更新数据库的任务线程[被Interrupted]");
                }
            }
        }
    }
}
