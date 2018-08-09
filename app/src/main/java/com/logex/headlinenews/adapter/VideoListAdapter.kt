package com.logex.headlinenews.adapter

import android.content.Context
import com.logex.adapter.recyclerview.CommonAdapter
import com.logex.adapter.recyclerview.base.ViewHolder
import com.logex.headlinenews.R
import com.logex.headlinenews.model.NewsListEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/2/25
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 视频列表适配器
 */
class VideoListAdapter(context: Context, list: List<NewsListEntity.Content>, layoutResId: Int) : CommonAdapter<NewsListEntity.Content>(context, list, layoutResId) {

    override fun convertView(viewHolder: ViewHolder, item: NewsListEntity.Content, position: Int) {
        viewHolder.setText(R.id.tv_video_title, item.title)
        viewHolder.setText(R.id.tv_video_play_count, item.read_count.toString() + "次播放")

        viewHolder.setImageResourcesUrl(R.id.iv_video_thumbnail, item.middle_image?.url, -1)

        val user = item.user_info

        viewHolder.setCircleImageResourcesUrl(R.id.iv_user_avatar, user?.avatar_url, -1)

        viewHolder.setText(R.id.tv_user_name, item.media_name)

        viewHolder.setText(R.id.tv_comment_count, item.comment_count?.toString())
    }
}