package com.voicemod.videorecorder.data.mappers

import com.voicemod.videorecorder.common.Constants
import com.voicemod.videorecorder.data.entities.VideoEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FromFileToVideoEntityMapper : Mapper<File, VideoEntity>() {

    override fun mapFrom(from: File): VideoEntity {
        val uuid = SimpleDateFormat(Constants.TIMESTAMP_PATTERN, Locale.getDefault()).format(Date())
        val createAt = SimpleDateFormat(Constants.DATE_TIME_PATTERN, Locale.getDefault()).format(
            Date()
        )
        return VideoEntity(
            id = uuid,
            name = uuid,
            path = from.absolutePath,
            createdAt = createAt
        )
    }
}