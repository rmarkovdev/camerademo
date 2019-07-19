package io.fotoapparat.routine.focus

import android.media.MediaRecorder
import io.fotoapparat.hardware.Device
import io.fotoapparat.result.FocusResult
import kotlinx.coroutines.runBlocking

/**
 * Focuses the camera.
 */
internal fun Device.focus(): FocusResult = runBlocking {
    val cameraDevice = awaitSelectedCamera()

    cameraDevice.autoFocus()
}

internal fun Device.attachRecordingCamera(mediaRecorder: MediaRecorder) = runBlocking {
    val cameraDevice = awaitSelectedCamera()
    cameraDevice.unlock()
    cameraDevice.attachRecordingCamera(mediaRecorder)
}
