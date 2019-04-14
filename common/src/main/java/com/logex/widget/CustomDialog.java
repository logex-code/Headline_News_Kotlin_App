package com.logex.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.logex.utils.AutoUtils;

/**
 * 创建人: Administrator
 * 日期: 2018/9/29
 * 邮箱: 15679158128@163.com
 * 版本: 1.0
 * 定制Dialog基类
 */
public abstract class CustomDialog {
    protected Context context;
    protected View view;
    protected Dialog dialog;

    public CustomDialog(Context context) {
        this.context = context;
        createView();
    }

    private CustomDialog createView() {
        // 获取Dialog布局
        view = LayoutInflater.from(context).inflate(getLayoutId(), null);
        AutoUtils.auto(view);
        return this;
    }

    public CustomDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public CustomDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public CustomDialog setOnDismissListener(final DialogInterface.OnDismissListener listener) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null) listener.onDismiss(dialog);
            }
        });
        return this;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void show() {
        dialog.show();
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    /**
     * 得到布局
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();
}
