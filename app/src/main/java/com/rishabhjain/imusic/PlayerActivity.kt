package com.rishabhjain.imusic

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rishabhjain.imusic.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity(), ServiceConnection , MediaPlayer.OnCompletionListener{

    companion object {
       lateinit var musilistPA: ArrayList<Music>
        var songPosition: Int = 0
        var musicService: MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var isfavorite: Boolean = false
        var findex = -1
        var nowPlayingId: String = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setTheme(R.style.SecondaryTheme)
        setContentView(binding.root)

        iniializeMediaPlayer()
        playPauseButton()

        binding.songName.isSelected = true

        binding.backBtnPA.setOnClickListener { finish() }
        binding.nextBtn.setOnClickListener { nextPreviousBtn(increment = true) }
        binding.previousBtn.setOnClickListener { nextPreviousBtn(increment = false) }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    musicService!!.mediaPlayer!!.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        binding.repeatBtn.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.dark_yellow))
                Toast.makeText(this, "Repeat mode on", Toast.LENGTH_SHORT).show()
            }else{
                repeat = false
                binding.repeatBtn.setColorFilter(ContextCompat.getColor(this, R.color.dark_red))
                Toast.makeText(this, "Repeat mode off", Toast.LENGTH_SHORT).show()
            }
        }

        binding.shareBtn.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musilistPA[songPosition].path))
            val chooser = Intent.createChooser(shareIntent, "Audio is sharing to: ")
            startActivity(chooser)
        }

        binding.favBtn.setOnClickListener {
            if (isfavorite){
                isfavorite = false
                binding.favBtn.setImageResource(R.drawable.favorite_border_icon)
                FavoriteActivity.favSongslist.removeAt(findex)
                Toast.makeText(this, "Song removed from favorites", Toast.LENGTH_SHORT).show()
            }else{
                isfavorite = true
                binding.favBtn.setImageResource(R.drawable.favorite_icon)
                FavoriteActivity.favSongslist.add(musilistPA[songPosition])
                Toast.makeText(this, "Song added to favorites", Toast.LENGTH_SHORT).show()
            }
        }





    }

    private fun iniializeMediaPlayer() {
        songPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "RVAdapter" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)

                musilistPA = ArrayList()
                musilistPA.addAll(MainActivity.mainMusiclist)
                setSongImageandLayout()

            }
            "MainActivitySearch" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)

                musilistPA = ArrayList()
                musilistPA.addAll(MainActivity.musicListSearch)
                setSongImageandLayout()

            }
            "MainActivity" -> {
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)

                musilistPA = ArrayList()
                musilistPA.addAll(MainActivity.mainMusiclist)
                musilistPA.shuffle()
                setSongImageandLayout()

            }
            "FavoriteAdapter" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)

                musilistPA = ArrayList()
                musilistPA.addAll(FavoriteActivity.favSongslist)
                setSongImageandLayout()

            }
            "FavoriteActivity" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)

                musilistPA = ArrayList()
                musilistPA.addAll(FavoriteActivity.favSongslist)
                musilistPA.shuffle()
                setSongImageandLayout()

            }
            "NowPlaying" ->{
                binding.tvSeekbarStart.text = formatTimeDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvSeekbarEnd.text = formatTimeDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seekbar.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seekbar.max = musicService!!.mediaPlayer!!.duration
                binding.songName.text = musilistPA[songPosition].title
                NowPlayingFragment.binding.nowplysongname.text = musilistPA[songPosition].title
                setSongImageandLayout()
                if (musicService!!.mediaPlayer!!.isPlaying){
                    binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
                }else
                    binding.playPauseBtn.setImageResource(R.drawable.play_icon)

            }
            "PlaylistDetailsAdapter" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)

                musilistPA = ArrayList()
                musilistPA.addAll(PlaylistsActivity.musicPlaylist.ref[PlaylistsDetail.currentPlaylistPos].playlist)
                setSongImageandLayout()

            }
            "PlaylistDetailsShuffle" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)

                musilistPA = ArrayList()
                musilistPA.addAll(PlaylistsActivity.musicPlaylist.ref[PlaylistsDetail.currentPlaylistPos].playlist)
                musilistPA.shuffle()
                setSongImageandLayout()

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    private fun creatingMediaPlayer() {
        if (musicService!!.mediaPlayer == null)
            musicService!!.mediaPlayer = MediaPlayer()
        else musicService!!.mediaPlayer!!.reset()
        musicService!!.mediaPlayer!!.setDataSource(musilistPA[songPosition].path)
        musicService!!.mediaPlayer!!.prepare()
        musicService!!.mediaPlayer!!.start()
        binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
        binding.songName.text = musilistPA[songPosition].title
        musicService!!.showNotification(R.drawable.pause_icon)
        binding.tvSeekbarStart.text = formatTimeDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
        binding.tvSeekbarEnd.text = formatTimeDuration(musicService!!.mediaPlayer!!.duration.toLong())
        binding.seekbar.progress = 0
        binding.seekbar.max = musicService!!.mediaPlayer!!.duration
        musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        nowPlayingId = musilistPA[songPosition].id


    }

    private fun setSongImageandLayout() {
        findex = favChecker(musilistPA[songPosition].id)
        Glide.with(this)
            .load(musilistPA[songPosition].imgUri)
            .apply(RequestOptions().placeholder(R.mipmap.music_icon)).centerCrop()
            .into(binding.songImg)

        if (isfavorite)
            binding.favBtn.setImageResource(R.drawable.favorite_icon)
        else
            binding.favBtn.setImageResource(R.drawable.favorite_border_icon)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun playPauseButton() {
        binding.playPauseBtn.setOnClickListener {
            if (musicService!!.mediaPlayer!!.isPlaying) {
                binding.playPauseBtn.setImageResource(R.drawable.play_icon)
                musicService!!.mediaPlayer!!.pause()
                NowPlayingFragment.binding.nowPlayPause.setIconResource(R.drawable.play_icon)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    musicService!!.showNotification(R.drawable.play_icon)
                }

            } else {
                binding.playPauseBtn.setImageResource(R.drawable.pause_icon)
                musicService!!.mediaPlayer!!.start()
                NowPlayingFragment.binding.nowPlayPause.setIconResource(R.drawable.pause_icon)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    musicService!!.showNotification(R.drawable.pause_icon)
                }
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun nextPreviousBtn(increment: Boolean) {
        if(!repeat){
            if (increment) {
                if (songPosition != musilistPA.size - 1) {
                    songPosition += 1

                } else {
                    songPosition = 0

                }
                setSongImageandLayout()
                creatingMediaPlayer()

            } else {
                if (songPosition != 0) {
                    songPosition -= 1

                } else {
                    songPosition = musilistPA.size - 1

                }
                setSongImageandLayout()
                creatingMediaPlayer()
            }
        }else{
            Toast.makeText(this, "Please turn off repeat mode", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        creatingMediaPlayer()
        musicService!!.seekbarProgress()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)


    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCompletion(mp: MediaPlayer?) {
        setSongsPosition(increment = true)
        creatingMediaPlayer()
    }




}