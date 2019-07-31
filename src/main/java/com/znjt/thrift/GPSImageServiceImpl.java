package com.znjt.thrift;

import com.znjt.service.GPSTransferService;
import org.apache.thrift.TException;

import java.util.List;

/**
 * Created by qiuzx on 2019-03-27
 * Company BTT
 * Depart Tech
 */
public class GPSImageServiceImpl implements GPSImgService.Iface {
    private GPSTransferService gpsTransferService = new GPSTransferService();
    @Override
    public ThriftMessageResponse transferGPSImgs(ThriftMessageRequest request) throws TException {
        List<GPSImgRecord> messages = request.getMessages();
        messages = ImageProcessor4Thrift.processGPSImageRecords(gpsTransferService,messages);
        ThriftMessageResponse response = new ThriftMessageResponse();
        response.setMessages(messages);
        return response;
    }

    @Override
    public String ping() throws TException {
        return "pong";
    }
}
