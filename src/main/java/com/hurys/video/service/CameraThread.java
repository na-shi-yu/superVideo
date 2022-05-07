package com.hurys.video.service;

import com.hurys.video.entity.CameraPojo;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.hurys.video.service.impl.RealPlayServiceImpl.PLAYERJOB;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/1/11 17:35
 * @Description:
 */
@Component
@Log4j2
public class CameraThread implements Runnable {

    private CameraPojo cameraPojo;

    private Thread currentThread;

    public CameraThread(CameraPojo cameraPojo) {
        this.cameraPojo = cameraPojo;
    }

    /**
     * 中断线程
     */
    public void setInterrupted() {
        currentThread.interrupt();
    }

    @Override
    public void run() {
        // 直播流
        try {
            // 获取当前线程存入缓存
            currentThread = Thread.currentThread();
            // 执行转流推流任务
            CameraTransform push = new CameraTransform(cameraPojo).from();
            if (push != null) {
                push.to().go(currentThread);
            }
        } catch (Exception e) {
            log.error("当前线程：" + Thread.currentThread().getName() + " 当前任务：" + cameraPojo.getRtsp() + "停止...");
            e.printStackTrace();
        }
    }
}
