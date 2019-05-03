package com.logex.headlinenews.ui.news

import android.content.Context
import com.logex.headlinenews.base.BaseViewModel
import com.logex.headlinenews.base.Callback
import com.logex.headlinenews.model.NewsCommentEntity
import com.logex.headlinenews.model.NewsDetailEntity
import com.logex.headlinenews.model.NewsListEntity

/**
 * 创建人: liguangxi
 * 日期: 2019/5/3
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * NewsViewModel
 **/
class NewsViewModel(context: Context) : BaseViewModel<NewsProvider>(context) {

    companion object {
        /**
         * 获取新闻列表
         */
        const val FETCH_NEWS_LIST = 1
        /**
         * 获取新闻详情
         */
        const val FETCH_NEWS_DETAIL = 2
        /**
         * 获取新闻评论
         */
        const val FETCH_NEWS_COMMENT = 3
    }

    override fun createProvider(): NewsProvider = NewsProvider()

    fun getHomeNewsList(category: String?, count: Int, lastTime: Long) {
        mProvider?.getHomeNewsList(category, count, lastTime, System.currentTimeMillis(),
                object : Callback<List<NewsListEntity>> {
                    override fun onSuccess(t: List<NewsListEntity>?) {
                        notifyDataChange(FETCH_NEWS_LIST, t)
                    }

                    override fun onFailure(errInfo: String?) {
                        notifyFailure(FETCH_NEWS_LIST, errInfo)
                    }
                })
    }

    fun getNewsDetail(url: String?) {
        mProvider?.getNewsDetail(url, object : Callback<NewsDetailEntity> {
            override fun onSuccess(t: NewsDetailEntity?) {
                notifyDataChange(FETCH_NEWS_DETAIL, t)
            }

            override fun onFailure(errInfo: String?) {
                notifyFailure(FETCH_NEWS_DETAIL, errInfo)
            }
        })
    }

    fun getComment(groupId: String?, itemId: String?, offset: Int, count: Int) {
        mProvider?.getComment(groupId, itemId, offset, count, object : Callback<List<NewsCommentEntity>> {
            override fun onSuccess(t: List<NewsCommentEntity>?) {
                notifyDataChange(FETCH_NEWS_COMMENT, t)
            }

            override fun onFailure(errInfo: String?) {
                notifyFailure(FETCH_NEWS_COMMENT, errInfo)
            }
        })
    }
}