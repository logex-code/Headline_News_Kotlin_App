package com.logex.headlinenews.ui.home

import com.logex.headlinenews.base.BaseProvider
import com.logex.headlinenews.base.Callback
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.http.HttpFactory
import com.logex.headlinenews.http.HttpObserver
import com.logex.headlinenews.model.HomeNewsSubscribed
import com.logex.headlinenews.model.HomeSearchSuggest
import com.logex.headlinenews.model.SubscribedEntity
import com.logex.headlinenews.model.SubscribedRecommend

/**
 * 创建人: liguangxi
 * 日期: 2019/5/3
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * HomeProvider
 **/
class HomeProvider : BaseProvider() {

    fun getSubscribedRecommendList(callback: Callback<SubscribedRecommend>?) {
        HttpFactory.create()?.getSubscribedRecommendList()
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<SubscribedRecommend>() {
                    override fun onHandleSuccess(data: SubscribedRecommend?, isCache: Boolean) {
                        callback?.onSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        callback?.onFailure(errInfo)
                    }

                })
    }

    fun getHomeNewsSearchSuggest(callback: Callback<HomeSearchSuggest>?) {
        HttpFactory.create()?.getHomeNewsSearchSuggest()
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<HomeSearchSuggest>() {
                    override fun onHandleSuccess(data: HomeSearchSuggest?, isCache: Boolean) {
                        callback?.onSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        callback?.onFailure(errInfo)
                    }

                })
    }

    fun getHomeNewsSubscribedList(callback: Callback<HomeNewsSubscribed>?) {
        HttpFactory.create()?.getHomeNewsSubscribedList()
                ?.doOnNext({ result ->
                    val data = result.data
                    val list = data?.data
                    list?.add(0, SubscribedEntity(
                            null,
                            null,
                            null,
                            "推荐",
                            null,
                            null,
                            null,
                            null,
                            null))
                })
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<HomeNewsSubscribed>() {
                    override fun onHandleSuccess(data: HomeNewsSubscribed?, isCache: Boolean) {
                        callback?.onSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        callback?.onFailure(errInfo)
                    }

                })
    }
}