package com.logex.adapter.recyclerview.wrapper;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.logex.adapter.recyclerview.base.ViewHolder;
import com.logex.adapter.recyclerview.utils.WrapperUtils;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * EmptyWrapper
 */
public class EmptyWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_EMPTY = 10001;

    private Context mContext;
    private RecyclerView.Adapter mInnerAdapter;
    private View mEmptyView;
    private int mEmptyLayoutId;

    public EmptyWrapper(Context context, RecyclerView.Adapter adapter) {
        this.mContext = context;
        this.mInnerAdapter = adapter;
    }

    public boolean isEmpty() {
        return (mEmptyView != null || mEmptyLayoutId != 0) && mInnerAdapter.getItemCount() == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmpty()) {
            return ITEM_TYPE_EMPTY;
        }
        return mInnerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_EMPTY:
                ViewHolder holder;
                if (mEmptyView != null) {
                    holder = ViewHolder.createViewHolder(mContext, mEmptyView);
                } else {
                    holder = ViewHolder.createViewHolder(mContext, parent, mEmptyLayoutId);
                }
                return holder;
            default:
                return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isEmpty()) {
            convertEmptyView(holder.itemView);
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mInnerAdapter.onAttachedToRecyclerView(recyclerView);
        WrapperUtils.onAttachedToRecyclerView(recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                if (isEmpty()) {
                    return gridLayoutManager.getSpanCount();
                }
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        if (isEmpty()) {
            WrapperUtils.setFullSpan(holder);
        }
    }

    @Override
    public int getItemCount() {
        return isEmpty() ? 1 : mInnerAdapter.getItemCount();
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    public void setEmptyView(int layoutId) {
        mEmptyLayoutId = layoutId;
    }

    public void convertEmptyView(View emptyView) {

    }
}
