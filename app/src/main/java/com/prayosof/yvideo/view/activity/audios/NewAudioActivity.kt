package com.prayosof.yvideo.view.activity.audios

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prayosof.yvideo.R
import com.prayosof.yvideo.adapter.audioAdapter.TrackAdapter
import com.prayosof.yvideo.view.fragment.audio.AudioFragment
import denis.musicplayer.data.media.model.Track
import java.io.File
import java.util.*

class NewAudioActivity : AppCompatActivity() {

    companion object{
        @JvmField
        var Broadcast_PLAY_NEW_AUDIO: String = "PlayNewAudio"
       // var Broadcast_PLAY_NEW_AUDIO: String = "PlayNewAudio"
        var REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    }


    var selectedTrackData: ArrayList<Track>? = null
    var position = 0

    private var player: CustomMediaPlayerService? = null
    var serviceBound = false
    var audioList: ArrayList<Track>? = null

    var collapsingImageView: ImageView? = null

    var imageIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_audio)
        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
//        setSupportActionBar(toolbar)
        collapsingImageView = findViewById(R.id.collapsingImageView) as? ImageView

        loadCollapsingImage(imageIndex)
        if (null != intent.extras) {
            if (Objects.requireNonNull(Objects.requireNonNull(intent.extras)!!.getString("is_track_adapter")) == "track") {
                selectedTrackData?.clear()
                selectedTrackData = TrackAdapter.tracksData //(ArrayList<Track>) getIntent().getSerializableExtra("tracks");
            } else {
                selectedTrackData?.clear()
                selectedTrackData = AudioFragment.musicFiles
            }
        }


        position = intent.getIntExtra("position", -1)

        val currentFile = File(selectedTrackData?.get(position)?.data)
        if (currentFile.exists()) {
            // Toast.makeText(getApplicationContext(), "Exist", Toast.LENGTH_LONG).show();
//            val name = AudioPlayerActivity.selectedTrackData[position].title
//            song_name.setText(name)
//            song_artist.setText(AudioPlayerActivity.selectedTrackData[position].artist)
//            Log.e("position", position.toString() + "")
//            bindServiceConnection()
//            playAudio(position)
            //MusicService.MUSIC_PATH =  selectedTrackData.get(position).getData();
        } else {
            Toast.makeText(applicationContext, "File not Exist", Toast.LENGTH_LONG).show()
            return
        }


        if (checkAndRequestPermissions()) {
            loadAudioList()
        }


    }

    private fun loadAudioList() {
        loadAudio()
        initRecyclerView()
    }

    private fun checkAndRequestPermissions(): Boolean {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionReadPhoneState: Int = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            val permissionStorage: Int = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val listPermissionsNeeded: MutableList<String> = ArrayList()
            if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
            }
            if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            return if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), NewAudioActivity.REQUEST_ID_MULTIPLE_PERMISSIONS)
                false
            } else {
                true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        val TAG = "LOG_PERMISSION"
        Log.d(TAG, "Permission callback called-------")
        when (requestCode) {
            NewAudioActivity.REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> = HashMap()
                // Initialize the map with both permissions
                perms[Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    // Check for both permissions
                    if (perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Phone state and storage permissions granted")
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        loadAudioList()
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                      //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                            showDialogOK("Phone state and storage permissions required for this app",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                            DialogInterface.BUTTON_NEGATIVE -> {
                                            }
                                        }
                                    })
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show()
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }


    private fun initRecyclerView() {
        if (audioList != null && audioList!!.size > 0) {
            val recyclerView = findViewById<RecyclerView>(R.id.in_player_recyclerview) //findViewById(R.id.recyclerview)
            val adapter = RecyclerView_Adapter(audioList, application)
            recyclerView.setAdapter(adapter)
            recyclerView.setLayoutManager(LinearLayoutManager(this))

            recyclerView.addOnItemTouchListener(CustomTouchListener(this, object : onItemClickListener {
                override fun onClick(view: View?, index: Int) {
                    playAudio(index)
                }
            }))
        }
    }

    @SuppressLint("Recycle")
    private fun loadCollapsingImage(i: Int) {
        val array = resources.obtainTypedArray(R.array.images)
        collapsingImageView?.setImageDrawable(array.getDrawable(i))
        //collapsingImageView?.setImageDrawable(resources.getDrawable(R.drawable.brand_logo))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }


    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("serviceStatus", serviceBound)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("serviceStatus")
    }

    //Binding this Client to the AudioPlayer Service
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder: CustomMediaPlayerService.LocalBinder = service as CustomMediaPlayerService.LocalBinder
            player = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }


    private fun playAudio(audioIndex: Int) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            val storage = StorageUtil(applicationContext)
            storage.storeAudio(audioList)
            storage.storeAudioIndex(audioIndex)
            val playerIntent = Intent(this, CustomMediaPlayerService::class.java)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, BIND_AUTO_CREATE)
        } else {
            //Store the new audioIndex to SharedPreferences
            val storage = StorageUtil(applicationContext)
            storage.storeAudioIndex(audioIndex)

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            val broadcastIntent = Intent(NewAudioActivity.Broadcast_PLAY_NEW_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }

    /**
     * Load audio files using [ContentResolver]
     *
     * If this don't works for you, load the audio files to audioList Array your oun way
     */
    private fun loadAudio() {
        audioList = selectedTrackData
//        val contentResolver = contentResolver
//        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
//        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
//        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)
//        if (cursor != null && cursor.count > 0) {
//            audioList = ArrayList<MediaStore.Audio>()
//            while (cursor.moveToNext()) {
//                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
//                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
//                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
//                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
//
//                // Save to audioList
//                audioList!!.add(MediaStore.Audio(data, title, album, artist))
//            }
//        }
//        cursor?.close()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            //service is active
            player!!.stopSelf()
        }
    }
}
