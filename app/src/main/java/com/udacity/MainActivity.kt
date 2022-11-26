package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.Models.FileItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var notificationManager = getSystemService(NotificationManager::class.java)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        glide_button.setOnCheckedChangeListener { compoundButton, newValue ->
            if (newValue) {
                url_text.isEnabled = false
                title = getString(R.string.button_glide)
                url_text.setText(getString(R.string.glide_url))
            }
        }

        udacity_button.setOnCheckedChangeListener { compoundButton, newValue ->
            if (newValue) {
                url_text.isEnabled = false
                title = getString(R.string.button_udaciy)
                url_text.setText(getString(R.string.udacity_url))
            }
        }

        retrofit_button.setOnCheckedChangeListener { compoundButton, newValue ->
            if (newValue) {
                url_text.isEnabled = false
                title = getString(R.string.retrofit_button)
                url_text.setText(getString(R.string.retrofit_url))
            }
        }

        other_button.setOnCheckedChangeListener { compoundButton, newValue ->
            if (newValue) {
                url_text.isEnabled = true
                title = ""
                url_text.setText("")
            }
        }

        download_button.setOnClickListener {
            download()
            //download_button.startDownload()
        }

        suggested_group.check(udacity_button.id)

        notificationManager.createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
            getString(R.string.notification_description)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            download_button.endDownload()
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Toast.makeText(context,"file Downloaded",Toast.LENGTH_LONG).show()
            notifyDownload(id ?: 0)
        }
    }

    private fun notifyDownload(fileID: Long) {

        val query = DownloadManager.Query().setFilterById(fileID)
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val fileQuery = downloadManager.query(query)
        if (fileQuery.moveToFirst()) {
            val status = fileQuery.getInt(fileQuery.getColumnIndex(DownloadManager.COLUMN_STATUS))

            val title = fileQuery.getString(fileQuery.getColumnIndex(DownloadManager.COLUMN_TITLE))

            val fileItem = FileItem(title, status)

            var notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.sendNotification(
                getString(R.string.app_name),
                getString(R.string.notification_description),
                fileItem,
                this
            )
        }
    }

    private fun download() {
        try {
            val uri = Uri.parse(url_text.text.toString())
            val request =
                DownloadManager.Request(uri)
                    .setTitle(title)
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            var downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        } catch (Exc:IllegalArgumentException) {
            Toast.makeText(this,"please, choose a vaild url",Toast.LENGTH_LONG).show()
            download_button.endDownload()

        } catch (ex:Exception){
            download_button.endDownload()
        }
    }

    companion object {
        //private const val URL ="https://github.com/bumptech/glide/archive/refs/heads/master.zip"
//            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        //private const val CHANNEL_ID = "channelId"
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}