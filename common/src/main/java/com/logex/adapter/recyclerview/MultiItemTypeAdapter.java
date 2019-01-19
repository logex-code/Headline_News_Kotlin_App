package com.logex.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logex.adapter.recyclerview.base.ItemViewDelegate;
import com.logex.adapter.recyclerview.base.ItemViewDelegateManager;
import com.logex.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * RecyclerView支持设置不同类型item
 */
public class MultiItemTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected List<T> mData;

    protected int mLayoutId;

    protected LayoutInflater mInflater;

    protected ItemViewDelegateManager<T> mItemViewDelegateManager;
    protected OnItemClickListener mOnItemClickListener;

    public MultiItemTypeAdapter(Context context, List<T> data, int layoutId) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;

        mInflater = LayoutInflater.from(context);
        mItemViewDelegateManager = new ItemViewDelegateManager<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (!useItemViewDelegateManager()) {
            return super.getItemViewType(position);
        }
        return mItemViewDelegateManager.getItemViewType(getItem(position), position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);

        return ViewHolder.createViewHolder(mContext, parent, itemViewDelegate.getItemViewLayoutId());
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mItemViewDelegateManager.convert(holder, getItem(position), holder.getAdapterPosition());

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.getConvertView(), holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public T getItem(int position) {
        return mData.get(position);
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

    public List<T> getData() {
        return mData;
    }

    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    protected OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }
}
