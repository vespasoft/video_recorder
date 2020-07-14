package com.voicemod.videorecorder.view

import android.app.Application
import com.voicemod.videorecorder.presentation.viewmodel.VideoLibraryVMFactory
import com.voicemod.videorecorder.presentation.viewmodel.VideoRecorderVMFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App: Application() {

    // just declare it
    private val videoRecorderModule = module {
        single { VideoRecorderVMFactory.inject(androidContext())}
    }
    private val videoLibraryModule = module {
        single { VideoLibraryVMFactory.inject(androidContext())}
    }

    override fun onCreate() {
        super.onCreate()

        // start Koin!
        startKoin {
            // Android context
            androidContext(this@App)
            // modules
            modules(videoRecorderModule, videoLibraryModule)
        }
    }

}