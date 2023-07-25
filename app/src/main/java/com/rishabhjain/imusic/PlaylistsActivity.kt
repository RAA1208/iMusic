package com.rishabhjain.imusic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rishabhjain.imusic.databinding.ActivityPlaylistsBinding
import com.rishabhjain.imusic.databinding.AddPlaylistDialogBinding

class PlaylistsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistsBinding
    private lateinit var adapter: PlaylistsAdapter

    companion object{
        var musicPlaylist: MusicPlaylist = MusicPlaylist()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistsBinding.inflate(layoutInflater)
        setTheme(R.style.SecondaryTheme)
        setContentView(binding.root)

        binding.backBtnPlaylists.setOnClickListener { finish() }


        adapter = PlaylistsAdapter(this, musicPlaylist.ref)
        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this, 2)
        binding.playlistRV.adapter = adapter

        binding.playlistsAddBtn.setOnClickListener {customAlertDialog()}
    }

    private fun customAlertDialog(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog, binding.root, false)
        val binder = AddPlaylistDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
               .setTitle("Create New Playlists")
               .setPositiveButton("ADD"){dialog, _->
                   val playlistName = binder.playlistName.text

                   if (playlistName != null) {
                       if (playlistName.isNotEmpty()){
                           createPlaylist(playlistName.toString())
                       }
                   }
                   dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.grey)
    }

     private fun createPlaylist(playlistName: String) {
         var playlistExists = false

         for (i in musicPlaylist.ref){
             if (playlistName == i.name){
                 playlistExists = true
                 break
             }
         }
         if (playlistExists) Toast.makeText(this, "Playlist already exists", Toast.LENGTH_SHORT).show()
         else{
             val tempPlayList = Playlist()
             tempPlayList.name = playlistName
             tempPlayList.playlist = ArrayList()
             musicPlaylist.ref.add(tempPlayList)
             adapter.refreshPlalist()
         }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}