package com.logex.headlinenews.base

import com.logex.headlinenews.model.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * http接口
 */
interface BaseHttpApi {
    @Headers("Content-Type:application/x-www-form-urlencoded")

    /**
     * 首页导航栏搜索接口
     */
    @GET("search/suggest/homepage_suggest/")
    fun getHomeNewsSearchSuggest(): Observable<HttpResult<HomeSearchSuggest>>

    /**
     * 首页顶部标题接口
     */
    @GET("article/category/get_subscribed/v1/")
    fun getHomeNewsSubscribedList(): Observable<HttpResult<HomeNewsSubscribed>>

    @GET("api/news/feed/v75/")
    fun getHomeNewsList(@Query("category") category: String?,
                        @Query("tt_from") tt_from: String?): Observable<HttpResult<List<NewsListEntity>>>

    @GET("video_api/get_category/v1/")
    fun getVideoCategoryList(): Observable<HttpResult<List<VideoCategoryEntity>>>
}