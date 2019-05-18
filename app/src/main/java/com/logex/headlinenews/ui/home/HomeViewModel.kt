package com.logex.headlinenews.ui.home

import android.content.Context
import com.logex.headlinenews.base.BaseViewModel
import com.logex.headlinenews.base.LiveData
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
 * HomeViewModel
 **/
class HomeViewModel(context: Context) : BaseViewModel(context) {
    var subscribedRecommendData: LiveData<SubscribedRecommend>? = null
        get() {
            if (field == null) {
                subscribedRecommendData = LiveData()
            }
            return field
        }

    var newsSearchSuggestData: LiveData<HomeSearchSuggest>? = null
        get() {
            if (field == null) {
                newsSearchSuggestData = LiveData()
            }
            return field
        }

    var newsSubscribedListData: LiveData<HomeNewsSubscribed>? = null
        get() {
            if (field == null) {
                newsSubscribedListData = LiveData()
            }
            return field
        }

    fun getSubscribedRecommendList() {
        HttpFactory.create()?.getSubscribedRecommendList()
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<SubscribedRecommend>() {
                    override fun onHandleSuccess(data: SubscribedRecommend?, isCache: Boolean) {
                        subscribedRecommendData?.setValue(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        errorData.setValue(errInfo)
                    }

                })
    }

    fun getHomeNewsSearchSuggest() {
        HttpFactory.create()?.getHomeNewsSearchSuggest()
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : HttpObserver<HomeSearchSuggest>() {
                    override fun onHandleSuccess(data: HomeSearchSuggest?, isCache: Boolean) {
                        newsSearchSuggestData?.setValue(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        errorData?.setValue(errInfo)
                    }

                })
    }

    fun getHomeNewsSubscribedList() {
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
                        newsSubscribedListData?.setValue(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        errorData?.setValue(errInfo)
                    }

                })
    }
}