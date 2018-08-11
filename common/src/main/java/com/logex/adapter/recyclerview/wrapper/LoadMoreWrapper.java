package com.logex.adapter.recyclerview.wrapper;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.logex.adapter.recyclerview.base.ViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * LoadMoreWrapper
 */
public class LoadMoreWrapper extends RecyclerView.Adapter {
    private static final int ITEM_TYPE_LOAD_MORE = 10002;
    private static final int ITEM_TYPE_EMPTY_MORE = 10003;
    private static final int ITEM_TYPE_LOAD_FAILED = 10004;
    private static final int ITEM_TYPE_LOAD_NONE = 10005;

    /**
     * 加载更多中
     */
    public static final int LOAD_MORE = 0;
    /**
     * 无数据了
     */
    public static final int EMPTY_MORE = 1;
    /**
     * 加载失败
     */
    public static final int LOAD_FAILED = 2;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mInnerAdapter;

    private View mLoadMoreView, mEmptyMoreView, mLoadFailedView;
    private int mLoadMoreLayoutId, mEmptyMoreLayoutId, mLoadFailedLayoutId;

    private int mLoadMode = LOAD_MORE;

    @IntDef({LOAD_MORE, EMPTY_MORE, LOAD_FAILED})
    @Retention(RetentionPolicy.SOURCE)
    @interface LoadingMode {
    }

    public LoadMoreWrapper(Context context, RecyclerView.Adapter adapter, RecyclerView recyclerView) {
        this.mContext = context;
        this.mInnerAdapter = adapter;
        this.mRecyclerView = recyclerView;
    }

    private boolean hasLoadMore() {
        return mLoadMoreView != null || mLoadMoreLayoutId != 0;
    }

    private boolean hasEmptyMore() {
        return mEmptyMoreView != null || mEmptyMoreLayoutId != 0;
    }

    private boolean hasLoadFailed() {
        return mLoadFailedView != null || mLoadFailedLayoutId != 0;
    }

    /**
     * 是否显示完成加载的数据
     *
     * @param position position
     * @return true false
     */
    private boolean isShowDataDone(int position) {

        return position >= mInnerAdapter.getItemCount();
    }

    /**
     * 是否滚动到底部了
     *
     * @return true false
     */
    private boolean isScrollToBottom() {

        return mRecyclerView.computeVerticalScrollOffset() != 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowDataDone(position)) {

            if (!isScrollToBottom()) return ITEM_TYPE_LOAD_NONE;

            switch (mLoadMode) {
                case LOAD_MORE:
                    return hasLoadMore() ? ITEM_TYPE_LOAD_MORE : ITEM_TYPE_LOAD_NONE;
                case EMPTY_MORE:
                    return hasEmptyMore() ? ITEM_TYPE_EMPTY_MORE : ITEM_TYPE_LOAD_NONE;
                case LOAD_FAILED:
                    return hasLoadFailed() ? ITEM_TYPE_LOAD_FAILED : ITEM_TYPE_LOAD_NONE;
            }
        }
        return mInnerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;
        switch (viewType) {
            case ITEM_TYPE_EMPTY_MORE:
                // 创建没有更多了ViewHolder
                if (mEmptyMoreView != null) {
                    holder = ViewHolder.createViewHolder(mContext, mEmptyMoreView);
                } else {
                    holder = ViewHolder.createViewHolder(mContext, parent, mEmptyMoreLayoutId);
                }
                return holder;
            case ITEM_TYPE_LOAD_MORE:
                if (mLoadMoreView != null) {
                    holder = ViewHolder.createViewHolder(mContext, mLoadMoreView);
                } else {
                    holder = ViewHolder.createViewHolder(mContext, parent, mLoadMoreLayoutId);
                }
                return holder;
            case ITEM_TYPE_LOAD_FAILED:
                if (mLoadFailedView != null) {
                    holder = ViewHolder.createViewHolder(mContext, mLoadFailedView);
                } else {
                    holder = ViewHolder.createViewHolder(mContext, parent, mLoadFailedLayoutId);
                }
                return holder;
            case ITEM_TYPE_LOAD_NONE:
                return ViewHolder.createViewHolder(mContext, new View(mContext));
            default:
                return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isShowDataDone(position)) {
            if (!isScrollToBottom()) return;
            switch (mLoadMode) {
                case LOAD_MORE:
                    // 延时500ms触发
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnLoadMoreListener != null) {
                                mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    }, 500);
                    break;
                case LOAD_FAILED:
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 重新加载更多
                            mLoadMode = LOAD_MORE;
                            notifyDataSetChanged();
                        }
                    });
                    break;
            }
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mInnerAdapter.getItemCount() + 1;
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

    /**
     * 设置没有更多显示view
     *
     * @param layoutId 布局id
     * @return this
     */
    public LoadMoreWrapper setEmptyMoreView(int layoutId) {
        mEmptyMoreLayoutId = layoutId;
        return this;
    }

    public LoadMoreWrapper setLoadFailedView(View loadFailedView) {
        mLoadFailedView = loadFailedView;
        return this;
    }

    public LoadMoreWrapper setLoadFailedView(int layoutId) {
        mLoadFailedLayoutId = layoutId;
        return this;
    }

    /**
     * 显示不同加载模式
     *
     * @param loadingMode 加载模式
     */
    public void showLoadMode(@LoadingMode int loadingMode) {
        if (loadingMode == LOAD_MORE) {
            this.mLoadMode = loadingMode;
        } else if (isScrollToBottom()) {
            this.mLoadMode = loadingMode;
            notifyDataSetChanged();
        }
    }
}
