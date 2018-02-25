package com.logex.headlinenews.ui.home

import android.os.Bundle
import com.logex.fragmentation.BaseFragment
import com.logex.headlinenews.R

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 我的页面
 */
class MineFragment : BaseFragment() {

    companion object {

        fun newInstance(): MineFragment {
            val args = Bundle()
            val fragment = MineFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_mine

    override fun viewCreate(savedInstanceState: Bundle?) {

    }
}