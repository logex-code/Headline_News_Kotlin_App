package com.logex.headlinenews.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建人: liguangxi
 * 日期: 2018/3/2
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 时间格式化工具类
 */
class TimeFormatUtil {

    companion object {

        fun getPublishTime(time: Long?): String {
            if (time == null) return "未知"

            val currentTime: Long = System.currentTimeMillis() / 1000
            val newTime = currentTime - time

            return when {
                newTime < 60 -> "刚刚"
                newTime < 3600 -> "${newTime / 60}分钟前"
                newTime < 86400 -> "${newTime / 3600}小时前"
                newTime in 86400..259200 -> "${newTime / 86400}天前"
                else -> {
                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    format.format(Date(time))
                }
            }
        }
    }
}