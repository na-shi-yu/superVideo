package com.hurys.video;

import com.hurys.video.entity.CameraPojo;
import com.hurys.video.flv.HttpFlvServer;
import com.hurys.video.service.CameraThread;
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
        cameraPojo.setRtmp("rtmp://admin:hurys2012@192.168.10.67/myapp/8554/1");
        cameraPojo.setRtsp("rtsp://admin:hurys2012@192.168.10.199:8554/1");
//        cameraPojo.setRtsp("rtsp://192.168.2.134:8554/qq");
        CameraThread job = new CameraThread(cameraPojo);
        job.run();
        HttpFlvServer httpFlvServer = new HttpFlvServer(1888,2);
        try {
            httpFlvServer.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
