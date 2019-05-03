package com.logex.headlinenews.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 创建人: Administrator
 * 日期: 2019/4/28
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * 数据提供基础类
 */
abstract public class BaseProvider {
    private CompositeDisposable compositeDisposable = null;// 管理订阅者者

    /**
     * 添加订阅
     *
     * @param disposable disposable
     */
    protected void addSubscribe(Disposable disposable) {
        if (disposable == null) return;
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    /**
     * 解除订阅
     */
    protected void unSubscribe() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        compositeDisposable = null;
    }
}
