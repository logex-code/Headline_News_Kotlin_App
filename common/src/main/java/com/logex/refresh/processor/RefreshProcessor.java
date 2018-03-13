package com.logex.refresh.processor;

import android.view.MotionEvent;

import com.logex.refresh.PullRefreshLayout;
import com.logex.refresh.utils.ScrollingUtil;

/**
 * Created by lcodecore on 2017/3/1.
 *
 */

public class RefreshProcessor implements IDecorator {

    protected PullRefreshLayout.CoContext cp;

    public RefreshProcessor(PullRefreshLayout.CoContext processor) {
        if (processor == null) throw new NullPointerException("The coprocessor can not be null.");
        cp = processor;
    }

    private float mTouchX, mTouchY;

    @Override
    public boolean interceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = ev.getX();
                mTouchY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - mTouchX;
                float dy = ev.getY() - mTouchY;
                if (Math.abs(dx) <= Math.abs(dy)) {//滑动允许最大角度为45度
                    if (dy > 0 && ScrollingUtil.isViewToTop(cp.getTargetView(),cp.getTouchSlop()) && cp.allowPullDown()) {
                        cp.setStatePTD();
                        return true;
                    } else if (dy < 0 && ScrollingUtil.isViewToBottom(cp.getTargetView(),cp.getTouchSlop()) && cp.allowPullUp()) {
                        cp.setStatePBU();
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public boolean dealTouchEvent(MotionEvent e) {
        if (cp.isRefreshVisible() || cp.isLoadingVisible()) return false;

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = e.getY() - mTouchY;
                if (cp.isStatePTD()) {
                    dy = Math.min(cp.getMaxHeadHeight() * 2, dy);
                    dy = Math.max(0, dy);
                    cp.getAnimProcessor().scrollHeadByMove(dy);
                } else if (cp.isStatePBU()) {
                    //加载更多的动作
                    dy = Math.min(cp.getMaxBottomHeight() * 2, Math.abs(dy));
                    dy = Math.max(0, dy);
                    cp.getAnimProcessor().scrollBottomByMove(dy);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (cp.isStatePTD()) {
                    cp.getAnimProcessor().dealPullDownRelease();
                } else if (cp.isStatePBU()) {
                    cp.getAnimProcessor().dealPullUpRelease();
                }
                return true;
        }
        return false;
    }

    @Override
    public void onFingerDown(MotionEvent ev) {
    }

    @Override
    public void onFingerScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, float velocityY) {
        //手指在屏幕上滚动，如果此时正处在刷新状态，可隐藏
        int mTouchSlop = cp.getTouchSlop();
        if (cp.isRefreshVisible() && distanceY >= mTouchSlop && !cp.isOpenFloatRefresh()) {
            cp.setRefreshing(false);
            cp.getAnimProcessor().animHeadHideByVy((int) velocityY);
        }
        if (cp.isLoadingVisible() && distanceY <= -mTouchSlop) {
            cp.setLoadingMore(false);
            cp.getAnimProcessor().animBottomHideByVy((int) velocityY);
        }
    }

    @Override
    public void onFingerFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    }
}
