package com.logex.videoplayer;

import java.lang.ref.WeakReference;

/**
 * Put JCVideoPlayer into layout
 * From a JCVideoPlayer to another JCVideoPlayer
 * Created by liguangxi on 16/7/26.
 */
public class JCVideoPlayerManager {
    private static WeakReference<JCMediaPlayerListener> LISTENER;
    private static WeakReference<JCMediaPlayerListener> LAST_LISTENER;

    /**
     * 设置当前播放器监听事件
     *
     * @param listener 播放事件
     */
    public static void setListener(JCMediaPlayerListener listener) {
        if (listener != null) {
            LISTENER = new WeakReference<>(listener);
        }
    }

    /**
     * 设置最后播放器监听事件
     *
     * @param listener 播放事件
     */
    public static void setLastListener(JCMediaPlayerListener listener) {
        if (listener != null) {
            LAST_LISTENER = new WeakReference<>(listener);
        }
    }

    /**
     * 获取当前播放器监听事件
     *
     * @return 当前播放器事件
     */
    public static JCMediaPlayerListener listener() {
        if (LISTENER != null) {
            return LISTENER.get();
        }
        return null;
    }

    /**
     * 获取最后播放器监听事件
     *
     * @return 最后播放器事件
     */
    public static JCMediaPlayerListener lastListener() {
        if (LAST_LISTENER != null) {
            return LAST_LISTENER.get();
        }
        return null;
    }

    /**
     * 暂停全部视频
     */
    public static void onVideoPauseAll() {
        if (LISTENER != null) {
            LISTENER.get().onVideoPause();
        }
        if (LAST_LISTENER != null) {
            LAST_LISTENER.get().onVideoPause();
        }
    }
}
