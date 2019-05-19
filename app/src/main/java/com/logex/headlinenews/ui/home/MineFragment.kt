package com.logex.headlinenews.ui.home

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.logex.fragmentation.BaseFragment
import com.logex.headlinenews.R
import com.logex.headlinenews.base.RxBus
import com.logex.headlinenews.model.event.StartBrotherEvent
import com.logex.headlinenews.ui.persion.MyFollowFragment
import com.logex.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 我的页面
 */
class MineFragment : BaseFragment(), View.OnClickListener {

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
        // 设置间距
        val statusBarHeight = StatusBarUtil.getStatusBarHeight(context)
        val llMineTopLP = ll_mine_top.layoutParams as LinearLayout.LayoutParams
        val flMineLoginLP = fl_mine_login.layoutParams as LinearLayout.LayoutParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            llMineTopLP.height = llMineTopLP.height + statusBarHeight
            flMineLoginLP.topMargin = flMineLoginLP.topMargin + statusBarHeight
        }

        ll_my_follow.setOnClickListener(this)
        ll_my_history.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
        // 打开我的关注页面
            R.id.ll_my_follow -> RxBus.getDefault().post(StartBrotherEvent(MyFollowFragment()))
        }
    }
}