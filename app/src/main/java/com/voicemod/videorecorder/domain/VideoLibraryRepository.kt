package com.voicemod.videorecorder.domain

import com.voicemod.videorecorder.data.entities.VideoEntity
import io.reactivex.Observable

interface VideoLibraryRepository {
    fun createFile() : Observable<VideoEntity>
    fun save(videoEntity: VideoEntity): Observable<Any>
    fun saveAll(videoEntities: List<VideoEntity>): Observable<Any>
    fun getAll(): Observable<List<VideoEntity>>
    fun get(videoId: Int): Observable<VideoEntity?>
    fun remove(videoEntity: VideoEntity) : Observable<Any>
}