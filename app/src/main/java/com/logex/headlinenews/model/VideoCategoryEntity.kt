package com.logex.headlinenews.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 视频分类实体
 */
@Parcelize
data class VideoCategoryEntity(
        val category: String?,
        val category_type: Int?,
        val flags: Int?,
        val icon_url: String?,
        val name: String?,
        val tip_new: Int?,
        val type: Int?,
        val web_url: String?
) : Parcelable