package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 日期: 2018/3/2
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻详情
 */
data class NewsDetailEntity(
        val detail_source: String?,
        val media_user: MediaUser?,
        val publish_time: Long?,
        val title: String?,
        val url: String?,
        val is_original: Boolean?,
        val is_pgc_article: Boolean?,
        val content: String?,
        val source: String?,
        val video_play_count: Int,
        val comment_count: Int?,
        val creator_uid: Long?
) {

    data class MediaUser(
            val no_display_pgc_icon: Boolean,
            val avatar_url: String?,
            val id: String?,
            val screen_name: String?)
}