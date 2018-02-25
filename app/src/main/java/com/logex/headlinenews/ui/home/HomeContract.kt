package com.logex.headlinenews.ui.home

import com.logex.headlinenews.base.BaseView
import com.logex.headlinenews.model.HomeNewsSubscribed
import com.logex.headlinenews.model.HomeSearchSuggest

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
interface HomeContract {

    interface HomeView : BaseView {

        fun getHomeNewsSearchSuggestSuccess(data: HomeSearchSuggest?)

        fun getHomeNewsSearchSuggestFailure(errInfo: String?)

        fun getHomeNewsSubscribedListSuccess(data: HomeNewsSubscribed?)

        fun getHomeNewsSubscribedListFailure(errInfo: String?)
    }

    interface HomePresenter {

        fun getHomeNewsSearchSuggest()

        fun getHomeNewsSubscribedList()
    }
}