package com.logex.headlinenews.ui.video

import android.content.Context
import com.logex.headlinenews.base.BaseViewModel
import com.logex.headlinenews.base.LiveData
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.http.HttpFactory
import com.logex.headlinenews.http.HttpObserver
import com.logex.headlinenews.model.HttpResult
import com.logex.headlinenews.model.NewsListEntity
import com.logex.headlinenews.model.VideoCategoryEntity
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil

/**
 * 创建人: liguangxi
 * 日期: 2019/5/3
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * VideoViewModel
 **/
class VideoViewModel(context: Context) : BaseViewModel(context) {
    var videoCategoryData: LiveData<ArrayList<VideoCategoryEntity>>? = null
        get() {
            if (field == null) {
                videoCategoryData = LiveData()
            }
            return field
        }

    var videoListData: LiveData<List<NewsListEntity>>? = null
        get() {
            if (field == null) {
                videoListData = LiveData()
            }
            return field
        }

    fun getVideoCategoryList() {
        HttpFactory.create()?.getVideoCategoryList()
                ?.doOnNext({ result ->
                    val list = result.data
                    list?.add(0, VideoCategoryEntity(
                            "video",
                            null,
                            null,
                            null,
                            "推荐",
                            null,
                            null,
                            null))
                })
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<java.util.ArrayList<VideoCategoryEntity>>() {
                    override fun onHandleSuccess(data: java.util.ArrayList<VideoCategoryEntity>?, isCache: Boolean) {
                        videoCategoryData?.setValue(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        errorData?.setValue(errInfo)
                    }
                })
    }

    fun getVideoList(category: String?, count: Int, lastTime: Long) {
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
                                    contentList.add(content)
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
                        LogUtil.i("获取视频列表成功数量>>>>>>" + data?.size)

                        videoListData?.setValue(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        errorData?.setValue(errInfo)
                    }

                })
    }
}