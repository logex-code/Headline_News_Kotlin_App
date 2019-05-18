package com.logex.headlinenews.base

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.logex.adapter.recyclerview.wrapper.LoadMoreWrapper
import com.logex.fragmentation.BaseFragment
import com.logex.headlinenews.R
import io.reactivex.disposables.Disposable

/**
 * 创建人: Administrator
 * 日期: 2019/4/28
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * MVVM架构Fragment基类
 */
abstract class MVVMFragment<T : BaseViewModel> : BaseFragment() {
    protected var mViewModel: T? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (mViewModel == null) {
            // 初始化ViewModel
            mViewModel = createViewModel()
        }
        if (mViewModel != null) {
            // 数据观察
            dataObserver()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    protected abstract fun createViewModel(): T

    /**
     * 数据观察
     */
    protected open fun dataObserver() {

    }

    /**
     * 注册观察者
     */
    protected open fun <E> registerObserver(liveData: LiveData<E>?, observer: Observer<E>) {
        mViewModel?.observe(liveData, observer)
    }

    /**
     * 添加订阅
     *
     * @param disposable disposable
     */
    protected fun addSubscribe(disposable: Disposable) {
        mViewModel?.addSubscribe(disposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewModel?.detachView()
        mViewModel = null
    }

    /**
     * 初始化LinearLayoutManager
     *
     * @param recyclerView RecyclerView
     * @param orientation  Sets the orientation of the layout.
     */
    protected fun initLinearLayoutManager(recyclerView: RecyclerView, orientation: Int) {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = orientation
        recyclerView.layoutManager = linearLayoutManager
    }

    /**
     * 创建LoadMoreWrapper
     *
     * @param adapter      RecyclerView.Adapter
     * @param recyclerView RecyclerView
     * @return LoadMoreWrapper
     */
    protected fun createLoadMoreWrapper(adapter: RecyclerView.Adapter<*>?, recyclerView: RecyclerView): LoadMoreWrapper {
        return LoadMoreWrapper(context, adapter, recyclerView)
                .setLoadMoreView(R.layout.view_load_more_footer)
                .setEmptyMoreView(R.layout.view_empty_more_footer)
                .setLoadFailedView(R.layout.view_load_failed_footer)
                .setOnLoadMoreListener { onLoadMore() }
    }

    /**
     * 下拉刷新列表数据
     */
    protected open fun onPullRefresh() = Unit

    /**
     * 加载更多
     */
    protected open fun onLoadMore() = Unit

    protected fun resetListLoadMore(loadMoreWrapper: LoadMoreWrapper?) {
        loadMoreWrapper?.showLoadMode(LoadMoreWrapper.LOAD_MORE)
    }

    protected fun showListEmptyMore(loadMoreWrapper: LoadMoreWrapper?) {
        loadMoreWrapper?.showLoadMode(LoadMoreWrapper.EMPTY_MORE)
    }

    protected fun showLoadMoreFailed(loadMoreWrapper: LoadMoreWrapper?) {
        loadMoreWrapper?.showLoadMode(LoadMoreWrapper.LOAD_FAILED)
    }
}
