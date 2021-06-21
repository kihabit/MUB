package com.prayosof.yvideo.view.browser.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.BookmarkAdapter;
import com.prayosof.yvideo.helper.DBHelperBookmark;
import com.prayosof.yvideo.model.BookmarkModel;
import com.prayosof.yvideo.view.browser.adapters.HistoryAdapter;
import com.prayosof.yvideo.view.browser.models.HistoryModel;

import java.util.ArrayList;
import java.util.Collections;


public class HistoryFragment extends Fragment {

    private View view;
    private RecyclerView rvHistory;

    private ArrayList<HistoryModel> arrayList;
    private DBHelperBookmark dbHelperBookmark;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = (RecyclerView) view.findViewById(R.id.rv_web_history);

        dbHelperBookmark = new DBHelperBookmark(getActivity());
        arrayList = new ArrayList<HistoryModel>();
        arrayList.clear();
        arrayList = dbHelperBookmark.GetAllHistory();

        Collections.reverse(arrayList);
        adapter = new HistoryAdapter(getActivity(), arrayList);
        rvHistory.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }
}