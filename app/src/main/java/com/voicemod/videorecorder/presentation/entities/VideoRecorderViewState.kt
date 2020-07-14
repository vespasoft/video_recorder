package com.voicemod.videorecorder.presentation.entities

import com.voicemod.videorecorder.data.entities.VideoEntity

data class VideoRecorderViewState(
    var showLoading: Boolean = true,
    var videoEntity: VideoEntity? = null
)