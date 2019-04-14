package com.logex.headlinenews.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Base64
import android.view.View
import com.logex.headlinenews.R
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.http.HttpFactory
import com.logex.headlinenews.model.HttpResult
import com.logex.headlinenews.model.VideoPathEntity
import com.logex.utils.LogUtil
import com.logex.utils.UIUtils
import com.logex.videoplayer.JCVideoPlayerStandard
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.video_list_plyer.view.*
import java.util.*
import java.util.zip.CRC32

/**
 * 创建人: liguangxi
 * 时间: 18-8-20
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
class VideoListPlayer(context: Context, attrs: AttributeSet?) : JCVideoPlayerStandard(context, attrs) {
    var videoId: String? = null // 视频id

    override fun getLayoutId(): Int {
        return R.layout.video_list_plyer
    }

    fun showVideoThumbnail(imageUrl: String?): VideoListPlayer {
        UIUtils.showImgFromUrl(context, ivVideoThumbnail, imageUrl,
                R.drawable.list_item_place_photo)
        return this
    }

    fun showVideoPlayCount(playCount: String?): VideoListPlayer {
        tv_play_count?.text = playCount
        return this
    }

    fun showVideoDuration(duration: String?): VideoListPlayer {
        tv_video_duration?.text = duration
        return this
    }

    override fun onClick(v: View) {
        if (url == null || url.isEmpty()) {
            // 解析视频地址
            changeUiToPreparingShow()

            val r = Random().nextInt(Int.MAX_VALUE)
            val oldUrl = "/video/urls/v/1/toutiao/mp4/$videoId?r=$r"
            val crc32 = CRC32()
            crc32.update(oldUrl.toByteArray())
            var c = crc32.value
            // crc32 可能为负数，要保证其为正数
            if (c < 0) c += 0x100000000
            val videoPath = "http://i.snssdk.com$oldUrl&s=$c"

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
                        override fun onComplete() {

                        }

                        override fun onSubscribe(d: Disposable?) {

                        }

                        override fun onNext(realUrl: String) {
                            LogUtil.i("解码后地址>>>$realUrl")

                            url = realUrl
                            onClickStart()
                        }

                        override fun onError(e: Throwable?) {
                            e?.fillInStackTrace()
                            UIUtils.showToast(context, e?.message)
                        }

                    })
        } else {
            super.onClick(v)
        }
    }

    override fun changeUiToNormalShow() {
        super.changeUiToNormalShow()
        dl_bg?.visibility = VISIBLE
        tv_play_count?.visibility = VISIBLE
        tv_video_duration?.visibility = VISIBLE
    }

    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        dl_bg?.visibility = GONE
        tv_play_count?.visibility = GONE
        tv_video_duration?.visibility = GONE
    }

    override fun changeUiToPlayingToggle() {
        super.changeUiToPlayingToggle()
        dl_bg?.visibility = VISIBLE
        tv_play_count?.visibility = VISIBLE
    }

    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
        dl_bg?.visibility = VISIBLE
        tv_play_count?.visibility = VISIBLE
    }

    override fun changeUiToPauseToggle() {
        super.changeUiToPauseToggle()
        dl_bg?.visibility = GONE
        tv_play_count?.visibility = GONE
    }

    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        dl_bg?.visibility = VISIBLE
        tv_play_count?.visibility = GONE
        ll_play_complete?.visibility = visibility
    }

    override fun autoDismissControlView() {
        super.autoDismissControlView()
        llTopContainer.visibility = GONE
        dl_bg?.visibility = GONE
        tv_play_count?.visibility = GONE
    }
}