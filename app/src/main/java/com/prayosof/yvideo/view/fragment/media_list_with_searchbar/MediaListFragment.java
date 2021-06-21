package com.prayosof.yvideo.view.fragment.media_list_with_searchbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.MediaListAdapter;
import com.prayosof.yvideo.interfaces.CustomOnClickListener;
import com.prayosof.yvideo.model.AllMediaListModel;
import com.prayosof.yvideo.model.ModelVideos;
import com.prayosof.yvideo.view.fragment.BaseFragment;
import com.prayosof.yvideo.view.fragment.dashboard.VideosListFragment;

import java.io.File;
import java.util.ArrayList;


public class MediaListFragment extends BaseFragment implements CustomOnClickListener {

    RecyclerView rvMediaList;
    int int_position = 0, value = 0;
    ArrayList<ModelVideos> videosModelList;
    MediaListAdapter adapter;
    EditText searcMediaList;
    ArrayList<AllMediaListModel> allVideoPaths;
    CustomOnClickListener listener;
    public static int count = 0;
    public static ImageView ivDelete;
    ImageView ivCross;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            value = getArguments().getInt("value");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_media_list, container, false);


        // Lookup the recyclerview in activity layout
        ivCross = (ImageView) root.findViewById(R.id.iv_media_cross);
        ivDelete = (ImageView) root.findViewById(R.id.iv_media_delete);
        rvMediaList = (RecyclerView) root.findViewById(R.id.rvMediaList);

        rvMediaList.setHasFixedSize(true);
        //rvMediaList.setLayoutManager(new LinearLayoutManager(getContext()));
        videosModelList = VideosListFragment.videosModelList;
        allVideoPaths = new ArrayList<AllMediaListModel>();
        searcMediaList = (EditText) root.findViewById(R.id.searcMediaList);
        setUI();
        searcMediaList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    ivCross.setVisibility(View.VISIBLE);
                    filter(s.toString());
                } else {
                    ivCross.setVisibility(View.INVISIBLE);
                    if (adapter != null) {
                        adapter.updateList(allVideoPaths);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        MainVideoFragment.ivDelete.isClickable = false
//        MainVideoFragment.ivDelete.isEnabled = false
//        MainVideoFragment.ivDelete.setColorFilter(resources.getColor(R.color.white))

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searcMediaList.setText("");
                searcMediaList.clearFocus();
                ivCross.setVisibility(View.INVISIBLE);
                if (adapter != null) {
                    adapter.updateList(allVideoPaths);
                }
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you really want to delete files?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> list = new ArrayList<>();
                        for (int i=0; i<allVideoPaths.size(); i++) {
                            if (allVideoPaths.get(i).isSelected()) {
                                File file = new File(allVideoPaths.get(i).getFileName());
                                file.delete();

                                allVideoPaths.remove(i);
                                adapter.notifyItemRemoved(i);
                                adapter.notifyItemRangeChanged(i, allVideoPaths.size());
                                i--;
                            } else {
                                list.add(allVideoPaths.get(i).getFileName());
                            }
                        }
                        videosModelList.get(value).setAllVideoPath(list);
                        VideosListFragment.videosModelList = videosModelList;
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i=0; i<allVideoPaths.size(); i++) {
                            allVideoPaths.get(i).setSelected(false);
                        }
                        adapter.updateList(allVideoPaths);
                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
            }
        });

        return root;
    }

    private void filter(String text) {
        //val temp: ArrayList<Any?> = ArrayList<Any?>()
        ArrayList<AllMediaListModel> temp = new ArrayList<AllMediaListModel>();

        //KJS
        for (int i=0; i<allVideoPaths.size(); i++) {
            if (allVideoPaths.get(i).getFileName().toLowerCase().contains(text)) {
                AllMediaListModel model = new AllMediaListModel();
                model.setFileName(allVideoPaths.get(i).getFileName());
                model.setSelected(false);
                temp.add(model);
            }
        }

        //update recyclerview
        if (adapter != null) {
            adapter.updateList(temp);
        }
    }

    private void setUI() {
        if (videosModelList.size() != 0) {
            listener = this;

            //KJS
            for (int i=0; i<videosModelList.get(value).allVideoPath.size(); i++) {
                AllMediaListModel model = new AllMediaListModel();
                model.setFileName(videosModelList.get(value).allVideoPath.get(i));
                model.setSelected(false);
                allVideoPaths.add(model);
            }
//            allVideoPaths = videosModelList[value].allVideoPath
            adapter = new MediaListAdapter(getActivity(), allVideoPaths, value, listener);
            rvMediaList.setAdapter(adapter);
        }
    }

    @Override
    public void onShareClick(String fileName, String filePath) {
        startFileShareIntent(filePath);
    }
}