package com.logex.headlinenews.ui.video

import android.content.Context
import com.logex.headlinenews.base.BaseViewModel
import com.logex.headlinenews.base.Callback
import com.logex.headlinenews.model.NewsListEntity
import com.logex.headlinenews.model.VideoCategoryEntity

/**
 * 创建人: liguangxi
 * 日期: 2019/5/3
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * VideoViewModel
 **/
class VideoViewModel(context: Context) : BaseViewModel<VideoProvider>(context) {

    companion object {
        /**
         * 获取视频分类
         */
        const val FETCH_VIDEO_CATEGORY = 1
        /**
         * 获取视频新闻
         */
        const val FETCH_VIDEO_NEWS = 2
    }

    override fun createProvider(): VideoProvider = VideoProvider()

    fun getVideoCategoryList() {
        mProvider?.getVideoCategoryList(object : Callback<ArrayList<VideoCategoryEntity>> {
            override fun onSuccess(t: ArrayList<VideoCategoryEntity>?) {
                notifyDataChange(FETCH_VIDEO_CATEGORY, t)
            }

            override fun onFailure(errInfo: String?) {
                notifyFailure(FETCH_VIDEO_CATEGORY, errInfo)
            }
        })
    }

    fun getVideoList(category: String?, count: Int, lastTime: Long) {
        mProvider?.getVideoList(category, count, lastTime, System.currentTimeMillis(),
                object : Callback<List<NewsListEntity>> {
                    override fun onSuccess(t: List<NewsListEntity>?) {
                        notifyDataChange(FETCH_VIDEO_NEWS, t)
                    }

                    override fun onFailure(errInfo: String?) {
                        notifyFailure(FETCH_VIDEO_NEWS, errInfo)
                    }
                })
    }
}