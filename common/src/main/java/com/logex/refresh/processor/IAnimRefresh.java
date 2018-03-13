package com.logex.refresh.processor;

/**
 * Created by lcodecore on 2017/3/3.
 */

public interface IAnimRefresh {
    void scrollHeadByMove(float moveY);
    void scrollBottomByMove(float moveY);
    void animHeadToRefresh();
    void animHeadBack();
    void animHeadHideByVy(int vy);
    void animBottomToLoad();
    void animBottomBack();
    void animBottomHideByVy(int vy);
}
