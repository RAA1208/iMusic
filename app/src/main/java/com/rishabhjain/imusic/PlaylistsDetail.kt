package com.rishabhjain.imusic

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.rishabhjain.imusic.databinding.ActivityPlaylistsDetailBinding

class PlaylistsDetail : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistsDetailBinding
    private lateinit var adapter: RVAdapter

    companion object{
      var  currentPlaylistPos: Int = -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistsDetailBinding.inflate(layoutInflater)
        setTheme(R.style.SecondaryTheme)
        setContentView(binding.root)
       currentPlaylistPos =  intent.extras?.getInt("index") as Int


        binding.playlistDetailRV.setItemViewCacheSize(10)
        binding.playlistDetailRV.setHasFixedSize(true)
        binding.playlistDetailRV.layoutManager = LinearLayoutManager(this)
        adapter = RVAdapter(this, PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist, playlistDetails = true)
        binding.playlistDetailRV.adapter = adapter
        binding.backBtnPlaylistsD.setOnClickListener { finish() }
        binding.playlistsDetailShuffleBtn.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.songAddBtnPA.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.songRemoveBtnPA.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs from playlist?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = PlaylistsActivity.musicPlaylist.ref[currentPlaylistPos].name
        if(adapter.itemCount > 0)
        {
            binding.playlistsDetailShuffleBtn.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistsActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)
        editor.apply()
    }
}

