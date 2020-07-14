package com.voicemod.videorecorder.domain.mappers

import com.voicemod.videorecorder.data.entities.VideoEntity
import com.voicemod.videorecorder.data.mappers.Mapper
import com.voicemod.videorecorder.view.adapter.LibraryItem

class VideoEntityLibraryItemMapper : Mapper<VideoEntity, LibraryItem>() {

    override fun mapFrom(from: VideoEntity): LibraryItem {
        return LibraryItem.Video(
            uuid = from.id,
            title = from.name,
            path = from.path,
            thumbnail = from.thumbnail
        )
    }
}