package com.logex.headlinenews.base

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.logex.adapter.recyclerview.wrapper.LoadMoreWrapper
import com.logex.fragmentation.BaseFragment
import com.logex.headlinenews.R
import com.logex.utils.LogUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * MVP模式BaseFragment
 */
abstract class MVPBaseFragment<T : BaseViewPresenter<*>> : BaseFragment() {
    protected var mPresenter: T? = null
    protected var compositeDisposable: CompositeDisposable? = null // 管理订阅者者

    /**
     * 添加订阅
     *
     * @param disposable 订阅者
     */
    protected fun addDisposable(disposable: Disposable?) {
        if (disposable == null) return
        if (compositeDisposable == null) {
            // 新建订阅管理
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(disposable)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        // 初始化presenter
        mPresenter = createPresenter()
        super.onViewCreated(view, savedInstanceState)
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
    protected fun createLoadMoreWrapper(adapter: RecyclerView.Adapter<*>?, recyclerView: RecyclerView): LoadMoreWrapper =
            LoadMoreWrapper(context, adapter, recyclerView)
                    .setLoadMoreView(R.layout.view_load_more_footer)
                    .setEmptyMoreView(R.layout.view_empty_more_footer)
                    .setLoadFailedView(R.layout.view_load_failed_footer)
                    .setOnLoadMoreListener({
                        LogUtil.i("执行加载更多中..........")
                        onLoadMore()
                    })

    /**
     * 下拉刷新列表数据
     */
    open protected fun onPullRefresh() = Unit

    /**
     * 加载更多
     */
    open protected fun onLoadMore() = Unit

    protected fun resetListLoadMore(loadMoreWrapper: LoadMoreWrapper?) {
        loadMoreWrapper?.showLoadMode(LoadMoreWrapper.LOAD_MORE)
    }

    protected fun showListEmptyMore(loadMoreWrapper: LoadMoreWrapper?) {
        loadMoreWrapper?.showLoadMode(LoadMoreWrapper.EMPTY_MORE)
    }

    protected fun showLoadMoreFailed(loadMoreWrapper: LoadMoreWrapper?) {
        loadMoreWrapper?.showLoadMode(LoadMoreWrapper.LOAD_FAILED)
    }

    /**
     * 创建presenter
     *
     * @return presenter
     */
    protected abstract fun createPresenter(): T

    override fun onDestroyView() {
        super.onDestroyView()

        mPresenter?.detachView()
        compositeDisposable?.dispose()
        compositeDisposable?.clear()
        compositeDisposable = null
    }
}