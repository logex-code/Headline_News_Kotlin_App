package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 动态实体
 */
data class DynamicEntity(var has_more: Boolean?, var data: ArrayList<DynamicEntity.Content>) {

    data class Content(var comment_visible_count: Int, var comment_type: Int, var open_url: String?,
                       var share_url: String?, var comment_count: Int, var content: String?,
                       var user: User?) {

        data class User(var verified_reason: String?, var is_blocking: Int, var schema: String?,
                        var avatar_url: String?, var user_id: String?, var screen_name: String?,
                        var is_friend: Int, var is_blocked: Int, var user_verified: Boolean,
                        var description: String?)
    }
}