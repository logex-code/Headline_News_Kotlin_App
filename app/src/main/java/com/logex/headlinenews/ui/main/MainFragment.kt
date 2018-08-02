package com.logex.headlinenews.ui.main

import android.os.Bundle
import android.view.View
import com.logex.fragmentation.BaseFragment
import com.logex.headlinenews.R
import com.logex.headlinenews.model.event.StartBrotherEvent
import com.logex.headlinenews.ui.home.HomeFragment
import com.logex.headlinenews.ui.home.MicroNewsFragment
import com.logex.headlinenews.ui.home.MineFragment
import com.logex.headlinenews.ui.home.VideoFragment
import com.logex.utils.LogUtil
import com.logex.utils.StatusBarUtil
import com.logex.utils.UIUtils
import kotlinx.android.synthetic.main.layout_main_bottom.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * MainFragment
 */
class MainFragment : BaseFragment(), View.OnClickListener {
    private val mFragments = arrayOfNulls<BaseFragment>(4)
    // 当前fragment的index
    private var currentTabIndex: Int = 0
    private var TOUCH_TIME: Long = 0
    // 再点一次退出程序时间设置
    private val WAIT_TIME = 2000L

    companion object {

        fun newInstance(): MainFragment {
            val args = Bundle()
            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_main

    override fun viewCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        if (savedInstanceState == null) {
            // 加载子fragment
            mFragments[0] = HomeFragment.newInstance()
            mFragments[1] = VideoFragment.newInstance()
            mFragments[2] = MicroNewsFragment.newInstance()
            mFragments[3] = MineFragment.newInstance()
            loadMultipleRootFragment(R.id.fragment_container, 0, mFragments[0], mFragments[1],
                    mFragments[2], mFragments[3])
        } else {
            LogUtil.i("从系统恢复..................")
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用,也可以通过getChildFragmentManager.getFragments()自行进行判断查找(效率更高些),用下面的方法查找更方便些
            currentTabIndex = savedInstanceState.getInt("currentTabIndex")
            mFragments[0] = findChildFragment(HomeFragment::class.java)
            mFragments[1] = findChildFragment(VideoFragment::class.java)
            mFragments[2] = findChildFragment(MicroNewsFragment::class.java)
            mFragments[3] = findChildFragment(MineFragment::class.java)
        }
        initView()
    }

    private fun initView() {
        // 默认选中首页
        ll_main_bottom_tab.getChildAt(currentTabIndex).isSelected = true

        btn_container_index.setOnClickListener(this)
        btn_container_video.setOnClickListener(this)
        btn_container_micro.setOnClickListener(this)
        btn_container_mine.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_container_index -> switchFragment(0)
            R.id.btn_container_video -> switchFragment(1)
            R.id.btn_container_micro -> switchFragment(2)
            R.id.btn_container_mine -> switchFragment(3)
        }
    }

    /**
     * 切换fragment

     * @param index
     */
    private fun switchFragment(index: Int) {
        if (currentTabIndex == index) return
        showHideFragment(mFragments[index], mFragments[currentTabIndex])
        // 未选中状态
        ll_main_bottom_tab.getChildAt(currentTabIndex).isSelected = false
        // 把当前tab设为选中状态
        ll_main_bottom_tab.getChildAt(index).isSelected = true
        currentTabIndex = index

        when (currentTabIndex) {
            1, 2 -> StatusBarUtil.setStatusBarDarkMode(true, mActivity)
            else -> StatusBarUtil.setStatusBarDarkMode(false, mActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("currentTabIndex", currentTabIndex)
    }

    override fun onBackPressedSupport(): Boolean {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            mActivity.finish()
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            UIUtils.showToast(context, "再按一次退出程序!")
        }
        return true
    }

    /**
     * 打开其他兄弟fragment
     */
    @Subscribe
    fun startBrother(event: StartBrotherEvent) =
            if (event.launchMode == -1 && event.requestCode == -1) {
                start(event.targetFragment)
            } else if (event.requestCode == -1) {
                when (event.launchMode) {
                    BaseFragment.SINGLETOP -> start(event.targetFragment, BaseFragment.SINGLETOP)
                    BaseFragment.SINGLETASK -> start(event.targetFragment, BaseFragment.SINGLETASK)
                    else -> start(event.targetFragment, BaseFragment.STANDARD)
                }
            } else {
                startForResult(event.targetFragment, event.requestCode)
            }
}