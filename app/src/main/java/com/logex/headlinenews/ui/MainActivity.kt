package com.logex.headlinenews.ui

import android.os.Bundle
import com.logex.fragmentation.BaseActivity
import com.logex.fragmentation.anim.DefaultHorizontalAnimator
import com.logex.fragmentation.anim.FragmentAnimator
import com.logex.headlinenews.R
import com.logex.headlinenews.ui.main.MainFragment
import com.logex.utils.AutoUtils
import com.logex.utils.StatusBarUtil

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * MainActivity
 */
class MainActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initCreate(savedInstanceState: Bundle?) {
        AutoUtils.auto(this)
        StatusBarUtil.setTranslucentStatusBar(this)
        isUseDarkMode = StatusBarUtil.setStatusBarDarkMode(false, this)

        if (savedInstanceState == null) {
            // 加载入口fragment(主页面)
            loadRootFragment(R.id.fl_content, MainFragment.newInstance())
        }
    }

    override fun onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport()
    }

    public override fun onCreateFragmentAnimator(): FragmentAnimator {
        // 设置横向(和安卓4.x动画相同)
        return DefaultHorizontalAnimator()
    }
}