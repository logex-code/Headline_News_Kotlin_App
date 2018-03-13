package com.logex.refresh.footer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logex.common.R;
import com.logex.refresh.IBottomView;
import com.logex.utils.AutoUtils;
import com.logex.widget.LoadingView;

/**
 * Created by liguangxi on 16-12-23.
 * 默认上拉加载更多布局
 */
public class DefaultBottomView extends RelativeLayout implements IBottomView {
    private LoadingView loadingView;
    private TextView refreshTextView;

    public DefaultBottomView(Context context) {
        this(context, null);
    }

    public DefaultBottomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_pull_refresh_footer, this);
        AutoUtils.auto(this);
        refreshTextView = (TextView) findViewById(R.id.tv_pull_refresh);
        loadingView = (LoadingView) findViewById(R.id.loading_view);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingUp(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction > -1f) refreshTextView.setText(R.string.pull_refresh_footer_hint_normal);
        if (fraction == -1f) refreshTextView.setText(R.string.pull_refresh_footer_hint_ready);
    }

    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        refreshTextView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction > -1f) {
            refreshTextView.setText(R.string.pull_refresh_footer_hint_normal);
        }
    }

    @Override
    public void onFinish() {
        refreshTextView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void reset() {
        refreshTextView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }
}
