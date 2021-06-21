package com.prayosof.yvideo.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.view.fragment.IntroSlides;

public class IntroSliderActivity extends AppIntro {

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        super.init(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(IntroSlides.newInstance(R.layout.intro_slide1));
        addSlide(IntroSlides.newInstance(R.layout.intro_slide2));
        addSlide(IntroSlides.newInstance(R.layout.intro_slide3));
        addSlide(IntroSlides.newInstance(R.layout.intro_slide4));

        //Animation call
        setFlowAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}