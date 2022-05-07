package com.hurys.video.controller;

import com.alibaba.fastjson.JSONObject;
import com.hurys.video.common.ApiResult;
import com.hurys.video.service.RealPlayService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/4/28 13:52
 * @Description: 播放
 */
@RestController
@RequestMapping("/play")
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
    public ApiResult playVideo(@RequestBody JSONObject params) {
        return realPlayService.realPlay(params);
    }

    /**
     * 心跳
     *
     * @param params
     * @return
     */
    @PostMapping("/heatBeat")
    public ApiResult playHeatBeat(@RequestBody JSONObject params) {
        return realPlayService.heatBeat(params);
    }

}
