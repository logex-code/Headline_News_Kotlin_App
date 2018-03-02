package com.logex.headlinenews.base

import android.content.Context
import android.support.annotation.StringRes
import com.google.gson.JsonParseException
import com.logex.headlinenews.R
import com.logex.utils.NetworkUtil
import com.logex.utils.UIUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.json.JSONException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
abstract class BaseViewPresenter<T : BaseView>(context: Context, view: T) {
    protected var mView: T? = view
    protected var mContext: Context? = context
    protected var compositeDisposable: CompositeDisposable? = null // 管理订阅者者

    /**
     * 销毁view
     */
    fun detachView() {
        compositeDisposable?.dispose()
        compositeDisposable?.clear()
        this.mView = null
        this.mContext = null
        this.compositeDisposable = null
    }

    /**
     * 请求失败处理
     *
     * @param e 请求异常
     */
    protected fun onRequestFailure(e: Throwable) {
        if (mView == null) return
        if (NetworkUtil.isNetWorkConnected(mContext)) {
            mView?.onServerFailure()
            if (e is SocketTimeoutException) {
                UIUtils.showToast(mContext, getString(R.string.message_server_timeout))
            } else if (e is JsonParseException || e is JSONException) {
                UIUtils.showToast(mContext, getString(R.string.message_data_unavailable))
            } else if (e is UnknownHostException) {
                UIUtils.showToast(mContext, getString(R.string.message_network_un_smooth))
            } else if (e is HttpException) {
                UIUtils.showToast(mContext, "HTTP ${e.code()} ${e.message()}")
            } else {
                UIUtils.showToast(mContext, getString(R.string.message_server_unavailable))
            }
        } else {
            mView?.onNetworkFailure()
        }
    }

    protected fun getString(@StringRes strId: Int): String? = mContext?.resources?.getString(strId)

    /**
     * 添加订阅
     *
     * @param disposable 订阅者
     */
    protected fun addDisposable(disposable: Disposable?) {
        if (disposable == null) return
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(disposable)
    }
}