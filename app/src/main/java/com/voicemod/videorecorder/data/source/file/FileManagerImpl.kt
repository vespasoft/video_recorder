package com.voicemod.videorecorder.data.source.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Environment
import com.voicemod.videorecorder.common.Constants.VIDEO_DIRECTORY_NAME
import com.voicemod.videorecorder.data.source.FileManager
import com.voicemod.videorecorder.domain.exceptions.FileException
import java.io.File


class FileManagerImpl(private val context: Context) : FileManager {

    /**
     *
     * @param uuid
     * the uuid to the Video
     * @return a file of the video if the operation is completed.
     */
    @Throws(Exception::class)
    override fun createFile(uuid: String): File {
        val mediaStorageDir = File(Environment.getExternalStorageDirectory(), VIDEO_DIRECTORY_NAME)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                throw FileException("Oops! Failed create $VIDEO_DIRECTORY_NAME directory")
            }
        }
        return File(mediaStorageDir.path + File.separator + VIDEO_PREFIX + uuid + VIDEO_EXTENSION)

    }

    /**
     *
     * @param path
     * the path to the Video
     * @return a thumbnail of the video or null if retrieving the thumbnail failed.
     */
    @Throws(Exception::class)
    override fun getVideoThumbnail(path: String): Bitmap? {
        var bitmap: Bitmap? = null
        MediaMetadataRetriever().let { mediaMetadataRetriever ->
            try {
                mediaMetadataRetriever.setDataSource(path)
                mediaMetadataRetriever.embeddedPicture?.let { data ->
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    if (bitmap == null) {
                        bitmap = mediaMetadataRetriever.frameAtTime
                    }
                }
            } catch (e: Exception) {
                throw java.lang.Exception(e)
            } finally {
                mediaMetadataRetriever.release()
            }
        }
        return bitmap
    }

    @Throws(Exception::class)
    override fun deleteFile(path: String): Boolean {
        return try {
            File(path).delete()
        } catch (e: Exception) {
            false
        }
    }

    companion object {

        private const val PATH_SEPARATOR = "_"
        private const val VIDEO_EXTENSION = ".mp4"
        private const val VIDEO_PREFIX = "VM_"
    }

}
