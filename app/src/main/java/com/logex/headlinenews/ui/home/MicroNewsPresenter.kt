package com.logex.headlinenews.ui.home

import android.content.Context
import com.logex.headlinenews.base.BaseViewPresenter
import com.logex.headlinenews.base.HttpFactory
import com.logex.headlinenews.base.NewsObserver
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.model.DynamicEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 */
class MicroNewsPresenter(context: Context, view: MicroNewsContract.MicroNewsView) : BaseViewPresenter<MicroNewsContract.MicroNewsView>(context, view), MicroNewsContract.MicroNewsPresenter {

    override fun getDynamicList(userId: String?, count: Int) {
        HttpFactory.create()?.getDynamicList(userId, count)
                ?.compose(RxSchedulers.io_main())
                ?.subscribeWith(object : NewsObserver<DynamicEntity>() {
                    override fun onHandleSuccess(data: DynamicEntity?) {
                        mView?.getDynamicListSuccess(data)
                    }

                    override fun onHandleError(errInfo: String?) {
                        mView?.getDynamicListFailure(errInfo)
                    }

                    override fun onFailure(e: Throwable) = onRequestFailure(e)

                })
    }
}