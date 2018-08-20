package com.logex.headlinenews.ui.home

import com.logex.headlinenews.base.BaseView
import com.logex.headlinenews.model.NewsListEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 */
interface MicroNewsContract {

    interface MicroNewsView : BaseView {

        fun getMicroNewsListSuccess(data: List<NewsListEntity.Content>)

        fun getMicroNewsListFailure(errInfo: String?)
    }

    interface MicroNewsPresenter {

        fun getMicroNewsList(category: String?, count: Int, lastTime: Long, currentTime: Long)
    }
}