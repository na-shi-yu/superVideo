package com.hurys.video.common;

/**
 * @author YYK
 * @version 1.0
 * @date 2021/8/4 10:45
 * @Description: 接口状态码枚举类
 */
@SuppressWarnings("all")
public enum ApiCode {

    SUCCESS(200, "操作成功"),

    ERROR_AUTH(201, "用户名或密码错误"),

    NEED_PARAM(202, "缺少参数"),

    OLD_PWD_ERROR(203, "旧密码输入错误"),

    UNAUTHORIZED(401, "非法访问"),

    NOT_PERMISSION(403, "没有权限"),

    NOT_FOUND(404, "你请求的路径不存在"),

    FAIL(500, "操作失败"),

    SYSTEM_EXCEPTION(5000, "系统异常!"),

    PARAMETER_EXCEPTION(5001, "请求参数校验异常"),

    PARAMETER_PARSE_EXCEPTION(5002, "请求参数解析异常"),

    HTTP_MEDIA_TYPE_EXCEPTION(5003, "HTTP Media 类型异常"),

    SYSTEM_LOGIN_EXCEPTION(5005, "系统登录异常"),

    /**
     * 账户鉴权认证异常提示
     */
    ACCESS_TOKEN_INVALID(1001, "access_token 无效,身份认证失败"),
    REFRESH_TOKEN_INVALID(1002, "refresh_token 无效,身份认证刷新失败"),
    INSUFFICIENT_PERMISSIONS(1003, "账号权限不足以访问该资源接口"),
    ACCESS_UNAUTHORIZED(1004, "访问此资源需要身份验证"),
    ACCOUNT_NOT_EXIT(1005, "账号不存在"),
    ACCOUNT_LOCKED(1006, "账号被锁定"),
    ACCOUNT_PASSWORD_ERROR(1007, "账号/密码错误"),
    VALIDATE_CODE_ERROR(1008, "验证码错误"),
    ACCOUNT_PASSWORD_ERROR_TIMES(1009, "用户连续输入账号密码错误超过三次"),

    USER_NOT_FOUND(3001, "Username Not Found"),

    BAD_CREDENTIALS(3002, "Bad Credentials"),

    USER_LOCKED(3003, "Account Has Been Disabled/Product Error"),

    USER_ALREADY_EXISTS(3004, "User Already Exists"),

    INCORRECT_ORIGINAL_PASSWORD(3005, "Incorrect Original Password"),

    FORBIDDEN(4001, "Forbidden"),

    UNKNOWN_ERROR(5000, "Unknown Error"),

    ARGS_VALIDATE_ERROR(5001, "Arguments Validate Error"),

    INTERNAL_ERROR(5002, "Internal Error"),

    SERVICE_ERROR(6000, "Service return error"),

    PRODUCT_TAG_FOUND(7001, "Product Not Found"),
    ;

    private final int code;
    private final String msg;

    ApiCode(final int code, final String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 通过状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举
     */
    public static ApiCode getApiCode(int code) {
        ApiCode[] ecs = ApiCode.values();
        for (ApiCode ec : ecs) {
            if (ec.getCode() == code) {
                return ec;
            }
        }
        return SUCCESS;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
