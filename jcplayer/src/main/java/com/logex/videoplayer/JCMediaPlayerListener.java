package com.logex.videoplayer;

/**
 * Created by Nathen on 16/7/26.
 * JCMediaPlayerListener
 */
public interface JCMediaPlayerListener {
    void onPrepared();

    void onCompletion();

    void onAutoCompletion();

    void onBufferingUpdate(int percent);

    void onSeekComplete();

    void onError(int what, int extra);

    void onInfo(int what, int extra);

    void onVideoSizeChanged();

    void goBackThisListener();

    boolean goToOtherListener();

}
