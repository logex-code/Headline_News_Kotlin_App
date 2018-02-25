package com.logex.headlinenews.ui.news

import android.content.Context
import com.logex.headlinenews.base.BaseViewPresenter
import com.logex.headlinenews.base.HttpFactory
import com.logex.headlinenews.base.NewsObserver
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.model.NewsListEntity
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
class NewsListPresenter(context: Context, view: NewsListContract.NewsListView) : BaseViewPresenter<NewsListContract.NewsListView>(context, view), NewsListContract.NewsListPresenter {

    override fun getHomeNewsList(category: String?, tt_from: String?) {
        HttpFactory.create()?.getHomeNewsList(category, tt_from)
                ?.compose(RxSchedulers.io_main())
                ?.doOnNext {
                    val newsList: List<NewsListEntity>? = it.data

                    if (newsList != null) {

                        val contentList = arrayListOf<NewsListEntity.Content>()

                        for (item in newsList) {
                            val json = item.content ?: continue

                            try {
                                val content: NewsListEntity.Content = GsonUtil.getInstance().fromJson(json, NewsListEntity.Content::class.java)
                                contentList.add(content)
                            } finally {
                                continue
                            }
                        }
                        mView?.getHomeNewsListSuccess(contentList)
                    }
                }
                ?.subscribeWith(object : NewsObserver<List<NewsListEntity>>() {
                    override fun onHandleSuccess(data: List<NewsListEntity>?) =
                            LogUtil.i("获取新闻列表成功数量>>>>>>" + data?.size)

                    override fun onHandleError(errInfo: String?) {
                        mView?.getHomeNewsListFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
    }

}