package com.prayosof.yvideo.view.fragment.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.VideosListAdpter;
import com.prayosof.yvideo.helper.DialogHelper;
import com.prayosof.yvideo.model.ModelVideos;
import com.prayosof.yvideo.view.fragment.home.HomeViewModel;

import java.io.File;
import java.util.ArrayList;

public class VideosListFragment extends Fragment implements VideosListAdpter.MediaListner {

    private HomeViewModel homeViewModel;
    private static final int PERMISSION_REQ_WRITE_EXTERNAL_STORAGE = 689;
    boolean boolean_folder;
    public static VideosListAdpter obj_adapter;
    private MenuItem mRefreshMenuItem;

    public static ArrayList<ModelVideos> videosModelList = new ArrayList<>();

    private static final int REQUEST_PERMISSIONS = 100;

    int column_index_data, column_index_folder_name;
    RecyclerView rvVideos;

    ProgressBar progressbar;
    View root;
    Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);
        mContext = getContext();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_videos_list, container, false);

        // Lookup the recyclerview in activity layout
        progressbar = root.findViewById(R.id.progressbar);
        rvVideos = (RecyclerView) root.findViewById(R.id.rvVideosFolders);
        rvVideos.setHasFixedSize(true);
        rvVideos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        getPermision();

//        RelativeLayout rrRecentVideos = root.findViewById(R.id.rrRecentVideos);
//        rrRecentVideos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Bundle bundle = new Bundle();
//                bundle.putInt("value", 0);
//                Navigation.findNavController(requireView()).navigate(R.id.mediaListFragment, bundle);
//
//            }
//        });
        return root;

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getPermision() {

        if ((ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {


            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
//                ActivityCompat.requestPermissions(requireActivity(),
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
//                        REQUEST_PERMISSIONS);
            }
        } else {
            //Log.e("Else","Else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getVideosPath();
            } else {
                getVideosPath();
            }
        }
    }

    @SuppressLint("Recycle")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getVideosPath() {
        videosModelList.clear();

        Log.d("videosModelList", videosModelList.size() + "");

        int int_position = 0;
        boolean_folder = false;
        Uri uri;
        Cursor cursor;
        ModelVideos e = new ModelVideos();
        e.setStr_folder("Recent Added");
        ArrayList<String> recentsPath = new ArrayList<>();

        // e.setAllVideoPath(recentsPath);
        int recentMaxCount = 0;
//        videosModelList.add(e);

        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = requireActivity().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);

        }
        if (cursor != null) {
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        }
        assert cursor != null;

        while (cursor.moveToNext()) {

//            else if (recentMaxCount == 50) {
//                e.setAllVideoPath(recentsPath);
//                videosModelList.add(e);
//                recentMaxCount++;
//            }
            absolutePathOfImage = cursor.getString(column_index_data);
            File tempFile = null;
            if (absolutePathOfImage != null) {
                tempFile = new File(absolutePathOfImage);
            }
            if (tempFile != null && tempFile.exists()) {
                if (recentMaxCount < 50) {
                    recentsPath.add(absolutePathOfImage);
                    recentMaxCount++;
                }
            }


            if (absolutePathOfImage != null) {
                Log.v("Column", absolutePathOfImage);
            }
            String foldername = cursor.getString(column_index_folder_name);
            if (foldername == null)
                foldername = "Root";
            Log.v("Folder", foldername);

            for (int i = 0; i < videosModelList.size(); i++) {
                if (videosModelList.get(i).getStr_folder().equals(foldername)) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            Log.d("boolean_folder", boolean_folder + "");


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
//                if (videosModelList.size() == 0 && int_position == 0) {
//                    return;
//                }
                al_path.addAll(videosModelList.get(int_position).getAllVideoPath());
                al_path.add(absolutePathOfImage);
                videosModelList.get(int_position).setAllVideoPath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                ModelVideos obj_model = new ModelVideos();
                obj_model.setStr_folder(foldername);
                obj_model.setAllVideoPath(al_path);

                videosModelList.add(obj_model);

            }

        }


        e.setAllVideoPath(recentsPath);
        videosModelList.add(0, e);
        for (int i = 0; i < videosModelList.size(); i++) {
            //Log.e("FOLDER", videosModelList.get(i).getStr_folder());
            for (int j = 0; j < videosModelList.get(i).getAllVideoPath().size(); j++) {
                //Log.e("FILE", videosModelList.get(i).getAllVideoPath().get(j));
            }
        }


//        ModelVideos last = videosModelList.get(videosModelList.size() - 1);
//        videosModelList.remove(videosModelList.size() - 1);
//        videosModelList.add(0, last);

        obj_adapter = new VideosListAdpter(getActivity(), videosModelList, this);

        rvVideos.setAdapter(obj_adapter);

        //gv_folder.setAdapter(obj_adapter);
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getVideosPath();
                    }
                } else {
                    Toast.makeText(requireActivity(), "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onItemClick(int pos) {


//        MediaListFragment nextFrag= new MediaListFragment();
//        Bundle args = new Bundle();
//        args.putInt("value", pos);
//        nextFrag.setArguments(args);

//        MediaListFragment nextFrag= new MediaListFragment();
//        Bundle args = new Bundle();
//        args.putInt("value", pos);
//        nextFrag.setArguments(args);
//
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_host_fragment, nextFrag, "findThisFragment")
//                .addToBackStack(null)
//                .commit();
        Bundle bundle = new Bundle();
        bundle.putInt("value", pos);
        Navigation.findNavController(requireView()).navigate(R.id.mediaListFragment, bundle);

//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_host_fragment, nextFrag, "findThisFragment")
//                .addToBackStack(null)
//                .commit();
    }

    @Override
    public void onItemDelete(final int pos) {
        DialogHelper.showYesNoDialog(getContext(), "Are you sure You want to delete all videos belong to this Folder?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteAllFies(pos);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        });
    }

    private void deleteAllFies(final int pos) {
        progressbar.setVisibility(View.VISIBLE);
        rvVideos.setVisibility(View.GONE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String videoPath : videosModelList.get(pos).getAllVideoPath()) {
                        deleteFile(videoPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Handler mainHandler = new Handler(mContext.getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            refreshVideo();
                        } // This is your code
                    };
                    mainHandler.post(myRunnable);
                }
            }
        });
    }

    private void deleteFile(String path) {
        final String[] selectionArgs = new String[]{path};


        // Set up the projection (we only need the ID)
        String[] projection = {MediaStore.Video.Media._ID};

        // Match on the file path
        String selection = MediaStore.Video.Media.DATA + " = ?";


        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);

        if (c != null) {
            if (c.moveToFirst()) {
                // We found the ID. Deleting the item via the content provider will also remove the file
                long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                Uri deleteUri = ContentUris.withAppendedId(queryUri, id);
                contentResolver.delete(deleteUri, null, null);

            } else {
                // File not found in media store DB
            }
            c.close();
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.refresh_ic).setVisible(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_ic) {
            refreshVideo();
            //Toast.makeText(requireContext(), "d", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshVideo() {
        progressbar.setVisibility(View.VISIBLE);
        rvVideos.setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            public void run() {
                getPermision();
                progressbar.setVisibility(View.GONE);
                rvVideos.setVisibility(View.VISIBLE);
            }
        }, 1000);   //1 seconds
    }
}