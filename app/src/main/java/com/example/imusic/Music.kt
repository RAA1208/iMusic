package com.example.imusic

import android.media.MediaMetadataRetriever
import android.widget.Toast
import java.util.concurrent.TimeUnit

data class Music(val id: String, val title: String, val album: String, val duration: Long, val artist: String,
                 val path: String, val imgUri: String )

    fun formatTimeDuration(duration: Long):String{
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds  =TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)-
                minutes*TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minutes, seconds)
    }

     fun getImgPath(path: String): ByteArray? {
         val retriever = MediaMetadataRetriever()
         retriever.setDataSource(path)
         return retriever.embeddedPicture
     }

    fun setSongsPosition(increment: Boolean){
    if (!PlayerActivity.repeat){
        if (increment){
            if (PlayerActivity.songPosition != PlayerActivity.musilistPA.size-1){
                PlayerActivity.songPosition += 1

            }else{
                PlayerActivity.songPosition = 0

            }

        }else{
            if (PlayerActivity.songPosition != 0) {
                PlayerActivity.songPosition -= 1

            } else {
                PlayerActivity.songPosition = PlayerActivity.musilistPA.size - 1

            }
        }
    }
}
