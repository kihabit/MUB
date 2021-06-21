package com.prayosof.yvideo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.model.ModelAudios;

import java.util.ArrayList;

/**
 * Created by Yogesh Y. Nikam on 18/08/20.
 */
public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {
        Context context;
        ArrayList<ModelAudios> audioListData = new ArrayList<>();



        MediaListner mListner;


public interface MediaListner {

    public void onItemClick(int pos);

}

    public AudioListAdapter(Context context, ArrayList<ModelAudios> al_menu, AudioListAdapter.MediaListner listner) {
        this.audioListData = al_menu;
        this.context = context;
        this.mListner = listner;
    }

    @NonNull
    @Override
    public AudioListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_list_row, parent, false);
        AudioListAdapter.ViewHolder viewHolder = new AudioListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AudioListAdapter.ViewHolder holder, final int position) {

        holder.tvFolderName.setText(audioListData.get(position).getStr_folder());
        holder.tvFolderSize.setText("("+ audioListData.get(position).getAllAudioPaths().size()+") Audios");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListner.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return audioListData.size();
    }

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView tvFolderName, tvFolderSize;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        tvFolderName = itemView.findViewById(R.id.folder_name);
        tvFolderSize = itemView.findViewById(R.id.folder_size);

    }
}


}