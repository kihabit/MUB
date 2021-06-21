package com.prayosof.yvideo.view.browser.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.view.browser.models.HistoryModel;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<HistoryModel> arrayList;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText(arrayList.get(position).getName());
        holder.tvUrl.setText(arrayList.get(position).getUrl());
        holder.tvDate.setText(arrayList.get(position).getDate() + " " + arrayList.get(position).getTime());

        try {
//            byte[] bytes = Base64.decode(arrayList.get(position).getIcon(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arrayList.get(position).getIcon(), 0, arrayList.get(position).getIcon().length);
            holder.ivIcon.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIcon;
        private TextView tvTitle, tvUrl, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_item_history_icon);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_history_title);
            tvUrl = (TextView) itemView.findViewById(R.id.tv_item_history_url);
            tvDate = (TextView) itemView.findViewById(R.id.tv_item_history_date);
        }
    }
}