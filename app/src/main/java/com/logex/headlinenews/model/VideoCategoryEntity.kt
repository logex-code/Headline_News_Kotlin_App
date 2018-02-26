package com.logex.headlinenews.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
data class VideoCategoryEntity(var category: String?, var name: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoCategoryEntity> {
        override fun createFromParcel(parcel: Parcel): VideoCategoryEntity {
            return VideoCategoryEntity(parcel)
        }

        override fun newArray(size: Int): Array<VideoCategoryEntity?> {
            return arrayOfNulls(size)
        }
    }
}