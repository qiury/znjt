package com.znjt.dao.beans;

import lombok.Data;

import java.util.Date;

/**
 * Created by qiuzx on 2019-04-18
 * Company BTT
 * Depart Tech
 */
@Data
public class PCITransferIniBean {
    private long id;
    private String pcigpsid;
    private String gpsid;
    private Date collect_time;
    private float start_latitude;
    private float start_longitude;
    private float end_latitude;
    private float end_longitude;
    private float pci_value;
    private float distance;
    private int status;
    private String selecct_url;
    private int uploadstatus;
    private float azimuth;
    private int isupload;
    private String crack_num;
    private String patchcrack_num;
    private String pothole_num;
    private String patchpothole_num;
    private String net_num;
    private String patchnet_num;
    private String dataId;
}
