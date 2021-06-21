package com.prayosof.yvideo.view.browser.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.view.browser.activities.FullViewActivity2;
import com.prayosof.yvideo.view.browser.models.WhatsAppStatusModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WhatsAppStatusAdapter extends RecyclerView.Adapter<WhatsAppStatusAdapter.ViewHolder> {

    public static String DIRECTORY_TO_SAVE_MEDIA_NOW = new File(Environment.getExternalStorageDirectory() + "/Music Bazaar/WhatsAppStatus") + "/";
    private Context context;
    private ArrayList<WhatsAppStatusModel> arrayList;
    private ArrayList<File> list = new ArrayList<File>();

    public WhatsAppStatusAdapter(Context context, ArrayList<WhatsAppStatusModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_whatsapp_status, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        list.add(new File(arrayList.get(position).getPath()));
        try {
            Glide.with(context).load(arrayList.get(position).getPath())
                    .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(holder.ivIcon);
        } catch (Exception ioe) {

        }
        if (arrayList.get(position).getPath().endsWith(".mp4")) {
            holder.ivPlay.setVisibility(View.VISIBLE);
        } else {
            holder.ivPlay.setVisibility(View.GONE);
        }

        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File directory = new File(Environment.getExternalStorageDirectory() + "/Music Bazaar/WhatsAppStatus/");
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                try {
                    FileUtils.copyFileToDirectory(new File(arrayList.get(position).getPath()), new File(DIRECTORY_TO_SAVE_MEDIA_NOW));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                        "File saved successfully!", Snackbar.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inNext = new Intent(context, FullViewActivity2.class);
                inNext.putExtra("ImageDataFile", list);
                inNext.putExtra("Position", position);
                inNext.putExtra("type", 1);
                context.startActivity(inNext);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIcon, ivPlay;
        private Button btnDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_item_status_icon);
            ivPlay = (ImageView) itemView.findViewById(R.id.iv_item_status_play);
            btnDownload = (Button) itemView.findViewById(R.id.btn_item_status_download);
        }
    }
}