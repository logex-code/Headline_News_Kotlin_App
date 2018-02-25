package com.logex.headlinenews.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.logex.headlinenews.R

/**
 * 创建人: liguangxi
 * 日期: 2018/2/22
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 * 启动页
 */
class SplashActivity : Activity() {
    private val handler = Handler()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler.postDelayed({
            // 打开主页面
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onBackPressed() {
        // 不重写该方法
    }
}