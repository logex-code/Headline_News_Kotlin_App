package com.logex.headlinenews

import java.text.SimpleDateFormat
import java.util.*

/**
 * 创建人: liguangxi
 * 日期: 2019/5/3
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * 定义扩展
 **/

/**
 * 获取新闻发布时间
 * @param time 发布时间戳/1000
 */
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

/**
 * 获取视频时长
 */
fun getVideoDuration(duration: Int): String {
    if (duration <= 0) return "00:00"
    return when {
        duration < 10 -> "00:0$duration"
        duration < 60 -> "00:$duration"
        duration < 600 -> {
            val remainder = duration % 60
            if (remainder < 10) {
                "0${duration / 60}:0$remainder"
            } else {
                "0${duration / 60}:$remainder"
            }
        }
        else -> {
            val remainder = duration % 60
            if (remainder < 10) {
                "${duration / 60}:0$remainder"
            } else {
                "${duration / 60}:$remainder"
            }
        }
    }
}

class NewsConstant {

    companion object {
        const val BASE_URL = "https://is.snssdk.com/"
    }
}