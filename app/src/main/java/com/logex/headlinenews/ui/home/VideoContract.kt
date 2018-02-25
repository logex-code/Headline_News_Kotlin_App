package com.logex.headlinenews.ui.home

import com.logex.headlinenews.base.BaseView
import com.logex.headlinenews.model.VideoCategoryEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
interface VideoContract {

    interface VideoView : BaseView {

        fun getVideoCategoryListSuccess(data: List<VideoCategoryEntity>?)

        fun getVideoCategoryListFailure(errInfo: String?)
    }

    interface VideoPresenter {

        fun getVideoCategoryList()
    }
}