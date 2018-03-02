package com.logex.headlinenews.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import com.logex.headlinenews.R
import com.logex.headlinenews.base.BaseQuickAdapter
import com.logex.headlinenews.base.BaseViewHolder
import com.logex.headlinenews.model.NewsCommentEntity
import com.logex.headlinenews.utils.TimeFormatUtil
import com.logex.utils.AutoUtils

/**
 * 创建人: liguangxi
 * 日期: 2018/3/2
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻评论列表适配器
 */
class NewsCommentAdapter(context: Context, list: List<NewsCommentEntity>, vararg layoutResId: Int) : BaseQuickAdapter<NewsCommentEntity>(context, list, layoutResId[0]) {
    private val TYPE_DETAIL_HEADER = 0
    private val TYPE_COMMENT_ITEM = 1

    private val detailHeaderLayoutResId = layoutResId[0]
    private val commentItemLayoutResId = layoutResId[1]

    override fun getItemViewType(position: Int): Int = if (position == 0 && getItem(position).data != null) {
        TYPE_DETAIL_HEADER
    } else {
        TYPE_COMMENT_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder {
        val view: View
        when (viewType) {
            TYPE_DETAIL_HEADER -> {
                view = mInflater.inflate(detailHeaderLayoutResId, parent, false)
                AutoUtils.auto(view)
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                val wvNewsDetail = view.findViewById(R.id.wv_news_detail)
                // 重新设置宽高
                wvNewsDetail.layoutParams = lp
                return DetailHeaderViewHolder(view, context)
            }

            TYPE_COMMENT_ITEM -> {
                view = mInflater.inflate(commentItemLayoutResId, parent, false)
                AutoUtils.auto(view)
                return CommentViewHolder(view, context)
            }
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun convertView(viewHolder: BaseViewHolder, item: NewsCommentEntity, position: Int) {
        when (viewHolder) {
            is DetailHeaderViewHolder -> {
                val data = item.data ?: return

                val wvNewsDetail = viewHolder.getView<WebView>(R.id.wv_news_detail)

                // 启用js
                wvNewsDetail.settings.javaScriptEnabled = true
                viewHolder.setText(R.id.tv_news_title, data.title)

                val mediaUser = data.media_user
                if (mediaUser != null) {
                    viewHolder.setVisible(R.id.ll_media_user_info, true)
                    viewHolder.setCircleImageResourcesUrl(R.id.iv_user_avatar, mediaUser.avatar_url, -1)
                    viewHolder.setText(R.id.tv_user_name, data.source)
                    viewHolder.setText(R.id.tv_news_time, TimeFormatUtil.getPublishTime(data.publish_time))
                    viewHolder.setText(R.id.tv_news_source, "· ${mediaUser.screen_name}")
                }

                val htmlPart1 = "<!DOCTYPE HTML html>\n" +
                        "<head><meta charset=\"utf-8\"/>\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, user-scalable=no\"/>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<style> \n" +
                        "img{width:100%!important;height:auto!important}\n" +
                        " </style>"
                val htmlPart2 = "</body></html>"

                val html = htmlPart1 + data.content + htmlPart2

                wvNewsDetail.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            }

            is CommentViewHolder -> {
                viewHolder.setCircleImageResourcesUrl(R.id.iv_user_avatar, item.comment?.user_profile_image_url, -1)
                viewHolder.setText(R.id.tv_user_name, item.comment?.user_name)
            }
        }
    }

    private class DetailHeaderViewHolder(view: View, context: Context) : BaseViewHolder(view, context)

    private class CommentViewHolder(view: View, context: Context) : BaseViewHolder(view, context)
}