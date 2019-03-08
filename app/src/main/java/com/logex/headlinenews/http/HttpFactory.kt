package com.logex.headlinenews.http

import com.logex.headlinenews.utils.NewsConstant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * http请求工厂
 */
class HttpFactory {

    companion object {
        private var retrofit: Retrofit? = null
        private var httpApi: HttpApi? = null
        private val client = OkHttpClient.Builder()
                .addInterceptor(HttpInterceptor())
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build()

        fun create(): HttpApi? = synchronized(HttpFactory::class.java) {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(NewsConstant.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(client)
                        .build()

                httpApi = retrofit?.create(HttpApi::class.java)
            }
            return httpApi
        }
    }
}