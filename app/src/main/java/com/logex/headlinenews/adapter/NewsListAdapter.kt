package com.logex.headlinenews.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.logex.headlinenews.R
import com.logex.headlinenews.base.BaseQuickAdapter
import com.logex.headlinenews.base.BaseViewHolder
import com.logex.headlinenews.model.NewsListEntity
import com.logex.utils.AutoUtils
import com.logex.utils.ValidateUtil


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻列表适配器
 */
class NewsListAdapter(context: Context, list: List<NewsListEntity.Content>, vararg layoutResId: Int) : BaseQuickAdapter<NewsListEntity.Content>(context, list, layoutResId[0]) {
    private val TYPE_EMPTY_PICTURE: Int = 0 // 无图
    private val TYPE_SINGLE_PICTURE: Int = 1 // 单图
    private val TYPE_MULTIPLE_PICTURE: Int = 2 // 多图

    private val emptyLayoutResId: Int = layoutResId[0]
    private val singleLayoutResId: Int = layoutResId[1]
    private val multipleLayoutResId: Int = layoutResId[2]

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item.has_image) {
            val imageList: List<NewsListEntity.Content.ImageList>? = item.image_list
            if (ValidateUtil.isListNonEmpty(imageList)) {
                return TYPE_MULTIPLE_PICTURE
            } else {
                return TYPE_SINGLE_PICTURE
            }
        } else {
            return TYPE_EMPTY_PICTURE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        val view: View
        when (viewType) {
            TYPE_EMPTY_PICTURE -> {
                view = mInflater.inflate(emptyLayoutResId, parent, false)
                AutoUtils.auto(view)
                return EmptyViewHolder(view, context)
            }

            TYPE_SINGLE_PICTURE -> {
                view = mInflater.inflate(singleLayoutResId, parent, false)
                AutoUtils.auto(view)
                return SingleViewHolder(view, context)
            }

            TYPE_MULTIPLE_PICTURE -> {
                view = mInflater.inflate(multipleLayoutResId, parent, false)
                AutoUtils.auto(view)
                return MultipleViewHolder(view, context)
            }
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun convertView(viewHolder: BaseViewHolder, item: NewsListEntity.Content, position: Int) {

        when (viewHolder) {
            is EmptyViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                viewHolder.setText(R.id.tv_news_source, item.media_name)

                viewHolder.setText(R.id.tv_news_comment, "${item.comment_count}评论")

                viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
            }

            is SingleViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                viewHolder.setText(R.id.tv_news_source, item.media_name)

                viewHolder.setText(R.id.tv_news_comment, "${item.comment_count}评论")

                val middleImage = item.middle_image

                if (middleImage != null) {
                    viewHolder.setImageResourcesUrl(R.id.iv_news_img, middleImage.url, -1)
                }
            }

            is MultipleViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                // 显示图片
                val imageList: List<NewsListEntity.Content.ImageList>? = item.image_list
                val rvNewsPicture: RecyclerView = viewHolder.getView(R.id.rv_news_picture)
                val gridManager = GridLayoutManager(context, 3)
                rvNewsPicture.layoutManager = gridManager
                val pictureAdapter = NewsMultiplePictureAdapter(context, imageList,
                        R.layout.recycle_item_news_picture_list_view, item.gallary_image_count)
                rvNewsPicture.adapter = pictureAdapter

                viewHolder.setText(R.id.tv_news_source, item.media_name)

                viewHolder.setText(R.id.tv_news_comment, "${item.comment_count}评论")
            }
        }
    }

    private class EmptyViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class SingleViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class MultipleViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private fun getPublishTime(time: Int?): String {
        if (time == null) return "未知"

        val currentTime: Long = System.currentTimeMillis() / 1000

        if (currentTime - time < 60) return "刚刚"

        return "${(currentTime - time) / 60}分钟"
    }
}