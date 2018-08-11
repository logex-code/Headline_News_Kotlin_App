package com.logex.pullrefresh.header;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.logex.common.R;
import com.logex.pullrefresh.PullRefreshLayout;
import com.logex.utils.AutoUtils;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/1
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 默认刷新头
 */
public class DefaultRefreshView extends LinearLayout implements PullRefreshLayout.IRefreshView {
    private TextView tvPullRefresh;

    public DefaultRefreshView(Context context) {
        this(context, null);
    }

    public DefaultRefreshView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_pull_refresh_header, this);
        AutoUtils.auto(this);

        tvPullRefresh = (TextView) findViewById(R.id.tv_pull_refresh);
    }

    @Override
    public void stop() {
        tvPullRefresh.setText(R.string.pull_refresh_header_hint_normal);
    }

    @Override
    public void doRefresh() {
        tvPullRefresh.setText(R.string.pull_refresh_header_hint_loading);
    }

    @Override
    public void onPull(int offset, int total, int overPull) {
        // LogUtil.i("onPull>>>>>>offset>>>>" + offset + "\ntotal>>>>" + total + "\noverPull>>>" + overPull);

        if (offset == total) {
            tvPullRefresh.setText(R.string.pull_refresh_header_hint_ready);
        } else {
            tvPullRefresh.setText(R.string.pull_refresh_header_hint_normal);
        }
    }
}
