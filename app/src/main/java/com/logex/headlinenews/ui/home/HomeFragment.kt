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

    override fun createViewModel(): HomeViewModel = HomeViewModel(context)

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
        registerObserver(mViewModel?.subscribedRecommendData, Observer { data ->
            LogUtil.i("推荐频道列表>>>>>" + GsonUtil.getInstance().toJson(data))
        })

        registerObserver(mViewModel?.newsSearchSuggestData, Observer { data ->
            LogUtil.i("新闻推荐搜索>>>>>" + GsonUtil.getInstance().toJson(data))

            val suggest = data?.homepage_search_suggest

            if (StringUtil.isEmpty(suggest)) {
                tv_news_suggest.text = "搜你想搜的"
            } else {
                tv_news_suggest.text = suggest
            }
        })

        registerObserver(mViewModel?.newsSubscribedListData, Observer { data ->
            LogUtil.i("新闻标签列表>>>>>>" + GsonUtil.getInstance().toJson(data))

            val list = data?.data

            if (list != null && list.isNotEmpty()) {
                vp_index_news.adapter = HomeNewsPagerAdapter(childFragmentManager, list)
                tab_news.setupWithViewPager(vp_index_news)
            }
        })
    }
}