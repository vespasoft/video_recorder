package com.voicemod.videorecorder.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.voicemod.videorecorder.common.SingleLiveEvent
import com.voicemod.videorecorder.data.mappers.Mapper
import com.voicemod.videorecorder.data.entities.VideoEntity
import com.voicemod.videorecorder.domain.usecases.DeleteVideoUseCase
import com.voicemod.videorecorder.domain.usecases.GetLibraryItemsUseCase
import com.voicemod.videorecorder.presentation.entities.VideoLibraryViewState
import com.voicemod.videorecorder.view.adapter.LibraryItem
import io.reactivex.disposables.Disposable

class VideoLibraryViewModel constructor(
    private val getLibraryItems: GetLibraryItemsUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val videoEntityLibraryItemMapper: Mapper<VideoEntity, LibraryItem>
) : BaseViewModel() {
    var viewState: MutableLiveData<VideoLibraryViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<Throwable?> = SingleLiveEvent()
    init {
        viewState.value = VideoLibraryViewState()
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    override fun getViewStatus() {
        getLibraryItemsUseCase()
    }

    private fun getLibraryItemsUseCase() {
        addDisposable(
            observerGetLibraryItems()
        )
    }

    fun deleteVideo(uuid: String, path: String) {
        addDisposable(
            deleteVideoUseCase.delete(
                VideoEntity(
                    id = uuid,
                    path = path
                ))
                .subscribe ({ completed ->
                    if (completed) {
                        observerGetLibraryItems()
                    }
                }, {
                    throwError(it)
                })
        )
    }

    private fun observerGetLibraryItems(): Disposable {
        return getLibraryItems.observable()
            .flatMap { videoEntityLibraryItemMapper.observable(it) }
            .subscribe ({ libraryItems ->
                viewState.value?.let {
                    this.viewState.value = this.viewState.value?.copy(showLoading = false, itemList = libraryItems)
                    this.errorState.value = null
                }
            }, {
                viewState.value = viewState.value?.copy(showLoading = false)
                errorState.value = it
            })
    }

    private fun throwError(error: Throwable?) {
        viewState.value = viewState.value?.copy(showLoading = false)
        errorState.value = error
    }

}