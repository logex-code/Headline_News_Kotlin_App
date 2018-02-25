package com.logex.fragmentation;

/**
 * Created by liguangxi on 16-12-18.
 * ISupportFragment
 */
interface ISupportFragment extends ISupport {

    /**
     * replace目标Fragment, 主要用于Fragment之间的replace
     *
     * @param toFragment 目标Fragment
     * @param addToBack  是否添加到回退栈
     */
    void replaceFragment(BaseFragment toFragment, boolean addToBack);

    /**
     * @return 位于栈顶的子Fragment
     */
    BaseFragment getTopChildFragment();

    /**
     * @return 当前Fragment的前一个Fragment
     */
    BaseFragment getPreFragment();

    /**
     * @param fragmentClass 目标子Fragment的Class
     * @param <T>           继承自SupportFragment的Fragment
     * @return 目标子Fragment
     */
    <T extends BaseFragment> T findChildFragment(Class<T> fragmentClass);

    <T extends BaseFragment> T findChildFragment(String fragmentTag);

    /**
     * 子栈内 出栈
     */
    void popChild();

    /**
     * 子栈内 出栈到目标Fragment
     *
     * @param targetFragmentClass          目标Fragment的Class
     * @param includeTargetFragment   是否包含目标Fragment
     */
    void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment);

    void popToChild(String targetFragmentTag, boolean includeTargetFragment);

    /**
     * 子栈内 出栈到目标Fragment,并在出栈后立即进行Fragment事务(可以防止出栈后,直接进行Fragment事务的异常)
     *
     * @param targetFragmentClass               目标Fragment的Class
     * @param includeTargetFragment        是否包含目标Fragment
     * @param afterPopTransactionRunnable  出栈后紧接着的Fragment事务
     */
    void popToChild(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable);

    void popToChild(String targetFragmentTag, boolean includeTargetFragment, Runnable afterPopTransactionRunnable);
}
