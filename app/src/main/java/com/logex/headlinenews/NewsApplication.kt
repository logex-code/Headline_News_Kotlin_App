package com.logex.headlinenews

import android.app.Application

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * NewsApplication
 */
class NewsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: NewsApplication? = null
    }
}