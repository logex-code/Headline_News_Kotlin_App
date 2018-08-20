package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 日期: 2018/3/2
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻评论
 */
data class NewsCommentEntity(
        val comment: Comment?,
        val cell_type: Int?
) {

    data class Comment(
            val id: Long,
            val text: String,
            val content_rich_span: String,
            val reply_count: Int,
            val reply_list: List<Reply>?,
            val digg_count: Int,
            val bury_count: Int,
            val forward_count: Int,
            val create_time: Long,
            val score: Double,
            val user_id: Long,
            val user_name: String,
            val remark_name: String,
            val user_profile_image_url: String,
            val user_verified: Boolean,
            val interact_style: Int,
            val is_following: Int,
            val is_followed: Int,
            val is_blocking: Int,
            val is_blocked: Int,
            val is_pgc_author: Int,
            val author_badge: List<Any>,
            val verified_reason: String,
            val user_bury: Int,
            val user_digg: Int,
            val user_relation: Int,
            val user_auth_info: String,
            val user_decoration: String,
            val large_image_list: Any?,
            val thumb_image_list: Any?,
            val media_info: MediaInfo?,
            val platform: String,
            val has_author_digg: Int
    )

    /**
     * 评论回复实体
     */
    data class Reply(
            val id: Long,
            val text: String,
            val content_rich_span: String,
            val user_id: Int,
            val user_name: String,
            val user_verified: Boolean,
            val user_auth_info: String,
            val user_profile_image_url: String,
            val is_pgc_author: Int,
            val author_badge: List<AuthorBadge>?,
            val digg_count: Int,
            val user_digg: Int
    )

    data class AuthorBadge(
            val url: String,
            val open_url: String,
            val width: Int,
            val height: Int,
            val url_list: List<Url>?,
            val uri: String
    )

    data class Url(
            val url: String
    )

    data class MediaInfo(
            val name: String,
            val avatar_url: String
    )
}