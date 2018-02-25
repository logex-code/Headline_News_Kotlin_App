package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻列表模型
 */
data class NewsListEntity(var content: String?, var code: String?) {

    data class Content(var abstract: String?, var action_list: List<ActionList>, var aggr_type: Int?,
                       var allow_download: Boolean?, var article_alt_url: String?, var article_sub_type: Int?,
                       var article_type: Int?, var article_url: String?, var ban_comment: Int?,
                       var behot_time: Int?, var bury_count: Int?, var cell_flag: Int?, var cell_layout_style: Int?,
                       var cell_type: Int?, var comment_count: Int?, var cursor: Long?, var digg_count: Int?,
                       var display_url: String?, var forward_info: ForwardInfo?, var group_id: String?,
                       var has_m3u8_video: Boolean?, var has_mp4_video: Int?, var has_video: Boolean?,
                       var hot: Int?, var ignore_web_transform: Int?, var is_stick: Boolean, var is_subject: Boolean?,
                       var item_id: String?, var item_version: Int?, var keywords: String?, var label: String?,
                       var label_style: Int?, var level: Int?, var like_count: Int, var log_pb: LogPb?, var media_info: MediaInfo?,
                       var media_name: String?, var preload_web: Int, var publish_time: Int?, var read_count: Int?,
                       var repin_count: Int?, var rid: String?, var share_count: Int?, var share_url: String?,
                       var show_portrait: Boolean?, var show_portrait_article: Boolean?, var source: String?,
                       var source_icon_style: Int?, var source_open_url: String?, var stick_label: String?,
                       var stick_style: Int?, var tag: String?, var tag_id: String?, var tip: Int?, var title: String?,
                       var ugc_recommend: UgcRecommend?, var url: String?, var user_info: UserInfo?,
                       var user_repin: Int?, var user_verified: Int?, var verified_content: String?,
                       var video_style: Int?, val image_list: List<ImageList>?, val filter_words: List<FilterWords>?,
                       val gallary_image_count: Int, val has_image: Boolean, val middle_image: MiddleImage?) {

        data class ActionList(var action: Int?, var desc: String?)

        data class ForwardInfo(var forward_count: Int?)

        data class LogPb(var impr_id: String?)

        data class MediaInfo(var avatar_url: String?, var follow: Boolean?, var is_star_user: Boolean?,
                             var media_id: String?, var name: String?)

        data class UgcRecommend(var activity: String?, var reason: String?)

        data class UserInfo(var avatar_url: String?, var description: String?)

        data class ImageList(val height: Int?, val uri: String?, val url: String?, val width: Int?,
                             val url_list: List<UrlList>?) {

            data class UrlList(val url: String?)
        }

        data class FilterWords(val id: String?, val name: String?, val is_selected: Boolean?)

        data class MiddleImage(val height: Int?, val uri: String?, val url: String?, val width: Int?,
                               val url_list: List<UrlList>?) {

            data class UrlList(val url: String?)
        }

    }
}