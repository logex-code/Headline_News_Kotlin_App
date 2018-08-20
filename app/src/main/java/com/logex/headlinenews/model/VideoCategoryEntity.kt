package com.logex.headlinenews.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
data class VideoCategoryEntity(
        val category: String?,
        val category_type: Int?,
        val flags: Int?,
        val icon_url: String?,
        val name: String?,
        val tip_new: Int?,
        val type: Int?,
        val web_url: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeValue(category_type)
        parcel.writeValue(this.flags)
        parcel.writeString(icon_url)
        parcel.writeString(name)
        parcel.writeValue(tip_new)
        parcel.writeValue(type)
        parcel.writeString(web_url)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<VideoCategoryEntity> {
        override fun createFromParcel(parcel: Parcel): VideoCategoryEntity = VideoCategoryEntity(parcel)

        override fun newArray(size: Int): Array<VideoCategoryEntity?> = arrayOfNulls(size)
    }
}