package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 通用请求结果模型
 */
data class HttpResult<T>(
        val message: String?,
        var data: T?,
        var isGetCache: Boolean?,
        private val success: Boolean,
        val tips: Tips?
) {

    data class Tips(
            val type: String,
            val display_duration: Int,
            val display_info: String,
            val display_template: String,
            val open_url: String,
            val web_url: String,
            val download_url: String,
            val app_name: String,
            val package_name: String
    )

    fun isSuccess(): Boolean = "success" == message || success

}