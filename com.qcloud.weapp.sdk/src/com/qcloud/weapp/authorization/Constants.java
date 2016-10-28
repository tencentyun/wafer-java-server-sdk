package com.qcloud.weapp.authorization;

/**
 * 登录服务常量，包括登录错误类型
 * */
public final class Constants {
    static final String WX_SESSION_MAGIC_ID = "F2C224D4-2BCE-4C64-AF9F-A6D872000D1A";
    static final String WX_HEADER_CODE = "X-WX-Code";
    static final String WX_HEADER_ID = "X-WX-Id";
    static final String WX_HEADER_SKEY = "X-WX-Skey";
    static final String WX_HEADER_ENCRYPT_DATA = "X-WX-Encrypt-Data";

    /**
     * 表示登录失败
     * */
    public static final String ERR_LOGIN_FAILED = "ERR_LOGIN_FAILED";

    /**
     * 表示会话过期的错误
     * */
    public static final String ERR_INVALID_SESSION = "ERR_INVALID_SESSION";

    /**
     * 表示检查登录态失败
     * */
    public static final String ERR_CHECK_LOGIN_FAILED = "ERR_CHECK_LOGIN_FAILED";
}
