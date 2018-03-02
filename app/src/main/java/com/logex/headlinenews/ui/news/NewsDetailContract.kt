package com.logex.headlinenews.ui.news

import com.logex.headlinenews.base.BaseView
import com.logex.headlinenews.model.NewsCommentEntity
import com.logex.headlinenews.model.NewsDetailEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/3/2
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
interface NewsDetailContract {

    interface NewsDetailView : BaseView {

        fun getNewsDetailSuccess(data: NewsDetailEntity?)

        fun getNewsDetailFailure(errInfo: String?)

        fun getCommentSuccess(data: List<NewsCommentEntity>?)

        fun getCommentFailure(errInfo: String?)
    }

    interface NewsDetailPresenter {

        /**
         * 获取新闻详情
         */
        fun getNewsDetail(url: String?)

        fun getComment(groupId: String?, itemId: String?, offset: Int, count: Int)
    }
}