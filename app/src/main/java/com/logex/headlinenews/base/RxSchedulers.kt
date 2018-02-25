package com.logex.headlinenews.base

import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * rxjava控制
 */
class RxSchedulers {

    companion object {

        /**
         * 线程切换
         */
        fun <T> io_main(): ObservableTransformer<T, T> =
                ObservableTransformer {
                    it.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }

        fun <T> io_main2(): FlowableTransformer<T, T> =
                FlowableTransformer {
                    it.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }

        fun <T> computation_main(): ObservableTransformer<T, T> =
                ObservableTransformer {
                    it.subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                }
    }
}