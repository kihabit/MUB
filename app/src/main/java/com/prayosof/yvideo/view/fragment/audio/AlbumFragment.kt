package com.prayosof.yvideo.view.fragment.audio

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prayosof.yvideo.R
import com.prayosof.yvideo.adapter.audioAdapter.AlbumAdapter
import com.prayosof.yvideo.interfaces.OnSongClickedListner
import denis.musicplayer.data.media.model.Album
import denis.musicplayer.data.media.model.Track
import java.util.*

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class AlbumFragment : Fragment(), OnSongClickedListner {
    lateinit var recyclerView: RecyclerView
    var albumAdapter: AlbumAdapter? = null
    var listner: OnSongClickedListner = this
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View
        view = inflater.inflate(R.layout.fragment_album, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
            albumAdapter = AlbumAdapter(context, scanAlbums(), listner)
            recyclerView.setAdapter(albumAdapter)
        return view
    }

    fun scanAlbums(): ArrayList<Album> {
        val array = ArrayList<Album>()
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART
        )
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)).toLong()
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST))
                val cover = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
                cursor.moveToNext()
                array.add(Album(id, album, artist, cover))
            }
            cursor.close()
        }
        return array
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    fun scanAlbumTracks(albumId: Long): ArrayList<Track> {
        val array = ArrayList<Track>()
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
    override fun onClick(albumId: Long) {


//
      // val action =  AlbumFragmentDirections.actionAlbumFragmentToTracksFragment(albumId)
//
//navController.navigate(action)
        TracksFragment.newInstance(albumId, "album")
        val bundle = bundleOf("albumId" to albumId, "from_page" to "album")
        Navigation.findNavController(this!!.requireView()).navigate(R.id.tracksFragment, bundle)

        //Navigation.findNavController(this!!.requireView()).navigate(action)

        //Log.e("scanAlbumTracks", String.valueOf(scanAlbumTracks(albumId)));
        //Log.d("albumIddd", albumId+"");
    }
}