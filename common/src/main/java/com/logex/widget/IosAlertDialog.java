package com.logex.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.logex.common.R;
import com.logex.utils.ScreenUtils;

/**
 * 仿ios弹框
 */
public class IosAlertDialog extends CustomDialog {
    private TextView tvTitle;
    private TextView tvMessage;
    private Button btnNegative;
    private Button btnPositive;
    private DividerLine dlLine;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;

    public IosAlertDialog(Context context) {
        super(context);
    }

    public IosAlertDialog builder() {
        // 获取自定义Dialog布局中的控件
        tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);

        tvMessage = (TextView) view.findViewById(R.id.tv_dialog_message);
        tvMessage.setVisibility(View.GONE);

        btnNegative = (Button) view.findViewById(R.id.btn_dialog_negative);
        btnNegative.setVisibility(View.GONE);

        btnPositive = (Button) view.findViewById(R.id.btn_dialog_positive);
        btnPositive.setVisibility(View.GONE);

        dlLine = (DividerLine) view.findViewById(R.id.dl_line);
        dlLine.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams((int) (ScreenUtils.getScreenWidth(context) * 0.72f),
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return this;
    }

    public IosAlertDialog setTitle(String title) {
        showTitle = true;
        tvTitle.setText(title);
        return this;
    }

    public IosAlertDialog setMsg(String msg) {
        showMsg = true;
        tvMessage.setText(msg);
        return this;
    }

    public IosAlertDialog setPositiveButton(String text, final OnClickListener listener) {
        showPosBtn = true;
        btnPositive.setText(text);
        btnPositive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public IosAlertDialog setNegativeButton(String text, final OnClickListener listener) {
        showNegBtn = true;
        btnNegative.setText(text);
        btnNegative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    private void setLayout() {
        if (!showTitle && !showMsg) {
            tvTitle.setText("提示");
            tvTitle.setVisibility(View.VISIBLE);
        }
        if (showTitle) {
            tvTitle.setVisibility(View.VISIBLE);
        }
        if (showMsg) {
            tvMessage.setVisibility(View.VISIBLE);
        }
        if (!showPosBtn && !showNegBtn) {
            btnPositive.setText("确定");
            btnPositive.setVisibility(View.VISIBLE);
            btnPositive.setBackgroundResource(R.drawable.alertdialog_single_selector);
            btnPositive.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        if (showPosBtn && showNegBtn) {
            btnPositive.setVisibility(View.VISIBLE);
            btnPositive.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setBackgroundResource(R.drawable.alertdialog_left_selector);
            dlLine.setVisibility(View.VISIBLE);
        }
        if (showPosBtn && !showNegBtn) {
            btnPositive.setVisibility(View.VISIBLE);
            btnPositive.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
        if (!showPosBtn && showNegBtn) {
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setBackgroundResource(R.drawable.alertdialog_single_selector);
        }
    }

    @Override
    public void show() {
        setLayout();
        super.show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_alertdialog;
    }
}
