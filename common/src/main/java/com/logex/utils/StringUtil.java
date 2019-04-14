package com.logex.utils;

/**
 * 创建人: liguangxi
 * 日期: 2018/2/28
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 字符串判空 包含 null 和 ""
     *
     * @param str 字符串
     * @return true false
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 字符串非空
     *
     * @param str 字符串
     * @return true false
     */
    public static boolean isNotEmpty(CharSequence str) {
        return str != null && str.length() > 0;
    }

    /**
     * 空安全的 equals
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }
}
