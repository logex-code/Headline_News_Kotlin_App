package com.logex.headlinenews.base;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 创建人: Administrator
 * 日期: 2019/4/28
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * BaseViewModel
 */
public abstract class BaseViewModel {
    protected Context context;

    private CompositeDisposable compositeDisposable = null;// 管理订阅者者

    // LiveData
    private List<LiveData> mLiveDataList = new ArrayList<>();
    // 提交成功LiveData
    public LiveData<Object> successData = new LiveData<>();
    // 通用错误LiveData
    public LiveData<String> errorData = new LiveData<>();

    public BaseViewModel(Context context) {
        this.context = context;
    }

    protected <T> void observe(LiveData<T> liveData, Observer<T> observer) {
        mLiveDataList.add(liveData);
        liveData.setObserver(observer);
    }

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
     * 销毁view
     */
    protected void detachView() {
        // 销毁Disposable
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        compositeDisposable = null;

        // 销毁LiveData
        for (LiveData liveData : mLiveDataList) {
            liveData.detach();
        }
        mLiveDataList.clear();
        mLiveDataList = null;
    }
}
