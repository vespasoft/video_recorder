package com.voicemod.videorecorder.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.voicemod.videorecorder.domain.usecases.CreateFileUseCase
import com.voicemod.videorecorder.domain.usecases.DeleteVideoUseCase
import com.voicemod.videorecorder.domain.usecases.SaveVideoUseCase
import com.voicemod.videorecorder.view.di.UseCaseModule

class VideoRecorderVMFactory(
    private val createFileUseCase: CreateFileUseCase,
    private val saveVideoUseCase: SaveVideoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoRecorderViewModel(createFileUseCase, saveVideoUseCase, deleteVideoUseCase) as T
    }

    companion object {
        fun inject(context: Context) =
            VideoRecorderVMFactory(
                createFileUseCase = UseCaseModule.provideCreateFileUseCase(context),
                saveVideoUseCase = UseCaseModule.provideSaveVideoUseCase(context),
                deleteVideoUseCase = UseCaseModule.provideDeleteVideoUseCase(context)
            )
    }


}