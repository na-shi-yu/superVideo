package com.hurys.video.controller;

import com.alibaba.fastjson.JSONObject;
import com.hurys.video.entity.CameraPojo;
import com.hurys.video.service.CameraThread;
import com.hurys.video.service.RealPlayService;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/4/28 13:52
 * @Description:
 */
@RestController
public class PlayController {

    @Resource
    private RealPlayService realPlayService;


    /**
     * 实时播放
     *
     * @param params
     * @return
     */
    @PostMapping("/real")
    public JSONObject playVideo(@RequestBody JSONObject params) {

        return realPlayService.realPlay(params);
    }

    /**
     * 心跳
     *
     * @param params
     * @return
     */
    @PostMapping("heatBet")
    public JSONObject playHeatBeat(@RequestBody JSONObject params) {
        return realPlayService.heatBeat(params);
    }

}
