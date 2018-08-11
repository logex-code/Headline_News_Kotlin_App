package com.logex.adapter.abslistview;

import android.content.Context;
import android.support.annotation.StringRes;

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

    //添加数据
    public void add(T elem) {
        mData.add(elem);
        notifyDataSetChanged();
    }

    //添加所有数据
    public void addAll(List<T> elem) {
        mData.addAll(elem);
        notifyDataSetChanged();
    }

    //删除数据
    public void remove(T elem) {
        mData.remove(elem);
        notifyDataSetChanged();
    }

    //根据指定位置删除数据
    public void remove(int index) {
        mData.remove(index);
        notifyDataSetChanged();
    }

    //clear数据
    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    protected abstract void convertView(ViewHolder viewHolder, T item, int position);

    protected String getString(@StringRes int id) {
        return mContext.getString(id);
    }
}
