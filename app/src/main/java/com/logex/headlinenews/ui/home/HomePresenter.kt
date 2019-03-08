package com.logex.headlinenews.ui.home

import android.content.Context
import com.logex.headlinenews.base.BaseViewPresenter
import com.logex.headlinenews.http.HttpFactory
import com.logex.headlinenews.http.HttpObserver
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.model.HomeNewsSubscribed
import com.logex.headlinenews.model.HomeSearchSuggest
import com.logex.headlinenews.model.SubscribedRecommend

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
class HomePresenter(context: Context, view: HomeContract.HomeView) : BaseViewPresenter<HomeContract.HomeView>(context, view), HomeContract.HomePresenter {

    override fun getSubscribedRecommendList() {
        HttpFactory.create()?.getSubscribedRecommendList()
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<SubscribedRecommend>() {
                    override fun onHandleSuccess(data: SubscribedRecommend?) {
                        mView?.getSubscribedRecommendListSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        mView?.getSubscribedRecommendListFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
    }

    override fun getHomeNewsSearchSuggest() {
        HttpFactory.create()?.getHomeNewsSearchSuggest()
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<HomeSearchSuggest>() {
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
                ?.subscribeWith(object : HttpObserver<HomeNewsSubscribed>() {
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