package com.znjt.net;

import com.znjt.utils.LoggerUtils;
import io.grpc.netty.shaded.io.netty.util.NetUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Created by qiuzx on 2019-03-18
 * Company BTT
 * Depart Tech
 * @author qiuzx
 */
public class NetUtils {
//    public static void main(String[] args) throws SocketException{
//        Socket socket = new Socket();
//        System.out.println("res = " + getLocalIp("192.168.36.1"));
//    }


    public static String getLocalIp(String targetIp) throws SocketException {
       Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
       //boolean isTrueIP = false;
       if(allNetInterfaces!=null){
           List<String> addressList = new ArrayList<>();
           HashMap<String,String> map = new HashMap<>();
           //while (allNetInterfaces.hasMoreElements()&&!isTrueIP){
           while (allNetInterfaces.hasMoreElements()){
               NetworkInterface networkInterface = allNetInterfaces.nextElement();
               if(!networkInterface.isUp()){
                   continue;
               }
               List<InterfaceAddress> interfaceAddressList = networkInterface.getInterfaceAddresses();
               if(interfaceAddressList!=null){
                   for(InterfaceAddress interfaceAddress:interfaceAddressList){
                       InetAddress inetAddress = interfaceAddress.getAddress();
                       if (inetAddress != null && inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                           // 获取ip地址
                           String hostAddress = inetAddress.getHostAddress();
                           addressList.add(hostAddress);
                           // 获取子网掩码
                           String maskAddress = NetUtils.calcMaskByPrefixLength(interfaceAddress.getNetworkPrefixLength());
                           // 将IP地址和子网掩码存放到map中
                           map.put(hostAddress, maskAddress);
                       }
                   }
               }
           }
           if (addressList.size() > 0) {
               // 算出匹配度最高的ip地址
               String matchedIp = NetUtils.matchMaxIp(addressList, targetIp);
              // 根据IP地址取出子网掩码
               String matchedMask = map.get(matchedIp);
               if (matchedMask != null) {
                   // 本地ip和子网掩码按位与后的结果
                   String subnetAddress = NetUtils.calcSubnetAddress(matchedIp, matchedMask);
                  String subnetAddress2 = NetUtils.calcSubnetAddress(targetIp, matchedMask);
                   if (subnetAddress.equals(subnetAddress2)) {
                       return matchedIp;
                   }
               }
           }
       }
       return null;
    }


    /**
     * 计算子网掩码
     *
     * @param length
     * @return
     */
    public static String calcMaskByPrefixLength(int length) {
        if (length == -1) {
            length = 24;
        }
        StringBuilder maskStr = new StringBuilder();
        int[] maskIp = new int[4];
        for (int i = 0; i < maskIp.length; i++) {
            maskIp[i] = (length >= 8) ? 255 : (length > 0 ? (0xff << (8 - length) & 0xff) : 0);
            length -= 8;
            maskStr.append(maskIp[i]);
            if (i < maskIp.length - 1) {
                maskStr.append(".");
            }
        }
        return maskStr.toString();
    }

    /**
     * 计算ip地址和子网掩码按位与结果
     *
     * @param ip
     * @param mask
     * @return
     */
    public static String calcSubnetAddress(String ip, String mask) {
        String result = "";
        try {
            // calc sub-net IP
            InetAddress ipAddress = InetAddress.getByName(ip);
            InetAddress maskAddress = InetAddress.getByName(mask);

            byte[] ipRaw = ipAddress.getAddress();
            byte[] maskRaw = maskAddress.getAddress();

            int unsignedByteFilter = 0x000000ff;
            int[] resultRaw = new int[ipRaw.length];
            for (int i = 0; i < resultRaw.length; i++) {
                resultRaw[i] = (ipRaw[i] & maskRaw[i] & unsignedByteFilter);
            }
            // make result string
            result = result + resultRaw[0];
            for (int i = 1; i < resultRaw.length; i++) {
                result = result + "." + resultRaw[i];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 算出匹配度最高的ip地址
     *
     * @param addressList
     * @param networkSegment
     * @return
     */
    public static String matchMaxIp(List<String> addressList, String networkSegment) {
        List<Integer> list = new ArrayList<Integer>();
        Map<Integer, String> hashMap = new HashMap<Integer, String>();

        StringBuilder sb = new StringBuilder();
        String[] split = networkSegment.split("\\.");
        for (String string : split) {
            String binaryString = Integer.toBinaryString(Integer.valueOf(string));
            while (binaryString.length() < 8) {
                binaryString = "0" + binaryString;
            }
            sb.append(binaryString);
        }

        for (String address : addressList) {
            if (address.equals(networkSegment)) {
                return address;
            }
            int match = match(address, sb.toString());
            list.add(match);
            hashMap.put(match, address);
        }
        Collections.sort(list);

        return hashMap.get(list.get(list.size() - 1));
    }

    /**
     * 计算ip地址的匹配度
     *
     * @param address
     * @param networkSegment
     * @return
     */
    public static int match(String address, String networkSegment) {
        int length = 0;
        String[] split = address.split("\\.");
        StringBuilder sb1 = new StringBuilder();

        for (String string : split) {
            String binaryString = Integer.toBinaryString(Integer.valueOf(string));
            while (binaryString.length() < 8) {
                binaryString = "0" + binaryString;
            }
            sb1.append(binaryString);
        }
        for (int i = 0; i < sb1.length(); i++) {
            if (sb1.charAt(i) == networkSegment.charAt(i)) {
                length++;
            } else {
                return length;
            }
        }
        return length;
    }

    /**
     * 判断当前机器获取的ip地址是否是公网ip
     * @return
     */
    public static boolean is_inner_network_ip(String pattern) throws  UnknownHostException{
        InetAddress inetAddress = getLocalHostLANAddress();
        if(inetAddress!=null){
            String local_ip = inetAddress.getHostAddress();
            if(StringUtils.isNotBlank(local_ip)){
                String[] ips = pattern.split(",");
                boolean matched = false;
                for(String ip:ips){
                    matched = local_ip.startsWith(pattern);
                    if(matched){
                        return !matched;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 明确一些规则：
     * 127.xxx.xxx.xxx 属于"loopback" 地址，即只能你自己的本机可见，就是本机地址，比较常见的有127.0.0.1；
     * 192.168.xxx.xxx 属于private 私有地址(site local address)，属于本地组织内部访问，只能在本地局域网可见。
     * 同样10.xxx.xxx.xxx、从172.16.xxx.xxx 到 172.31.xxx.xxx都是私有地址，也是属于组织内部访问；
     * 169.254.xxx.xxx 属于连接本地地址（link local IP），在单独网段可用
     * 从224.xxx.xxx.xxx 到 239.xxx.xxx.xxx 属于组播地址
     * 比较特殊的255.255.255.255 属于广播地址
     * 除此之外的地址就是点对点的可用的公开IPv4地址
     * 正确的IP拿法，即优先拿site-local地址
     */
    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}
