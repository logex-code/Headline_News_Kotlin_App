package com.logex.adapter.recyclerview;

import android.content.Context;
import android.support.annotation.StringRes;

import com.logex.adapter.recyclerview.base.ItemViewDelegate;
import com.logex.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * RecyclerView通用adapter
 */
public abstract class CommonAdapter<T> extends MultiItemTypeAdapter<T> {

    public CommonAdapter(final Context context, List<T> data, int layoutId) {
        super(context, data, layoutId);

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return mLayoutId;
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

    public String getString(@StringRes int string) {
        return mContext.getResources().getString(string);
    }
}
