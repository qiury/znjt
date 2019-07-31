package com.znjt.dao.beans;

import java.util.Date;

/**
 * Created by qiuzx on 2019-03-12
 * Company BTT
 * Depart Tech
 * create table ini (int id )
 * @author qiuzx
 */
public class ACCTransferIniBean {
    private long id;
    private String accid;
    private String status;
    private String date;
    private String time;
    private String acc1x;
    private String acc1y;
    private String acc1z;
    private String acc2x;
    private String acc2y;
    private String acc2z;
    /*
      0表示未上传或者未上传成功
     */

    private int uploadstatus;

    public String getAccid() {
        return accid;
    }

    public void setAccid(String accid) {
        this.accid = accid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAcc1x() {
        return acc1x;
    }

    public void setAcc1x(String acc1x) {
        this.acc1x = acc1x;
    }

    public String getAcc1y() {
        return acc1y;
    }

    public void setAcc1y(String acc1y) {
        this.acc1y = acc1y;
    }

    public String getAcc1z() {
        return acc1z;
    }

    public void setAcc1z(String acc1z) {
        this.acc1z = acc1z;
    }

    public String getAcc2x() {
        return acc2x;
    }

    public void setAcc2x(String acc2x) {
        this.acc2x = acc2x;
    }

    public String getAcc2y() {
        return acc2y;
    }

    public void setAcc2y(String acc2y) {
        this.acc2y = acc2y;
    }

    public String getAcc2z() {
        return acc2z;
    }

    public void setAcc2z(String acc2z) {
        this.acc2z = acc2z;
    }

    public int getUploadstatus() {
        return uploadstatus;
    }

    public void setUploadstatus(int uploadstatus) {
        this.uploadstatus = uploadstatus;
    }


    @Override
    public String toString() {
        return "ACCTransferIniBean{" +
                "id=" + id +
                ", accid='" + accid + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", acc1x='" + acc1x + '\'' +
                ", acc1y='" + acc1y + '\'' +
                ", acc1z='" + acc1z + '\'' +
                ", acc2x='" + acc2x + '\'' +
                ", acc2y='" + acc2y + '\'' +
                ", acc2z='" + acc2z + '\'' +
                ", uploadstatus=" + uploadstatus +
                '}';
    }
}
