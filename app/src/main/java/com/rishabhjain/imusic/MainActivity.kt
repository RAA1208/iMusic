package com.rishabhjain.imusic

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Audio.Media
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rishabhjain.imusic.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: RVAdapter

    companion object {
        var mainMusiclist: ArrayList<Music> = ArrayList()
        var musicListSearch = ArrayList<Music>()
        var search: Boolean = false
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_IMusic)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reqPermission()

//      Initialize toggle
        toggle =
            ActionBarDrawerToggle(this, binding.root, R.string.open_drawer, R.string.close_drawer)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


//      Setting onClick listeners on menu items
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.feedback_btn -> Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                R.id.setting_btn -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                R.id.about_btn -> Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                R.id.exit_btn -> {
                    val builder = MaterialAlertDialogBuilder(this)
                        .setTitle("Exit")
                        .setMessage("Do you want to exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            if (PlayerActivity.musicService != null) {
                                PlayerActivity.musicService!!.stopForeground(true)
                                PlayerActivity.musicService!!.mediaPlayer!!.release()
                                PlayerActivity.musicService = null
                            }
                            exitProcess(1)
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setBackgroundColor(R.color.dark_red)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setBackgroundColor(R.color.dark_red)

                }


            }
            true
        }


//      Setting onclick listeners on buttons
        binding.shufflebtn.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }

        binding.playlistbtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlaylistsActivity::class.java))
        }

        binding.favoritesbtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
        }

        search = false

//      Adding songs to musiclist
        mainMusiclist = getAllSongs()


//      Initialize recyclerView
        musicAdapter = RVAdapter(this, mainMusiclist)
        binding.recycyclerView.setHasFixedSize(true)
        binding.recycyclerView.setItemViewCacheSize(13)
        binding.recycyclerView.layoutManager = LinearLayoutManager(this)
        binding.recycyclerView.adapter = musicAdapter

//        retrieving favorites data using shared preferences
        FavoriteActivity.favSongslist = ArrayList()
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE)
        val jsonString =  editor.getString("FavoriteSongs", null)
        val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
        if (jsonString != null){
            val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
            FavoriteActivity.favSongslist.addAll(data)
        }


    }


    private fun reqPermission() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager())
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse("package: ${this.applicationContext.packageName}")
                    activityResultLauncher.launch(intent)

                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    activityResultLauncher.launch(intent)
                }
        } else if (VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    10
                )
            }
        }

    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager())
                    Toast.makeText(this, "Permmission Granted", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "Permmission Denied", Toast.LENGTH_SHORT).show()

            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    10
                )

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Range")
    private fun getAllSongs(): ArrayList<Music> {
        val templists = ArrayList<Music>()
        val selection = Media.IS_MUSIC + "!=0"
        val projection = arrayOf(
            Media._ID, Media.TITLE, Media.ALBUM, Media.ARTIST,
            Media.DURATION, Media.DATE_ADDED, Media.DATA
        )
        val cursor = this.contentResolver.query(
            Media.EXTERNAL_CONTENT_URI, projection, selection,
            null, null, null
        )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndex(Media._ID))
                    val title = cursor.getString(cursor.getColumnIndex(Media.TITLE))
                    val album = cursor.getString(cursor.getColumnIndex(Media.ALBUM))
                    val duration = cursor.getLong(cursor.getColumnIndex(Media.DURATION))
                    val artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST))
                    val path = cursor.getString(cursor.getColumnIndex(Media.DATA))
//                    val albumID = cursor.getString(cursor.getColumnIndex(Media.ALBUM_ID))
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val imgUri = Uri.withAppendedPath(uri, album).toString()
                    val music = Music(
                        id = id, title = title, album = album, duration = duration, artist = artist,
                        path = path, imgUri = imgUri
                    )
                    val file = File(music.path)
                    if (file.exists())
                        templists.add(music)

                } while (cursor.moveToNext())
                cursor.close()
            }
        }
        return templists

    }

    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.musicService!!.mediaPlayer!!.isPlaying && PlayerActivity.musicService != null) {
            PlayerActivity.musicService!!.stopForeground(true)
            PlayerActivity.musicService!!.mediaPlayer!!.release()
            PlayerActivity.musicService = null
            exitProcess(1)
        }
    }

//    storing favorites data using shared preferences
    override fun onResume() {
        super.onResume()
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavoriteActivity.favSongslist)
        editor.putString("FavoriteSongs", jsonString)
        editor.apply()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.searchview_menu, menu)
        val searchView = menu?.findItem(R.id.searchview)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in mainMusiclist) {
                        if (song.title.lowercase().contains(userInput)) {
                            musicListSearch.add(song)
                            search = true
                            musicAdapter.updateMusicList(searchList = musicListSearch)
                        }
                    }
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)

    }


}