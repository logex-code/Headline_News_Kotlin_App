package com.logex.headlinenews.adapter

import android.content.Context
import com.logex.adapter.recyclerview.CommonAdapter
import com.logex.adapter.recyclerview.base.ViewHolder
import com.logex.headlinenews.R
import com.logex.headlinenews.getVideoDuration
import com.logex.headlinenews.model.NewsListEntity
import com.logex.headlinenews.widget.VideoListPlayer
import com.logex.videoplayer.JCVideoPlayer

/**
 * 创建人: liguangxi
 * 日期: 2018/2/25
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 视频列表适配器
 */
class VideoListAdapter(context: Context, list: List<NewsListEntity>, layoutResId: Int) :
        CommonAdapter<NewsListEntity>(context, list, layoutResId) {

    override fun convertView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        // 处理视频播放
        val mVideoPlayer = viewHolder.getView<VideoListPlayer>(R.id.mVideoPlayer)

        mVideoPlayer.videoId = item.video_id

        mVideoPlayer.setUp("", JCVideoPlayer.SCREEN_WINDOW_LIST, item.title)
        mVideoPlayer.showVideoThumbnail(item.middle_image?.url)
                .showVideoPlayCount("${item.read_count}次播放")
                .showVideoDuration(getVideoDuration(item.video_duration))

        val user = item.user_info

        viewHolder.setCircleImageResourcesUrl(R.id.iv_user_avatar, user?.avatar_url, -1)

        viewHolder.setText(R.id.tv_user_name, item.media_name)

        viewHolder.setText(R.id.tv_comment_count, item.comment_count?.toString())
    }
}