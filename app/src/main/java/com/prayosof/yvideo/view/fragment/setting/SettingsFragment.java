package com.prayosof.yvideo.view.fragment.setting;

import android.app.UiModeManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.SessionManager;

public class SettingsFragment extends Fragment {

    Switch switchAutoPlayNext, switchAPinchTozoom, switchRememberBrightness, switchDarkTheme;

    SessionManager sessionManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        sessionManager = new SessionManager(requireContext());

        switchAutoPlayNext = root.findViewById(R.id.autoPlayNext);
        switchAPinchTozoom = root.findViewById(R.id.pinchZoom);
        switchRememberBrightness = root.findViewById(R.id.rememberBrightness);
        switchDarkTheme = root.findViewById(R.id.darkMode);


        if (sessionManager.getDarkModeValue()) {
            switchDarkTheme.setChecked(true);
        } else {
            switchDarkTheme.setChecked(false);
        }
        if (sessionManager.isPinchZoomEnabled()) {
            switchAPinchTozoom.setChecked(true);
        } else {
            switchAPinchTozoom.setChecked(false);
        }
        if (sessionManager.isAutoPlayNextEnabled()) {
            switchAutoPlayNext.setChecked(true);
        } else {
            switchAutoPlayNext.setChecked(false);
        }
        if (sessionManager.isBrightnessEnabled()) {
            switchRememberBrightness.setChecked(true);
        } else {
            switchRememberBrightness.setChecked(false);
        }



        switchAutoPlayNext.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sessionManager.setAutoPlayNextEnabled(true);
                } else {

                    sessionManager.setAutoPlayNextEnabled(false);
                }

            }
        });
        switchAPinchTozoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sessionManager.setPinchZoomEnabled(true);
                } else {

                    sessionManager.setPinchZoomEnabled(false);
                }

            }
        });
        switchRememberBrightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sessionManager.setBrightnessEnabled(true);
                } else {

                    sessionManager.setBrightnessEnabled(false);
                }
            }
        });
        switchDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sessionManager.setDarkThemeMode(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {

                    sessionManager.setDarkThemeMode(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

//
//                    val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//                    when (isNightTheme) {
//                        Configuration.UI_MODE_NIGHT_YES ->
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                        Configuration.UI_MODE_NIGHT_NO ->
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


            }
        });

        return root;

    }

}