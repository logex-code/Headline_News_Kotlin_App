package com.logex.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.logex.common.R;

/**
 * app标题栏
 */
public class AppTitleBar extends RelativeLayout {
    protected RelativeLayout rlTitle;
    protected LinearLayout llTitleLeft;
    protected ImageView ivLeftImage;
    protected RelativeLayout rlTitleRight;
    protected ImageView ivRightImage, ivRightImage2;
    protected TextView tvTitle, tvLeftTitle, tvRightTitle;
    protected DividerLine dlLine;

    public AppTitleBar(Context context) {
        this(context, null);
    }

    public AppTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppTitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.layout_widget_title_bar, this);
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title_bar);
        llTitleLeft = (LinearLayout) findViewById(R.id.ll_title_left);
        ivLeftImage = (ImageView) findViewById(R.id.iv_title_left_image);
        tvLeftTitle = (TextView) findViewById(R.id.tv_left_title);
        tvRightTitle = (TextView) findViewById(R.id.tv_right_title);
        rlTitleRight = (RelativeLayout) findViewById(R.id.rl_title_right);
        ivRightImage = (ImageView) findViewById(R.id.iv_right_image);
        ivRightImage2 = (ImageView) findViewById(R.id.iv_right_image2);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        dlLine = (DividerLine) findViewById(R.id.dv_line);
        parseStyle(context, attrs);
    }

    private void parseStyle(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AppTitleBar);
        Drawable background = ta.getDrawable(R.styleable.AppTitleBar_titleBarBackground);
        if (null != background) {
            rlTitle.setBackgroundDrawable(background);
        }

        String title = ta.getString(R.styleable.AppTitleBar_titleBarTitle);
        tvTitle.setText(title);

        int titleTextColor = ta.getColor(R.styleable.AppTitleBar_titleBarTitleColor,
                getResources().getColor(R.color.title_text_color));
        tvTitle.setTextColor(titleTextColor);

        String leftTitleText = ta.getString(R.styleable.AppTitleBar_titleBarLeftTitle);
        tvLeftTitle.setText(leftTitleText);

        int leftTitleTextColor = ta.getColor(R.styleable.AppTitleBar_titleBarLeftTitleColor,
                getResources().getColor(R.color.title_text_color));
        tvLeftTitle.setTextColor(leftTitleTextColor);

        String rightTitleText = ta.getString(R.styleable.AppTitleBar_titleBarRightTitle);
        tvRightTitle.setText(rightTitleText);

        int rightTitleTextColor = ta.getColor(R.styleable.AppTitleBar_titleBarRightTitleColor,
                getResources().getColor(R.color.title_text_color));
        tvRightTitle.setTextColor(rightTitleTextColor);

        Drawable leftDrawable = ta.getDrawable(R.styleable.AppTitleBar_titleBarLeftImage);
        if (null != leftDrawable) {
            ivLeftImage.setImageDrawable(leftDrawable);
        }
        Drawable rightDrawable = ta.getDrawable(R.styleable.AppTitleBar_titleBarRightImage);
        if (null != rightDrawable) {
            ivRightImage.setImageDrawable(rightDrawable);
        }

        Drawable rightDrawable2 = ta.getDrawable(R.styleable.AppTitleBar_titleBarRightImage2);
        if (null != rightDrawable2) {
            ivRightImage2.setImageDrawable(rightDrawable2);
        }

        boolean isLeftClick = ta.getBoolean(R.styleable.AppTitleBar_titleBarLeftIsClick, true);
        llTitleLeft.setEnabled(isLeftClick);

        boolean isShowLine = ta.getBoolean(R.styleable.AppTitleBar_titleBarLineIsShow, true);
        dlLine.setVisibility(isShowLine ? View.VISIBLE : View.GONE);

        ta.recycle();
    }

    /**
     * 设置标题文字颜色
     *
     * @param color color
     */
    public void setTitleTextColor(int color) {
        tvTitle.setTextColor(getResources().getColor(color));
    }

    public void setLeftImageResource(int resId) {
        ivLeftImage.setImageResource(resId);
    }

    public void setRightImageResource(int resId) {
        ivRightImage.setImageResource(resId);
    }

    public void setRightImage2Resource(int resId) {
        ivRightImage2.setImageResource(resId);
    }

    public void setRightImageClickListener(OnClickListener listener) {
        ivRightImage.setOnClickListener(listener);
    }

    public void setRightImage2ClickListener(OnClickListener listener) {
        ivRightImage2.setOnClickListener(listener);
    }

    public void setLeftLayoutClickListener(OnClickListener listener) {
        llTitleLeft.setOnClickListener(listener);
    }

    public void setRightLayoutClickListener(OnClickListener listener) {
        rlTitleRight.setOnClickListener(listener);
    }

    public void setLeftTitleClickListener(OnClickListener listener) {
        tvLeftTitle.setOnClickListener(listener);
    }

    public void setRightTitleClickListener(OnClickListener listener) {
        tvRightTitle.setOnClickListener(listener);
    }

    public void setTitleClickListener(OnClickListener listener) {
        tvTitle.setOnClickListener(listener);
    }

    public void setLeftLayoutVisibility(int visibility) {
        llTitleLeft.setVisibility(visibility);
    }

    public void setRightLayoutVisibility(int visibility) {
        rlTitleRight.setVisibility(visibility);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setLeftTitle(String title) {
        tvLeftTitle.setText(title);
    }

    public void setRightTitle(String title) {
        tvRightTitle.setText(title);
    }

    public void setRightTitleColor(@ColorRes int color) {
        tvRightTitle.setTextColor(getResources().getColor(color));
    }

    public void setRightImage2Visibility(boolean flag) {
        ivRightImage2.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    public void setBackgroundColor(int color) {
        rlTitle.setBackgroundColor(color);
    }

    public LinearLayout getLeftLayout() {
        return llTitleLeft;
    }

    public RelativeLayout getRightLayout() {
        return rlTitleRight;
    }

    public TextView getRightTitle() {
        return tvRightTitle;
    }
}
