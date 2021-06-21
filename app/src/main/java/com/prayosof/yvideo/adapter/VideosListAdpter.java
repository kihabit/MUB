package com.prayosof.yvideo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.model.ModelVideos;
import com.prayosof.yvideo.view.fragment.video.MainVideoFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


/**
 * Created by Yogesh Y. Nikam on 27/06/20.
 */
public class VideosListAdpter extends RecyclerView.Adapter<VideosListAdpter.ViewHolder> {

    private Context context;
    public static ArrayList<ModelVideos> videosListData = new ArrayList<>();
    public static MediaListner mListner;

    public interface MediaListner {
        public void onItemClick(int pos);
        public void onItemDelete(int pos);
    }

    public VideosListAdpter(Context context, ArrayList<ModelVideos> al_menu, MediaListner listner) {
        this.videosListData = al_menu;
        this.context = context;
        this.mListner = listner;
    }

    @NonNull
    @Override
    public VideosListAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final VideosListAdpter.ViewHolder holder, final int position) {
        holder.tvFolderName.setText(videosListData.get(position).getStr_folder());
        holder.tvFolderSize.setText("(" + videosListData.get(position).getAllVideoPath().size() + ") Videos");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListner.onItemClick(position);
            }
        });

        holder.trackListMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenu().add(1, R.id.delete, 1, "Delete");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                            mListner.onItemDelete(position);
                            return true;
                        }
                        return false;
                    }
                });

                //displaying the popup
                //displaying the popup
                popupMenu.show();
            }


        });

        holder.cbDelete.setChecked(videosListData.get(position).isSelected());
        holder.cbDelete.setTag(videosListData.get(position));

        holder.cbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videosListData.get(position).isSelected()) {
                    videosListData.get(position).setSelected(false);
                    MainVideoFragment.count--;
                } else {
                    videosListData.get(position).setSelected(true);
                    MainVideoFragment.count++;
                    MainVideoFragment.type = "folder";
                }
                notifyItemChanged(holder.getAdapterPosition());

//                ModelVideos models = (ModelVideos) holder.cbDelete.getTag();
//
//
//                if (videosListData.get(position).isSelected()) {
//                    MainVideoFragment.count--;
//                    models.setSelected(holder.cbDelete.isChecked());
//                    videosListData.get(position).setSelected(holder.cbDelete.isChecked());
//                } else {
//                    models.setSelected(holder.cbDelete.isChecked());
//                    videosListData.get(position).setSelected(holder.cbDelete.isChecked());
//                    for (int j=0; j<videosListData.size();j++){
//                        if (videosListData.get(j).isSelected()){
//                            MainVideoFragment.type = "folder";
//                            MainVideoFragment.count++;
//                            MainVideoFragment.ivDelete.setClickable(true);
//                            MainVideoFragment.ivDelete.setEnabled(true);
//                            MainVideoFragment.ivDelete.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
//                        }
//                    }
//                }
                if (MainVideoFragment.count < 1) {
                    MainVideoFragment.ivDelete.setClickable(false);
                    MainVideoFragment.ivDelete.setEnabled(false);
                    MainVideoFragment.ivDelete.setColorFilter(context.getResources().getColor(R.color.black));
                } else {
                    MainVideoFragment.ivDelete.setClickable(true);
                    MainVideoFragment.ivDelete.setEnabled(true);
                    MainVideoFragment.ivDelete.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videosListData.size();
    }

    public void updateList(@NotNull ArrayList<ModelVideos> list) {
        videosListData = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFolderName, tvFolderSize;
        ImageButton trackListMenu;
        ImageView ivFolder;
        CheckBox cbDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFolder = (ImageView) itemView.findViewById(R.id.iv_item_video_folder);
            tvFolderName = itemView.findViewById(R.id.folder_name);
            tvFolderSize = itemView.findViewById(R.id.folder_size);
            trackListMenu = (ImageButton) itemView.findViewById(R.id.trackListMenu);
            cbDelete = (CheckBox) itemView.findViewById(R.id.cb_item_folder_row);
        }
    }
}