package com.logex.headlinenews.base;

import android.content.Context;
import android.util.SparseArray;

import io.reactivex.disposables.Disposable;

/**
 * 创建人: Administrator
 * 日期: 2019/4/28
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * BaseViewModel
 */
public abstract class BaseViewModel<P extends BaseProvider> {
    protected Context context;
    protected P mProvider;
    private SparseArray<Observer> mSubjectMap = new SparseArray<>();

    public BaseViewModel(Context context) {
        this.context = context;
        this.mProvider = createProvider();
    }

    public <T> void observe(int key, Observer<T> observer) {
        if (mSubjectMap.get(key) == null) {
            mSubjectMap.put(key, observer);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> void notifyDataChange(int key, T t) {
        if (mSubjectMap != null) {
            mSubjectMap.get(key).onSuccess(t);
        }
    }

    protected void notifyFailure(int key, String errInfo) {
        if (mSubjectMap != null) {
            mSubjectMap.get(key).onFailure(errInfo);
        }
    }

    protected abstract P createProvider();

    protected void addSubscribe(Disposable disposable) {
        mProvider.addSubscribe(disposable);
    }

    /**
     * 销毁view
     */
    protected void detachView() {
        mProvider.unSubscribe();
        mSubjectMap.clear();
        mSubjectMap = null;
    }
}
