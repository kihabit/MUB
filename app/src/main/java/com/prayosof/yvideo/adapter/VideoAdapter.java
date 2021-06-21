package com.prayosof.yvideo.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.DialogHelper;
import com.prayosof.yvideo.model.VideoModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Yogesh Y. Nikam on 05/07/20.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.viewHolder> {

    Context context;
    ArrayList<VideoModel> videoArrayList;
    public OnItemClickListener onItemClickListener;

    public VideoAdapter (Context context, ArrayList<VideoModel > videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @Override
    public VideoAdapter .viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoAdapter .viewHolder holder, final int i) {
        holder.title.setText(videoArrayList.get(i).getVideoTitle());
        holder.duration.setText(videoArrayList.get(i).getVideoDuration());

        holder.count.setText("Number of Videos: "+ videoArrayList.get(i).getDoubleCount());

        Glide.with(context).load("file://" + videoArrayList.get(i).getVideoUri())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.iv_image);
        holder.videoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


//
//                ArrayList<String> menuItem = new ArrayList<String>() ;
//                PopupMenu popupMenu = new PopupMenu(this,view);
//                MenuInflater inflater = popupMenu.getMenuInflater();
//                inflater.inflate(R.menu.main, popupMenu.getMenu());
//
//                for(int i = 0; i < 30; i = i+2){
//                    // menuItem =
//                    popupMenu.getMenu().add(menuItem.get(i));
//                }
//                popupMenu.show();

                PopupMenu popupMenu = new PopupMenu(context, view);

                popupMenu.getMenu().add(1, R.id.delete, 1, "Delete");

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {//
                            DialogHelper.showYesNoDialog(context, "Are you sure You want to delete this file?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
                                            Toast.makeText(context, "Delete Video", Toast.LENGTH_LONG).show();
                                            // handle menu1 click
                                            deleteFile(i,view);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            });

                            return true;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popupMenu.show();

            }
        });
    }
    private void deleteFile(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoArrayList.get(position).getId()); // get the uri content

        File file = new File(videoArrayList.get(position).getVideoUri().getPath());
        boolean deleted = file.delete();  // delete the file
        if (deleted) {
            context.getContentResolver().delete(contentUri, null, null);
            videoArrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, videoArrayList.size());
            Snackbar.make(v, "File deleted", Snackbar.LENGTH_LONG).show();
        } else {
            // if file is in sd card or api level is 19
            Snackbar.make(v, "File can't be deleted : ", Snackbar.LENGTH_LONG).show();

        }
    }
    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, duration, count;
        ImageView iv_image, videoMenu;
        public viewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            duration = (TextView) itemView.findViewById(R.id.duration);
            count = (TextView) itemView.findViewById(R.id.count);
            iv_image = (ImageView) itemView.findViewById(R.id.image);
            videoMenu = (ImageView) itemView.findViewById(R.id.videoMenu);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }

    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}