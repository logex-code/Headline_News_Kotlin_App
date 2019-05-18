package com.logex.headlinenews.ui.news

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import com.logex.adapter.recyclerview.wrapper.HeaderFooterWrapper
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.NewsCommentAdapter
import com.logex.headlinenews.base.MVVMFragment
import com.logex.headlinenews.base.Observer
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
class NewsDetailFragment : MVVMFragment<NewsViewModel>() {
    private var mAdapter: NewsCommentAdapter? = null
    private var mHeaderFooterWrapper: HeaderFooterWrapper? = null

    private var mNewsDetail: NewsDetailEntity? = null
    private var mList = arrayListOf<NewsCommentEntity>()
    private var mDetailScrollListener: DetailScrollListener? = null

    companion object {
        fun newInstance(args: Bundle): NewsDetailFragment {
            val fragment = NewsDetailFragment()
            fragment.arguments = args
            return fragment
        }

        const val DETAIL_URL = "url"
        const val GROUP_ID = "groupId"
        const val ITEM_ID = "itemId"
    }

    override fun createViewModel(): NewsViewModel = NewsViewModel(context)

    override fun getLayoutId(): Int = R.layout.fragment_news_detail

    override fun viewCreate(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.title_bar_color)
        StatusBarUtil.setStatusBarDarkMode(mActivity, true)

        iv_title_bar_back.setOnClickListener { pop() }
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
        // 获取详情内容
        val url = arguments.getString(DETAIL_URL)
        mViewModel?.getNewsDetail(url)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        StatusBarUtil.setStatusBarDarkMode(mActivity, false)

        if (mDetailScrollListener != null) {
            rv_news_comment.removeOnScrollListener(mDetailScrollListener)
        }
    }

    override fun dataObserver() {
        super.dataObserver()
        registerObserver(mViewModel?.newsDetailData, Observer { data ->
            mNewsDetail = data

            // 获取评论
            val groupId = arguments.getString(GROUP_ID)
            val itemId = arguments.getString(ITEM_ID)
            mViewModel?.getComment(groupId, itemId, 0, 20)

            // 显示标题栏中间信息
            val mediaUser = mNewsDetail?.media_user
            if (mediaUser != null) {
                UIUtils.showCircleImage(context, iv_user_avatar, mediaUser.avatar_url, -1)
                tv_title.text = mNewsDetail?.source
            }
        })

        registerObserver(mViewModel?.newsCommentData, Observer { data ->
            LogUtil.i("评论列表>>>>>" + GsonUtil.getInstance().toJson(data))

            if (data != null) {
                mList.addAll(data)
                showData(mList)
            }
        })

        registerObserver(mViewModel?.errorData, Observer { errInfo ->
            UIUtils.showToast(context, errInfo)
        })
    }

    private class DetailScrollListener(private val llCenterTitleBar: LinearLayout) : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // 显示或隐藏中间标题栏内容
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                // 隐藏
                if (llCenterTitleBar.visibility == View.VISIBLE) {
                    llCenterTitleBar.visibility = View.GONE
                }
            } else {
                // 显示
                if (llCenterTitleBar.visibility == View.GONE) {
                    llCenterTitleBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showData(list: ArrayList<NewsCommentEntity>) {
        if (mAdapter == null) {
            mAdapter = NewsCommentAdapter(context, list, R.layout.recycler_item_news_comment)

            //设置布局管理器
            initLinearLayoutManager(rv_news_comment, LinearLayoutManager.VERTICAL)

            mHeaderFooterWrapper = HeaderFooterWrapper(context, mAdapter)

            val mHeaderView = UIUtils.getXmlView(context, R.layout.header_news_detail)
            mAdapter?.convertHeaderView(mHeaderView, mNewsDetail)

            mHeaderFooterWrapper?.addHeaderView(mHeaderView)

            rv_news_comment.adapter = mHeaderFooterWrapper

            mDetailScrollListener = DetailScrollListener(ll_center_title_bar)
            rv_news_comment.addOnScrollListener(mDetailScrollListener)
        } else {
            mHeaderFooterWrapper?.notifyDataSetChanged()
        }
    }
}