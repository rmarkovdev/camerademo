package com.mobidev.camera.video

import android.content.Context
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Environment
import com.mobidev.camera.CaptureConfig
import java.util.*
import kotlin.concurrent.schedule


internal class VideoCaptureManager(
    private val context: Context,
    private val config: CaptureConfig
) {

    private var mediaRecorder: MediaRecorder? = null
    private var videoAbsolutePath: String? = null
    private var timerTask: TimerTask? = null

    fun setupMediaRecorder(after: (MediaRecorder) -> Unit) {
        if (videoAbsolutePath.isNullOrEmpty()) {
            videoAbsolutePath = getVideoFilePath(context)
        }
        val mediaRecorder = MediaRecorder()
        this.mediaRecorder = mediaRecorder
        after(mediaRecorder)
    }

    fun prepare() {
        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
            setVideoFrameRate(config.frameRate)
            setOutputFile(videoAbsolutePath)
            prepare()
        }
    }


    fun startRecordingVideo(after: (String?) -> Unit) {
        mediaRecorder?.start()
        timerTask = Timer("Recording", false).schedule(config.duration * 1000L) {
            releaseMediaRecorder()
            after(videoAbsolutePath)
        }
    }


    fun releaseMediaRecorder() {
        timerTask?.cancel()
        mediaRecorder?.reset()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun getVideoFilePath(context: Context): String {
        val filename = "video_" + System.currentTimeMillis() + ".mp4"
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return if (dir == null) {
            filename
        } else {
            "${dir.absolutePath}/$filename"
        }
    }

}