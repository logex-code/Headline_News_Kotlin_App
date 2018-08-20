package com.logex.headlinenews.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Base64
import com.logex.headlinenews.base.HttpFactory
import com.logex.headlinenews.base.RxSchedulers
import com.logex.headlinenews.model.HttpResult
import com.logex.headlinenews.model.VideoPathEntity
import com.logex.utils.LogUtil
import com.logex.videoplayer.JCVideoPlayerStandard
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
class VideoListPlayer(context: Context?, attrs: AttributeSet?) : JCVideoPlayerStandard(context, attrs) {
    var videoId: String? = null

    override fun onClickStart() {
        if (url != null && url.isNotEmpty()) {
            super.onClickStart()
            return
        }
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
}