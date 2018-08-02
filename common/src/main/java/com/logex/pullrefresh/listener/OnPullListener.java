package com.logex.pullrefresh.listener;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/1
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * OnPullListener
 */
public interface OnPullListener {

    void onMoveTarget(int offset);

    void onMoveRefreshView(int offset);

    void onRefresh();
}
