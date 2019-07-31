package com.znjt;

import com.znjt.dao.beans.ACCTransferIniBean;
import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.rpc.TransportClient;
import com.znjt.rpc.TransporterServer;
import com.znjt.service.ACCTransferService;
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
public class ACCTransferTest {
    ACCTransferService accTransferService;
    @Before
    public void init(){
        accTransferService = new ACCTransferService();
    }
    /**
     * 获取未上传数据记录的列表
     */
    @Test
    public void testFindUnUpLoadACCRecordDatas() {
        List<ACCTransferIniBean> recordDatas = accTransferService.findUnUpLoadACCRecordDatas("db01", 10);
        Optional.ofNullable(recordDatas).ifPresent(x->{
            x.forEach(System.out::println);
        });

    }



    /**
     * 测试上传数据记录到上游
     */
    @Test
    public void testUpLoadACCRecordDatas2UpStream(){
        List<ACCTransferIniBean> recordDatas = accTransferService.findUnUpLoadACCRecordDatas("db01", 5);
        Optional.ofNullable(recordDatas).ifPresent(rds->{
            rds.forEach(item->{
                item.setAccid(item.getStatus()+"_"+item.getAccid()+"_"+item.getId());
            });
            accTransferService.upLoadACCRecordDatas2UpStream("db02",rds);
        });
    }


    @Test
    public void testUpdateAllGPSRescords(){
        Instant instant = Instant.now();

        List<ACCTransferIniBean> recordDatas = accTransferService.findUnUpLoadACCRecordDatas("db01", 2);

        while(recordDatas!=null&&recordDatas.size()>0){
            recordDatas.forEach(item->{
                item.setAccid(item.getStatus()+"_"+item.getAccid()+"_"+item.getId());
            });
            accTransferService.upLoadACCRecordDatas2UpStream("db02",recordDatas);
            accTransferService.updateCurrentUpLoadedSuccessACCRescords("db01",recordDatas);
            recordDatas = accTransferService.findUnUpLoadACCRecordDatas("db01",2);
        }
        Duration duration = Duration.between(instant,Instant.now());
        System.err.println("总计耗时: "+duration.toMillis() + " 毫米");
    }
}