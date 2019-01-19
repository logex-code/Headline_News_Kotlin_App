package com.logex.fragmentation.anim;

/**
 * Created by liguangxi on 16-12-18.
 * 默认没有动画
 */
public class DefaultNoAnimator extends FragmentAnimator {

    public DefaultNoAnimator() {
        enter = 0;
        exit = 0;
        popEnter = 0;
        popExit = 0;
    }
}
