package com.logex.headlinenews.ui.home

import android.content.Context
import com.logex.headlinenews.base.BaseViewPresenter
import com.logex.headlinenews.base.HttpFactory
import com.logex.headlinenews.base.NewsObserver
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.model.VideoCategoryEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
class VideoPresenter(context: Context, view: VideoContract.VideoView) : BaseViewPresenter<VideoContract.VideoView>(context, view), VideoContract.VideoPresenter {

    override fun getVideoCategoryList() {
        HttpFactory.create()?.getVideoCategoryList()
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : NewsObserver<List<VideoCategoryEntity>>() {
                    override fun onHandleSuccess(data: List<VideoCategoryEntity>?) {
                        mView?.getVideoCategoryListSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        mView?.getVideoCategoryListFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
    }
}