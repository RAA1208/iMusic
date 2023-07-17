package com.rishabhjain.imusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            Application.PREVIOUS -> nextPreviousBtn(increment = false, context = context!!)
            Application.PLAY -> if (PlayerActivity.musicService!!.mediaPlayer!!.isPlaying) pauseMusic() else playMusic()
            Application.NEXT -> nextPreviousBtn(increment = true, context = context!!)
            Application.EXIT -> {
                PlayerActivity.musicService!!.stopForeground(true)
                PlayerActivity.musicService!!.mediaPlayer!!.release()
                PlayerActivity.musicService = null
                exitProcess(1)
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.isPlaying
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.pause_icon)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.play_icon)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nextPreviousBtn(increment: Boolean, context: Context){
        setSongsPosition(increment = increment)
        PlayerActivity.musicService!!.creatingMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musilistPA[PlayerActivity.songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.mipmap.music_icon)).centerCrop()
            .into(PlayerActivity.binding.songImg)
        playMusic()
        PlayerActivity.findex = favChecker(PlayerActivity.musilistPA[PlayerActivity.songPosition].id)
        if (PlayerActivity.isfavorite)
            PlayerActivity.binding.favBtn.setImageResource(R.drawable.favorite_icon)
        else
            PlayerActivity.binding.favBtn.setImageResource(R.drawable.favorite_border_icon)

    }

}