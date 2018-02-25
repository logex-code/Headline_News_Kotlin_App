package com.logex.fragmentation.helper.internal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.logex.fragmentation.BaseFragment;

import java.util.List;

/**
 * Created by YoKey on 17/4/4.
 * VisibleDelegate
 */

public class VisibleDelegate {
    private static final String FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE = "fragmentation_invisible_when_leave";

    // SupportVisible相关
    private boolean mIsSupportVisible;
    private boolean mNeedDispatch = true;
    private boolean mInvisibleWhenLeave;
    private boolean mIsFirstVisible = true;
    private boolean mFixStatePagerAdapter;
    private Bundle mSaveInstanceState;

    private BaseFragment mBaseFragment;

    public VisibleDelegate(BaseFragment fragment) {
        this.mBaseFragment = fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSaveInstanceState = savedInstanceState;
            if (!mFixStatePagerAdapter) { // setUserVisibleHint() may be called before onCreate()
                mInvisibleWhenLeave = savedInstanceState.getBoolean(FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE);
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FRAGMENTATION_STATE_SAVE_IS_INVISIBLE_WHEN_LEAVE, mInvisibleWhenLeave);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (!mInvisibleWhenLeave && !mBaseFragment.isHidden() &&
                (mBaseFragment.getUserVisibleHint() || mFixStatePagerAdapter)) {
            if ((mBaseFragment.getParentFragment() != null && isFragmentVisible(mBaseFragment.getParentFragment()))
                    || mBaseFragment.getParentFragment() == null) {
                mNeedDispatch = false;
                dispatchSupportVisible(true);
            }
        }
    }

    public void onResume() {
        if (!mIsFirstVisible) {
            if (!mIsSupportVisible && !mInvisibleWhenLeave && isFragmentVisible(mBaseFragment)) {
                mNeedDispatch = false;
                dispatchSupportVisible(true);
            }
        }
    }

    public void onPause() {
        if (mIsSupportVisible && isFragmentVisible(mBaseFragment)) {
            mNeedDispatch = false;
            mInvisibleWhenLeave = false;
            dispatchSupportVisible(false);
        } else {
            mInvisibleWhenLeave = true;
        }
    }

    public void onHiddenChanged(boolean hidden) {
        if (mBaseFragment.isResumed()) {
            dispatchSupportVisible(!hidden);
        }
    }

    public void onDestroyView() {
        mIsFirstVisible = true;
        mFixStatePagerAdapter = false;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (mBaseFragment.isResumed()) {
            if (!mIsSupportVisible && isVisibleToUser) {
                dispatchSupportVisible(true);
            } else if (mIsSupportVisible && !isVisibleToUser) {
                dispatchSupportVisible(false);
            }
        } else if (isVisibleToUser) {
            mInvisibleWhenLeave = false;
            mFixStatePagerAdapter = true;
        }
    }

    private void dispatchSupportVisible(boolean visible) {
        mIsSupportVisible = visible;

        if (!mNeedDispatch) {
            mNeedDispatch = true;
        } else {
            FragmentManager fragmentManager = mBaseFragment.getChildFragmentManager();
            if (fragmentManager != null) {
                List<Fragment> childFragments = fragmentManager.getFragments();
                if (childFragments != null) {
                    for (Fragment child : childFragments) {
                        if (child instanceof BaseFragment && !child.isHidden() && child.getUserVisibleHint()) {
                            ((BaseFragment) child).getVisibleDelegate().dispatchSupportVisible(visible);
                        }
                    }
                }
            }
        }

        if (visible) {
            mBaseFragment.onSupportVisible();

            if (mIsFirstVisible) {
                mIsFirstVisible = false;
                mBaseFragment.onLazyInitView(mSaveInstanceState);
            }
        } else {
            mBaseFragment.onSupportInvisible();
        }
    }

    private boolean isFragmentVisible(Fragment fragment) {
        return !fragment.isHidden() && fragment.getUserVisibleHint();
    }

    public boolean isSupportVisible() {
        return mIsSupportVisible;
    }
}
