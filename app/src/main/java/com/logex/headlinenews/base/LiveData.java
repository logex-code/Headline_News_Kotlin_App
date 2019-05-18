package com.logex.headlinenews.base;

/**
 * 创建人: Administrator
 * 日期: 2019/5/6
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * LiveData
 */
public class LiveData<T> {
    private Observer<T> observer;

    protected void setObserver(Observer<T> observer) {
        this.observer = observer;
    }

    public void setValue(T t) {
        if (observer != null) {
            observer.onChange(t);
        }
    }

    protected void detach() {
        observer = null;
    }
}
