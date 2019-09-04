package com.cmd;
//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖镇楼           BUG辟易
//
//                             佛曰:
//
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created on 2019-07-12 08:59
 * ClassName:Shutdown
 * Package:com.cmd
 * Company FREEDOM
 * Description:
 * Depart Tech
 *
 * @author qiury_zhenxin@126.com
 */
public class Shutdown {


    /**
     * 打印关机提示信息
     */
    public static void printShotdownInfo(LocalDateTime pdt){
        System.err.println("系统将在"+pdt.format(DateTimeFormatter.ofPattern("hh:mm:ss"))+"关机，输入 m 可以取消关机任务。");
    }

    /**
     * 输出提示信息
     */
    public static void printCancelShutdownInfo(){
        System.err.println("系统关机任务被取消成功。");
    }

    public static void printShutdownInfo(String cmd) {
        System.err.println("发送系统关机命令[" + cmd + "]给操作系统成功");
    }


    /**
     * 执行关机命令
     * @param delay
     * @throws IOException
     */
    public static void shutDownSysNow(int delay) throws IOException {
        String cmd = "shutdown -r -f -t " + delay;

        printShutdownInfo(cmd);

        Runtime.getRuntime().exec(cmd);
    }


    /**
     *   * 生成多个退格符
     *   * @param count
     *   * @return
     *   
     */
    private static String getBackspaces(int count) {
        char[] chs = new char[count];
        for (int i = 0; i < count; i++) {
            chs[i] = '\b';
        }
        return new String(chs);
    }

    public static String getPercentProgress(String pre,int percent){
        StringBuilder sb = new StringBuilder(pre+"已上传：");
        if(percent!=0){
            for (int i = 0; i < percent; i++) {
                sb.append("#");
            }
            sb.append(" ");
        }
        //sb.append(getBackspaces(sb.length()));
        sb.append(percent+"%").append('\r');
        return sb.toString();
    }
}
