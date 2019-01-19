package com.logex.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.ArrayRes;
import android.telephony.PhoneNumberUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.logex.common.R;
import com.logex.widget.IosAlertDialog;

/**
 * 创建人: liguangxi
 * 日期: 17/7/19
 * 邮箱 956328710@qq.com
 * 版本 1.0
 * UI工具类
 */
public class UIUtils {
    private static Toast mToast;

    /**
     * 从资源文件取得颜色id
     *
     * @param context 上下文
     * @param colorId 资源id
     * @return 颜色id
     */
    public static int getColor(Context context, int colorId) {
        return context.getResources().getColor(colorId);
    }

    /**
     * 从资源文件获取指定字符串
     *
     * @param context 上下文
     * @param strId   资源id
     * @return 字符串
     */
    public static String getString(Context context, int strId) {
        return context.getString(strId);
    }

    /**
     * 将布局文件转成view
     *
     * @param context  上下文
     * @param layoutId 布局id
     * @return view
     */
    public static View getXmlView(Context context, int layoutId) {
        return View.inflate(context, layoutId, null);
    }

    /**
     * 从资源文件获取数组
     *
     * @param context 上下文
     * @param arrId   资源id
     * @return string数组
     */
    public static String[] getStringArr(Context context, @ArrayRes int arrId) {
        return context.getResources().getStringArray(arrId);
    }

    /**
     * 从资源文件获取数组
     *
     * @param context context
     * @param arrId   资源id
     * @return int数组
     */
    public static int[] getIntResArr(Context context, @ArrayRes int arrId) {
        TypedArray ar = context.getResources().obtainTypedArray(arrId);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++) {
            resIds[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        return resIds;
    }

    /**
     * 弹出普通提示信息
     *
     * @param context 上下文
     * @param msg     内容
     */
    public static void showToast(Context context, String msg) {
        if (msg == null || context == null) return;

        if (mToast == null) {
            View view = UIUtils.getXmlView(context.getApplicationContext(), R.layout.layout_custom_toast_view);
            AutoUtils.auto(view);

            mToast = new Toast(context.getApplicationContext());
            mToast.setView(view);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        ((TextView) mToast.getView().findViewById(R.id.tv_toast)).setText(msg);
        mToast.show();
    }

    /**
     * 没有网络弹窗
     */
    public static void showNoNetDialog(final Context context) {
        new IosAlertDialog(context).builder()
                .setTitle("提示")
                .setMsg(context.getString(R.string.message_network_unavailable))
                .setNegativeButton("去设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).show();
    }

    /**
     * 弹出提示信息
     *
     * @param context context
     * @param message 内容
     */
    public static void showTipDialog(Context context, String message) {
        new IosAlertDialog(context)
                .builder()
                .setTitle("提示")
                .setMsg(message)
                .setNegativeButton(context.getString(R.string.confirm), null)
                .show();
    }

    /**
     * 从服务器或本地获取图片
     *
     * @param context context
     * @param view    要显示的控件
     * @param url     图片链接
     * @param id      服务器没有图片，显示默认的图片id
     */
    public static void showImgFromUrl(Context context, ImageView view, String url, int id) {
        Glide.with(context).load(url)
                .asBitmap()
                .placeholder(id)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    /**
     * 从服务器或本地获取图片(圆形)
     *
     * @param context context
     * @param view    要显示的控件
     * @param url     图片链接
     * @param id      服务器没有图片，显示默认的图片id
     */
    public static void showCircleImage(Context context, ImageView view, String url, int id) {
        Glide.with(context).load(url)
                .transform(new GlideCircleTransform(context))
                .placeholder(id)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    /**
     * 从服务器或本地获取图片(圆形带边框)
     *
     * @param context context
     * @param view    要显示的控件
     * @param url     图片链接
     * @param id      服务器没有图片，显示默认的图片id
     */
    public static void showCircleBorderImage(Context context, ImageView view, String url, int id, int borderWidth, int borderColor) {
        Glide.with(context).load(url)
                .transform(new GlideCircleTransform(context, borderWidth, borderColor))
                .placeholder(id)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    /**
     * 从服务器或本地获取图片(圆角)
     *
     * @param context context
     * @param view    要显示的控件
     * @param url     图片链接
     * @param id      服务器没有图片，显示默认的图片id
     * @param radius  角度
     */
    public static void showRoundImage(Context context, ImageView view, String url, int id, int radius) {
        Glide.with(context).load(url)
                .transform(new CenterCrop(context), new GlideRoundTransform(context, radius))
                .placeholder(id)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

    /**
     * 拨打手机电话
     *
     * @param context context
     * @param phone   phone
     */
    public static void callPhone(Context context, String phone) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    /**
     * 调起系统发短信功能
     *
     * @param context     context
     * @param phoneNumber phoneNumber
     * @param message     message
     */
    public static void doSendSMSTo(Context context, String phoneNumber, String message) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", message);
            context.startActivity(intent);
        }
    }

    /**
     * 手机震动提示
     */
    public static void mobileShake(Context context, int ms) {
        Object obj = context.getSystemService(Service.VIBRATOR_SERVICE);
        if (obj instanceof Vibrator) {
            ((Vibrator) obj).vibrate(ms);
        }
    }

    /**
     * 保持窗口常亮
     *
     * @param activity activity
     */
    public static void saveWindowLight(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
}
