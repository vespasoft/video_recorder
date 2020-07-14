package com.voicemod.videorecorder.domain.usecases

import com.voicemod.videorecorder.domain.VideoLibraryRepository
import com.voicemod.videorecorder.common.Transformer
import com.voicemod.videorecorder.data.entities.VideoEntity
import io.reactivex.Observable
import java.lang.IllegalArgumentException

class DeleteVideoUseCase(
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
                videoLibraryRepository.remove(entity)
                return@fromCallable true
            }
        }?: return Observable.error { IllegalArgumentException("VideoEntity must be provided.") }
    }

    fun delete(videoEntity: VideoEntity): Observable<Boolean> {
        val data = HashMap<String, VideoEntity>()
        data[PARAM_VIDEO_ENTITY] = videoEntity
        return observable(data)
    }

}