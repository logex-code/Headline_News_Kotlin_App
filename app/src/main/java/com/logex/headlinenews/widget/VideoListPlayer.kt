package com.logex.headlinenews.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.logex.headlinenews.R
import com.logex.headlinenews.base.HttpFactory
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.model.HttpResult
import com.logex.headlinenews.model.VideoPathEntity
import com.logex.utils.AutoUtils
import com.logex.utils.LogUtil
import com.logex.utils.UIUtils
import com.logex.videoplayer.JCVideoPlayerStandard
import com.logex.widget.DividerLine
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import java.util.*
import java.util.zip.CRC32

/**
 * 创建人: liguangxi
 * 时间: 18-8-20
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
class VideoListPlayer(context: Context, attrs: AttributeSet?) : JCVideoPlayerStandard(context, attrs) {
    var videoId: String? = null

    private var ivVideoThumbnail: ImageView? = null
    private var dlBg: DividerLine? = null
    private var tvTitle: TextView? = null
    private var tvPlayCount: TextView? = null
    private var tvVideoDuration: TextView? = null

    override fun init(context: Context) {
        super.init(context)
        // 添加缩略图控件
        ivVideoThumbnail = ImageView(context)
        ivVideoThumbnail?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        addView(ivVideoThumbnail, 0)

        // 添加dl
        dlBg = DividerLine(context)
        dlBg?.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        dlBg?.setBackgroundResource(R.drawable.thr_shadow_video)
        addView(dlBg, 1)

        // 添加标题和播放次数
        val llVideoTop = LinearLayout(context)
        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(44, 44, 44, 0)
        llVideoTop.layoutParams = layoutParams
        llVideoTop.orientation = LinearLayout.VERTICAL

        tvTitle = TextView(context)
        tvTitle?.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        tvTitle?.setTextColor(resources.getColor(R.color.white))
        tvTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, 42.0f)
        tvTitle?.maxLines = 2
        tvTitle?.setLineSpacing(0f, 1.2f)
        llVideoTop.addView(tvTitle)

        tvPlayCount = TextView(context)
        val tvPlayCountLp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        tvPlayCountLp.topMargin = 12
        tvPlayCount?.layoutParams = tvPlayCountLp
        tvPlayCount?.setTextColor(resources.getColor(R.color.video_list_play_count))
        tvPlayCount?.setTextSize(TypedValue.COMPLEX_UNIT_PX, 32.0f)
        llVideoTop.addView(tvPlayCount)

        addView(llVideoTop)

        // 添加时长控件
        tvVideoDuration = TextView(context)
        val tvVideoDurationLp = FrameLayout.LayoutParams(134, 64)
        tvVideoDurationLp.rightMargin = 24
        tvVideoDurationLp.bottomMargin = 24
        tvVideoDurationLp.gravity = Gravity.END or Gravity.BOTTOM
        tvVideoDuration?.layoutParams = tvVideoDurationLp
        tvVideoDuration?.setBackgroundResource(R.drawable.bg_video_list_duration)
        tvVideoDuration?.setTextColor(resources.getColor(R.color.white))
        tvVideoDuration?.setTextSize(TypedValue.COMPLEX_UNIT_PX, 28.0f)
        tvVideoDuration?.gravity = Gravity.CENTER
        addView(tvVideoDuration)

        val playCompletePanel = findViewById(R.id.ll_video_play_complete_panel)
        if (playCompletePanel != null) {
            removeView(playCompletePanel)
        }
    }

    fun showVideoThumbnail(imageUrl: String?): VideoListPlayer {
        UIUtils.showImgFromUrl(context, ivVideoThumbnail, imageUrl, -1)
        return this
    }

    fun showVideoTitle(title: String?): VideoListPlayer {
        tvTitle?.text = title
        return this
    }

    fun showVideoPlayCount(playCount: String?): VideoListPlayer {
        tvPlayCount?.text = playCount
        return this
    }

    fun showVideoDuration(duration: String?): VideoListPlayer {
        tvVideoDuration?.text = duration
        return this
    }

    override fun onClickStart() {
        if (url != null && url.isNotEmpty()) {
            super.onClickStart()
            return
        }
        releaseAllVideos()
        changeUiToPreparingShow()

        // 解析视频地址
        val r = Random().nextInt(Int.MAX_VALUE)
        val url = "/video/urls/v/1/toutiao/mp4/$videoId?r=$r"
        val crc32 = CRC32()
        crc32.update(url.toByteArray())
        var c = crc32.value
        // crc32 可能为负数，要保证其为正数
        if (c < 0) c += 0x100000000
        val videoPath = "http://i.snssdk.com$url&s=$c"

        HttpFactory.create()?.getVideoPath(videoPath)
                ?.compose(RxSchedulers.io_main())
                ?.map(Function<HttpResult<VideoPathEntity>, String> { t ->
                    val data = t.data
                    if (data?.video_list?.video_1?.main_url != null) {
                        val base64 = data.video_list.video_1.main_url
                        return@Function String(Base64.decode(base64.toByteArray(), Base64.DEFAULT))
                    }
                    ""
                })
                ?.safeSubscribe(object : Observer<String> {
                    override fun onComplete() = Unit

                    override fun onSubscribe(d: Disposable?) = Unit

                    override fun onNext(realUrl: String) {
                        LogUtil.i("解码后地址>>>" + realUrl)

                        this@VideoListPlayer.url = realUrl
                        this@VideoListPlayer.onClickStart()
                    }

                    override fun onError(e: Throwable?) {
                        e?.fillInStackTrace()
                    }

                })
    }

    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        ivVideoThumbnail?.visibility = GONE
        dlBg?.visibility = GONE
        tvTitle?.visibility = GONE
        tvPlayCount?.visibility = GONE
        tvVideoDuration?.visibility = GONE
    }

    override fun changeUiToPlayingToggle() {
        super.changeUiToPlayingToggle()
        dlBg?.visibility = VISIBLE
        tvTitle?.visibility = VISIBLE
        tvPlayCount?.visibility = VISIBLE
    }

    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
        dlBg?.visibility = VISIBLE
        tvTitle?.visibility = VISIBLE
        tvPlayCount?.visibility = VISIBLE
    }

    override fun changeUiToPauseToggle() {
        super.changeUiToPauseToggle()
        dlBg?.visibility = GONE
        tvTitle?.visibility = GONE
        tvPlayCount?.visibility = GONE
    }

    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        ivVideoThumbnail?.visibility = VISIBLE
        dlBg?.visibility = VISIBLE
        tvTitle?.visibility = VISIBLE
        tvPlayCount?.visibility = GONE

        // 添加2个按钮
        val view: LinearLayout = UIUtils.getXmlView(context, R.layout.layout_video_player_complete_panel_view) as LinearLayout
        view.gravity = Gravity.CENTER_VERTICAL
        AutoUtils.auto(view)
        addView(view)
    }

    override fun autoDismissControlView() {
        super.autoDismissControlView()
        dlBg?.visibility = GONE
        tvTitle?.visibility = GONE
        tvPlayCount?.visibility = GONE
    }
}