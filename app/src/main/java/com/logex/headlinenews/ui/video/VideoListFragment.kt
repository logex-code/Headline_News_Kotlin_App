package com.logex.headlinenews.ui.video

import android.os.Bundle
import com.logex.fragmentation.BaseFragment
import com.logex.fragmentation.anim.DefaultNoAnimator
import com.logex.fragmentation.anim.FragmentAnimator
import com.logex.headlinenews.R

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 视频列表页面
 */
class VideoListFragment : BaseFragment() {

    companion object {

        fun newInstance(args: Bundle): VideoListFragment {
            val fragment = VideoListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_video_list

    override fun viewCreate(savedInstanceState: Bundle?) {

    }

    override fun onCreateFragmentAnimator(): FragmentAnimator = DefaultNoAnimator()
}