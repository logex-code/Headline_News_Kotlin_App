package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 时间: 18-8-20
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 视频解析内容
 */
data class VideoPathEntity(val status: Int,
                           val user_id: String,
                           val video_id: String,
                           val validate: String,
                           val enable_ssl: Boolean,
                           val poster_url: String,
                           val video_duration: Double,
                           val media_type: String,
                           val auto_definition: String,
                           val video_list: VideoList?,
                           val dynamic_video: Any?
) {

    data class VideoList(
            val video_1: Video1?
    )

    data class Video1(
            val definition: String,
            val vtype: String,
            val vwidth: Int,
            val vheight: Int,
            val bitrate: Int,
            val size: Int,
            val quality: String,
            val codec_type: String,
            val logo_type: String,
            val encrypt: Boolean,
            val file_hash: String,
            val main_url: String?,
            val backup_url_1: String,
            val user_video_proxy: Int,
            val socket_buffer: Int,
            val preload_size: Int,
            val preload_interval: Int,
            val preload_min_step: Int,
            val preload_max_step: Int,
            val spade_a: String
    )
}