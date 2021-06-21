package com.prayosof.yvideo.view.browser.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.databinding.ActivityFullView2Binding;
import com.prayosof.yvideo.view.browser.adapters.ShowImagesAdapter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static com.prayosof.yvideo.view.browser.adapters.WhatsAppStatusAdapter.DIRECTORY_TO_SAVE_MEDIA_NOW;
import static com.prayosof.yvideo.view.browser.utils.Utils.shareImage;
import static com.prayosof.yvideo.view.browser.utils.Utils.shareImageVideoOnWhatsapp;
import static com.prayosof.yvideo.view.browser.utils.Utils.shareVideo;


public class FullViewActivity2 extends AppCompatActivity {

    private ActivityFullView2Binding binding;
    private FullViewActivity2 activity;
    private ArrayList<File> fileArrayList;
    private int Position = 0, type = 0;
    private ShowImagesAdapter showImagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_view2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        activity = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileArrayList = (ArrayList<File>) getIntent().getSerializableExtra("ImageDataFile");
            Position = getIntent().getIntExtra("Position", 0);
            type = getIntent().getIntExtra("type", 0);
        }
        initViews();
    }

    public void initViews() {
        showImagesAdapter = new ShowImagesAdapter(this, fileArrayList, FullViewActivity2.this);
        binding.vpView.setAdapter(showImagesAdapter);
        binding.vpView.setCurrentItem(Position);

        binding.vpView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                Position = arg0;
                System.out.println("Current position==" + Position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int num) {
            }
        });

        binding.imDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean b = fileArrayList.get(Position).delete();
                        if (b) {
                            deleteFileAA(Position);
                        }
                    }
                });
                ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = ab.create();
                alert.setTitle("Do you want to delete?");
                alert.show();
            }
        });
        binding.imShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileArrayList.get(Position).getName().contains(".mp4")) {
                    Log.d("SSSSS", "onClick: " + fileArrayList.get(Position) + "");
                    shareVideo(activity, fileArrayList.get(Position).getPath());
                } else {
                    shareImage(activity, fileArrayList.get(Position).getPath());
                }
            }
        });
        binding.imWhatsappShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileArrayList.get(Position).getName().contains(".mp4")) {
                    shareImageVideoOnWhatsapp(activity, fileArrayList.get(Position).getPath(), true);
                } else {
                    shareImageVideoOnWhatsapp(activity, fileArrayList.get(Position).getPath(), false);
                }
            }
        });
        binding.imClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (type == 1) {
            binding.rlSave.setVisibility(View.VISIBLE);
            binding.rlDelete.setVisibility(View.GONE);
        }

        binding.imSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File directory = new File(Environment.getExternalStorageDirectory() + "/Music Bazaar/WhatsAppStatus/");
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                try {
                    FileUtils.copyFileToDirectory(new File(fileArrayList.get(Position).getPath()), new File(DIRECTORY_TO_SAVE_MEDIA_NOW));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Snackbar.make(findViewById(android.R.id.content),
                        "File saved successfully!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    public void deleteFileAA(int position) {
        fileArrayList.remove(position);
        showImagesAdapter.notifyDataSetChanged();
        Toast.makeText(this, "File deleted successfully!", Toast.LENGTH_SHORT).show();
        if (fileArrayList.size() == 0) {
            onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}