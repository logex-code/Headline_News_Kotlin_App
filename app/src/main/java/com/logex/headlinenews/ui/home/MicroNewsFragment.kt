package com.logex.headlinenews.ui.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.logex.adapter.recyclerview.wrapper.LoadMoreWrapper
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.MicroNewsAdapter
import com.logex.headlinenews.base.MVVMFragment
import com.logex.headlinenews.base.Observer
import com.logex.headlinenews.model.NewsListEntity
import com.logex.pullrefresh.listener.PullRefreshListener
import com.logex.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_micro_news.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 微头条页面
 */
class MicroNewsFragment : MVVMFragment<MicroNewsViewModel>() {
    private var mAdapter: MicroNewsAdapter? = null
    private var mLoadMoreWrapper: LoadMoreWrapper? = null

    private var mList = arrayListOf<NewsListEntity>()
    private var isLoadMore = false // 加载更多是否触发
    private var lastTime = 0L

    override fun createViewModel(): MicroNewsViewModel = MicroNewsViewModel(context)

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

        pr_layout.setPullRefreshListener(object : PullRefreshListener() {

            override fun onRefresh() {
                onPullRefresh()
            }
        })
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        mViewModel?.getMicroNewsList("weitoutiao", 20, lastTime)
    }

    override fun dataObserver() {
        super.dataObserver()
        registerObserver(mViewModel?.microNewsListData, Observer { data ->
            pr_layout.finishRefresh()

            if (data != null && data.isNotEmpty()) {
                if (isLoadMore) {
                    mList.addAll(data)
                } else {
                    mList.clear()
                    mList.addAll(data)
                }

                showData(mList)
            }
        })

        registerObserver(mViewModel?.errorData, Observer { errInfo ->
            LogUtil.e("获取动态失败>>>>>$errInfo")

            pr_layout.finishRefresh()
            showLoadMoreFailed(mLoadMoreWrapper)
        })
    }

    override fun onPullRefresh() {
        super.onPullRefresh()
        isLoadMore = false
        mViewModel?.getMicroNewsList("weitoutiao", 20, lastTime)
    }

    override fun onLoadMore() {
        super.onLoadMore()
        isLoadMore = true
        mViewModel?.getMicroNewsList("weitoutiao", 20, lastTime)
    }

    private fun showData(list: ArrayList<NewsListEntity>) {
        if (mAdapter == null) {
            mAdapter = MicroNewsAdapter(context, list, R.layout.recycler_item_micro_news)

            //设置布局管理器
            initLinearLayoutManager(rv_micro_news, LinearLayoutManager.VERTICAL)

            mLoadMoreWrapper = createLoadMoreWrapper(mAdapter, rv_micro_news)

            rv_micro_news.adapter = mLoadMoreWrapper
        } else {
            mLoadMoreWrapper?.notifyDataSetChanged()
        }
    }
}