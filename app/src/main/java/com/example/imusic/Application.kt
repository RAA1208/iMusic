package com.example.imusic

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

class Application: Application() {
    companion object{
        const val CHHANEL_ID = "channel_id1"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }


    override fun onCreate() {
        super.onCreate()
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHHANEL_ID, "Now Playing", NotificationManager.IMPORTANCE_HIGH)
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(notificationChannel)

        }
    }


}