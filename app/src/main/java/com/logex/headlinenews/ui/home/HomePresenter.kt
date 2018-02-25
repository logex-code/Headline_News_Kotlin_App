package com.logex.headlinenews.ui.home

import android.content.Context
import com.logex.headlinenews.base.BaseViewPresenter
import com.logex.headlinenews.base.HttpFactory
import com.logex.headlinenews.base.NewsObserver
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.model.HomeNewsSubscribed
import com.logex.headlinenews.model.HomeSearchSuggest

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
class HomePresenter(context: Context, view: HomeContract.HomeView) : BaseViewPresenter<HomeContract.HomeView>(context, view), HomeContract.HomePresenter {

    override fun getHomeNewsSearchSuggest() {
        HttpFactory.create()?.getHomeNewsSearchSuggest()
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : NewsObserver<HomeSearchSuggest>() {
                    override fun onHandleSuccess(data: HomeSearchSuggest?) {
                        mView?.getHomeNewsSearchSuggestSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        mView?.getHomeNewsSearchSuggestFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
    }

    override fun getHomeNewsSubscribedList() {
        HttpFactory.create()?.getHomeNewsSubscribedList()
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : NewsObserver<HomeNewsSubscribed>() {
                    override fun onHandleSuccess(data: HomeNewsSubscribed?) {
                        mView?.getHomeNewsSubscribedListSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        mView?.getHomeNewsSubscribedListFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
    }

}