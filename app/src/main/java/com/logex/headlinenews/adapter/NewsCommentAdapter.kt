package com.logex.headlinenews.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.logex.adapter.recyclerview.CommonAdapter
import com.logex.adapter.recyclerview.base.ViewHolder
import com.logex.headlinenews.R
import com.logex.headlinenews.model.NewsCommentEntity
import com.logex.headlinenews.model.NewsDetailEntity
import com.logex.headlinenews.utils.TimeFormatUtil
import com.logex.utils.AutoUtils
import com.logex.utils.UIUtils


/**
 * 创建人: liguangxi
 * 日期: 2018/3/2
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻评论列表适配器
 */
open class NewsCommentAdapter(context: Context, list: List<NewsCommentEntity>, layoutId: Int) : CommonAdapter<NewsCommentEntity>(context, list, layoutId) {

    /**
     * 处理头部view数据
     * @param headerView 头部view
     * @param newsDetailModel 头部数据
     */
    open fun convertHeaderView(headerView: View, newsDetailModel: NewsDetailEntity?) {
        // 显示标题
        val tvNewsTitle: TextView = headerView.findViewById(R.id.tv_news_title) as TextView
        tvNewsTitle.text = newsDetailModel?.title

        val mediaUser = newsDetailModel?.media_user
        if (mediaUser != null) {
            val llMediaUserInfo = headerView.findViewById<LinearLayout>(R.id.ll_media_user_info)
            llMediaUserInfo.visibility = View.VISIBLE

            val ivUserAvatar: ImageView = headerView.findViewById(R.id.iv_user_avatar) as ImageView
            UIUtils.showCircleImage(mContext, ivUserAvatar, mediaUser.avatar_url, -1)

            val tvUserName: TextView = headerView.findViewById(R.id.tv_user_name) as TextView
            tvUserName.text = newsDetailModel.source

            val tvNewsTime: TextView = headerView.findViewById(R.id.tv_news_time) as TextView
            tvNewsTime.text = TimeFormatUtil.getPublishTime(newsDetailModel.publish_time)

            val tvNewsSource: TextView = headerView.findViewById(R.id.tv_news_source) as TextView
            tvNewsSource.text = "· ${mediaUser.screen_name}"
        }

        // 显示网页内容
        val wvNewsDetail: WebView = headerView.findViewById(R.id.wv_news_detail) as WebView

        wvNewsDetail.viewTreeObserver.addOnGlobalLayoutListener {
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            val marginX = AutoUtils.getDisplayWidthValue(30)
            val marginY = AutoUtils.getDisplayWidthValue(30)
            lp.setMargins(marginX, marginY, marginX, marginY)
            wvNewsDetail.layoutParams = lp
        }

        val settings = wvNewsDetail.settings
        // 启用js
        settings?.javaScriptEnabled = true
        settings?.domStorageEnabled = true

        val htmlPart1 = "<!DOCTYPE HTML html>\n" +
                "<head><meta charset=\"utf-8\"/>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, user-scalable=no\"/>\n" +
                "</head>\n" +
                "<body>\n" +
                "<style> \n" +
                "img{width:100%!important;height:auto!important}\n" +
                " </style>"
        val htmlPart2 = "</body></html>"

        val html = htmlPart1 + newsDetailModel?.content + htmlPart2

        wvNewsDetail.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    override fun convertView(viewHolder: ViewHolder, item: NewsCommentEntity, position: Int) {
        viewHolder.setCircleImageResourcesUrl(R.id.iv_user_avatar, item.comment?.user_profile_image_url, -1)
        viewHolder.setText(R.id.tv_user_name, item.comment?.user_name)
        viewHolder.setText(R.id.tv_like_count, item.comment?.digg_count.toString())
        viewHolder.setText(R.id.tv_comment_content, item.comment?.text)
        viewHolder.setText(R.id.tv_comment_time, "${TimeFormatUtil.getPublishTime(item.comment?.create_time)} · ")

        // 显示回复数
        val replyCount = item.comment?.reply_count
        val tvReplayCount = viewHolder.getView<TextView>(R.id.tv_reply_count)
        if (replyCount == 0) {
            tvReplayCount.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            tvReplayCount.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            tvReplayCount.setBackgroundColor(mContext.resources.getColor(R.color.transparent))
            tvReplayCount.text = "回复"
        } else {
            tvReplayCount.layoutParams.width = AutoUtils.getDisplayWidthValue(148)
            tvReplayCount.layoutParams.height = AutoUtils.getDisplayHeightValue(66)
            tvReplayCount.setBackgroundResource(R.drawable.bg_news_comment_reply_count)
            tvReplayCount.text = "${replyCount}回复"
        }
    }
}