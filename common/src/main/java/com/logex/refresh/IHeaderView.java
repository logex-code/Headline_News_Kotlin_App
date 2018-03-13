package com.logex.refresh;

import android.view.View;

/**
 * Created by lcodecore on 2016/10/1.
 * 下拉刷新接口
 */

public interface IHeaderView {
    View getView();

    /**
     * 下拉准备刷新动作
     *
     * @param fraction      当前下拉高度与总高度的比
     * @param maxHeadHeight
     * @param headHeight
     */
    void onPullingDown(float fraction, float maxHeadHeight, float headHeight);

    /**
     * 下拉释放过程
     *
     * @param fraction
     * @param maxHeadHeight
     * @param headHeight
     */
    void onPullReleasing(float fraction, float maxHeadHeight, float headHeight);

    /**
     * 开始下拉刷新
     *
     * @param maxHeadHeight
     * @param headHeight
     */
    void startAnim(float maxHeadHeight, float headHeight);

    /**
     * 下拉刷新结束
     *
     * @param animEndListener
     */
    void onFinish(OnAnimEndListener animEndListener);

    /**
     * 用于在必要情况下复位View，清除动画
     */
    void reset();
}
