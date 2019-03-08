package com.logex.headlinenews.ui.home

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
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 */
class MicroNewsPresenter(context: Context, view: MicroNewsContract.MicroNewsView) : BaseViewPresenter<MicroNewsContract.MicroNewsView>(context, view), MicroNewsContract.MicroNewsPresenter {

    override fun getMicroNewsList(category: String?, count: Int, lastTime: Long, currentTime: Long) {
        HttpFactory.create()?.getHomeNewsList(category, count, lastTime, currentTime)
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
                        mView?.getMicroNewsListSuccess(contentList)
                    }
                }
                ?.subscribeWith(object : HttpObserver<List<NewsListEntity>>() {
                    override fun onHandleSuccess(data: List<NewsListEntity>?) =
                            LogUtil.i("获取微头条列表成功数量>>>>>>" + data?.size)

                    override fun onHandleError(errInfo: String?) {
                        mView?.getMicroNewsListFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
    }
}