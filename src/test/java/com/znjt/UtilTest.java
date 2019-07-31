package com.znjt;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by qiuzx on 2019-03-26
 * Company BTT
 * Depart Tech
 */
public class UtilTest {
    @Test
    public void test01(){
        String paths = "D:/es/IR200CM/2019/03/05/11/16/IR200CM2019030511163924.jpg;\r\nD:/es/IR200CM/2019/03/05/11/16/IR200CM2019030511163976.jpg";
        paths = paths.replaceAll("(\r\n|\r|\n|\n\r)", "");
        System.err.println(paths);
    }

    @Test
    public void test03() throws Exception{
        //Thumbnails.of("/Users/qiuzx/IdeaProjects/qiuzx/deliverc/imgs/ai.png").scale(1f).outputQuality(0.25f).toFile("/Users/qiuzx/IdeaProjects/qiuzx/deliverc/imgs/ai01.png");
    }

    @Test
    public void test04(){
        int i = 1;
        i = i++;
        int k = i+++i*i++;
        System.err.println(k);
        System.err.println(i);
    }
}
