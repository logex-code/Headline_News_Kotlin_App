package com.logex.headlinenews.ui.persion

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.widget.TextView
import com.logex.adapter.recyclerview.CommonAdapter
import com.logex.adapter.recyclerview.base.ViewHolder
import com.logex.adapter.recyclerview.wrapper.LoadMoreWrapper
import com.logex.fragmentation.BaseFragment
import com.logex.headlinenews.R
import com.logex.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_my_follow.*

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 我的关注页面
 */
class MyFollowFragment : BaseFragment() {
    private val list = arrayListOf<String>()

    private var mLoadMoreWrapper: LoadMoreWrapper? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_follow
    }

    override fun viewCreate(savedInstanceState: Bundle?) {

    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)

        loadData()

        val mAdapter = TestAdapter(context, list, android.R.layout.simple_list_item_1)

        //设置布局管理器
        val linearLayoutManager = LinearLayoutManager(mActivity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_my_follow.layoutManager = linearLayoutManager

        mLoadMoreWrapper = LoadMoreWrapper(context, mAdapter)

        mLoadMoreWrapper?.setLoadMoreView(R.layout.recycler_item_load_more_view)

        val textView = TextView(context)
        textView.text = "没有更多了"
        textView.gravity = Gravity.CENTER
        textView.setTextColor(R.color.action_sheet_red)

        mLoadMoreWrapper?.setEmptyMoreView(textView)

        rv_my_follow.adapter = mLoadMoreWrapper

        mLoadMoreWrapper?.setOnLoadMoreListener {

            LogUtil.i("开始加载更多数据了..........")

            rv_my_follow.handler.postDelayed({

                loadData()

                if (list.size > 100) {
                    mLoadMoreWrapper?.setShowEmptyMore(true)
                }
                mLoadMoreWrapper?.notifyDataSetChanged()

            }, 3000)
        }
    }

    private fun loadData() {
        list.add("1")
        list.add("2")
        list.add("3")
        list.add("4")
        list.add("5")
        list.add("6")
        list.add("7")
        list.add("8")
        list.add("9")
        list.add("10")

        list.add("1")
        list.add("2")
        list.add("3")
        list.add("4")
        list.add("5")
        list.add("6")
        list.add("7")
        list.add("8")
        list.add("9")
        list.add("10")
    }

    private class TestAdapter(context: Context, list: ArrayList<String>, layoutResId: Int) : CommonAdapter<String>(context, list, layoutResId) {

        override fun convertView(viewHolder: ViewHolder, item: String?, position: Int) {

            viewHolder.setText(android.R.id.text1, item)
        }

    }
}