package com.logex.adapter.recyclerview.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

/**
 * 创建人: Administrator
 * 日期: 2018/9/14
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * WrapperUtils
 */
public class WrapperUtils {

    public interface SpanSizeCallback {

        int getSpanSize(GridLayoutManager layoutManager, int position);
    }

    public static void onAttachedToRecyclerView(RecyclerView recyclerView, final SpanSizeCallback callback) {

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return callback.getSpanSize(gridLayoutManager, position);
                }
            });
        }
    }

    public static void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {

            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

            p.setFullSpan(true);
        }
    }
}
