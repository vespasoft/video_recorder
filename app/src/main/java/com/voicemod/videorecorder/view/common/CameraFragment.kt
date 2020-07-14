package com.voicemod.videorecorder.view.common

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.voicemod.videorecorder.R
import io.reactivex.annotations.NonNull
import kotlinx.android.synthetic.main.fragment_video_recorder.*
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

abstract class CameraFragment : BaseFragment() {

    private var mCameraDevice: CameraDevice? = null
    private var mPreviewSession: CameraCaptureSession? = null
    private var mPreviewSize: Size? = null
    private var mVideoSize: Size? = null
    private var mMediaRecorder: MediaRecorder? = null
    private var mPreviewBuilder: CaptureRequest.Builder? = null
    private var mBackgroundThread: HandlerThread? = null
    private var mBackgroundHandler: Handler? = null
    private val mCameraOpenCloseLock = Semaphore(1)
    private var mSensorOrientation: Int? = null

    var mIsRecordingVideo: Boolean = false

    private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(
            surfaceTexture: SurfaceTexture,
            width: Int, height: Int
        ) {
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(
            surfaceTexture: SurfaceTexture,
            width: Int, height: Int
        ) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}

    }

    private val mStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(@NonNull cameraDevice: CameraDevice) {
            mCameraDevice = cameraDevice
            startPreview()
            mCameraOpenCloseLock.release()
            configureTransform(mTextureView.width, mTextureView.height)
        }

        override fun onDisconnected(@NonNull cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(@NonNull cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
            val activity = activity
            activity?.finish()
        }

    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        requestPermission()
    }

    override fun onPause() {
        closeCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper)
    }

    private fun stopBackgroundThread() {
        mBackgroundThread?.let { handlerThread ->
            handlerThread.quitSafely()
            try {
                handlerThread.join()
                mBackgroundThread = null
                mBackgroundHandler = null
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

    }

    fun enableTextureViewAndOpenCamera() {
        mTextureView.visibility = View.VISIBLE
        openCamera(mTextureView.width, mTextureView.height)
    }

    fun hiddenTextureView() {
        mTextureView.visibility = View.INVISIBLE
    }

    private fun requestPermission() {
        Dexter.withActivity(activity).withPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        if (mTextureView.isAvailable) {
                            openCamera(mTextureView.width, mTextureView.height)
                        } else {
                            mTextureView.surfaceTextureListener = mSurfaceTextureListener
                        }
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { error ->
                Toast.makeText(
                    activity!!.applicationContext,
                    "Error occurred! ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .onSameThread()
            .check()
    }

    /**
     * Showing Alert Dialog with Settings option in case of deny any permission
     */
    private fun showSettingsDialog() {
        AlertDialog.Builder(activity)
        .setTitle(getString(R.string.message_need_permission))
        .setMessage(getString(R.string.message_permission))
        .setPositiveButton(getString(R.string.title_go_to_setting)) { dialog, which ->
            dialog.cancel()
            this@CameraFragment.openSettings()
        }
        .show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity!!.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun openCamera(width: Int, height: Int) {
        val activity = activity
        if (null == activity || activity.isFinishing) {
            return
        }
        val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            val cameraId = manager.cameraIdList[0]

            val characteristics = manager.getCameraCharacteristics(cameraId)
            val map = characteristics
                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
            if (map == null) {
                throw RuntimeException("Cannot get available preview/video sizes")
            }
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))
            mPreviewSize = chooseOptimalSize(
                map.getOutputSizes(SurfaceTexture::class.java),
                width, height, mVideoSize!!
            )

            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize!!.width, mPreviewSize!!.height)
            } else {
                mTextureView.setAspectRatio(mPreviewSize!!.height, mPreviewSize!!.width)
            }
            configureTransform(width, height)
            mMediaRecorder = MediaRecorder()
            if (ActivityCompat.checkSelfPermission(
                    getActivity()!!,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                requestPermission()
                return
            }
            manager.openCamera(cameraId, mStateCallback, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "openCamera: Cannot access the camera.")
        } catch (e: NullPointerException) {
            Log.e(TAG, "Camera2API is not supported on the device.")
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.")
        }

    }

    private fun closeCamera() {
        try {
            mCameraOpenCloseLock.acquire()
            closePreviewSession()
            if (null != mCameraDevice) {
                mCameraDevice?.close()
                mCameraDevice = null
            }
            if (null != mMediaRecorder) {
                mMediaRecorder?.release()
                mMediaRecorder = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.")
        } finally {
            mCameraOpenCloseLock.release()
        }
    }

    private fun startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable || null == mPreviewSize) {
            return
        }
        try {
            closePreviewSession()
            val texture = mTextureView.surfaceTexture
            mPreviewSize?.let { size ->
                texture.setDefaultBufferSize(size.width, size.height)
            }
            mPreviewBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            val previewSurface = Surface(texture)
            mPreviewBuilder?.addTarget(previewSurface)
            mCameraDevice?.createCaptureSession(listOf(previewSurface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(@NonNull session: CameraCaptureSession) {
                        mPreviewSession = session
                        updatePreview()
                    }

                    override fun onConfigureFailed(@NonNull session: CameraCaptureSession) {
                        Log.e(TAG, "onConfigureFailed: Failed ")
                    }
                }, mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    /**
     * Update the camera preview. [.startPreview] needs to be called in advance.
     */
    private fun updatePreview() {
        if (null == mCameraDevice) {
            return
        }
        try {
            setUpCaptureRequestBuilder(mPreviewBuilder!!)
            val thread = HandlerThread("CameraPreview")
            thread.start()
            mPreviewSession!!.setRepeatingRequest(
                mPreviewBuilder!!.build(),
                null,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    private fun setUpCaptureRequestBuilder(builder: CaptureRequest.Builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
    }

    /**
     * Configures the necessary [Matrix] transformation to `mTextureView`.
     * This method should not to be called until the camera preview size is determined in
     * openCamera, or until the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        activity?.let { activity ->
            mPreviewSize?.let { size ->
                if (null == mTextureView || null == mPreviewSize || null == activity) {
                    return
                }
                val rotation = activity.windowManager.defaultDisplay.rotation
                val matrix = Matrix()
                val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
                val bufferRect =
                    RectF(0f, 0f, size.height.toFloat(), size.width.toFloat())
                val centerX = viewRect.centerX()
                val centerY = viewRect.centerY()
                if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
                    bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
                    matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                    val scale = Math.max(
                        viewHeight.toFloat() / size.height,
                        viewWidth.toFloat() / size.width
                    )
                    matrix.postScale(scale, scale, centerX, centerY)
                    matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
                }
                mTextureView.setTransform(matrix)
            }
        }
    }

    @Throws(IOException::class)
    private fun setUpMediaRecorder(absolutePath: String) {
        activity?.let { activity ->
            mMediaRecorder?.let { mediaRecorder ->
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                mediaRecorder.setOutputFile(absolutePath)
                val profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P)
                mediaRecorder.setVideoFrameRate(profile.videoFrameRate)
                mediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight)
                mediaRecorder.setVideoEncodingBitRate(profile.videoBitRate)
                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder.setAudioEncodingBitRate(profile.audioBitRate)
                mediaRecorder.setAudioSamplingRate(profile.audioSampleRate)
                val rotation = activity.windowManager.defaultDisplay.rotation
                when (mSensorOrientation) {
                    SENSOR_ORIENTATION_DEFAULT_DEGREES -> mMediaRecorder!!.setOrientationHint(
                        DEFAULT_ORIENTATIONS.get(rotation)
                    )
                    SENSOR_ORIENTATION_INVERSE_DEGREES -> mMediaRecorder!!.setOrientationHint(
                        INVERSE_ORIENTATIONS.get(rotation)
                    )
                }
                mediaRecorder.prepare()
            }
        }
    }

    fun startRecordingVideo(absolutePath: String) {
        if (null == mCameraDevice || mTextureView.isAvailable.not() || null == mPreviewSize) {
            return
        }
        try {
            closePreviewSession()
            setUpMediaRecorder(absolutePath)
            mTextureView.surfaceTexture?.let { texture ->
                mPreviewSize?.let { size ->
                    texture.setDefaultBufferSize(size.width, size.height)
                }
                mPreviewBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
                val surfaces = ArrayList<Surface>()
                val previewSurface = Surface(texture)
                surfaces.add(previewSurface)
                mPreviewBuilder?.addTarget(previewSurface)

                //MediaRecorder setup for surface
                mMediaRecorder?.surface?.let { recorderSurface ->
                    surfaces.add(recorderSurface)
                    mPreviewBuilder?.addTarget(recorderSurface)
                    // Start a capture session
                    mCameraDevice?.createCaptureSession(
                        surfaces,
                        object : CameraCaptureSession.StateCallback() {

                            override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
                                mPreviewSession = cameraCaptureSession
                                updatePreview()
                                activity?.runOnUiThread {
                                    mIsRecordingVideo = true
                                    // Start recording
                                    mMediaRecorder?.start()
                                }
                            }

                            override fun onConfigureFailed(@NonNull cameraCaptureSession: CameraCaptureSession) {
                                Log.e(TAG, "onConfigureFailed: Failed")
                            }
                        },
                        mBackgroundHandler
                    )
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession?.close()
            mPreviewSession = null
        }
    }

    @Throws(Exception::class)
    fun stopRecordingVideo() {
        // UI
        mIsRecordingVideo = false
        try {
            mPreviewSession?.stopRepeating()
            mPreviewSession?.abortCaptures()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        // Stop recording
        mMediaRecorder?.stop()
        mMediaRecorder?.reset()
    }

    /**
     * Compares two `Size`s based on their areas.
     */
    internal class CompareSizesByArea : Comparator<Size> {

        override fun compare(lhs: Size, rhs: Size): Int {
            // We cast here to ensure the multiplications won't overflow
            return java.lang.Long.signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
        }

    }

    companion object {

        private const val TAG = "CameraVideoFragment"

        private const val SENSOR_ORIENTATION_INVERSE_DEGREES = 270
        private const val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
        private val INVERSE_ORIENTATIONS = SparseIntArray()
        private val DEFAULT_ORIENTATIONS = SparseIntArray()

        init {
            INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0)
            INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90)
            INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180)
            INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270)
        }

        init {
            DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0)
            DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90)
            DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180)
            DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270)
        }

        /**
         * In this sample, we choose a video size with 3x4 for  aspect ratio. for more perfectness 720 as well Also, we don't use sizes
         * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
         *
         * @param choices The list of available sizes
         * @return The video size 1080p,720px
         */
        private fun chooseVideoSize(choices: Array<Size>): Size {
            for (size in choices) {
                if (1920 == size.width && 1080 == size.height) {
                    return size
                }
            }
            for (size in choices) {
                if (size.width == size.height * 4 / 3 && size.width <= 1080) {
                    return size
                }
            }
            Log.e(TAG, "Couldn't find any suitable video size")
            return choices[choices.size - 1]
        }


        /**
         * Given `choices` of `Size`s supported by a camera, chooses the smallest one whose
         * width and height are at least as large as the respective requested values, and whose aspect
         * ratio matches with the specified value.
         *
         * @param choices     The list of sizes that the camera supports for the intended output class
         * @param width       The minimum desired width
         * @param height      The minimum desired height
         * @param aspectRatio The aspect ratio
         * @return The optimal `Size`, or an arbitrary one if none were big enough
         */
        private fun chooseOptimalSize(
            choices: Array<Size>,
            width: Int,
            height: Int,
            aspectRatio: Size
        ): Size {
            // Collect the supported resolutions that are at least as big as the preview Surface
            val bigEnough = ArrayList<Size>()
            val w = aspectRatio.width
            val h = aspectRatio.height
            for (option in choices) {
                if (option.height == option.width * h / w &&
                    option.width >= width && option.height >= height
                ) {
                    bigEnough.add(option)
                }
            }

            // Pick the smallest of those, assuming we found any
            if (bigEnough.size > 0) {
                return Collections.min(bigEnough, CompareSizesByArea())
            } else {
                Log.e(TAG, "Couldn't find any suitable preview size")
                return choices[0]
            }
        }
    }

}

