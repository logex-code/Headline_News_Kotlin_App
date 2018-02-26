package com.logex.headlinenews.adapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.logex.headlinenews.model.VideoCategoryEntity
import com.logex.headlinenews.ui.video.VideoListFragment


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 首页新闻PagerAdapter
 */
class VideoPagerAdapter(fm: FragmentManager, private var mTabs: List<VideoCategoryEntity>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putParcelable("tab", mTabs[position])
        return VideoListFragment.newInstance(bundle)
    }

    override fun getCount(): Int = mTabs.size

    override fun getPageTitle(position: Int): CharSequence? = mTabs[position].name
}