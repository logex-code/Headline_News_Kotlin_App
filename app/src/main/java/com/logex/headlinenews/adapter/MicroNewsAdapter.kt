package com.logex.headlinenews.adapter

import android.content.Context
import com.logex.adapter.recyclerview.CommonAdapter
import com.logex.adapter.recyclerview.base.ViewHolder
import com.logex.headlinenews.R
import com.logex.headlinenews.model.DynamicEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 微头条列表适配器
 */
class MicroNewsAdapter(context: Context, list: List<DynamicEntity.Content>, layoutResId: Int) : CommonAdapter<DynamicEntity.Content>(context, list, layoutResId) {

    override fun convertView(viewHolder: ViewHolder, item: DynamicEntity.Content, position: Int) {
        // 显示用户头像
        viewHolder.setCircleImageResourcesUrl(R.id.iv_user_avatar, item.user?.avatar_url, -1)
        viewHolder.setText(R.id.tv_news_content, item.content)
    }
}