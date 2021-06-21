package com.prayosof.yvideo.view.browser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.databinding.ActivitySavedWhatsAppBinding;
import com.prayosof.yvideo.view.browser.adapters.FileListAdapter;
import com.prayosof.yvideo.view.browser.utils.FileListClickInterface;

import java.io.File;
import java.util.ArrayList;

import static com.prayosof.yvideo.view.browser.adapters.WhatsAppStatusAdapter.DIRECTORY_TO_SAVE_MEDIA_NOW;


public class SavedWhatsAppActivity extends AppCompatActivity implements FileListClickInterface {

    private ActivitySavedWhatsAppBinding binding;
    private FileListAdapter fileListAdapter;
    private ArrayList<File> fileArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_whats_app);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_saved_whats_app);

        getAllFiles();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllFiles();
    }

    private void getAllFiles(){
        fileArrayList = new ArrayList<>();
        fileArrayList.clear();
        File[] files = new File(DIRECTORY_TO_SAVE_MEDIA_NOW).listFiles();
        if (files!=null) {
            for (File file : files) {
                fileArrayList.add(file);
            }
            fileListAdapter = new FileListAdapter(this, fileArrayList, SavedWhatsAppActivity.this);
            binding.rvFileList.setAdapter(fileListAdapter);
        }
    }

    @Override
    public void getPosition(int position, File file) {
        Intent inNext = new Intent(this, FullViewActivity2.class);
        inNext.putExtra("ImageDataFile", fileArrayList);
        inNext.putExtra("Position", position);
        inNext.putExtra("type", 0);
        startActivity(inNext);
    }
}