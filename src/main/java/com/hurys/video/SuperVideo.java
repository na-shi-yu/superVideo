package com.hurys.video;

import com.hurys.video.entity.CameraPojo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/4/27 15:17
 * @Description:
 */

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SuperVideo {

    public static void main(String[] args) {
        SpringApplication.run(SuperVideo.class, args);
        CameraPojo cameraPojo = new CameraPojo();
//        cameraPojo.setRtmp("rtmp://192.168.10.67:18123/myapp/123");
//        cameraPojo.setRtsp("rtsp://admin:hurys2012@192.168.10.199:8554/1");
//        CameraThread job = new CameraThread(cameraPojo);
//        job.run();
    }
}