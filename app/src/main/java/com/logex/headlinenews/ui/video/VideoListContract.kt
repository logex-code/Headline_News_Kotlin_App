package com.logex.headlinenews.ui.video

import com.logex.headlinenews.base.BaseView
import com.logex.headlinenews.model.NewsListEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/2/25
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
interface VideoListContract {

    interface VideoListView : BaseView {

        fun getVideoListSuccess(data: List<NewsListEntity.Content>)

        fun getVideoListFailure(errInfo: String?)
    }

    interface VideoListPresenter{

        fun getVideoList(category: String?, tt_from: String?)
    }
}