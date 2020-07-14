package com.voicemod.videorecorder.domain.usecases

import com.voicemod.videorecorder.domain.VideoLibraryRepository
import com.voicemod.videorecorder.common.Transformer
import com.voicemod.videorecorder.data.entities.VideoEntity
import io.reactivex.Observable
import java.lang.IllegalArgumentException

class SaveVideoUseCase(
    transformer: Transformer<Boolean>,
    private val videoLibraryRepository: VideoLibraryRepository
): UseCase<Boolean>(transformer) {

    companion object {
        private const val PARAM_VIDEO_ENTITY = "param:videoEntity"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Boolean> {
        data?.get(PARAM_VIDEO_ENTITY)?.let {
            return@createObservable Observable.fromCallable {
                val entity = it as VideoEntity
                videoLibraryRepository.save(entity)
                return@fromCallable true
            }
        }?: return Observable.error { IllegalArgumentException("VideoEntity must be provided.") }

    }

    fun save(videoEntity: VideoEntity): Observable<Boolean> {
        val data = HashMap<String, VideoEntity>()
        data[PARAM_VIDEO_ENTITY] = videoEntity
        return observable(data)
    }

}