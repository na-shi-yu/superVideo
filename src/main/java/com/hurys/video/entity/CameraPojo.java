package com.hurys.video.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/1/11 17:27
 * @Description:
 */
@Data
@Component
public class CameraPojo {

    /**
     * 摄像头账号
     */
    private String username;
    /**
     * 摄像头密码
     */
    private String password;
    /**
     * 摄像头ip
     */
    private String ip;
    /**
     * 摄像头端口
     */
    private String port;
    /**
     * 摄像头通道号
     */
    private String channel;
    /**
     * 串流密钥 一般为设备编号
     */
    private String streamPass;
    /**
     * rtsp地址
     */
    private String rtsp;
    /**
     * rtmp地址
     */
    private String rtmp;
    /**
     * flv地址
     */
    private String flvUrl;
    /**
     * 打开时间
     */
    private String openTime;
    /**
     * 使用人数
     */
    private int count = 0;
    /**
     * 唯一标识token
     */
    private String token;
}
