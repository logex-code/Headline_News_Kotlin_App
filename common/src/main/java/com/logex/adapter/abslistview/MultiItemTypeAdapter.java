package com.logex.adapter.abslistview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.logex.adapter.abslistview.base.ItemViewDelegate;
import com.logex.adapter.abslistview.base.ItemViewDelegateManager;
import com.logex.adapter.abslistview.base.ViewHolder;
import com.logex.utils.AutoUtils;

import java.util.List;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * AbsListView支持设置不同类型item
 */
public class MultiItemTypeAdapter<T> extends ArrayAdapter<T> {
    protected Context mContext;
    protected List<T> mData;

    private ItemViewDelegateManager<T> mItemViewDelegateManager;

    public MultiItemTypeAdapter(Context context, List<T> data) {
        super(context, 0, data);
        this.mContext = context;
        this.mData = data;
        mItemViewDelegateManager = new ItemViewDelegateManager<>();
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    private boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    @Override
    public int getViewTypeCount() {
        if (useItemViewDelegateManager()) {
            return mItemViewDelegateManager.getItemViewDelegateCount();
        }
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (useItemViewDelegateManager()) {
            return mItemViewDelegateManager.getItemViewType(getItem(position), position);
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(getItem(position), position);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        ViewHolder viewHolder;
        if (convertView == null) {
            View itemView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
            AutoUtils.auto(itemView);

            viewHolder = new ViewHolder(mContext, itemView, parent, position);
            viewHolder.mLayoutId = layoutId;
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mPosition = position;
        }

        mItemViewDelegateManager.convert(viewHolder, getItem(position), position);
        return viewHolder.getConvertView();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
