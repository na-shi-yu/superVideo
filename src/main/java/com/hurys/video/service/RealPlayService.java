package com.hurys.video.service;

import com.alibaba.fastjson.JSONObject;
import com.hurys.video.common.ApiResult;

/**
 * @author YYK
 * @version 1.0
 * @date 2022/5/6 15:06
 * @Description:
 */
public interface RealPlayService {

    /**
     * 实时推送播放流
     * @return
     * @param params
     */
    ApiResult realPlay(JSONObject params);

    /**
     * 心跳
     * @param params
     * @return
     */
    ApiResult heatBeat(JSONObject params);
}
