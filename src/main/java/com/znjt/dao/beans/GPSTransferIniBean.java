package com.znjt.dao.beans;

import java.util.Date;
import java.util.List;

/**
 * Created by qiuzx on 2019-03-12
 * Company BTT
 * Depart Tech
 * create table ini (int id )
 * @author qiuzx
 */
public class GPSTransferIniBean {
    private long id;
    private String gpsid;
    private Date collect_time;
    private String status;
    private float latitude;
    private float longitude;
    private float speed;
    private float azimuth;
    private float declination;
    //图像原始路径
    private String originalUrl;
    private Date create_time;
    private Date upload_time;
    private boolean img_uploaded;
    private String result;
    private String area;
    private int uploadstatus;
    private String dataid;
    //表示当前关联的文件是否存在问题（丢失）
    private boolean file_err = false;
    //根目录
    private String baseDir;

    private String clientRecordId;
    private String file_full_path;
    private int totalLostedSize = 0;

    //文件名称
    private List<String> fileNames;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGpsid() {
        return gpsid;
    }

    public void setGpsid(String gpsid) {
        this.gpsid = gpsid;
    }

    public Date getCollect_time() {
        return collect_time;
    }

    public void setCollect_time(Date collect_time) {
        this.collect_time = collect_time;
    }

    public String getStatus() {
        return status;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public float getDeclination() {
        return declination;
    }

    public void setDeclination(float declination) {
        this.declination = declination;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(Date upload_time) {
        this.upload_time = upload_time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getUploadstatus() {
        return uploadstatus;
    }

    public void setUploadstatus(int uploadstatus) {
        this.uploadstatus = uploadstatus;
    }

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public boolean isFile_err() {
        return file_err;
    }

    public boolean isImg_uploaded() {
        return img_uploaded;
    }

    public void setImg_uploaded(boolean img_uploaded) {
        this.img_uploaded = img_uploaded;
    }

    public void setFile_err(boolean file_err) {
        this.file_err = file_err;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getClientRecordId() {
        return clientRecordId;
    }

    public void setClientRecordId(String clientRecordId) {
        this.clientRecordId = clientRecordId;
    }

    public String getFile_full_path() {
        return file_full_path;
    }

    public void setFile_full_path(String file_full_path) {
        this.file_full_path = file_full_path;
    }

    public int getTotalLostedSize() {
        return totalLostedSize;
    }

    public void setTotalLostedSize(int totalLostedSize) {
        this.totalLostedSize = totalLostedSize;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    @Override
    public String toString() {
        return "GPSTransferIniBean{" +
                "gpsid='" + gpsid + '\'' +
                ", collect_time=" + collect_time +
                ", status='" + status + '\'' +
                ", latitude=" + latitude +
                ", longitdue=" + longitude +
                ", speed=" + speed +
                ", azimuth=" + azimuth +
                ", declination=" + declination +
                ", originalUrl='" + originalUrl + '\'' +
                ", create_time=" + create_time +
                ", upload_time=" + upload_time +
                ", result='" + result + '\'' +
                ", area='" + area + '\'' +
                ", uploadstatus=" + uploadstatus +
                ", dataid='" + dataid + '\'' +
                '}';
    }
}
