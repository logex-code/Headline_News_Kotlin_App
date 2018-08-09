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
import com.logex.headlinenews.utils.TimeFormatUtil.Companion.getPublishTime
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
    private val TYPE_AD_BIG_IMAGE: Int = 3 // 广告大图
    private val TYPE_AD_BIG_IMAGE_APP: Int = 4 // 广告大图 带app下载
    private val TYPE_AD_HAS_VIDEO_APP: Int = 5 // 广告 有视频 带app下载
    private val TYPE_AD_MULTIPLE_IMAGE_APP: Int = 6 // 广告多图 带app下载
    private val TYPE_AD_MULTIPLE_IMAGE: Int = 7 // 广告多图
    private val TYPE_SINGLE_BIG_IMAGE: Int = 8 // 大图
    private val TYPE_VIDEO_BIG_IMAGE: Int = 9 // 视频 大图
    private val TYPE_VIDEO_SINGLE_IMAGE: Int = 10 // 视频 一小图

    private val emptyLayoutResId: Int = layoutResId[0]
    private val singleLayoutResId: Int = layoutResId[1]
    private val multipleLayoutResId: Int = layoutResId[2]
    private val adBigImageLayoutResId: Int = layoutResId[3]
    private val adBigImageAppLayoutResId: Int = layoutResId[4]
    private val adHasVideoAppLayoutResId: Int = layoutResId[5]
    private val adMultipleImageAppLayoutResId: Int = layoutResId[6]
    private val adMultipleImageLayoutResId: Int = layoutResId[7]
    private val singleBigImageLayoutResId: Int = layoutResId[8]
    private val videoBigImageLayoutResId: Int = layoutResId[9]
    private val videoSingleImageLayoutResId: Int = layoutResId[10]

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item.has_image) {
            val imageList: List<NewsListEntity.Content.ImageList>? = item.image_list
            if (ValidateUtil.isListNonEmpty(imageList)) {
                if (imageList!!.size > 1) {
                    return TYPE_MULTIPLE_PICTURE
                } else {
                    return TYPE_SINGLE_BIG_IMAGE
                }
            } else {
                return TYPE_SINGLE_PICTURE
            }
        } else if (item.ad_id != null) {
            val imageList: List<NewsListEntity.Content.ImageList>? = item.image_list
            if (item.download_url != null) {
                if (ValidateUtil.isListNonEmpty(imageList)) {
                    return TYPE_AD_MULTIPLE_IMAGE_APP
                } else {
                    return TYPE_AD_BIG_IMAGE_APP
                }
            } else if (item.has_video) {
                return TYPE_AD_HAS_VIDEO_APP
            } else if (ValidateUtil.isListNonEmpty(imageList)) {
                return TYPE_AD_MULTIPLE_IMAGE
            } else {
                return TYPE_AD_BIG_IMAGE
            }
        } else if (item.has_video) {
            return TYPE_VIDEO_BIG_IMAGE
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

            TYPE_AD_BIG_IMAGE -> {
                view = mInflater.inflate(adBigImageLayoutResId, parent, false)
                AutoUtils.auto(view)
                return ADBigImageViewHolder(view, context)
            }

            TYPE_AD_BIG_IMAGE_APP -> {
                view = mInflater.inflate(adBigImageAppLayoutResId, parent, false)
                AutoUtils.auto(view)
                return ADBigImageAppViewHolder(view, context)
            }

            TYPE_AD_HAS_VIDEO_APP -> {
                view = mInflater.inflate(adHasVideoAppLayoutResId, parent, false)
                AutoUtils.auto(view)
                return ADHasVideoAppViewHolder(view, context)
            }

            TYPE_AD_MULTIPLE_IMAGE_APP -> {
                view = mInflater.inflate(adMultipleImageAppLayoutResId, parent, false)
                AutoUtils.auto(view)
                return ADMultipleImageAppViewHolder(view, context)
            }

            TYPE_AD_MULTIPLE_IMAGE -> {
                view = mInflater.inflate(adMultipleImageLayoutResId, parent, false)
                AutoUtils.auto(view)
                return ADMultipleImageViewHolder(view, context)
            }

            TYPE_SINGLE_BIG_IMAGE -> {
                view = mInflater.inflate(singleBigImageLayoutResId, parent, false)
                AutoUtils.auto(view)
                return SingleBigImageViewHolder(view, context)
            }

            TYPE_VIDEO_BIG_IMAGE -> {
                view = mInflater.inflate(videoBigImageLayoutResId, parent, false)
                AutoUtils.auto(view)
                return VideoBigImageViewHolder(view, context)
            }
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun convertView(viewHolder: BaseViewHolder, item: NewsListEntity.Content, position: Int) {

        when (viewHolder) {
            is EmptyViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                viewHolder.setText(R.id.tv_news_source, item.source)

                viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")

                viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
            }

            is SingleViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                viewHolder.setText(R.id.tv_news_source, item.source)

                viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")

                val middleImage = item.middle_image

                if (middleImage != null) {
                    viewHolder.setImageResourcesUrl(R.id.iv_news_img, middleImage.url, R.drawable.bg_new_list_image)
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
                        R.layout.recycler_item_news_picture_list_view, item.gallary_image_count)
                rvNewsPicture.adapter = pictureAdapter

                viewHolder.setText(R.id.tv_news_source, item.media_name)

                viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")
            }

            is ADBigImageViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                val largeImageList = item.large_image_list

                if (ValidateUtil.isListNonEmpty(largeImageList)) {
                    val largeImage = largeImageList!![0]
                    viewHolder.setImageResourcesUrl(R.id.iv_news_img, largeImage.url, -1)
                }

                viewHolder.setText(R.id.tv_news_source, item.source)

                viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
            }

            is ADBigImageAppViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                val image = item.image

                if (image != null) {
                    viewHolder.setImageResourcesUrl(R.id.iv_news_img, image.url, -1)
                }

                viewHolder.setText(R.id.tv_news_subtitle, item.sub_title)

                viewHolder.setText(R.id.tv_news_source, item.source)

                viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
            }

            is ADHasVideoAppViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                val largeImageList = item.large_image_list

                if (ValidateUtil.isListNonEmpty(largeImageList)) {
                    val largeImage = largeImageList!![0]
                    viewHolder.setImageResourcesUrl(R.id.iv_video_thumbnail, largeImage.url, -1)
                }

                viewHolder.setText(R.id.tv_news_subtitle, item.sub_title)

                viewHolder.setText(R.id.tv_news_source, item.source)

                viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
            }

            is ADMultipleImageAppViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                // 显示图片
                val imageList: List<NewsListEntity.Content.ImageList>? = item.image_list
                val rvNewsPicture: RecyclerView = viewHolder.getView(R.id.rv_news_picture)
                val gridManager = GridLayoutManager(context, 3)
                rvNewsPicture.layoutManager = gridManager
                val pictureAdapter = NewsMultiplePictureAdapter(context, imageList,
                        R.layout.recycler_item_news_picture_list_view, item.gallary_image_count)
                rvNewsPicture.adapter = pictureAdapter

                viewHolder.setText(R.id.tv_news_subtitle, item.sub_title)

                viewHolder.setText(R.id.tv_news_source, item.source)

                viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
            }

            is ADMultipleImageViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                // 显示图片
                val imageList: List<NewsListEntity.Content.ImageList>? = item.image_list
                val rvNewsPicture: RecyclerView = viewHolder.getView(R.id.rv_news_picture)
                val gridManager = GridLayoutManager(context, 3)
                rvNewsPicture.layoutManager = gridManager
                val pictureAdapter = NewsMultiplePictureAdapter(context, imageList,
                        R.layout.recycler_item_news_picture_list_view, item.gallary_image_count)
                rvNewsPicture.adapter = pictureAdapter

                viewHolder.setText(R.id.tv_news_source, item.source)

                viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
            }

            is SingleBigImageViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                viewHolder.setText(R.id.tv_news_source, item.source)

                viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")

                // 显示图片
                val image = item.image_list!![0]

                viewHolder.setImageResourcesUrl(R.id.iv_news_img, image.url, R.drawable.bg_new_list_image)

                viewHolder.setText(R.id.tv_news_image_size, "${item.gallary_image_count}图")
            }

            is VideoBigImageViewHolder -> {
                viewHolder.setText(R.id.tv_news_title, item.title)

                val largeImageList = item.large_image_list

                if (ValidateUtil.isListNonEmpty(largeImageList)) {
                    val largeImage = largeImageList!![0]
                    viewHolder.setImageResourcesUrl(R.id.iv_video_thumbnail, largeImage.url, -1)
                }

                viewHolder.setText(R.id.tv_news_source, item.source)

                viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")

                viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
            }
        }
    }

    private class EmptyViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class SingleViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class MultipleViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class ADBigImageViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class ADBigImageAppViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class ADHasVideoAppViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class ADMultipleImageAppViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class ADMultipleImageViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class SingleBigImageViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class VideoBigImageViewHolder(view: View, context: Context) : BaseViewHolder(view, context)
}