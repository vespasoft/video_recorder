package com.voicemod.videorecorder.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.voicemod.videorecorder.data.entities.VideoEntity
import com.voicemod.videorecorder.data.mappers.Mapper
import com.voicemod.videorecorder.domain.mappers.VideoEntityLibraryItemMapper
import com.voicemod.videorecorder.domain.usecases.DeleteVideoUseCase
import com.voicemod.videorecorder.domain.usecases.GetLibraryItemsUseCase
import com.voicemod.videorecorder.view.adapter.LibraryItem
import com.voicemod.videorecorder.view.di.UseCaseModule

class VideoLibraryVMFactory(
    private val getLibraryItems: GetLibraryItemsUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase,
    private val videoEntityLibraryItemMapper: Mapper<VideoEntity, LibraryItem>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoLibraryViewModel(getLibraryItems, deleteVideoUseCase, videoEntityLibraryItemMapper) as T
    }

    companion object {
        fun inject(context: Context) = VideoLibraryVMFactory(
            getLibraryItems = UseCaseModule.provideGetLibraryItemsUseCase(context),
            deleteVideoUseCase = UseCaseModule.provideDeleteVideoUseCase(context),
            videoEntityLibraryItemMapper = VideoEntityLibraryItemMapper()
        )
    }


}