package com.mobidev.camerasample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobidev.camera.CaptureConfig
import com.mobidev.camera.CaptureManager
import com.mobidev.camerasample.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        capture_activity_button.setOnClickListener {
            CaptureManager.startCaptureActivity(this, CaptureConfig(5, 24))
        }

        capture_view_button.setOnClickListener {
            CaptureManager.startCaptureOnView(this, group, CaptureConfig.defaultConfig())
        }
    }
}
