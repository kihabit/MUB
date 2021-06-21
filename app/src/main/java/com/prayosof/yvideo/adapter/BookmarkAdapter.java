package com.prayosof.yvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.model.BookmarkModel;
import com.prayosof.yvideo.view.browser.activities.WebBrowserActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BookmarkModel> arrayList;

    public BookmarkAdapter(Context context, ArrayList<BookmarkModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_web_bookmark, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(arrayList.get(position).getIcon(), 0, arrayList.get(position).getIcon().length);
            holder.civIcon.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvName.setText(arrayList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, WebBrowserActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("url", arrayList.get(position).getUrl()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civIcon;
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civIcon = (CircleImageView) itemView.findViewById(R.id.civ_item_book_icon);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_book_name);
        }
    }
}