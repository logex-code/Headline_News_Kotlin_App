package com.logex.videoplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.logex.utils.LogUtil;
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
    protected RelativeLayout rlSurfaceContainer;
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
     * 状态 正常未播放
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
    protected int currentState = -1;

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
    public boolean looping = false;
    public Map<String, String> mapHeadData = new HashMap<>();

    public int seekToInAdvance = -1;

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
        rlSurfaceContainer = (RelativeLayout) findViewById(R.id.rl_surface_container);
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
        rlSurfaceContainer.setOnClickListener(this);
        rlSurfaceContainer.setOnTouchListener(this);

        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
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
                onEvent(JCBuriedPoint.ON_QUIT_FULLSCREEN);
                exitWindowFullscreen();
            } else {
                // 开启全屏
                onEvent(JCBuriedPoint.ON_ENTER_FULLSCREEN);
                startWindowFullscreen();
            }
        } else if (id == R.id.rl_surface_container) {
            if (currentState == CURRENT_STATE_ERROR) {
                // 重新加载播放
                prepareVideo();
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
            // 准备播放
            prepareVideo();
            onEvent(currentState != CURRENT_STATE_ERROR ? JCBuriedPoint.ON_CLICK_START_ICON :
                    JCBuriedPoint.ON_CLICK_START_ERROR);
        } else if (currentState == CURRENT_STATE_PLAYING) {
            LogUtil.i("暂停视频播放....");
            onEvent(JCBuriedPoint.ON_CLICK_PAUSE);
            JCMediaManager.instance().mediaPlayer.pause();
            setUiWitStateAndScreen(CURRENT_STATE_PAUSE);
        } else if (currentState == CURRENT_STATE_PAUSE) {
            LogUtil.i("恢复视频播放....");
            onEvent(JCBuriedPoint.ON_CLICK_RESUME);
            JCMediaManager.instance().mediaPlayer.start();
            setUiWitStateAndScreen(CURRENT_STATE_PLAYING);
        } else if (currentState == CURRENT_STATE_COMPLETE) {
            onEvent(JCBuriedPoint.ON_CLICK_START_COMPLETE);
            prepareVideo();
        }
    }

    /**
     * 视频准备播放
     */
    protected void prepareVideo() {
        LogUtil.i("视频准备播放.............");
        JCVideoPlayerManager.setListener(this);
        // 添加渲染视频控件
        addTextureView();
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        if (mAudioManager != null) {
            mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }

        // 保持屏幕常亮
        Activity activity = (Activity) context;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        JCMediaManager.instance().prepare(url, mapHeadData, looping);
        setUiWitStateAndScreen(CURRENT_STATE_PREPARING);
    }

    /**
     * 进入全屏播放
     */
    private void startWindowFullscreen() {
        LogUtil.i("播放动作>>>>>>>>进入全屏...");
        ViewGroup vp = (ViewGroup) ((Activity) context).findViewById(Window.ID_ANDROID_CONTENT);
        View view = vp.findViewById(R.id.jc_fullscreen_play);
        if (view != null) {
            vp.removeView(view);
        }
        if (rlSurfaceContainer.getChildCount() > 0) {
            rlSurfaceContainer.removeAllViews();
        }
        try {
            // 新建一个JCVideoPlayer控件
            Constructor<JCVideoPlayer> constructor = (Constructor<JCVideoPlayer>) JCVideoPlayer.this.getClass().getConstructor(Context.class);
            JCVideoPlayer jcVideoPlayer = constructor.newInstance(context);
            jcVideoPlayer.setId(R.id.jc_fullscreen_play);
            // 设置大小位置
            int w = mScreenWidth;
            int h = mScreenHeight;
            LayoutParams lp = new LayoutParams(h, w);
            lp.setMargins((w - h) / 2, -(w - h) / 2, 0, 0);
            vp.addView(jcVideoPlayer, lp);
            // 初始化
            jcVideoPlayer.setUp(url, JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            jcVideoPlayer.setUiWitStateAndScreen(currentState);
            jcVideoPlayer.addTextureView();
            jcVideoPlayer.setRotation(90);
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
        LogUtil.i("播放动作>>>>>>>>退出全屏...");
        ViewGroup vp = (ViewGroup) ((Activity) context).findViewById(Window.ID_ANDROID_CONTENT);
        View view = vp.findViewById(R.id.jc_fullscreen_play);
        if (view != null) {
            vp.removeView(view);
        }

        CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int id = v.getId();
        if (id == R.id.rl_surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    LogUtil.i("onTouch 播放器 按下 [" + this.hashCode() + "] ");
                    mTouchingProgressBar = true;

                    mDownX = x;
                    mDownY = y;
                    mChangeVolume = false;
                    mChangePosition = false;
                    /////////////////////
                    break;
                case MotionEvent.ACTION_MOVE:
                    LogUtil.i("onTouch 播放器 移动 [" + this.hashCode() + "] ");
                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);

                    if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
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
     * addTextureView
     */
    private void addTextureView() {
        LogUtil.i("添加视频渲染控件.........");
        // 首先清空所有子控件
        if (rlSurfaceContainer.getChildCount() > 0) {
            rlSurfaceContainer.removeAllViews();
        }
        mTextureView = new JCTextureView(context);
        mTextureView.setSurfaceTextureListener(this);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlSurfaceContainer.addView(mTextureView, lp);
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
        LogUtil.i("onPrepared " + " [" + this.hashCode() + "] ");

        if (currentState != CURRENT_STATE_PREPARING) return;
        JCMediaManager.instance().mediaPlayer.start();
        if (seekToInAdvance != -1) {
            JCMediaManager.instance().mediaPlayer.seekTo(seekToInAdvance);
            seekToInAdvance = -1;
        }
        startProgressTimer();
        setUiWitStateAndScreen(CURRENT_STATE_PLAYING);
    }

    @Override
    public void onInfo(int what, int extra) {
        LogUtil.i("onInfo what - " + what + " extra - " + extra);
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            LogUtil.i("MEDIA_INFO_BUFFERING_START");
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            LogUtil.i("MEDIA_INFO_BUFFERING_END");
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
        LogUtil.i("onSeekComplete" + " [" + this.hashCode() + "] ");
    }

    @Override
    public void onVideoSizeChanged() {
        LogUtil.i("onVideoSizeChanged " + " [" + this.hashCode() + "] ");

        int mVideoWidth = JCMediaManager.instance().currentVideoWidth;
        int mVideoHeight = JCMediaManager.instance().currentVideoHeight;
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            mTextureView.requestLayout();
        }
    }

    @Override
    public void onCompletion() {
        LogUtil.i("onCompletion " + " [" + this.hashCode() + "] ");
        onEvent(JCBuriedPoint.ON_PLAY_COMPLETE);
        setUiWitStateAndScreen(CURRENT_STATE_COMPLETE);
        if (rlSurfaceContainer.getChildCount() > 0) {
            rlSurfaceContainer.removeAllViews();
        }

        JCMediaManager.instance().currentVideoWidth = 0;
        JCMediaManager.instance().currentVideoHeight = 0;

        // 移除系统音频通知 比如监听系统来电
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        // 清除屏幕常亮flag
        Activity activity = (Activity) context;
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 退出全屏
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            exitWindowFullscreen();
        }
    }

    @Override
    public boolean onBackPress() {
        LogUtil.i("goToOtherListener " + " [" + this.hashCode() + "] ");
        if (currentScreen == JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN) {
            onEvent(JCBuriedPoint.ON_QUIT_FULLSCREEN);
            exitWindowFullscreen();
            JCVideoPlayerManager.setListener(JCVideoPlayerManager.lastListener());
            JCVideoPlayerManager.setLastListener(null);
            return true;
        }
        return false;
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.i("onError " + what + " - " + extra + " [" + this.hashCode() + "] ");
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

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        LogUtil.i("播放进度改变>>>>>>" + progress);
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
        LogUtil.i("bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ");
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
                LogUtil.i("播放进度更新 " + progress + " [" + this.hashCode() + "] ");
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
                    releaseAllVideos();
                    LogUtil.i("AUDIO_FOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (JCMediaManager.instance().mediaPlayer.isPlaying()) {
                        JCMediaManager.instance().mediaPlayer.pause();
                    }
                    LogUtil.i("AUDIO_FOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
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
        LogUtil.i("releaseAllVideos.........");
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (currentState == CURRENT_STATE_PLAYING) {
            // 暂停播放
            onEvent(JCBuriedPoint.ON_CLICK_PAUSE);
            JCMediaManager.instance().mediaPlayer.pause();
            setUiWitStateAndScreen(CURRENT_STATE_PAUSE);
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
