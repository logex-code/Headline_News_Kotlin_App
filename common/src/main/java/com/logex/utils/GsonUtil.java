package com.logex.utils;

import com.google.gson.Gson;

/**
 * 创建人: liguangxi
 * 日期: 17-7-27
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * json工具类
 */
public class GsonUtil {
    private static Gson gson;

    public static Gson getInstance() {
        synchronized (GsonUtil.class) {
            if (gson == null) {
                gson = new Gson();
            }
            return gson;
        }
    }
}
