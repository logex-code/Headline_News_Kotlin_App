package com.logex.headlinenews.base;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 创建人: liguangxi
 * 日期: 18-3-13
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 为RxBus使用的Observer, 主要提供next事件的try,catch
 */
public abstract class RxBusObserver<T> implements Observer<T>, Disposable {
    protected Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(T t) {
        try {
            onEvent(t);
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public final boolean isDisposed() {
        return disposable.isDisposed();
    }

    @Override
    public final void dispose() {
        disposable.dispose();
    }

    public abstract void onEvent(T t);
}
