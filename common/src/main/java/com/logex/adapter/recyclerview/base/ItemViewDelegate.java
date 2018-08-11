package com.logex.adapter.recyclerview.base;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * ItemViewDelegate
 */
public interface ItemViewDelegate<T> {

    /**
     * 获取该条目布局id
     *
     * @return 布局id
     */
    int getItemViewLayoutId();

    /**
     * 约束条件
     *
     * @param item     条目数据
     * @param position 位置
     * @return true显示该条目 false不显示
     */
    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);
}
