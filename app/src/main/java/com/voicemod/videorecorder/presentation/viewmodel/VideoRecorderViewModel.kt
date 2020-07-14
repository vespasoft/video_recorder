package com.voicemod.videorecorder.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.voicemod.videorecorder.common.SingleLiveEvent
import com.voicemod.videorecorder.data.entities.VideoEntity
import com.voicemod.videorecorder.domain.usecases.CreateFileUseCase
import com.voicemod.videorecorder.domain.usecases.DeleteVideoUseCase
import com.voicemod.videorecorder.domain.usecases.SaveVideoUseCase
import com.voicemod.videorecorder.presentation.entities.VideoRecorderViewState

class VideoRecorderViewModel(
    private val createFileUseCase: CreateFileUseCase,
    private val saveVideoUseCase: SaveVideoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase
) : BaseViewModel() {

    var viewState: MutableLiveData<VideoRecorderViewState> = MutableLiveData()
    var errorState: SingleLiveEvent<Throwable?> = SingleLiveEvent()

    init {
        viewState.value = VideoRecorderViewState()
    }

    override fun getViewStatus() {
        getCurrentFileUserCase()
    }

    private fun getCurrentFileUserCase() {
        addDisposable(
            createFileUseCase.observable()
                .subscribe ({ videoEntity ->
                    viewState.value?.let {_ ->
                        this.viewState.value = this.viewState.value?.copy(showLoading = false, videoEntity = videoEntity)
                        this.errorState.value = null
                    }
                }, {
                    viewState.value = viewState.value?.copy(showLoading = false)
                    errorState.value = it
                })
        )
    }

    fun saveVideo(videoEntity: VideoEntity) {
        addDisposable(
            saveVideoUseCase.save(videoEntity)
                .subscribe({ completed ->
                    isOperationComplete(completed)
                }, {
                    throwError(it)
                })
        )
    }

    fun deleteVideo(videoEntity: VideoEntity) {
        addDisposable(
            deleteVideoUseCase.delete(videoEntity)
                .subscribe ({completed ->
                    isOperationComplete(completed)
                }, {
                    throwError(it)
                })
        )
    }

    private fun isOperationComplete(completed: Boolean) {
        if (completed) {
            this.viewState.value = this.viewState.value?.copy(showLoading = false, videoEntity = null)
            this.errorState.value = null
        }
    }

    private fun throwError(error: Throwable?) {
        viewState.value = viewState.value?.copy(showLoading = false)
        errorState.value = error
    }

}