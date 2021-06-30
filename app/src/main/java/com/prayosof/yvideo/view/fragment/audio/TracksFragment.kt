package com.prayosof.yvideo.view.fragment.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.prayosof.yvideo.R
import com.prayosof.yvideo.adapter.audioAdapter.TrackAdapter
import com.prayosof.yvideo.interfaces.CustomOnClickListener
import com.prayosof.yvideo.interfaces.Playable
import com.prayosof.yvideo.model.ModelAudios
import com.prayosof.yvideo.model.MusicFiles
import com.prayosof.yvideo.services.NewMediaPlayerService
import com.prayosof.yvideo.view.activity.AudioPlayerActivity
import com.prayosof.yvideo.view.fragment.BaseFragment
import denis.musicplayer.data.media.model.Track
import java.lang.IllegalStateException

open class TracksFragment : BaseFragment(), CustomOnClickListener, Playable, View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var audiosFromFolders: ModelAudios

    open var musicFiles: java.util.ArrayList<MusicFiles>? = null

    private  var _id : Long = 0
    private  var _trackId : Long = 0
    private var fromPage : String = ""

    lateinit var listner : CustomOnClickListener


    //MINI PLAYER
    lateinit var player: NewMediaPlayerService
    lateinit var miniPlayerSeekbar: SeekBar
    lateinit var includeMiniPlayer: MaterialCardView

    lateinit var playerIntent: Intent
    var serviceBound = false
    lateinit var mPlayable: Playable
    lateinit var mpButtonPrevious: ImageButton
    lateinit var mpCurrentSongPauseBtn:ImageButton
    lateinit var mpButtonNext:ImageButton
    lateinit var mSeekbarUpdater: Runnable
    var handler = Handler()
    lateinit var mpSongTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey("folderTracks")) {
                audiosFromFolders = it.getSerializable("folderTracks") as ModelAudios
            }

            _id = it.getLong("albumId")
            _trackId = it.getLong("albumId")
            fromPage = it.getString("from_page")!!
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_tracks, container, false)

        val trackArray :  ArrayList<Track>

        listner = this
        val trackRecyclerView = root.findViewById<RecyclerView>(R.id.trackRecyclerView)
        if (fromPage == "artist") {

            trackArray = scanArtistTracks(_trackId)
            val viewAdapter = TrackAdapter(trackArray, requireContext(), listner)
            trackRecyclerView.adapter = viewAdapter

        } else if(fromPage == "genres") {

            trackArray = scanGenreTracks(_trackId)
            val viewAdapter = TrackAdapter(trackArray, requireContext(), listner)
            trackRecyclerView.adapter = viewAdapter

        } else if(fromPage == "folders") {
            trackArray = scanFolders(audiosFromFolders)
            val viewAdapter = TrackAdapter(trackArray, requireContext(), listner)

            trackRecyclerView.adapter = viewAdapter

        } else{
            trackArray = scanAlbumTracks(_trackId)

            val viewAdapter = TrackAdapter(trackArray, requireContext(), listner)


            trackRecyclerView.adapter = viewAdapter
        }

        //val trackArray = scanAlbumTracks(_trackId)

        mPlayable = this
        includeMiniPlayer = root.findViewById(R.id.mini_player_cardview)
        includeMiniPlayer.setOnClickListener(this)
        includeMiniPlayer.visibility = View.GONE

        mpButtonPrevious = root.findViewById(R.id.mpButtonPrevious)
        mpCurrentSongPauseBtn = root.findViewById(R.id.mpCurrentSongPauseBtn)
        mpButtonNext = root.findViewById(R.id.mpButtonNext)

        mpButtonPrevious.setOnClickListener(this)
        mpCurrentSongPauseBtn.setOnClickListener(this)
        mpButtonNext.setOnClickListener(this)

        miniPlayerSeekbar = root.findViewById(R.id.miniPlayerSeekbar)

        mpSongTitle = root.findViewById(R.id.mpSongTitle)

        return root

    }

    companion object {
        @JvmStatic
        fun newInstance(id: Long, from_page: String) =
                TracksFragment().apply {
                    arguments = Bundle().apply {
                        putLong("albumId", id)
                        putString("from_page", from_page)

                    }
                }

        @JvmStatic
        fun newInstanceFolders(tracks: ModelAudios, from_page: String) =
                TracksFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("folderTracks", tracks)
                        putString("from_page", from_page)

                    }
                }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun scanAlbumTracks(albumId: Long): java.util.ArrayList<Track> {
        val array = java.util.ArrayList<Track>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        )
        //         String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME+"";
        val selection = MediaStore.Audio.Media.ALBUM_ID + "=" + albumId
        val cursor = requireContext().contentResolver.query(uri, projection, selection, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)).toLong()
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                //String duration = Track.convertDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                val mAlbumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toLong()
                cursor.moveToNext()
                array.add(Track(id, title, artist, data, duration, mAlbumId))
            }
            cursor.close()
        }
        return array
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun scanFolders(audios: ModelAudios): java.util.ArrayList<Track> {
        val array = java.util.ArrayList<Track>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        )
        //         String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME+"";
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)).toLong()
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                var duration = ""
                try {
                    duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                }catch (e : IllegalStateException){
                    println(e)
                }
                //String duration = Track.convertDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
//                 var mAlbumId : Long = 0
//                if(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID) != null) {
//                     mAlbumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toLong()
//                }

                val mAlbumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                try {
                    if (mAlbumId != null) {
                        if (audios.allAudioPaths.contains(data)) {
                            array.add(Track(id, title, artist, data, duration, mAlbumId.toLong()))
                        }

                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

                cursor.moveToNext()



            }
            cursor.close()
        }
        return array
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun scanArtistTracks(artistID: Long): ArrayList<Track> {
        val array = ArrayList<Track>()

        val uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID)

        val selection = "${MediaStore.Audio.Media.ARTIST_ID} == $artistID"

        val sortOrder = "${MediaStore.Audio.AudioColumns.TITLE} COLLATE LOCALIZED ASC"

        val cursor  = context?.contentResolver?.query(uri, projection, selection, null, sortOrder)

        if(cursor != null) {
            cursor.moveToFirst()
            while(!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)).toLong()
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val duration = Track.convertDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)).toLong())
                val albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toLong()

                cursor.moveToNext()

                array.add(Track(id, title, artist, data, duration, albumId))
            }

            cursor.close()
        }

        return array
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun scanGenreTracks(genreID: Long): ArrayList<Track> {
        val array = ArrayList<Track>()

        val uri = android.provider.MediaStore.Audio.Genres.Members.getContentUri("external", genreID)

        val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID)

        val selection = "${MediaStore.Audio.Media.IS_MUSIC}  != 0"

        val sortOrder = "${MediaStore.Audio.AudioColumns.TITLE} COLLATE LOCALIZED ASC"

        val cursor = context?.contentResolver?.query(uri, projection, selection, null, sortOrder)

        if(cursor != null) {
            cursor.moveToFirst()
            while(!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)).toLong()
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val duration = Track.convertDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)).toLong())
                val albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toLong()

                cursor.moveToNext()

                array.add(Track(id, title, artist, data, duration, albumId))
            }

            cursor.close()
        }

        return array
    }

    override fun onShareClick(fileName: String?, filePath: String?) {
        filePath?.let { startFileShareIntent(it) }
    }


    //    private ServiceConnection serviceConnection = new ServiceConnection() {
    //        @Override
    //        public void onServiceConnected(ComponentName name, IBinder service) {
    //            musicService = ((MusicService.MyBinder) (service)).getService();
    //        }
    //
    //        @Override
    //        public void onServiceDisconnected(ComponentName name) {
    //            musicService = null;
    //        }
    //    };
    override fun onResume() {
        super.onResume()
        playerIntent = Intent(requireActivity(), NewMediaPlayerService::class.java)
        if (checkIsPlayerRunning()) {
            includeMiniPlayer.setVisibility(View.VISIBLE)
            startMiniPlayer()
        } else {
            includeMiniPlayer.setVisibility(View.GONE)
        }
    }

    open fun startMiniPlayer() {
        val serviceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                val binder = service as NewMediaPlayerService.LocalBinder
                player = binder.service
                player.registerCallBack(mPlayable)
                serviceBound = true
                if (NewMediaPlayerService.isplaying()) {
                    mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_icon_pause)
                } else {
                    mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_icon_play)
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
                serviceBound = false
            }
        }
        requireActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        mSeekbarUpdater = Runnable { // user interface interactions and updates on screen
            if (player != null) {
                miniPlayerSeekbar.setProgress(player.getCurrentPosition())
                miniPlayerSeekbar.setMax(player.getDuration())
            }
            handler.postDelayed(mSeekbarUpdater, 200)
        }
        mpSongTitle.setText(NewMediaPlayerService.getSongTitle())
        handler.postDelayed(mSeekbarUpdater, 1000)
        miniPlayerSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }


    open fun checkIsPlayerRunning(): Boolean {
        return NewMediaPlayerService.isRunning()
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.mpButtonNext -> player.skipToNext()
            R.id.mpButtonPrevious -> player.skipToPrevious()
            R.id.mpCurrentSongPauseBtn -> player.playOrPause()
            R.id.mini_player_cardview -> {
                val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
                intent.putExtra("is_track_adapter", "from_mini_player")
                //intent.putExtra("position", position);
                requireActivity().startActivity(intent)
            }
        }
    }

    override fun onTrackPrevious() {}

    override fun onTrackPlay(songName: String?) {
        mpSongTitle.setText(songName)
        mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_icon_pause)
        miniPlayerSeekbar.setProgress(player.getCurrentPosition())
        miniPlayerSeekbar.setMax(player.getDuration())
    }

    override fun onTrackPause() {
        mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_controls_play)
        miniPlayerSeekbar.setProgress(player.getCurrentPosition())
        miniPlayerSeekbar.setMax(player.getDuration())
    }

    override fun onTrackNext(track: Track) {
        mpSongTitle.setText(track.title)
    }

    override fun onTrackResume() {
        mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_icon_pause)
        miniPlayerSeekbar.setProgress(player.getCurrentPosition())
        miniPlayerSeekbar.setMax(player.getDuration())
    }

    override fun onServiceStoped() {
        includeMiniPlayer.visibility = View.GONE
    }
}