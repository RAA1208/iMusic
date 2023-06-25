package com.example.imusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imusic.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setTheme(R.style.SecondaryTheme)
        setContentView(binding.root)

        binding.backBtnFavorites.setOnClickListener { finish() }


    }
}