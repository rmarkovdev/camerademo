package com.mobidev.camera.ui

import android.app.Dialog
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import java.io.File


class ShareDialog : DialogFragment() {

    var filePath: String = ""//TODO provide parameter using arguments
    private var activities: List<ResolveInfo>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setCancelable(false)
            setTitle("Custom Sharing Dialog :)")
            setPositiveButton("Close") { _, _ -> removeFile() }
            setOnDismissListener { removeFile() }
            setSingleChoiceItems(setupAdapter(), -1) { _, which ->
                val info = activities?.get(which)
                val intent = shareIntent()
                intent.setPackage(info?.activityInfo?.packageName)
                startActivity(intent)
            }
        }.create()
    }

    private fun shareIntent(): Intent {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "com.mobidev.camerasample.fileprovider",
            File(filePath)
        )
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/mp4"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        return intent
    }


    private fun setupAdapter(): ArrayAdapter<DialogItem>? {
        val packageManager = context?.packageManager ?: return null
        val apps = packageManager.queryIntentActivities(shareIntent(), 0)
        this.activities = apps
        val items = ArrayList<DialogItem>()
        apps.mapTo(items) {
            DialogItem(
                it.loadLabel(packageManager).toString(),
                it.loadIcon(packageManager)
            )
        }

        return object :
            ArrayAdapter<DialogItem>(
                requireContext(),
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items
            ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val tv = v.findViewById<TextView>(android.R.id.text1)
                tv.text = items[position].app
                tv.textSize = 15.0f
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, null, null, null)
                tv.compoundDrawablePadding = (5 * resources.displayMetrics.density + 0.5f).toInt()
                return v
            }
        }

    }

    private fun removeFile() {
        //TODO: Remove file
        val activity = activity
        if (activity is CaptureActivity) {
            activity.finish()
        }
    }

}

data class DialogItem(val app: String, val icon: Drawable)