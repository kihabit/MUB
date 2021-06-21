package com.prayosof.yvideo.view.browser.fragments;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.view.browser.adapters.WebDownloadAdapter;
import com.prayosof.yvideo.interfaces.CustomOnClickListener;
import com.prayosof.yvideo.view.browser.models.WebDownloadModel;
import com.prayosof.yvideo.view.fragment.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class WebFileViewerFragment extends BaseFragment implements CustomOnClickListener {

    private View view;
    public static ImageView ivDelete;
    private RecyclerView rvVideos;

    public static String type = "";
    public static int count = 0;
    public static ArrayList<WebDownloadModel> arrayList;
    private ArrayList<File> list;
    public static WebDownloadAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_web_file_viewer, container, false);

        ivDelete = (ImageView) view.findViewById(R.id.iv_web_delete);
        rvVideos = (RecyclerView) view.findViewById(R.id.rv_web_videos);

        arrayList = new ArrayList<WebDownloadModel>();
        arrayList.clear();
        list = new ArrayList<File>();
        list.clear();
        list = getListFiles(new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/"));
        for (int i = 0; i < list.size(); i++) {
            WebDownloadModel model = new WebDownloadModel();
            model.setPath(list.get(i).getAbsolutePath());
            model.setSelected(false);
            arrayList.add(model);
        }

//        Collections.reverse(arrayList);
        rvVideos.setHasFixedSize(true);
        adapter = new WebDownloadAdapter(getActivity(), arrayList, this);
        rvVideos.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ivDelete.setClickable(false);
        ivDelete.setEnabled(false);
        ivDelete.setColorFilter(getResources().getColor(R.color.black));

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you really want to delete files?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.get(i).isSelected()) {
                                File file = new File(arrayList.get(i).getPath());
                                file.delete();
                                arrayList.remove(i);
                                adapter.notifyItemRemoved(i);
                                adapter.notifyItemRangeChanged(i, arrayList.size());
                                i--;
                            }
                        }
                        count = 0;
                        ivDelete.setClickable(false);
                        ivDelete.setEnabled(false);
                        ivDelete.setColorFilter(getResources().getColor(R.color.black));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type.equals("videos")) {
                            for (int i = 0; i < arrayList.size(); i++) {
                                arrayList.get(i).setSelected(false);
                            }
                            adapter.updateList(arrayList);
                        }
                        count = 0;
                        ivDelete.setClickable(false);
                        ivDelete.setEnabled(false);
                        ivDelete.setColorFilter(getResources().getColor(R.color.black));

                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        arrayList.clear();
        list.clear();
        list = getListFiles(new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + "/"));//+ "/Music Bazaar/"
        for (int i = 0; i < list.size(); i++) {
            WebDownloadModel model = new WebDownloadModel();
            model.setPath(list.get(i).getAbsolutePath());
            model.setSelected(false);
            arrayList.add(model);
        }

//        Collections.reverse(arrayList);
        rvVideos.setHasFixedSize(true);
        adapter = new WebDownloadAdapter(getActivity(), arrayList, this);
        rvVideos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files;
        files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {

                if (file.getName().endsWith(".mp4")) {
                    if (!inFiles.contains(file))
                        inFiles.add(file);
                }
            }
        }

        return inFiles;
    }

    @Override
    public void onShareClick(String fileName, String filePath) {
        startFileShareIntent(filePath);
    }
}