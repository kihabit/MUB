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

import java.util.ArrayList;

public class BookmarksFragment extends Fragment {

    private View view;
    private RecyclerView rvBookmarks;

    private ArrayList<BookmarkModel> arrayList;
    private DBHelperBookmark dbHelperBookmark;
    private BookmarkAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        rvBookmarks = (RecyclerView) view.findViewById(R.id.rv_web_bookmarks);

        dbHelperBookmark = new DBHelperBookmark(getActivity());
        arrayList = new ArrayList<BookmarkModel>();
        arrayList.clear();
        arrayList = dbHelperBookmark.GetAllBookmarks();

        adapter = new BookmarkAdapter(getActivity(), arrayList);
        rvBookmarks.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelperBookmark = new DBHelperBookmark(getActivity());
        arrayList = new ArrayList<BookmarkModel>();
        arrayList.clear();
        arrayList = dbHelperBookmark.GetAllBookmarks();

        adapter = new BookmarkAdapter(getActivity(), arrayList);
        rvBookmarks.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}