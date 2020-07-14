package com.voicemod.videorecorder.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.voicemod.videorecorder.R
import com.voicemod.videorecorder.presentation.entities.VideoLibraryViewState
import com.voicemod.videorecorder.presentation.viewmodel.VideoLibraryVMFactory
import com.voicemod.videorecorder.presentation.viewmodel.VideoLibraryViewModel
import com.voicemod.videorecorder.view.VideoPlayerActivity
import com.voicemod.videorecorder.view.adapter.LibraryItem
import com.voicemod.videorecorder.view.adapter.VMLibraryAdapter
import com.voicemod.videorecorder.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_video_library.*
import org.koin.android.ext.android.inject

class VideoLibraryFragment : BaseFragment() {

    val videoLibraryFactory: VideoLibraryVMFactory by inject()

    lateinit var viewModel: VideoLibraryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.getViewStatus()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_library, container, false)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getViewStatus()
    }

    override fun setUpViews() {

    }

    override fun observeViewState() {
        viewModel.text.observe(viewLifecycleOwner, Observer {
            titleTextView.text = it
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let {
                handleLibraryRecyclerView(it)
            }
        })
        viewModel.errorState.observe(viewLifecycleOwner, Observer { throwable ->
            throwable?.let {
                Toast.makeText(activity, throwable.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun handleLibraryRecyclerView(state: VideoLibraryViewState) {
        libraryRecyclerView?.let { recyclerView ->
            recyclerView.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            state.itemList?.let { libraryItems ->
                libraryRecyclerView.adapter = VMLibraryAdapter(
                    items = libraryItems,
                    onClickListener = { videoPath ->
                        showFullscreenVideoActivity(videoPath)
                    },
                    onDeleteClickListener = { item ->
                        val videoItem = item as LibraryItem.Video
                        videoItem.path?.let {
                            videoItem.uuid?.let {
                                viewModel.deleteVideo(
                                    uuid = videoItem.uuid,
                                    path = videoItem.path
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, videoLibraryFactory).get(VideoLibraryViewModel::class.java)
    }

    private fun showFullscreenVideoActivity(videoPath: String) = context?.let {
        VideoPlayerActivity.startActivity(it, videoPath)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        fun newInstance(): VideoLibraryFragment {
            val fragment = VideoLibraryFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}