package com.prayosof.yvideo.view.fragment.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.AdapterVideosFolder;
import com.prayosof.yvideo.model.ModelVideos;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private static final int PERMISSION_REQ_WRITE_EXTERNAL_STORAGE = 689;


    public static ArrayList<ModelVideos> videosModelList = new ArrayList<>();
    boolean boolean_folder;
    AdapterVideosFolder obj_adapter;
    GridView gv_folder;
    private static final int REQUEST_PERMISSIONS = 100;

    int column_index_data, column_index_folder_name;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        gv_folder = (GridView) root.findViewById(R.id.gv_folder);


        gv_folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(getActivity(), PhotosActivity.class);
//                intent.putExtra("value",i);
//                startActivity(intent);

                MusicListFragment nextFrag= new MusicListFragment();
                Bundle args = new Bundle();
                args.putInt("value", i);
                nextFrag.setArguments(args);
                //Navigation.findNavController(requireView()).navigate(R.id.bottom_music);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });


        if ((ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            //Log.e("Else","Else");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getVideosPath();
            } else {
                getVideosPath();
            }
        }
        return root;
    }

    @SuppressLint("Recycle")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getVideosPath() {
        videosModelList.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        long id=0;


       String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = requireActivity().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        }
        if (cursor != null) {
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        }
        assert cursor != null;
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
//            Log.e("Column", absolutePathOfImage);
//            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < videosModelList.size(); i++) {
                if (videosModelList.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }


            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(videosModelList.get(int_position).getAllVideoPath());
                al_path.add(absolutePathOfImage);
                videosModelList.get(int_position).setAllVideoPath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                ModelVideos obj_model = new ModelVideos();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setAllVideoPath(al_path);

                videosModelList.add(obj_model);


            }


        }


        for (int i = 0; i < videosModelList.size(); i++) {
            //Log.e("FOLDER", videosModelList.get(i).getStr_folder());
            for (int j = 0; j < videosModelList.get(i).getAllVideoPath().size(); j++) {
                //Log.e("FILE", videosModelList.get(i).getAllVideoPath().get(j));
            }
        }
        obj_adapter = new AdapterVideosFolder(getActivity(), videosModelList);
        gv_folder.setAdapter(obj_adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        getVideosPath();
                    }
                } else {
                    Toast.makeText(requireActivity(), "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



}