package com.logex.adapter.recyclerview.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.logex.utils.AutoUtils;
import com.logex.utils.GlideCircleTransform;
import com.logex.utils.GlideRoundTransform;

import java.io.File;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * RecyclerView ViewHolder
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;

    public ViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<>();
    }

    public static ViewHolder createViewHolder(Context context, View itemView) {
        AutoUtils.auto(itemView);
        return new ViewHolder(context, itemView);
    }

    public static ViewHolder createViewHolder(Context context, ViewGroup parent, int layoutId) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        AutoUtils.auto(itemView);
        return new ViewHolder(context, itemView);
    }

    // 通过viewId获取控件
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public String getString(@StringRes int string) {
        return mContext.getResources().getString(string);
    }

    public ViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public ViewHolder setText(int viewId, @StringRes int resId) {
        TextView view = this.getView(viewId);
        view.setText(resId);
        return this;
    }

    //设置文字是否加粗
    public ViewHolder setTextIsBold(int viewId, boolean isBold) {
        TextView view = this.getView(viewId);
        view.setTypeface(Typeface.defaultFromStyle(isBold ? Typeface.BOLD : Typeface.NORMAL));
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    //设置imageDrawable从网上
    public ViewHolder setImageResourcesUrl(int viewId, String url, int id) {
        ImageView view = getView(viewId);
        Glide.with(mContext).load(url)
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(id)
                .into(view);
        return this;
    }

    //设置imageDrawable从网上(圆形)
    public ViewHolder setCircleImageResourcesUrl(int viewId, String url, int id) {
        ImageView view = getView(viewId);
        Glide.with(mContext).load(url)
                .transform(new GlideCircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(id)
                .into(view);
        return this;
    }

    //设置imageDrawable从网上(圆形带边框)
    public ViewHolder setCircleImageResourcesUrl(int viewId, String url, int id, int borderWidth, int borderColor) {
        ImageView view = getView(viewId);
        Glide.with(mContext).load(url)
                .transform(new GlideCircleTransform(mContext, borderWidth, borderColor))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(id)
                .into(view);
        return this;
    }

    //设置imageDrawable从网上(圆角)
    public ViewHolder setRoundImageResourcesUrl(int viewId, String url, int id, int radius) {
        ImageView view = getView(viewId);
        Glide.with(mContext).load(url)
                .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext, radius))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(id)
                .into(view);
        return this;
    }

    public ViewHolder setImageAlpha(int viewId, float alpha) {
        ImageView view = getView(viewId);
        view.setAlpha(alpha);
        return this;
    }

    //设置imageDrawable从文件路径
    public ViewHolder setImageResourcesFile(int viewId, String path, int id) {
        ImageView view = getView(viewId);
        Glide.with(mContext).load(new File(path))
                .placeholder(id)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(view);
        return this;
    }

    public ViewHolder setImageLevel(int viewId, int level) {
        ImageView view = getView(viewId);
        view.setImageLevel(level);
        return this;
    }

    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    public ViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(mContext.getResources().getColor(color));
        return this;
    }

    public ViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public ViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(mContext.getResources().getColor(textColor));
        return this;
    }

    public ViewHolder setAlpha(int viewId, float value) {
        getView(viewId).setAlpha(value);
        return this;
    }

    public ViewHolder setEnabled(int viewId, boolean enabled) {
        View view = getView(viewId);
        view.setEnabled(enabled);
        return this;
    }

    // 设置通用选中
    public ViewHolder setCommonSelected(int viewId, boolean selected) {
        View view = getView(viewId);
        view.setSelected(selected);
        return this;
    }

    // 设置图片选中
    public ViewHolder setSelected(int viewId, boolean selected) {
        ImageView view = getView(viewId);
        view.setSelected(selected);
        return this;
    }

    public ViewHolder setTextSelected(int viewId, boolean selected) {
        TextView view = getView(viewId);
        view.setSelected(selected);
        return this;
    }

    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public ViewHolder linkify(int viewId) {
        TextView view = getView(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    public ViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    public ViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = getView(viewId);
        view.setProgress(progress);
        return this;
    }

    public ViewHolder setProgress(int viewId, int progress, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    public ViewHolder setMax(int viewId, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        return this;
    }

    public ViewHolder setRating(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

    public ViewHolder setRating(int viewId, float rating, int max) {
        RatingBar view = getView(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }

    public ViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    public ViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    public ViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    /**
     * 关于事件的
     */
    public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public ViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    public ViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }
}
