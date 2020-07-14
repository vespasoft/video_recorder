package com.voicemod.videorecorder.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.voicemod.videorecorder.R
import kotlinx.android.synthetic.main.activity_video_player.*

const val EXTRA_VIDEO_PATH = "com.voicemod.videorecorder.VIDEOPATH"

class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_video_player)
        supportActionBar?.hide()

        intent?.let {
            it.extras?.getString(EXTRA_VIDEO_PATH)?.let { videoPath ->
                mVoiceModVideoView.setMediaFile(
                    outputFilePath = videoPath,
                    autoPlay = true
                )
                mVoiceModVideoView
            }
        }

        configViews()

    }

    private fun configViews() {
        mCloseImageView.setOnClickListener {
            super.onBackPressed()
        }
    }

    companion object {

        fun startActivity(context: Context, videoPath: String) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_PATH, videoPath)
            }
            context.startActivity(intent)
        }
    }

}
