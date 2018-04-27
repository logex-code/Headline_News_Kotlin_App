package com.logex.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
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
public abstract class JCVideoPlayer extends FrameLayout implements JCMediaPlayerListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener, TextureView.SurfaceTextureListener {
    @IdRes
    public static final int FULLSCREEN_ID = 33797;
    @IdRes
    public static final int TINY_ID = 33798;
    public static final int THRESHOLD = 80;
    public static final int FULL_SCREEN_NORMAL_DELAY = 500;

    public static boolean WIFI_TIP_DIALOG_SHOWED = false;
    public static long CLICK_QUIT_FULLSCREEN_TIME = 0;

    public static final int SCREEN_LAYOUT_LIST = 0;
    public static final int SCREEN_WINDOW_FULLSCREEN = 1;
    public static final int SCREEN_WINDOW_TINY = 2;

    public static final int CURRENT_STATE_NORMAL = 0;
    public static final int CURRENT_STATE_PREPAREING = 1;
    public static final int CURRENT_STATE_PLAYING = 2;
    public static final int CURRENT_STATE_PLAYING_BUFFERING_START = 3;
    public static final int CURRENT_STATE_PAUSE = 5;
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    public static final int CURRENT_STATE_ERROR = 7;

    public int currentState = -1;
    public int currentScreen = -1;


    public String url = null;
    public Object[] objects = null;
    public boolean looping = false;
    public Map<String, String> mapHeadData = new HashMap<>();
    public int seekToInAdvance = -1;

    protected ImageView startButton;
    protected SeekBar sbVideoProgress;
    protected ImageView fullscreenButton;
    protected TextView tvCurrentTime, tvTotalTime;
    protected RelativeLayout rlSurfaceContainer;
    protected ViewGroup llBottomContainer, llTopContainer;
    protected ProgressBar loadingProgressBar;
    public Surface surface;

    protected JCBuriedPoint JC_BURIED_POINT;
    protected Timer UPDATE_PROGRESS_TIMER;

    protected int mScreenWidth;
    protected int mScreenHeight;
    protected AudioManager mAudioManager;
    protected Handler mHandler;
    protected ProgressTimerTask mProgressTimerTask;
    protected int mBackUpBufferState = -1;

    protected boolean mTouchingProgressBar;
    protected float mDownX;
    protected float mDownY;
    protected boolean mChangeVolume;
    protected boolean mChangePosition;
    protected int mDownPosition;
    protected int mGestureDownVolume;
    protected int mSeekTimePosition;
    protected Context context;

    public JCVideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public JCVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        this.context = context;
        View.inflate(context, getLayoutId(), this);
        rlSurfaceContainer = (RelativeLayout) findViewById(R.id.rl_surface_container);
        llBottomContainer = (ViewGroup) findViewById(R.id.ll_bottom_container);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        sbVideoProgress = (SeekBar) findViewById(R.id.sb_video_progress);
        tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
        fullscreenButton = (ImageView) findViewById(R.id.fullscreen);
        llTopContainer = (ViewGroup) findViewById(R.id.ll_top_container);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
        startButton = (ImageView) findViewById(R.id.start);

        startButton.setOnClickListener(this);
        fullscreenButton.setOnClickListener(this);
        sbVideoProgress.setOnSeekBarChangeListener(this);
        llBottomContainer.setOnClickListener(this);
        rlSurfaceContainer.setOnClickListener(this);

        rlSurfaceContainer.setOnTouchListener(this);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mHandler = new Handler();
    }

    public boolean setUp(String url, int screen, Object... objects) {
        if ((System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) < FULL_SCREEN_NORMAL_DELAY)
            return false;
        this.currentState = CURRENT_STATE_NORMAL;
        this.url = url;
        this.objects = objects;
        this.currentScreen = screen;
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
        int i = v.getId();
        if (i == R.id.start) {
            LogUtil.i("onClick start [" + this.hashCode() + "] ");
            if (TextUtils.isEmpty(url)) {
                UIUtils.showToast(context, getResources().getString(R.string.no_url));
                return;
            }
            if (currentState == CURRENT_STATE_NORMAL || currentState == CURRENT_STATE_ERROR) {
                if (!url.startsWith("file") && !JCUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    showWifiDialog();
                    return;
                }
                prepareVideo();
                onEvent(currentState != CURRENT_STATE_ERROR ? JCBuriedPoint.ON_CLICK_START_ICON : JCBuriedPoint.ON_CLICK_START_ERROR);
            } else if (currentState == CURRENT_STATE_PLAYING) {
                onEvent(JCBuriedPoint.ON_CLICK_PAUSE);
                LogUtil.i("pauseVideo [" + this.hashCode() + "] ");
                JCMediaManager.instance().mediaPlayer.pause();
                setUiWitStateAndScreen(CURRENT_STATE_PAUSE);
            } else if (currentState == CURRENT_STATE_PAUSE) {
                onEvent(JCBuriedPoint.ON_CLICK_RESUME);
                JCMediaManager.instance().mediaPlayer.start();
                setUiWitStateAndScreen(CURRENT_STATE_PLAYING);
            } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                onEvent(JCBuriedPoint.ON_CLICK_START_AUTO_COMPLETE);
                prepareVideo();
            }
        } else if (i == R.id.fullscreen) {
            LogUtil.i("onClick fullscreen [" + this.hashCode() + "] ");
            if (currentState == CURRENT_STATE_AUTO_COMPLETE) return;
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                //退出全屏
                backPress();
            } else {
                LogUtil.i("toFullscreenActivity [" + this.hashCode() + "] ");
                onEvent(JCBuriedPoint.ON_ENTER_FULLSCREEN);
                startWindowFullscreen();
            }
        } else if (i == R.id.rl_surface_container && currentState == CURRENT_STATE_ERROR) {
            LogUtil.i("onClick surfaceContainer State=Error [" + this.hashCode() + "] ");
            prepareVideo();
        }
    }

    protected void prepareVideo() {
        LogUtil.i("prepareVideo [" + this.hashCode() + "] ");
        JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
        if (listener != null) {
            listener.onCompletion();
        }
        JCVideoPlayerManager.setListener(this);
        addTextureView();
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        Activity activity = (Activity) context;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        JCMediaManager.instance().prepare(url, mapHeadData, looping);
        setUiWitStateAndScreen(CURRENT_STATE_PREPAREING);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int id = v.getId();
        if (id == R.id.rl_surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    LogUtil.i("onTouch surfaceContainer actionDown [" + this.hashCode() + "] ");
                    mTouchingProgressBar = true;

                    mDownX = x;
                    mDownY = y;
                    mChangeVolume = false;
                    mChangePosition = false;
                    /////////////////////
                    break;
                case MotionEvent.ACTION_MOVE:
                    LogUtil.i("onTouch surfaceContainer actionMove [" + this.hashCode() + "] ");
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
                    LogUtil.i("onTouch surfaceContainer actionUp [" + this.hashCode() + "] ");
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

    public void addTextureView() {
        LogUtil.i("addTextureView [" + this.hashCode() + "] ");
        if (rlSurfaceContainer.getChildCount() > 0) {
            rlSurfaceContainer.removeAllViews();
        }
        JCMediaManager.textureView = null;
        JCMediaManager.textureView = new JCResizeTextureView(getContext());
        JCMediaManager.textureView.setSurfaceTextureListener(this);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlSurfaceContainer.addView(JCMediaManager.textureView, layoutParams);
    }

    public void setUiWitStateAndScreen(int state) {
        currentState = state;
        switch (currentState) {
            case CURRENT_STATE_NORMAL:
                if (isCurrentMediaListener()) {
                    cancelProgressTimer();
                    JCMediaManager.instance().releaseMediaPlayer();
                }
                break;
            case CURRENT_STATE_PREPAREING:
                resetProgressAndTime();
                break;
            case CURRENT_STATE_PLAYING:
                startProgressTimer();
                break;
            case CURRENT_STATE_PAUSE:
                startProgressTimer();
                break;
            case CURRENT_STATE_ERROR:
                if (isCurrentMediaListener()) {
                    JCMediaManager.instance().releaseMediaPlayer();
                }
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                cancelProgressTimer();
                sbVideoProgress.setProgress(100);
                tvCurrentTime.setText(tvTotalTime.getText());
                break;
        }
    }

    protected void startProgressTimer() {
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 300);
    }

    protected void cancelProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
    }

    @Override
    public void onPrepared() {
        LogUtil.i("onPrepared " + " [" + this.hashCode() + "] ");

        if (currentState != CURRENT_STATE_PREPAREING) return;
        JCMediaManager.instance().mediaPlayer.start();
        if (seekToInAdvance != -1) {
            JCMediaManager.instance().mediaPlayer.seekTo(seekToInAdvance);
            seekToInAdvance = -1;
        }
        startProgressTimer();
        setUiWitStateAndScreen(CURRENT_STATE_PLAYING);
    }

    public void clearFullscreenLayout() {
        ViewGroup vp = (ViewGroup) ((Activity) context).findViewById(Window.ID_ANDROID_CONTENT);
        View oldF = vp.findViewById(FULLSCREEN_ID);
        View oldT = vp.findViewById(TINY_ID);
        if (oldF != null) {
            vp.removeView(oldF);
        }
        if (oldT != null) {
            vp.removeView(oldT);
        }
        showSupportActionBar(context);
    }

    @Override
    public void onAutoCompletion() {
        LogUtil.i("onAutoCompletion " + " [" + this.hashCode() + "] ");
        onEvent(JCBuriedPoint.ON_AUTO_COMPLETE);
        setUiWitStateAndScreen(CURRENT_STATE_AUTO_COMPLETE);
        if (rlSurfaceContainer.getChildCount() > 0) {
            rlSurfaceContainer.removeAllViews();
        }
        JCVideoPlayerManager.setLastListener(null);
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);

        Activity activity = (Activity) context;
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        clearFullscreenLayout();
    }

    @Override
    public void onCompletion() {
        LogUtil.i("onCompletion " + " [" + this.hashCode() + "] ");
        setUiWitStateAndScreen(CURRENT_STATE_NORMAL);
        if (rlSurfaceContainer.getChildCount() > 0) {
            rlSurfaceContainer.removeAllViews();
        }

        JCVideoPlayerManager.setListener(null);
        JCMediaManager.instance().currentVideoWidth = 0;
        JCMediaManager.instance().currentVideoHeight = 0;

        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        Activity activity = (Activity) context;
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        clearFullscreenLayout();

        activity.finish();
    }

    @Override
    public boolean goToOtherListener() {//这里这个名字这么写并不对,这是在回退的时候gotoother,如果直接gotoother就不叫这个名字
        LogUtil.i("goToOtherListener " + " [" + this.hashCode() + "] ");

        if (currentScreen == JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN
                || currentScreen == JCVideoPlayerStandard.SCREEN_WINDOW_TINY) {
            onEvent(currentScreen == JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN ?
                    JCBuriedPoint.ON_QUIT_FULLSCREEN :
                    JCBuriedPoint.ON_QUIT_TINYSCREEN);
            JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
            if (JCVideoPlayerManager.lastListener() == null) {//directly fullscreen
                if (listener != null) listener.onCompletion();
                showSupportActionBar(context);
                return true;
            }
            ViewGroup vp = (ViewGroup) ((Activity) context).findViewById(Window.ID_ANDROID_CONTENT);
            vp.removeView(this);
            JCVideoPlayerManager.setListener(JCVideoPlayerManager.lastListener());
            JCVideoPlayerManager.setLastListener(null);
            JCMediaManager.instance().lastState = currentState;//save state
            if (listener != null) listener.goBackThisListener();
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Override
    public void onBufferingUpdate(int percent) {
        if (currentState != CURRENT_STATE_NORMAL && currentState != CURRENT_STATE_PREPAREING) {
            LogUtil.i("onBufferingUpdate " + percent + " [" + this.hashCode() + "] ");
            setTextAndProgress(percent);
        }
    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.i("onError " + what + " - " + extra + " [" + this.hashCode() + "] ");
        if (what != 38 && what != -38) {
            setUiWitStateAndScreen(CURRENT_STATE_ERROR);
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        LogUtil.i("onInfo what - " + what + " extra - " + extra);
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mBackUpBufferState = currentState;
            setUiWitStateAndScreen(CURRENT_STATE_PLAYING_BUFFERING_START);
            LogUtil.i("MEDIA_INFO_BUFFERING_START");
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (mBackUpBufferState != -1) {
                setUiWitStateAndScreen(mBackUpBufferState);
                mBackUpBufferState = -1;
            }
            LogUtil.i("MEDIA_INFO_BUFFERING_END");
        }
    }

    @Override
    public void onVideoSizeChanged() {
        LogUtil.i("onVideoSizeChanged " + " [" + this.hashCode() + "] ");

        int mVideoWidth = JCMediaManager.instance().currentVideoWidth;
        int mVideoHeight = JCMediaManager.instance().currentVideoHeight;
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            JCMediaManager.textureView.requestLayout();
        }
    }

    @Override
    public void goBackThisListener() {
        LogUtil.i("goBackThisListener " + " [" + this.hashCode() + "] ");

        currentState = JCMediaManager.instance().lastState;
        setUiWitStateAndScreen(currentState);
        addTextureView();

        showSupportActionBar(context);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        LogUtil.i("onSurfaceTextureAvailable [" + this.hashCode() + "] ");
        this.surface = new Surface(surface);
        JCMediaManager.instance().setDisplay(this.surface);
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

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        LogUtil.i("bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ");
        cancelProgressTimer();
        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogUtil.i("bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ");
        onEvent(JCBuriedPoint.ON_SEEK_POSITION);
        startProgressTimer();
        ViewParent viewParent = getParent();
        while (viewParent != null) {
            viewParent.requestDisallowInterceptTouchEvent(false);
            viewParent = viewParent.getParent();
        }
        if (currentState != CURRENT_STATE_PLAYING &&
                currentState != CURRENT_STATE_PAUSE) return;
        int time = seekBar.getProgress() * getDuration() / 100;
        JCMediaManager.instance().mediaPlayer.seekTo(time);
        LogUtil.i("seekTo " + time + " [" + this.hashCode() + "] ");
    }

    public boolean backPress() {
        LogUtil.i("backPress");
        JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
        if (listener != null) {
            return listener.goToOtherListener();
        }
        return false;
    }

    private void startWindowFullscreen() {
        LogUtil.i("startWindowFullscreen " + " [" + this.hashCode() + "] ");

        hideSupportActionBar(context);

        ViewGroup vp = (ViewGroup) ((Activity) context).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(FULLSCREEN_ID);
        if (old != null) {
            vp.removeView(old);
        }
        if (rlSurfaceContainer.getChildCount() > 0) {
            rlSurfaceContainer.removeAllViews();
        }
        try {
            Constructor<JCVideoPlayer> constructor = (Constructor<JCVideoPlayer>) JCVideoPlayer.this.getClass().getConstructor(Context.class);
            JCVideoPlayer jcVideoPlayer = constructor.newInstance(getContext());
            jcVideoPlayer.setId(FULLSCREEN_ID);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            int w = wm.getDefaultDisplay().getWidth();
            int h = wm.getDefaultDisplay().getHeight();
            LayoutParams lp = new LayoutParams(h, w);
            lp.setMargins((w - h) / 2, -(w - h) / 2, 0, 0);
            vp.addView(jcVideoPlayer, lp);
            jcVideoPlayer.setUp(url, JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            jcVideoPlayer.setUiWitStateAndScreen(currentState);
            jcVideoPlayer.addTextureView();
            jcVideoPlayer.setRotation(90);

            JCVideoPlayerManager.setLastListener(this);
            JCVideoPlayerManager.setListener(jcVideoPlayer);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startWindowTiny() {
        LogUtil.i("startWindowTiny " + " [" + this.hashCode() + "] ");
        onEvent(JCBuriedPoint.ON_ENTER_TINYSCREEN);

        ViewGroup vp = (ViewGroup) ((Activity) context).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(TINY_ID);
        if (old != null) {
            vp.removeView(old);
        }
        if (rlSurfaceContainer.getChildCount() > 0) {
            rlSurfaceContainer.removeAllViews();
        }
        try {
            Constructor<JCVideoPlayer> constructor = (Constructor<JCVideoPlayer>) JCVideoPlayer.this.getClass().getConstructor(Context.class);
            JCVideoPlayer mJcVideoPlayer = constructor.newInstance(getContext());
            mJcVideoPlayer.setId(TINY_ID);
            LayoutParams lp = new LayoutParams(400, 400);
            lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            vp.addView(mJcVideoPlayer, lp);
            mJcVideoPlayer.setUp(url, JCVideoPlayerStandard.SCREEN_WINDOW_TINY, objects);
            mJcVideoPlayer.setUiWitStateAndScreen(currentState);
            mJcVideoPlayer.addTextureView();
            JCVideoPlayerManager.setLastListener(this);
            JCVideoPlayerManager.setListener(mJcVideoPlayer);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
                int position = getCurrentPositionWhenPlaying();
                int duration = getDuration();
                LogUtil.i("onProgressUpdate " + position + "/" + duration + " [" + this.hashCode() + "] ");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setTextAndProgress(0);
                    }
                });
            }
        }
    }

    protected int getCurrentPositionWhenPlaying() {
        int position = 0;
        if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
            try {
                position = (int) JCMediaManager.instance().mediaPlayer.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }

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

    protected void setTextAndProgress(int secProgress) {
        int position = getCurrentPositionWhenPlaying();
        int duration = getDuration();
        int progress = position * 100 / (duration == 0 ? 1 : duration);
        setProgressAndTime(progress, secProgress, position, duration);
    }

    protected void setProgressAndTime(int progress, int secProgress, int currentTime, int totalTime) {
        if (!mTouchingProgressBar) {
            if (progress != 0) sbVideoProgress.setProgress(progress);
        }
        if (secProgress > 95) secProgress = 100;
        if (secProgress != 0) sbVideoProgress.setSecondaryProgress(secProgress);
        tvCurrentTime.setText(JCUtils.stringForTime(currentTime));
        tvTotalTime.setText(JCUtils.stringForTime(totalTime));
    }

    protected void resetProgressAndTime() {
        sbVideoProgress.setProgress(0);
        sbVideoProgress.setSecondaryProgress(0);
        tvCurrentTime.setText(JCUtils.stringForTime(0));
        tvTotalTime.setText(JCUtils.stringForTime(0));
    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    releaseAllVideos();
                    LogUtil.i("AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (JCMediaManager.instance().mediaPlayer.isPlaying()) {
                        JCMediaManager.instance().mediaPlayer.pause();
                    }
                    LogUtil.i("AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };

    public void release() {
        if (isCurrentMediaListener() &&
                (System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) > FULL_SCREEN_NORMAL_DELAY) {
            LogUtil.i("release [" + this.hashCode() + "]");
            releaseAllVideos();
        }
    }

    protected boolean isCurrentMediaListener() {
        return JCVideoPlayerManager.listener() != null
                && JCVideoPlayerManager.listener() == this;
    }

    public void releaseAllVideos() {
        LogUtil.i("releaseAllVideos");
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

    public void setJcBuriedPoint(JCBuriedPoint jcBuriedPoint) {
        JC_BURIED_POINT = jcBuriedPoint;
    }

    public void onEvent(int type) {
        if (JC_BURIED_POINT != null && isCurrentMediaListener()) {
            JC_BURIED_POINT.onEvent(type, url, currentScreen, objects);
        }
    }

    public void startFullscreen(Context context, Class _class, String url, Object... objects) {

        hideSupportActionBar(context);
        ViewGroup vp = (ViewGroup) ((AppCompatActivity) context).findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(FULLSCREEN_ID);
        if (old != null) {
            vp.removeView(old);
        }
        try {
            Constructor<JCVideoPlayer> constructor = _class.getConstructor(Context.class);
            JCVideoPlayer jcVideoPlayer = constructor.newInstance(context);
            jcVideoPlayer.setId(FULLSCREEN_ID);
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int w = wm.getDefaultDisplay().getWidth();
            int h = wm.getDefaultDisplay().getHeight();
            LayoutParams lp = new LayoutParams(h, w);
            lp.setMargins((w - h) / 2, -(w - h) / 2, 0, 0);
            vp.addView(jcVideoPlayer, lp);

            jcVideoPlayer.setUp(url, JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            jcVideoPlayer.addTextureView();
            jcVideoPlayer.setRotation(90);

            jcVideoPlayer.startButton.performClick();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean ACTIONBAR_STATUS = false;

    private void hideSupportActionBar(Context context) {
        ActionBar ab = ((AppCompatActivity) context).getSupportActionBar();
        if (ab != null) {
            ACTIONBAR_STATUS = ab.isShowing();//本来就是显示的
            if (ACTIONBAR_STATUS) {
                ((AppCompatActivity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ab.setShowHideAnimationEnabled(false);
                ab.hide();
            }
        }
    }

    private void showSupportActionBar(Context context) {
        ((AppCompatActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar ab = ((AppCompatActivity) context).getSupportActionBar();
        if (ab != null) {
            if (ACTIONBAR_STATUS) {
                ab.setShowHideAnimationEnabled(false);
                ab.show();
            }
        }
    }

    public void showWifiDialog() {

    }

    protected void showProgressDialog(float deltaX,
                                      String seekTime, int seekTimePosition,
                                      String totalTime, int totalTimeDuration) {
    }

    protected void dismissProgressDialog() {

    }

    protected void showVolumeDialog(float deltaY, int volumePercent) {

    }

    protected void dismissVolumeDialog() {

    }

    public abstract int getLayoutId();

}
