package com.logex.videoplayer;

/**
 * Created by liguangxi
 * on 16/7/26.
 * JCMediaPlayerListener
 */
public interface JCMediaPlayerListener {

    /**
     * 播放准备中
     */
    void onPrepared();

    /**
     * 接收流信息
     *
     * @param what  接收流状态
     * @param extra 额外信息
     */
    void onInfo(int what, int extra);

    /**
     * 缓冲区更新
     *
     * @param percent 下载百分进度
     */
    void onBufferingUpdate(int percent);

    /**
     * 跳转进度完成
     */
    void onSeekComplete();

    /**
     * 视频大小改变
     */
    void onVideoSizeChanged();

    /**
     * 暂停播放 用于点击了下一个视频播放
     */
    void onVideoPause();

    /**
     * 播放完成
     */
    void onCompletion();

    /**
     * 播放返回
     *
     * @return true 消费事件
     */
    boolean onBackPress();

    /**
     * 播放错误
     *
     * @param what  错误码
     * @param extra 额外信息
     */
    void onError(int what, int extra);

}
