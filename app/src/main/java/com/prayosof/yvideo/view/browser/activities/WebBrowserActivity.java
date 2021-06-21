package com.prayosof.yvideo.view.browser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.DBHelperBookmark;
import com.prayosof.yvideo.helper.SessionManager;
import com.prayosof.yvideo.model.BookmarkModel;
import com.prayosof.yvideo.view.browser.fragments.ContentFragment;
import com.prayosof.yvideo.view.browser.models.SearchViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class WebBrowserActivity extends AppCompatActivity implements Observer {

    private ArrayList<String> liveData = new ArrayList<>();
    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

        try {
            Bundle intent = getIntent().getExtras();
            url = intent.getString("url");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        SearchViewModel searchViewModel = new SearchViewModel(this);
        setviewModel(searchViewModel);

        ContentFragment fragment1 = new ContentFragment();
        Bundle args = new Bundle();
        FragmentManager manager1 = getSupportFragmentManager();
        FragmentTransaction transaction1 = manager1.beginTransaction();
        args.putString("url", url);
        fragment1.setArguments(args);
        transaction1.replace(R.id.home_container, fragment1, "fm");
        transaction1.commit();
    }

    private void AddToBookmark() {
        DBHelperBookmark dbHelperBookmark = new DBHelperBookmark(this);
        BookmarkModel model = new BookmarkModel();
//        model.setName(bookName);
//        model.setUrl(bookUrl);
//        model.setIcon(bookIcon);
        dbHelperBookmark.AddBookmark(model);

        ImageView imageView = (ImageView) findViewById(R.id.btn_bookmark);
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
    }

    private void setviewModel(Observable observable) {
        observable.addObserver(this);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("isdownload", false)) {
//                binding.space.showBadgeAtIndex(0, 1, getResources().getColor(R.color.colorRed));
            } else {
//                binding.space.showBadgeAtIndex(1, 1, getResources().getColor(R.color.colorGreen));
                Intent intent1 = new Intent("savefile");
                intent1.putExtra("isfinish", true);
                LocalBroadcastManager.getInstance(WebBrowserActivity.this).sendBroadcast(intent1);
            }
        }

    };

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof SearchViewModel) {
            SearchViewModel searchViewModel = (SearchViewModel) observable;
            String searchResponce = searchViewModel.getmJobsDetails();
            if (searchResponce != null) {
                liveData.add(searchResponce);
            }
        }
    }
}