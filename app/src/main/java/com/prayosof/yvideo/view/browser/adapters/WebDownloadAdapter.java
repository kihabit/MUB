package com.prayosof.yvideo.view.browser.adapters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.DialogHelper;
import com.prayosof.yvideo.interfaces.CustomOnClickListener;
import com.prayosof.yvideo.view.activity.video.VideoPlayerActivity;
import com.prayosof.yvideo.view.browser.fragments.WebFileViewerFragment;
import com.prayosof.yvideo.view.browser.models.WebDownloadModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.prayosof.yvideo.view.browser.fragments.WebFileViewerFragment.arrayList;


public class WebDownloadAdapter extends RecyclerView.Adapter<WebDownloadAdapter.ViewHolder> {

    private Context context;
//    private ArrayList<WebDownloadModel> arrayList;
    private ArrayList<String> list = new ArrayList<String>();
    private CustomOnClickListener listener;

    public WebDownloadAdapter(Context context, ArrayList<WebDownloadModel> arrayList, CustomOnClickListener listener) {
        this.context = context;
//        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_list_row, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        list.add(arrayList.get(position).getPath());

        try {
            Glide.with(context).load(arrayList.get(position).getPath())
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.iv_image);
        } catch (Exception ioe) {

        }

        String filename = "";
        try {
            filename = arrayList.get(position).getPath()
                    .substring(arrayList.get(position).getPath().lastIndexOf("/") + 1);
            holder.tvFileName.setText(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String finalFilename = filename;
        holder.videoListMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenu().add(1, R.id.delete, 1, "Delete");
                popupMenu.getMenu().add(1, R.id.share_file, 1, "Share");
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {//
                            DialogHelper.showYesNoDialog(context, "Are you sure You want to delete this file?", new DialogInterface.OnClickListener() {
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
                            listener.onShareClick(finalFilename, arrayList.get(position).getPath());

                            return true;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popupMenu.show();
            }
        });

        holder.cbSelect.setVisibility(View.VISIBLE);
        holder.cbSelect.setChecked(arrayList.get(position).isSelected());
        holder.cbSelect.setTag(arrayList.get(position));
        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.get(position).isSelected()) {
                    arrayList.get(position).setSelected(false);
                    WebFileViewerFragment.count--;
                } else {
                    arrayList.get(position).setSelected(true);
                    WebFileViewerFragment.count++;
                }
                notifyItemChanged(holder.getAdapterPosition());

//                WebDownloadModel models = (WebDownloadModel) holder.cbSelect.getTag();
//
//                if (arrayList.get(position).isSelected()) {
//                    WebFileViewerFragment.count--;
//                    models.setSelected(holder.cbSelect.isChecked());
//                    arrayList.get(position).setSelected(holder.cbSelect.isChecked());
//                } else {
//                    models.setSelected(holder.cbSelect.isChecked());
//                    arrayList.get(position).setSelected(holder.cbSelect.isChecked());
//                    for (int j = 0; j < arrayList.size(); j++) {
//                        if (arrayList.get(j).isSelected()) {
//                            WebFileViewerFragment.type = "videos";
//                            WebFileViewerFragment.count++;
//                            WebFileViewerFragment.ivDelete.setClickable(true);
//                            WebFileViewerFragment.ivDelete.setEnabled(true);
//                            WebFileViewerFragment.ivDelete.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
//                        }
//                    }
//                }
                if (WebFileViewerFragment.count == 0 || WebFileViewerFragment.count < 0) {
                    WebFileViewerFragment.ivDelete.setClickable(false);
                    WebFileViewerFragment.ivDelete.setEnabled(false);
                    WebFileViewerFragment.ivDelete.setColorFilter(context.getResources().getColor(R.color.black));
                } else {
                    WebFileViewerFragment.ivDelete.setClickable(true);
                    WebFileViewerFragment.ivDelete.setEnabled(true);
                    WebFileViewerFragment.ivDelete.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videosModelList", list);
                    intent.putExtra("videoPath", arrayList.get(position).getPath());
                    intent.putExtra("current_video_pos", position);
                    intent.putExtra("type", "1");
                    context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void updateList(@NotNull ArrayList<WebDownloadModel> list) {
        arrayList = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_image;
        ImageView videoListMenu;
        TextView tvFileName;
        CheckBox cbSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = (TextView) itemView.findViewById(R.id.tv_file_name);
            videoListMenu = itemView.findViewById(R.id.videoListMenu);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            cbSelect = (CheckBox) itemView.findViewById(R.id.cb_item_media_row);
        }
    }

    public void deleteFile(final int position, final View v) {
        final String[] selectionArgs = new String[]{arrayList.get(position).getPath()};
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set up the projection (we only need the ID)
                String[] projection = {MediaStore.Video.Media._ID};

                // Match on the file path
                String selection = MediaStore.Video.Media.DATA + " = ?";


                // Query for the ID of the media matching the file path
                Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = context.getContentResolver();
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

        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
        Snackbar.make(v, "File deleted", Snackbar.LENGTH_LONG).show();
    }
}