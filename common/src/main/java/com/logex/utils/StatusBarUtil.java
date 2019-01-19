package com.logex.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 创建人: liguangxi
 * 日期: 17/7/19
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 状态栏工具类
 */
public class StatusBarUtil {
    private static volatile int statusHeight; // 状态栏高度

    /**
     * 透明状态栏
     *
     * @param activity activity
     */
    public static void setTranslucentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0+ 实现
            Window window = activity.getWindow();
            // 因为EMUI3.1系统与这种沉浸式方案API有点冲突，会没有沉浸式效果。
            if (isEMUI3_1()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4 实现
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private static boolean isEMUI3_1() {
        return "EmotionUI_3.1".equals(getEmuiVersion());
    }

    /**
     * 获取EMUI版本
     *
     * @return 版本
     */
    private static String getEmuiVersion() {
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            return (String) getMethod.invoke(classType, "ro.build.version.emui");
        } catch (Exception e) {
            LogUtil.i("非华为EMUI...............");
        }
        return "";
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (statusHeight > 0) return statusHeight;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 生成一个和状态栏大小相同的矩形条 * * @param activity 需要设置的activity *
     *
     * @param color 状态栏颜色值 *
     * @return 状态栏矩形条
     */
    public static View createStatusView(Context context, @ColorRes int color) {
        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(context));
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(context.getResources().getColor(color));
        return statusView;
    }

    /**
     * 设置状态栏Dark模式
     *
     * @param activity activity
     * @param dark     是否Dark模式
     */
    public static boolean setStatusBarDarkMode(Activity activity, boolean dark) {
        Window window = activity.getWindow();
        if (RomUtil.isMiui()) {
            // MIUI
            boolean result = setStatusBarDarkMIUI(activity, dark);
            return result || setStatusBarDarkMarshmallow(window, dark);
        } else if (RomUtil.isFlyme()) {
            // Flyme
            boolean result = setStatusBarDarkFlyme(window, dark);
            return result || setStatusBarDarkMarshmallow(window, dark);
        }
        return setStatusBarDarkMarshmallow(window, dark);
    }

    /**
     * MIUI系统设置状态栏Dark模式
     *
     * @param activity activity
     * @param dark     是否Dark模式
     */
    private static boolean setStatusBarDarkMIUI(Activity activity, boolean dark) {
        Window window = activity.getWindow();
        Class<? extends Window> clazz = window.getClass();
        try {
            // 适配android6.0之前miui
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, dark ? darkModeFlag : 0, darkModeFlag);
            // 适配miui v9 实现改成了谷歌官方标准
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int vis = decorView.getSystemUiVisibility();
                    if (dark) {
                        vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    } else {
                        vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    }
                    decorView.setSystemUiVisibility(vis);
                }
            }
            return true;
        } catch (Exception e) {
            LogUtil.e("非MIUI系统.......");
        }
        return false;
    }

    /**
     * Flyme系统设置状态栏Dark模式
     *
     * @param window window
     * @param dark   是否Dark模式
     * @return 是否Dark模式
     */
    private static boolean setStatusBarDarkFlyme(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                LogUtil.e("非Flyme系统...........");
            }
        }
        return result;
    }

    /**
     * 安卓6.0以上系统设置状态栏Dark模式
     *
     * @param window window
     * @param dark   是否Dark模式
     */
    private static boolean setStatusBarDarkMarshmallow(Window window, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (dark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
            return true;
        }
        return false;
    }
}
