package com.hurys.video.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.hurys.video.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YYK
 * @version 1.0
 * @date 2021/8/4 10:46
 * @Description: 接口统一返回实体类
 */
@Data
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 4879232822241086113L;

    private int code;

    private T data;

    private String message;

    private String time;

    /**
     * 构造函数
     */
    public ApiResult() {
    }

    public static ApiResult result(boolean flag) {
        if (flag) {
            return ok();
        }
        return fail("");
    }

    public static ApiResult result(boolean flag, Object data) {
        if (flag) {
            return ok(data);
        }
        return fail("");
    }

    public static ApiResult result(ApiCode apiCode) {
        return result(apiCode, null);
    }

    public static ApiResult result(ApiCode apiCode, Object data) {
        return result(apiCode, null, data);
    }

    public static ApiResult result(ApiCode apiCode, String msg, Object data) {
        String message = apiCode.getMsg();
        if (StringUtils.isNotBlank(msg)) {
            message = msg;
        }
        return ApiResult.builder()
                .code(apiCode.getCode())
                .message(message)
                .data(data)
                .time(DateUtil.getNowTime())
                .build();
    }

    public static ApiResult ok() {
        return ok(null);
    }

    public static ApiResult ok(Object data) {
        return result(ApiCode.SUCCESS, data);
    }

    public static ApiResult ok(String message, Object data) {
        return result(ApiCode.SUCCESS, message, data);
    }

    public static ApiResult fail(String msg) {
        return result(ApiCode.FAIL, msg, null);

    }

    public static ApiResult fail(ApiCode apiCode) {
        return result(apiCode, null);
    }

    public static ApiResult fail(ApiCode apiCode, Object data) {
        if (ApiCode.SUCCESS == apiCode) {
            throw new RuntimeException("失败结果状态码不能为" + ApiCode.SUCCESS.getCode());
        }
        return result(apiCode, data);
    }

    public static ApiResult fail(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return result(ApiCode.FAIL, map);
    }

}
