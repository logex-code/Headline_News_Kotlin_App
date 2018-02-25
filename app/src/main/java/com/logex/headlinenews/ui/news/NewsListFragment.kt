package com.logex.headlinenews.ui.news

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.logex.fragmentation.anim.DefaultNoAnimator
import com.logex.fragmentation.anim.FragmentAnimator
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.NewsListAdapter
import com.logex.headlinenews.base.MVPBaseFragment
import com.logex.headlinenews.model.HomeNewsSubscribed
import com.logex.headlinenews.model.NewsListEntity
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_news_list.*


/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻列表页面
 */
class NewsListFragment : MVPBaseFragment<NewsListPresenter>(), NewsListContract.NewsListView {
    private var mAdapter: NewsListAdapter? = null

    override fun onServerFailure() {

    }

    override fun onNetworkFailure() {

    }

    override fun getHomeNewsListSuccess(data: List<NewsListEntity.Content>) {
        LogUtil.i("新闻列表>>>>>>" + GsonUtil.getInstance().toJson(data))

        showData(data)
    }

    private fun showData(list: List<NewsListEntity.Content>) {
        if (mAdapter == null) {
            mAdapter = NewsListAdapter(context, list, R.layout.recycler_item_news_empty_image,
                    R.layout.recycler_item_news_single_image, R.layout.recycler_item_news_multiple_image)
            //设置布局管理器
            val linearLayoutManager = LinearLayoutManager(mActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            rv_news_list.layoutManager = linearLayoutManager
            rv_news_list.adapter = mAdapter
        } else {
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun getHomeNewsListFailure(errInfo: String?) {
        LogUtil.e("获取新闻列表失败>>>>>>" + errInfo)
    }

    override fun createPresenter(): NewsListPresenter {
        return NewsListPresenter(context, this)
    }

    private var mTab: HomeNewsSubscribed.SubscribedBean? = null

    companion object {

        fun newInstance(args: Bundle): NewsListFragment {
            val fragment = NewsListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_news_list

    override fun viewCreate(savedInstanceState: Bundle?) {
        mTab = arguments.getParcelable("tab")

        LogUtil.i("当前标签信息>>>>>>" + GsonUtil.getInstance().toJson(mTab))
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator = DefaultNoAnimator()

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        // 获取新闻列表
        mPresenter?.getHomeNewsList(mTab?.category, null)
    }
}