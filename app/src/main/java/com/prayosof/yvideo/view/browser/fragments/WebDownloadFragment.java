package com.prayosof.yvideo.view.browser.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.BookmarkAdapter;
import com.prayosof.yvideo.helper.DBHelperBookmark;
import com.prayosof.yvideo.helper.SessionManager;
import com.prayosof.yvideo.model.BookmarkModel;
import com.prayosof.yvideo.view.browser.activities.WebBrowserActivity;
import com.prayosof.yvideo.view.browser.activities.WhatsAppStatusActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class WebDownloadFragment extends Fragment implements View.OnClickListener {

    private View view;
    private LinearLayout llUrl;
    private ImageView ivDownload, ivWhatsApp, ivFacebook, ivInstagram, ivTwitter, ivTubidy, ivTed;
    private RecyclerView rvBookmarks;

    private static final String ARG_POSITION = "position";
    private ArrayList<BookmarkModel> arrayList;
    private DBHelperBookmark dbHelperBookmark;
    private BookmarkAdapter adapter;

    public static WebDownloadFragment newInstance(int position) {
        WebDownloadFragment f = new WebDownloadFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_web_download, container, false);

        llUrl = (LinearLayout) view.findViewById(R.id.ll_web_down_url);
        ivDownload = (ImageView) view.findViewById(R.id.iv_web_download);
        ivWhatsApp = (ImageView) view.findViewById(R.id.iv_web_whatsapp);
        ivFacebook = (ImageView) view.findViewById(R.id.iv_web_facebook);
        ivInstagram = (ImageView) view.findViewById(R.id.iv_web_instagram);
        ivTwitter = (ImageView) view.findViewById(R.id.iv_web_twitter);
        ivTubidy = (ImageView) view.findViewById(R.id.iv_web_tubidy);
        ivTed = (ImageView) view.findViewById(R.id.iv_web_ted);
        rvBookmarks = (RecyclerView) view.findViewById(R.id.rv_web_download);

        llUrl.setOnClickListener(this);
        ivDownload.setOnClickListener(this);
        ivWhatsApp.setOnClickListener(this);
        ivFacebook.setOnClickListener(this);
        ivInstagram.setOnClickListener(this);
        ivTwitter.setOnClickListener(this);
        ivTubidy.setOnClickListener(this);
        ivTed.setOnClickListener(this);

        dbHelperBookmark = new DBHelperBookmark(getActivity());
        arrayList = new ArrayList<BookmarkModel>();
        arrayList.clear();
        arrayList = dbHelperBookmark.GetAllBookmarks();

        Collections.reverse(arrayList);
        adapter = new BookmarkAdapter(getActivity(), arrayList);
        rvBookmarks.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        String downloadPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + File.separator; //+ "Music Bazaar" + File.separator;
        File dir = new File(downloadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        SessionManager manager = new SessionManager(getActivity());
        manager.storeFolderPath(getActivity(), downloadPath);
        Log.e("path", manager.getFolderPath(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHelperBookmark = new DBHelperBookmark(getActivity());
        arrayList = new ArrayList<BookmarkModel>();
        arrayList.clear();
        arrayList = dbHelperBookmark.GetAllBookmarks();

        Collections.reverse(arrayList);
        adapter = new BookmarkAdapter(getActivity(), arrayList);
        rvBookmarks.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_web_down_url:
                startActivity(new Intent(getActivity(), WebBrowserActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("url", ""));
                break;
            case R.id.iv_web_download:
                WebFileViewerFragment fragment = new WebFileViewerFragment();
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment, fragment, "fm");
                transaction.addToBackStack(manager.getClass().getName());
                transaction.commit();
                break;
            case R.id.iv_web_whatsapp:
                startActivity(new Intent(getActivity(), WhatsAppStatusActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
            case R.id.iv_web_facebook:
                startActivity(new Intent(getActivity(), WebBrowserActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("url", "https://www.facebook.com/"));
                break;
            case R.id.iv_web_instagram:
                startActivity(new Intent(getActivity(), WebBrowserActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("url", "https://www.instagram.com/"));
                break;
            case R.id.iv_web_twitter:
                startActivity(new Intent(getActivity(), WebBrowserActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("url", "https://www.twitter.com/"));
                break;
            case R.id.iv_web_tubidy:
                startActivity(new Intent(getActivity(), WebBrowserActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("url", "https://www.tubidy.mobi/"));
                break;
            case R.id.iv_web_ted:
                startActivity(new Intent(getActivity(), WebBrowserActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("url", "https://www.ted.com/"));
                break;
        }
    }
}