package com.logex.headlinenews.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 创建人: liguangxi
 * 日期: 2018/8/1
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 频道实体
 */
@Parcelize
data class SubscribedEntity(
        val category: String?,
        val web_url: String?,
        val flags: Int?,
        val name: String?,
        val tip_new: Int?,
        val default_add: Int?,
        val concern_id: String?,
        val type: Int?,
        val icon_url: String?
) : Parcelable