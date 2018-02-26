package com.logex.headlinenews.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
data class HomeNewsSubscribed(var version: String?, var data: ArrayList<SubscribedBean>) {

    data class SubscribedBean(var category: String?, var web_url: String?, var flags: Int?, var name: String?,
                              var tip_new: Int?, var default_add: Int?, var concern_id: String?, var type: Int?,
                              var icon_url: String?) : Parcelable {

        constructor(parcel: Parcel) : this(
                parcel.readString(),
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString(),
                parcel.readValue(Int::class.java.classLoader) as? Int,
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(category)
            parcel.writeString(web_url)
            parcel.writeValue(flags)
            parcel.writeString(name)
            parcel.writeValue(tip_new)
            parcel.writeValue(default_add)
            parcel.writeString(concern_id)
            parcel.writeValue(type)
            parcel.writeString(icon_url)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<HomeNewsSubscribed.SubscribedBean> {
            override fun createFromParcel(parcel: Parcel): HomeNewsSubscribed.SubscribedBean = SubscribedBean(parcel)

            override fun newArray(size: Int): Array<HomeNewsSubscribed.SubscribedBean?> = arrayOfNulls(size)
        }
    }
}