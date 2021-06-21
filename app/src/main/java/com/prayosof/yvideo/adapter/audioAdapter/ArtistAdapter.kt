package com.prayosof.yvideo.adapter.audioAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prayosof.yvideo.R
import com.prayosof.yvideo.interfaces.OnSongClickedListner
import denis.musicplayer.data.media.model.Artist
import java.util.*

/**
 * Created by Yogesh Y. Nikam on 05/07/20.
 */
class ArtistAdapter(private val mContext: Context, private val artistFiles: ArrayList<Artist>, var mListner: OnSongClickedListner) : RecyclerView.Adapter<ArtistAdapter.MyHolder>() {
    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        view = LayoutInflater.from(mContext).inflate(R.layout.artist_item, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.artist_name.text = artistFiles[position].artist
        holder.album_count.text = artistFiles[position].albumCount + " Albums"
        holder.track_count.text = artistFiles[position].trackCount + " Tracks"
        holder.itemView.setOnClickListener { mListner.onClick(artistFiles[position].id) }
    }

    override fun getItemCount(): Int {
        return artistFiles.size
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var artist_name: TextView
        var album_count: TextView
        var track_count: TextView

        init {
            artist_name = itemView.findViewById(R.id.artist_name)
            album_count = itemView.findViewById(R.id.artist_album_count)
            track_count = itemView.findViewById(R.id.artist_track_count)
        }
    }

}