package com.znjt.dao.beans;

import lombok.Data;

import java.util.Date;

/**
 * Created by qiuzx on 2019-04-18
 * Company BTT
 * Depart Tech
 */
@Data
public class IRITransferIniBean {
    private long id;
    private String irigpsid;
    private String gpsid;
    private Date collect_time;
    private float start_latitude;
    private float start_longitude;
    private float end_latitude;
    private float end_longitude;
    private float iri_value;
    private float distance;
    private int uploadstatus;
    private float azimuth;
    private String dataid;

}
