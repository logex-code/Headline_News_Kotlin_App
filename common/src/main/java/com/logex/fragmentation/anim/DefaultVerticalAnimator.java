package com.logex.fragmentation.anim;

import com.logex.common.R;

/**
 * Created by liguangxi on 16-12-18.
 * 默认垂直动画（5.0系统动画）
 */
public class DefaultVerticalAnimator extends FragmentAnimator {

    public DefaultVerticalAnimator() {
        enter = R.anim.v_fragment_enter;
        exit = R.anim.v_fragment_exit;
        popEnter = R.anim.v_fragment_pop_enter;
        popExit = R.anim.v_fragment_pop_exit;
    }
}
