package com.mobidev.camera.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobidev.camera.CaptureConfig
import com.mobidev.camera.CaptureManager
import io.fotoapparat.view.CameraView

internal class CaptureActivity : AppCompatActivity() {

    private var cameraView: CameraView? = null
    private val captureManager = CaptureManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CameraView(this).let {
            setContentView(it)
            cameraView = it
        }
    }

    override fun onStart() {
        super.onStart()
        val cameraView = cameraView ?: return
        CaptureManager.verifyPermission(this) {
            if (!it) {
                finish()
                return@verifyPermission
            }
            captureManager.attachView(this, cameraView, lifecycle, getConfig())
            captureManager.startVideoCapture {
                //TODO Start ShareDialog
                runOnUiThread { finish() }
            }
        }

    }

    private fun getConfig(): CaptureConfig {
        return (intent.getSerializableExtra(ConfigExtraKey) as? CaptureConfig) ?: CaptureConfig.defaultConfig()
    }

    companion object {
        private const val ConfigExtraKey = "config"

        internal fun intent(activity: Activity, config: CaptureConfig): Intent {
            return Intent(activity, CaptureActivity::class.java).apply {
                putExtra(ConfigExtraKey, config)
            }
        }
    }
}
