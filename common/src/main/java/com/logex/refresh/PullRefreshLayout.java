package com.logex.refresh;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.logex.common.R;
import com.logex.refresh.footer.DefaultBottomView;
import com.logex.refresh.header.DefaultHeaderView;
import com.logex.refresh.processor.AnimProcessor;
import com.logex.refresh.processor.IDecorator;
import com.logex.refresh.processor.OverScrollDecorator;
import com.logex.refresh.processor.RefreshProcessor;
import com.logex.utils.DensityUtil;

/**
 * Created by lcodecore on 16/3/2.
 * 自定义下拉刷新
 */
public class PullRefreshLayout extends RelativeLayout implements PullListener {

    //波浪的高度,最大扩展高度
    protected float mMaxHeadHeight;
    protected float mMaxBottomHeight;

    //头部的高度
    protected float mHeadHeight;

    //允许的越界回弹的高度
    protected float mOverScrollHeight;

    //子控件
    private View mChildView;

    //头部layout
    protected FrameLayout mHeadLayout;

    //额外头部layout
    private FrameLayout mExtraHeadLayout;

    private IHeaderView mHeadView;
    private IBottomView mBottomView;

    //底部高度
    private float mBottomHeight;

    //底部layout
    private FrameLayout mBottomLayout;


    //是否刷新视图可见
    protected boolean isRefreshVisible = false;

    //是否加载更多视图可见
    protected boolean isLoadingVisible = false;

    //是否需要加载更多,默认需要
    protected boolean enableLoadMore = true;
    //是否需要下拉刷新,默认需要
    protected boolean enableRefresh = true;

    //是否在越界回弹的时候显示下拉图标
    protected boolean isOverScrollTopShow = true;
    //是否在越界回弹的时候显示上拉图标
    protected boolean isOverScrollBottomShow = true;

    //是否隐藏刷新控件,开启越界回弹模式(开启之后刷新控件将隐藏)
    protected boolean isPureScrollModeOn = false;

    //是否自动加载更多
    protected boolean autoLoadMore = false;

    //是否开启悬浮刷新模式
    protected boolean floatRefresh = false;

    //是否允许进入越界回弹模式
    protected boolean enableOverScroll = true;

    private boolean isLoadNoMore = false; // 是否没有更多了

    private CoContext cp;
    private int mTouchSlop;

    public PullRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullRefreshLayout, defStyleAttr, 0);
        try {
            mMaxHeadHeight = a.getDimensionPixelSize(R.styleable.PullRefreshLayout_tr_max_head_height, (int) DensityUtil.dip2px(context, 120));
            mHeadHeight = a.getDimensionPixelSize(R.styleable.PullRefreshLayout_tr_head_height, (int) DensityUtil.dip2px(context, 80));
            mMaxBottomHeight = a.getDimensionPixelSize(R.styleable.PullRefreshLayout_tr_max_bottom_height, (int) DensityUtil.dip2px(context, 120));
            mBottomHeight = a.getDimensionPixelSize(R.styleable.PullRefreshLayout_tr_bottom_height, (int) DensityUtil.dip2px(context, 60));
            mOverScrollHeight = a.getDimensionPixelSize(R.styleable.PullRefreshLayout_tr_overscroll_height, (int) mHeadHeight);
            enableLoadMore = a.getBoolean(R.styleable.PullRefreshLayout_tr_enable_loadmore, true);
            isPureScrollModeOn = a.getBoolean(R.styleable.PullRefreshLayout_tr_pureScrollMode_on, false);
            isOverScrollTopShow = a.getBoolean(R.styleable.PullRefreshLayout_tr_overscroll_top_show, true);
            isOverScrollBottomShow = a.getBoolean(R.styleable.PullRefreshLayout_tr_overscroll_bottom_show, true);
            enableOverScroll = a.getBoolean(R.styleable.PullRefreshLayout_tr_enable_overscroll, true);
            autoLoadMore = a.getBoolean(R.styleable.PullRefreshLayout_tr_enable_autoLoadMore, true);
        } finally {
            a.recycle();
        }

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        cp = new CoContext();

        addHeader();
        addFooter();

        setPullListener(this);
    }

    private void addHeader() {
        mHeadLayout = new FrameLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        layoutParams.addRule(ALIGN_PARENT_TOP);

        mExtraHeadLayout = new FrameLayout(getContext());
        mExtraHeadLayout.setId(R.id.ex_header);
        LayoutParams layoutParams2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        this.addView(mExtraHeadLayout, layoutParams2);
        this.addView(mHeadLayout, layoutParams);

        if (mHeadView == null) setHeaderView(new DefaultHeaderView(getContext()));
    }

    private void addFooter() {
        mBottomLayout = new FrameLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        mBottomLayout.setLayoutParams(layoutParams);

        this.addView(mBottomLayout);

        if (mBottomView == null) setBottomView(new DefaultBottomView(getContext()));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获得子控件
        //onAttachedToWindow方法中mChildView始终是第0个child，把header、footer放到构造函数中，mChildView最后被inflate
        mChildView = getChildAt(3);

        cp.init();
        decorator = new OverScrollDecorator(cp, new RefreshProcessor(cp));
        initGestureDetector();
    }

    private IDecorator decorator;
    private GestureDetector gestureDetector;

    private void initGestureDetector() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent ev) {
                decorator.onFingerDown(ev);
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                decorator.onFingerScroll(e1, e2, distanceX, distanceY, vy);
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                decorator.onFingerFling(e1, e2, velocityX, velocityY);
                return false;
            }
        });
    }

    private VelocityTracker moveTracker;
    private int mPointerId;
    private float vy;

    private void obtainTracker(MotionEvent event) {
        if (null == moveTracker) {
            moveTracker = VelocityTracker.obtain();
        }
        moveTracker.addMovement(event);
    }

    private void releaseTracker() {
        if (null != moveTracker) {
            moveTracker.clear();
            moveTracker.recycle();
            moveTracker = null;
        }
    }

    /*************************************
     * 触摸事件处理
     *****************************************/
    int mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //1.监听fling动作 2.获取手指滚动速度（存在滚动但非fling的状态）
        //TODO 考虑是否可以去除GestureDetector只保留VelocityTracker
        obtainTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                moveTracker.computeCurrentVelocity(1000, mMaxVelocity);
                vy = moveTracker.getYVelocity(mPointerId);
                releaseTracker();
                break;
        }
        gestureDetector.onTouchEvent(event);

        return super.dispatchTouchEvent(event);
    }

    /**
     * 拦截事件
     *
     * @return return true时,ViewGroup的事件有效,执行onTouchEvent事件
     * return false时,事件向下传递,onTouchEvent无效
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = decorator.interceptTouchEvent(ev);
        return intercept || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean consume = decorator.dealTouchEvent(e);
        return consume || super.onTouchEvent(e);
    }

    /*************************************
     * 开放api区
     *****************************************/
    /**
     * 主动刷新
     */
    public void startRefresh() {
        cp.startRefresh();
    }

    /**
     * 主动加载更多
     */
    public void startLoadMore() {
        cp.startLoadMore();
    }

    /**
     * 刷新结束
     */
    public void finishRefreshing() {
        cp.finishRefreshing();
    }

    /**
     * 加载更多结束
     */
    public void finishLoadMore() {
        cp.finishLoadMore();
    }

    /**
     * 手动设置刷新View
     */
    public void setTargetView(View targetView) {
        if (targetView != null) mChildView = targetView;
    }

    /**
     * 手动设置RefreshLayout的装饰
     */
    public void setDecorator(IDecorator decorator1) {
        if (decorator1 != null) decorator = decorator1;
    }

    /**
     * 设置头部View
     */
    public void setHeaderView(final IHeaderView headerView) {
        if (headerView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mHeadLayout.removeAllViewsInLayout();
                    mHeadLayout.addView(headerView.getView());
                }
            });
            mHeadView = headerView;
        }
    }

    /**
     * 设置固定在顶部的header
     */
    public void addFixedExHeader(@NonNull final View view) {

        post(new Runnable() {
            @Override
            public void run() {
                if (mExtraHeadLayout != null) {
                    if (mExtraHeadLayout.getChildCount() == 0) {
                        mExtraHeadLayout.addView(view);
                        cp.onAddExHead();
                        cp.setExHeadFixed();
                    }
                }
            }
        });
    }

    /**
     * 移除固定在顶部的header
     *
     * @param view view
     */
    public void removeFixedExHeader(@NonNull final View view) {

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mExtraHeadLayout != null) {
                    if (mExtraHeadLayout.getChildCount() == 1) {
                        final int mHeight = view.getMeasuredHeight();
                        ValueAnimator animator = ValueAnimator.ofInt(mHeight, 0);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                view.getLayoutParams().height = (int) animation.getAnimatedValue();
                                //此方法必须调用,调用后会重新调用onMeasure和onLayout方法进行测量和定位
                                mExtraHeadLayout.requestLayout();
                            }
                        });
                        animator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mExtraHeadLayout.removeViewInLayout(view);
                                view.getLayoutParams().height = mHeight;
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        animator.setDuration(500);
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.start();
                    }
                }
            }
        }, 1000);
    }

    /**
     * 获取额外附加的头部
     */
    public View getExtraHeaderView() {
        return mExtraHeadLayout;
    }

    /**
     * 设置底部View
     */
    public void setBottomView(final IBottomView bottomView) {
        if (bottomView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mBottomLayout.removeAllViewsInLayout();
                    mBottomLayout.addView(bottomView.getView());
                }
            });
            this.mBottomView = bottomView;
        }
    }

    /**
     * 设置是否悬浮刷新头
     *
     * @param ifOpenFloatRefreshMode 是否悬浮刷新模式
     */
    public void setFloatRefresh(boolean ifOpenFloatRefreshMode) {
        floatRefresh = ifOpenFloatRefreshMode;
        post(new Runnable() {
            @Override
            public void run() {
                if (mHeadLayout != null) mHeadLayout.bringToFront();
            }
        });
    }

    /**
     * 设置wave的下拉高度
     */
    public void setMaxHeadHeight(float maxHeightDp) {
        this.mMaxHeadHeight = DensityUtil.dip2px(getContext(), maxHeightDp);
    }

    /**
     * 设置下拉头的高度
     */
    public void setHeaderHeight(float headHeightDp) {
        this.mHeadHeight = DensityUtil.dip2px(getContext(), headHeightDp);
    }

    public void setMaxBottomHeight(float maxBottomHeight) {
        mMaxBottomHeight = DensityUtil.dip2px(getContext(), maxBottomHeight);
    }

    /**
     * 设置底部高度
     */
    public void setBottomHeight(float bottomHeightDp) {
        this.mBottomHeight = DensityUtil.dip2px(getContext(), bottomHeightDp);
    }

    /**
     * 是否允许加载更多
     */
    public void setEnableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
        if (mBottomView != null) {
            mBottomView.getView().setVisibility(enableLoadMore ? VISIBLE : GONE);
        }
    }

    /**
     * 设置没有更多了
     */
    public void setLoadNoMore(boolean loadNoMore, String str) {
        this.isLoadNoMore = loadNoMore;
        if (mBottomView != null && loadNoMore) {
            mBottomView.onLoadNoMore(str);
        }
    }

    /**
     * 是否允许下拉刷新
     */
    public void setEnableRefresh(boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
    }

    /**
     * 是否允许越界时显示刷新控件
     */
    public void setOverScrollTopShow(boolean isOverScrollTopShow) {
        this.isOverScrollTopShow = isOverScrollTopShow;
    }

    public void setOverScrollBottomShow(boolean isOverScrollBottomShow) {
        this.isOverScrollBottomShow = isOverScrollBottomShow;
    }

    public void setOverScrollRefreshShow(boolean isOverScrollRefreshShow) {
        this.isOverScrollTopShow = isOverScrollRefreshShow;
        this.isOverScrollBottomShow = isOverScrollRefreshShow;
    }

    /**
     * 是否允许开启越界回弹模式
     */
    public void setEnableOverScroll(boolean enableOverScroll1) {
        this.enableOverScroll = enableOverScroll1;
    }

    /**
     * 是否开启纯净的越界回弹模式,开启时刷新和加载更多控件不显示
     */
    public void setPureScrollModeOn(boolean pureScrollModeOn) {
        isPureScrollModeOn = pureScrollModeOn;
        if (pureScrollModeOn) {
            isOverScrollTopShow = false;
            isOverScrollBottomShow = false;
            setMaxHeadHeight(mOverScrollHeight);
            setHeaderHeight(mOverScrollHeight);
            setMaxBottomHeight(mOverScrollHeight);
            setBottomHeight(mOverScrollHeight);
        }
    }

    /**
     * 设置越界高度
     */
    public void setOverScrollHeight(float overScrollHeightDp) {
        this.mOverScrollHeight = DensityUtil.dip2px(getContext(), overScrollHeightDp);
    }

    /**
     * 设置OverScroll时自动加载更多
     *
     * @param ifAutoLoadMore 为true表示底部越界时主动进入加载更多模式，否则直接回弹
     */
    public void setAutoLoadMore(boolean ifAutoLoadMore) {
        autoLoadMore = ifAutoLoadMore;
        setEnableLoadMore(true);
    }

    /**
     * 设置刷新控件监听器
     */
    private RefreshListenerAdapter refreshListener;

    public void setOnRefreshListener(RefreshListenerAdapter refreshListener) {
        if (refreshListener != null) {
            this.refreshListener = refreshListener;
        }
    }

    //设置拖动屏幕的监听器
    private PullListener pullListener;

    private void setPullListener(PullListener pullListener) {
        this.pullListener = pullListener;
    }

    @Override
    public void onPullingDown(PullRefreshLayout refreshLayout, float fraction) {
        mHeadView.onPullingDown(fraction, mMaxHeadHeight, mHeadHeight);
        if (!enableRefresh) return;
        if (refreshListener != null) refreshListener.onPullingDown(refreshLayout, fraction);
    }

    @Override
    public void onPullingUp(PullRefreshLayout refreshLayout, float fraction) {
        if (!enableLoadMore || isLoadNoMore) return;
        mBottomView.onPullingUp(fraction, mMaxHeadHeight, mHeadHeight);
        if (refreshListener != null) refreshListener.onPullingUp(refreshLayout, fraction);
    }

    @Override
    public void onPullDownReleasing(PullRefreshLayout refreshLayout, float fraction) {
        mHeadView.onPullReleasing(fraction, mMaxHeadHeight, mHeadHeight);
        if (!enableRefresh) return;
        if (refreshListener != null) refreshListener.onPullDownReleasing(refreshLayout, fraction);
    }

    @Override
    public void onPullUpReleasing(PullRefreshLayout refreshLayout, float fraction) {
        if (!enableLoadMore || isLoadNoMore) return;
        mBottomView.onPullReleasing(fraction, mMaxBottomHeight, mBottomHeight);
        if (refreshListener != null) refreshListener.onPullUpReleasing(refreshLayout, fraction);
    }

    @Override
    public void onRefresh(PullRefreshLayout refreshLayout) {
        mHeadView.startAnim(mMaxHeadHeight, mHeadHeight);
        if (refreshListener != null) refreshListener.onRefresh(refreshLayout);
    }

    @Override
    public void onLoadMore(PullRefreshLayout refreshLayout) {
        if (isLoadNoMore) {
            finishLoadMore();
        } else {
            mBottomView.startAnim(mMaxBottomHeight, mBottomHeight);
            if (refreshListener != null) refreshListener.onLoadMore(refreshLayout);
        }
    }

    @Override
    public void onFinishRefresh() {
        if (!isRefreshVisible) return;
        mHeadView.onFinish(new OnAnimEndListener() {
            @Override
            public void onAnimEnd() {
                cp.finishRefreshAfterAnim();
            }
        });
    }

    @Override
    public void onFinishLoadMore() {
        if (!isLoadingVisible) return;
        mBottomView.onFinish();
    }

    @Override
    public void onRefreshCanceled() {
        if (refreshListener != null) refreshListener.onRefreshCanceled();
    }

    @Override
    public void onLoadMoreCanceled() {
        if (refreshListener != null) refreshListener.onLoadMoreCanceled();
    }

    public class CoContext {
        private AnimProcessor animProcessor;

        private final static int PULLING_TOP_DOWN = 0;
        private final static int PULLING_BOTTOM_UP = 1;
        private int state = PULLING_TOP_DOWN;

        private static final int EX_MODE_NORMAL = 0;
        private static final int EX_MODE_FIXED = 1;
        private int exHeadMode = EX_MODE_NORMAL;


        public CoContext() {
            animProcessor = new AnimProcessor(this);
        }

        public void init() {
            if (isPureScrollModeOn) {
                setOverScrollTopShow(false);
                setOverScrollBottomShow(false);
                if (mHeadLayout != null) mHeadLayout.setVisibility(GONE);
                if (mBottomLayout != null) mBottomLayout.setVisibility(GONE);
            }
        }

        public AnimProcessor getAnimProcessor() {
            return animProcessor;
        }

        public float getMaxHeadHeight() {
            return mMaxHeadHeight;
        }

        public int getHeadHeight() {
            return (int) mHeadHeight;
        }

        public int getExtraHeadHeight() {
            return mExtraHeadLayout.getHeight();
        }

        public int getMaxBottomHeight() {
            return (int) mMaxBottomHeight;
        }

        public int getBottomHeight() {
            return (int) mBottomHeight;
        }

        public int getOsHeight() {
            return (int) mOverScrollHeight;
        }

        public View getTargetView() {
            return mChildView;
        }

        public View getHeader() {
            return mHeadLayout;
        }

        public View getFooter() {
            return mBottomLayout;
        }

        public int getTouchSlop() {
            return mTouchSlop;
        }

        public void resetHeaderView() {
            if (mHeadView != null) mHeadView.reset();
        }

        public void resetBottomView() {
            if (mBottomView != null) mBottomView.reset();
        }

        public View getExHead() {
            return mExtraHeadLayout;
        }

        public void setExHeadNormal() {
            exHeadMode = EX_MODE_NORMAL;
        }

        public void setExHeadFixed() {
            exHeadMode = EX_MODE_FIXED;
        }

        public boolean isExHeadNormal() {
            return exHeadMode == EX_MODE_NORMAL;
        }

        public boolean isExHeadFixed() {
            return exHeadMode == EX_MODE_FIXED;
        }

        /**
         * 在添加附加Header前锁住，阻止一些额外的位移动画
         */
        private boolean isExHeadLocked = true;

        public boolean isExHeadLocked() {
            return isExHeadLocked;
        }

        //添加了额外头部时触发
        public void onAddExHead() {
            isExHeadLocked = false;
            LayoutParams params = (LayoutParams) mChildView.getLayoutParams();
            params.addRule(BELOW, mExtraHeadLayout.getId());
            mChildView.setLayoutParams(params);
            requestLayout();
        }


        /**
         * 主动刷新、加载更多、结束
         */
        public void startRefresh() {
            post(new Runnable() {
                @Override
                public void run() {
                    setStatePTD();
                    if (!isPureScrollModeOn && mChildView != null) {
                        setRefreshing(true);
                        animProcessor.animHeadToRefresh();
                    }
                }
            });
        }

        public void startLoadMore() {
            post(new Runnable() {
                @Override
                public void run() {
                    setStatePBU();
                    if (!isPureScrollModeOn && mChildView != null) {
                        setLoadingMore(true);
                        animProcessor.animBottomToLoad();
                    }
                }
            });
        }

        public void finishRefreshing() {
            onFinishRefresh();
        }

        public void finishRefreshAfterAnim() {
            if (isRefreshVisible() && mChildView != null) {
                setRefreshing(false);
                animProcessor.animHeadBack();
            }
        }

        public void finishLoadMore() {
            onFinishLoadMore();
            if (isLoadingVisible() && mChildView != null) {
                setLoadingMore(false);
                animProcessor.animBottomBack();
            }
        }

        public boolean enableOverScroll() {
            return enableOverScroll;
        }

        public boolean allowPullDown() {
            return enableRefresh || enableOverScroll;
        }

        public boolean allowPullUp() {
            return enableLoadMore || enableOverScroll;
        }

        public boolean enableRefresh() {
            return enableRefresh;
        }

        public boolean enableLoadMore() {
            return enableLoadMore;
        }

        public boolean allowOverScroll() {
            return (!isRefreshVisible && !isLoadingVisible);
        }

        public boolean isRefreshVisible() {
            return isRefreshVisible;
        }

        public boolean isLoadingVisible() {
            return isLoadingVisible;
        }

        public void setRefreshing(boolean refreshing) {
            isRefreshVisible = refreshing;
        }

        public void setLoadingMore(boolean loadingMore) {
            isLoadingVisible = loadingMore;
        }

        public boolean isOpenFloatRefresh() {
            return floatRefresh;
        }

        public boolean autoLoadMore() {
            return autoLoadMore;
        }

        public boolean isPureScrollModeOn() {
            return isPureScrollModeOn;
        }

        public boolean isOverScrollTopShow() {
            return isOverScrollTopShow;
        }

        public boolean isOverScrollBottomShow() {
            return isOverScrollBottomShow;
        }

        public void onPullingDown(float offsetY) {
            pullListener.onPullingDown(PullRefreshLayout.this, offsetY / mHeadHeight);
        }

        public void onPullingUp(float offsetY) {
            pullListener.onPullingUp(PullRefreshLayout.this, offsetY / mBottomHeight);
        }

        public void onRefresh() {
            pullListener.onRefresh(PullRefreshLayout.this);
        }

        public void onLoadMore() {
            pullListener.onLoadMore(PullRefreshLayout.this);
        }

        public void onFinishRefresh() {
            pullListener.onFinishRefresh();
        }

        public void onFinishLoadMore() {
            pullListener.onFinishLoadMore();
        }

        public void onPullDownReleasing(float offsetY) {
            pullListener.onPullDownReleasing(PullRefreshLayout.this, offsetY / mHeadHeight);
        }

        public void onPullUpReleasing(float offsetY) {
            pullListener.onPullUpReleasing(PullRefreshLayout.this, offsetY / mBottomHeight);
        }

        public void onRefreshCanceled() {
            pullListener.onRefreshCanceled();
        }

        public void onLoadMoreCanceled() {
            pullListener.onLoadMoreCanceled();
        }

        public void setStatePTD() {
            state = PULLING_TOP_DOWN;
        }

        public void setStatePBU() {
            state = PULLING_BOTTOM_UP;
        }

        public boolean isStatePTD() {
            return PULLING_TOP_DOWN == state;
        }

        public boolean isStatePBU() {
            return PULLING_BOTTOM_UP == state;
        }
    }
}
