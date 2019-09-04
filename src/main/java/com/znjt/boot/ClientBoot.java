package com.znjt.boot;

import com.cmd.Shutdown;
import com.znjt.dao.beans.ACCTransferIniBean;
import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.dao.beans.IRITransferIniBean;
import com.znjt.dao.beans.PCITransferIniBean;
import com.znjt.exs.ExceptionInfoUtils;
import com.znjt.net.NetQuality;
import com.znjt.net.NetStatusUtils;
import com.znjt.net.NetUtils;
import com.znjt.rpc.TransportClient;
import com.znjt.rpc.UpLoadReson;
import com.znjt.service.ACCTransferService;
import com.znjt.service.GPSTransferService;
import com.znjt.service.IRITransferService;
import com.znjt.service.PCITransferService;
import com.znjt.utils.FileIOUtils;
import com.znjt.utils.LoggerUtils;
import io.grpc.netty.shaded.io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by qiuzx on 2019-03-14
 * Company BTT
 * Depart Tech
 *
 * @author qiuzx
 */
public class ClientBoot {
    private Logger logger = LoggerFactory.getLogger(ClientBoot.class);
    private static ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 4, new DefaultThreadFactory());
    private String server_ip = null;
    private String ip_pattern = null;
    private String inner_ip_pattern = null;
    private int server_port = 9898;
    //网络是否准许连接（是否畅通）
    private volatile boolean net_allowed_connect = false;
    private volatile boolean exit_sys_stauts = false;
    private ACCTransferService accTransferService;
    private GPSTransferService gpsTransferService;
    private IRITransferService iriTransferService;
    private PCITransferService pciTransferService;
    private TransportClient client;

    private static boolean RATE_LIMITING = true;
    private static final int INC_STEP = 5;
    //每条上传指令等待最大时长，在这个时间范围内的认为是合理的，否则需要减速
    private static final long TIME_WATER_LINE = 10000;
    private String[] ext_tables = null;
    private boolean multi_condition_on_gps = false;
    private boolean test_upload_speed = false;
    private static ThreadPoolExecutor upload_file_pool = null;
    private static volatile boolean has_data_transfer = false;
    private static volatile LocalDateTime lastest_upload_time = LocalDateTime.now();

    //超过多久没有数据上传就可以执行关机计划
    public static int wait_for_check_has_datas = 600;
    //执行关机计划的具体时间
    private static volatile LocalDateTime exec_shutdown_cmd_time = null;
    //当前的任务是否被取消了
    private static volatile boolean wating_shutdown = false;
    private static volatile boolean manual_canceled_shutdown = false;
    private static final int delay_sec_to_shutdown_sys = 20;
    public static boolean auto_off_os = false;

    //-1表示上传job已经结束，[0-正无穷]表示合法的值
    private static long ACC_JOB_START_ID = -1;
    //表示一轮上传工作是否结束
    private static boolean ACC_JOB_OVER = true;

    private static long GPS_JOB_START_ID = -1;
    private static boolean GPS_JOB_OVER = true;

    private static long GPS_IMG_JOB_START_ID = -1;
    private static boolean GPS_IMG_JOB_OVER = true;

    private static long GPS_PIC_JOB_START_ID = -1;
    private static boolean GPS_PIC_JOB_OVER = true;

    private static long IRI_JOB_START_ID = -1;
    private static boolean IRI_JOB_OVER = true;

    private static long PCI_JOB_START_ID = -1;
    private static boolean PCI_JOB_OVER = true;

    //每隔10轮上传就统计一次上传进度
    private static final int REPEATER = 10;


    static {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(409600);
        int processors = Runtime.getRuntime().availableProcessors();
        upload_file_pool = new ThreadPoolExecutor(processors, processors * 2, 2, TimeUnit.MILLISECONDS, queue, new NameTreadFactory(), new CustomerIgnorePolicy());
    }

    static class NameTreadFactory implements ThreadFactory {

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "upload-thread-" + mThreadNum.getAndIncrement());
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

    /**
     * 启动客户端任务
     *
     * @param server_ip
     * @param server_port
     */
    public void start_client_jobs(String server_ip, int server_port, String ip_pattern, String inner_ip_pattern, String[] ext_tables, boolean test_upload_speed) {
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.ip_pattern = ip_pattern;
        this.ext_tables = ext_tables;
        this.inner_ip_pattern = inner_ip_pattern;
        this.test_upload_speed = test_upload_speed;
        if (server_ip == null || server_ip.equals("")) {
            throw new RuntimeException("IP地址[" + server_ip + "]不合法，无法启动客户端");
        }
        init();
    }


    private void init() {
        checkCondition();
        RATE_LIMITING = Boot.expire();
        accTransferService = new ACCTransferService();
        gpsTransferService = new GPSTransferService();
        iriTransferService = new IRITransferService();
        pciTransferService = new PCITransferService();
        client = new TransportClient(gpsTransferService, server_ip, server_port, Boot.IMAGE_BATCH_SIZE);
        start_monitor_jobs();
    }

    private void checkCondition() {
        if (this.ext_tables != null) {
            List<String> tbs = Arrays.asList(ext_tables);
            boolean has_pci = tbs.contains("pci");
            boolean has_iri = tbs.contains("iri");
            //this.multi_condition_on_gps = has_iri&&has_pci; //暂时取消该条件的过滤
        }
    }

    /**
     * 启动任务
     */
    private void start_monitor_jobs() {
        start_net_jobs();
        if (auto_off_os) {
            start_check_shutdown_sys_jobs();
        }
        if (test_upload_speed) {
            start_gps_img_test_jobs();
        } else {
            start_gps_record_jobs();
            if (!Boot.assign_imgs_path_by_manual) {
                start_gps_img_jobs4Even();
                start_gps_img_jobs4Odd();
            } else {
                //手工指定图像路径，不需要执行图像上传任务，只需要执行图像路径同步即可
                start_gps_img_path_jobs();
            }
//            start_acc_jobs();
//            start_pci_jobs();
//            start_iri_jobs();
        }
    }


    public void terminal_jobs() {
        if (client != null) {
            client.release();
        }
        exit_sys_stauts = true;
    }

    private void shutdownThreadPool() {
        if (upload_file_pool != null && !upload_file_pool.isShutdown()) {
            upload_file_pool.shutdown();
        }
    }

    /**
     * 取消关机命令
     */
    public void cancel_shoudown_jobs() {
        if (wating_shutdown) {
            manual_canceled_shutdown = true;
            exec_shutdown_cmd_time = null;
            update_lastest_upload_time();
            Shutdown.printCancelShutdownInfo();
        }
        wating_shutdown = false;
    }

    private void start_check_shutdown_sys_jobs() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            //如果人工取消过关机计划，那么就永久关闭该计划
            // if(manual_canceled_shutdown){
            //     return;
            //  }
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(lastest_upload_time, now);
            long dt = duration.getSeconds();
            if (dt > wait_for_check_has_datas) {
                if (exec_shutdown_cmd_time == null) {
                    exec_shutdown_cmd_time = LocalDateTime.now().plusSeconds(60L);
                    Shutdown.printShotdownInfo(exec_shutdown_cmd_time);
                    wating_shutdown = true;
                } else if (now.isAfter(exec_shutdown_cmd_time)) {
                    try {
                        Shutdown.shutDownSysNow(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("将在 [" + exec_shutdown_cmd_time.format(DateTimeFormatter.ofPattern("hh:mm:ss")) + "] 发送关机命令....");
                }
            } else {
                System.err.println("系统未达到自动关机条件....");
            }
        }, 30, 30, TimeUnit.MINUTES);
    }

    /**
     * 模拟命令行，向系统发送退出指令
     */
    public static void mockSystemIn4ExitSystem() {
        InputStream is = new InputStream() {
            @Override
            public int read() throws IOException {
                return 'q';
            }
        };
        System.setIn(is);
    }

    private void start_net_jobs() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                NetQuality netQuality = NetStatusUtils.getNetworkQuality(server_ip, 5);
                if (netQuality == NetQuality.BAD || netQuality == NetQuality.BREOKEN) {
                    net_allowed_connect = false;
                    //在网络通的情况下，不进行关机处理
                    update_lastest_upload_time();
                } else {
                    net_allowed_connect = true;
                }
                logger.info("网络质量：" + netQuality.getQuality());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }, 5, 5, TimeUnit.SECONDS);

    }

    private void start_acc_jobs() {
        try {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                if (net_allowed_connect) {
                    try {
                        start_monitor_acc();
                    } catch (Exception ex) {
                        update_lastest_upload_time();
                        LoggerUtils.error(this.logger, "处理ACC数据出现一次错误，原因 " + ex.getMessage());
                    }
                }
            }, 2, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * @param
     * @Desc: 计算ACC数据上传的进度条
     * @return: void
     * @Author: qiuzx
     * @Date: 2019-08-30
     * @Copyright 2019-2020 BTT - Powered By 研发中心
     * @version V1.0
     */
    private void calculate_acc_progressbar_job() {

        if (ACC_JOB_START_ID != -1) {
            LoggerUtils.debug(this.logger, "开始统计ACC数据上传进度信息...");

            long totalRecs = this.accTransferService.findTotalJobs("master", ACC_JOB_START_ID);
            long unUploadRecs = this.accTransferService.findUnUpLoadACCRecords("master");

            int upLoadPercent = getUploadPercent(totalRecs, unUploadRecs);
            System.err.print(Shutdown.getPercentProgress("ACC", upLoadPercent));

            LoggerUtils.debug(this.logger, "统计ACC数据上传进度信息...结束");
        }
        if (ACC_JOB_OVER) {
            ACC_JOB_START_ID = -1;
        }

    }

    /**
     * @param
     * @Desc: 计算IRI数据上传的进度条
     * @return: void
     * @Author: qiuzx
     * @Date: 2019-08-30
     * @Copyright 2019-2020 BTT - Powered By 研发中心
     * @version V1.0
     */
    private void calculate_iri_progressbar_job() {
        if (IRI_JOB_START_ID != -1) {
            LoggerUtils.debug(this.logger, "开始统计IRI数据上传进度信息...");

            long totalRecs = this.iriTransferService.findTotalJobs("master", IRI_JOB_START_ID);
            long unUploadRecs = this.iriTransferService.findUnUpLoadIRIRecords("master");

            int upLoadPercent = getUploadPercent(totalRecs, unUploadRecs);
            System.err.print(Shutdown.getPercentProgress("IRI", upLoadPercent));

            LoggerUtils.debug(this.logger, "统计IRI数据上传进度信息...结束");
        }
        if (IRI_JOB_OVER) {
            IRI_JOB_START_ID = -1;
        }
    }

    /**
     * @param
     * @Desc: 计算gps image数据上传的进度条
     * @return: void
     * @Author: qiuzx
     * @Date: 2019-08-30
     * @Copyright 2019-2020 BTT - Powered By 研发中心
     * @version V1.0
     */
    private void calculate_gps_img_progressbar_job() {
        if (GPS_JOB_START_ID != -1) {
            LoggerUtils.debug(this.logger, "开始统计Image数据上传进度信息...");
            long totalRecs = this.gpsTransferService.findTotalImgJobs("master", GPS_IMG_JOB_START_ID);
            long unUploadRecs = this.gpsTransferService.findUnUpLoadGPSImgRecords("master");
            int upLoadPercent = getUploadPercent(totalRecs, unUploadRecs);
            System.err.print(Shutdown.getPercentProgress(Boot.assign_imgs_path_by_manual?"图片路径数据":"图片数据", upLoadPercent));
            LoggerUtils.debug(this.logger, "统计Image数据上传进度信息...结束");
        }
        if (IRI_JOB_OVER) {
            IRI_JOB_START_ID = -1;
        }
    }

    /**
     * @param condition 是否增加条件过滤
     * @Desc: 计算ACC数据上传的进度条
     * @return: void
     * @Author: qiuzx
     * @Date: 2019-08-30
     * @Copyright 2019-2020 BTT - Powered By 研发中心
     * @version V1.0
     */
    private void calculate_gps_progressbar_job(boolean condition) {
        long totalRecs = -1;
        long unUploadRecs = -1;
        if (GPS_JOB_START_ID != -1) {
            LoggerUtils.debug(this.logger, "开始统计GPS数据上传进度信息...");
            if (condition) {
                totalRecs = this.gpsTransferService.findTotalJobsOnCondition("master", GPS_JOB_START_ID);
                unUploadRecs = this.gpsTransferService.findUnUpLoadGPSRecordsOnCondition("master");
            } else {
                totalRecs = this.gpsTransferService.findTotalJobs("master", GPS_JOB_START_ID);
                unUploadRecs = this.gpsTransferService.findUnUpLoadGPSRecords("master");
            }
            int upLoadPercent = getUploadPercent(totalRecs, unUploadRecs);
            System.err.print(Shutdown.getPercentProgress("GPS", upLoadPercent));

            LoggerUtils.debug(this.logger, "统计GPS数据上传进度信息...结束");
        }
        if (GPS_JOB_OVER) {
            GPS_JOB_START_ID = -1;
        }

    }

    /**
     * @param
     * @Desc: 计算PCI数据上传的进度条
     * @return: void
     * @Author: qiuzx
     * @Date: 2019-08-30
     * @Copyright 2019-2020 BTT - Powered By 研发中心
     * @version V1.0
     */
    private void calculate_pci_progressbar_job() {

        if (PCI_JOB_START_ID != -1) {
            LoggerUtils.debug(this.logger, "开始统计PCI数据上传进度信息...");

            long totalRecs = this.pciTransferService.findTotalJobs("master", PCI_JOB_START_ID);
            long unUploadRecs = this.pciTransferService.findUnUpLoadPCIRecords("master");

            int upLoadPercent = getUploadPercent(totalRecs, unUploadRecs);
            System.err.print(Shutdown.getPercentProgress("PCI", upLoadPercent));

            LoggerUtils.debug(this.logger, "统计PCI数据上传进度信息...结束");
        }
        if (PCI_JOB_OVER) {
            PCI_JOB_START_ID = -1;
        }

    }


    /**
     * @param totalRecs
     * @param unUploadRecs
     * @Desc: 获取已经上传的百分比
     * @return: int
     * @Author: qiuzx
     * @Date: 2019-08-30
     * @Copyright 2019-2020 BTT - Powered By 研发中心
     * @version V1.0
     */
    private int getUploadPercent(long totalRecs, long unUploadRecs) {
        if (totalRecs == 0) {
            return 100;
        } else {
            //说明存在数据动态上传中，此时输出99%
            if (unUploadRecs > totalRecs) {
                return 99;
            } else {
                return (int) (((totalRecs - unUploadRecs) * 1.0 / totalRecs) * 100);
            }
        }
    }

    private void start_pci_jobs() {
        if (ext_tables == null || !Arrays.asList(ext_tables).contains("pci")) {
            return;
        }
        try {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                if (net_allowed_connect) {
                    try {
                        start_monitor_pci();
                    } catch (Exception ex) {
                        update_lastest_upload_time();
                        LoggerUtils.warn(this.logger, "pci数据传输过程中出现一次异常..." + ExceptionInfoUtils.getExceptionCauseInfo(ex));
                    }
                }
            }, 2, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void start_iri_jobs() {
        if (ext_tables == null || !Arrays.asList(ext_tables).contains("iri")) {
            return;
        }
        try {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                if (net_allowed_connect) {
                    try {
                        start_monitor_iri();
                    } catch (Exception ex) {
                        update_lastest_upload_time();
                        LoggerUtils.warn(this.logger, "处理iri数据出现一次失败...详情  " + ExceptionInfoUtils.getExceptionCauseInfo(ex));
                    }

                }
            }, 2, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    private void start_gps_img_test_jobs() {
        try {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                if (net_allowed_connect) {
                    try {
                        start_monitor_gps_test_img();
                    } catch (Exception ex) {
                        LoggerUtils.warn(this.logger, "图像传输测试出现一次失败...详情" + ExceptionInfoUtils.getExceptionCauseInfo(ex));
                    }
                }
            }, 3, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void start_gps_record_jobs() {
        try {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                if (net_allowed_connect) {
                    try {
                        start_monitor_gps_records();
                    } catch (Exception ex) {
                        update_lastest_upload_time();
                        LoggerUtils.warn(this.logger, "gps记录传输出现一次失败...详情参照日志" + ExceptionInfoUtils.getExceptionCauseInfo(ex));
                    }

                }
            }, 3, 10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void start_gps_img_path_jobs() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                start_monitor_gps_img_paths();
            } catch (Exception ex) {
                update_lastest_upload_time();
                LoggerUtils.error(this.logger, "gps图像[路径]数据传输出现一次失败...详情参照日志" + ExceptionInfoUtils.getExceptionCauseInfo(ex));
            }

        }, 3, 10, TimeUnit.SECONDS);
    }


    private void start_gps_img_jobs4Even() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (net_allowed_connect) {
                try {
                    if (check_net_status()) {
                        start_monitor_gps_img(true);
                    }
                } catch (Exception ex) {
                    update_lastest_upload_time();
                    LoggerUtils.error(this.logger, "gps图像数据传输出现一次失败...详情参照日志" + ExceptionInfoUtils.getExceptionCauseInfo(ex));
                }

            }
        }, 3, 10, TimeUnit.SECONDS);
    }

    private void start_gps_img_jobs4Odd() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (net_allowed_connect) {
                try {
                    if (check_net_status()) {
                        start_monitor_gps_img(false);
                    }
                } catch (Exception ex) {
                    update_lastest_upload_time();
                    LoggerUtils.error(this.logger, "gps图像数据传输出现一次失败...详情参照日志" + ExceptionInfoUtils.getExceptionCauseInfo(ex));
                }

            }
        }, 3, 10, TimeUnit.SECONDS);
    }


    private boolean check_net_status() {
        try {
            if (!"*".equals(ip_pattern)) {
                String[] iips = inner_ip_pattern.split(",");
                if (NetUtils.is_inner_network_ip(ip_pattern)) {
                    for (String iip : iips) {
                        if (NetUtils.getLocalIp(iip) != null) {
                            LoggerUtils.info(logger, "处于内部网络环境...准许图像上传");
                            return true;
                        }
                    }
                } else {
                    for (String iip : iips) {
                        String local_ip = NetUtils.getLocalIp(iip);
                        if (StringUtils.isNotBlank(local_ip)) {
                            LoggerUtils.info(logger, "处于外部环境和内部环境共存...准许图像上传，内部环境依据[" + local_ip + "]");
                            return true;
                        }
                    }
                    //在外部环境下不准许关机操作
                    update_lastest_upload_time();
                    LoggerUtils.info(logger, "处于外部网络环境为不准许上传图像数据.");
                }
            }
        } catch (Exception ex) {
            LoggerUtils.error(logger, ExceptionInfoUtils.getExceptionCauseInfo(ex));
        }
        LoggerUtils.warn(logger, "当前网络系统禁止了执行图像上传操作");
        return false;
    }

    /**
     * 检查gps中未上传的img信息
     */
    private void start_monitor_gps_test_img() {
        List<File> fileList = new ArrayList<>();
        FileIOUtils.getFileList(fileList, Boot.BASE_DIR + File.separator + "sender" + File.separator);
        //FileIOUtils.getFileList(fileList,"/Users/qiuzx/IdeaProjects/qiuzx/deliverc/imgs/");
        if (fileList.size() > 0) {
            fileList.forEach(file -> {
                upload_file_pool.execute(() -> {
                    client.uploadBigDataByRPC(file.getAbsolutePath());
                });
                //file.delete();
            });
        } else {
            System.err.println("没有需要上传的文件");
        }
    }

    /**
     * 检查gps中未上传的img路径
     */
    private void start_monitor_gps_img_paths() {
        //获取所有为上传的图像记录信息
        List<GPSTransferIniBean> recordDatas = gpsTransferService.findUnUpLoadGPSImgDatas(Boot.DOWNSTREAM_DBNAME, Boot.RECORD_BATCH_SIZE);
        if (recordDatas != null && recordDatas.size() > 0) {
            GPS_IMG_JOB_START_ID = recordDatas.get(0).getId();
        } else {
            GPS_IMG_JOB_OVER = true;
        }
        int current_repeater = 0;
        boolean has_err = false;
        while (recordDatas != null && recordDatas.size() > 0) {
            current_repeater++;
            if (current_repeater > REPEATER) {
                calculate_gps_img_progressbar_job();
            }
            update_lastest_upload_time();
            logger.info("开始上传[gps图像路径]数据...[" + recordDatas.size() + "]条");
            try {
                //上传图像
                client.uploadImgDataPath(recordDatas);
                recordDatas = gpsTransferService.findUnUpLoadGPSImgDatas(Boot.DOWNSTREAM_DBNAME, Boot.RECORD_BATCH_SIZE);
            } catch (Exception ex) {
                has_err = true;
                //上传图像出现任何异常都要终止本次任务，重新进入单步上传，放置图像重复上传
                LoggerUtils.error(logger, ExceptionInfoUtils.getExceptionCauseInfo(ex));
                break;
            }
        }
        GPS_IMG_JOB_OVER = true;
        calculate_gps_img_progressbar_job();

        if (has_err) {
            LoggerUtils.warn(logger, "图像传输出现超时异常，终止此次图像同步任务...");
        } else {
            LoggerUtils.warn(logger, "没有gps图像路径数据需要上传...");
        }
    }

    /**
     * 检查gps中未上传的img信息
     *
     * @param even 上传的是
     */
    private void start_monitor_gps_img(boolean even) {
        //当前执行到第几层
        long invoke_time = 0;
        boolean by_sync_single = true;
        LoggerUtils.info(logger, "检查是否存在要上传的gps图像数据...");
        boolean has_err = false;
        String msg = even ? "偶数记录" : "奇数记录";
        GPS_IMG_JOB_OVER = false;
        //开始批上传的速度控制在2条，如果带宽准许，会逐步提升上传记录记录数。
        int dync_batch_size = 2;
        //List<GPSTransferIniBean> recordDatas = gpsTransferService.findUnUpLoadGPSImgDatas(Boot.DOWNSTREAM_DBNAME, Boot.IMAGE_BATCH_SIZE*2);
        List<GPSTransferIniBean> recordDatas = null;
        try {
            recordDatas = gpsTransferService.findUnUpLoadGPSImgDatas4EvenOrOdd(Boot.DOWNSTREAM_DBNAME, Boot.IMAGE_BATCH_SIZE, even ? 0 : 1);
            if (recordDatas != null && recordDatas.size() > 0) {
                GPS_IMG_JOB_START_ID = recordDatas.get(0).getId();
            } else {
                GPS_IMG_JOB_OVER = true;
            }
        }catch (Exception ex){
            LoggerUtils.warn(this.logger, ExceptionInfoUtils.getExceptionCauseInfo(ex));
            throw new RuntimeException("查询Master机器GPS图像数据失败 " + getExceptionCause(ex));
        }

        invoke_time++;
        UpLoadReson upLoadReson = null;
        long uplaod_speed = -1;
        long consumerMils = -1;
        int current_repeater = 0;
        while (recordDatas != null && recordDatas.size() > 0) {
            update_lastest_upload_time();
            current_repeater++;
            if (current_repeater > REPEATER) {
                calculate_gps_img_progressbar_job();
            }
            if (!check_net_status()) {
                break;
            }
            limiting();
            logger.info("开始上传[" + msg + "]gps图像数据...[" + recordDatas.size() + "]条");
            try {
                //上传图像
                upLoadReson = client.uploadBigDataByRPC(recordDatas, by_sync_single);
            } catch (Exception ex) {
                //上传图像出现任何异常都要终止本次任务，重新进入单步上传，放置图像重复上传
                LoggerUtils.error(logger, ExceptionInfoUtils.getExceptionCauseInfo(ex));
                has_err = true;
                break;
            }

            //对批量上传的图像数据进行速度判断，如果速度达不到就自动降低批量上传的大小
            if (!by_sync_single && upLoadReson != null) {
                if (upLoadReson.getUpdate_records() == 0) {
                    LoggerUtils.warn(logger, "批量同步图像到服务器，由于服务器后台数据验证原因没有入库，终止此次图像同步任务...[等待下次同步单条数据，进行数据修复]");
                    has_err = true;
                    break;
                }
                uplaod_speed = upLoadReson.getSpeed();

                if (uplaod_speed > 0) {
                    //转换为100KB的单位
                    uplaod_speed = uplaod_speed / (1024 * 100);
                    //获取消耗时间
                    consumerMils = upLoadReson.getConsumeMins();
                    StringBuilder sb = new StringBuilder();
                    //没有达到100KB每秒
                    if (uplaod_speed == 0 || consumerMils > TIME_WATER_LINE) {
                        //快速降低批量大小
                        dync_batch_size = dync_batch_size / 2;
                        if (uplaod_speed <= 0) {
                            sb.append("上传速度[" + uplaod_speed + "]KB,小余100KB. ");
                        }
                        if (consumerMils > TIME_WATER_LINE) {
                            sb.append("上传耗时[" + consumerMils + "]ms,大于[" + TIME_WATER_LINE + "]ms.");
                        }
                        LoggerUtils.info(logger, sb.toString() + " 减少单次批量记录为原来的一半");
                    } else {
                        //尝试递增
                        dync_batch_size = dync_batch_size += INC_STEP;
                        LoggerUtils.info(logger, "网络带宽良好,耗时小余" + TIME_WATER_LINE + "ms，增加单次批量记录[" + INC_STEP + "]");
                    }
                    //现在批量最小值
                    if (dync_batch_size < 2) {
                        dync_batch_size = 2;
                    }
                    //限制批量最大值
                    if (dync_batch_size > Boot.IMAGE_BATCH_SIZE) {
                        dync_batch_size = Boot.IMAGE_BATCH_SIZE;
                    }
                }

            }

            recordDatas = gpsTransferService.findUnUpLoadGPSImgDatas4EvenOrOdd(Boot.DOWNSTREAM_DBNAME, dync_batch_size, even ? 0 : 1);
            invoke_time++;
            /*
              前2次采用同步单条方式传输，防止前期没有来得及更新的数据造成了图像重复存储，造成服务器端出现僵尸数据
             */
            if (invoke_time > Boot.PRE_UPLOAD_IMAGE_BY_SYNC_SINGLE_TIMES) {
                by_sync_single = false;
            }
            if (!net_allowed_connect || exit_sys_stauts) {
                break;
            }
        }
        GPS_IMG_JOB_OVER = true;

        calculate_gps_img_progressbar_job();

        if (has_err) {
            LoggerUtils.warn(logger, "图像传输出现超时异常，终止此次图像同步任务...");
        } else {
            LoggerUtils.warn(logger, "没有gps图像数据需要上传...");
        }

    }

    /**
     * 检查gps未上传的记录
     */
    private void start_monitor_gps_test_records() {
        LoggerUtils.info(logger, "检查是否存在要上传的gps记录数据...");
        //查询指定目录下是否存在需要上传的文件

        //开始递归查询

    }

    /**
     * 检查gps未上传的记录
     */
    private void start_monitor_gps_records() {
        LoggerUtils.info(logger, "检查是否存在要上传的gps记录数据...");
        GPS_JOB_OVER = false;
        List<GPSTransferIniBean> recordData = loadGPSRecordDataOnCondition(Boot.RECORD_BATCH_SIZE);
        if (recordData != null && recordData.size() > 0) {
            GPS_JOB_START_ID = recordData.get(0).getId();
        } else {
            GPS_JOB_START_ID = -1;
            GPS_JOB_OVER = true;
        }

        int current_repeater = 0;
        while (recordData != null && recordData.size() > 0) {
            current_repeater++;
            if (current_repeater >= REPEATER) {
                current_repeater = 0;
                calculate_gps_progressbar_job(this.multi_condition_on_gps);
            }

            update_lastest_upload_time();
            limiting();
            try {
                LoggerUtils.info(this.logger, "开始批量上传GPS记录[" + recordData.size() + "]条");
                this.gpsTransferService.upLoadGPSRecordDatas2UpStream("slave", recordData);
                LoggerUtils.info(this.logger, "批量上传GPS记录[" + recordData.size() + "]条，成功！");
            } catch (Exception ex) {
                throw new RuntimeException("上传Master的gps数据出现异常，上传失败" + getExceptionCause(ex));
            }
            try {
                LoggerUtils.info(this.logger, "开始批量更新Master的GPS记录[" + recordData.size() + "]条");
                this.gpsTransferService.updateCurrentUpLoadedSuccessGPSRescords("master", recordData);
                LoggerUtils.info(this.logger, "批量更新Master的GPS记录[" + recordData.size() + "]条，成功！");
            } catch (Exception ex) {
                throw new RuntimeException("更新本地gps数据出现异常，查询失败" + getExceptionCause(ex));
            }
            LoggerUtils.info(this.logger, "检查Master是否存在要上传的gps记录数据...");
            recordData = loadGPSRecordDataOnCondition(Boot.RECORD_BATCH_SIZE);
            if (!this.net_allowed_connect || this.exit_sys_stauts) {
                break;
            }
        }
        GPS_JOB_OVER = true;
        //最后统计一次
        calculate_gps_progressbar_job(this.multi_condition_on_gps);
        LoggerUtils.warn(this.logger, "没有gps记录数据需要上传...");
    }

    private List<GPSTransferIniBean> loadGPSRecordDataOnCondition(int batchSize) {
        if (this.multi_condition_on_gps) {
            try {
                return this.gpsTransferService.findUnUpLoadGPSRecordDatasOnCondition("master", batchSize);
            } catch (Exception ex) {
                throw new RuntimeException("查询Master的gps数据出现异常，查询失败" + getExceptionCause(ex));
            }
        }
        try {
            return this.gpsTransferService.findUnUpLoadGPSRecordDatas("master", batchSize);
        } catch (Exception ex) {
            throw new RuntimeException("查询Master的gps数据出现异常，查询失败" + getExceptionCause(ex));
        }
    }

    /**
     * 检查未上传的acc记录
     */
    private void start_monitor_acc() {
        LoggerUtils.info(this.logger, "检查Master是否存在要上传的ACC记录数据...");
        List<ACCTransferIniBean> recordDatas = null;
        ACC_JOB_OVER = false;
        try {
            recordDatas = this.accTransferService.findUnUpLoadACCRecordDatas("master", Boot.RECORD_BATCH_SIZE);
            if (recordDatas != null && recordDatas.size() > 0) {
                ACC_JOB_START_ID = recordDatas.get(0).getId();
            } else {
                //表示任务结束
                ACC_JOB_START_ID = -1;
                ACC_JOB_OVER = true;
            }
        } catch (Exception ex) {
            LoggerUtils.warn(this.logger, ExceptionInfoUtils.getExceptionCauseInfo(ex));
            throw new RuntimeException("查询Master机器ACC数据失败 " + getExceptionCause(ex));
        }

        int current_repeater = 0;
        while (recordDatas != null && recordDatas.size() > 0) {
            current_repeater++;

            if (current_repeater >= REPEATER) {
                current_repeater = 0;
                //对传输进度进行一次计算
                calculate_acc_progressbar_job();
            }

            update_lastest_upload_time();
            limiting();
            LoggerUtils.info(this.logger, "开始批量上传ACC记录[" + recordDatas.size() + "]条");
            try {
                this.accTransferService.upLoadACCRecordDatas2UpStream("slave", recordDatas);
                LoggerUtils.info(this.logger, "批量上传ACC记录[" + recordDatas.size() + "]条,成功！");
            } catch (Exception ex) {
                throw new RuntimeException("上传ACC数据到Slaver失败 " + getExceptionCause(ex));
            }

            try {
                LoggerUtils.info(this.logger, "开始批量更新Master机器ACC记录[" + recordDatas.size() + "]条");
                this.accTransferService.updateCurrentUpLoadedSuccessACCRescords("master", recordDatas);
                LoggerUtils.info(this.logger, "批量更新Master机器ACC记录[" + recordDatas.size() + "]条，成功");
            } catch (Exception ex) {
                throw new RuntimeException("更新Master机器ACC数据失败 " + getExceptionCause(ex));
            }
            try {
                LoggerUtils.info(this.logger, "检查Master是否存在要上传的ACC记录数据...");
                recordDatas = this.accTransferService.findUnUpLoadACCRecordDatas("master", Boot.RECORD_BATCH_SIZE);
            } catch (Exception ex) {
                throw new RuntimeException("查询Master机器ACC数据失败 " + getExceptionCause(ex));
            }


            if (!this.net_allowed_connect || this.exit_sys_stauts) {
                break;
            }
        }
        ACC_JOB_OVER = true;
        //一轮结束后对传输进度进行一次统计
        calculate_acc_progressbar_job();

        LoggerUtils.warn(this.logger, "没有ACC记录数据需要上传...");
    }

    /**
     * 检查未上传的iri记录
     */
    private void start_monitor_iri() {
        LoggerUtils.debug(this.logger, "检查Master是否存在要上传的iri记录数据...");
        List<IRITransferIniBean> recordDatas = null;
//        try {
//            recordDatas = this.iriTransferService.findUnUpLoadIRIRecordDatas("master", Boot.RECORD_BATCH_SIZE);
//        } catch (Exception ex) {
//            throw new RuntimeException("查询Master的iri数据失败 " + getExceptionCause(ex));
//        }
        IRI_JOB_OVER = false;
        try {
            recordDatas = this.iriTransferService.findUnUpLoadIRIRecordDatas("master", Boot.RECORD_BATCH_SIZE);
            if (recordDatas != null && recordDatas.size() > 0) {
                IRI_JOB_START_ID = recordDatas.get(0).getId();
            } else {
                IRI_JOB_OVER = true;
                IRI_JOB_START_ID = -1;
            }
        } catch (Exception ex) {
            throw new RuntimeException("查询Master的iri数据失败 " + getExceptionCause(ex));
        }
        int current_repeater = 0;
        while (recordDatas != null && recordDatas.size() > 0) {
            limiting();
            current_repeater++;

            if (current_repeater >= REPEATER) {
                current_repeater = 0;
                calculate_iri_progressbar_job();
            }

            update_lastest_upload_time();
            try {
                LoggerUtils.info(this.logger, "开始批量上传iri记录[" + recordDatas.size() + "]条");
                this.iriTransferService.upLoadACCRecordDatas2UpStream("slave", recordDatas);
                LoggerUtils.info(this.logger, "批量上传iri记录[" + recordDatas.size() + "]条，成功！");
            } catch (Exception ex) {
                throw new RuntimeException("上传Master的iri数据失败 " + getExceptionCause(ex));
            }

            try {
                LoggerUtils.info(this.logger, "开始批量更新iri记录[" + recordDatas.size() + "]条");
                this.iriTransferService.updateCurrentUpLoadedSuccessIRIRescords("master", recordDatas);
                LoggerUtils.info(this.logger, "批量更新iri记录[" + recordDatas.size() + "]条，成功");
            } catch (Exception ex) {
                throw new RuntimeException("更新Master的iri数据失败 " + getExceptionCause(ex));
            }
            try {
                LoggerUtils.debug(this.logger, "检查Master是否存在要上传的iri记录数据...");
                recordDatas = this.iriTransferService.findUnUpLoadIRIRecordDatas("master", Boot.RECORD_BATCH_SIZE);
            } catch (Exception ex) {
                throw new RuntimeException("查询Master的iri数据失败 " + getExceptionCause(ex));
            }

            if (!this.net_allowed_connect || this.exit_sys_stauts) {
                break;
            }
        }
        IRI_JOB_OVER = true;
        //任务结束时统计一次
        calculate_iri_progressbar_job();
        LoggerUtils.warn(this.logger, "Master没有iri记录数据需要上传...");
    }

    /**
     * 检查未上传的pci记录
     */
    private void start_monitor_pci() {
        LoggerUtils.info(this.logger, "检查Master是否存在要上传的pci记录数据...");
        List<PCITransferIniBean> recordDatas = null;
        PCI_JOB_OVER = false;
        try {
            recordDatas = this.pciTransferService.findUnUpLoadPCIRecordDatas("master", Boot.RECORD_BATCH_SIZE);
            if (recordDatas != null && recordDatas.size() > 0) {
                PCI_JOB_START_ID = recordDatas.get(0).getId();
            } else {
                PCI_JOB_OVER = true;
            }
        } catch (Exception ex) {
            LoggerUtils.debug(this.logger, ExceptionInfoUtils.getExceptionCauseInfo(ex));
            throw new RuntimeException("查询Master的pci数据失败 " + getExceptionCause(ex));
        }
        int current_repeater = 0;
        while (recordDatas != null && recordDatas.size() > 0) {
            current_repeater++;
            if (current_repeater > REPEATER) {
                current_repeater = 0;
                calculate_pci_progressbar_job();
            }
            limiting();
            update_lastest_upload_time();
            try {
                LoggerUtils.info(this.logger, "开始批量上传pci记录[" + recordDatas.size() + "]条");
                this.pciTransferService.upLoadPCIRecordDatas2UpStream("slave", recordDatas);
                LoggerUtils.info(this.logger, "批量上传pci记录[" + recordDatas.size() + "]条，成功！");
            } catch (Exception ex) {
                LoggerUtils.debug(this.logger, ExceptionInfoUtils.getExceptionCauseInfo(ex));
                throw new RuntimeException("上传Master的pci数据失败 " + getExceptionCause(ex));
            }
            try {
                LoggerUtils.info(this.logger, "开始批量更新Master的pci记录[" + recordDatas.size() + "]条");
                this.pciTransferService.updateCurrentUpLoadedSuccessPCIRescords("master", recordDatas);
                LoggerUtils.info(this.logger, "批量上传pci记录[" + recordDatas.size() + "]条，成功！");
            } catch (Exception ex) {
                LoggerUtils.debug(this.logger, ExceptionInfoUtils.getExceptionCauseInfo(ex));
                throw new RuntimeException("更新Master的pci数据状态失败 " + getExceptionCause(ex));
            }
            try {
                LoggerUtils.info(this.logger, "检查Master是否存在要上传的pci记录数据...");
                recordDatas = this.pciTransferService.findUnUpLoadPCIRecordDatas("master", Boot.RECORD_BATCH_SIZE);
            } catch (Exception ex) {
                LoggerUtils.debug(this.logger, ExceptionInfoUtils.getExceptionCauseInfo(ex));
                throw new RuntimeException("查询Master的pci数据失败 " + getExceptionCause(ex));
            }

            if (!this.net_allowed_connect || this.exit_sys_stauts) {
                break;
            }
        }
        PCI_JOB_OVER = true;
        calculate_pci_progressbar_job();

        LoggerUtils.warn(this.logger, "没有pci记录数据需要上传...");
    }

    private void limiting() {
        if (RATE_LIMITING) {
            LoggerUtils.warn(logger, "限速中.....");
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(5000, 10000));
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * The default thread factory
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "Client-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            t.setDaemon(true);
            return t;
        }
    }

    public static String getExceptionCause(Exception ex) {
        String msg = ExceptionInfoUtils.getExceptionCauseInfo(ex);
        if (StringUtils.isNotBlank(msg) &&
                msg.contains("MySQLNonTransientConnectionException")) {
            return "[数据库连接失败]";
        }
        return msg;
    }

    /**
     * 更新数据最新上传时间
     */
    private static void update_lastest_upload_time() {
        lastest_upload_time = LocalDateTime.now();
    }
}
