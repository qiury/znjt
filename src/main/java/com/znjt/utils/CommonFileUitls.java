package com.znjt.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by qiuzx on 2019-03-19
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public class CommonFileUitls {
    /**
     * 获取项目跟路径(jar文件所在的目录）
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getProjectPath()  {
        URL url = CommonFileUitls.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    /**
     * 获取config真实路径
     * @return
     */
    public static String getRealPath() {
        String realPath = CommonFileUitls.class.getClassLoader().getResource("").getFile();
        File file = new File(realPath);
        realPath = file.getAbsolutePath();
        try {
            realPath = URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realPath;
    }

}
