package com.logex.headlinenews.ui.news

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.logex.adapter.recyclerview.wrapper.HeaderFooterWrapper
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.NewsCommentAdapter
import com.logex.headlinenews.base.MVPBaseFragment
import com.logex.headlinenews.model.NewsCommentEntity
import com.logex.headlinenews.model.NewsDetailEntity
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import com.logex.utils.StatusBarUtil
import com.logex.utils.UIUtils
import kotlinx.android.synthetic.main.fragment_news_detail.*

/**
 * 创建人: liguangxi
 * 日期: 2018/3/2
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻详情页面
 */
class NewsDetailFragment : MVPBaseFragment<NewsDetailPresenter>(), NewsDetailContract.NewsDetailView {
    private var mAdapter: NewsCommentAdapter? = null
    private var mHeaderFooterWrapper: HeaderFooterWrapper? = null
    private var mNewsDetail: NewsDetailEntity? = null
    private var mList = arrayListOf<NewsCommentEntity>()

    override fun getCommentSuccess(data: List<NewsCommentEntity>?) {
        LogUtil.i("评论列表>>>>>" + GsonUtil.getInstance().toJson(data))

        if (data != null && data.isNotEmpty()) {
            mList.addAll(data)
            showData(mList)
        }
    }

    private fun showData(list: ArrayList<NewsCommentEntity>) {
        if (mAdapter == null) {
            mAdapter = NewsCommentAdapter(context, list, R.layout.recycler_item_news_comment)

            //设置布局管理器
            initLinearLayoutManager(rv_news_comment, LinearLayoutManager.VERTICAL)

            mHeaderFooterWrapper = HeaderFooterWrapper(context, mAdapter)

            val mHeaderView = UIUtils.getXmlView(context, R.layout.header_news_detail_layout)
            mAdapter?.convertHeaderView(mHeaderView, mNewsDetail)

            mHeaderFooterWrapper?.addHeaderView(mHeaderView)

            rv_news_comment.adapter = mHeaderFooterWrapper
        } else {
            mHeaderFooterWrapper?.notifyDataSetChanged()
        }
    }

    override fun getCommentFailure(errInfo: String?) {
        LogUtil.e("获取评论列表失败>>>>>>" + errInfo)
    }

    override fun getNewsDetailSuccess(data: NewsDetailEntity?) {

        if (data == null) return

        mNewsDetail = data

        // 获取评论
        val groupId = arguments.getString(GROUP_ID)
        val itemId = arguments.getString(ITEM_ID)
        mPresenter?.getComment(groupId, itemId, 0, 20)
    }

    override fun getNewsDetailFailure(errInfo: String?) {
        LogUtil.e("获取新闻详情失败>>>>>>>" + errInfo)
    }

    override fun onServerFailure() {

    }

    override fun onNetworkFailure() {

    }

    override fun createPresenter(): NewsDetailPresenter {
        return NewsDetailPresenter(context, this)
    }

    companion object {
        val DETAIL_URL = "url"
        val GROUP_ID = "groupId"
        val ITEM_ID = "itemId"

        fun newInstance(args: Bundle): NewsDetailFragment {
            val fragment = NewsDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_news_detail

    override fun viewCreate(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.title_bar_color)
        StatusBarUtil.setStatusBarDarkMode(true, mActivity)

        title_bar.setLeftLayoutClickListener { pop() }
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        val url = arguments.getString(DETAIL_URL)
        mPresenter?.getNewsDetail(url)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        StatusBarUtil.setStatusBarDarkMode(false, mActivity)
    }
}