package com.voicemod.videorecorder.data.source

import com.voicemod.videorecorder.data.entities.VideoEntity

interface VideoLocalDataSource {

    @Throws(Exception::class)
    fun save(videoEntity: VideoEntity)

    @Throws(Exception::class)
    fun saveAll(videoEntityList: List<VideoEntity>)

    fun get(videoId: Int): VideoEntity?

    fun getAll(): List<VideoEntity>

    fun remove(videoEntity: VideoEntity)

    fun removeAll()

}
