package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 动态实体
 */
data class DynamicEntity(
        val has_more: Boolean?,
        val data: ArrayList<DynamicEntity.Content>?
) {

    data class Content(
            val comment_visible_count: Int,
            val comment_type: Int,
            val open_url: String?,
            val share_url: String?,
            val comment_count: Int,
            val content: String?,
            val user: User?
    ) {

        data class User(
                val verified_reason: String?,
                val is_blocking: Int,
                val schema: String?,
                val avatar_url: String?,
                val user_id: String?,
                val screen_name: String?,
                val is_friend: Int,
                val is_blocked: Int,
                val user_verified: Boolean,
                val description: String?
        )
    }
}