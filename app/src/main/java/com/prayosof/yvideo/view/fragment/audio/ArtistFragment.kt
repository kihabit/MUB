package com.prayosof.yvideo.view.fragment.audio

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.prayosof.yvideo.R
import com.prayosof.yvideo.adapter.audioAdapter.ArtistAdapter
import com.prayosof.yvideo.interfaces.OnSongClickedListner
import denis.musicplayer.data.media.model.Artist

class ArtistFragment : Fragment(), OnSongClickedListner {
   lateinit var recyclerView: RecyclerView
    var artistAdapter: ArtistAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_artist, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewArtist)
        recyclerView.setHasFixedSize(true)

        var array : ArrayList<Artist> = scanArtists()

        Log.d("array", array.toString());
            artistAdapter = context?.let { ArtistAdapter(it, array, this) }
            recyclerView.setAdapter(artistAdapter)

        return view
    }

    fun scanArtists(): ArrayList<Artist> {
        val array = ArrayList<Artist>()

        val uri: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )

        val sortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"

        val cursor: Cursor? = context?.contentResolver?.query(uri, projection, null, null, sortOrder)

        if(cursor != null) {
            cursor.moveToFirst()
            while(!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)).toLong()
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                val albumsCount = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS))
                val tracksCount = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))

                cursor.moveToNext()

                array.add(Artist(id, artist, albumsCount, tracksCount))
            }

            cursor.close()
        }

        return array
    }

    override fun onClick(albumId: Long) {
        TracksFragment.newInstance(albumId, "artist")
        val bundle = bundleOf("albumId" to albumId, "from_page" to "artist")

        Navigation.findNavController(this!!.requireView()).navigate(R.id.tracksFragment, bundle)
    }


}