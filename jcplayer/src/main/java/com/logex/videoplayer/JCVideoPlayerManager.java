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

    public static void setListener(JCMediaPlayerListener listener) {
        if (listener != null) {
            LISTENER = new WeakReference<>(listener);
        }
    }

    public static void setLastListener(JCMediaPlayerListener listener) {
        if (listener != null) {
            LAST_LISTENER = new WeakReference<>(listener);
        }
    }

    public static JCMediaPlayerListener listener() {
        if (LISTENER != null) {
            return LISTENER.get();
        }
        return null;
    }

    public static JCMediaPlayerListener lastListener() {
        if (LAST_LISTENER != null) {
            return LAST_LISTENER.get();
        }
        return null;
    }
}
