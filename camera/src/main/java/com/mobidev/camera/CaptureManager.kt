package com.mobidev.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mobidev.camera.ui.CaptureActivity
import com.mobidev.camera.video.VideoCaptureManager
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.view.CameraView

open class CaptureManager internal constructor() : LifecycleObserver {

    companion object {
        fun startCaptureActivity(activity: Activity, config: CaptureConfig) {
            activity.startActivity(CaptureActivity.intent(activity, config))
        }

        fun startCaptureOnView(activity: AppCompatActivity, viewGroup: FrameLayout, config: CaptureConfig) {
            verifyPermission(activity) { allGranted ->
                if (!allGranted) return@verifyPermission
                CameraView(activity).also { cameraView ->
                    viewGroup.addView(cameraView)
                    val captureManager = CaptureManager()
                    captureManager.attachView(activity, cameraView, activity.lifecycle, config)
                    captureManager.startVideoCapture {
                        activity.runOnUiThread {
                            viewGroup.removeView(cameraView)
                        }

                    }
                }
            }
        }

        internal fun verifyPermission(activity: Activity, allGranted: (Boolean) -> Unit) {
            Dexter.withActivity(activity)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        allGranted(report?.areAllPermissionsGranted() == true)
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        list: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                    }

                }).check()
        }
    }

    private var cameraManager: Fotoapparat? = null
    private var videoCaptureManager: VideoCaptureManager? = null
    private var cameraView: CameraView? = null

    internal fun attachView(
        context: Context,
        cameraView: CameraView,
        lifecycle: Lifecycle,
        config: CaptureConfig
    ) {
        this.cameraView = cameraView
        cameraManager = Fotoapparat(context, cameraView, scaleType = ScaleType.CenterCrop)
        videoCaptureManager = VideoCaptureManager(context, config)
        lifecycle.addObserver(this)
    }

    internal fun startVideoCapture(captureEnd: () -> Unit) {
        val videoCaptureManager = videoCaptureManager ?: return
        cameraManager?.start()
        videoCaptureManager.setupMediaRecorder {
            cameraManager?.attachRecordingCamera(it) {
                videoCaptureManager.prepare()
                videoCaptureManager.startRecordingVideo {
                    cameraManager?.lock()
                    captureEnd()
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        cameraManager?.stop()
        cameraManager = null
        videoCaptureManager?.releaseMediaRecorder()
        videoCaptureManager = null
    }
}