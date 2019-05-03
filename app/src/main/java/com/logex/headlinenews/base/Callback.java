package com.logex.headlinenews.base;

/**
 * 创建人: Administrator
 * 日期: 2019/4/30
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * Callback
 */
public interface Callback<T> {

    void onSuccess(T t);

    void onFailure(String errInfo);
}
