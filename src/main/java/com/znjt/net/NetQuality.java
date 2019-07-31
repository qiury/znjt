package com.znjt.net;

/**
 * 定义网络质量
 * Created by qiuzx on 2019-03-11
 * Company BTT
 * Depart Tech
 */
public enum NetQuality {

    GOOD(100),NORMAL(70),BAD(1), BREOKEN(0);

    private int quality;
    NetQuality(int quality){
        this.quality = quality;
    }

    public int getQuality(){
        return quality;
    }

}
