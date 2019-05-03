package com.logex.headlinenews.http

import com.google.gson.JsonParseException
import com.logex.headlinenews.NewsApplication
import com.logex.headlinenews.R
import com.logex.headlinenews.cache.CacheManager
import com.logex.headlinenews.model.HttpResult
import com.logex.utils.GsonUtil
import com.logex.utils.NetworkUtil

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 创建人: liguangxi
 * 日期: 2017/12/30
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 封装Observer实现网络请求回调
 */
abstract class HttpObserver<T> : Observer<HttpResult<T>>, Disposable {
    protected var disposable: Disposable? = null
    private var isCache = false // 是否缓存
    private var key: String? = null // 缓存key

    constructor()

    constructor(cache: Boolean, key: String) {
        this.isCache = cache
        this.key = key
    }

    override fun onSubscribe(d: Disposable) {
        this.disposable = d
    }

    override fun onNext(result: HttpResult<T>?) {
        if (result == null) {
            onHandleError("返回内容为空")
            return
        }
        when {
            result.isSuccess() -> {
                if (isCache) {
                    // 保存缓存
                    val json = GsonUtil.getInstance().toJson(result)
                    CacheManager.getInstance().putCache(key, json)
                }

                onHandleSuccess(result.data, result.isCache)
            }
            else -> onHandleError(result.message)
        }
    }

    override fun onError(e: Throwable) {
        val appContext = NewsApplication.instance
        val errInfo: String?
        if (NetworkUtil.isNetWorkConnected(appContext)) {
            errInfo = if (e is SocketTimeoutException) {
                appContext?.getString(R.string.message_server_timeout)
            } else if (e is JsonParseException || e is JSONException) {
                appContext?.getString(R.string.message_data_unavailable)
            } else if (e is UnknownHostException || e is ConnectException) {
                appContext?.getString(R.string.message_network_un_smooth)
            } else if (e is HttpException) {
                "HTTP ${e.code()} ${e.message()}"
            } else {
                appContext?.getString(R.string.message_server_unavailable)
            }
        } else {
            // 无网络
            errInfo = appContext?.getString(R.string.message_network_unavailable)
        }
        onHandleError(errInfo)
    }

    override fun onComplete() = Unit

    override fun isDisposed(): Boolean = disposable?.isDisposed ?: true

    override fun dispose() = disposable?.dispose() ?: Unit

    /**
     * 获取数据成功或提交成功
     *
     * @param data 数据
     * @param isCache 是否是缓存数据
     */
    abstract fun onHandleSuccess(data: T?, isCache: Boolean)

    /**
     * 获取数据失败或提交失败
     *
     * @param errInfo 服务器返回的错误信息
     */
    abstract fun onHandleError(errInfo: String?)
}
