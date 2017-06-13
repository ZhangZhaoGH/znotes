package com.zhang.znotes.api;

import com.zhang.znotes.utils.CommonUtil;

/**
 * Created by zz on 2017/3/20.
 * 各种接口地址
 */

public class ApiConstants {
    /**
     * oneKeyShare申请的key
     */
    private static final String APP_KEY = "1c3ad81bd8fe6";
    /**
     * 用户登录成功后返回的token
     */
    public static String USER_TOKEN = "";
    /**
     * 用户登录成功后返回的id
     */
    public static String USER_ID = "";
    /**
     * 注册
     */
    private static final String REGISTER_URL = "http://apicloud.mob.com/user/rigister?key=" + APP_KEY;
    /**
     * 登录
     */
    private static final String LOGIN_URL = "http://apicloud.mob.com/user/login?key=" + APP_KEY;

    /**
     * 注册
     */
    public static String getRegisterUrl(String account, String password) {
        return REGISTER_URL + "&username=" + account + "&password=" + password + "&email=" + CommonUtil.getRandom(9) + "@163.com";
    }

    /**
     * 登录
     */
    public static String getLoginUrl(String account, String password) {
        return LOGIN_URL + "&username=" + account + "&password=" + password;
    }

}
