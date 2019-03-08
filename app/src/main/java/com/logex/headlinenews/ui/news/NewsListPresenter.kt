package com.logex.headlinenews.ui.news

import android.content.Context
import com.logex.headlinenews.base.BaseViewPresenter
import com.logex.headlinenews.http.HttpFactory
import com.logex.headlinenews.http.HttpObserver
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

    override fun getHomeNewsList(category: String?, count: Int, lastTime: Long, currentTime: Long) {
        var newLastTime: Long = lastTime
        //如果为空，则是从来没有刷新过，使用当前时间戳
        if (newLastTime == 0L) {
            newLastTime = currentTime / 1000
        }
        HttpFactory.create()?.getHomeNewsList(category, count, newLastTime, currentTime / 1000)
                ?.compose(RxSchedulers.io_main())
                ?.doOnNext {
                    val newsList: List<NewsListEntity>? = it.data

                    if (newsList != null) {

                        val contentList = arrayListOf<NewsListEntity.Content>()

                        for (item in newsList) {
                            val json = item.content ?: continue

                            try {
                                val content: NewsListEntity.Content = GsonUtil.getInstance()
                                        .fromJson(json, NewsListEntity.Content::class.java)

                                if (lastTime.toInt() != 0 && "置顶" == content.label) {
                                    // 过滤掉加载更多中列表标签为置顶的
                                    LogUtil.i("过滤掉置顶条目....")
                                } else {
                                    contentList.add(content)
                                }
                            } finally {
                                continue
                            }
                        }
                        mView?.getHomeNewsListSuccess(contentList)
                    }
                }
                ?.subscribeWith(object : HttpObserver<List<NewsListEntity>>() {
                    override fun onHandleSuccess(data: List<NewsListEntity>?) =
                            LogUtil.i("获取新闻列表成功数量>>>>>>" + data?.size + "条")

                    override fun onHandleError(errInfo: String?) {
                        mView?.getHomeNewsListFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
    }

}