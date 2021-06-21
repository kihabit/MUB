package com.prayosof.yvideo.view.fragment.video;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.RecyclerView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.AllMediaListAdapter;
import com.prayosof.yvideo.adapter.MediaListAdapter;
import com.prayosof.yvideo.interfaces.CustomOnClickListener;
import com.prayosof.yvideo.model.AllMediaListModel;
import com.prayosof.yvideo.view.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.HashSet;

import static com.prayosof.yvideo.view.fragment.video.MainVideoFragment.ivDelete;

public class AllVideosListFragment extends BaseFragment implements CustomOnClickListener {

    private View view;
    private EditText etSearch;
    private ImageView ivCross;
    private RecyclerView rvVideos;

    private ArrayList<AllMediaListModel> arrayList;
    public static AllMediaListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_videos_list, container, false);

        etSearch = (EditText) view.findViewById(R.id.search_all_videos);
        ivCross = (ImageView)  view.findViewById(R.id.iv_all_videos_cross);
        rvVideos = (RecyclerView) view.findViewById(R.id.rv_all_videos);

        arrayList = new ArrayList<AllMediaListModel>();
        arrayList.clear();
        arrayList = getAllMedia();

        rvVideos.setHasFixedSize(true);
        adapter = new AllMediaListAdapter(getActivity(), arrayList, 0, this);
        rvVideos.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    ivCross.setVisibility(View.VISIBLE);
                    filter(s.toString());
                } else {
                    ivCross.setVisibility(View.GONE);
                    if (adapter != null) {
                        adapter.updateList(arrayList);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                etSearch.clearFocus();
                ivCross.setVisibility(View.GONE);
                if (adapter != null) {
                    adapter.updateList(arrayList);
                }
            }
        });

        return view;
    }

    private void filter(String query) {
        ArrayList<AllMediaListModel> temp = new ArrayList<>();

        for (int i=0; i<arrayList.size(); i++) {
            if (arrayList.get(i).getFileName().toLowerCase().contains(query)) {
                AllMediaListModel model = new AllMediaListModel();
                model.setFileName(arrayList.get(i).getFileName());
                model.setSelected(false);
                temp.add(model);
            }
        }

        if (adapter != null) {
            adapter.updateList(temp);
        }
    }

    public ArrayList<AllMediaListModel> getAllMedia() {
        HashSet<AllMediaListModel> videoItemHashSet = new HashSet<>();
        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME};
        Cursor cursor = getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        try {
            cursor.moveToFirst();
            do {
                AllMediaListModel model = new AllMediaListModel();
                model.setFileName((cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))));
                model.setSelected(false);
                videoItemHashSet.add(model);
            } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<AllMediaListModel> downloadedList = new ArrayList<>(videoItemHashSet);

        return downloadedList;
    }

    @Override
    public void onShareClick(String fileName, String filePath) {
        startFileShareIntent(filePath);
    }
}