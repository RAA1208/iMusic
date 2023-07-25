package com.rishabhjain.imusic

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rishabhjain.imusic.databinding.FragmentNowPlayingBinding

class NowPlayingFragment : Fragment() {
    @SuppressLint("StaticFieldLeak")
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE

        binding.nowPlayPause.setOnClickListener {
            if (PlayerActivity.musicService!!.mediaPlayer!!.isPlaying) {
                binding.nowPlayPause.setIconResource(R.drawable.play_icon)
                PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.play_icon)
                PlayerActivity.musicService!!.mediaPlayer!!.pause()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
                }

            } else {
                binding.nowPlayPause.setIconResource(R.drawable.pause_icon)
                PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
                PlayerActivity.musicService!!.mediaPlayer!!.start()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
                }
            }
        }

        binding.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.songPosition)
            intent.putExtra("class", "NowPlaying")
            startActivity(intent)
        }

        binding.nextBtn.setOnClickListener {
            setSongsPosition(increment = true)
            PlayerActivity.musicService!!.creatingMediaPlayer()

            Glide.with(this)
                .load(PlayerActivity.musilistPA[PlayerActivity.songPosition].imgUri)
                .apply(RequestOptions().placeholder(R.mipmap.music_icon)).centerCrop()
                .into(binding.nowplaySongimg)

            binding.nowplysongname.text = PlayerActivity.musilistPA[PlayerActivity.songPosition].title

            PlayerActivity.findex = favChecker(PlayerActivity.musilistPA[PlayerActivity.songPosition].id)
            if (PlayerActivity.isfavorite)
                PlayerActivity.binding.favBtn.setImageResource(R.drawable.favorite_icon)
            else
                PlayerActivity.binding.favBtn.setImageResource(R.drawable.favorite_border_icon)

            PlayerActivity.musicService!!.mediaPlayer!!.isPlaying
            PlayerActivity.musicService!!.mediaPlayer!!.start()
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
            binding.nowPlayPause.setIconResource(R.drawable.pause_icon)

        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            binding.nowplysongname.isSelected = true

            Glide.with(this)
                .load(PlayerActivity.musilistPA[PlayerActivity.songPosition].imgUri)
                .apply(RequestOptions().placeholder(R.mipmap.music_icon)).centerCrop()
                .into(binding.nowplaySongimg)

            binding.nowplysongname.text = PlayerActivity.musilistPA[PlayerActivity.songPosition].title

            if (PlayerActivity.musicService!!.mediaPlayer!!.isPlaying) {
                binding.nowPlayPause.setIconResource(R.drawable.pause_icon)

            } else {
                binding.nowPlayPause.setIconResource(R.drawable.play_icon)
            }





        }
    }


}