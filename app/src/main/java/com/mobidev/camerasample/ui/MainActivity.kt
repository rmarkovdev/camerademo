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

        captureActivityButton.setOnClickListener {
            CaptureManager.startCaptureActivity(this, configuration())
        }

        captureViewButton.setOnClickListener {
            CaptureManager.startCaptureOnView(this, previewFrameLayout, configuration())
        }
    }


    private fun configuration(): CaptureConfig {
        return CaptureConfig(
            Integer.parseInt(durationSpinner.selectedItem.toString()),
            Integer.parseInt(frameRateSpinner.selectedItem.toString())
        )
    }


}
