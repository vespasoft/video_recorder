package com.voicemod.videorecorder.data.entities

import android.graphics.Bitmap

data class VideoEntity (
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val path: String,
    val duration: Long = 0,
    val createdAt: String = "",
    var updatedAt: String = "",
    var thumbnail: Bitmap? = null
)