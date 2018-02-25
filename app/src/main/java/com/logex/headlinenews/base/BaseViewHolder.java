package com.logex.headlinenews.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.logex.utils.GlideCircleTransform;
import com.logex.utils.GlideRoundTransform;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by liguangxi on 16-10-30.
 * RecyclerView BaseViewHolder
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    public Context context;
    private final SparseArray<View> views = new SparseArray();
    private final LinkedHashSet<Integer> childClickViewIds = new LinkedHashSet();
    private final LinkedHashSet<Integer> itemChildLongClickViewIds = new LinkedHashSet();
    public View convertView;
    private Object associatedObject;

    public BaseViewHolder(View view, Context context) {
        super(view);
        this.convertView = view;
        this.context = context;
    }

    public HashSet<Integer> getItemChildLongClickViewIds() {
        return this.itemChildLongClickViewIds;
    }

    public HashSet<Integer> getChildClickViewIds() {
        return this.childClickViewIds;
    }

    public View getConvertView() {
        return this.convertView;
    }

    public BaseViewHolder setText(int viewId, CharSequence value) {
        TextView view = this.getView(viewId);
        view.setText(value);
        return this;
    }

    public BaseViewHolder setText(int viewId, @StringRes int strId) {
        TextView view = this.getView(viewId);
        view.setText(strId);
        return this;
    }

    //设置文字是否加粗
    public BaseViewHolder setTextIsBold(int viewId, boolean isBold) {
        TextView view = this.getView(viewId);
        view.setTypeface(Typeface.defaultFromStyle(isBold ? Typeface.BOLD : Typeface.NORMAL));
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, @DrawableRes int imageResId) {
        ImageView view = this.getView(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    public String getString(@StringRes int string) {
        return context.getResources().getString(string);
    }

    //设置imageDrawable从网上
    public BaseViewHolder setImageResourcesUrl(int viewId, String url, int id) {
        ImageView view = getView(viewId);
        Glide.with(context).load(url)
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(id)
                .into(view);
        return this;
    }

    //设置imageDrawable从网上(圆形)
    public BaseViewHolder setCircleImageResourcesUrl(int viewId, String url, int id) {
        ImageView view = getView(viewId);
        Glide.with(context).load(url)
                .transform(new GlideCircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(id)
                .into(view);
        return this;
    }

    //设置imageDrawable从网上(圆形带边框)
    public BaseViewHolder setCircleImageResourcesUrl(int viewId, String url, int id, int borderWidth, int borderColor) {
        ImageView view = getView(viewId);
        Glide.with(context).load(url)
                .transform(new GlideCircleTransform(context, borderWidth, borderColor))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(id)
                .into(view);
        return this;
    }

    //设置imageDrawable从网上(圆角)
    public BaseViewHolder setRoundImageResourcesUrl(int viewId, String url, int id, int radius) {
        ImageView view = getView(viewId);
        Glide.with(context).load(url)
                .transform(new GlideRoundTransform(context, radius))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(id)
                .into(view);
        return this;
    }

    public BaseViewHolder setImageAlpha(int viewId, float alpha) {
        ImageView view = getView(viewId);
        view.setAlpha(alpha);
        return this;
    }

    //设置imageDrawable从文件路径
    public BaseViewHolder setImageResourcesFile(int viewId, String path, int id) {
        ImageView view = getView(viewId);
        Glide.with(context).load(new File(path))
                .placeholder(id)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(view);
        return this;
    }

    public BaseViewHolder setBackgroundColor(int viewId, int color) {
        View view = this.getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public BaseViewHolder setBackgroundRes(int viewId, @DrawableRes int backgroundRes) {
        View view = this.getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = this.getView(viewId);
        view.setTextColor(context.getResources().getColor(textColor));
        return this;
    }

    // 设置通用选中
    public BaseViewHolder setCommonSelected(int viewId, boolean selected) {
        View view = getView(viewId);
        view.setSelected(selected);
        return this;
    }

    // 设置图片选中
    public BaseViewHolder setSelected(int viewId, boolean selected) {
        ImageView view = getView(viewId);
        view.setSelected(selected);
        return this;
    }

    public BaseViewHolder setTextSelected(int viewId, boolean selected) {
        TextView view = getView(viewId);
        view.setSelected(selected);
        return this;
    }

    public BaseViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = this.getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }

    public BaseViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = this.getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public BaseViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= 11) {
            this.getView(viewId).setAlpha(value);
        } else {
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0L);
            alpha.setFillAfter(true);
            this.getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    public BaseViewHolder setImageLevel(int viewId, int level) {
        ImageView view = getView(viewId);
        view.setImageLevel(level);
        return this;
    }

    public BaseViewHolder setVisible(int viewId, boolean visible) {
        View view = this.getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BaseViewHolder setEnabled(int viewId, boolean enabled) {
        View view = getView(viewId);
        view.setEnabled(enabled);
        return this;
    }

    public BaseViewHolder linkify(int viewId) {
        TextView view = this.getView(viewId);
        Linkify.addLinks(view, 15);
        return this;
    }

    public BaseViewHolder setTypeface(int viewId, Typeface typeface) {
        TextView view = this.getView(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | 128);
        return this;
    }

    public BaseViewHolder setTypeface(Typeface typeface, int... viewIds) {
        int[] var3 = viewIds;
        int var4 = viewIds.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            int viewId = var3[var5];
            TextView view = this.getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | 128);
        }

        return this;
    }

    public BaseViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = this.getView(viewId);
        view.setProgress(progress);
        return this;
    }

    public BaseViewHolder setProgress(int viewId, int progress, int max) {
        ProgressBar view = this.getView(viewId);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }

    public BaseViewHolder setMax(int viewId, int max) {
        ProgressBar view = this.getView(viewId);
        view.setMax(max);
        return this;
    }

    public BaseViewHolder setRating(int viewId, float rating) {
        RatingBar view = this.getView(viewId);
        view.setRating(rating);
        return this;
    }

    public BaseViewHolder setRating(int viewId, float rating, int max) {
        RatingBar view = this.getView(viewId);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = this.getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public BaseViewHolder addOnClickListener(int viewId) {
        this.childClickViewIds.add(Integer.valueOf(viewId));
        return this;
    }

    public BaseViewHolder addOnLongClickListener(int viewId) {
        this.itemChildLongClickViewIds.add(Integer.valueOf(viewId));
        return this;
    }

    public BaseViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = this.getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    public BaseViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = this.getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnItemClickListener(int viewId, AdapterView.OnItemClickListener listener) {
        AdapterView view = this.getView(viewId);
        view.setOnItemClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnItemLongClickListener(int viewId, AdapterView.OnItemLongClickListener listener) {
        AdapterView view = this.getView(viewId);
        view.setOnItemLongClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnItemSelectedClickListener(int viewId, AdapterView.OnItemSelectedListener listener) {
        AdapterView view = this.getView(viewId);
        view.setOnItemSelectedListener(listener);
        return this;
    }

    public BaseViewHolder setOnCheckedChangeListener(int viewId, CompoundButton.OnCheckedChangeListener listener) {
        CompoundButton view = this.getView(viewId);
        view.setOnCheckedChangeListener(listener);
        return this;
    }

    public BaseViewHolder setTag(int viewId, Object tag) {
        View view = this.getView(viewId);
        view.setTag(tag);
        return this;
    }

    public BaseViewHolder setTag(int viewId, int key, Object tag) {
        View view = this.getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    public BaseViewHolder setChecked(int viewId, boolean checked) {
        View view = this.getView(viewId);
        if (view instanceof CompoundButton) {
            ((CompoundButton) view).setChecked(checked);
        } else if (view instanceof CheckedTextView) {
            ((CheckedTextView) view).setChecked(checked);
        }
        return this;
    }

    public BaseViewHolder setAdapter(int viewId, Adapter adapter) {
        AdapterView view = this.getView(viewId);
        view.setAdapter(adapter);
        return this;
    }

    public <T extends View> T getView(int viewId) {
        View view = this.views.get(viewId);
        if (view == null) {
            view = this.convertView.findViewById(viewId);
            this.views.put(viewId, view);
        }

        return (T) view;
    }

    public Object getAssociatedObject() {
        return this.associatedObject;
    }

    public void setAssociatedObject(Object associatedObject) {
        this.associatedObject = associatedObject;
    }
}
