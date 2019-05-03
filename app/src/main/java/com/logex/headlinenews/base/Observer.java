package com.logex.headlinenews.base;

/**
 * 创建人: Administrator
 * 日期: 2019/4/29
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * 观察者
 */
public interface Observer<T> {

    void onSuccess(T t);

    void onFailure(String errInfo);
}
