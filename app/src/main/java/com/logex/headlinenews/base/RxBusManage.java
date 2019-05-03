package com.logex.headlinenews.base;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * 创建人: Administrator
 * 日期: 2019/4/25
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * RxBusManage
 */
public class RxBusManage {
    private static volatile RxBusManage instance;
    private SparseArray<List<Disposable>> mBusDisposable = new SparseArray<>();

    public static RxBusManage getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBusManage();
                }
            }
        }
        return instance;
    }

    /**
     * 添加订阅
     *
     * @param key        key
     * @param disposable disposable
     */
    public void addSubscribe(int key, Disposable disposable) {
        if (disposable.isDisposed()) return;

        List<Disposable> resources = mBusDisposable.get(key);
        if (resources == null) {
            resources = new ArrayList<>();
            resources.add(disposable);
            mBusDisposable.put(key, resources);
        } else {
            resources.add(disposable);
        }
    }

    /**
     * 解除订阅
     *
     * @param key key
     */
    public void unSubscribe(int key) {
        List<Disposable> resources = mBusDisposable.get(key);
        if (resources != null) {
            for (Disposable d : resources) {
                if (!d.isDisposed()) {
                    d.dispose();
                }
            }
            mBusDisposable.remove(key);
        }
    }
}
