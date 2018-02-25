package com.logex.fragmentation.helper.internal;

/**
 * Hide
 * 在5.0之前的设备, popTo(Class<?> fragmentClass, boolean includeSelf, Runnable afterPopTransactionRunnable)
 * 在出栈多个Fragment并随后立即执行start操作时,会出现一瞬间的闪屏.
 * 该Listener是为了解决该问题.
 * Created by liguangxi on 16-12-18.
 */
public interface OnFragmentDestroyViewListener {
    void onDestroyView();
}
