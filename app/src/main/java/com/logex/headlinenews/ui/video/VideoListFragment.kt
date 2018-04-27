package com.logex.headlinenews.ui.video

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.logex.fragmentation.anim.DefaultNoAnimator
import com.logex.fragmentation.anim.FragmentAnimator
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.VideoListAdapter
import com.logex.headlinenews.base.MVPBaseFragment
import com.logex.headlinenews.model.NewsListEntity
import com.logex.headlinenews.model.VideoCategoryEntity
import com.logex.refresh.PullRefreshLayout
import com.logex.refresh.RefreshListenerAdapter
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_video_list.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 视频列表页面
 */
class VideoListFragment : MVPBaseFragment<VideoListPresenter>(), VideoListContract.VideoListView {
    private var mAdapter: VideoListAdapter? = null
    private var lastTime = 0L
    private var mList = arrayListOf<NewsListEntity.Content>()
    private var isLoadMore = false // 加载更多是否触发

    override fun onServerFailure() {
        onStopLoad(pr_layout)
    }

    override fun onNetworkFailure() {
        onStopLoad(pr_layout)
    }

    override fun getVideoListSuccess(data: List<NewsListEntity.Content>) {
        LogUtil.i("视频列表>>>>>>" + GsonUtil.getInstance().toJson(data))
        onStopLoad(pr_layout)
        if (data.isNotEmpty()) {
            lastTime = data[data.size - 1].behot_time
            if (isLoadMore) {
                if (mTab?.category == null) {
                    mList.addAll(data.subList(1, data.size - 1))
                } else {
                    mList.addAll(data)
                }
            } else {
                mList.clear()
                mList.addAll(data)
            }

            showData(mList)
        }
    }

    private fun showData(list: List<NewsListEntity.Content>) {
        if (mAdapter == null) {
            mAdapter = VideoListAdapter(context, list, R.layout.recycler_item_video_big_image)
            //设置布局管理器
            val linearLayoutManager = LinearLayoutManager(mActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            rv_video_list.layoutManager = linearLayoutManager
            rv_video_list.adapter = mAdapter
        } else {
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun getVideoListFailure(errInfo: String?) {
        LogUtil.e("获取视频列表失败>>>>>>" + errInfo)
        onStopLoad(pr_layout)
    }

    override fun createPresenter(): VideoListPresenter {
        return VideoListPresenter(context, this)
    }

    private var mTab: VideoCategoryEntity? = null

    companion object {

        fun newInstance(args: Bundle): VideoListFragment {
            val fragment = VideoListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_video_list

    override fun viewCreate(savedInstanceState: Bundle?) {
        mTab = arguments.getParcelable("tab")

        LogUtil.i("当前标签信息>>>>>>" + GsonUtil.getInstance().toJson(mTab))

        pr_layout.setOnRefreshListener(object : RefreshListenerAdapter() {

            override fun onRefresh(refreshLayout: PullRefreshLayout?) {
                super.onRefresh(refreshLayout)
                lastTime = 0
                isLoadMore = false
                // 获取新闻列表
                mPresenter?.getVideoList(mTab?.category, 20, lastTime, System.currentTimeMillis())
            }

            override fun onLoadMore(refreshLayout: PullRefreshLayout?) {
                super.onLoadMore(refreshLayout)
                isLoadMore = true
                // 获取新闻列表
                mPresenter?.getVideoList(mTab?.category, 20, lastTime, System.currentTimeMillis())
            }
        })
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator = DefaultNoAnimator()

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        // 获取新闻列表
        mPresenter?.getVideoList(mTab?.category, 20, lastTime, System.currentTimeMillis())
    }
}