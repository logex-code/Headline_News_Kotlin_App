package com.logex.headlinenews.base

import com.logex.headlinenews.model.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * http接口
 */
interface BaseHttpApi {

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

    /**
     * 获取新闻列表 视频 列表
     * @param category 分类
     * @param count 加载数量
     * @param lastTime 最后时间
     * @param currentTime 当前时间
     */
    @GET("api/news/feed/v54/")
    fun getHomeNewsList(@Query("category") category: String?,
                        @Query("count") count: Int,
                        @Query("min_behot_time") lastTime: Long,
                        @Query("last_refresh_sub_entrance_interval") currentTime: Long): Observable<HttpResult<List<NewsListEntity>>>

    /**
     * 获取视频分类
     */
    @GET("video_api/get_category/v1/")
    fun getVideoCategoryList(): Observable<HttpResult<ArrayList<VideoCategoryEntity>>>

    /**
     * 获取新闻详情
     */
    @GET
    fun getNewsDetail(@Url url: String?): Observable<HttpResult<NewsDetailEntity>>

    /**
     * 获取评论列表数据
     *
     * @param groupId
     * @param itemId
     * @param offset 区间 如0-20 20-40
     * @param count
     * @return
     */
    @GET("article/v2/tab_comments/")
    fun getComment(@Query("group_id") groupId: String?,
                   @Query("item_id") itemId: String?,
                   @Query("offset") offset: Int,
                   @Query("count") count: Int): Observable<HttpResult<List<NewsCommentEntity>>>
}