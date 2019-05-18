package com.logex.headlinenews.ui.home

import android.content.Context
import com.logex.headlinenews.base.BaseViewModel
import com.logex.headlinenews.base.LiveData
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.http.HttpFactory
import com.logex.headlinenews.http.HttpObserver
import com.logex.headlinenews.model.HttpResult
import com.logex.headlinenews.model.NewsListEntity
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil

/**
 * 创建人: liguangxi
 * 日期: 2019/5/3
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * MicroNewsViewModel
 **/
class MicroNewsViewModel(context: Context) : BaseViewModel(context) {
    var microNewsListData: LiveData<List<NewsListEntity>>? = null
        get() {
            if (field == null) {
                microNewsListData = LiveData()
            }
            return field
        }

    fun getMicroNewsList(category: String?, count: Int, lastTime: Long) {
        HttpFactory.create()?.getHomeNewsList(category, count, lastTime, System.currentTimeMillis())
                ?.compose(RxSchedulers.io_main())
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
                                    contentList.add(content)
                                }
                            }
                        }

                        newResult.data = contentList
                    }

                    newResult
                }
                ?.subscribeWith(object : HttpObserver<List<NewsListEntity>>() {
                    override fun onHandleSuccess(data: List<NewsListEntity>?, isCache: Boolean) {
                        LogUtil.i("获取微头条列表成功数量>>>>>>" + data?.size)

                        microNewsListData?.setValue(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        errorData.setValue(errInfo)
                    }

                })
    }
}