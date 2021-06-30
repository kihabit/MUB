package com.prayosof.yvideo.adapter.audioAdapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.DialogHelper;
import com.prayosof.yvideo.interfaces.CustomOnClickListener;
import com.prayosof.yvideo.view.activity.AudioPlayerActivity;

import java.io.File;
import java.util.ArrayList;

import denis.musicplayer.data.media.model.Track;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Track> mFiles;
    CustomOnClickListener mListener;

    public MusicAdapter(Context mContext, ArrayList<Track> mFiles, CustomOnClickListener listener) {
        this.mFiles = mFiles;
        this.mContext = mContext;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
//        byte[] image = getAlbumArt(mFiles.get(position).getPath());
//        if (image != null) {
//            Glide.with(mContext).asBitmap()
//                    .load(image)
//                    .into(holder.album_art);
//        } else {
//            Glide.with(mContext)
//                    .load(R.drawable.albumartsample)
//                    .into(holder.album_art);
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                Intent intent = new Intent(mContext, NewAudioActivity.class);
//                intent.putExtra("is_track_adapter", "music_adapter");
//                intent.putExtra("position", position);
//                mContext.startActivity(intent);


//                Intent intent = new Intent(mContext, TestActivity.class);
//                intent.putExtra("is_track_adapter", "music_adapter");
//                intent.putExtra("position", position);
//                mContext.startActivity(intent);



                Intent intent = new Intent(mContext, AudioPlayerActivity.class);
                intent.putExtra("is_track_adapter", "music_adapter");
                intent.putExtra("position", position);
                mContext.startActivity(intent);

//                Intent intent = new Intent(mContext, PlayerActivity.class);
//                intent.putExtra("is_track_adapter", "music_adapter");
//                intent.putExtra("position", position);
//                mContext.startActivity(intent);



                //Toast.makeText(mContext, "position: "+position, Toast.LENGTH_LONG).show();
            }
        });

        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PopupMenu popupMenu = new PopupMenu(mContext, v);

                popupMenu.getMenu().add(1, R.id.delete, 1, "Delete");
                popupMenu.getMenu().add(1, R.id.share_file, 1, "Share");

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {//
                            DialogHelper.showYesNoDialog(mContext, "Are you sure You want to delete this file?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
                                            deleteFile(position, v);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            });

                            return true;
                        }
                        if (item.getItemId() == R.id.share_file) {

                            mListener.onShareClick(mFiles.get(position).getTitle(), mFiles.get(position).getData());

                            return true;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popupMenu.show();


//                PopupMenu popupMenu = new PopupMenu(mContext, v);
//                popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
//
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.delete:
//                                Toast.makeText(mContext, "delete processing", Toast.LENGTH_SHORT).show();
//                                deleteFile(position, v);
//                                break;
//                        }
//                        return true;
//                    }
//                });
            }
        });
    }

    private void deleteFile(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mFiles.get(position).getId()); // get the uri content

        File file = new File(mFiles.get(position).getData());
        boolean deleted = file.delete();  // delete the file
        if (deleted) {
            try {
                mContext.getContentResolver().delete(contentUri, null, null);
                mFiles.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mFiles.size());
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            Snackbar.make(v, "File deleted", Snackbar.LENGTH_LONG).show();
        } else {
            // if file is in sd card or api level is 19
            Snackbar.make(v, "File can't be deleted : ", Snackbar.LENGTH_LONG).show();

        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView file_name;
        ImageView album_art, menuMore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menuMore = itemView.findViewById(R.id.songsMenu);
        }
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
