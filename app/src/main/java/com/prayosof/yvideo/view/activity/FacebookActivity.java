package com.prayosof.yvideo.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.exoplayer2.util.Log;
import com.google.android.material.tabs.TabLayout;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.view.fragment.online.FileViewerFragment;
import com.prayosof.yvideo.view.fragment.online.UrlDownloadFragment;
import com.prayosof.yvideo.view.browser.fragments.WebDownloadFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FacebookActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.ic_baseline_home_24,
            R.drawable.ic_baseline_video_library_24,
            R.drawable.ic_baseline_file_copy_24
    };
    private final String TAGS = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_online_facebook);

        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        // Monitor launch times and interval from installation
        // Custom condition: 3 days and 5 launches


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_album, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (item.getItemId()) {
            case R.id.menu_rate:
                intent.setData(Uri.parse("market://details?id=com.video.fb.facebookvideodownloaderpaid"));
                startActivity(intent);
                return true;
            case R.id.MoreApps:
                intent.setData(Uri.parse("market://search?q=pub:Precode apps"));
                startActivity(intent);
                return true;
            case R.id.Share_This_App:
                shareApp();
                return true;
            case R.id.About:
                showAbout();
                return true;
            case R.id.clear_app_data:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Clear App Data?");
                alertDialog.setMessage("Do you Really want to clear Application Data ?This will clear App Data but will not delete Databases!");
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        clearApplicationData(getApplicationContext());
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAbout() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        TextView textView = (TextView) messageView.findViewById(R.id.title);
        TextView textView1 = (TextView) messageView.findViewById(R.id.about_credits);
        TextView textView2 = (TextView) messageView.findViewById(R.id.textView2);
        textView.setTextColor(Color.DKGRAY);
        textView1.setTextColor(Color.DKGRAY);
        textView2.setTextColor(Color.DKGRAY);
        String tex = "Facebook Video Downloader enables you to download videos from facebook while browsing directly to your device while taking your minimal storage.\nPlay videos at a later time , share the videos through whatsapp , gmail with your friends .\n\nIf you like it, share with your friends .\n\nThis App is Not Associated to Facebook Organization in any form .Any Unauthorized downloading or re-uploading of contents and/or violations of intellectual property rights is the sole responsibility of the user .\n \nHappy Browsing :) ";
        textView2.setText(tex);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    public void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                File f = new File(appDir, s);
                if (deleteDir(f))
                    Log.i(TAGS, String.format("**************** DELETED -> (%s) *******************", f.getAbsolutePath()));
            }
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (!(dir.toString().contains("shared_prefs") || dir.toString().contains("databases"))) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    public void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Facebook Video Downloader");
            String sAux = "\nDo you want to Download PPlayer ? .Install this App , Its Amazing :). \n\n";
            sAux = sAux + "";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Share this App"));
        } catch (Exception e) {
            //e.toString();
        }
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new FaceBookHomeFragment(), "Home");
        adapter.addFragment(new WebDownloadFragment(), "Home");
        adapter.addFragment(new FileViewerFragment(), "Videos List");
        adapter.addFragment(new UrlDownloadFragment(), "Paste Link");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private String[] titles = {"Home", "Videos List", "Paste Link"};

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return WebDownloadFragment.newInstance(position);
                }
                case 1: {
                    return FileViewerFragment.newInstance(position);
                }
                case 2: {
                    return UrlDownloadFragment.newInstance(position);
                }
            }
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

//    private Button downloadButton;
//    String fileN = null ;
//    public static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
//    boolean result;
//    String urlString;
//    Dialog downloadDialog;
//
//    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//       // setTheme(Constant.theme);
//        setContentView(R.layout.activity_facebook); result = checkPermission();
//        if(result){
//            checkFolder();
//        }
//        if (!isConnectingToInternet(getApplicationContext())) {
//            Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_LONG).show();
//        }
//
//        downloadButton = (Button) DialogView.findViewById(R.id.downloadButton);
//        downloadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(FacebookActivity.this, "Downloading", Toast.LENGTH_SHORT).show();
//                newDownload(urlString);
//                main_dialog.dismiss();
//            }
//        });
//    }
//
//    public static boolean isConnectingToInternet(Context context) {
//        ConnectivityManager cm =
//                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        return activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//    }
//
//
//    // DownloadTask for downloding video from URL
//    public class DownloadTask extends AsyncTask<String, Integer, String> {
//        private Context context;
//        private PowerManager.WakeLock mWakeLock;
//        public DownloadTask(Context context) {
//            this.context = context;
//        }
//        private NumberProgressBar bnp;
//        @Override
//        protected String doInBackground(String... sUrl) {
//            InputStream input = null;
//            OutputStream output = null;
//            HttpURLConnection connection = null;
//            try {
//                java.net.URL url = new URL(sUrl[0]);
//                connection = (HttpURLConnection) url.openConnection();
//                connection.connect();
//
//                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                    return "Server returned HTTP " + connection.getResponseCode()
//                            + " " + connection.getResponseMessage();
//                }
//
//                int fileLength = connection.getContentLength();
//
//                input = connection.getInputStream();
//                fileN = "FbDownloader_" + UUID.randomUUID().toString().substring(0, 10) + ".mp4";
//                File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "Yogesh", fileN);
//                output = new FileOutputStream(filename);
//
//                byte data[] = new byte[4096];
//                long total = 0;
//                int count;
//                while ((count = input.read(data)) != -1) {
//                    if (isCancelled()) {
//                        input.close();
//                        return null;
//                    }
//                    total += count;
//                    if (fileLength > 0)
//                        publishProgress((int) (total * 100 / fileLength));
//                    output.write(data, 0, count);
//                }
//            } catch (Exception e) {
//                return e.toString();
//            } finally {
//                try {
//                    if (output != null)
//                        output.close();
//                    if (input != null)
//                        input.close();
//                } catch (IOException ignored) {
//                }
//
//                if (connection != null)
//                    connection.disconnect();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    getClass().getName());
//            mWakeLock.acquire();
//            LayoutInflater dialogLayout = LayoutInflater.from(FacebookActivity.this);
//            View DialogView = dialogLayout.inflate(R.layout.progress_dialog, null);
//            downloadDialog = new Dialog(FacebookActivity.this, R.style.CustomAlertDialog);
//            downloadDialog.setContentView(DialogView);
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            lp.copyFrom(downloadDialog.getWindow().getAttributes());
//            lp.width = (getResources().getDisplayMetrics().widthPixels);
//            lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
//            downloadDialog.getWindow().setAttributes(lp);
//
//            final Button cancel = (Button) DialogView.findViewById(R.id.cancel_btn);
//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //stopping the Asynctask
//                    cancel(true);
//                    downloadDialog.dismiss();
//
//                }
//            });.
//
//            downloadDialog.setCancelable(false);
//            downloadDialog.setCanceledOnTouchOutside(false);
//            bnp = (NumberProgressBar)DialogView.findViewById(R.id.number_progress_bar);
//            bnp.setProgress(0);
//            bnp.setMax(100);
//            downloadDialog.show();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            super.onProgressUpdate(progress);
//            bnp.setProgress(progress[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            mWakeLock.release();
//            downloadDialog.dismiss();
//            if (result != null)
//                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
//            else
//                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
//            MediaScannerConnection.scanFile(FacebookActivity.this,
//                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() +
//                            Constants.FOLDER_NAME + fileN}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String newpath, Uri newuri) {
//                            Log.i("ExternalStorage", "Scanned " + newpath + ":");
//                            Log.i("ExternalStorage", "-> uri=" + newuri);
//                        }
//                    });
//
//        }
//    }
//
//    //hare you can start downloding video
//    public void newDownload(String url) {
//        final DownloadTask downloadTask = new DownloadTask(FacebookActivity.this);
//        downloadTask.execute(url);
//    }
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    public boolean checkPermission() {
//        int currentAPIVersion = Build.VERSION.SDK_INT;
//        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(FacebookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(FacebookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(FacebookActivity.this);
//                    alertBuilder.setCancelable(true);
//                    alertBuilder.setTitle("Permission necessary");
//                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
//                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                        public void onClick(DialogInterface dialog, int which) {
//                            ActivityCompat.requestPermissions(FacebookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
//                        }
//                    });
//                    AlertDialog alert = alertBuilder.create();
//                    alert.show();
//                } else {
//                    ActivityCompat.requestPermissions(FacebookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
//                }
//                return false;
//            } else {
//                return true;
//            }
//        } else {
//            return true;
//        }
//    }
//
//
//    public void checkAgain() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(FacebookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(FacebookActivity.this);
//            alertBuilder.setCancelable(true);
//            alertBuilder.setTitle("Permission necessary");
//            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!");
//            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                public void onClick(DialogInterface dialog, int which) {
//                    ActivityCompat.requestPermissions(FacebookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
//                }
//            });
//            AlertDialog alert = alertBuilder.create();
//            alert.show();
//        } else {
//            ActivityCompat.requestPermissions(FacebookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
//        }
//    }
//
//
//    //Here you can check App Permission
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    checkFolder();
//                } else {
//                    //code for deny
//                    checkAgain();
//                }
//                break;
//        }
//    }
//
//    //hare you can check folfer whare you want to store download Video
//    public void checkFolder() {
//        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FBDownloader/";
//        File dir = new File(path);
//        boolean isDirectoryCreated = dir.exists();
//        if (!isDirectoryCreated) {
//            isDirectoryCreated = dir.mkdir();
//        }
//        if (isDirectoryCreated) {
//            // do something\
//            Log.d("Folder", "Already Created");
//        }
//    }