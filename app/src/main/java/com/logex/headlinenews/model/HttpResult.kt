package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 通用请求结果模型
 */
data class HttpResult<out T>(val message: String? = null, val data: T? = null, var isGetCache: Boolean?) {

    fun isSuccess(): Boolean = "success" == message

}