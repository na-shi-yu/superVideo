package com.hurys.video.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hurys.video.common.ApiResult;
import com.hurys.video.entity.CameraPojo;
import com.hurys.video.service.CameraThread;
import com.hurys.video.service.RealPlayService;
import com.hurys.video.service.Start;
import com.hurys.video.utils.Ping;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/5/6 15:07
 * @Description:
 */
@Log4j2
@Service
public class RealPlayServiceImpl implements RealPlayService {
    /**
     * 订阅
     * key:设备编号，value：过期时间，单位s
     */
    public static final Map<String,Integer> SUBSCRIBE = new HashMap<String,Integer>();

    /**
     * 设备线程
     * key 设备编号  value 线程
     */
    public static final Map<String,CameraThread> PLAYERJOB = new HashMap<String,CameraThread>();

    @Value("${nginx.ip}")
    private String nginxIp;

    @Value("${nginx.rtmpPort}")
    private String rtmpPort;

    @Value("${nginx.rtmpPath}")
    private String rtmpPath;

    @Value("${nginx.flvPort}")
    private String flvPort;

    @Resource
    private Start start;

    public static final int TIMEOUT = 60;
    /**
     * 播放
     * @param params
     * @return
     */
    @Override
    public ApiResult realPlay(JSONObject params) {
        JSONObject result = new JSONObject();
        try{
            String ip = params.getString("ip");
            String port = params.getString("port");
            boolean flag = Ping.pingHost(ip, Integer.parseInt(port));
            if (!flag){
                return ApiResult.fail("请检测网络联通性");
            }
            String username = params.getString("username");
            String password = params.getString("password");
            // 1主通道 2次通道
            String channel = params.getString("channel");
            String deviceNo = params.getString("deviceNo");
            String rtspPlayUrl = "rtsp://" + username + ":" + password + "@" + ip + ":" + port + "/" + channel;
            String rtmpPlayUrl = "rtmp://" + nginxIp + ":" + rtmpPort + "/" + rtmpPath + "/" + deviceNo;
            String flvPlayUrl = "http://" + nginxIp + ":" + flvPort + "/live?port=" +rtmpPort + "&app=" + rtmpPath + "&stream=" + deviceNo;
            if (!SUBSCRIBE.containsKey(deviceNo)){
                CameraPojo cameraPojo = new CameraPojo();
                cameraPojo.setRtmp(rtmpPlayUrl);
                cameraPojo.setRtsp(rtspPlayUrl);
                start.play(deviceNo,cameraPojo);
            }
            synchronized(SUBSCRIBE){
                SUBSCRIBE.put(deviceNo,TIMEOUT);
            }
            result.put("rtmpPlayUrl",rtmpPlayUrl);
            result.put("rtspPlayUrl",rtspPlayUrl);
            result.put("flvPlayUrl" ,flvPlayUrl);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResult.fail("视频播放失败");
        }
        return ApiResult.ok(result);
    }


    /**
     * 心跳
     * @param params
     * @return
     */
    @Override
    public ApiResult heatBeat(JSONObject params) {
        try {
            String deviceNo = params.getString("deviceNo");
            //采用过期模式
            synchronized(SUBSCRIBE){
                SUBSCRIBE.put(deviceNo,TIMEOUT);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ApiResult.ok();
    }

    /**
     * 心跳监测
     */
    @PostConstruct
    public void heartBeat() {
        /**使用Executors工具快速构建对象*/
        log.info("心跳监测线程开启");
        ScheduledExecutorService scheduledExecutor = new ScheduledThreadPoolExecutor(1);
        try {
            scheduledExecutor.scheduleWithFixedDelay(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Iterator<Map.Entry<String, Integer>> iterator = SUBSCRIBE.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    Map.Entry<String, Integer> next = iterator.next();
                                    String key = next.getKey();
                                    log.info("心跳监测：{}",next.toString());
                                    synchronized (SUBSCRIBE) {
                                        SUBSCRIBE.merge(key, -1, Integer::sum);
                                        if (SUBSCRIBE.get(key).intValue() < 0) {
                                            //超时，去除订阅，关闭线程
                                            SUBSCRIBE.remove(key);
                                            PLAYERJOB.get(key).setInterrupted();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                log.error("心跳监测异常：" + e.getMessage());
                            }
                        }
                    },
                    0, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("心跳监测异常：" + e.getMessage());
        }
    }
}
