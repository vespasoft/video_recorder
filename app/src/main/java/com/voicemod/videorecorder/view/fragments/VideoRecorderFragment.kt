package com.voicemod.videorecorder.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.voicemod.videorecorder.R
import com.voicemod.videorecorder.data.entities.VideoEntity
import com.voicemod.videorecorder.presentation.viewmodel.VideoRecorderVMFactory
import com.voicemod.videorecorder.presentation.viewmodel.VideoRecorderViewModel
import com.voicemod.videorecorder.view.CameraActivity
import com.voicemod.videorecorder.view.CameraActivity.Companion.UI_ANIMATION_DELAY
import com.voicemod.videorecorder.view.common.CameraFragment
import kotlinx.android.synthetic.main.fragment_video_recorder.*
import org.koin.android.ext.android.inject

class VideoRecorderFragment : CameraFragment() {

    val factory: VideoRecorderVMFactory by inject()
    lateinit var viewModel: VideoRecorderViewModel

    private lateinit var currentVideoEntity: VideoEntity
    private var mOutputFilePath: String? = null

    private lateinit var cameraActivity : CameraActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.getViewStatus()
        }
        cameraActivity = activity as CameraActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_recorder, container, false)
    }

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, factory).get(VideoRecorderViewModel::class.java)
    }

    override fun setUpViews() {
        mRecordVideo.setOnClickListener {
            if (mIsRecordingVideo) {
                try {
                    stopRecordingVideo()
                    mRecordVideo.visibility = View.GONE
                    hiddenTextureView()
                    configMediaFile()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                mOutputFilePath?.let {
                    cameraActivity.delayedHide(UI_ANIMATION_DELAY)
                    startRecordingVideo(it)
                    mRecordVideo.setImageResource(R.drawable.ic_stop)
                }
            }
        }
        mSaveImageView.setOnClickListener {
            mOutputFilePath?.let {
                viewModel.saveVideo(currentVideoEntity)
                openVideoCamera()
                mRecordVideo.visibility = View.VISIBLE
                cameraActivity.showToolbar()
            }
        }
        mCancelImageView.setOnClickListener {
            openVideoCamera()
            mRecordVideo.visibility = View.VISIBLE
            cameraActivity.showToolbar()
            viewModel.deleteVideo(currentVideoEntity)
        }
    }

    override fun observeViewState() {
        this.viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.videoEntity?.let {
                currentVideoEntity = it
                mOutputFilePath = it.path
            }
        })
        viewModel.errorState.observe(viewLifecycleOwner, Observer { throwable ->
            throwable?.let {
                Toast.makeText(activity, throwable.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun configMediaFile() {
        mOutputFilePath?.let {
            mVoiceModVideoView.setMediaFile(
                outputFilePath = it
            )
            mVoiceModVideoView.onVideoCompletionListener {
                mSaveImageView.visibility = View.VISIBLE
                mCancelImageView.visibility = View.VISIBLE
            }
            mSaveImageView.visibility = View.VISIBLE
            mCancelImageView.visibility = View.VISIBLE
        }

    }

    private fun openVideoCamera() {
        mVoiceModVideoView.resetPlayer()
        mRecordVideo.setImageResource(R.drawable.ic_record)
        mSaveImageView.visibility = View.GONE
        mCancelImageView.visibility = View.GONE
        enableTextureViewAndOpenCamera()
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        fun newInstance(): VideoRecorderFragment {
            val fragment = VideoRecorderFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}