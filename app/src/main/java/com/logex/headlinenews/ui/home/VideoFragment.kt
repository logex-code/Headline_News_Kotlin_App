package com.logex.headlinenews.ui.home

import android.os.Bundle
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.VideoPagerAdapter
import com.logex.headlinenews.base.MVVMFragment
import com.logex.headlinenews.base.Observer
import com.logex.headlinenews.ui.video.VideoViewModel
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_video.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 西瓜视频页面
 */
class VideoFragment : MVVMFragment<VideoViewModel>() {

    companion object {

        fun newInstance(): VideoFragment {
            val args = Bundle()
            val fragment = VideoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun createViewModel(): VideoViewModel = VideoViewModel(context)

    override fun getLayoutId(): Int = R.layout.fragment_video

    override fun viewCreate(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.title_bar_color)
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        mViewModel?.getVideoCategoryList()
    }

    override fun dataObserver() {
        super.dataObserver()
        registerObserver(mViewModel?.videoCategoryData, Observer { data ->
            LogUtil.i("视频分类列表>>>>>>" + GsonUtil.getInstance().toJson(data))

            if (data != null && data.isNotEmpty()) {
                val adapter = VideoPagerAdapter(childFragmentManager)
                adapter.mTabs = data
                vp_video.adapter = adapter
                tab_video.setupWithViewPager(vp_video)
            }
        })

        registerObserver(mViewModel?.errorData, Observer { errInfo ->
            LogUtil.e("获取视频分类失败>>>>>$errInfo")
        })
    }
}