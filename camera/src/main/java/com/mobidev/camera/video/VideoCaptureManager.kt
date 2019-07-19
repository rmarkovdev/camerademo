package com.mobidev.camera.video

import android.content.Context
import android.media.MediaRecorder
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
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(videoAbsolutePath)
            setVideoSize(1920, 1080)//TODO Should be configured
            setVideoFrameRate(config.frameRate)
            setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
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
        val filename = "${System.currentTimeMillis()}.mp4"
        val dir = context.getExternalFilesDir(null)

        return if (dir == null) {
            filename
        } else {
            "${dir.absolutePath}/$filename"
        }
    }

}