package com.logex.headlinenews.base

import okhttp3.Interceptor
import okhttp3.Response


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 拦截请求封装公共参数
 */
class CommonInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()

        // 添加新的参数
        val authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())
                .addQueryParameter("device_id", "8800803362")
                .addQueryParameter("iid", System.currentTimeMillis().toString())

        // 新的请求
        val newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build())
                .build()

        return chain.proceed(newRequest)
    }
}