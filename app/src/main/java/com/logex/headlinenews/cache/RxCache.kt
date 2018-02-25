package com.logex.headlinenews.cache

import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.model.HttpResult
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import io.reactivex.Observable
import java.lang.reflect.Type

/**
 * Created by liguangxi on 17-5-23.
 * Rx缓存(缓存机制，先加载磁盘再请求网络刷新数据保存在磁盘)
 */
class RxCache {

    companion object {

        fun <T> load(key: String, type: Type, fromNetwork: Observable<HttpResult<T>>): Observable<HttpResult<T>> =
                Observable.concat(loadFromDisk<T>(key, type).compose(RxSchedulers.io_main()),
                        fromNetwork.compose(RxSchedulers.io_main()))

        private fun <T> loadFromDisk(key: String, type: Type): Observable<HttpResult<T>> =
                Observable.create {
                    val cache = CacheManager.getInstance().getCache(key)
                    val t = jsonToObj<T>(cache, type)
                    if (it.isDisposed)
                        if (t != null) {
                            t.isGetCache = true
                            it.onNext(t)
                        }
                    it.onComplete()
                }

        private fun <T> jsonToObj(json: String?, type: Type): HttpResult<T>? {
            LogUtil.i("缓存转对象>>>>>>>" + json!!)
            try {
                return GsonUtil.getInstance().fromJson<HttpResult<T>>(json, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }
}
