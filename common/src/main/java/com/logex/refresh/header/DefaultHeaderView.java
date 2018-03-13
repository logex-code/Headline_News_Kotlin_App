package com.logex.refresh.header;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logex.common.R;
import com.logex.refresh.IHeaderView;
import com.logex.refresh.OnAnimEndListener;
import com.logex.utils.AutoUtils;

/**
 * Created by liguanxgi on 2016/10/2.
 * 默认下拉刷新头
 */

public class DefaultHeaderView extends RelativeLayout implements IHeaderView {
    private ImageView refreshArrow; // 下拉刷新箭头
    private ImageView ivLoading; // 下拉刷新动画
    private TextView refreshTextView;

    public DefaultHeaderView(Context context) {
        this(context, null);
    }

    public DefaultHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_pull_refresh_header, this);
        AutoUtils.auto(this);

        refreshArrow = (ImageView) findViewById(R.id.iv_pull_refresh_arrow);
        refreshTextView = (TextView) findViewById(R.id.tv_pull_refresh);
        ivLoading = (ImageView) findViewById(R.id.iv_pull_refresh_loading);
    }

    public void setArrowResource(@DrawableRes int resId) {
        refreshArrow.setImageResource(resId);
    }

    public void setTextColor(@ColorInt int color) {
        refreshTextView.setTextColor(color);
    }

    public void setPullDownStr(String pullDownStr) {
        refreshTextView.setText(pullDownStr);
    }

    public void setReleaseRefreshStr(String releaseRefreshStr) {
        refreshTextView.setText(releaseRefreshStr);
    }

    public void setRefreshingStr(String refreshingStr) {
        refreshTextView.setText(refreshingStr);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) {
            refreshTextView.setText(R.string.pull_refresh_header_hint_normal);
            refreshArrow.setVisibility(VISIBLE);
            ivLoading.setVisibility(GONE);
        }
        if (fraction > 1f) {
            refreshTextView.setText(R.string.pull_refresh_header_hint_ready);
            refreshArrow.setVisibility(GONE);
            ivLoading.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) {
            refreshTextView.setText(R.string.pull_refresh_header_hint_normal);
            if (refreshArrow.getVisibility() == GONE) {
                refreshArrow.setVisibility(VISIBLE);
                ivLoading.setVisibility(GONE);
            }
        }
    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        refreshTextView.setText(R.string.pull_refresh_header_hint_loading);
        refreshArrow.setVisibility(GONE);
        ivLoading.setVisibility(VISIBLE);
    }

    @Override
    public void onFinish(OnAnimEndListener listener) {
        listener.onAnimEnd();
    }

    @Override
    public void reset() {
        refreshArrow.setVisibility(VISIBLE);
        ivLoading.setVisibility(GONE);
        refreshTextView.setText(R.string.pull_refresh_header_hint_normal);

        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) ivLoading.getDrawable();

        drawable.start();
    }
}
