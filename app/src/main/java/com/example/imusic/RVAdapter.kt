package com.example.imusic

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

class RVAdapter(private val context:Context, private var musicList: ArrayList<Music>): RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val songImg: ImageView = itemView.findViewById(R.id.songImage)
        val title: TextView = itemView.findViewById(R.id.songName)
        val album: TextView = itemView.findViewById(R.id.moviewName)
        val duration: TextView = itemView.findViewById(R.id.songDuration)
        val rootLayout: LinearLayout = itemView.findViewById(R.id.linearLayoutItemsRV)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_music_view, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return musicList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatTimeDuration(musicList[position].duration)
        Glide.with(context)
             .load(musicList[position].imgUri)
             .apply(RequestOptions()
             .placeholder(R.mipmap.music_icon))
             .into(holder.songImg)

        holder.rootLayout.setOnClickListener {
           when {
               MainActivity.search -> setIntent(ref = "MainActivitySearch", pos = position)
               else -> setIntent(ref = "RVAdapter", pos = position)
           }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

   private fun setIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)

    }
}