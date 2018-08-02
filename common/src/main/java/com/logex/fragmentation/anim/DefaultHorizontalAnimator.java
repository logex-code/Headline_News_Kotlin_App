package com.logex.fragmentation.anim;

import android.os.Parcel;
import android.os.Parcelable;

import com.logex.common.R;


/**
 * Created by liguangxi on 16-12-18.
 * 默认水平动画
 */
public class DefaultHorizontalAnimator extends FragmentAnimator implements Parcelable {

    public DefaultHorizontalAnimator() {
        enter = R.anim.h_fragment_enter; // a->b b进入动画
        exit = R.anim.h_fragment_exit; // a->b a离开动画
        popEnter = R.anim.h_fragment_pop_enter; // b->a a进入动画
        popExit = R.anim.h_fragment_pop_exit; // b->a b离开动画
    }

    protected DefaultHorizontalAnimator(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DefaultHorizontalAnimator> CREATOR = new Creator<DefaultHorizontalAnimator>() {
        @Override
        public DefaultHorizontalAnimator createFromParcel(Parcel in) {
            return new DefaultHorizontalAnimator(in);
        }

        @Override
        public DefaultHorizontalAnimator[] newArray(int size) {
            return new DefaultHorizontalAnimator[size];
        }
    };
}
