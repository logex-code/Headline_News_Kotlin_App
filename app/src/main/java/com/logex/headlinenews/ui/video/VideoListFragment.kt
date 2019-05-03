package com.logex.headlinenews.ui.video

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.logex.adapter.recyclerview.wrapper.LoadMoreWrapper
import com.logex.fragmentation.anim.DefaultNoAnimator
import com.logex.fragmentation.anim.FragmentAnimator
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.VideoListAdapter
import com.logex.headlinenews.base.MVVMFragment
import com.logex.headlinenews.base.Observer
import com.logex.headlinenews.model.NewsListEntity
import com.logex.headlinenews.model.VideoCategoryEntity
import com.logex.pullrefresh.listener.PullRefreshListener
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import com.logex.videoplayer.JCVideoPlayer
import kotlinx.android.synthetic.main.fragment_video_list.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 视频列表页面
 */
class VideoListFragment : MVVMFragment<VideoViewModel>() {
    private var mAdapter: VideoListAdapter? = null
    private var mLoadMoreWrapper: LoadMoreWrapper? = null
    private var lastTime = 0L
    private var mList = arrayListOf<NewsListEntity>()
    private var isLoadMore = false // 加载更多是否触发

    private var mTab: VideoCategoryEntity? = null

    companion object {

        fun newInstance(args: Bundle): VideoListFragment {
            val fragment = VideoListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun createViewModel(): VideoViewModel = VideoViewModel(context)

    override fun getLayoutId(): Int = R.layout.fragment_video_list

    override fun viewCreate(savedInstanceState: Bundle?) {
        mTab = arguments.getParcelable("tab")

        LogUtil.i("当前标签信息>>>>>>" + GsonUtil.getInstance().toJson(mTab))

        pr_layout.setPullRefreshListener(object : PullRefreshListener() {
            override fun onRefresh() {
                onPullRefresh()
            }
        })
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator = DefaultNoAnimator()

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        // 获取新闻列表
        mViewModel?.getVideoList(mTab?.category, 20, lastTime)
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        JCVideoPlayer.onPauseVideo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        JCVideoPlayer.releaseAllVideos()
    }

    override fun onPullRefresh() {
        super.onPullRefresh()
        lastTime = 0
        isLoadMore = false
        // 获取视频列表
        mViewModel?.getVideoList(mTab?.category, 20, lastTime)
    }

    override fun onLoadMore() {
        super.onLoadMore()
        lastTime = mList[mList.size - 1].behot_time
        isLoadMore = true
        // 获取视频列表
        mViewModel?.getVideoList(mTab?.category, 20, lastTime)
    }

    override fun dataObserver() {
        super.dataObserver()
        mViewModel?.observe(VideoViewModel.FETCH_VIDEO_NEWS, object : Observer<List<NewsListEntity>> {
            override fun onSuccess(data: List<NewsListEntity>?) {
                LogUtil.i("视频列表>>>>>>" + GsonUtil.getInstance().toJson(data))

                pr_layout.finishRefresh()

                if (data != null && data.isNotEmpty()) {
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

            override fun onFailure(errInfo: String?) {
                LogUtil.e("获取视频列表失败>>>>>>$errInfo")

                pr_layout.finishRefresh()
                showLoadMoreFailed(mLoadMoreWrapper)
            }
        })
    }

    private fun showData(list: List<NewsListEntity>) {
        if (mAdapter == null) {
            mAdapter = VideoListAdapter(context, list, R.layout.recycler_item_video_big_image)

            //设置布局管理器
            initLinearLayoutManager(rv_video_list, LinearLayoutManager.VERTICAL)

            mLoadMoreWrapper = createLoadMoreWrapper(mAdapter, rv_video_list)

            rv_video_list.adapter = mLoadMoreWrapper

            rv_video_list.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
                override fun onChildViewDetachedFromWindow(view: View?) {
                    JCVideoPlayer.onChildViewAttachedToWindow()
                }

                override fun onChildViewAttachedToWindow(view: View?) {

                }
            })
        } else {
            mLoadMoreWrapper?.notifyDataSetChanged()
        }
    }
}