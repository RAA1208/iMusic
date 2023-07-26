package com.rishabhjain.imusic

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistsAdapter(private val context: Context, private var playList: ArrayList<Playlist>): RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val playListimg: ImageView = itemView.findViewById(R.id.playlistImg)
        val playlistName: TextView = itemView.findViewById(R.id.playlistName)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.playlistDeleteBtn)
        val root = itemView.rootView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.playlists_view, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return playList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.playlistName.text = playList[position].name
        holder.playlistName.isSelected = true
        holder.deleteBtn.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playList[position].name)
                .setMessage("Do you want to delete this playlist")
                .setPositiveButton("YES"){dialog, _ ->
                    PlaylistsActivity.musicPlaylist.ref.removeAt(position)
                    refreshPlalist()
                    dialog.dismiss()
                }
                .setNegativeButton("NO"){dialog,_ ->
                    dialog.dismiss()
                }

            val customDialog = builder.create()
            customDialog.show()
            customDialog.window?.setBackgroundDrawableResource(R.color.grey)
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        }

        holder.root.setOnClickListener {
            val intent = Intent(context, PlaylistsDetail::class.java )
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }

        if(PlaylistsActivity.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context)
                .load(PlaylistsActivity.musicPlaylist.ref[position].playlist[0].imgUri)
                .apply(RequestOptions().placeholder(R.mipmap.music_icon).centerCrop())
                .into(holder.playListimg)
        }


//        Glide.with(context)
//            .load(playList[position])
//            .apply(RequestOptions().placeholder(R.mipmap.music_icon)).centerCrop()
//            .into(holder.playListimg)
//
//        holder.root.setOnClickListener {
//            val intent = Intent(context, PlayerActivity::class.java)
//            intent.putExtra("index", position)
//            intent.putExtra("class", "FavoriteAdapter")
//            ContextCompat.startActivity(context, intent, null)
        }

    fun refreshPlalist(){
        playList = ArrayList()
        playList.addAll(PlaylistsActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }



    }




