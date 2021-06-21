package com.prayosof.yvideo.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.DialogHelper;
import com.prayosof.yvideo.helper.SessionManager;
import com.prayosof.yvideo.interfaces.CustomOnClickListener;
import com.prayosof.yvideo.model.AllMediaListModel;
import com.prayosof.yvideo.view.activity.video.CustomExoPlayerActivity;
import com.prayosof.yvideo.view.activity.video.VideoPlayerActivity;
import com.prayosof.yvideo.view.fragment.video.MainVideoFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.prayosof.yvideo.view.fragment.video.MainVideoFragment.count;
import static com.prayosof.yvideo.view.fragment.video.MainVideoFragment.ivDelete;
import static com.prayosof.yvideo.view.fragment.video.MainVideoFragment.type;


public class AllMediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.ViewHolder> {

    public static ArrayList<AllMediaListModel> videosModelList = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<String>();
    private Context mContext;
    private int pos;
    private CustomOnClickListener mListener;
    private SessionManager sessionManager;
    private int lastSelectedPosition = -1;

    public AllMediaListAdapter() {

    }

    public AllMediaListAdapter(Context context, ArrayList<AllMediaListModel> allVideoPath, int int_position, CustomOnClickListener listener) {
        this.mContext = context;
        this.videosModelList = allVideoPath;
        this.pos = int_position;
        this.mListener = listener;
        this.sessionManager = new SessionManager(mContext);
    }

    @NonNull
    @Override
    public MediaListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_list_row, parent, false);
        MediaListAdapter.ViewHolder viewHolder = new MediaListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MediaListAdapter.ViewHolder holder, final int position) {
        list.add(videosModelList.get(position).getFileName());

        try {
            Glide.with(mContext).load(videosModelList.get(position).getFileName())
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.iv_image);
        } catch (Exception ioe) {

        }

        String filename = "";
        try {
            filename = videosModelList.get(position).getFileName()
                    .substring(videosModelList.get(position).getFileName().lastIndexOf("/") + 1);
            holder.tvFileName.setText(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String finalFilename = filename;
        holder.videoListMenu.setOnClickListener(new View.OnClickListener() {
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
                                    switch (which) {
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
                            mListener.onShareClick(finalFilename, videosModelList.get(position).getFileName());

                            return true;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popupMenu.show();
            }
        });

        holder.cbDelete.setVisibility(View.VISIBLE);
        holder.cbDelete.setChecked(videosModelList.get(position).isSelected());
        holder.cbDelete.setTag(videosModelList.get(position));

//        holder.itemView.setBackgroundColor(videosModelList.get(position).isSelected() ? Color.argb(70, 0, 200, 200) : Color.TRANSPARENT);
//        holder.iv_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (lastSelectedPosition > 0) {
//                    videosModelList.get(lastSelectedPosition).setSelected(false);
//                }
//                videosModelList.get(position).setSelected(!videosModelList.get(position).isSelected());
//                holder.itemView.setBackgroundColor(videosModelList.get(position).isSelected() ? Color.argb(70, 0, 200, 200) : Color.TRANSPARENT);
//
//                lastSelectedPosition = holder.getAdapterPosition();
//
//                ivDelete.setClickable(false);
//                ivDelete.setEnabled(false);
//                ivDelete.setColorFilter(mContext.getResources().getColor(R.color.black));
//
//                for (int i = 0; i < videosModelList.size(); i++) {
//                    if (videosModelList.get(i).isSelected()) {
//                        ivDelete.setClickable(true);
//                        ivDelete.setEnabled(true);
//                        ivDelete.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
//                    }
//                }
//            }
//        });

        holder.cbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videosModelList.get(position).isSelected()) {
                    videosModelList.get(position).setSelected(false);
                    count--;
                } else {
                    videosModelList.get(position).setSelected(true);
                    count++;
                    type = "videos";
                }
                notifyItemChanged(holder.getAdapterPosition());
//                AllMediaListModel models = (AllMediaListModel) holder.cbDelete.getTag();
//
//                if (videosModelList.get(position).isSelected()) {
//                    MainVideoFragment.count--;
//                    models.setSelected(holder.cbDelete.isChecked());
//                    videosModelList.get(position).setSelected(holder.cbDelete.isChecked());
//                } else {
//                    models.setSelected(holder.cbDelete.isChecked());
//                    videosModelList.get(position).setSelected(holder.cbDelete.isChecked());
//                    for (int j = 0; j < videosModelList.size(); j++) {
//                        if (videosModelList.get(j).isSelected()) {
//                            MainVideoFragment.type = "videos";
//                            MainVideoFragment.count++;
//                            ivDelete.setClickable(true);
//                            ivDelete.setEnabled(true);
//                            ivDelete.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
//                        }
//                    }
//                }
                if (count == 0 || count < 0) {
                    MainVideoFragment.ivDelete.setClickable(false);
                    MainVideoFragment.ivDelete.setEnabled(false);
                    MainVideoFragment.ivDelete.setColorFilter(mContext.getResources().getColor(R.color.black));
                } else {
                    MainVideoFragment.ivDelete.setClickable(true);
                    MainVideoFragment.ivDelete.setEnabled(true);
                    MainVideoFragment.ivDelete.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.isPinchZoomEnabled()) {
                    Intent intent = new Intent(mContext, CustomExoPlayerActivity.class);
                    intent.putExtra("videosModelList", list);
                    intent.putExtra("videoPath", videosModelList.get(position).getFileName());
                    intent.putExtra("current_video_pos", position);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                    intent.putExtra("videosModelList", list);
                    intent.putExtra("videoPath", videosModelList.get(position).getFileName());
                    intent.putExtra("current_video_pos", position);
                    intent.putExtra("type", "1");
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videosModelList.size();
    }

    public ArrayList<AllMediaListModel> getVideosList() {
        return videosModelList;
    }

    public void updateList(@NotNull ArrayList<AllMediaListModel> list) {
        videosModelList = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;
        ImageView videoListMenu;
        CheckBox cbDelete;
        TextView tvFileName;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tvFileName = (TextView) itemView.findViewById(R.id.tv_file_name);
            videoListMenu = itemView.findViewById(R.id.videoListMenu);
//            tv_foldern = (TextView) itemView.findViewById(R.id.tv_folder);
//            tv_foldersize = (TextView) itemView.findViewById(R.id.tv_folder2);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            cbDelete = (CheckBox) itemView.findViewById(R.id.cb_item_media_row);
        }
    }

    public void deleteFile(final int position, final View v) {
        final String[] selectionArgs = new String[]{videosModelList.get(position).getFileName()};
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set up the projection (we only need the ID)
                String[] projection = {MediaStore.Video.Media._ID};

                // Match on the file path
                String selection = MediaStore.Video.Media.DATA + " = ?";


                // Query for the ID of the media matching the file path
                Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = mContext.getContentResolver();
                Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);

                if (c != null) {
                    if (c.moveToFirst()) {
                        // We found the ID. Deleting the item via the content provider will also remove the file
                        long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                        Uri deleteUri = ContentUris.withAppendedId(queryUri, id);
                        contentResolver.delete(deleteUri, null, null);

                    } else {
                        // File not found in media store DB
                    }
                    c.close();
                }
            }
        }, 3000);

        videosModelList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, videosModelList.size());
        Snackbar.make(v, "File deleted", Snackbar.LENGTH_LONG).show();
    }
}