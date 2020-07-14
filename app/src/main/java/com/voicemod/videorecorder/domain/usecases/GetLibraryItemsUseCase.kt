package com.voicemod.videorecorder.domain.usecases

import com.voicemod.videorecorder.domain.VideoLibraryRepository
import com.voicemod.videorecorder.common.Transformer
import com.voicemod.videorecorder.data.entities.VideoEntity
import io.reactivex.Observable

class GetLibraryItemsUseCase(
    transformer: Transformer<List<VideoEntity>>,
    private val videoLibraryRepository: VideoLibraryRepository
): UseCase<List<VideoEntity>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<List<VideoEntity>> {
        return videoLibraryRepository.getAll()
    }

}