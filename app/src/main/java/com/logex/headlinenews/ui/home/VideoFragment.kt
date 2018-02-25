package com.logex.headlinenews.ui.home

import android.os.Bundle
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.VideoPagerAdapter
import com.logex.headlinenews.base.MVPBaseFragment
import com.logex.headlinenews.model.VideoCategoryEntity
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import com.logex.utils.StatusBarUtil
import com.logex.utils.ValidateUtil
import kotlinx.android.synthetic.main.fragment_video.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 西瓜视频页面
 */
class VideoFragment : MVPBaseFragment<VideoPresenter>(), VideoContract.VideoView {

    override fun onServerFailure() {

    }

    override fun onNetworkFailure() {

    }

    override fun getVideoCategoryListSuccess(data: List<VideoCategoryEntity>?) {
        LogUtil.i("视频分类列表>>>>>>" + GsonUtil.getInstance().toJson(data))

        if (ValidateUtil.isListNonEmpty(data)) {
            vp_video.adapter = VideoPagerAdapter(childFragmentManager, data!!)
            tab_video.setupWithViewPager(vp_video)
        }
    }

    override fun getVideoCategoryListFailure(errInfo: String?) {
        LogUtil.e("获取视频分类失败>>>>>" + errInfo)
    }

    override fun createPresenter(): VideoPresenter {
        return VideoPresenter(context, this)
    }

    companion object {

        fun newInstance(): VideoFragment {
            val args = Bundle()
            val fragment = VideoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_video

    override fun viewCreate(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.common_bg)
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        StatusBarUtil.setStatusBarDarkMode(true, mActivity)
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        StatusBarUtil.setStatusBarDarkMode(false, mActivity)
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        mPresenter?.getVideoCategoryList()
    }
}