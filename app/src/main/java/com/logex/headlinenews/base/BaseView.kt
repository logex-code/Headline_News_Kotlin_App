package com.logex.headlinenews.base

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
interface BaseView {

    /**
     * 请求服务器失败
     */
    fun onServerFailure()

    /**
     * 网络错误
     */
    fun onNetworkFailure()
}