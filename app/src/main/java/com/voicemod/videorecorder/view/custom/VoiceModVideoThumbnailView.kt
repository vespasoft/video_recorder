package com.voicemod.videorecorder.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.voicemod.videorecorder.R
import kotlinx.android.synthetic.main.voicemod_custom_video_thumbnail.view.*

class VoiceModVideoThumbnailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private var view = View.inflate(context, R.layout.voicemod_custom_video_thumbnail, this)
    private val mVideoView: ImageView
    private val mPlayVideo: ImageView

    init {
        mVideoView = view.mVideoView
        mPlayVideo = view.mPlayVideo
    }

    private fun initializeViews(onPlayVideoClickListener: () -> Unit) {
        mVideoView.visibility = View.VISIBLE
        mPlayVideo.visibility = View.VISIBLE
        mPlayVideo.setOnClickListener {
            onPlayVideoClickListener()
        }
    }

    fun setVideoThumbnail(videoPath: String?, onPlayVideoClickListener: () -> Unit) {
        videoPath?.let {
            Glide.with(context)
                .load(videoPath)
                .override(300, 250)// Example
                .into(mVideoView)
        }
        initializeViews(onPlayVideoClickListener)
    }

}

