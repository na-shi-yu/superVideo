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

    /**
     * nginx的ip
     */
    @Value("${nginx.ip}")
    private String nginxIp;

    /**
     * rtmp的推送端口
     */
    @Value("${nginx.rtmpPort}")
    private String rtmpPort;

    /**
     * rtmp的path
     */
    @Value("${nginx.rtmpPath}")
    private String rtmpPath;

    /**
     * flv的端口号
     */
    @Value("${nginx.flvPort}")
    private String flvPort;

    @Resource
    private Start start;

    /**
     * 视频心跳超时时间
     */
    public static final int TIMEOUT = 120;

    /**
     * 播放
     * @param params json
     * @return api
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
//            e.printStackTrace();
            log.error("视频播放失败,{}" ,e.getMessage());
            return ApiResult.fail("视频播放失败");
        }
        return ApiResult.ok(result);
    }


    /**
     * 心跳
     * @param params json
     * @return api
     */
    @Override
    public ApiResult heatBeat(JSONObject params) {
        try {
            String deviceNo = params.getString("deviceNo");
            if (!PLAYERJOB.containsKey(deviceNo)){
                return ApiResult.fail("暂未播放该视频，心跳无效");
            }
            //采用过期模式
            synchronized(SUBSCRIBE){
                SUBSCRIBE.put(deviceNo,TIMEOUT);
            }
        }catch (Exception e){
//            e.printStackTrace();
            log.error("心跳异常,{}" ,e.getMessage());
        }
        return ApiResult.ok();
    }

    /**
     * 心跳监测 1s执行一次
     */
    @PostConstruct
    public void heartBeat() {
        log.info("心跳监测线程开启");
        ScheduledExecutorService heatBeat = new ScheduledThreadPoolExecutor(1);
        try {
            heatBeat.scheduleWithFixedDelay(
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
                                            PLAYERJOB.remove(key);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                               // e.printStackTrace();
                                log.error("心跳监测异常：{}" + e.getMessage());
                            }
                        }
                    },
                    0, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("心跳监测异常：{}" + e.getMessage());
        }
    }
}
