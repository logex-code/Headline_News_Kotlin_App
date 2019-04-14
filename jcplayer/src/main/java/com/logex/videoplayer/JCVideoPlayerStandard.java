package com.logex.videoplayer;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.logex.utils.UIUtils;
import com.logex.widget.DividerLine;
import com.logex.widget.IosAlertDialog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nathen
 * On 2016/04/18 16:15
 * 标准播放界面
 */
public class JCVideoPlayerStandard extends JCVideoPlayer {
    public LinearLayout llTopContainer;
    public DividerLine dlHeader;
    public ImageView ivVideoBack;
    public ProgressBar pbPlayBottom;
    public TextView tvVideoTitle;
    public ImageView ivVideoThumbnail;

    protected Timer DISMISS_CONTROL_VIEW_TIMER;
    protected DismissControlViewTimerTask mDismissControlViewTimerTask;

    protected Dialog mProgressDialog;
    protected ProgressBar mDialogProgressBar;
    protected TextView mDialogSeekTime;
    protected TextView mDialogTotalTime;
    protected ImageView mDialogIcon;

    protected Dialog mVolumeDialog;
    protected ImageView mDialogVolumeImageView;
    protected ProgressBar mDialogVolumeProgressBar;
    protected TextView mDialogVolumeTextView;

    public JCVideoPlayerStandard(Context context) {
        super(context);
    }

    public JCVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        llTopContainer = (LinearLayout) findViewById(R.id.ll_top_container);
        dlHeader = (DividerLine) findViewById(R.id.dl_header);
        pbPlayBottom = (ProgressBar) findViewById(R.id.pb_play_bottom);
        tvVideoTitle = (TextView) findViewById(R.id.tv_video_title);
        ivVideoBack = (ImageView) findViewById(R.id.iv_video_back);
        ivVideoThumbnail = (ImageView) findViewById(R.id.iv_video_thumbnail);

        ivVideoThumbnail.setOnClickListener(this);
        ivVideoBack.setOnClickListener(this);
    }

    @Override
    public boolean setUp(String url, int screen, Object... objects) {
        if (objects.length == 0) return false;
        if (super.setUp(url, screen, objects)) {
            if (currentScreen == SCREEN_WINDOW_LIST) {
                ivPlayFullscreen.setImageResource(R.drawable.jc_enlarge);
                tvVideoTitle.setText(objects[0].toString());
            } else if (currentScreen == SCREEN_WINDOW_NORMAL) {
                ivPlayFullscreen.setImageResource(R.drawable.jc_enlarge);
            } else if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                ivPlayFullscreen.setImageResource(R.drawable.jc_shrink);
                tvVideoTitle.setText(objects[0].toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.jc_layout_standard;
    }

    @Override
    public void setUiWitStateAndScreen(int state) {
        super.setUiWitStateAndScreen(state);
        switch (currentState) {
            case CURRENT_STATE_NORMAL:
                changeUiToNormalShow();
                cancelDismissControlViewTimer();
                break;
            case CURRENT_STATE_PREPARING:
                changeUiToPreparingShow();
                startDismissControlViewTimer();
                break;
            case CURRENT_STATE_PLAYING:
                changeUiToPlayingShow();
                startDismissControlViewTimer();
                break;
            case CURRENT_STATE_PAUSE:
                changeUiToPauseShow();
                cancelDismissControlViewTimer();
                break;
            case CURRENT_STATE_ERROR:
                changeUiToErrorShow();
                break;
            case CURRENT_STATE_COMPLETE:
                changeUiToCompleteShow();
                cancelDismissControlViewTimer();
                pbPlayBottom.setProgress(100);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.fl_surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    if (mChangePosition) {
                        int duration = getDuration();
                        int progress = mSeekTimePosition * 100 / (duration == 0 ? 1 : duration);
                        pbPlayBottom.setProgress(progress);
                    }
                    if (!mChangePosition && !mChangeVolume) {
                        onEvent(JCBuriedPointStandard.ON_CLICK_BLANK);
                        onClickUiToggle();
                    }
                    break;
            }
        }
        return super.onTouch(v, event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.iv_video_thumbnail) {
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
                onEvent(JCBuriedPointStandard.ON_CLICK_START_THUMB); //开始的事件应该在播放之后，此处特殊
            } else if (currentState == CURRENT_STATE_COMPLETE) {
                onClickUiToggle();
            }
        } else if (id == R.id.fl_surface_container) {
            startDismissControlViewTimer();
        } else if (id == R.id.iv_video_back) {
            // 返回
            JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
            if (listener != null) {
                listener.onBackPress();
            }
        }
    }

    @Override
    public void showWifiDialog() {
        super.showWifiDialog();
        new IosAlertDialog(context).builder()
                .setTitle("温馨提示")
                .setMsg(getResources().getString(R.string.tips_not_wifi))
                .setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel), null)
                .setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onEvent(JCBuriedPointStandard.ON_CLICK_START_WIFIDIALOG);
                        startVideo();
                        WIFI_TIP_DIALOG_SHOWED = true;
                    }
                })
                .setCanceledOnTouchOutside(false)
                .show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        startDismissControlViewTimer();
    }

    /**
     * 切换状态显示 如播放状态时 手指触碰抬起 底部栏要显示
     */
    private void onClickUiToggle() {
        switch (currentState) {
            case CURRENT_STATE_PREPARING:
                if (llBottomContainer.getVisibility() == GONE) {
                    changeUiToPreparingToggle();
                } else {
                    changeUiToPreparingShow();
                }
                break;
            case CURRENT_STATE_PLAYING:
                if (llBottomContainer.getVisibility() == GONE) {
                    changeUiToPlayingToggle();
                } else {
                    changeUiToPlayingShow();
                }
                break;
            case CURRENT_STATE_PAUSE:
                if (llBottomContainer.getVisibility() == VISIBLE) {
                    changeUiToPauseToggle();
                } else {
                    changeUiToPauseShow();
                }
                break;
            case CURRENT_STATE_COMPLETE:
                if (ivPlayStart.getVisibility() == VISIBLE) {
                    changeUiToCompleteToggle();
                } else {
                    changeUiToCompleteShow();
                }
                break;
        }
    }

    @Override
    protected void setDownProgressShow(int progress) {
        super.setDownProgressShow(progress);
        if (progress > 0) {
            pbPlayBottom.setSecondaryProgress(progress);
        }
    }

    @Override
    protected void setProgressAndTime(int progress, int currentTime, int totalTime) {
        super.setProgressAndTime(progress, currentTime, totalTime);
        if (progress > 0) {
            pbPlayBottom.setProgress(progress);
        }
    }

    @Override
    protected void resetProgressAndTime() {
        super.resetProgressAndTime();
        pbPlayBottom.setProgress(0);
        pbPlayBottom.setSecondaryProgress(0);
    }

    /**
     * 显示默认UI
     */
    protected void changeUiToNormalShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(VISIBLE, GONE, VISIBLE, GONE, GONE, VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, GONE, VISIBLE, GONE, GONE, VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, GONE, VISIBLE, GONE, GONE, VISIBLE);
                updateStartImage();
                break;
        }
    }

    /**
     * 显示播放准备UI
     */
    protected void changeUiToPreparingShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(VISIBLE, GONE, GONE, VISIBLE, GONE, VISIBLE);
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, GONE, GONE, VISIBLE, GONE, VISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, GONE, GONE, VISIBLE, GONE, VISIBLE);
                break;
        }
    }

    /**
     * 切换播放准备UI
     */
    private void changeUiToPreparingToggle() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, VISIBLE, GONE, VISIBLE, GONE, GONE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, VISIBLE, GONE, VISIBLE, GONE, GONE);
                break;
        }
    }

    /**
     * 显示正在播放UI
     */
    protected void changeUiToPlayingShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(GONE, GONE, GONE, GONE, VISIBLE, GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, GONE, GONE, GONE, VISIBLE, GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, GONE, GONE, GONE, VISIBLE, GONE);
                updateStartImage();
                break;
        }
    }

    /**
     * 切换正在播放UI
     */
    protected void changeUiToPlayingToggle() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, GONE, GONE, GONE);
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, GONE, GONE, GONE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, GONE, GONE, GONE);
                break;
        }
    }

    /**
     * 显示暂停播放UI
     */
    protected void changeUiToPauseShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, GONE, GONE, GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, GONE, GONE, GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, GONE, GONE, GONE);
                updateStartImage();
                break;
        }
    }

    /**
     * 切换暂停播放UI
     */
    protected void changeUiToPauseToggle() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(VISIBLE, GONE, GONE, GONE, VISIBLE, GONE);
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, GONE, GONE, GONE, VISIBLE, GONE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, GONE, GONE, GONE, VISIBLE, GONE);
                break;
        }
    }

    /**
     * 显示播放完成UI
     */
    protected void changeUiToCompleteShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(VISIBLE, GONE, VISIBLE, GONE, GONE, VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, GONE, VISIBLE, GONE, GONE, VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, GONE, VISIBLE, GONE, GONE, VISIBLE);
                updateStartImage();
                break;
        }
    }

    /**
     * 切换播放完成UI
     */
    private void changeUiToCompleteToggle() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(GONE, GONE, GONE, GONE, GONE, VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, GONE, GONE, GONE, GONE, VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, GONE, GONE, GONE, GONE, VISIBLE);
                updateStartImage();
                break;
        }
    }

    /**
     * 显示播放错误UI
     */
    private void changeUiToErrorShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisible(GONE, GONE, VISIBLE, GONE, GONE, GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_NORMAL:
                setAllControlsVisible(VISIBLE, GONE, VISIBLE, GONE, GONE, GONE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, GONE, VISIBLE, GONE, GONE, GONE);
                updateStartImage();
                break;
        }
    }

    /**
     * 设置控件显示或隐藏
     *
     * @param topCon    顶部标题栏
     * @param bottomCon 底部控制栏
     * @param startBtn  开始播放按钮
     * @param loadingPb 播放加载
     * @param bottomPb  底部播放进度条
     * @param thumbImg  缩略图
     */
    private void setAllControlsVisible(int topCon, int bottomCon, int startBtn, int loadingPb,
                                       int bottomPb, int thumbImg) {
        llTopContainer.setVisibility(topCon);
        llBottomContainer.setVisibility(bottomCon);
        ivPlayStart.setVisibility(startBtn);
        pbPlayLoading.setVisibility(loadingPb);
        pbPlayBottom.setVisibility(bottomPb);
        ivVideoThumbnail.setVisibility(thumbImg);
    }

    /**
     * 更新播放按钮图标
     */
    private void updateStartImage() {
        switch (currentState) {
            case CURRENT_STATE_PLAYING:
                ivPlayStart.setImageResource(R.drawable.jc_click_pause_selector);
                break;
            case CURRENT_STATE_ERROR:
                ivPlayStart.setImageResource(R.drawable.jc_click_error_selector);
                break;
            case CURRENT_STATE_COMPLETE:
                ivPlayStart.setImageResource(R.drawable.jc_click_replay_selector);
                break;
            default:
                ivPlayStart.setImageResource(R.drawable.jc_click_play_selector);
                break;
        }
    }

    @Override
    protected void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jc_dialog_progress, null);
            mDialogProgressBar = ((ProgressBar) localView.findViewById(R.id.duration_progressbar));
            mDialogSeekTime = ((TextView) localView.findViewById(R.id.tv_current));
            mDialogTotalTime = ((TextView) localView.findViewById(R.id.tv_duration));
            mDialogIcon = ((ImageView) localView.findViewById(R.id.duration_image_tip));
            mProgressDialog = createDialogWithView(localView);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        mDialogSeekTime.setText(seekTime);
        mDialogTotalTime.setText(String.format("%1$s/", totalTime));
        mDialogProgressBar.setProgress(totalTimeDuration <= 0 ? 0 : (seekTimePosition * 100 / totalTimeDuration));
        if (deltaX > 0) {
            mDialogIcon.setBackgroundResource(R.drawable.jc_forward_icon);
        } else {
            mDialogIcon.setBackgroundResource(R.drawable.jc_backward_icon);
        }
    }

    @Override
    protected void dismissProgressDialog() {
        super.dismissProgressDialog();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void showVolumeDialog(float deltaY, int volumePercent) {
        super.showVolumeDialog(deltaY, volumePercent);
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jc_dialog_volume, null);
            mDialogVolumeImageView = localView.findViewById(R.id.volume_image_tip);
            mDialogVolumeTextView = localView.findViewById(R.id.tv_volume);
            mDialogVolumeProgressBar = ((ProgressBar) localView.findViewById(R.id.volume_progressbar));
            mVolumeDialog = createDialogWithView(localView);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        if (volumePercent <= 0) {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jc_close_volume);
        } else {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jc_add_volume);
        }
        if (volumePercent > 100) {
            volumePercent = 100;
        } else if (volumePercent < 0) {
            volumePercent = 0;
        }
        mDialogVolumeTextView.setText(volumePercent + "%");
        mDialogVolumeProgressBar.setProgress(volumePercent);
    }

    @Override
    protected void dismissVolumeDialog() {
        super.dismissVolumeDialog();
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
        }
    }

    public Dialog createDialogWithView(View localView) {
        Dialog dialog = new Dialog(getContext(), R.style.jc_style_dialog_progress);
        dialog.setContentView(localView);
        Window window = dialog.getWindow();
        window.addFlags(Window.FEATURE_ACTION_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        window.setLayout(-2, -2);
        WindowManager.LayoutParams localLayoutParams = window.getAttributes();
        localLayoutParams.gravity = Gravity.CENTER;
        window.setAttributes(localLayoutParams);
        return dialog;
    }

    /**
     * 开启一段时间隐藏播放窗口控件定时器
     */
    private void startDismissControlViewTimer() {
        // 首先取消
        cancelDismissControlViewTimer();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        DISMISS_CONTROL_VIEW_TIMER.schedule(mDismissControlViewTimerTask, 2500);
    }

    /**
     * 取消一段时间隐藏播放窗口控件定时器
     */
    private void cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
        }
    }

    /**
     * 一段时间隐藏操作控件定时器(正在播放时)
     */
    private class DismissControlViewTimerTask extends TimerTask {

        @Override
        public void run() {
            if (currentState == CURRENT_STATE_PLAYING && ivPlayStart != null) {
                ivPlayStart.post(new Runnable() {
                    @Override
                    public void run() {
                        autoDismissControlView();
                    }
                });
            }
        }
    }

    /**
     * 播放后自动隐藏一些控件
     */
    protected void autoDismissControlView() {
        pbPlayBottom.setVisibility(VISIBLE);
        llBottomContainer.setVisibility(GONE);
        ivPlayStart.setVisibility(GONE);
    }
}
