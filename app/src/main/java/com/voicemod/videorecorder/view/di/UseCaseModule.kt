package com.voicemod.videorecorder.view.di

import android.content.Context
import com.voicemod.videorecorder.common.ASyncTransformer
import com.voicemod.videorecorder.data.VideoLibraryRepositoryImpl
import com.voicemod.videorecorder.data.mappers.FromFileToVideoEntityMapper
import com.voicemod.videorecorder.data.source.db.VideoRecorderDbHelper
import com.voicemod.videorecorder.data.source.file.FileManagerImpl
import com.voicemod.videorecorder.data.source.local.VideoLocalDataSourceImpl
import com.voicemod.videorecorder.domain.usecases.CreateFileUseCase
import com.voicemod.videorecorder.domain.usecases.DeleteVideoUseCase
import com.voicemod.videorecorder.domain.usecases.GetLibraryItemsUseCase
import com.voicemod.videorecorder.domain.usecases.SaveVideoUseCase

class UseCaseModule {

    companion object {

        private fun provideVideoLibraryRepository(context: Context) = VideoLibraryRepositoryImpl(
            fileManager = FileManagerImpl(context = context),
            videoLocalDataSource = VideoLocalDataSourceImpl(
                sqLiteOpenHelper = VideoRecorderDbHelper(
                    context = context
                )
            ),
            fromFileToVideoEntityMapper = FromFileToVideoEntityMapper()
        )

        fun provideCreateFileUseCase(context: Context): CreateFileUseCase {
            return CreateFileUseCase(
                transformer = ASyncTransformer(),
                videoLibraryRepository = provideVideoLibraryRepository(context)
            )
        }

        fun provideSaveVideoUseCase(context: Context): SaveVideoUseCase {
            return SaveVideoUseCase(
                transformer = ASyncTransformer(),
                videoLibraryRepository = provideVideoLibraryRepository(context)
            )
        }

        fun provideDeleteVideoUseCase(context: Context): DeleteVideoUseCase {
            return DeleteVideoUseCase(
                transformer = ASyncTransformer(),
                videoLibraryRepository = provideVideoLibraryRepository(context)
            )
        }

        fun provideGetLibraryItemsUseCase(context: Context): GetLibraryItemsUseCase {
            return GetLibraryItemsUseCase(
                transformer = ASyncTransformer(),
                videoLibraryRepository = provideVideoLibraryRepository(context)
            )
        }
    }
}