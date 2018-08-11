package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 日期: 2018/3/2
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻评论
 */
data class NewsCommentEntity(val comment: Comment?, val cell_type: Int?) {

    data class Comment(val id: String?, val text: String?, val digg_count: Int?, val user_name: String?,
                       val user_profile_image_url: String?, val create_time: Long?)
}