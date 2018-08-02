package com.logex.headlinenews.model.event;

import com.logex.fragmentation.BaseFragment;

/**
 * Created by liguangxi on 17-2-1.
 * 打开兄弟fragment
 */
public class StartBrotherEvent {
    public BaseFragment targetFragment;
    public int launchMode = -1;
    public int requestCode = -1;

    public StartBrotherEvent(BaseFragment targetFragment) {
        this.targetFragment = targetFragment;
    }

    public StartBrotherEvent(BaseFragment targetFragment, int launchMode) {
        this.targetFragment = targetFragment;
        this.launchMode = launchMode;
    }

    public StartBrotherEvent(int requestCode, BaseFragment targetFragment) {
        this.requestCode = requestCode;
        this.targetFragment = targetFragment;
    }
}
