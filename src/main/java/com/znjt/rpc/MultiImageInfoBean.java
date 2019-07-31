package com.znjt.rpc;

import java.util.List;

/**
 * Created by qiuzx on 2019-05-05
 * Company BTT
 * Depart Tech
 *
 * @author qiuzx
 */
public class MultiImageInfoBean {
    private List<byte[]> imgs = null;
    private List<String> fileNames = null;

    public List<byte[]> getImgs() {
        return imgs;
    }

    public void setImgs(List<byte[]> imgs) {
        this.imgs = imgs;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }
}
