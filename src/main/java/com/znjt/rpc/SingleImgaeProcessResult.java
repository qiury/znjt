package com.znjt.rpc;

/**
 * 单张图像处理结果
 * Created by qiuzx on 2019-03-23
 * Company BTT
 * Depart Tech
 */
public class SingleImgaeProcessResult {
    private boolean img_err = false;
    private boolean persistent = false;
    //绝对路径
    private String filePath = null;
    //相对路径
    private String relPath = null;

    public SingleImgaeProcessResult(boolean img_err, boolean persistent, String filePath, String relPath) {
        this.img_err = img_err;
        this.persistent = persistent;
        this.filePath = filePath;
        this.relPath = relPath;
    }

    public boolean isImg_err() {
        return img_err;
    }

    public void setImg_err(boolean img_err) {
        this.img_err = img_err;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRelPath() {
        return relPath;
    }

    public void setRelPath(String relPath) {
        this.relPath = relPath;
    }
}
