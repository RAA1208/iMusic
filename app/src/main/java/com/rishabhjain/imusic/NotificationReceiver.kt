package com.rishabhjain.imusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            Application.PREVIOUS -> nextPreviousBtn(increment = false, context = context!!)
            Application.PLAY -> if (PlayerActivity.musicService!!.mediaPlayer!!.isPlaying) pauseMusic() else playMusic()
            Application.NEXT -> nextPreviousBtn(increment = true, context = context!!)
            Application.EXIT -> {
               val builder =   MaterialAlertDialogBuilder(context!!)
                   .setTitle("Exit")
                   .setMessage("Do you want to close app")
                   .setPositiveButton("YES"){_,_ ->
                       exitProcess()
                   }
                   .setNegativeButton("NO"){dialog,_ ->
                       dialog.dismiss()
                   }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.isPlaying
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
        NowPlayingFragment.binding.nowPlayPause.setIconResource(R.drawable.pause_icon)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.play_icon)
        NowPlayingFragment.binding.nowPlayPause.setIconResource(R.drawable.play_icon)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nextPreviousBtn(increment: Boolean, context: Context){
        setSongsPosition(increment = increment)
        PlayerActivity.musicService!!.creatingMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musilistPA[PlayerActivity.songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.mipmap.music_icon)).centerCrop()
            .into(PlayerActivity.binding.songImg)

        Glide.with(context)
            .load(PlayerActivity.musilistPA[PlayerActivity.songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.mipmap.music_icon)).centerCrop()
            .into(NowPlayingFragment.binding.nowplaySongimg)

        NowPlayingFragment.binding.nowplysongname.text = PlayerActivity.musilistPA[PlayerActivity.songPosition].title

        PlayerActivity.findex = favChecker(PlayerActivity.musilistPA[PlayerActivity.songPosition].id)
        if (PlayerActivity.isfavorite)
            PlayerActivity.binding.favBtn.setImageResource(R.drawable.favorite_icon)
        else
            PlayerActivity.binding.favBtn.setImageResource(R.drawable.favorite_border_icon)

        playMusic()

    }

}