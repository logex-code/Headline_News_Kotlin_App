package com.logex.headlinenews.adapter

import android.content.Context
import com.logex.headlinenews.R
import com.logex.headlinenews.base.BaseQuickAdapter
import com.logex.headlinenews.base.BaseViewHolder
import com.logex.headlinenews.model.NewsListEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/2/25
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 视频列表适配器
 */
class VideoListAdapter(context: Context, list: List<NewsListEntity.Content>, layoutResId: Int) : BaseQuickAdapter<NewsListEntity.Content>(context, list, layoutResId) {

    override fun convertView(viewHolder: BaseViewHolder, item: NewsListEntity.Content, position: Int) {
        viewHolder.setText(R.id.tv_video_title, item.title)

        val middleImage = item.middle_image

        if (middleImage != null) {
            viewHolder.setImageResourcesUrl(R.id.iv_video_thumbnail, middleImage.url, -1)
        }
    }
}