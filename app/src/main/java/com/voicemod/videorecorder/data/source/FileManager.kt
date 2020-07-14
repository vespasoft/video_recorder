package com.voicemod.videorecorder.data.source

import android.graphics.Bitmap
import java.io.File

interface FileManager {
    fun createFile(uuid: String): File
    fun getVideoThumbnail(path: String): Bitmap?
    fun deleteFile(path: String): Boolean
}