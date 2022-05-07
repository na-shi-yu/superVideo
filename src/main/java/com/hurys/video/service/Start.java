package com.hurys.video.service;

import com.hurys.video.entity.CameraPojo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.hurys.video.service.impl.RealPlayServiceImpl.PLAYERJOB;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/5/6 17:25
 * @Description: 启动类
 */
@Component
public class Start {

    /**
     * 启动线程
     * @param deviceNo
     * @param cameraPojo
     */
    @Async
    public void play(String deviceNo, CameraPojo cameraPojo){
        CameraThread job = new CameraThread(cameraPojo);
        PLAYERJOB.put(deviceNo,job);
        job.run();
    }
}
