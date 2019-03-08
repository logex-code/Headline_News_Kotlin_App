package com.logex.headlinenews.ui.news

import android.content.Context
import com.logex.headlinenews.base.BaseViewPresenter
import com.logex.headlinenews.http.HttpFactory
import com.logex.headlinenews.http.HttpObserver
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.model.NewsCommentEntity
import com.logex.headlinenews.model.NewsDetailEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/3/2
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
class NewsDetailPresenter(context: Context, view: NewsDetailContract.NewsDetailView) : BaseViewPresenter<NewsDetailContract.NewsDetailView>(context, view), NewsDetailContract.NewsDetailPresenter {

    override fun getNewsDetail(url: String?) {
        val disposable = HttpFactory.create()?.getNewsDetail(url)
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<NewsDetailEntity>() {
                    override fun onHandleSuccess(data: NewsDetailEntity?) {
                        mView?.getNewsDetailSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        mView?.getNewsDetailFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
        addDisposable(disposable)
    }

    override fun getComment(groupId: String?, itemId: String?, offset: Int, count: Int) {
        val disposable = HttpFactory.create()?.getComment(groupId, itemId, offset, count)
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<List<NewsCommentEntity>>() {
                    override fun onHandleSuccess(data: List<NewsCommentEntity>?) {
                        mView?.getCommentSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        mView?.getCommentFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
        addDisposable(disposable)
    }
}