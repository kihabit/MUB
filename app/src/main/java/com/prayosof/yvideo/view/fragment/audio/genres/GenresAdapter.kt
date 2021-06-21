package com.prayosof.yvideo.view.fragment.audio.genres

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prayosof.yvideo.R
import com.prayosof.yvideo.interfaces.OnSongClickedListner
import denis.musicplayer.data.media.model.Genre

/**
 * Created by Yogesh Y. Nikam on 12/07/20.
 */

class GenresAdapter(mGenresList : ArrayList<Genre>, mContext: Context, listner: OnSongClickedListner) : RecyclerView.Adapter<GenresAdapter.MyHolder>() {
    lateinit var view: View
    val context : Context = mContext
    val genresList : ArrayList<Genre> = mGenresList
    val mListner :  OnSongClickedListner  = listner

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenresAdapter.MyHolder {
        view = LayoutInflater.from(context).inflate(R.layout.genres_item, parent, false)
        return GenresAdapter.MyHolder(view)
    }

    override fun getItemCount(): Int {
        return genresList.size
    }

    override fun onBindViewHolder(holder: GenresAdapter.MyHolder, position: Int) {
        holder.genres_name.text = genresList.get(position).name

        holder.itemView.setOnClickListener {
            mListner.onClick(genresList.get(position).id)
        }
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var genres_name: TextView


        init {
            genres_name = itemView.findViewById(R.id.genres_name)

        }
    }


}