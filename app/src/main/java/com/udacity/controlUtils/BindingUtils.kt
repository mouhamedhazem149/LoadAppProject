package com.udacity

import android.app.DownloadManager
import android.graphics.Color
import android.widget.TextView

fun TextView.updateTextViewFromFileStatus(fileStatus: Int) {
    when (fileStatus) {
        DownloadManager.STATUS_PAUSED -> {
            setText(context.getString(R.string.paused_status))
            setTextColor(Color.YELLOW)
        }
        DownloadManager.STATUS_PENDING -> {
            setText(context.getString(R.string.pending_status))
            setTextColor(Color.YELLOW)
        }
        DownloadManager.STATUS_RUNNING -> {
            setText(context.getString(R.string.running_status))
            setTextColor(Color.BLUE)
        }
        DownloadManager.STATUS_SUCCESSFUL -> {
            setText(context.getString(R.string.success_status))
            setTextColor(Color.GREEN)
        }
        DownloadManager.STATUS_FAILED -> {
            setText(context.getString(R.string.failed_status))
            setTextColor(Color.RED)
        }
        else -> {
            setText(context.getString(R.string.unknown_status))
            setTextColor(Color.RED)
        }
    }
}