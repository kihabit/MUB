package com.prayosof.yvideo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.EntypoModule;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.prayosof.yvideo.helper.SessionManager;
import com.prayosof.yvideo.services.NewMediaPlayerService;
import com.prayosof.yvideo.view.activity.IntroSliderActivity;
import com.prayosof.yvideo.view.activity.video.VideoPlayerActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavView;
    private Boolean isPinchToZoomSet;
    private SessionManager sessionManager;
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private InAppUpdateManager inAppUpdateManager;

    private ImageView ivModeIcon;
    private Switch switchDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ivModeIcon = (ImageView) findViewById(R.id.modeicon);
        switchDarkTheme = (Switch) findViewById(R.id.darkMode);
        sessionManager = new SessionManager(this);

        if (sessionManager.getDarkModeValue()) {
            ivModeIcon.setImageDrawable(getResources().getDrawable(R.mipmap.moon));
            switchDarkTheme.setChecked(true);
            sessionManager.setDarkThemeMode(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            ivModeIcon.setImageDrawable(getResources().getDrawable(R.mipmap.sun_cloud));
            switchDarkTheme.setChecked(false);
            sessionManager.setDarkThemeMode(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

//        ivModeIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, IntroSliderActivity.class));
//            }
//        });

        switchDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ivModeIcon.setImageDrawable(getResources().getDrawable(R.mipmap.moon));
                    switchDarkTheme.setChecked(true);
                    sessionManager.setDarkThemeMode(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    ivModeIcon.setImageDrawable(getResources().getDrawable(R.mipmap.sun_cloud));
                    switchDarkTheme.setChecked(false);
                    sessionManager.setDarkThemeMode(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                startActivity(new Intent(MainActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
            }
        });

        initToolbar();
        initFab();
        initNavigation();
        //showBottomNavigation(false);

        //Button test = findViewById(R.id.test);

//        test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openOnBOard();
//            }
//        });
    }

//    public void openOnBOard() {
//        Intent i = new Intent(this, OnBoardingActivity.class);
//        startActivity(i);
//    }

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void initFab() {

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initNavigation() {

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavView = findViewById(R.id.bottom_nav_view);

        if (sessionManager.getDarkModeValue()) {
            bottomNavView.setBackgroundColor(getResources().getColor(R.color.black));
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_audios, R.id.nav_duplicate_directory, R.id.nav_setting,
                R.id.nav_tools, R.id.nav_feedback, R.id.nav_faq, R.id.nav_about_us, R.id.bottom_videos_list,
                R.id.bottom_music, R.id.bottom_punjabi, R.id.nav_download)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.nav_duplicate_directory) {
                    bottomNavView.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.nav_setting) {
                    //bottomNavView.getMenu().setGroupCheckable(0, false, true);

                } else if (destination.getId() == R.id.nav_download) {
                    bottomNavView.setVisibility(View.GONE);
                } else {
                    bottomNavView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void checkForUpdates() {
        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
                .resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
                .mode(Constants.UpdateMode.IMMEDIATE);

        inAppUpdateManager.checkForAppUpdate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menu.findItem(R.id.action_share_app).setVisible(true);
        if (sessionManager.getDarkModeValue()) {
            Spannable spannable = new SpannableString("Share App");
            spannable.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannable.length(), 0);
            menu.findItem(R.id.action_share_app).setTitle(spannable);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case R.id.action_share_app:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
                return true;

            case R.id.action_download:
                // Some other methods
                return true;

            case R.id.action_settings:
                // Some other methods
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

//        switch (item.getItemId()) {
//            case R.id.action_share_app:
//                try {
//                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                    shareIntent.setType("text/plain");
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Video player");
//                    String shareMessage= "\nLet me recommend you this application\n\n";
//                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
//                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                    startActivity(Intent.createChooser(shareIntent, "choose one"));
//                } catch(Exception e) {
//                    //e.toString();
//                }
//                break;
//        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // If the update is cancelled by the user,
                // you can request to start the update again.
                inAppUpdateManager.checkForAppUpdate();

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        int backStackEntryCount = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment).getChildFragmentManager().getBackStackEntryCount();

        Log.e("backStackEntryCount", backStackEntryCount + "");
        MainActivity.super.onBackPressed();

        /*if (backStackEntryCount == 0) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                             MainActivity.super.onBackPressed();
                            //MainActivity.super.finish();
                            //MainActivity.this.finishAffinity();
                            Log.e("Finish service", "Finish");
//                            Intent playerIntent = new Intent(MainActivity.this, NewMediaPlayerService.class);
////                            stopService(playerIntent);
////                            if(Build.VERSION.SDK_INT < 21){
////                                finishAffinity();
////
////                                                final Handler handler = new Handler(Looper.getMainLooper());
////                handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        System.exit(0);
////                    }
////                }, 2000);
////
////
////
////                            } else {
////                                Log.e("Finish service", "Finish");
////                                finishAndRemoveTask();
////
////                                final Handler handler = new Handler(Looper.getMainLooper());
////                                handler.postDelayed(new Runnable() {
////                                    @Override
////                                    public void run() {
////                                        System.exit(0);
////                                    }
////                                }, 2000);
////
////                            }

                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            MainActivity.super.onBackPressed();
        }*/


    }

    private void showBothNavigation(boolean isShowable) {
        navigationView.setVisibility(isShowable ? View.VISIBLE : View.GONE);
        bottomNavView.setVisibility(isShowable ? View.VISIBLE : View.GONE);
    }

    private void showBottomNavigation(boolean isShowable) {
        bottomNavView.setVisibility(isShowable ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.getDarkModeValue()) {
            bottomNavView.setBackgroundColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionManager.setMusicServiceRunning(false);

        if (NewMediaPlayerService.isRunning()) {
//            Intent playerIntent = new Intent(MainActivity.this, NewMediaPlayerService.class);
//            stopService(playerIntent);

        }
    }
}