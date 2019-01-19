package com.logex.headlinenews.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.logex.headlinenews.R


/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 主页面底部tab
 */
class MainTabView : LinearLayout {
    private var tabImage: ImageView? = null
    private var tabText: TextView? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, paramInt: Int) : super(context, attrs, paramInt) {
        init(context, attrs)
    }

    /**
     * 初始化view
     *
     * @param context context
     * @param attrs   attrs
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.layout_main_tab_view, this)
        tabImage = findViewById<ImageView>(R.id.iv_tab_image)
        tabText = findViewById<TextView>(R.id.tv_tab_text)
        parseStyle(context, attrs)
    }

    private fun parseStyle(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.MainTabView)
            val tabDrawable = ta.getDrawable(R.styleable.MainTabView_tabImage)
            if (tabDrawable != null) {
                tabImage?.setImageDrawable(tabDrawable)
            }
            val tabTitle = ta.getString(R.styleable.MainTabView_tabTitle)
            tabText?.text = tabTitle
            val titleColor = ta.getColorStateList(R.styleable.MainTabView_tabTitleColor)
            if (titleColor != null) {
                tabText?.setTextColor(titleColor)
            }
            val flag = ta.getBoolean(R.styleable.MainTabView_isShowTabTitle, true)
            if (flag) {
                tabText?.visibility = View.VISIBLE
            } else {
                tabText?.visibility = View.GONE
            }
            ta.recycle()
        }
    }

    /**
     * 设置图片
     *
     * @param drawable drawable
     */
    fun setTabImageResource(drawable: Drawable) {
        tabImage?.setImageDrawable(drawable)
    }

    /**
     * 设置标题
     *
     * @param title title
     */
    fun setTabText(title: String) {
        tabText?.text = title
    }

    fun setTabTextColor(color: ColorStateList) {
        tabText?.setTextColor(color)
    }
}