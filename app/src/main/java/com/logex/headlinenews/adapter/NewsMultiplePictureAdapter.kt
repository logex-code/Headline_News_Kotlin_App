package com.logex.headlinenews.adapter

import android.content.Context
import com.logex.headlinenews.R
import com.logex.headlinenews.base.BaseQuickAdapter
import com.logex.headlinenews.base.BaseViewHolder
import com.logex.headlinenews.model.NewsListEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻列表多图适配器
 */
class NewsMultiplePictureAdapter(context: Context, list: List<NewsListEntity.Content.ImageList>?, layoutResId: Int, val imageSize: Int) : BaseQuickAdapter<NewsListEntity.Content.ImageList>(context, list, layoutResId) {

    override fun convertView(viewHolder: BaseViewHolder, item: NewsListEntity.Content.ImageList, position: Int) {
        viewHolder.setImageResourcesUrl(R.id.iv_news_img, item.url, R.drawable.bg_new_list_image)

        viewHolder.setVisible(R.id.tv_news_image_size, imageSize > 3 && position == 2)
        viewHolder.setText(R.id.tv_news_image_size, "${imageSize}图")
    }
}