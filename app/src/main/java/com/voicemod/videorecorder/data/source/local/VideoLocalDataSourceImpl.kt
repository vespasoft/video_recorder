package com.voicemod.videorecorder.data.source.local

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.voicemod.videorecorder.data.source.VideoLocalDataSource
import com.voicemod.videorecorder.data.source.db.VideoRecorderContract
import com.voicemod.videorecorder.data.entities.VideoEntity
import com.voicemod.videorecorder.domain.exceptions.DatabaseException

import java.util.ArrayList

class VideoLocalDataSourceImpl(
    private val sqLiteOpenHelper: SQLiteOpenHelper
) : VideoLocalDataSource {

    @Throws(Exception::class)
    override fun save(videoEntity: VideoEntity) {
        val db = sqLiteOpenHelper.writableDatabase

        db.beginTransaction()

        try {
            val values = ContentValues()
                values.clear()
                values.put(VideoRecorderContract.Video.COLUMN_VIDEO_ID, videoEntity.id)
                values.put(VideoRecorderContract.Video.COLUMN_NAME, videoEntity.name)
                values.put(VideoRecorderContract.Video.COLUMN_URL, videoEntity.path)
                values.put(VideoRecorderContract.Video.COLUMN_CREATEDAT, videoEntity.createdAt)
                values.put(VideoRecorderContract.Video.COLUMN_UPDATEDAT, videoEntity.updatedAt)
                db.insertWithOnConflict(
                    VideoRecorderContract.Video.TABLE_NAME, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            throw DatabaseException(e.message)
        } finally {
            db.endTransaction()
        }
    }

    override fun saveAll(videoEntityList: List<VideoEntity>) {
        for (videoEntity in videoEntityList) {
            save(videoEntity)
        }
    }

    override fun get(videoId: Int): VideoEntity? {
        val db = sqLiteOpenHelper.writableDatabase

        val sql = "SELECT * FROM " + VideoRecorderContract.Video.TABLE_NAME +
                " WHERE " + VideoRecorderContract.Video.COLUMN_VIDEO_ID + " = " + videoId.toString()

        val cursor = db.rawQuery(sql, null)

        return if (cursor.moveToFirst()) {
            createVideoEntity(cursor)
        } else null
    }

    @Throws(Exception::class)
    override fun getAll(): List<VideoEntity> {
        val db = sqLiteOpenHelper.writableDatabase

        val sql = "SELECT * FROM " + VideoRecorderContract.Video.TABLE_NAME

        val cursor = db.rawQuery(sql, null)

        return createVideoEntityList(cursor)
    }

    override fun remove(videoEntity: VideoEntity) {
        val db = sqLiteOpenHelper.writableDatabase

        val whereClause = VideoRecorderContract.Video.COLUMN_VIDEO_ID + "=?"
        val whereArgs = arrayOf(videoEntity.id.toString())

        db.delete(VideoRecorderContract.Video.TABLE_NAME, whereClause, whereArgs)
    }

    override fun removeAll() {
        val db = sqLiteOpenHelper.writableDatabase

        val whereClause = VideoRecorderContract.Video.COLUMN_VIDEO_ID + ">?"
        val whereArgs = arrayOf("0")

        db.delete(VideoRecorderContract.Video.TABLE_NAME, whereClause, whereArgs)
    }

    @Throws(Exception::class)
    private fun createVideoEntityList(cursor: Cursor?): List<VideoEntity> {
        val documentEntityList = ArrayList<VideoEntity>()
        try {
            if (cursor!!.moveToFirst()) {
                do {
                    documentEntityList.add(createVideoEntity(cursor))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            throw DatabaseException(e.message)
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
        return documentEntityList
    }

    private fun createVideoEntity(cursor: Cursor): VideoEntity {
        return VideoEntity(
            id = cursor.getString(cursor.getColumnIndex(VideoRecorderContract.Video.COLUMN_VIDEO_ID)),
            name = cursor.getString(cursor.getColumnIndex(VideoRecorderContract.Video.COLUMN_NAME)),
            path = cursor.getString(cursor.getColumnIndex(VideoRecorderContract.Video.COLUMN_URL)),
            createdAt = cursor.getString(cursor.getColumnIndex(VideoRecorderContract.Video.COLUMN_CREATEDAT)),
            updatedAt = cursor.getString(cursor.getColumnIndex(VideoRecorderContract.Video.COLUMN_UPDATEDAT)),
            duration = 1000
        )
    }

}
