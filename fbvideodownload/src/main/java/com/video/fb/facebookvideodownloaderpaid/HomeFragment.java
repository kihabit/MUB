package com.video.fb.facebookvideodownloaderpaid;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Adi on 25-03-2017.
 */
public class HomeFragment extends Fragment {

    public static final String VIDEO_DW_DIR= "MUB Downloads";
    private static final String ARG_POSITION = "position";
    private View mainView;
    EditText editText;
    Button btnGo, btnClear;
    MaterialCardView cvFb;
    FacebookFragment facebookFragment;
    private PrefManager pref;
    public static HomeFragment newInstance(int position) {
        HomeFragment f = new HomeFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_home_fb,container,false);
        facebookFragment=new FacebookFragment();
        editText= (EditText) mainView.findViewById(R.id.editText);
        btnGo = (Button) mainView.findViewById(R.id.btnGo);
        btnClear = (Button) mainView.findViewById(R.id.btnClear);
        cvFb= (MaterialCardView) mainView.findViewById(R.id.cvFb);
        pref = new PrefManager(getContext());

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=editText.getText().toString();
                if(url.length()==0)
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Please Paste or Enter the Url",Toast.LENGTH_SHORT).show();
                }
                else if(!URLUtil.isValidUrl(url) || !Patterns.WEB_URL.matcher(url).matches())
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Please Enter a valid Url",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    downloadVideo(url);
                }
            }
        });

        cvFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getChildFragmentManager().beginTransaction().replace(R.id.root_frame, FacebookFragment.newInstance(0)).commit();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        return mainView;
    }
    private void downloadVideo(String pathvideo) {
        /*if (!(pathvideo.contains("fbcdn")))
        {
            Toast.makeText(getContext(), "Please Enter Only Video Url", Toast.LENGTH_LONG).show();
        }*/
        if(pathvideo.contains("story"))
        {
            facebookFragment.getUrlfromUrlDownload(pathvideo);
        }
        else
        {
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + HomeFragment.VIDEO_DW_DIR);
            directory.mkdirs();
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pathvideo));
            request.allowScanningByMediaScanner();
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            File root = new File(Environment.getExternalStorageDirectory() + File.separator + VIDEO_DW_DIR);
            int Number = pref.getFileName();
            Uri path = Uri.withAppendedPath(Uri.fromFile(root), "Video-" + Number + ".mp4");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"Video-"+Number+".mp4");
            DownloadManager dm = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
            ArrayList<String> urldownloadFragmentList = (new FacebookFragment()).getList();
            if(urldownloadFragmentList.contains(pathvideo))
            {
                Toast.makeText(getActivity().getApplicationContext(),"The Video is Already Downloading",Toast.LENGTH_LONG).show();
            }
            else
            {
                urldownloadFragmentList.add(pathvideo);
                dm.enqueue(request);
                Toast.makeText(getActivity().getApplicationContext(),"Downloading Video-"+Number+".mp4",Toast.LENGTH_LONG).show();
                Number++;
                pref.setFileName(Number);
            }
        }
    }
}