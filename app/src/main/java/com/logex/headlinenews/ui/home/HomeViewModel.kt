package com.logex.headlinenews.ui.home

import android.content.Context
import com.logex.headlinenews.base.BaseViewModel
import com.logex.headlinenews.base.Callback
import com.logex.headlinenews.model.HomeNewsSubscribed
import com.logex.headlinenews.model.HomeSearchSuggest
import com.logex.headlinenews.model.SubscribedRecommend

/**
 * 创建人: liguangxi
 * 日期: 2019/5/3
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * HomeViewModel
 **/
class HomeViewModel(context: Context) : BaseViewModel<HomeProvider>(context) {

    companion object {
        /**
         * 获取订阅推荐
         */
        const val FETCH_SUBSCRIBED_RECOMMEND = 1
        /**
         * 获取搜索推荐
         */
        const val FETCH_SEARCH_SUGGEST = 2
        /**
         * 获取新闻频道列表
         */
        const val FETCH_NEWS_SUBSCRIBED = 3
    }

    override fun createProvider(): HomeProvider = HomeProvider()

    fun getSubscribedRecommendList() {
        mProvider?.getSubscribedRecommendList(object : Callback<SubscribedRecommend> {
            override fun onSuccess(t: SubscribedRecommend?) {
                notifyDataChange(FETCH_SUBSCRIBED_RECOMMEND, t)
            }

            override fun onFailure(errInfo: String?) {
                notifyFailure(FETCH_SUBSCRIBED_RECOMMEND, errInfo)
            }
        })
    }

    fun getHomeNewsSearchSuggest() {
        mProvider?.getHomeNewsSearchSuggest(object : Callback<HomeSearchSuggest> {
            override fun onSuccess(t: HomeSearchSuggest?) {
                notifyDataChange(FETCH_SEARCH_SUGGEST, t)
            }

            override fun onFailure(errInfo: String?) {
                notifyFailure(FETCH_SEARCH_SUGGEST, errInfo)
            }
        })
    }

    fun getHomeNewsSubscribedList() {
        mProvider?.getHomeNewsSubscribedList(object : Callback<HomeNewsSubscribed> {
            override fun onSuccess(t: HomeNewsSubscribed?) {
                notifyDataChange(FETCH_NEWS_SUBSCRIBED, t)
            }

            override fun onFailure(errInfo: String?) {
                notifyFailure(FETCH_NEWS_SUBSCRIBED, errInfo)
            }
        })
    }
}