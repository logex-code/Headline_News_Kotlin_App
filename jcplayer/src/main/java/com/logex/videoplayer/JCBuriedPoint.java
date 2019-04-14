package com.logex.videoplayer;

/**
 * Created by liguangxi
 * On 2016/04/04 22:13
 * 提供打点功能
 */
public interface JCBuriedPoint {

    /**
     * 点击播放按钮
     */
    int ON_CLICK_START_ICON = 0;
    int ON_CLICK_START_ERROR = 1;
    int ON_CLICK_START_COMPLETE = 2;
    int ON_CLICK_PAUSE = 3;
    int ON_CLICK_RESUME = 4;

    int ON_SEEK_POSITION = 5;
    int ON_PLAY_PAUSE = 6;
    int ON_PLAY_COMPLETE = 7;

    int ON_ENTER_FULLSCREEN = 8;
    int ON_QUIT_FULLSCREEN = 9;


    int ON_TOUCH_SCREEN_SEEK_VOLUME = 10;
    int ON_TOUCH_SCREEN_SEEK_POSITION = 11;

    void onEvent(int type, String url, int screen, Object... objects);

}
