package com.voicemod.videorecorder.domain.usecases

import com.voicemod.videorecorder.domain.VideoLibraryRepository
import com.voicemod.videorecorder.common.Transformer
import com.voicemod.videorecorder.data.entities.VideoEntity
import io.reactivex.Observable

class CreateFileUseCase(
    transformer: Transformer<VideoEntity>,
    private val videoLibraryRepository: VideoLibraryRepository
): UseCase<VideoEntity>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<VideoEntity> {
        return videoLibraryRepository.createFile()
    }

}