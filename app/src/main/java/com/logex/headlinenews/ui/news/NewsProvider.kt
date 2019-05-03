package com.logex.headlinenews.ui.news

import com.logex.headlinenews.base.BaseProvider
import com.logex.headlinenews.base.Callback
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.http.HttpFactory
import com.logex.headlinenews.http.HttpObserver
import com.logex.headlinenews.model.HttpResult
import com.logex.headlinenews.model.NewsCommentEntity
import com.logex.headlinenews.model.NewsDetailEntity
import com.logex.headlinenews.model.NewsListEntity
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil

/**
 * 创建人: liguangxi
 * 日期: 2019/5/3
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * NewsProvider
 **/
class NewsProvider : BaseProvider() {

    fun getHomeNewsList(category: String?, count: Int, lastTime: Long, currentTime: Long,
                        callback: Callback<List<NewsListEntity>>?) {
        HttpFactory.create()?.getHomeNewsList(category, count, lastTime, currentTime)
                ?.map { result ->
                    val newsList: List<Any>? = result.data
                    val newResult = HttpResult<List<NewsListEntity>>(
                            result.message,
                            result.success,
                            null,
                            result.isCache,
                            result.tips
                    )

                    if (newsList != null) {
                        val contentList = arrayListOf<NewsListEntity>()
                        for (item in newsList) {
                            if (item is Map<*, *>) {
                                val json = item["content"]
                                if (json is String) {
                                    val content: NewsListEntity = GsonUtil.getInstance().fromJson(json, NewsListEntity::class.java)
                                    if (lastTime.toInt() == 0 || "置顶" != content.label) {
                                        contentList.add(content)
                                    }
                                }
                            }
                        }

                        newResult.data = contentList
                    }

                    newResult
                }
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<List<NewsListEntity>>() {
                    override fun onHandleSuccess(data: List<NewsListEntity>?, isCache: Boolean) {
                        LogUtil.i("获取新闻列表成功数量>>>>>>" + data?.size)

                        callback?.onSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        callback?.onFailure(errInfo)
                    }

                })
    }

    fun getNewsDetail(url: String?, callback: Callback<NewsDetailEntity>?) {
        val disposable = HttpFactory.create()?.getNewsDetail(url)
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<NewsDetailEntity>() {
                    override fun onHandleSuccess(data: NewsDetailEntity?, isCache: Boolean) {
                        callback?.onSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        callback?.onFailure(errInfo)
                    }

                })
        addSubscribe(disposable)
    }

    fun getComment(groupId: String?, itemId: String?, offset: Int, count: Int,
                   callback: Callback<List<NewsCommentEntity>>?) {
        val disposable = HttpFactory.create()?.getComment(groupId, itemId, offset, count)
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<List<NewsCommentEntity>>() {
                    override fun onHandleSuccess(data: List<NewsCommentEntity>?, isCache: Boolean) {
                        callback?.onSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        callback?.onFailure(errInfo)
                    }

                })
        addSubscribe(disposable)
    }
}