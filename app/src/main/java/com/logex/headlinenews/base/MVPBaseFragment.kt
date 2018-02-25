package com.logex.headlinenews.base

import android.os.Bundle
import android.view.View
import com.logex.fragmentation.BaseFragment
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