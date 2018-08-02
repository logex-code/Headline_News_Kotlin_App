package com.logex.headlinenews.ui.home

import android.os.Bundle
import android.text.TextUtils
import com.logex.headlinenews.R
import com.logex.headlinenews.adapter.HomeNewsPagerAdapter
import com.logex.headlinenews.base.MVPBaseFragment
import com.logex.headlinenews.model.HomeNewsSubscribed
import com.logex.headlinenews.model.HomeSearchSuggest
import com.logex.headlinenews.model.SubscribedEntity
import com.logex.headlinenews.model.SubscribedRecommend
import com.logex.utils.GsonUtil
import com.logex.utils.LogUtil
import com.logex.utils.ValidateUtil
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 首页
 */
class HomeFragment : MVPBaseFragment<HomePresenter>(), HomeContract.HomeView {

    override fun getSubscribedRecommendListSuccess(data: SubscribedRecommend?) {
        LogUtil.i("推荐频道列表>>>>>"+GsonUtil.getInstance().toJson(data))
    }

    override fun getSubscribedRecommendListFailure(errInfo: String?) {
        LogUtil.e("获取频道推荐错误>>>>>"+errInfo)
    }

    override fun onServerFailure() {

    }

    override fun onNetworkFailure() {

    }

    override fun getHomeNewsSearchSuggestSuccess(data: HomeSearchSuggest?) {
        LogUtil.i("新闻推荐搜索>>>>>" + GsonUtil.getInstance().toJson(data))

        val suggest = data?.homepage_search_suggest

        if (TextUtils.isEmpty(suggest)) {
            tv_news_suggest.text = "搜你想搜的"
        } else {
            tv_news_suggest.text = suggest
        }
    }

    override fun getHomeNewsSearchSuggestFailure(errInfo: String?) {
        LogUtil.e("获取新闻推荐搜索失败>>>>>>>>" + errInfo)
    }

    override fun getHomeNewsSubscribedListSuccess(data: HomeNewsSubscribed?) {
        LogUtil.i("新闻标签列表>>>>>>" + GsonUtil.getInstance().toJson(data))

        if (data == null) return
        val list = data.data

        if (ValidateUtil.isListNonEmpty(list)) {
            //新增推荐tab
            val item = SubscribedEntity("", null, null, "推荐", null, null, null, null, null)
            list.add(0, item)

            vp_index_news.adapter = HomeNewsPagerAdapter(childFragmentManager, list)
            tab_news.setupWithViewPager(vp_index_news)
        }
    }

    override fun getHomeNewsSubscribedListFailure(errInfo: String?) {
        LogUtil.e("获取新闻标签列表失败>>>>>" + errInfo)
    }

    override fun createPresenter(): HomePresenter {
        return HomePresenter(context, this)
    }

    companion object {

        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun viewCreate(savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.colorPrimary)


    }

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)

        mPresenter?.getHomeNewsSearchSuggest()
        mPresenter?.getHomeNewsSubscribedList()

        mPresenter?.getSubscribedRecommendList()
    }
}