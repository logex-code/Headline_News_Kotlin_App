package com.logex.pullrefresh.calculator;

import com.logex.pullrefresh.PullRefreshLayout;

/**
 * 创建人: liguangxi
 * 日期: 2018/8/1
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 效果下拉头处于下拉区域中间的效果
 * 当targetCurrentOffset < refreshViewHeight, refreshView跟随targetView，其距离为0
 * 当targetCurrentOffset >= targetRefreshOffset RefreshView垂直方向永远居中于在[0, targetCurrentOffset]
 */

public class CenterRefreshOffsetCalculator implements PullRefreshLayout.RefreshOffsetCalculator {

    @Override
    public int calculateRefreshOffset(int refreshInitOffset, int refreshEndOffset, int refreshViewHeight, int targetCurrentOffset, int targetInitOffset, int targetRefreshOffset) {
        if (targetCurrentOffset < refreshViewHeight) {
            return targetCurrentOffset - refreshViewHeight;
        }
        return (targetCurrentOffset - refreshViewHeight) / 2;
    }
}
