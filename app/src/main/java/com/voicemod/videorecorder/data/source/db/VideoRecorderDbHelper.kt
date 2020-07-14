package com.voicemod.videorecorder.data.source.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class VideoRecorderDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    internal interface References {
        companion object {
            val DOCUMENT_ID = String.format(
                "REFERENCES %s(%s) ON DELETE CASCADE ON UPDATE NO ACTION",
                VideoRecorderContract.Video.TABLE_NAME, VideoRecorderContract.Video.COLUMN_VIDEO_ID
            )
        }
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            db.setForeignKeyConstraintsEnabled(true)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE " + VideoRecorderContract.Video.TABLE_NAME + " (" +
                    VideoRecorderContract.Video.COLUMN_VIDEO_ID + TEXT_PRIMARY_TYPE + COMMA_SEP +
                    VideoRecorderContract.Video.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    VideoRecorderContract.Video.COLUMN_CREATEDAT + TEXT_TYPE + COMMA_SEP +
                    VideoRecorderContract.Video.COLUMN_UPDATEDAT + TEXT_TYPE + COMMA_SEP +
                    VideoRecorderContract.Video.COLUMN_URL + TEXT_TYPE + " )"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + VideoRecorderContract.Video.TABLE_NAME)

        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "VoiceMod.db"
        private const val TEXT_TYPE = " TEXT"
        private const val TEXT_PRIMARY_TYPE = " TEXT PRIMARY KEY"
        private const val COMMA_SEP = ","
    }

}
