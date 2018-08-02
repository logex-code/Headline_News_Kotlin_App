package com.logex.headlinenews.ui.news

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.logex.fragmentation.anim.DefaultNoAnimator
import com.logex.fragmentation.anim.FragmentAnimator
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.NewsListAdapter
import com.logex.headlinenews.base.MVPBaseFragment
import com.logex.headlinenews.model.NewsListEntity
import com.logex.headlinenews.model.SubscribedEntity
import com.logex.headlinenews.model.event.StartBrotherEvent
import com.logex.pullrefresh.listener.OnPullListener
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_news_list.*
import org.greenrobot.eventbus.EventBus

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 新闻列表页面
 */
class NewsListFragment : MVPBaseFragment<NewsListPresenter>(), NewsListContract.NewsListView {
    private var mAdapter: NewsListAdapter? = null
    private var lastTime = 0L
    private var mList = arrayListOf<NewsListEntity.Content>()
    private var isLoadMore = false // 加载更多是否触发

    override fun onServerFailure() {

    }

    override fun onNetworkFailure() {

    }

    override fun getHomeNewsListSuccess(data: List<NewsListEntity.Content>) {
        if (data.isNotEmpty()) {
            lastTime = data[data.size - 1].behot_time
            if (isLoadMore) {
                if (mTab?.name == "推荐") {
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

        pr_layout.finishRefresh()
    }

    private fun showData(list: List<NewsListEntity.Content>) {
        if (mAdapter == null) {
            mAdapter = NewsListAdapter(context, list, R.layout.recycler_item_news_empty_image,
                    R.layout.recycler_item_news_single_image, R.layout.recycler_item_news_multiple_image,
                    R.layout.recycler_item_news_ad_big_image, R.layout.recycler_item_news_ad_big_image_app,
                    R.layout.recycler_item_news_ad_video_app, R.layout.recycler_item_news_ad_multiple_image_app,
                    R.layout.recycler_item_news_ad_multiple_image, R.layout.recycler_item_news_single_big_image,
                    R.layout.recycler_item_news_video_big_image, R.layout.recycler_item_news_video_single_image)
            //设置布局管理器
            val linearLayoutManager = LinearLayoutManager(mActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            rv_news_list.layoutManager = linearLayoutManager
            rv_news_list.adapter = mAdapter
            mAdapter?.setOnItemClickListener({ _, position ->
                val item = mAdapter?.getItem(position)

                // 拼接详情地址
                val itemId = item?.item_id
                val urlSb = StringBuffer("http://m.toutiao.com/i")
                urlSb.append(itemId).append("/info/")
                val url = urlSb.toString() //http://m.toutiao.com/i6412427713050575361/info/

                val bundle = Bundle()
                bundle.putString(NewsDetailFragment.DETAIL_URL, url)
                bundle.putString(NewsDetailFragment.GROUP_ID, item?.group_id)
                bundle.putString(NewsDetailFragment.ITEM_ID, itemId)
                EventBus.getDefault().post(StartBrotherEvent(NewsDetailFragment.newInstance(bundle)))
            })
        } else {
            mAdapter?.notifyDataSetChanged()
        }
    }

    override fun getHomeNewsListFailure(errInfo: String?) {
        LogUtil.e("获取新闻列表失败>>>>>>" + errInfo)
        pr_layout.finishRefresh()
    }

    override fun createPresenter(): NewsListPresenter {
        return NewsListPresenter(context, this)
    }

    private var mTab: SubscribedEntity? = null

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

        pr_layout.setOnPullListener(object : OnPullListener {

            override fun onMoveTarget(offset: Int) {
                LogUtil.i("onMoveTarget>>>>>>" + offset)
            }

            override fun onMoveRefreshView(offset: Int) {
                LogUtil.i("onMoveRefreshView>>>>>>" + offset)
            }

            override fun onRefresh() {
                lastTime = 0
                isLoadMore = false
                // 获取新闻列表
                mPresenter?.getHomeNewsList(mTab?.category, 20, lastTime, System.currentTimeMillis())
            }

            /*override fun onLoadMore() {
                isLoadMore = true
                // 获取新闻列表
                mPresenter?.getHomeNewsList(mTab?.category, 20, lastTime, System.currentTimeMillis())
            }*/

        })
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator = DefaultNoAnimator()

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        // 获取新闻列表
        mPresenter?.getHomeNewsList(mTab?.category, 20, lastTime, System.currentTimeMillis())
    }
}