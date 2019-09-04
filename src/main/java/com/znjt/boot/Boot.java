package com.znjt.boot;

import com.znjt.exs.ExceptionInfoUtils;
import com.znjt.utils.CommonFileUitls;
import com.znjt.utils.FileIOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.io.Resources;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.*;

/**
 * Created by qiuzx on 2019-03-19
 * Company BTT
 * Depart Tech
 */
public class Boot {
    //目标数据源名称
    public static final String UPSTREAM_DBNAME = "slave";
    //源数据源名称
    public static final String DOWNSTREAM_DBNAME = "master";
    //普通记录批处理大小
    public static int RECORD_BATCH_SIZE = 500;
    //图像记录批处理大小
    public static int IMAGE_BATCH_SIZE = 50;
    //数据帧准许的最大值
    public static int FRAME_MAX_SIXE = 40*1024*1024;
    //每次开始上传图像任务时，前面几次采用Stream方式上传数据
    public static int PRE_UPLOAD_IMAGE_BY_SYNC_SINGLE_TIMES = 4;
    private static final int try_use_days = 100;
    private static boolean is_server = false;
    private static boolean allow_upload_img = true;
    private static String ip_blacklist = "";
    private static String inner_ip_pattern = "";
    private static ClientBoot clientBoot;
    private static String role = null;
    private static String MASTER = "master";
    private static String SLAVE = "slave";
    private static String[] EXTEND_TABLES = null;
    private static boolean test_upload_speed = false;
    public static boolean save_test_file_to_disk = false;

    //人工指定图像路径
    public static boolean assign_imgs_path_by_manual = false;

    public static final String BASE_DIR = CommonFileUitls.getProjectPath();

    private static ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue(1));
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    /**
     * 系统启动
     * @param args
     */
    public static void main(String[] args) throws Exception {
        try {
            FileIOUtils.init_test_dir(BASE_DIR, File.separator + "sender" + File.separator);

            Properties properties = read_sys_cfg();
            initAndStart(properties);
        } catch (Exception ex) {
            System.err.println(ExceptionInfoUtils.getExceptionCauseInfo(ex));
            Runtime.getRuntime().halt(-1);
        }
    }
    private static void initAndStart(Properties properties) {
        init(properties);
        String role = properties.getProperty("role");
        if (StringUtils.isNotBlank(role)) {
            role = role.trim();
            if ("master".equals(role) || "slave".equals(role)) {
                autoStartFromCfg(properties, role);
            } else {
                System.err.println("sys.properties配置文件中role参数值不正确，请使用master或者slave");
            }
        } else {
            read_opt_from_terminal(properties);
        }
    }
    private static void autoStartFromCfg(Properties properties, String role) {
        String appName = "[Server]";
        boolean started = false;
        if (MASTER.equals(role)) {
            appName = "[Client]";
            is_server = false;
            String up_stream_ip = properties.getProperty("upstrem.ip");
            String up_stream_port = properties.getProperty("upstrem.port");
            if (validUpStreamIp(up_stream_ip) && validPortParam(up_stream_port)) {
                startClient(up_stream_ip.trim(), Integer.parseInt(up_stream_port.trim()));
                started = true;
            }
        } else if (SLAVE.equals(role)) {
            is_server = true;
            String up_stream_port = properties.getProperty("upstrem.port");
            if (validPortParam(up_stream_port)) {
                startSer(Integer.parseInt(up_stream_port.trim()));
                started = true;
            }
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException interruptedException) {}

        if (started) {
            ctrl_life_from_terminal(appName);
        }
    }

    private static boolean validUpStreamIp(String up_stream_ip){
        boolean valid = true;
        if(StringUtils.isBlank(up_stream_ip)){
            System.err.println("sys.properties配置文件中参数upstrem.ip值不能为空");
            valid = false;
        }
        if(!orIp(up_stream_ip)){
            valid =false;
            System.err.println("sys.properties配置文件中参数upstrem.ip值["+up_stream_ip+"]不是合法ip地址");
        }
        return valid;
    }
    private static boolean validPortParam(String up_stream_port){
        boolean valid = true;
        if(StringUtils.isBlank(up_stream_port)){
            System.err.println("sys.properties配置文件中参数upstrem.port值不能为空");
            valid = false;
        }
        if(!isNumeric(up_stream_port)){
            System.err.println("sys.properties配置文件中参数upstrem.port值["+up_stream_port+"]不是正整数");
            valid = false;
        }
        return valid;
    }

    /**
     * 覆盖默认参数
     * @param properties
     */
    private static void init(Properties properties){
        Optional.ofNullable(properties).ifPresent(prop->{
            String param = prop.getProperty("record_batch_size");

            if(StringUtils.isNotEmpty(param)){
                param = param.trim();
                if(isNumeric(param)){
                    RECORD_BATCH_SIZE = Integer.parseInt(param);
                }
            }
            param = prop.getProperty("image_batch_size");
            if(StringUtils.isNoneEmpty(param)){
                param = param.trim();
                if(isNumeric(param)){
                    IMAGE_BATCH_SIZE = Integer.parseInt(param);
                }
            }
            param = prop.getProperty("frame_max_size");
            if(StringUtils.isNotBlank(param)){
                param = param.trim();
                if(isNumeric(param)){
                    FRAME_MAX_SIXE = Integer.parseInt(param)*1024*1024;
                }
            }
            param = prop.getProperty("allow_upload_img");
            if(StringUtils.isNotBlank(param)){
                allow_upload_img = Boolean.parseBoolean(param);
            }
            param = prop.getProperty("ip_balck_list");
            if(StringUtils.isNotBlank(param)){
                ip_blacklist = param.trim();
            }
            if(!allow_upload_img){
                ip_blacklist = "*";
            }

            param = prop.getProperty("inner_ip_pattern");
            if(StringUtils.isNotBlank(param)){
                inner_ip_pattern = param.trim();
            }
            role = prop.getProperty("role");

            param = prop.getProperty("extend_tables");
            if(StringUtils.isNotBlank(param)){
                EXTEND_TABLES = param.trim().split(",");
            }

            param = prop.getProperty("test_upload_speed");
            if(StringUtils.isNotBlank(param)){
                test_upload_speed = Boolean.parseBoolean(param.trim());
            }

            param = prop.getProperty("save_test_file_to_disk");
            if(StringUtils.isNotBlank(param)){
                save_test_file_to_disk = Boolean.parseBoolean(param.trim());
            }
            param = prop.getProperty("wait_for_check_has_datas");
            if (StringUtils.isNotBlank(param)) {
                ClientBoot.wait_for_check_has_datas = Integer.parseInt(param.trim());
            }
            param = prop.getProperty("auto_off_os");
            if (StringUtils.isNotBlank(param)) {
                ClientBoot.auto_off_os = Boolean.parseBoolean(param.trim());
            }

            param = prop.getProperty("assign_imgs_path_by_manual");
            if(StringUtils.isNoneBlank(param)){
                assign_imgs_path_by_manual = Boolean.parseBoolean(param);
            }
        });
    }

    private static void ctrl_life_from_terminal(String appName) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(System.in);
            System.err.println(appName + " Starting ...... \r\n输入exit(e)/quit(q)/close(c)退出程序 ");
            List<String> exit_cmds = Arrays.asList(new String[] { "e", "c", "q", "quit", "close", "exit" });
            while (scanner.hasNextLine()) {
                String cmd = scanner.nextLine();
                if (exit_cmds.contains(cmd)) {
                    break;
                }

                if ("m".equals(cmd) &&
                        clientBoot != null) {
                    clientBoot.cancel_shoudown_jobs();
                }

                System.err.println("输入exit(e)/quit(q)/close(c)退出程序 ");
            }

            System.err.println("系统即将退出，启动释放资源的操作..");
            relese_resource();
            System.err.println("资源释放完毕，退出系统 bye !");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     * 根据命令行的输入选择启动客户端还是服务器端
     * @param properties
     */
    private static void read_opt_from_terminal(Properties properties){
        Scanner scanner = null;
        try {
            scanner = new Scanner(System.in);
            //通过命令行的方式，覆盖配置文件指定的值
            String _port = null;//绑定的端口
            String _ip = null;//连接的端口
            System.err.println("欢迎使用数据同步系统，请您选择要启动的功能名称,请输入ser[ver]或cli[ent] ");
            String appName = "[Server]";
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                if("exit".equals(str.trim())){
                    break;
                }
                //说明启动的是客户端程序
                if(str.trim().startsWith("cli")){
                    appName = "[Client]";
                    _ip = read_ip_from_cmd(scanner);
                    if(!_ip.isEmpty()){
                        properties.setProperty("upstrem.ip",_ip);
                    }
                    System.err.println("Use IP: ["+properties.getProperty("upstrem.ip")+"]" );
                    _port = read_port_from_cmd(scanner);

                    if(!_port.isEmpty()){
                        properties.setProperty("upstrem.port",_port);
                    }
                    System.err.println("Use Port: ["+properties.getProperty("upstrem.port")+"]");
                    //启动客户端程序
                    executorService.execute(()->{
                        clientBoot = new ClientBoot();
                        clientBoot.start_client_jobs(properties.getProperty("upstrem.ip"),Integer.parseInt(properties.getProperty("upstrem.port").trim()),ip_blacklist,inner_ip_pattern,EXTEND_TABLES,test_upload_speed);
                    });
                    break;
                }else if(str.trim().startsWith("ser")){
                    is_server = true;
                    _port = read_port_from_cmd(scanner);
                    if(!_port.isEmpty()){
                        properties.setProperty("upstrem.port",_port);
                    }
                    System.err.println("start Server at " + properties.getProperty("upstrem.port"));
                    //启动服务端程序
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            ServerBoot.start_server(Integer.parseInt(properties.getProperty("upstrem.port").trim()));
                        }
                    });
//                    thriftExecutorService.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            ServerBoot.start_thrift(8899);
//                        }
//                    });
                    break;
                }else{
                    System.err.println("输入的["+str+"]不合法，请您选择要启动的程序名称,请输入ser[ver]或cli[ent] ");
                }

            }
            start_monitor_user_controll(appName,scanner);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(scanner!=null){
                scanner.close();
            }
        }
    }

    private static void startClient(String up_stream_ip,int up_stream_port){
        //启动客户端程序
        executorService.execute(()->{
            clientBoot = new ClientBoot();
            System.err.println("启动客户端程序[ip="+up_stream_ip+"],[port="+up_stream_port+"]");
            countDownLatch.countDown();
            clientBoot.start_client_jobs(up_stream_ip,up_stream_port,ip_blacklist,inner_ip_pattern,EXTEND_TABLES,test_upload_speed);
        });

    }
    private static void startSer(int port){
        //启动服务端程序
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("启动服务端程序[port="+port+"]");
                countDownLatch.countDown();
                ServerBoot.start_server(port);
            }
        });
    }

    /**
     * 启动监听用户输入的请求
     */
    private static void start_monitor_user_controll(String appName, Scanner scanner){
        System.err.println(appName + " Starting ...... \r\n输入exit(e)/quit(q)/close(c)退出程序 ");
        List<String> exit_cmds = Arrays.asList("e","c","q","quit","close","exit");

        while (scanner.hasNextLine()) {
            String cmd = scanner.nextLine();
            if(exit_cmds.contains(cmd)){
                break;
            }
            //取消
            if("m".equals(cmd)){
                if(clientBoot!=null){
                    clientBoot.cancel_shoudown_jobs();
                }
            }
            System.err.println("输入exit(e)/quit(q)/close(c)退出程序 ");
        }
        System.err.println("系统即将退出，启动释放资源的操作..");
        relese_resource();
        System.err.println("资源释放完毕，退出系统 bye !");
    }

    private static void relese_resource(){
        try {
            //释放资源
            if(is_server){
                stopAndReleaseServerResource();
            }else{
                stopAndReleaseClientResource();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(executorService!=null){
                try {
                    if(!executorService.isShutdown()){
                        executorService.shutdown();;
                    }
                    if(!executorService.awaitTermination(15,TimeUnit.SECONDS)){
                        executorService.shutdownNow();
                    }else{
                        System.err.println("任务线程执行结束...");
                    }
                    if(!executorService.isShutdown()){
                        System.err.println("等待任务线程执行结束...超时，执行强制退出");
                        Runtime.getRuntime().halt(-1);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
//            if(thriftExecutorService!=null){
//                try {
//                    if(!thriftExecutorService.isShutdown()){
//                        thriftExecutorService.shutdown();;
//                    }
//                    if(!thriftExecutorService.awaitTermination(15,TimeUnit.SECONDS)){
//                        thriftExecutorService.shutdownNow();
//                    }else{
//                        System.err.println("任务线程执行结束...");
//                    }
//                    if(!thriftExecutorService.isShutdown()){
//                        System.err.println("等待任务线程执行结束...超时，执行强制退出");
//                        Runtime.getRuntime().halt(-1);
//                    }
//                }catch (Exception ex){
//                    ex.printStackTrace();
//                }
//            }
        }
    }


    /**
     * 客户端的线程是doemon的的，当前线程结束，子线程就结束
     */
    private static void stopAndReleaseClientResource(){
        if(clientBoot!=null){
            clientBoot.terminal_jobs();
        }
    }
    /**
     * 停止serve
     */
    private static void stopAndReleaseServerResource(){
        ServerBoot.stop_server();
    }


    /**
     * 从命令行读取Ip
     * @param scanner
     * @return
     */
    private static String read_ip_from_cmd(Scanner scanner){
        System.err.println("请输入服务器端的IP地址，默认请直接按回车");
        String _ip = null;
        while(scanner.hasNextLine()){
            _ip = scanner.nextLine();
            //用户输入了IP地址
            if(_ip.trim().length()!=0){
                if(!orIp(_ip)){
                    System.err.println("输入的服务器ip["+_ip+"]地址不合法，请检查后重新输入，默认请直接按回车");
                    continue;
                }
                break;
            }
            break;
        }
        return _ip.trim();
    }

    private static String read_port_from_cmd(Scanner scanner){
        System.err.println("请输入服务器端监听端口，默认请直接按回车");
        String _port = null;
        while(scanner.hasNextLine()){
            _port = scanner.nextLine();
            //用户输入了IP地址
            if(_port.trim().length()!=0){
                if(!isNumeric(_port)){
                    System.err.println("输入的服务器监听端口["+_port+"]不是数字，请检查后重新输入，默认请直接按回车");
                    continue;
                }
                break;
            }
            break;
        }
        return _port.trim();
    }
    /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    /**
     * 判断IP是否合法
     * @param ip
     * @return
     */
    public static boolean orIp(String ip) {
        if(ip == null || "".equals(ip)) {
            return false;
        }
        String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        return ip.matches(regex);
    }

    /**
     * 读取配置文件
     * @return
     */
    private static Properties read_sys_cfg(){
        Properties properties = null;
        try(InputStream is = Resources.getResourceAsStream("sys.properties")) {
            properties = new Properties();
            properties.load(is);
        }catch (FileNotFoundException ex){
            throw new RuntimeException("没有找到sys.properties",ex);
        }catch (IOException e){
            throw new RuntimeException("IO异常",e);
        }
        return properties;
    }

    /**
     * 判断适用期限是否到期
     * @return
     */
    public static boolean expire(){
        try {
           File sys_file = Resources.getResourceAsFile("ehcache.xml");
           if(sys_file!=null){
               Instant instant = getCreateTime(sys_file.getAbsolutePath());
               Duration between = Duration.between(instant, Instant.now());
               long days = between.toDays();
               if(days>try_use_days){
                   return true;
               }else{
                   long remain = try_use_days-days;
                   if(remain<0){
                      remain = 0;
                   }
                   if(remain<7){
                       System.err.println("Days Remaining[ " + remain +" ]days");
                   }
                   return false;
               }
           }else{
               System.err.println("没有读取到ehcache.xml配置文件....");
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取文件的创建时间
     * @param fullFileName
     * @return
     */
    private static Instant getCreateTime(String fullFileName){
        Path path = Paths.get(fullFileName);
        BasicFileAttributeView basicview= Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        try {
            BasicFileAttributes attr = basicview.readAttributes();
            return attr.creationTime().toInstant();
        } catch (Exception e) {
        }
        return LocalDateTime.of(1970,1,1,0,0,0).atZone(ZoneId.systemDefault()).toInstant();
    }
}
