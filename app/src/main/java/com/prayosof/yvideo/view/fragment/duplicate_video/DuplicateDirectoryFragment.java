package com.prayosof.yvideo.view.fragment.duplicate_video;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.VideoAdapter;
import com.prayosof.yvideo.model.DuplicateCheckModel;
import com.prayosof.yvideo.model.VideoModel;
import com.prayosof.yvideo.view.activity.video.VideoPlay2Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DuplicateDirectoryFragment extends Fragment {

    private DuplicateDirectoryViewModel duplicateDirectoryViewModel;

    public static ArrayList<VideoModel> videoArrayList;
    RecyclerView recyclerView;
    public static final int PERMISSION_READ = 0;
    View root;
    //ArrayList<ViewModel>list = new ArrayList();

    HashMap<String, VideoModel> videoModelkMap = new HashMap<>();
    HashMap<String, VideoModel> doubleCheckMap = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        duplicateDirectoryViewModel =
                ViewModelProviders.of(this).get(DuplicateDirectoryViewModel.class);
        root = inflater.inflate(R.layout.fragment_duplicate_directory, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        duplicateDirectoryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //  textView.setText(s);
            }
        });

        if (checkPermission()) {
            videoList();
        }

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void videoList() {
        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        videoArrayList = new ArrayList<>();
        getVideos();
    }


    //get video files from storage
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getVideos() {
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                int  id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));


                VideoModel videoModel = new VideoModel();
                videoModel.setVideoTitle(title);
                videoModel.setVideoUri(Uri.parse(data));
                videoModel.setVideoDuration(timeConversion(Long.parseLong(duration)));
                videoModel.setSize(size);
                videoModel.setId(id);


//                VideoModel duplicateCheckModel = new VideoModel();
//                duplicateCheckModel.setSize(size);
//                duplicateCheckModel.setVideoDuration(duration);
//                duplicateCheckModel.setVideoTitle(title);
//                duplicateCheckModel.setVideoUri(uri);
//                duplicateCheckModel.setDoubleCount(1);


                // doubleCheckMap.put(duration, doubleCheckMap.containsKey(duration) ? doubleCheckMap.get(duration) + 1 : 1);


                if (videoModelkMap.containsKey(duration)) {

                    VideoModel doubleCheckModel = videoModelkMap.get(duration);

                    if (doubleCheckModel != null && doubleCheckModel.getSize().equals(size)) {

                        doubleCheckModel.setDoubleCount(doubleCheckModel.getDoubleCount() + 1);

                        doubleCheckMap.put(duration, doubleCheckModel);


//                        if (doubleCheckMap.containsKey(duration)) {
//
//
//                            int count = doubleCountMap.get(duration)+1;
//                           // doubleCountMap.put(duration,count);
//
//                            //doubleCheckMap.put(duration, videoModel);
//                            videoModel.setDoubleCount(count+1);
//                            doubleCheckMap.put(duration, videoModel);
//
//
//                        } else {
////                            doubleCountMap.put(duration,2);
//                            videoModel.setDoubleCount(2);
//                            doubleCheckMap.put(duration, videoModel);
//                        }


                    }


                } else {
                    videoModelkMap.put(duration, videoModel);

                }


            } while (cursor.moveToNext());
        }

        for (Map.Entry<String, VideoModel> doubleCheckMap : doubleCheckMap.entrySet()) {
            videoArrayList.add(doubleCheckMap.getValue());
        }

        VideoAdapter adapter = new VideoAdapter(requireContext(), videoArrayList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                Intent intent = new Intent(requireContext(), VideoPlay2Activity.class);
                intent.putExtra("pos", pos);
                startActivity(intent);
            }
        });

    }

    //time conversion
    public String timeConversion(long value) {
        String videoTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
    }

    //runtime storage permission
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(requireContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                    } else {
                        videoList();
                    }
                }
            }
        }
    }
}
