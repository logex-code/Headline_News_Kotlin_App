package com.logex.fragmentation.helper;

import android.os.Bundle;

import com.logex.fragmentation.BaseFragment;


/**
 * Created by liguangxi on 16-12-18.
 * fragment18个生命周期方法
 */
public class FragmentLifecycleCallbacks {

    /**
     * Called when the Fragment is called onSaveInstanceState().
     */
    public void onFragmentSaveInstanceState(BaseFragment fragment, Bundle outState) {

    }

    /**
     * Called when the Fragment is called onEnterAnimationEnd().
     */
    public void onFragmentEnterAnimationEnd(BaseFragment fragment, Bundle savedInstanceState) {

    }

    /**
     * Called when the Fragment is called onLazyInitView().
     */
    public void onFragmentLazyInitView(BaseFragment fragment, Bundle savedInstanceState) {

    }

    /**
     * Called when the Fragment is called onSupportVisible().
     */
    public void onFragmentSupportVisible(BaseFragment fragment) {

    }

    /**
     * Called when the Fragment is called onSupportInvisible().
     */
    public void onFragmentSupportInvisible(BaseFragment fragment) {

    }

    /**
     * Called when the Fragment is called onAttach().
     */
    public void onFragmentAttached(BaseFragment fragment) {

    }

    /**
     * Called when the Fragment is called onCreate().
     */
    public void onFragmentCreated(BaseFragment fragment, Bundle savedInstanceState) {

    }

    // 因为我们一般会移除super.onCreateView()来复写 onCreateView()  所以这里一般是捕捉不到onFragmentCreateView
//    /**
//     * Called when the Fragment is called onCreateView().
//     */
//    public void onFragmentCreateView(SupportFragment fragment, Bundle savedInstanceState) {
//
//    }

    /**
     * Called when the Fragment is called onCreate().
     */
    public void onFragmentViewCreated(BaseFragment fragment, Bundle savedInstanceState) {

    }

    /**
     * Called when the Fragment is called onActivityCreated().
     */
    public void onFragmentActivityCreated(BaseFragment fragment, Bundle savedInstanceState) {

    }

    /**
     * Called when the Fragment is called onStart().
     */
    public void onFragmentStarted(BaseFragment fragment) {

    }

    /**
     * Called when the Fragment is called onResume().
     */
    public void onFragmentResumed(BaseFragment fragment) {

    }

    /**
     * Called when the Fragment is called onPause().
     */
    public void onFragmentPaused(BaseFragment fragment) {

    }

    /**
     * Called when the Fragment is called onStop().
     */
    public void onFragmentStopped(BaseFragment fragment) {

    }

    /**
     * Called when the Fragment is called onDestroyView().
     */
    public void onFragmentDestroyView(BaseFragment fragment) {

    }

    /**
     * Called when the Fragment is called onDestroy().
     */
    public void onFragmentDestroyed(BaseFragment fragment) {

    }

    /**
     * Called when the Fragment is called onDetach().
     */
    public void onFragmentDetached(BaseFragment fragment) {

    }
}
