package com.logex.fragmentation.anim;

import com.logex.common.R;


/**
 * Created by liguangxi on 16-12-18.
 * 默认水平动画
 */
public class DefaultHorizontalAnimator extends FragmentAnimator {

    public DefaultHorizontalAnimator() {
        enter = R.anim.h_fragment_enter;
        exit = R.anim.h_fragment_exit;
        popEnter = R.anim.h_fragment_pop_enter;
        popExit = R.anim.h_fragment_pop_exit;
    }
}
