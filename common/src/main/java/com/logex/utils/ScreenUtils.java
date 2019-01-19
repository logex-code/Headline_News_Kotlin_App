package com.logex.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * 获得屏幕相关的辅助类
 *
 * @author liguangxi
 */
public class ScreenUtils {

    /**
     * 获得屏幕宽度
     *
     * @param context context
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context context
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity activity
     * @return 屏幕截图
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity activity
     * @return 屏幕截图
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取屏幕原始尺寸高度，包括虚拟功能键高度
     *
     * @param context context
     * @return 尺寸高度
     */
    public static int getDpi(Context context) {
        int dpi;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        Class c;
        try {
            c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
            dpi = getScreenHeight(context);
        }
        return dpi;
    }

    /**
     * 判断设备是否有虚拟键
     *
     * @param context context
     * @return 有true 没有false
     */
    public static boolean isVirtualKey(Context context) {
        return getScreenHeight(context) != getDpi(context);
    }

    /**
     * 获取 虚拟按键的高度
     *
     * @param context context
     * @return 虚拟按键的高度
     */
    public static int getBottomStatusHeight(Context context) {
        int totalHeight = getDpi(context);
        int contentHeight = getScreenHeight(context);
        return totalHeight - contentHeight;
    }

    /**
     * 判断是否是刘海屏(这里只是个简便的判断方式)
     *
     * @return true有
     */
    public static boolean hasNotchScreen(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(context);
        return statusBarHeight > DensityUtil.dip2px(context, 32);
    }
}
