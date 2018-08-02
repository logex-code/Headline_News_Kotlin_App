package com.logex.adapter.abslistview;

import android.content.Context;

import com.logex.adapter.abslistview.base.ItemViewDelegate;
import com.logex.adapter.abslistview.base.ViewHolder;

import java.util.List;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * AbsListView通用adapter
 */
public abstract class CommonAdapter<T> extends MultiItemTypeAdapter<T> {

    public CommonAdapter(Context context, List<T> data, final int layoutId) {
        super(context, data);

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, T t, int position) {
                convertView(holder, t, position);
            }
        });
    }

    protected abstract void convertView(ViewHolder viewHolder, T item, int position);
}
