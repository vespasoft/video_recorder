package com.voicemod.videorecorder.presentation.entities

import com.voicemod.videorecorder.view.adapter.LibraryItem

data class VideoLibraryViewState(
    var showLoading: Boolean = true,
    var itemList: List<LibraryItem>? = null
)