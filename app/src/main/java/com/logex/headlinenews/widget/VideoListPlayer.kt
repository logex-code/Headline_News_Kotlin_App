package com.logex.headlinenews.widget

import android.content.Context
import android.util.AttributeSet
import com.logex.headlinenews.R
import com.logex.videoplayer.JCVideoPlayer

/**
 * 创建人: liguangxi
 * 日期: 18-3-14
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 视频列表播放器
 */
class VideoListPlayer : JCVideoPlayer {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun getLayoutId(): Int = R.layout.jc_video_list_player
}