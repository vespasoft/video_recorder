package com.voicemod.videorecorder.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import com.voicemod.videorecorder.R
import com.voicemod.videorecorder.view.common.extension.parseVideo
import kotlinx.android.synthetic.main.voicemod_custom_video_view.view.*

class VoiceModVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private var view = View.inflate(context, R.layout.voicemod_custom_video_view, this)
    private val mVideoView: VideoView
    private val mPlayVideo: ImageView

    init {
        mVideoView = view.mVideoView
        mPlayVideo = view.mPlayVideo
    }

    private fun initializeViews() {
        mVideoView.visibility = View.VISIBLE
        mPlayVideo.visibility = View.VISIBLE
        mPlayVideo.setOnClickListener {
            startPlayVideo()
        }
    }

    fun resetPlayer() {
        mVideoView.visibility = View.GONE
        mPlayVideo.visibility = View.GONE
    }

    fun onVideoCompletionListener(listener: () -> Unit) {
        mVideoView.setOnCompletionListener {
            listener()
        }
    }

    fun setMediaFile(outputFilePath: String, autoPlay: Boolean = false) {
        // Set media controller
        mVideoView.setMediaController(MediaController(context))
        mVideoView.requestFocus()
        mVideoView.setVideoPath(outputFilePath)
        mVideoView.seekTo(100)
        initializeViews()
        if (autoPlay) {
            startPlayVideo()
        }
    }

    private fun startPlayVideo() {
        mVideoView.start()
        mPlayVideo.visibility = View.GONE
    }

}

