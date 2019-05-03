package com.logex.headlinenews.ui.home

import android.content.Context
import com.logex.headlinenews.base.BaseViewModel
import com.logex.headlinenews.base.Callback
import com.logex.headlinenews.model.NewsListEntity

/**
 * 创建人: liguangxi
 * 日期: 2019/5/3
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * MicroNewsViewModel
 **/
class MicroNewsViewModel(context: Context) : BaseViewModel<MicroNewsProvider>(context) {

    companion object {
        /**
         * 获取微头条列表
         */
        const val FETCH_MICRO_NEWS = 1
    }

    override fun createProvider(): MicroNewsProvider = MicroNewsProvider()

    fun getMicroNewsList(category: String?, count: Int, lastTime: Long) {
        mProvider?.getMicroNewsList(category, count, lastTime, System.currentTimeMillis(),
                object : Callback<List<NewsListEntity>> {
                    override fun onSuccess(t: List<NewsListEntity>?) {
                        notifyDataChange(FETCH_MICRO_NEWS, t)
                    }

                    override fun onFailure(errInfo: String?) {
                        notifyFailure(FETCH_MICRO_NEWS, errInfo)
                    }
                })
    }
}