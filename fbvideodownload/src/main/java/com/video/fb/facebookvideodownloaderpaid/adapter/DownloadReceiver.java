package com.video.fb.facebookvideodownloaderpaid.adapter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.video.fb.facebookvideodownloaderpaid.DBHelper;
import com.video.fb.facebookvideodownloaderpaid.FacebookFragment;
import com.video.fb.facebookvideodownloaderpaid.HomeFragment;
import com.video.fb.facebookvideodownloaderpaid.PrefManager;

import java.util.ArrayList;


/**
 * Created by Adi on 12-02-2017.
 */
public  class DownloadReceiver extends BroadcastReceiver {
    private DBHelper mDatabase;
    private static final String tag = DownloadReceiver.class.getSimpleName();
    DownloadManager dmo;
    private PrefManager pref;
    private HomeFragment homeFragment;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        mDatabase = new DBHelper(context);
        if ("android.intent.action.DOWNLOAD_COMPLETE".equals(action)) {
            Bundle extras = intent.getExtras();
            dmo=(DownloadManager)context.getSystemService(context.DOWNLOAD_SERVICE);
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor c = dmo.query(q);
            ArrayList<String> xcoords = (new FacebookFragment()).getList();
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    xcoords.remove(c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI)));
                    pref = new PrefManager(context);
                    Toast.makeText(context,"Download Completed",Toast.LENGTH_LONG).show();
                    String title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    String path= Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath();
                    long size=(c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES )));
                    Bitmap curThumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
                    mDatabase.addRecording(title, path,size,curThumb);
                    mDatabase.close();
                }
                else
                {
                    xcoords.remove(c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI)));
                }
            }
            c.close();
        }
        else if ("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED".equals(action))
        {
       //     Log.d(tag,"Notification clicked");
        }
        else
        {
         //   Log.d(tag,action);
        }
    }
}
