package com.znjt.rpc;

/**
 * Created by qiuzx on 2019-05-05
 * Company BTT
 * Depart Tech
 *
 * @author qiuzx
 */
public class ImageInfoBean {
    private String fileName;
    private byte[] img;

    public ImageInfoBean() {
    }

    public ImageInfoBean(String fileName, byte[] img) {
        this.fileName = fileName;
        this.img = img;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }
}
