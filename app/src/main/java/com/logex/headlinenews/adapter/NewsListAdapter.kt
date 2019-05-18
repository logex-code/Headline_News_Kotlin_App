package com.logex.headlinenews.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.logex.adapter.recyclerview.CommonAdapter
import com.logex.adapter.recyclerview.base.ItemViewDelegate
import com.logex.adapter.recyclerview.base.ViewHolder
import com.logex.headlinenews.R
import com.logex.headlinenews.getPublishTime
import com.logex.headlinenews.getVideoDuration
import com.logex.headlinenews.model.NewsListEntity
import com.logex.utils.ValidateUtil


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻列表适配器
 */
class NewsListAdapter(context: Context, list: List<NewsListEntity>, layoutId: Int) :
        CommonAdapter<NewsListEntity>(context, list, layoutId) {

    init {
        // 添加单图type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_single_image

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    item.has_image && (item.image_list == null || item.image_list.isEmpty())

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertSingleImageView(viewHolder, item, position)

        })

        // 添加一张大图type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_single_big_image

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    item.has_image && item.image_list != null && item.image_list.size <= 1

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertSingleBigImageView(viewHolder, item, position)

        })

        // 添加多图type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_multiple_image

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    item.has_image && item.image_list != null && item.image_list.size > 1

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertMultipleView(viewHolder, item, position)

        })

        // 添加广告大图type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_ad_big_image

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    item.ad_id != null && item.download_url == null && !item.has_video &&
                            (item.image_list == null || item.image_list.isEmpty())

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertADBigImageView(viewHolder, item, position)

        })

        // 添加广告多图type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_ad_multiple_image

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    item.ad_id != null && item.download_url == null && !item.has_video &&
                            item.image_list != null && item.image_list.isNotEmpty()

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertADMultipleImageView(viewHolder, item, position)

        })

        // 添加广告 有视频 带app下载type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_ad_video_app

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    item.ad_id != null && item.has_video

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertADHasVideoAppView(viewHolder, item, position)

        })

        // 添加广告大图 带app下载type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_ad_big_image_app

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    item.ad_id != null && item.download_url != null && !item.has_video &&
                            (item.image_list == null || item.image_list.isEmpty())

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertADBigImageAppView(viewHolder, item, position)

        })

        // 添加广告多图 带app下载type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_ad_multiple_image_app

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    item.ad_id != null && item.download_url != null && !item.has_video &&
                            item.image_list != null && item.image_list.isNotEmpty()

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertADMultipleImageAppView(viewHolder, item, position)

        })

        // 添加视频 单图type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_video_single_image

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    !item.has_image && item.ad_id == null && item.has_video &&
                            (item.image_list == null || item.image_list.isEmpty())

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertVideoSingleImageView(viewHolder, item, position)

        })

        // 添加视频 大图type
        addItemViewDelegate(object : ItemViewDelegate<NewsListEntity> {

            override fun getItemViewLayoutId(): Int = R.layout.recycler_item_news_video_big_image

            override fun isForViewType(item: NewsListEntity, position: Int): Boolean =
                    !item.has_image && item.ad_id == null && item.has_video &&
                            (item.image_list == null || item.image_list.isEmpty())

            override fun convert(viewHolder: ViewHolder, item: NewsListEntity, position: Int) =
                    convertVideoBigImageView(viewHolder, item, position)

        })
    }

    private fun convertVideoSingleImageView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)

        val middleImage = item.middle_image

        if (middleImage != null) {
            viewHolder.setImageResourcesUrl(R.id.iv_video_thumbnail, middleImage.url, -1)
        }

        viewHolder.setText(R.id.tv_news_source, item.source)
        viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")
        viewHolder.setText(R.id.tv_video_duration, "02:33")
    }

    private fun convertVideoBigImageView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)

        val largeImageList = item.large_image_list
        val largeImage = largeImageList?.get(0)
        viewHolder.setImageResourcesUrl(R.id.iv_video_thumbnail, largeImage?.url,
                R.drawable.list_item_place_photo)

        viewHolder.setText(R.id.tv_video_duration, getVideoDuration(item.video_duration))
        viewHolder.setText(R.id.tv_news_source, item.source)
        viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")
        viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
    }

    private fun convertADMultipleImageAppView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)

        // 显示图片
        val imageList: List<NewsListEntity.Image>? = item.image_list
        val rvNewsImage: RecyclerView = viewHolder.getView(R.id.rv_news_image)
        if (imageList != null && imageList.isNotEmpty()) {
            rvNewsImage.visibility = View.VISIBLE

            val imageAdapter = NewsMultiImageAdapter(mContext, imageList,
                    R.layout.recycler_item_news_picture_list)

            // 设置布局管理器
            val gridManager = GridLayoutManager(mContext, 3)
            rvNewsImage.layoutManager = gridManager

            imageAdapter.imageSize = item.gallary_image_count
            rvNewsImage.adapter = imageAdapter
        } else {
            rvNewsImage.visibility = View.GONE
        }

        viewHolder.setText(R.id.tv_news_subtitle, item.sub_title)
        viewHolder.setText(R.id.tv_news_source, item.source)
        viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
    }

    private fun convertADBigImageAppView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)

        val image = item.image
        if (image != null) {
            viewHolder.setImageResourcesUrl(R.id.iv_news_img, image.url, -1)
        }

        viewHolder.setText(R.id.tv_news_subtitle, item.sub_title)
        viewHolder.setText(R.id.tv_news_source, item.source)
        viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
    }

    private fun convertADHasVideoAppView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)

        val largeImageList = item.large_image_list

        if (ValidateUtil.isListNonEmpty(largeImageList)) {
            val largeImage = largeImageList?.get(0)
            viewHolder.setImageResourcesUrl(R.id.iv_video_thumbnail, largeImage?.url, -1)
        }

        viewHolder.setText(R.id.tv_news_subtitle, item.sub_title)
        viewHolder.setText(R.id.tv_news_source, item.source)
        viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
    }

    private fun convertADMultipleImageView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)

        // 显示图片
        val imageList: List<NewsListEntity.Image>? = item.image_list
        val rvNewsImage: RecyclerView = viewHolder.getView(R.id.rv_news_image)
        if (imageList != null && imageList.isNotEmpty()) {
            rvNewsImage.visibility = View.VISIBLE

            val imageAdapter = NewsMultiImageAdapter(mContext, imageList,
                    R.layout.recycler_item_news_picture_list)

            // 设置布局管理器
            val gridManager = GridLayoutManager(mContext, 3)
            rvNewsImage.layoutManager = gridManager

            imageAdapter.imageSize = item.gallary_image_count
            rvNewsImage.adapter = imageAdapter
        } else {
            rvNewsImage.visibility = View.GONE
        }

        viewHolder.setText(R.id.tv_news_source, item.source)
        viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
    }

    private fun convertADBigImageView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)

        val largeImageList = item.large_image_list

        if (ValidateUtil.isListNonEmpty(largeImageList)) {
            val largeImage = largeImageList?.get(0)
            viewHolder.setImageResourcesUrl(R.id.iv_news_img, largeImage?.url, -1)
        }

        viewHolder.setText(R.id.tv_news_source, item.source)
        viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
    }

    private fun convertMultipleView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)

        // 显示图片
        val imageList: List<NewsListEntity.Image>? = item.image_list
        val rvNewsImage: RecyclerView = viewHolder.getView(R.id.rv_news_image)
        if (imageList != null && imageList.isNotEmpty()) {
            rvNewsImage.visibility = View.VISIBLE

            val imageAdapter = NewsMultiImageAdapter(mContext, imageList,
                    R.layout.recycler_item_news_picture_list)

            // 设置布局管理器
            val gridManager = GridLayoutManager(mContext, 3)
            rvNewsImage.layoutManager = gridManager

            imageAdapter.imageSize = item.gallary_image_count
            rvNewsImage.adapter = imageAdapter
        } else {
            rvNewsImage.visibility = View.GONE
        }

        // 标签
        viewHolder.setVisible(R.id.tv_news_label, item.label?.isNotEmpty() == true)
        viewHolder.setText(R.id.tv_news_label, item.label)

        viewHolder.setText(R.id.tv_news_source, item.media_name)
        viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")
    }

    private fun convertSingleBigImageView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)
        viewHolder.setText(R.id.tv_news_source, item.source)
        viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")

        // 显示图片
        val image = item.image_list?.get(0)
        viewHolder.setImageResourcesUrl(R.id.iv_news_img, image?.url, R.drawable.bg_new_list_image)

        viewHolder.setText(R.id.tv_news_image_size, "${item.gallary_image_count}图")
    }

    private fun convertSingleImageView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)

        viewHolder.setText(R.id.tv_news_source, item.source)

        viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")

        val middleImage = item.middle_image

        if (middleImage != null) {
            viewHolder.setImageResourcesUrl(R.id.iv_news_img, middleImage.url, R.drawable.bg_new_list_image)
        }
    }

    override fun convertView(viewHolder: ViewHolder, item: NewsListEntity, position: Int) {
        viewHolder.setText(R.id.tv_news_title, item.title)
        // 标签
        viewHolder.setVisible(R.id.tv_news_label, item.label?.isNotEmpty() == true)
        viewHolder.setText(R.id.tv_news_label, item.label)

        viewHolder.setText(R.id.tv_news_source, item.source)
        viewHolder.setText(R.id.tv_comment_count, "${item.comment_count}评论")
        viewHolder.setText(R.id.tv_news_time, getPublishTime(item.publish_time))
    }
}