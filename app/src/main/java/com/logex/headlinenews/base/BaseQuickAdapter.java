package com.logex.headlinenews.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logex.utils.AutoUtils;

import java.util.List;

/**
 * Created by liguangxi on 16-10-30.
 * RecyclerView通用适配器
 */
public abstract class BaseQuickAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected LayoutInflater mInflater;
    protected int mLayoutId;
    protected Context context;
    protected List<T> mData;

    public BaseQuickAdapter(Context context, List<T> list, int layoutId) {
        this.context = context;
        this.mData = list;
        this.mLayoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        AutoUtils.auto(view);
        return new BaseViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        convertView(holder, mData.get(position), position);
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener == null || holder.getAdapterPosition() == -1) return;
                mOnItemClickListener.onItemClick(holder.convertView, holder.getAdapterPosition());
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

    protected abstract void convertView(BaseViewHolder viewHolder, T item, int position);
}
