package com.znjt.utils;

import com.znjt.exs.FileIOException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by qiuzx on 2019-03-15
 * Company BTT
 * Depart Tech
 */
public class FileIOUtils {
    private static final AtomicInteger LOOPER = new AtomicInteger();
    //100k
    private static int BUFFER_SIZE = 102400;
    private FileIOUtils(){

    }
    /**
     * 从指定位置读取二进制文件
     * @return
     */
    public static byte[] getImgBytesDataFromPath(String path){
        byte[] bytes = null;
        if(path!=null){
            //测试路
            path="/Users/qiuzx/Downloads/zjz.jpeg";
            try(InputStream is = new BufferedInputStream(new FileInputStream(path),BUFFER_SIZE)){
                bytes = new byte[is.available()];
                IOUtils.read(is,bytes);
            }catch (Exception ex){
                throw new FileIOException(ex);
            }
        }
        return bytes;
    }



    /**
     * 将内存二进制数据写入disk
     * @param path
     * @param imgs
     */
    public static void saveBinaryImg2Disk(String path ,byte[] imgs){
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path),BUFFER_SIZE)) {
            IOUtils.write(imgs,bos);
        }catch (Exception ex){
            throw new FileIOException(ex);
        }
    }

    /**
     * 删除文件
     * @param abs_path
     */
    public static void deleteFile(String abs_path){
        File file = new File(abs_path);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * 为图像创建相对路径,相对fs目录
     * @return
     */
    public static String createRelativePath4Image(String imageName,boolean use_org_file_name){
        StringBuilder sb = new StringBuilder(File.separator+"fs"+File.separator);
        sb.append(getRandomFlag());
        sb.append(File.separator);
        sb.append(getRandomFlag());
        sb.append(File.separator).append(imageName);
        if(!use_org_file_name){
            sb.append(getLoopNumFlag());
        }
        sb.append(".jpg");
        return sb.toString();
    }


    public static void main(String[] args) {
//        System.err.println(createRelativePath4Image("01",false));
//        System.err.println(CommonFileUitls.getProjectPath());
//        StringBuilder sb = new StringBuilder("d:");
//        sb.append("\\\\").append("test").append("\\").append("zntj_sh_001").append("\\").append("ZNJT_SH_0112019051617425648.jpg");
//        String path = sb.toString();
//
//        path = path.replaceAll("\\\\","/");
//        System.err.println(getImageFileNameNoExtension(path));

//        String path = "/Users/qiuzx/IdeaProjects/qiuzx/deliverc/target/sender/";
//        List<File> list = new ArrayList<>();
//        getFileList(list,path);
//        list.forEach(file->{
//            System.err.println(file.getAbsolutePath());
//        });

    }

    /**
     * 获取一个2位循环数字
     * @return
     */
    private static String getLoopNumFlag(){
        int res = LOOPER.incrementAndGet();
        if(res==99){
            LOOPER.set(1);
        }
        return String.format("%02x",res);
    }

    /**
     * 生成随机数
     * @return
     */
    private static String getRandomFlag(){
        return String.format("%02x",ThreadLocalRandom.current().nextInt(0,128));
    }

    /**
     * 初始化文件存储目录12**128个
     */
    public static void init_fs_dirs(String base_dir,String relative_dir){
        String dirs;
        File dir = null;
        for(int i=0;i<128;i++){
            for(int j=0;j<128;j++){
                dirs = base_dir+relative_dir+String.format("%02x",i)+"/"+String.format("%02x",j);
                dir = new File(dirs);
                if(!dir.exists()){
                    boolean res = dir.mkdirs();
                    System.err.println("创建系统文件夹["+dirs+"] " + (res?"success":"failure"));
                }
            }
        }
    }

    /**
     * 初始化测试文件夹
     * @param base_dir
     * @param relative_dir
     */
    public static void init_test_dir(String base_dir,String relative_dir){
       String path = base_dir+relative_dir;
       File dir = new File(path);
       if(!dir.exists()){
           boolean res = dir.mkdirs();
           System.err.println("创建[测试]系统文件夹["+path+"] " + (res?"success":"failure"));
       }
    }




    /**
     * 从全路径中获取文件名称，不包含扩展名
     * @param filePath
     * @return
     */
    public static String getImageFileName(String filePath,boolean hasExtension) {
        if (StringUtils.isBlank(filePath)) {
            return filePath;
        }
        //转换为统一的格式
        filePath = filePath.replaceAll("\\\\","/");

        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf("/");
        //不含扩展名
        if(!hasExtension) {
            if (lastSep == -1) {
                return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
            }
            if (lastPoi == -1 || lastSep > lastPoi) {
                return filePath.substring(lastSep + 1);
            }
            return filePath.substring(lastSep + 1, lastPoi);
        }else{
            if (lastSep == -1) {
                return filePath;
            }
            return filePath.substring(lastSep + 1);
        }
    }

    /**
     * 获取指定文件夹下及其子目录下的所有文件信息
     * @param filelist
     * @param filePath
     * @return
     */
    public static List<File> getFileList(List<File> filelist,String filePath) {
        File dir = new File(filePath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        File file = null;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                file = files[i];
                if (file.isDirectory()) { // 判断是文件还是文件夹
                    getFileList(filelist,file.getAbsolutePath()); // 获取文件绝对路径
                } else {
                    filelist.add(file);
                }
            }
        }
        return filelist;
    }
}
