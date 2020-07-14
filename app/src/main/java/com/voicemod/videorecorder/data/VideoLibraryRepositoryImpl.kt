package com.voicemod.videorecorder.data

import com.voicemod.videorecorder.common.Constants.TIMESTAMP_PATTERN
import com.voicemod.videorecorder.data.entities.VideoEntity
import com.voicemod.videorecorder.data.mappers.FromFileToVideoEntityMapper
import com.voicemod.videorecorder.data.source.FileManager
import com.voicemod.videorecorder.data.source.VideoLocalDataSource
import com.voicemod.videorecorder.domain.VideoLibraryRepository
import com.voicemod.videorecorder.domain.exceptions.FileException
import io.reactivex.Observable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class VideoLibraryRepositoryImpl (
    private val fileManager: FileManager,
    private val videoLocalDataSource: VideoLocalDataSource,
    private val fromFileToVideoEntityMapper: FromFileToVideoEntityMapper
) : VideoLibraryRepository {

    override fun createFile(): Observable<VideoEntity> {
        val uuid = SimpleDateFormat(TIMESTAMP_PATTERN, Locale.getDefault()).format(Date())
        val file = fileManager.createFile(uuid)
        return fromFileToVideoEntityMapper.observable(file)
    }

    override fun save(videoEntity: VideoEntity) : Observable<Any> {
        return Observable.just(
            videoLocalDataSource.save(videoEntity)
        )
    }

    override fun saveAll(videoEntities: List<VideoEntity>): Observable<Any> {
        return Observable.just(
            videoLocalDataSource.saveAll(videoEntities)
        )
    }

    override fun remove(videoEntity: VideoEntity): Observable<Any> {
        return try {
            fileManager.deleteFile(videoEntity.path)
            videoLocalDataSource.remove(videoEntity)
            Observable.just(true)
        } catch (e: Exception) {
            throw FileException("Error deleting the file")
        }
    }

    override fun get(videoId: Int): Observable<VideoEntity?> {
        return Observable.just(
            videoLocalDataSource.get(videoId)?.apply {
                thumbnail = fileManager.getVideoThumbnail(path)
            }
        )
    }

    override fun getAll(): Observable<List<VideoEntity>> {
        return Observable.just(
            videoLocalDataSource.getAll()
        )
    }

}