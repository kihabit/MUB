package com.prayosof.yvideo.view.fragment.audio.genres

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.prayosof.yvideo.R
import com.prayosof.yvideo.interfaces.OnSongClickedListner
import com.prayosof.yvideo.view.fragment.audio.TracksFragment
import denis.musicplayer.data.media.model.Genre
import denis.musicplayer.data.media.model.Track

// TODO: Rename parameter arguments, choose names that match

class GenresFragment : Fragment() , OnSongClickedListner {

    lateinit var recyclerView: RecyclerView
    var genersAdapter: GenresAdapter? = null

    val listner : OnSongClickedListner = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_genres, container, false)

        recyclerView = root.findViewById(R.id.recyclerViewGenres)

        recyclerView.setHasFixedSize(true)

        var array : ArrayList<Genre> = scanGenres()

        Log.d("array", array.toString());
        genersAdapter = context?.let { GenresAdapter(array, requireContext(), listner) }
        recyclerView.setAdapter(genersAdapter)

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                GenresFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    override fun onClick(albumId: Long) {

        TracksFragment.newInstance(albumId, "genres")
        val bundle = bundleOf("albumId" to albumId, "from_page" to "genres")

        Navigation.findNavController(this.requireView()).navigate(R.id.tracksFragment, bundle)

    }

     @RequiresApi(Build.VERSION_CODES.Q)
     fun scanGenres(): ArrayList<Genre> {
        val array = ArrayList<Genre>()

        val uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
        )

        val sortOrder = "${MediaStore.Audio.Genres.NAME} ASC"

        val cursor = context?.contentResolver?.query(uri, projection, null, null, sortOrder)

        if(cursor != null) {
            cursor.moveToFirst()
            while(!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres._ID)).toLong()
                val name: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME))

                cursor.moveToNext()

                if (scanGenreTracks(id).size != 0) {
                    array.add(Genre(id, name))
                }


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
}