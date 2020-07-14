package com.voicemod.videorecorder.view.common.extension

import android.content.res.Resources
import com.coremedia.iso.IsoFile
import com.coremedia.iso.boxes.Container
import com.coremedia.iso.boxes.TrackBox
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Mp4TrackImpl
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import kotlin.math.roundToInt

fun Float.convertDpToPixel(): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = this * (metrics.densityDpi / 160f)
    return px.roundToInt()
}

//TODO: It's not necessary, it's pending to delete
fun String.parseVideo(): String {
    val filePath = this
    val channel = FileDataSourceImpl(this)
    val isoFile = IsoFile(channel)
    val trackBoxes = isoFile.movieBox.getBoxes(TrackBox::class.java)
    var isError = false
    for (trackBox in trackBoxes) {
        val firstEntry =
            trackBox.mediaBox.mediaInformationBox.sampleTableBox.timeToSampleBox.entries[0]
        // Detect if first sample is a problem and fix it in isoFile
        // This is a hack. The audio deltas are 1024 for my files, and video deltas about 3000
        // 10000 seems sufficient since for 30 fps the normal delta is about 3000
        if (firstEntry.delta > 10000) {
            isError = true
            firstEntry.delta = 3000
        }
    }

    if (isError) {
        val movie = Movie()
        for (trackBox in trackBoxes) {
            movie.addTrack(
                Mp4TrackImpl(
                    channel.toString() + "[" + trackBox.trackHeaderBox.trackId + "]",
                    trackBox
                )
            )
        }
        movie.matrix = isoFile.movieBox.movieHeaderBox.matrix
        val out: Container = DefaultMp4Builder().build(movie)

        val fc: FileChannel = RandomAccessFile(filePath, "rw").channel
        out.writeContainer(fc)
        fc.close()
        return filePath
    }
    return this
}