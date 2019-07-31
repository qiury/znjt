package com.znjt;

import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.rpc.TransportClient;
import com.znjt.rpc.TransporterServer;
import com.znjt.service.GPSTransferService;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-03-15
 * Company BTT
 * Depart Tech
 */
public class GPSTransferTest {
    GPSTransferService gpsTransferService;
    @Before
    public void init(){
        gpsTransferService = new GPSTransferService();
    }
    /**
     * 获取未上传数据记录的列表
     */
    @Test
    public void testFindUnUpLoadGPSRecordDatas() {
        List<GPSTransferIniBean> recordDatas = gpsTransferService.findUnUpLoadGPSRecordDatas("db01", 10);
        Optional.ofNullable(recordDatas).ifPresent(x->{

        });

    }

    /**
     * 获取已经上传记录的列表但是未上传图像的列表
     */
    @Test
    public void testFindUnUpLoadGPSImgDatas(){
        List<GPSTransferIniBean> recordDatas = gpsTransferService.findUnUpLoadGPSImgDatas("db01", 10);
        Optional.ofNullable(recordDatas).ifPresent(x->{
            x.forEach(System.out::println);
        });
    }


    /**
     * 测试上传数据记录到上游
     */
    @Test
    public void testUpLoadGPSRecordDatas2UpStream(){
        List<GPSTransferIniBean> recordDatas = gpsTransferService.findUnUpLoadGPSRecordDatas("db01", 10);
        Optional.ofNullable(recordDatas).ifPresent(rds->{
            gpsTransferService.upLoadGPSRecordDatas2UpStream("db02",rds);
        });
    }

    /**
     * 测试插入到自身
     */
    @Test
    public void testUpLoadGPSRecordDatas2UpStream02(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        final String key = now.format(formatter);
        List<GPSTransferIniBean> recordDatas = gpsTransferService.findUnUpLoadGPSRecordDatas("db01", 5000);

        Optional.ofNullable(recordDatas).ifPresent(rds->{
            int i = 0;
            for(GPSTransferIniBean bean:rds){
                bean.setGpsid(key+""+ String.format("%03d",i++));
            }
            gpsTransferService.upLoadGPSRecordDatas2UpStream("db01",rds);
        });
    }

    @Test
    public void testUpdateCurrentUpLoadedSuccessGPSRescords(){
       recursionInvoke(1,1);
    }

    /**
     * 递归批量同步数据1
     * @param index
     * @param max
     */
    private void recursionInvoke(int index,int max){

        System.err.println( "the " + index+ " times invoke  ... ");
        List<GPSTransferIniBean> recordDatas = gpsTransferService.findUnUpLoadGPSRecordDatas("db01", 10);
        Optional.ofNullable(recordDatas).ifPresent(rds->{
            if(rds.size()>0){
                gpsTransferService.upLoadGPSRecordDatas2UpStream("db02",rds);
                gpsTransferService.updateCurrentUpLoadedSuccessGPSRescords("db01",recordDatas);
            }
        });
        if(recordDatas==null||recordDatas.size()==0||index>=1){
            return;
        }
        recursionInvoke(++index,max);
    }

    @Test
    public void testUpdateAllGPSRescords(){
        Instant instant = Instant.now();

        List<GPSTransferIniBean> recordDatas = gpsTransferService.findUnUpLoadGPSRecordDatas("db02", 200);

        while(recordDatas!=null&&recordDatas.size()>0){
            gpsTransferService.upLoadGPSRecordDatas2UpStream("db01",recordDatas);
            gpsTransferService.updateCurrentUpLoadedSuccessGPSRescords("db02",recordDatas);
            recordDatas = gpsTransferService.findUnUpLoadGPSRecordDatas("db02",200);
        }
        Duration duration = Duration.between(instant,Instant.now());
        System.err.println("总计耗时: "+duration.toMillis() + " 毫米");
    }

    @Test
    public void testUpLoadImgDatas(){
        File file = new File("imgs/ai.png");
        startServerAndUploadImgs(file.getAbsolutePath());
    }


    private void startServerAndUploadImgs(String filePath){
        Thread server = new Thread(new Runnable() {
            @Override
            public void run() {
                TransporterServer servers = new TransporterServer();
                servers.startServer(9898);
            }
        });
        server.start();

        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //client.
                List<GPSTransferIniBean> recordDatas = gpsTransferService.findUnUpLoadGPSImgDatas("db01", 10);
                TransportClient client = new TransportClient(gpsTransferService,"127.0.0.1",9898,20);
                Optional.ofNullable(recordDatas).ifPresent(item->{
                    item.forEach(x->{
                        x.setOriginalUrl(filePath);
                        x.setDataid(Instant.now().toString());
                    });
                    client.uploadBigDataByRPC(item,false);
                });
            }
        });
        clientThread.start();

        System.err.println("main thread is here.....");

        //等待server和client结束
        try {
            server.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            clientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
