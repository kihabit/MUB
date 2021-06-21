package com.prayosof.yvideo.adapter.audioAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.interfaces.OnSongClickedListner;
import com.prayosof.yvideo.model.MusicFiles;

import java.util.ArrayList;
import java.util.Objects;

import denis.musicplayer.data.media.model.Album;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<Album> albumFiles;

    OnSongClickedListner mListner;
    View view;
    public AlbumAdapter(Context mContext, ArrayList<Album> albumFiles, OnSongClickedListner listner) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
        this.mListner=  listner;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.album_name.setText(albumFiles.get(position).getAlbum());


        if (albumFiles.get(position).getArt() != null) {
            Glide.with(mContext).asBitmap()
                    .load(albumFiles.get(position).getArt())
                    .into(holder.album_image);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.brand_logo)
                    .into(holder.album_image);
        }


//        byte[] image = getAlbumArt(albumFiles.get(position).getArt());
//        if (image != null) {
//            Glide.with(mContext).asBitmap()
//                    .load(image)
//                    .into(holder.album_image);
//        } else {
//            Glide.with(mContext)
//                    .load(R.drawable.albumartsample)
//                    .into(holder.album_image);
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListner.onClick(albumFiles.get(position).getId());
              }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView album_name;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.album_image);
            album_name = itemView.findViewById(R.id.album_name);
        }
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;

//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        mmr.setDataSource(uri);
//        byte[] artBytes =  mmr.getEmbeddedPicture();
//        if(artBytes != null)
//        {
//            InputStream is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
//            Bitmap bm = BitmapFactory.decodeStream(is);
//            //imgArt.setImageBitmap(bm);
//            return artBytes;
//        }
//        else
//        {
//            //imgArt.setImageDrawable(getResources().getDrawable(R.drawable.adele));
//            return artBytes;
//        }
    }
}
