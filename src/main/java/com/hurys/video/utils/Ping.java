package com.hurys.video.utils;


import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YYK
 * @version 1.0
 * @date 2020/5/13 10:27
 * @Description: ping工具
 */
@Component
@Log4j2
public class Ping {

    private static final boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
    /**
     * 当返回值是true时，说明host是可用的，false则不可。
     * @param ipAddress
     * @return
     * @throws Exception
     */
    public static boolean ping(String ipAddress) throws Exception {
        int  timeOut = 300;
        return InetAddress.getByName(ipAddress).isReachable(timeOut);
    }

    public static boolean ping(String ipAddress,int pingTime,int timeOut) {
        BufferedReader buf = null;
        try {
            String command = "";
            if (isWin){
                command = "ping " + ipAddress + " -n " + pingTime + " -w " + timeOut;
            }else {
                command = "ping -c " + pingTime + " -w " + 1 + " " + ipAddress;
            }
            Process pro = Runtime.getRuntime().exec(command);
            buf = new BufferedReader(new InputStreamReader( pro.getInputStream()));
            int connectedCount = 0;
            String line = null;
            while ((line = buf.readLine()) != null){
                connectedCount += getCheckResult(line);
            }
            return connectedCount > 0;
        } catch (Exception ex) {
            return false;
        }finally {
            try {
                buf.close();
            }catch (IOException e){
                log.error("ping失败：" + ipAddress, "异常：" + e.getMessage());
            }
        }
    }

    /**
     * ping匹配计数
     * @param line
     * @return
     */
    private static int getCheckResult(String line){
        Pattern pattern = Pattern.compile("(\\d+ms)",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()){
            return 1;
        }
        return 0;
    }

    /**
     * ping 端口
     * @param ipAddress
     * @param port
     * @return
     * @throws Exception
     */
     public static boolean pingHost(String ipAddress,int port) {
        boolean isReachable = false;
        Socket connect = new Socket();
        try {
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(ipAddress, port);
             //此处3000是超时时间,单位 毫秒
             connect.connect(endpointSocketAddr,3000);
             isReachable = connect.isConnected();
             } catch (Exception e) {
                System.out.println(e.getMessage() + ", ip = " + ipAddress + ", port = " +port);
             } finally {
                 if (connect != null) {
                    try {
                        connect.close();
                    } catch (IOException e) {
                       // System.out.println(e.getMessage() + ", ip = " + ipAddress + ", port = " +port);
                    }
                 }
            }
        return isReachable;
     }

    /**
     * ping ip，返回ping时长
     * @param ip
     * @return
     * @throws Exception
     */
    public static String isConnect(String ip) throws Exception {
        BufferedReader br = null;
        try{
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("ping " + ip);
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), "GB2312");
            br = new BufferedReader(inputStreamReader);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            //未ping通
            if(!sb.toString().contains("平均")){
                return "-1";
            }
            else{
                String s = sb.toString().substring(sb.toString().lastIndexOf("平均") + 5, sb.length()).toString();
                String regEx="[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(s);
                return m.replaceAll("").trim();
            }
        }catch (Exception e){
            throw new Exception();
        }finally {
            if (br != null){
                br.close();
            }
        }
    }

    /**
     * 获取ping远程设备时长
     * @param ip
     * @return
     */
    public static long getConnectTime(String ip){
        long conTime = -1l;
        long pingStartTime = System.currentTimeMillis();
        try {
            boolean ping = ping(ip);
            long pingEndTime = System.currentTimeMillis();
            if(ping){
                conTime = pingEndTime-pingStartTime;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Ping 服务器IP：{}地址异常", ip);
        }

        return conTime;
    }

    /**
     * 获取当前网段全部可用ip
     * @return
     */
    public static List<String>  getIPs()
    {
        List<String> list = new ArrayList<String>();
        boolean flag = false;
        int count=0;
        Runtime r = Runtime.getRuntime();
        Process p;
        try {
            p = r.exec("arp -a");
           // InputStreamReader isr = new InputStreamReader(fis, "GBK");
            BufferedReader br = new BufferedReader(new InputStreamReader(p
                    .getInputStream(),"GBK"));
            String inline;
            while ((inline = br.readLine()) != null) {
                if(inline.indexOf("接口") > -1){
                    flag = !flag;
                    if(!flag){
                        //碰到下一个"接口"退出循环
                        break;
                    }
                }
                if(flag){
                    count++;
                    if(count > 2){
                        //有效IP
                        String[] str=inline.split(" {4}");
                        list.add(str[0]);
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据ip获取主机名称
     * @param ips
     * @return
     */
    public static Map<String,String> getHostnames(List<String> ips){
        Map<String,String> map = new HashMap<String,String>();
        System.out.println("正在提取hostname...");
        for(String ip : ips){
            String command = "ping -a " + ip;
            Runtime r = Runtime.getRuntime();
            Process p;
            try {
                p = r.exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(p
                        .getInputStream()));
                String inline;
                while ((inline = br.readLine()) != null) {
                    if(inline.indexOf("[") > -1){
                        int start = inline.indexOf("Ping ");
                        int end = inline.indexOf("[");
                        String hostname = inline.substring(start+"Ping ".length(),end-1);
                        System.out.println(hostname);
                        map.put(ip,hostname);
                    }
                }
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("提取结束！");
        return map;
    }

    public static void main(String[] args) throws Exception {
       // ping02("33.226.255.63");
        ;
        System.out.println(Ping.ping("127.0.0.1",4,1000));
    }
}