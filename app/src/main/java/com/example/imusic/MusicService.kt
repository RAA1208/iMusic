package com.example.imusic

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable


    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "iMusic")
        return myBinder

    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {

            return this@MusicService
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification(playPauseBtn: Int) {
        val prevIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Application.PREVIOUS)
        val pendingPrevIntent =
            PendingIntent.getBroadcast(baseContext, 10, prevIntent, PendingIntent.FLAG_IMMUTABLE)

        val playIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Application.PLAY)
        val pendingPlayIntent =
            PendingIntent.getBroadcast(baseContext, 10, playIntent, PendingIntent.FLAG_IMMUTABLE)

        val nextIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Application.NEXT)
        val pendingNextIntent =
            PendingIntent.getBroadcast(baseContext, 10, nextIntent, PendingIntent.FLAG_IMMUTABLE)

        val exitIntent =
            Intent(baseContext, NotificationReceiver::class.java).setAction(Application.EXIT)
        val pendingExitIntent =
            PendingIntent.getBroadcast(baseContext, 10, exitIntent, PendingIntent.FLAG_IMMUTABLE)

        val imgArray = getImgPath(PlayerActivity.musilistPA[PlayerActivity.songPosition].path)
        val image = if (imgArray != null) {
            BitmapFactory.decodeByteArray(imgArray, 0, imgArray.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.music_icon_foreground)
        }

        val notification = NotificationCompat.Builder(baseContext, Application.CHHANEL_ID)
            .setSmallIcon(R.drawable.playlists_icon)
            .setLargeIcon(image)
            .setContentTitle(PlayerActivity.musilistPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musilistPA[PlayerActivity.songPosition].artist)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
            )
            .addAction(R.drawable.previous_icon, "Previous", pendingPrevIntent)
            .addAction(playPauseBtn, "Play", pendingPlayIntent)
            .addAction(R.drawable.next_icon, "Next", pendingNextIntent)
            .addAction(R.drawable.exit_icon, "Exit", pendingExitIntent)
            .build()

        startForeground(8, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    fun creatingMediaPlayer() {
        if (PlayerActivity.musicService!!.mediaPlayer == null)
            PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
        else PlayerActivity.musicService!!.mediaPlayer!!.reset()
        PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musilistPA[PlayerActivity.songPosition].path)
        PlayerActivity.musicService!!.mediaPlayer!!.prepare()
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
        PlayerActivity.binding.songName.text = PlayerActivity.musilistPA[PlayerActivity.songPosition].title
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.tvSeekbarStart.text = formatTimeDuration(mediaPlayer!!.currentPosition.toLong())
        PlayerActivity.binding.tvSeekbarEnd.text = formatTimeDuration(mediaPlayer!!.duration.toLong())
        PlayerActivity.binding.seekbar.progress = 0
        PlayerActivity.binding.seekbar.max = mediaPlayer!!.duration

    }

    fun seekbarProgress(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekbarStart.text = formatTimeDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekbar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)


    }
}