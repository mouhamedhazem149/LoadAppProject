package com.udacity

import android.animation.ObjectAnimator
import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.Models.FileItem
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val filePackage = intent.getParcelableExtra<FileItem>(getString(R.string.file_package_key))

        filename_text.setText(filePackage?.fileTitle ?: "")
        status_text.updateTextViewFromFileStatus(filePackage?.fileStatus ?: -1)

        ok_button.setOnClickListener {
            onNavigateUp()
        }

        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelNotifications()
    }
}