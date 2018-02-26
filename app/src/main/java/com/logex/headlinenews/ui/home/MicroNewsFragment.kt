package com.logex.headlinenews.ui.home

import android.os.Bundle
import com.logex.fragmentation.BaseFragment
import com.logex.headlinenews.R
import com.logex.utils.StatusBarUtil

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 微头条页面
 */
class MicroNewsFragment : BaseFragment() {

    companion object {

        fun newInstance(): MicroNewsFragment {
            val args = Bundle()
            val fragment = MicroNewsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_micro_news

    override fun viewCreate(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.title_bar_color)
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        StatusBarUtil.setStatusBarDarkMode(true, mActivity)
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        StatusBarUtil.setStatusBarDarkMode(false, mActivity)
    }
}