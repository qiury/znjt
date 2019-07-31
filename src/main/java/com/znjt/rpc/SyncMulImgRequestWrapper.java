package com.znjt.rpc;

import com.znjt.proto.SyncMulImgRequest;

/**
 * Created by qiuzx on 2019-04-03
 * Company BTT
 * Depart Tech
 */
public class SyncMulImgRequestWrapper {
    private SyncMulImgRequest syncMulImgRequest;
    private long total_bys = -1;

    public SyncMulImgRequest getSyncMulImgRequest() {
        return syncMulImgRequest;
    }

    public void setSyncMulImgRequest(SyncMulImgRequest syncMulImgRequest) {
        this.syncMulImgRequest = syncMulImgRequest;
    }

    public long getTotal_bys() {
        return total_bys;
    }

    public void setTotal_bys(long total_bys) {
        this.total_bys = total_bys;
    }
}
