package com.logex.adapter.recyclerview.wrapper;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.logex.adapter.recyclerview.base.ViewHolder;
import com.logex.adapter.recyclerview.utils.WrapperUtils;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * LoadMoreWrapper
 */
public class LoadMoreWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_LOAD_MORE = 10002;
    private static final int ITEM_TYPE_EMPTY_MORE = 10003;

    private Context mContext;
    private RecyclerView.Adapter mInnerAdapter;

    private View mLoadMoreView, mEmptyMoreView;
    private int mLoadMoreLayoutId, mEmptyMoreLayoutId;

    private boolean isShowEmptyMore = false;

    public LoadMoreWrapper(Context context, RecyclerView.Adapter adapter) {
        this.mContext = context;
        this.mInnerAdapter = adapter;
    }

    private boolean hasLoadMore() {
        return mLoadMoreView != null || mLoadMoreLayoutId != 0;
    }

    private boolean hasEmptyMore() {
        return mEmptyMoreView != null || mEmptyMoreLayoutId != 0;
    }

    private boolean isShowLoadMore(int position) {
        //LogUtil.i("position>>>>" + position + "\nmDataSize>>>>" + mInnerAdapter.getItemCount());

        return hasLoadMore() && (position >= mInnerAdapter.getItemCount());
    }

    private boolean isShowEmptyMore(int position) {
        return hasEmptyMore() && (position >= mInnerAdapter.getItemCount()) && isShowEmptyMore;
    }

    public void setShowEmptyMore(boolean showEmptyMore) {
        isShowEmptyMore = showEmptyMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowEmptyMore(position)) {
            return ITEM_TYPE_EMPTY_MORE;
        } else if (isShowLoadMore(position)) {
            return ITEM_TYPE_LOAD_MORE;
        }
        return mInnerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_EMPTY_MORE) {
            ViewHolder holder;
            if (mEmptyMoreView != null) {
                holder = ViewHolder.createViewHolder(mContext, mEmptyMoreView);
            } else {
                holder = ViewHolder.createViewHolder(mContext, parent, mEmptyMoreLayoutId);
            }
            return holder;
        } else if (viewType == ITEM_TYPE_LOAD_MORE) {
            ViewHolder holder;
            if (mLoadMoreView != null) {
                holder = ViewHolder.createViewHolder(mContext, mLoadMoreView);
            } else {
                holder = ViewHolder.createViewHolder(mContext, parent, mLoadMoreLayoutId);
            }
            return holder;
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isShowEmptyMore(position)) {
            return;
        } else if (isShowLoadMore(position)) {
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMore();
            }
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                if (isShowLoadMore(position)) {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null) {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        if (isShowLoadMore(holder.getLayoutPosition())) {
            setFullSpan(holder);
        }
    }

    private void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

            p.setFullSpan(true);
        }
    }

    @Override
    public int getItemCount() {
        return mInnerAdapter.getItemCount() + (hasLoadMore() ? 1 : 0);
    }

    public interface OnLoadMoreListener {

        /**
         * 加载更多
         */
        void onLoadMore();
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public LoadMoreWrapper setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if (loadMoreListener != null) {
            mOnLoadMoreListener = loadMoreListener;
        }
        return this;
    }

    /**
     * 设置加载更多view
     *
     * @param loadMoreView 加载更多view
     * @return this
     */
    public LoadMoreWrapper setLoadMoreView(View loadMoreView) {
        mLoadMoreView = loadMoreView;
        return this;
    }

    public LoadMoreWrapper setLoadMoreView(int layoutId) {
        mLoadMoreLayoutId = layoutId;
        return this;
    }

    /**
     * 设置没有更多显示view
     *
     * @param emptyMoreView 没有更多view
     * @return this
     */
    public LoadMoreWrapper setEmptyMoreView(View emptyMoreView) {
        mEmptyMoreView = emptyMoreView;
        return this;
    }

    public LoadMoreWrapper setEmptyMoreView(int layoutId) {
        mEmptyMoreLayoutId = layoutId;
        return this;
    }
}
