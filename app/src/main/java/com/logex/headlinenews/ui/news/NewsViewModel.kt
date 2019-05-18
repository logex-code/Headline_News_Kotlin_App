package com.logex.headlinenews.ui.news

import android.content.Context
import com.logex.headlinenews.base.BaseViewModel
import com.logex.headlinenews.base.LiveData
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
 * NewsViewModel
 **/
class NewsViewModel(context: Context) : BaseViewModel(context) {
    var newsListData: LiveData<List<NewsListEntity>>? = null
        get() {
            if (field == null) {
                newsListData = LiveData()
            }
            return field
        }

    var newsDetailData: LiveData<NewsDetailEntity>? = null
        get() {
            if (field == null) {
                newsDetailData = LiveData()
            }
            return field
        }

    var newsCommentData: LiveData<List<NewsCommentEntity>>? = null
        get() {
            if (field == null) {
                newsCommentData = LiveData()
            }
            return field
        }

    fun getHomeNewsList(category: String?, count: Int, lastTime: Long) {
        HttpFactory.create()?.getHomeNewsList(category, count, lastTime, System.currentTimeMillis())
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

                        newsListData?.setValue(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        errorData.setValue(errInfo)
                    }

                })
    }

    fun getNewsDetail(url: String?) {
        val disposable = HttpFactory.create()?.getNewsDetail(url)
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<NewsDetailEntity>() {
                    override fun onHandleSuccess(data: NewsDetailEntity?, isCache: Boolean) {
                        newsDetailData?.setValue(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        errorData.setValue(errInfo)
                    }

                })
        addSubscribe(disposable)
    }

    fun getComment(groupId: String?, itemId: String?, offset: Int, count: Int) {
        val disposable = HttpFactory.create()?.getComment(groupId, itemId, offset, count)
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<List<NewsCommentEntity>>() {
                    override fun onHandleSuccess(data: List<NewsCommentEntity>?, isCache: Boolean) {
                        newsCommentData?.setValue(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        errorData.setValue(errInfo)
                    }

                })
        addSubscribe(disposable)
    }
}