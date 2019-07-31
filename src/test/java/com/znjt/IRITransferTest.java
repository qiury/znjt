package com.znjt;

import com.znjt.dao.beans.IRITransferIniBean;
import com.znjt.service.IRITransferService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-04-18
 * Company BTT
 * Depart Tech
 */
public class IRITransferTest {
    private IRITransferService iriTransferService;
    @Before
    public void init(){
        iriTransferService = new IRITransferService();
    }

    @Test
    public void test01(){
        List<IRITransferIniBean> iriTransferIniBeans = iriTransferService.findUnUpLoadIRIRecordDatas("master",10);
        Optional.ofNullable(iriTransferIniBeans).ifPresent(beans->{
            iriTransferService.upLoadACCRecordDatas2UpStream("slave",beans);
            iriTransferService.updateCurrentUpLoadedSuccessIRIRescords("master",beans);
        });
    }
}
