package com.voicemod.videorecorder.view.adapter.view_holders

import android.view.View
import com.voicemod.videorecorder.view.adapter.LibraryItem
import com.voicemod.videorecorder.view.adapter.VMLibraryAdapter
import kotlinx.android.synthetic.main.library_video_item.view.*
import kotlinx.android.synthetic.main.voicemod_custom_video_thumbnail.view.*


class VideoViewHolder(
    view: View,
    private val onClickListener: (String) -> Unit,
    private val onDeleteClickListener: (LibraryItem) -> Unit
) : VMLibraryAdapter.ViewHolder(view) {

    private val titleTextView = view.titleTextView
    private val videoThumbnailView = view.mVoiceModVideoThumbnailView
    private val deleteView = view.mDeleteVideo

    override fun bind(item: LibraryItem) {
        super.bind(item)

        item as LibraryItem.Video
        titleTextView?.text = item.title
        videoThumbnailView.setVideoThumbnail(
            videoPath = item.path,
            onPlayVideoClickListener = {
                item.path?.let { onClickListener(it) }
            }
        )
        deleteView.setOnClickListener {
            onDeleteClickListener(item)
        }
    }
}