package com.znjt;

import com.znjt.dao.beans.PCITransferIniBean;
import com.znjt.service.PCITransferService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-04-18
 * Company BTT
 * Depart Tech
 */
public class PCITransferTest {
    private PCITransferService pciTransferService;
    @Before
    public void init(){
        pciTransferService = new PCITransferService();
    }

    @Test
    public void test01(){
        List<PCITransferIniBean> pciTransferIniBeans = pciTransferService.findUnUpLoadPCIRecordDatas("master",10);
        Optional.ofNullable(pciTransferIniBeans).ifPresent(beans->{
            pciTransferService.upLoadPCIRecordDatas2UpStream("slave",beans);
            pciTransferService.updateCurrentUpLoadedSuccessPCIRescords("master",beans);
        });
    }
}
