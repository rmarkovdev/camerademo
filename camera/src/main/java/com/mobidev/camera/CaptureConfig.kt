package com.mobidev.camera

import java.io.Serializable


//TODO Available frame rates should be retrieved from camera config
data class CaptureConfig(
    val duration: Int,//Seconds
    val frameRate: Int
) : Serializable {
    companion object {
        fun defaultConfig() = CaptureConfig(15, 30)
    }
}