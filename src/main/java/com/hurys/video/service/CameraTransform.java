package com.hurys.video.service;

import com.hurys.video.entity.CameraPojo;
import lombok.extern.log4j.Log4j2;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/1/11 17:32
 * @Description:
 */
@Component
@Log4j2
public class CameraTransform {

    // 解码器
    protected FFmpegFrameGrabber grabber = null;
    // 编码器
    protected FFmpegFrameRecorder record = null;
    // 视频像素宽
    int width;
    // 视频像素高
    int height;

    // 视频参数
    protected int audiocodecid;
    protected int codecid;
    // 帧率
    protected double framerate;
    // 比特率
    protected int bitrate;

    // 音频参数
    // 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
    private int audioChannels;
    private int audioBitrate;
    private int sampleRate;

    // 设备信息
    private CameraPojo cameraPojo;

    public CameraTransform(CameraPojo cameraPojo) {
        this.cameraPojo = cameraPojo;
    }

    /**
     * 选择视频源
     *
     * @throws Exception
     * @author wuguodong
     */
    public CameraTransform from(){
        try {
            // 采集/抓取器
            //System.out.println(cameraPojo.getRtsp());
            grabber = new FFmpegFrameGrabber(cameraPojo.getRtsp());
            if (cameraPojo.getRtsp().indexOf("rtsp") >= 0) {
                // tcp用于解决丢包问题
                grabber.setOption("rtsp_transport", "tcp");
            }
            // 设置采集器构造超时时间
            grabber.setOption("stimeout", "2000000");
            // 开始之后ffmpeg会采集视频信息，之后就可以获取音视频信息
            grabber.start();
            width = grabber.getImageWidth();
            height = grabber.getImageHeight();
            // 若视频像素值为0，说明采集器构造超时，程序结束
            if (width == 0 && height == 0) {
                System.err.println("[ERROR]   拉流超时...");
                return null;
            }
            // 视频参数
            audiocodecid = grabber.getAudioCodec();
            //System.err.println("音频编码：" + audiocodecid);
            codecid = grabber.getVideoCodec();
            // 帧率
            framerate = grabber.getVideoFrameRate();
            // 比特率
            bitrate = grabber.getVideoBitrate();
            // 音频参数
            // 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
            audioChannels = grabber.getAudioChannels();
            audioBitrate = grabber.getAudioBitrate();
            if (audioBitrate < 1) {
                // 默认音频比特率
                audioBitrate = 128 * 1000;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("FFmpeg获取视频流失败：{}" , e.getMessage());
        }
        return this;
    }

    /**
     * 选择输出
     *
     * @throws Exception
     * @author wuguodong
     */
    public CameraTransform to() {
        try {
            // 录制/推流器
            record = new FFmpegFrameRecorder(cameraPojo.getRtmp(), width, height);
            // 画面质量参数，0~51；18~28是一个合理范围
            record.setVideoOption("crf", "28");
            record.setGopSize(2);
            record.setFrameRate(framerate);
            record.setVideoBitrate(bitrate);

            record.setAudioChannels(audioChannels);
            record.setAudioBitrate(audioBitrate);
            record.setSampleRate(sampleRate);
            AVFormatContext fc = null;
            if (cameraPojo.getRtmp().indexOf("rtmp") >= 0 || cameraPojo.getRtmp().indexOf("flv") > 0) {
                // 封装格式flv
                record.setFormat("flv");
                record.setAudioCodecName("aac");
                record.setVideoCodec(codecid);
                fc = grabber.getFormatContext();
            }
            record.start(fc);
        }catch (Exception e){
            e.printStackTrace();
            log.error("FFmpeg推送视频流失败：{}，推送地址：{}" , e.getMessage(), cameraPojo.getRtmp());
        }
        return this;
    }

    /**
     * 转封装
     *
     * @throws org.bytedeco.javacv.FrameGrabber.Exception
     * @throws org.bytedeco.javacv.FrameRecorder.Exception
     * @throws InterruptedException
     * @author wuguodong
     */
    public CameraTransform go(Thread nowThread)
            throws org.bytedeco.javacv.FrameGrabber.Exception, org.bytedeco.javacv.FrameRecorder.Exception {
        long err_index = 0;// 采集或推流导致的错误次数
        // 连续五次没有采集到帧则认为视频采集结束，程序错误次数超过5次即中断程序
        //将探测时留下的数据帧释放掉，以免因为dts，pts的问题对推流造成影响
        grabber.flush();
        for (int no_frame_index = 0; no_frame_index < 5 || err_index < 5; ) {
            try {
                // 用于中断线程时，结束该循环
                nowThread.sleep(1);
                AVPacket pkt = null;
                // 获取没有解码的音视频帧
                pkt = grabber.grabPacket();
                if (pkt == null || pkt.size() <= 0 || pkt.data() == null) {
                    // 空包记录次数跳过
                    no_frame_index++;
                    err_index++;
                    continue;
                }
                // 不需要编码直接把音视频帧推出去
                err_index += (record.recordPacket(pkt) ? 0 : 1);
                av_packet_unref(pkt);
            } catch (InterruptedException e) {
                // 当需要结束推流时，调用线程中断方法，中断推流的线程。当前线程for循环执行到
                // nowThread.sleep(1);这行代码时，因为线程已经不存在了，所以会捕获异常，结束for循环
                // 销毁构造器
                grabber.close();
                record.close();
                log.debug("设备中断推流成功...");
                break;
            } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
                err_index++;
            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                err_index++;
            }
        }
        // 程序正常结束销毁构造器
        grabber.close();
        record.close();
        //System.err.println("设备推流完毕...");
        return this;
    }
}
