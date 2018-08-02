package com.logex.headlinenews.ui.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.logex.adapter.recyclerview.wrapper.LoadMoreWrapper
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.MicroNewsAdapter
import com.logex.headlinenews.base.MVPBaseFragment
import com.logex.headlinenews.model.DynamicEntity
import com.logex.pullrefresh.listener.OnPullListener
import com.logex.utils.LogUtil
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

    private var mList = arrayListOf<DynamicEntity.Content>()
    private var isLoadMore = false // 加载更多是否触发

    override fun onServerFailure() {

    }

    override fun onNetworkFailure() {

    }

    override fun getDynamicListSuccess(data: DynamicEntity?) {
        LogUtil.i("动态条数>>>>" + data?.data?.size)

        if (!isLoadMore) pr_layout.finishRefresh()

        val list = data?.data

        if (ValidateUtil.isListNonEmpty(list)) {
            if (isLoadMore) {
                mList.addAll(list!!)
            } else {
                mList.clear()
                mList.addAll(list!!)
            }

            showData(mList)
        }
    }

    private fun showData(list: ArrayList<DynamicEntity.Content>) {
        if (mAdapter == null) {
            mAdapter = MicroNewsAdapter(context, list, R.layout.recycler_item_micro_news)
            //设置布局管理器
            val linearLayoutManager = LinearLayoutManager(mActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            rv_micro_news.layoutManager = linearLayoutManager

            mLoadMoreWrapper = LoadMoreWrapper(context, mAdapter)

            mLoadMoreWrapper?.setLoadMoreView(R.layout.recycler_item_load_more_view)

            rv_micro_news.adapter = mLoadMoreWrapper

            mLoadMoreWrapper?.setOnLoadMoreListener {
                isLoadMore = true
                mPresenter?.getDynamicList("51025535398", 20)
            }
        } else {
            mLoadMoreWrapper?.notifyDataSetChanged()
        }
    }

    override fun getDynamicListFailure(errInfo: String?) {
        LogUtil.e("获取动态失败>>>>>" + errInfo)
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

        pr_layout.setOnPullListener(object : OnPullListener {

            override fun onMoveTarget(offset: Int) = Unit

            override fun onMoveRefreshView(offset: Int) = Unit

            override fun onRefresh() {
                mPresenter?.getDynamicList("51025535398", 20)
            }

        })
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)

        mPresenter?.getDynamicList("51025535398", 20)
    }
}