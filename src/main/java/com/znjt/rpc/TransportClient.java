package com.znjt.rpc;

import com.znjt.dao.beans.GPSTransferIniBean;
import com.znjt.service.GPSTransferService;
import com.znjt.thrift.GPSImgClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-03-15
 * Company BTT
 * Depart Tech
 */
public class TransportClient {
    private TransporterClientProxy transporterClientProxy;
    //private GPSImgClient gpsImgClient;
    private Logger logger = LoggerFactory.getLogger(TransportClient.class);
    public TransportClient(GPSTransferService localTransferService, String addr, int port, int max_batch_size) {
        transporterClientProxy = new TransporterClientProxy(localTransferService, addr, port, max_batch_size);
        //gpsImgClient = new GPSImgClient(localTransferService,addr,8899);
        createShutdownHook();
    }

    public void uploadBigDataByRPC(String filePath) {
        transporterClientProxy.transferData2ServerBySync(filePath);
    }


    /**
     * 是否通过流的方式上传图像（优点，具有数据去重功能。缺点，效率比批处理低）
     * @param gpsTransferIniBeans

     */
    public void uploadImgDataPath(List<GPSTransferIniBean> gpsTransferIniBeans) {
        if (gpsTransferIniBeans != null && gpsTransferIniBeans.size() > 0) {
            gpsTransferIniBeans.forEach(item -> {
                if (item.getDataid() == null) {
                    item.setDataid(item.getStatus() + "&" + item.getGpsid());
                }
                //可能存在多个路径通过;分割
                String paths = item.getOriginalUrl().replaceAll("(\r\n|\r|\n|\n\r)", "");
                String[] pts = paths.split(";");
                StringBuilder sb = new StringBuilder();
                int index = 0;
                String basePath = null;
                for (String sub:pts){
                    if(StringUtils.isNotBlank(sub)){
                        if(index==0){
                            //补充地址信息
                            basePath = sub.substring(0, 2);
                            item.setBaseDir(basePath);
                        }
                        index++;
                        sb.append(sub.substring(2)).append(";");
                    }
                }
                item.setOriginalUrl(sb.toString());
            });
            if(logger.isInfoEnabled()) {
                logger.info("同步 {批量} 方式上传Image路径数据");
            }
            //通过同步批处理方式发送数据
            transporterClientProxy.transferImagePathData(gpsTransferIniBeans);
        }
    }


    /**
     * 是否通过流的方式上传图像（优点，具有数据去重功能。缺点，效率比批处理低）
     * @param gpsTransferIniBeans
     * @param by_sync_single 同步单条的方式上传数据
     */
    public UpLoadReson uploadBigDataByRPC(List<GPSTransferIniBean> gpsTransferIniBeans,boolean by_sync_single) {
        if (gpsTransferIniBeans != null && gpsTransferIniBeans.size() > 0) {
            gpsTransferIniBeans.forEach(item -> {
                if (item.getDataid() == null) {
                    item.setDataid(item.getStatus() + "&" + item.getGpsid());
                }
            });
            if(by_sync_single){
                if(logger.isInfoEnabled()){
                    logger.info("同步 {单条} 方式上传数据");
                }
                //通过同步单条的方式处理
                transporterClientProxy.transferData2ServerBySync(gpsTransferIniBeans);
            }else {
                if(logger.isInfoEnabled()) {
                    logger.info("同步 {批量} 方式上传数据");
                }
                //通过同步批处理方式发送数据
                return transporterClientProxy.transferData2ServerBySync4Batch(gpsTransferIniBeans);
                //transporterClientProxy.transferData2ServerBySyncSingleRecord4Batch(gpsTransferIniBeans);
                //gpsImgClient.uploadImgRecords(gpsTransferIniBeans);
            }

        }
        return null;
    }



    /**
     * 注册jvm关闭时释放线程资源的回调
     */
    private void createShutdownHook() {
        //程序退出时关闭资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            release();
        }));
    }

    /**
     * 关闭线程资源
     */
    public void release() {
        Optional.ofNullable(transporterClientProxy).ifPresent(proxy -> {
            proxy.release();
        });
    }
}
