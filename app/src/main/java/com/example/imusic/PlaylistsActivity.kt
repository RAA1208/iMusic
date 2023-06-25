package com.example.imusic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.imusic.databinding.ActivityPlaylistsBinding

class PlaylistsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistsBinding.inflate(layoutInflater)
        setTheme(R.style.SecondaryTheme)
        setContentView(binding.root)

        binding.backBtnPlaylists.setOnClickListener { finish() }
    }
}