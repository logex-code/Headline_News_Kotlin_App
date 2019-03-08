package com.logex.headlinenews.http

import com.logex.headlinenews.cache.CacheManager
import com.logex.headlinenews.model.HttpResult
import com.logex.utils.GsonUtil

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * 创建人: liguangxi
 * 日期: 2017/12/30
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 封装Observer实现网络请求回调
 */
abstract class HttpObserver<T> : Observer<HttpResult<T>>, Disposable {
    protected var disposable: Disposable? = null
    private var useCache = false // 是否使用缓存
    private var key: String? = null // 缓存key

    constructor()

    constructor(useCache: Boolean, key: String) {
        this.useCache = useCache
        this.key = key
    }

    override fun onSubscribe(d: Disposable) {
        this.disposable = d
    }

    override fun onNext(result: HttpResult<T>?) {
        if (useCache) {
            val json = GsonUtil.getInstance().toJson(result)
            CacheManager.getInstance().putCache(key, json)
        }
        if (result == null) {
            onHandleError("返回内容为空")
            return
        }
        if (result.isSuccess()) {
            onHandleSuccess(result.data, result.isGetCache)
        } else {
            onHandleError(result)
        }
    }

    override fun onError(e: Throwable) = onFailure(e)

    override fun onComplete() = Unit

    override fun isDisposed(): Boolean = disposable!!.isDisposed

    override fun dispose() = disposable!!.dispose()

    /**
     * 获取数据成功或提交成功
     *
     * @param data 数据
     */
    abstract fun onHandleSuccess(data: T?)

    /**
     * 获取数据失败或提交失败
     *
     * @param errInfo 服务器返回的错误信息
     */
    abstract fun onHandleError(errInfo: String?)

    abstract fun onFailure(e: Throwable)

    fun onHandleSuccess(data: T?, isGetCache: Boolean?) = onHandleSuccess(data)

    fun onHandleError(result: HttpResult<T>) = onHandleError(result.message)
}
