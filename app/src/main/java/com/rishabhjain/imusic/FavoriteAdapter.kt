package com.rishabhjain.imusic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class FavoriteAdapter(private val context: Context, private var musicList: ArrayList<Music>): RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val songimg: ImageView = itemView.findViewById(R.id.favSongimg)
        val songname: TextView = itemView.findViewById(R.id.favSongName)
        val root = itemView.rootView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.favorite_view, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return musicList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.songname.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].imgUri)
            .apply(RequestOptions().placeholder(R.mipmap.music_icon)).centerCrop()
            .into(holder.songimg)

        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "FavoriteAdapter")
            ContextCompat.startActivity(context, intent, null)
        }



    }




}