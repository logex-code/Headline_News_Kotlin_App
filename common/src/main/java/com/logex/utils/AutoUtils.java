package com.logex.utils;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 创建人: liguangxi
 * 日期: 2017/7/1
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * 自动适配工具类
 */
public class AutoUtils {
    private static int displayWidth; // 实际设备屏幕宽
    private static int displayHeight; // 实际设备屏幕高

    private static int designWidth; // 基设备屏幕宽
    private static int designHeight; // 基设备屏幕高

    private static double textPixelsRate;

    public static void setSize(Activity activity, boolean hasStatusBar, int designWidth, int designHeight) {
        if (activity == null || designWidth < 1 || designHeight < 1) return;
        Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        if (hasStatusBar) height -= StatusBarUtil.getStatusBarHeight(activity);


        AutoUtils.displayWidth = width;
        AutoUtils.displayHeight = height;

        AutoUtils.designWidth = designWidth;
        AutoUtils.designHeight = designHeight;

        double displayDiagonal = Math.sqrt(Math.pow(AutoUtils.displayWidth, 2) + Math.pow(displayHeight, 2));
        double designDiagonal = Math.sqrt(Math.pow(AutoUtils.designWidth, 2) + Math.pow(designHeight, 2));
        AutoUtils.textPixelsRate = displayDiagonal / designDiagonal;
    }

    public static void auto(Activity act) {
        if (act == null || displayWidth < 1 || displayHeight < 1) return;

        View view = act.getWindow().getDecorView();
        auto(view);
    }

    public static void auto(View view) {
        if (view == null || displayWidth < 1 || displayHeight < 1) return;

        AutoUtils.autoTextSize(view);
        AutoUtils.autoSize(view);
        AutoUtils.autoPadding(view);
        AutoUtils.autoMargin(view);

        if (view instanceof ViewGroup) {
            auto((ViewGroup) view);
        }

    }

    private static void auto(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();

        for (int i = 0; i < count; i++) {

            View child = viewGroup.getChildAt(i);

            if (child != null) {
                auto(child);
            }
        }
    }

    public static void autoMargin(View view) {
        if (!(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams))
            return;

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (lp == null) return;

        lp.leftMargin = getDisplayWidthValue(lp.leftMargin);
        lp.topMargin = getDisplayHeightValue(lp.topMargin);
        lp.rightMargin = getDisplayWidthValue(lp.rightMargin);
        lp.bottomMargin = getDisplayHeightValue(lp.bottomMargin);

    }

    public static void autoPadding(View view) {
        int l = view.getPaddingLeft();
        int t = view.getPaddingTop();
        int r = view.getPaddingRight();
        int b = view.getPaddingBottom();

        l = getDisplayWidthValue(l);
        t = getDisplayHeightValue(t);
        r = getDisplayWidthValue(r);
        b = getDisplayHeightValue(b);

        view.setPadding(l, t, r, b);
    }

    public static void autoSize(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();

        if (lp == null) return;
        int oldWeight = lp.width;
        if (oldWeight > 0) {
            lp.width = getDisplayWidthValue(lp.width);
        }

        if (lp.height > 0) {
            // fix这里假如布局宽高设置一样
            if (oldWeight == lp.height) {
                lp.height = getDisplayWidthValue(oldWeight);
            } else {
                lp.height = getDisplayHeightValue(lp.height);
            }
        }

    }

    public static void autoTextSize(View view) {
        if (view instanceof TextView) {
            double designPixels = ((TextView) view).getTextSize();
            double displayPixels = textPixelsRate * designPixels;
            ((TextView) view).setIncludeFontPadding(false);
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) displayPixels);
        }
    }

    public static int autoTextSize(int size) {
        return (int) (textPixelsRate * size);
    }

    public static int getDisplayWidthValue(int designWidthValue) {
        if (designWidthValue < 2) {
            return designWidthValue;
        }
        return designWidthValue * displayWidth / designWidth;
    }

    public static int getDisplayHeightValue(int designHeightValue) {
        if (designHeightValue < 2) {
            return designHeightValue;
        }
        return designHeightValue * displayHeight / designHeight;
    }

}
