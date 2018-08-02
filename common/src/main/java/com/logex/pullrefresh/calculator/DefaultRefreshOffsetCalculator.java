package com.logex.pullrefresh.calculator;

import com.logex.pullrefresh.PullRefreshLayout;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/1
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 默认效果 一直跟随targetView下拉效果
 */
public class DefaultRefreshOffsetCalculator implements PullRefreshLayout.RefreshOffsetCalculator {

    @Override
    public int calculateRefreshOffset(int refreshInitOffset, int refreshEndOffset, int refreshViewHeight, int targetCurrentOffset, int targetInitOffset, int targetRefreshOffset) {
        int distance = targetRefreshOffset / 2 + refreshViewHeight / 2;
        int max = targetCurrentOffset - refreshViewHeight;
        return Math.min(max, targetCurrentOffset - distance);
    }
}
