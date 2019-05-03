package com.logex.headlinenews.ui.home

import android.os.Bundle
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.HomeNewsPagerAdapter
import com.logex.headlinenews.base.MVVMFragment
import com.logex.headlinenews.base.Observer
import com.logex.headlinenews.model.HomeNewsSubscribed
import com.logex.headlinenews.model.HomeSearchSuggest
import com.logex.headlinenews.model.SubscribedEntity
import com.logex.headlinenews.model.SubscribedRecommend
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import com.logex.utils.StringUtil
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 首页
 */
class HomeFragment : MVVMFragment<HomeViewModel>() {

    companion object {

        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun createViewModel(): HomeViewModel  = HomeViewModel(context)

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun viewCreate(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.colorPrimary)
    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)

        mViewModel?.getHomeNewsSearchSuggest()
        mViewModel?.getHomeNewsSubscribedList()

        mViewModel?.getSubscribedRecommendList()
    }

    override fun dataObserver() {
        super.dataObserver()
        mViewModel?.observe(HomeViewModel.FETCH_SUBSCRIBED_RECOMMEND,object : Observer<SubscribedRecommend>{
            override fun onSuccess(data: SubscribedRecommend?) {
                LogUtil.i("推荐频道列表>>>>>" + GsonUtil.getInstance().toJson(data))
            }

            override fun onFailure(errInfo: String?) {
                LogUtil.e("获取频道推荐错误>>>>>$errInfo")
            }
        })

        mViewModel?.observe(HomeViewModel.FETCH_SEARCH_SUGGEST,object : Observer<HomeSearchSuggest>{
            override fun onSuccess(data: HomeSearchSuggest?) {
                LogUtil.i("新闻推荐搜索>>>>>" + GsonUtil.getInstance().toJson(data))

                val suggest = data?.homepage_search_suggest

                if (StringUtil.isEmpty(suggest)) {
                    tv_news_suggest.text = "搜你想搜的"
                } else {
                    tv_news_suggest.text = suggest
                }
            }

            override fun onFailure(errInfo: String?) {
                LogUtil.e("获取新闻推荐搜索失败>>>>>>>>$errInfo")
            }
        })

        mViewModel?.observe(HomeViewModel.FETCH_NEWS_SUBSCRIBED,object : Observer<HomeNewsSubscribed>{
            override fun onSuccess(data: HomeNewsSubscribed?) {
                LogUtil.i("新闻标签列表>>>>>>" + GsonUtil.getInstance().toJson(data))

                val list = data?.data

                if (list != null && list.isNotEmpty()) {
                    vp_index_news.adapter = HomeNewsPagerAdapter(childFragmentManager, list)
                    tab_news.setupWithViewPager(vp_index_news)
                }
            }

            override fun onFailure(errInfo: String?) {
                LogUtil.e("获取新闻标签列表失败>>>>>$errInfo")
            }
        })
    }
}