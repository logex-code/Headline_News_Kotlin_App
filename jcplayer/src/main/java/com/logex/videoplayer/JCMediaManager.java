package com.logex.videoplayer;

import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Surface;

import com.logex.utils.LogUtil;

import java.lang.reflect.Method;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * <p>统一管理MediaPlayer的地方,只有一个mediaPlayer实例，那么不会有多个视频同时播放，也节省资源。</p>
 * <p>Unified management MediaPlayer place, there is only one MediaPlayer instance,
 * then there will be no more video broadcast at the same time, also save resources.</p>
 * Created by liguangxi
 * On 2015/11/30 15:39
 */
public class JCMediaManager implements IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnInfoListener {
    private static JCMediaManager JCMediaManager;
    public IjkMediaPlayer mediaPlayer;

    public int currentVideoWidth = 0;
    public int currentVideoHeight = 0;

    /**
     * 准备播放
     */
    private static final int HANDLER_PREPARE = 0;
    /**
     * 开始显示播放内容
     */
    private static final int HANDLER_SET_DISPLAY = 1;
    /**
     * 释放播放器
     */
    private static final int HANDLER_RELEASE = 2;

    private MediaHandler mMediaHandler;
    private Handler mainThreadHandler;

    public static JCMediaManager instance() {
        if (JCMediaManager == null) {
            JCMediaManager = new JCMediaManager();
        }
        return JCMediaManager;
    }

    private JCMediaManager() {
        mediaPlayer = new IjkMediaPlayer();
        HandlerThread mMediaHandlerThread = new HandlerThread("JCMediaManager");
        mMediaHandlerThread.start();
        mMediaHandler = new MediaHandler((mMediaHandlerThread.getLooper()));
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    private class MediaHandler extends Handler {

        MediaHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_PREPARE:
                    try {
                        currentVideoWidth = 0;
                        currentVideoHeight = 0;
                        mediaPlayer.release();
                        mediaPlayer = new IjkMediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        Class<IjkMediaPlayer> clazz = IjkMediaPlayer.class;
                        Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
                        method.invoke(mediaPlayer, ((FuckBean) msg.obj).url, ((FuckBean) msg.obj).mapHeadData);
                        mediaPlayer.setLooping(((FuckBean) msg.obj).looping);
                        mediaPlayer.setOnPreparedListener(JCMediaManager.this);
                        mediaPlayer.setOnCompletionListener(JCMediaManager.this);
                        mediaPlayer.setOnBufferingUpdateListener(JCMediaManager.this);
                        mediaPlayer.setScreenOnWhilePlaying(true);
                        mediaPlayer.setOnSeekCompleteListener(JCMediaManager.this);
                        mediaPlayer.setOnErrorListener(JCMediaManager.this);
                        mediaPlayer.setOnInfoListener(JCMediaManager.this);
                        mediaPlayer.setOnVideoSizeChangedListener(JCMediaManager.this);
                        mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDLER_SET_DISPLAY:
                    if (msg.obj == null) {
                        mediaPlayer.setSurface(null);
                    } else {
                        Surface Surface = (Surface) msg.obj;
                        if (Surface.isValid()) {
                            LogUtil.i("set surface");
                            mediaPlayer.setSurface(Surface);
                        }
                    }
                    break;
                case HANDLER_RELEASE:
                    mediaPlayer.release();
                    break;
            }
        }
    }

    /**
     * 准备播放
     *
     * @param url         播放地址
     * @param mapHeadData mapHeadData
     * @param loop        是否循环播放
     */
    public void prepare(String url, Map<String, String> mapHeadData, boolean loop) {
        if (TextUtils.isEmpty(url)) return;
        Message msg = mMediaHandler.obtainMessage();
        msg.what = HANDLER_PREPARE;
        msg.obj = new FuckBean(url, mapHeadData, loop);
        mMediaHandler.sendMessage(msg);
    }

    /**
     * 设置Surface
     *
     * @param Surface Surface
     */
    public void setDisplay(Surface Surface) {
        Message msg = mMediaHandler.obtainMessage();
        msg.what = HANDLER_SET_DISPLAY;
        msg.obj = Surface;
        mMediaHandler.sendMessage(msg);
    }

    /**
     * 释放播放器
     */
    public void releaseMediaPlayer() {
        Message msg = mMediaHandler.obtainMessage();
        msg.what = HANDLER_RELEASE;
        mMediaHandler.sendMessage(msg);
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
                if (listener != null) {
                    listener.onPrepared();
                }
            }
        });
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
                if (listener != null) {
                    listener.onCompletion();
                }
                JCMediaPlayerListener lastListener = JCVideoPlayerManager.lastListener();
                if (lastListener != null) {
                    lastListener.onCompletion();
                }
            }
        });
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, final int percent) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
                if (listener != null) {
                    listener.onBufferingUpdate(percent);
                }
            }
        });
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
                if (listener != null) {
                    listener.onSeekComplete();
                }
            }
        });
    }

    @Override
    public boolean onError(IMediaPlayer mp, final int what, final int extra) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
                if (listener != null) {
                    listener.onError(what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, final int what, final int extra) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
                if (listener != null) {
                    listener.onInfo(what, extra);
                }
            }
        });
        return false;
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
        currentVideoWidth = mp.getVideoWidth();
        currentVideoHeight = mp.getVideoHeight();
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                JCMediaPlayerListener listener = JCVideoPlayerManager.listener();
                if (listener != null) {
                    listener.onVideoSizeChanged();
                }
            }
        });
    }

    private class FuckBean {
        String url;
        Map<String, String> mapHeadData;
        boolean looping;

        FuckBean(String url, Map<String, String> mapHeadData, boolean loop) {
            this.url = url;
            this.mapHeadData = mapHeadData;
            this.looping = loop;
        }
    }
}
