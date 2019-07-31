package com.znjt.rpc;

import com.znjt.proto.GPSRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuzx on 2019-03-20
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public class ImageProcessorResult {
    private GPSRecord gpsRecord;
    private boolean ops_res;
    private int losted_size = 0;
    private String absPath = TransferProtoImpl4Server.BASE_DIR;
    private List<SingleImgaeProcessResult> prs = new ArrayList<>();

    public ImageProcessorResult(){}
    public ImageProcessorResult(GPSRecord gpsRecord) {
        this.gpsRecord = gpsRecord;
    }

    public GPSRecord getGpsRecord() {
        return gpsRecord;
    }

    public void setGpsRecord(GPSRecord gpsRecord) {
        this.gpsRecord = gpsRecord;
    }

    public int getLosted_size() {
        return losted_size;
    }

    public void setLosted_size(int losted_size) {
        this.losted_size = losted_size;
    }

    public boolean isOps_res() {
        return ops_res;
    }

    public void setOps_res(boolean ops_res) {
        this.ops_res = ops_res;
    }

    public List<SingleImgaeProcessResult> getPrs() {
        return prs;
    }

    public void setPrs(List<SingleImgaeProcessResult> prs) {
        this.prs = prs;
    }

    public void addProcessResult(SingleImgaeProcessResult pr){
        this.prs.add(pr);
    }

    public String getAbsPath() {
        return absPath;
    }

    public void setAbsPath(String absPath) {
        this.absPath = absPath;
    }
}
