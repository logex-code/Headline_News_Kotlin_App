package com.logex.headlinenews.ui.home

import android.os.Bundle
import android.view.View
import com.logex.fragmentation.BaseFragment
import com.logex.headlinenews.R
import com.logex.headlinenews.model.event.StartBrotherEvent
import com.logex.headlinenews.ui.persion.MyFollowFragment
import kotlinx.android.synthetic.main.fragment_mine.*
import org.greenrobot.eventbus.EventBus

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
        ll_my_follow.setOnClickListener(this)
        ll_my_history.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
        // 打开我的关注页面
            R.id.ll_my_follow -> EventBus.getDefault().post(StartBrotherEvent(MyFollowFragment()))
        }
    }
}