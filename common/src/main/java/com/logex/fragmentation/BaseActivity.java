package com.logex.fragmentation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.logex.common.R;
import com.logex.fragmentation.anim.DefaultVerticalAnimator;
import com.logex.fragmentation.anim.FragmentAnimator;
import com.logex.fragmentation.helper.FragmentLifecycleCallbacks;
import com.logex.fragmentation.helper.internal.LifecycleHelper;
import com.logex.utils.AppInfoUtil;
import com.logex.utils.AutoUtils;
import com.logex.utils.StatusBarUtil;
import com.logex.utils.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;


/**
 * Created by liguangxi on 16-12-18.
 * 所有activity基类
 */
public abstract class BaseActivity extends AppCompatActivity implements ISupport {
    private FragmentationDelegate mFragmentationDelegate;
    private LifecycleHelper mLifecycleHelper;
    private ArrayList<FragmentLifecycleCallbacks> mFragmentLifecycleCallbacks;
    private FragmentAnimator mFragmentAnimator;
    private int mDefaultFragmentBackground = 0;
    boolean mPopMultipleNoAnim = false;
    // 防抖动 是否可以点击
    private boolean mFragmentClickable = true;
    private Handler mHandler;
    protected InputMethodManager inputMethodManager;
    //private IosLoadingDialog sweetAlertDialog;
    public Context context;
    protected static final int REQUEST_CODE_CAMERA = 1;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected File cameraFile;
    protected boolean isUseDarkMode = false; // 是否开启了状态栏dark模式

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        AutoUtils.setSize(this, true, 1080, 1920);
        mFragmentationDelegate = getFragmentationDelegate();
        mFragmentAnimator = onCreateFragmentAnimator();
        setContentView(getLayoutId());
        context = this;
        initCreate(arg0);
    }

    /**
     * 隐藏软键盘
     */
    protected void hideSoftKeyboard() {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 设置加载进度框
     *
     * @param title title
     */
    public void setLoading(String title) {
        /*if (sweetAlertDialog == null) {
            sweetAlertDialog = new IosLoadingDialog(this).builder()
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(false);
        }
        sweetAlertDialog.setTitle(title).show();*/
    }

    /**
     * 隐藏加载进度框
     */
    public void dismissLoading() {
        /*if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }*/
    }

    /**
     * 从图库获取图片
     *
     * @param selectCount 选择数量
     * @param isSingle    是否单选
     */
    public void startAlbum(int selectCount, boolean isSingle) {
        /*Intent intent = new Intent(context, MultiImageSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        // 最大图片选择数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, selectCount);
        // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        if (isSingle) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        } else {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);*/
    }

    /**
     * 照相获取图片
     */
    public void startCamera() {
        if (!AppInfoUtil.isSdcardExist()) {
            UIUtils.showToast(context, getString(R.string.sd_card_does_not_exist));
            return;
        }
        File appDir = new File(Environment.getExternalStorageDirectory(), "YunJuBao/Temp");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        cameraFile = new File(appDir, UUID.randomUUID().toString() + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(cameraFile)), REQUEST_CODE_CAMERA);
    }

    /**
     * 得到布局
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化视图和数据
     *
     * @param savedInstanceState savedInstanceState
     */
    protected abstract void initCreate(Bundle savedInstanceState);

    public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks callback) {
        synchronized (this) {
            if (mFragmentLifecycleCallbacks == null) {
                mFragmentLifecycleCallbacks = new ArrayList<>();
                mLifecycleHelper = new LifecycleHelper(mFragmentLifecycleCallbacks);
            }
            mFragmentLifecycleCallbacks.add(callback);
        }
    }

    public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks callback) {
        synchronized (this) {
            if (mFragmentLifecycleCallbacks != null) {
                mFragmentLifecycleCallbacks.remove(callback);
            }
        }
    }

    FragmentationDelegate getFragmentationDelegate() {
        if (mFragmentationDelegate == null) {
            mFragmentationDelegate = new FragmentationDelegate(this);
        }
        return mFragmentationDelegate;
    }

    Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
        return mHandler;
    }

    /**
     * 获取设置的全局动画, copy
     *
     * @return FragmentAnimator
     */
    public FragmentAnimator getFragmentAnimator() {
        return new FragmentAnimator(
                mFragmentAnimator.getEnter(), mFragmentAnimator.getExit(),
                mFragmentAnimator.getPopEnter(), mFragmentAnimator.getPopExit()
        );
    }

    /**
     * 设置全局动画, 一般情况建议复写onCreateFragmentAnimator()设置
     */
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        this.mFragmentAnimator = fragmentAnimator;
    }

    /**
     * 构建Fragment转场动画
     * <p/>
     * 如果是在Activity内实现,则构建的是Activity内所有Fragment的转场动画,
     * 如果是在Fragment内实现,则构建的是该Fragment的转场动画,此时优先级 > Activity的onCreateFragmentAnimator()
     *
     * @return FragmentAnimator对象
     */
    protected FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultVerticalAnimator();
    }

    /**
     * 当Fragment根布局 没有 设定background属性时,
     * Fragmentation默认使用Theme的android:windowbackground作为Fragment的背景,
     * 可以通过该方法改变Fragment背景。
     */
    protected void setDefaultFragmentBackground(@DrawableRes int backgroundRes) {
        mDefaultFragmentBackground = backgroundRes;
    }

    /**
     * (因为事务异步的原因) 如果你想在onCreate()中使用start/pop等 Fragment事务方法, 请使用该方法把你的任务入队
     *
     * @param runnable 需要执行的任务
     */
    protected void enqueueAction(Runnable runnable) {
        getHandler().post(runnable);
    }

    /**
     * 不建议复写该方法,请使用 {@link #onBackPressedSupport} 代替
     */
    @Override
    final public void onBackPressed() {
        // 这里是防止动画过程中，按返回键取消加载Fragment
        if (!mFragmentClickable) {
            setFragmentClickable(true);
        }

        // 获取activeFragment:即从栈顶开始 状态为show的那个Fragment
        BaseFragment activeFragment = mFragmentationDelegate.getActiveFragment(null, getSupportFragmentManager());
        if (mFragmentationDelegate.dispatchBackPressedEvent(activeFragment)) return;

        onBackPressedSupport();
    }

    /**
     * 该方法回调时机为,Activity回退栈内Fragment的数量 小于等于1 时,默认finish Activity
     * 请尽量复写该方法,避免复写onBackPress(),以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            finish();
        }
    }

    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     *
     * @param containerId 容器id
     * @param toFragment  目标Fragment
     */
    @Override
    public void loadRootFragment(int containerId, BaseFragment toFragment) {
        mFragmentationDelegate.loadRootTransaction(getSupportFragmentManager(), containerId, toFragment);
    }

    /**
     * 以replace方式加载根Fragment
     */
    @Override
    public void replaceLoadRootFragment(int containerId, BaseFragment toFragment, boolean addToBack) {
        mFragmentationDelegate.replaceLoadRootTransaction(getSupportFragmentManager(), containerId, toFragment, addToBack);
    }

    /**
     * 加载多个根Fragment
     *
     * @param containerId 容器id
     * @param toFragments 目标Fragments
     */
    @Override
    public void loadMultipleRootFragment(int containerId, int showPosition, BaseFragment... toFragments) {
        mFragmentationDelegate.loadMultipleRootTransaction(getSupportFragmentManager(), containerId, showPosition, toFragments);
    }

    /**
     * show一个Fragment,hide其他同栈所有Fragment
     * 使用该方法时，要确保同级栈内无多余的Fragment,(只有通过loadMultipleRootFragment()载入的Fragment)
     * <p/>
     * 建议使用更明确的{@link #showHideFragment(BaseFragment, BaseFragment)}
     *
     * @param showFragment 需要show的Fragment
     */
    @Override
    public void showHideFragment(BaseFragment showFragment) {
        showHideFragment(showFragment, null);
    }

    /**
     * show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
     *
     * @param showFragment 需要show的Fragment
     * @param hideFragment 需要hide的Fragment
     */
    @Override
    public void showHideFragment(BaseFragment showFragment, BaseFragment hideFragment) {
        mFragmentationDelegate.showHideFragment(getSupportFragmentManager(), showFragment, hideFragment);
    }

    /**
     * 启动目标Fragment
     *
     * @param toFragment 目标Fragment
     */
    @Override
    public void start(BaseFragment toFragment) {
        start(toFragment, BaseFragment.STANDARD);
    }

    @Override
    public void start(BaseFragment toFragment, @BaseFragment.LaunchMode int launchMode) {
        mFragmentationDelegate.dispatchStartTransaction(getSupportFragmentManager(), getTopFragment(), toFragment, 0, launchMode, FragmentationDelegate.TYPE_ADD);
    }

    @Override
    public void startForResult(BaseFragment toFragment, int requestCode) {
        mFragmentationDelegate.dispatchStartTransaction(getSupportFragmentManager(), getTopFragment(), toFragment, requestCode, BaseFragment.STANDARD, FragmentationDelegate.TYPE_ADD_RESULT);
    }

    @Override
    public void startWithPop(BaseFragment toFragment) {
        mFragmentationDelegate.dispatchStartTransaction(getSupportFragmentManager(), getTopFragment(), toFragment, 0, BaseFragment.STANDARD, FragmentationDelegate.TYPE_ADD_WITH_POP);
    }

    /**
     * 得到位于栈顶Fragment
     */
    @Override
    public BaseFragment getTopFragment() {
        return mFragmentationDelegate.getTopFragment(getSupportFragmentManager());
    }

    /**
     * 获取栈内的fragment对象
     */
    @Override
    public <T extends BaseFragment> T findFragment(Class<T> fragmentClass) {
        return mFragmentationDelegate.findStackFragment(fragmentClass, null, getSupportFragmentManager());
    }

    @Override
    public <T extends BaseFragment> T findFragment(String fragmentTag) {
        FragmentationDelegate.checkNotNull(fragmentTag, "tag == null");
        return mFragmentationDelegate.findStackFragment(null, fragmentTag, getSupportFragmentManager());
    }

    /**
     * 出栈
     */
    @Override
    public void pop() {
        mFragmentationDelegate.back(getSupportFragmentManager());
    }

    /**
     * 出栈到目标fragment
     *
     * @param targetFragmentClass   目标fragment
     * @param includeTargetFragment 是否包含该fragment
     */
    @Override
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        popTo(targetFragmentClass.getName(), includeTargetFragment);
    }

    @Override
    public void popTo(String targetFragmentTag, boolean includeTargetFragment) {
        popTo(targetFragmentTag, includeTargetFragment, null);
    }

    /**
     * 用于出栈后,立刻进行FragmentTransaction操作
     */
    @Override
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        popTo(targetFragmentClass.getName(), includeTargetFragment, afterPopTransactionRunnable);
    }

    @Override
    public void popTo(String targetFragmentTag, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        mFragmentationDelegate.popTo(targetFragmentTag, includeTargetFragment, afterPopTransactionRunnable, getSupportFragmentManager());
    }

    void preparePopMultiple() {
        mPopMultipleNoAnim = true;
    }

    void popFinish() {
        mPopMultipleNoAnim = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFragmentLifecycleCallbacks != null) {
            mFragmentLifecycleCallbacks.clear();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // 这里是防止动画过程中，按返回键取消加载Fragment
            if (!mFragmentClickable) {
                setFragmentClickable(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 防抖动(防止点击速度过快)
        if (!mFragmentClickable) return true;

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 防抖动(防止点击速度过快)
     */
    void setFragmentClickable(boolean clickable) {
        mFragmentClickable = clickable;
    }

    public int getDefaultFragmentBackground() {
        return mDefaultFragmentBackground;
    }

    void dispatchFragmentLifecycle(int lifecycle, BaseFragment fragment, Bundle bundle, boolean visible) {
        if (mLifecycleHelper == null) return;
        mLifecycleHelper.dispatchLifecycle(lifecycle, fragment, bundle, visible);
    }

    /**
     * 显示栈视图dialog,调试时使用
     */
    public void showFragmentStackHierarchyView() {
        mFragmentationDelegate.showFragmentStackHierarchyView();
    }

    /**
     * 显示栈视图日志,调试时使用
     */
    public void logFragmentStackHierarchy(String TAG) {
        mFragmentationDelegate.logFragmentRecords(TAG);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 颜色
     */
    protected void setStatusBarColor(@ColorRes int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ViewGroup mRootView = (ViewGroup) ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
                if (!isUseDarkMode && color == R.color.title_bar_color) {
                    // fix假如系统不支持状态栏dark模式并且设置状态栏颜色为白色，则修改状态栏为默认颜色防止看不清状态栏图标
                    color = R.color.status_bar_color;
                }
                mRootView.addView(StatusBarUtil.createStatusView(context, color), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
