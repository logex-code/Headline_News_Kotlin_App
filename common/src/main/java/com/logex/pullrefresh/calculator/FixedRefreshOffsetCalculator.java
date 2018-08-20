package com.logex.pullrefresh.calculator;

import com.logex.pullrefresh.PullRefreshLayout;

/**
 * 创建人: liguangxi
 * 时间: 18-8-14
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 效果下拉头下拉到一定距离不动
 * 偏移范围限定在[refreshInitOffset, refreshEndOffset]
 */
public class FixedRefreshOffsetCalculator implements PullRefreshLayout.RefreshOffsetCalculator {

    @Override
    public int calculateRefreshOffset(int refreshInitOffset, int refreshEndOffset, int refreshViewHeight, int targetCurrentOffset, int targetInitOffset, int targetRefreshOffset) {
        int refreshOffset;
        if (targetCurrentOffset >= targetRefreshOffset) {
            refreshOffset = refreshEndOffset;
        } else if (targetCurrentOffset <= targetInitOffset) {
            refreshOffset = refreshInitOffset;
        } else {
            float percent = (targetCurrentOffset - targetInitOffset) * 1.0f / (targetRefreshOffset - targetInitOffset);
            refreshOffset = (int) (refreshInitOffset + percent * (refreshEndOffset - refreshInitOffset));
        }
        return refreshOffset;
    }
}
