package com.logex.headlinenews.ui.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.logex.adapter.recyclerview.wrapper.LoadMoreWrapper
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.MicroNewsAdapter
import com.logex.headlinenews.base.MVPBaseFragment
import com.logex.headlinenews.model.NewsListEntity
import com.logex.pullrefresh.listener.PullRefreshListener
import com.logex.utils.LogUtil
import com.logex.utils.UIUtils
import com.logex.utils.ValidateUtil
import kotlinx.android.synthetic.main.fragment_micro_news.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 微头条页面
 */
class MicroNewsFragment : MVPBaseFragment<MicroNewsPresenter>(), MicroNewsContract.MicroNewsView {
    private var mAdapter: MicroNewsAdapter? = null
    private var mLoadMoreWrapper: LoadMoreWrapper? = null

    private var mList = arrayListOf<NewsListEntity.Content>()
    private var isLoadMore = false // 加载更多是否触发
    private var lastTime = 0L

    override fun onServerFailure() {
        pr_layout.finishRefresh()
        showLoadMoreFailed(mLoadMoreWrapper)
    }

    override fun onNetworkFailure() {
        pr_layout.finishRefresh()
        showLoadMoreFailed(mLoadMoreWrapper)
        UIUtils.showNoNetDialog(mActivity)
    }

    override fun getMicroNewsListSuccess(data: List<NewsListEntity.Content>) {
        pr_layout.finishRefresh()

        if (ValidateUtil.isListNonEmpty(data)) {
            if (isLoadMore) {
                mList.addAll(data)
            } else {
                mList.clear()
                mList.addAll(data)
            }

            showData(mList)
        }
    }

    private fun showData(list: ArrayList<NewsListEntity.Content>) {
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

    override fun getMicroNewsListFailure(errInfo: String?) {
        LogUtil.e("获取动态失败>>>>>" + errInfo)

        pr_layout.finishRefresh()
        showLoadMoreFailed(mLoadMoreWrapper)
    }

    override fun createPresenter(): MicroNewsPresenter {
        return MicroNewsPresenter(context, this)
    }

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

    override fun onPullRefresh() {
        super.onPullRefresh()
        isLoadMore = false
        mPresenter?.getMicroNewsList("weitoutiao", 20, lastTime, System.currentTimeMillis())
    }

    override fun onLoadMore() {
        super.onLoadMore()
        isLoadMore = true
        mPresenter?.getMicroNewsList("weitoutiao", 20, lastTime, System.currentTimeMillis())
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)

        mPresenter?.getMicroNewsList("weitoutiao", 20, lastTime, System.currentTimeMillis())
    }
}