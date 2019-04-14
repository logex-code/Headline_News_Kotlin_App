package com.logex.videoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.logex.utils.AutoUtils;
import com.logex.utils.LogUtil;
import com.logex.utils.ScreenUtils;
import com.logex.utils.UIUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nathen on 16/7/30.
 * 视频播放基础类
 */
public abstract class JCVideoPlayer extends FrameLayout implements JCMediaPlayerListener,
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener,
        TextureView.SurfaceTextureListener {
    protected Context context;

    protected ImageView ivPlayStart;
    protected SeekBar sbVideoProgress;
    protected ImageView ivPlayFullscreen;
    protected TextView tvCurrentTime, tvTotalTime;
    protected FrameLayout flSurfaceContainer;
    protected LinearLayout llBottomContainer;
    protected ProgressBar pbPlayLoading;

    /**
     * 列表风格
     */
    public static final int SCREEN_WINDOW_LIST = 0;
    /**
     * 正常风格
     */
    public static final int SCREEN_WINDOW_NORMAL = 1;
    /**
     * 全屏风格
     */
    public static final int SCREEN_WINDOW_FULLSCREEN = 2;
    /**
     * 当前屏幕显示风格
     */
    protected int currentScreen = -1;

    /**
     * 状态 闲置
     */
    protected static final int CURRENT_STATE_NORMAL = 1;
    /**
     * 状态 准备播放
     */
    protected static final int CURRENT_STATE_PREPARING = 2;
    /**
     * 状态 播放中
     */
    protected static final int CURRENT_STATE_PLAYING = 3;
    /**
     * 状态 播放暂停
     */
    protected static final int CURRENT_STATE_PAUSE = 4;
    /**
     * 状态 播放完成
     */
    protected static final int CURRENT_STATE_COMPLETE = 5;
    /**
     * 状态 播放错误
     */
    protected static final int CURRENT_STATE_ERROR = 6;
    /**
     * 当前播放状态
     */
    public int currentState = -1;

    /**
     * wifi提示框是否在显示
     */
    protected boolean WIFI_TIP_DIALOG_SHOWED = false;

    private TextureView mTextureView; // 渲染控件
    public Surface surface;

    public static final int THRESHOLD = 80;
    public static final int FULL_SCREEN_NORMAL_DELAY = 500;
    /**
     * 退出全屏时记录当前播放时间
     */
    public static long CLICK_QUIT_FULLSCREEN_TIME = 0;

    public String url = null; // 视频url
    public Object[] objects = null;
    public boolean looping = false; // 是否循环播放
    public Map<String, String> mapHeadData = new HashMap<>();

    protected JCBuriedPoint JC_BURIED_POINT;

    protected Timer UPDATE_PROGRESS_TIMER;
    protected ProgressTimerTask mProgressTimerTask;

    protected int mScreenWidth, mScreenHeight;

    protected AudioManager mAudioManager;

    protected boolean mTouchingProgressBar;
    protected float mDownX;
    protected float mDownY;
    /**
     * 音量是否修改
     */
    protected boolean mChangeVolume;
    /**
     * 进度是否修改
     */
    protected boolean mChangePosition;

    protected int mDownPosition;
    protected int mGestureDownVolume;
    protected int mSeekTimePosition;

    public JCVideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public JCVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void init(Context context) {
        this.context = context;
        View.inflate(context, getLayoutId(), this);
        flSurfaceContainer = (FrameLayout) findViewById(R.id.fl_surface_container);
        llBottomContainer = (LinearLayout) findViewById(R.id.ll_bottom_container);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        sbVideoProgress = (SeekBar) findViewById(R.id.sb_play_progress);
        tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
        ivPlayFullscreen = (ImageView) findViewById(R.id.iv_play_fullscreen);
        pbPlayLoading = (ProgressBar) findViewById(R.id.pb_play_loading);
        ivPlayStart = (ImageView) findViewById(R.id.iv_play_start);

        // 添加事件
        ivPlayStart.setOnClickListener(this);
        ivPlayFullscreen.setOnClickListener(this);
        sbVideoProgress.setOnSeekBarChangeListener(this);
        llBottomContainer.setOnClickListener(this);
        flSurfaceContainer.setOnClickListener(this);
        flSurfaceContainer.setOnTouchListener(this);

        mScreenWidth = ScreenUtils.getScreenWidth(context);
        mScreenHeight = ScreenUtils.getScreenHeight(context);
        // 获取音频服务
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public boolean setUp(String url, int screen, Object... objects) {
        if ((System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) < FULL_SCREEN_NORMAL_DELAY) {
            return false;
        }
        this.url = url;
        this.currentScreen = screen;
        this.objects = objects;
        setUiWitStateAndScreen(CURRENT_STATE_NORMAL);
        return true;
    }

    public boolean setUp(String url, int screen, Map<String, String> mapHeadData, Object... objects) {
        if (setUp(url, screen, objects)) {
            this.mapHeadData.clear();
            this.mapHeadData.putAll(mapHeadData);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_play_start) {
            // 点击播放
            onClickStart();
        } else if (id == R.id.iv_play_fullscreen) {
            // 点击全屏
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                //退出全屏
                cancelProgressTimer();
                exitWindowFullscreen();

                // 设置事件监听
                JCVideoPlayerManager.setListener(JCVideoPlayerManager.lastListener());
                JCVideoPlayerManager.setLastListener(null);

                JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
                if (listener instanceof JCVideoPlayer) {
                    ((JCVideoPlayer) listener).addTextureView();
                }
            } else {
                // 开启全屏
                startWindowFullscreen();
            }
        } else if (id == R.id.fl_surface_container) {
            if (currentState == CURRENT_STATE_ERROR) {
                // 重新加载播放
                startVideo();
            }
        }
    }

    protected void onClickStart() {
        if (TextUtils.isEmpty(url)) {
            UIUtils.showToast(context, getResources().getString(R.string.no_url));
            return;
        }
        if (currentState == CURRENT_STATE_NORMAL || currentState == CURRENT_STATE_ERROR) {
            if (!url.startsWith("file") && !JCUtils.isWifiConnected(context) && !WIFI_TIP_DIALOG_SHOWED) {
                // 不是播放本地文件 wifi未连接弹框提醒用户
                showWifiDialog();
                return;
            }
            // 开始播放
            startVideo();
        } else if (currentState == CURRENT_STATE_PLAYING) {
            LogUtil.i("暂停视频播放" + " [" + this.hashCode() + "] ");
            onEvent(JCBuriedPoint.ON_CLICK_PAUSE);
            pauseVideo();
        } else if (currentState == CURRENT_STATE_PAUSE) {
            LogUtil.i("恢复视频播放" + " [" + this.hashCode() + "] ");
            onEvent(JCBuriedPoint.ON_CLICK_RESUME);
            resumeVideo();
        } else if (currentState == CURRENT_STATE_COMPLETE) {
            LogUtil.i("重新视频播放" + " [" + this.hashCode() + "] ");
            onEvent(JCBuriedPoint.ON_CLICK_START_COMPLETE);
            startVideo();
        }
    }

    /**
     * 视频开始播放
     */
    public void startVideo() {
        JCVideoPlayerManager.onVideoPauseAll();
        LogUtil.i("打开视频" + " [" + this.hashCode() + "] ");
        onEvent(currentState != CURRENT_STATE_ERROR ? JCBuriedPoint.ON_CLICK_START_ICON :
                JCBuriedPoint.ON_CLICK_START_ERROR);
        JCVideoPlayerManager.setListener(this);
        // 添加渲染视频控件
        addTextureView();

        // 注册系统来电音频监听
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        JCMediaManager.instance().prepare(url, mapHeadData, looping);
        setUiWitStateAndScreen(CURRENT_STATE_PREPARING);

        // 保持屏幕常亮
        Activity activity = (Activity) context;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 暂停视频播放
     */
    protected void pauseVideo() {
        JCMediaManager.instance().mediaPlayer.pause();
        setUiWitStateAndScreen(CURRENT_STATE_PAUSE);
    }

    /**
     * 恢复视频播放
     */
    protected void resumeVideo() {
        JCMediaManager.instance().mediaPlayer.start();
        setUiWitStateAndScreen(CURRENT_STATE_PLAYING);
    }

    public void onResumeVideo() {
        if (currentState == CURRENT_STATE_PAUSE) {
            resumeVideo();
        }
    }

    public void onPauseVideo() {
        if (currentState == CURRENT_STATE_PLAYING) {
            pauseVideo();
        }
    }

    /**
     * 进入全屏播放
     */
    private void startWindowFullscreen() {
        LogUtil.i("进入全屏" + " [" + this.hashCode() + "] ");
        onEvent(JCBuriedPoint.ON_ENTER_FULLSCREEN);
        ViewGroup vp = (ViewGroup) ((Activity) context).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.jc_fullscreen_play);
        if (old != null) {
            vp.removeView(old);
        }
        try {
            // 新建一个JCVideoPlayer控件
            Constructor<JCVideoPlayer> constructor = (Constructor<JCVideoPlayer>) JCVideoPlayer.this.getClass().getConstructor(Context.class);
            JCVideoPlayer jcVideoPlayer = constructor.newInstance(context);
            jcVideoPlayer.setId(R.id.jc_fullscreen_play);

            // view适配
            AutoUtils.auto(jcVideoPlayer);

            // 设置大小位置
            int w = mScreenWidth;
            int h = mScreenHeight;
            LayoutParams lp = new LayoutParams(h, w);
            vp.addView(jcVideoPlayer, lp);

            // 全面屏处理
            if (ScreenUtils.hasNotchScreen(context)) {
                // 设置间距 适配全面屏
                ViewGroup.LayoutParams layoutParams = jcVideoPlayer.llBottomContainer.getLayoutParams();
                if (layoutParams instanceof FrameLayout.LayoutParams) {
                    ((LayoutParams) layoutParams).rightMargin = AutoUtils.getDisplayWidthValue(84);
                }
            }

            // 初始化
            jcVideoPlayer.setUp(url, SCREEN_WINDOW_FULLSCREEN, objects);
            jcVideoPlayer.setUiWitStateAndScreen(currentState);
            jcVideoPlayer.addTextureView();

            // 设置横屏
            Activity activity = (Activity) context;
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏

            // 设置播放状态监听
            JCVideoPlayerManager.setLastListener(this);
            JCVideoPlayerManager.setListener(jcVideoPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出全屏播放
     */
    private void exitWindowFullscreen() {
        LogUtil.i("退出全屏" + " [" + this.hashCode() + "] ");
        onEvent(JCBuriedPoint.ON_QUIT_FULLSCREEN);
        ViewGroup vp = (ViewGroup) ((Activity) context).findViewById(Window.ID_ANDROID_CONTENT);
        View view = vp.findViewById(R.id.jc_fullscreen_play);
        if (view != null) {
            vp.removeView(view);
        }

        // 切换回竖屏
        Activity activity = (Activity) context;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏

        CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int id = v.getId();
        if (id == R.id.fl_surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    LogUtil.i("onTouch 播放器 按下 [" + this.hashCode() + "] ");
                    mTouchingProgressBar = true;

                    mDownX = x;
                    mDownY = y;
                    mChangeVolume = false;
                    mChangePosition = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    LogUtil.i("onTouch 播放器 移动 [" + this.hashCode() + "] ");
                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);

                    if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                        // 全屏模式移动可拖动进度和修改音量
                        if (!mChangePosition && !mChangeVolume) {
                            if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                                cancelProgressTimer();
                                if (absDeltaX >= THRESHOLD) {
                                    mChangePosition = true;
                                    mDownPosition = getCurrentPositionWhenPlaying();
                                } else {
                                    mChangeVolume = true;
                                    mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                }
                            }
                        }
                    }

                    if (mChangePosition) {
                        int totalTimeDuration = getDuration();
                        mSeekTimePosition = (int) (mDownPosition + deltaX * totalTimeDuration / mScreenWidth);
                        if (mSeekTimePosition > totalTimeDuration)
                            mSeekTimePosition = totalTimeDuration;
                        String seekTime = JCUtils.stringForTime(mSeekTimePosition);
                        String totalTime = JCUtils.stringForTime(totalTimeDuration);

                        showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
                    }

                    if (mChangeVolume) {
                        deltaY = -deltaY;
                        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        int deltaV = (int) (max * deltaY * 3 / mScreenHeight);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
                        // dialog中显示百分比
                        int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / mScreenHeight);
                        showVolumeDialog(-deltaY, volumePercent);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtil.i("onTouch 播放器 抬起 [" + this.hashCode() + "] ");
                    mTouchingProgressBar = false;
                    dismissProgressDialog();
                    dismissVolumeDialog();
                    if (mChangePosition) {
                        onEvent(JCBuriedPoint.ON_TOUCH_SCREEN_SEEK_POSITION);
                        JCMediaManager.instance().mediaPlayer.seekTo(mSeekTimePosition);
                        int duration = getDuration();
                        int progress = mSeekTimePosition * 100 / (duration == 0 ? 1 : duration);
                        sbVideoProgress.setProgress(progress);
                    }
                    if (mChangeVolume) {
                        onEvent(JCBuriedPoint.ON_TOUCH_SCREEN_SEEK_VOLUME);
                    }
                    startProgressTimer();
                    break;
            }
        }
        return false;
    }

    /**
     * 添加视频渲染控件
     */
    private void addTextureView() {
        LogUtil.i("添加视频渲染控件" + " [" + this.hashCode() + "] ");
        // 首先清空所有子控件
        if (flSurfaceContainer.getChildCount() > 0) {
            flSurfaceContainer.removeAllViews();
        }
        mTextureView = new JCTextureView(context);
        mTextureView.setSurfaceTextureListener(this);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        flSurfaceContainer.addView(mTextureView, lp);
    }

    public void setUiWitStateAndScreen(int state) {
        this.currentState = state;
        switch (currentState) {
            case CURRENT_STATE_NORMAL:
                if (isCurrentMediaListener()) {
                    cancelProgressTimer();
                    JCMediaManager.instance().releaseMediaPlayer();
                }
                break;
            case CURRENT_STATE_PREPARING:
                resetProgressAndTime();
                break;
            case CURRENT_STATE_PLAYING:
                startProgressTimer();
                break;
            case CURRENT_STATE_PAUSE:
                cancelProgressTimer();
                break;
            case CURRENT_STATE_ERROR:
                if (isCurrentMediaListener()) {
                    JCMediaManager.instance().releaseMediaPlayer();
                }
                break;
            case CURRENT_STATE_COMPLETE:
                cancelProgressTimer();
                break;
        }
    }

    /**
     * 开启播放进度
     */
    protected void startProgressTimer() {
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 300);
    }

    /**
     * 取消播放进度
     */
    protected void cancelProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
            UPDATE_PROGRESS_TIMER = null;
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
            mProgressTimerTask = null;
        }
    }

    @Override
    public void onPrepared() {
        LogUtil.i("播放准备中" + " [" + this.hashCode() + "] ");
        JCMediaManager.instance().mediaPlayer.start();

        // 判断上次是否已经播放一段进度
        long position = JCUtils.getSavedProgress(context, url);
        if (position > 0) {
            JCMediaManager.instance().mediaPlayer.seekTo(position);
        }

        // 切换到正在播放中ui
        setUiWitStateAndScreen(CURRENT_STATE_PLAYING);
    }

    @Override
    public void onInfo(int what, int extra) {
        LogUtil.i("视频信息" + what + " extra - " + extra + " [" + this.hashCode() + "] ");
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            LogUtil.i("开始缓冲" + " [" + this.hashCode() + "] ");
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            LogUtil.i("缓冲结束" + " [" + this.hashCode() + "] ");
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        if (currentState != CURRENT_STATE_NORMAL && currentState != CURRENT_STATE_PREPARING) {
            LogUtil.i("接收视频流进度 " + percent + " [" + this.hashCode() + "] ");
            setDownProgressShow(percent);
        }
    }

    @Override
    public void onSeekComplete() {
        LogUtil.i("跳转进度完成" + " [" + this.hashCode() + "] ");
    }

    @Override
    public void onVideoSizeChanged() {
        LogUtil.i("视频大小改变 " + " [" + this.hashCode() + "] ");

        int mVideoWidth = JCMediaManager.instance().currentVideoWidth;
        int mVideoHeight = JCMediaManager.instance().currentVideoHeight;
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            mTextureView.requestLayout();
        }
    }

    @Override
    public void onVideoPause() {
        LogUtil.i("暂停播放 " + " [" + this.hashCode() + "] ");
        cancelProgressTimer();
        if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
            // 保存视频进度
            long position = getCurrentPositionWhenPlaying();
            if (position > 0) {
                JCUtils.saveProgress(context, url, position);
            }
        }
        onEvent(JCBuriedPoint.ON_PLAY_PAUSE);
        setUiWitStateAndScreen(CURRENT_STATE_NORMAL);
        if (flSurfaceContainer.getChildCount() > 0) {
            flSurfaceContainer.removeAllViews();
        }

        JCMediaManager.instance().currentVideoWidth = 0;
        JCMediaManager.instance().currentVideoHeight = 0;

        // 移除系统音频通知 比如监听系统来电
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);

        // 清除屏幕常亮flag
        Activity activity = (Activity) context;
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onCompletion() {
        LogUtil.i("播放结束 " + " [" + this.hashCode() + "] ");
        onEvent(JCBuriedPoint.ON_PLAY_COMPLETE);
        setUiWitStateAndScreen(CURRENT_STATE_COMPLETE);
        if (flSurfaceContainer.getChildCount() > 0) {
            flSurfaceContainer.removeAllViews();
        }

        JCMediaManager.instance().currentVideoWidth = 0;
        JCMediaManager.instance().currentVideoHeight = 0;

        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            JCVideoPlayerManager.setListener(null);

            // 退出全屏
            exitWindowFullscreen();
        } else {
            JCVideoPlayerManager.setListener(null);
            JCVideoPlayerManager.setLastListener(null);

            // 移除系统音频通知 比如监听系统来电
            mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);

            // 清除屏幕常亮flag
            Activity activity = (Activity) context;
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public boolean onBackPress() {
        LogUtil.i("按下返回 " + " [" + this.hashCode() + "] ");
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            cancelProgressTimer();
            exitWindowFullscreen();
            JCVideoPlayerManager.setListener(JCVideoPlayerManager.lastListener());
            JCVideoPlayerManager.setLastListener(null);

            JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
            if (listener instanceof JCVideoPlayer) {
                ((JCVideoPlayer) listener).addTextureView();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.i("播放错误 " + what + " - " + extra + " [" + this.hashCode() + "] ");
        if (what != 38 && what != -38) {
            setUiWitStateAndScreen(CURRENT_STATE_ERROR);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        LogUtil.i("onSurfaceTextureAvailable [" + this.hashCode() + "] ");
        this.surface = new Surface(surfaceTexture);
        JCMediaManager.instance().setDisplay(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        LogUtil.i("onSurfaceTextureSizeChanged [" + this.hashCode() + "] ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        LogUtil.i("onSurfaceTextureDestroyed [" + this.hashCode() + "] ");
        surface.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //LogUtil.i("onSurfaceTextureUpdated [" + this.hashCode() + "] ");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        LogUtil.i("播放进度改变 " + progress + " [" + this.hashCode() + "] ");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        LogUtil.i("开始拖动播放进度条 [" + this.hashCode() + "] ");
        // 取消播放进度运行
        cancelProgressTimer();

        // 拦截上层事件交给进度条处理
        ViewParent viewParent = getParent();
        while (viewParent != null) {
            viewParent.requestDisallowInterceptTouchEvent(true);
            viewParent = viewParent.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogUtil.i("停止拖动进度条 [" + this.hashCode() + "] ");
        // 打点记录
        onEvent(JCBuriedPoint.ON_SEEK_POSITION);

        // 开始播放进度运行
        startProgressTimer();

        // 拦截上层事件交给进度条处理
        ViewParent viewParent = getParent();
        while (viewParent != null) {
            viewParent.requestDisallowInterceptTouchEvent(false);
            viewParent = viewParent.getParent();
        }

        if (currentState != CURRENT_STATE_PLAYING &&
                currentState != CURRENT_STATE_PAUSE) return;
        int time = seekBar.getProgress() * getDuration() / 100;
        JCMediaManager.instance().mediaPlayer.seekTo(time);
    }

    /**
     * 播放进度时间刷新定时器
     */
    private class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
                final int position = getCurrentPositionWhenPlaying();
                final int duration = getDuration();
                final int progress = position * 100 / (duration == 0 ? 1 : duration);
                ivPlayStart.post(new Runnable() {
                    @Override
                    public void run() {
                        setProgressAndTime(progress, position, duration);
                    }
                });
            }
        }
    }

    /**
     * 获取当前播放百分比进度
     *
     * @return 百分比进度
     */
    protected int getCurrentPositionWhenPlaying() {
        int position = 0;
        if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
            try {
                position = (int) JCMediaManager.instance().mediaPlayer.getCurrentPosition();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return position;
    }

    /**
     * 获取视频时长
     *
     * @return 时长
     */
    protected int getDuration() {
        int duration = 0;
        try {
            duration = (int) JCMediaManager.instance().mediaPlayer.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    /**
     * 设置下载缓冲视频进度显示
     *
     * @param progress 下载视频进度
     */
    protected void setDownProgressShow(int progress) {
        if (progress > 0) {
            // 显示下载缓冲进度
            sbVideoProgress.setSecondaryProgress(progress);
        }
    }

    /**
     * 设置底下进度和时间显示
     *
     * @param progress    当前播放进度
     * @param currentTime 当前时间
     * @param totalTime   总时间
     */
    protected void setProgressAndTime(int progress, int currentTime, int totalTime) {
        if (!mTouchingProgressBar && progress > 0) {
            // 显示播放进度
            sbVideoProgress.setProgress(progress);
        }
        tvCurrentTime.setText(JCUtils.stringForTime(currentTime));
        tvTotalTime.setText(JCUtils.stringForTime(totalTime));
    }

    /**
     * 重置播放进度和时间
     */
    protected void resetProgressAndTime() {
        sbVideoProgress.setProgress(0);
        sbVideoProgress.setSecondaryProgress(0);
        tvCurrentTime.setText(JCUtils.stringForTime(0));
        tvTotalTime.setText(JCUtils.stringForTime(0));
    }

    /**
     * 监听系统播放音频
     */
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    LogUtil.i("AUDIO_FOCUS_LOSS [" + this.hashCode() + "]");
                    releaseAllVideos();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    LogUtil.i("AUDIO_FOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    if (JCMediaManager.instance().mediaPlayer.isPlaying()) {
                        JCMediaManager.instance().mediaPlayer.pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };

    /**
     * 是否是当前监听器
     *
     * @return true false
     */
    protected boolean isCurrentMediaListener() {
        return JCVideoPlayerManager.listener() != null
                && JCVideoPlayerManager.listener() == this;
    }

    /**
     * 释放所有播放
     */
    public void releaseAllVideos() {
        LogUtil.i("释放所有播放器 [" + this.hashCode() + "] ");
        JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
        if (listener != null) {
            listener.onCompletion();
        }
        JCMediaPlayerListener lastListener = JCVideoPlayerManager.lastListener();
        if (lastListener != null) {
            lastListener.onCompletion();
        }
        JCMediaManager.instance().releaseMediaPlayer();
    }

    /**
     * 设置打点监听
     *
     * @param jcBuriedPoint jcBuriedPoint
     */
    public void setJcBuriedPoint(JCBuriedPoint jcBuriedPoint) {
        JC_BURIED_POINT = jcBuriedPoint;
    }

    public void onEvent(int type) {
        if (JC_BURIED_POINT != null && isCurrentMediaListener()) {
            JC_BURIED_POINT.onEvent(type, url, currentScreen, objects);
        }
    }

    /**
     * 显示未连接WIFI弹框
     */
    protected void showWifiDialog() {

    }

    /**
     * 显示播放进度拖动框
     *
     * @param deltaX            deltaX
     * @param seekTime          seekTime
     * @param seekTimePosition  seekTimePosition
     * @param totalTime         totalTime
     * @param totalTimeDuration totalTimeDuration
     */
    protected void showProgressDialog(float deltaX, String seekTime, int seekTimePosition,
                                      String totalTime, int totalTimeDuration) {
    }

    /**
     * 隐藏播放进度拖动框
     */
    protected void dismissProgressDialog() {

    }

    /**
     * 显示播放音量调整框
     *
     * @param deltaY        deltaY
     * @param volumePercent volumePercent
     */
    protected void showVolumeDialog(float deltaY, int volumePercent) {

    }

    /**
     * 隐藏播放音量调整框
     */
    protected void dismissVolumeDialog() {

    }

    /**
     * 获取播放器布局id
     *
     * @return 布局id
     */
    public abstract int getLayoutId();

}
