package com.logex.headlinenews.ui.news

import com.logex.headlinenews.base.BaseView
import com.logex.headlinenews.model.NewsListEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
interface NewsListContract {

    interface NewsListView : BaseView {

        fun getHomeNewsListSuccess(data: List<NewsListEntity.Content>)

        fun getHomeNewsListFailure(errInfo: String?)
    }

    interface NewsListPresenter {

        fun getHomeNewsList(category: String?, count: Int, lastTime: Long, currentTime: Long)
    }
}