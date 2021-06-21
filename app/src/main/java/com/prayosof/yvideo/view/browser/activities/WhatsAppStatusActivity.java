package com.prayosof.yvideo.view.browser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.view.browser.adapters.WhatsAppStatusAdapter;
import com.prayosof.yvideo.view.browser.models.WhatsAppStatusModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class WhatsAppStatusActivity extends AppCompatActivity {

    private LinearLayout llNotFound;
    private ImageView ivBack, ivSaved;
    private Button btnOpen;
    private RecyclerView rvStatus;

    private File[] allfiles;
    ArrayList<WhatsAppStatusModel> statusModelArrayList = new ArrayList<WhatsAppStatusModel>();

    public static final String WHATSAPP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_app_status);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        llNotFound = (LinearLayout) findViewById(R.id.ll_status_not_found);
        ivBack = (ImageView) findViewById(R.id.iv_status_back);
        ivSaved = (ImageView) findViewById(R.id.iv_status_folder);
        btnOpen = (Button) findViewById(R.id.btn_status_open);
        rvStatus = (RecyclerView) findViewById(R.id.rv_status_all);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WhatsAppStatusActivity.this, SavedWhatsAppActivity.class));
            }
        });

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                if (intent != null) {
                    // We found the activity now start the activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    // Bring user to the market or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id=com.whatsapp"));
                    startActivity(intent);
                }
            }
        });

        GetAllStatus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetAllStatus();
    }

    private void GetAllStatus() {
        if (this.getListFiles().size() > 0) {
            llNotFound.setVisibility(View.GONE);
            WhatsAppStatusAdapter adapter = new WhatsAppStatusAdapter(this, this.getListFiles());
            rvStatus.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            llNotFound.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<WhatsAppStatusModel> getListFiles() {
        statusModelArrayList.clear();

        WhatsAppStatusModel whatsappStatusModel;
        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + WHATSAPP_STATUSES_LOCATION;
        File targetDirector = new File(targetPath);
        allfiles = targetDirector.listFiles();

        try {
            Arrays.sort(allfiles, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }
            });

            for (int i = 0; i < allfiles.length; i++) {
                File file = allfiles[i];
                whatsappStatusModel = new WhatsAppStatusModel("WhatsStatus: " + (i + 1),
                        Uri.fromFile(file),
                        allfiles[i].getAbsolutePath(),
                        file.getName());
                statusModelArrayList.add(whatsappStatusModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusModelArrayList;
    }
}