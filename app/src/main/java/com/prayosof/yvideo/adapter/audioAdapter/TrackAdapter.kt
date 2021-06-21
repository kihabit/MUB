package com.prayosof.yvideo.adapter.audioAdapter

import android.content.ContentUris
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.prayosof.yvideo.R
import com.prayosof.yvideo.helper.DialogHelper
import com.prayosof.yvideo.interfaces.CustomOnClickListener
import com.prayosof.yvideo.view.activity.AudioPlayerActivity
import denis.musicplayer.data.media.model.Track
import java.io.File

/**
 * Created by Yogesh Y. Nikam on 12/07/20.
 */




class TrackAdapter(mTrackData: ArrayList<Track>, context: Context, listner : CustomOnClickListener) :
        RecyclerView.Adapter<TrackAdapter.MyViewHolder>() {


    companion object {
        lateinit var tracksData: ArrayList<Track>
        const val TEA_NAME = "TEA_NAME"

    }

val mContext = context
val mListener  = listner
    var  trackData = mTrackData


    override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): TrackAdapter.MyViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return MyViewHolder(v)
    }


    override fun getItemCount() = trackData.size

    override fun onBindViewHolder(holder: TrackAdapter.MyViewHolder, position: Int) {

        holder.trackName?.setText(trackData.get(position).title)
        //holder.trackImg?.setText(trackData.get(position).title)

        holder.trackImg?.let {
            Glide.with(mContext)
                .load(R.drawable.music_headphone)
                .into(it)
        }
        holder.trackListMenu?.setOnClickListener{
            val popupMenu = PopupMenu(mContext, it)

            popupMenu.menu.add(1, R.id.delete, 1, "Delete")
            popupMenu.menu.add(1, R.id.share_file, 1, "Share")

            popupMenu.show()

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                if (item.itemId == R.id.delete) { //
                    //
                    DialogHelper.showYesNoDialog(mContext, "Are you sure You want to delete this file?") { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE ->                                             //Yes button clicked
                                deleteFile(position, it)
                            DialogInterface.BUTTON_NEGATIVE -> {
                            }
                        }
                    }
                    return@OnMenuItemClickListener true
                }
                if (item.itemId == R.id.share_file) {
                   mListener.onShareClick(trackData.get(position).title, trackData.get(position).data)
                    return@OnMenuItemClickListener true
                }
                false
            })
            //displaying the popup
            //displaying the popup
            popupMenu.show()
        }
        holder.itemView.setOnClickListener {
            tracksData = trackData

            val intent = Intent(mContext, AudioPlayerActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("is_track_adapter", "track")
            mContext.startActivity(intent)
//
//            val intent = Intent(mContext, PlayerActivity::class.java)
//            intent.putExtra("position", position)
//            intent.putExtra("is_track_adapter", "track")
//            mContext.startActivity(intent)
            //Toast.makeText(mContext, "position: "+position, Toast.LENGTH_LONG).show();
        }
    }

    private fun  deleteFile(position: Int, v: View) {
        val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, trackData.get(position).id) // get the uri content
        val file = File(trackData.get(position).data)
        val deleted: Boolean = file.delete() // delete the file
        if (deleted) {
            mContext.contentResolver.delete(contentUri, null, null)
            trackData.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, trackData.size)
            Snackbar.make(v, "File deleted", Snackbar.LENGTH_LONG).show()
        } else {
            // if file is in sd card or api level is 19
            Snackbar.make(v, "File can't be deleted : ", Snackbar.LENGTH_LONG).show()
        }
    }

    class MyViewHolder(v : View) :RecyclerView.ViewHolder(v) {

        var trackName: TextView? = null
        var trackImg: ImageView? = null
        var trackListMenu : ImageView? = null
        init {
            trackName = v.findViewById(R.id.music_file_name)

            trackImg = v.findViewById(R.id.music_img)
            trackListMenu = v.findViewById(R.id.trackListMenu)
        }

    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

}