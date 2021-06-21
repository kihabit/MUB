package com.prayosof.yvideo.view.fragment.video;

import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.jakewharton.rxbinding4.widget.RxSeekBar;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.databinding.FragmentEqualizerBinding;
import com.prayosof.yvideo.interfaces.AudioEffects;
import com.prayosof.yvideo.model.EffectsFragmentModel;
import com.prayosof.yvideo.view.activity.video.CustomExoPlayerActivity;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EqualizerFragment extends DialogFragment {

    private final EffectsFragmentModel model = new EffectsFragmentModel();

    int sessionId=0;
    private FragmentEqualizerBinding binding;
    Equalizer equlizer;

    public static EqualizerFragment newInstance(int sessionId) {
        EqualizerFragment f = new EqualizerFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("session_id", sessionId);

        f.setArguments(args);

        return f;
    }

    @Inject
    AudioEffects audioEffects;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_equalizer, container,
//                false);
//        Objects.requireNonNull(getDialog()).setTitle("DialogFragment");
//

        final FragmentEqualizerBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_equalizer, container, false);

        if (getArguments() != null){
            sessionId = getArguments().getInt("session_id");
        }
        // Do something else
        binding.setModel(model);
        binding.switchBassBoost.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        EqualizerFragment.this.setBassBoostEnabled(isChecked);
                    }
                });

//
//        RxSeekBar.userChanges(binding.seekBarBassBoost).subscribe(new Consumer<Integer>() {
//
//            private boolean mFirst = true;
//
////            @Override
////            public void accept(@NonNull final Integer progress) {
////                if (mFirst) {
////                    mFirst = false;
////                } else {
////                    setBassBoostStrength(progress);
////                }
////            }
////        });

        RxSeekBar.userChanges(binding.seekBarBassBoost).subscribe(new io.reactivex.rxjava3.functions.Consumer<Integer>() {
            private boolean mFirst = true;
            @Override
            public void accept(Integer progress) throws Throwable {
                if (mFirst) {
                    mFirst = false;
                } else {
                    setBassBoostStrength(progress);
                }
            }
        });

        binding.switchEqualizer.setEnabled(sessionId != 0);
        binding.switchEqualizer.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        EqualizerFragment.this.setEqualizerEnabled(isChecked);
                    }
                });

        this.binding = binding;
        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        model.setBassBoostStrength(BassBoost.PARAM_STRENGTH);
        model.setBassBoostEnabled(true);
        model.setEqualizerEnabled(true);
        binding.equalizerView.setEqualizer(equlizer);
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.equalizerView.setEqualizer(null);
    }

    private void setBassBoostEnabled(final boolean enabled) {
        model.setBassBoostEnabled(enabled);
    }

    private void setBassBoostStrength(final int strength) {
        model.setBassBoostStrength(strength);
//        audioEffects.setBassBoostStrength(strength);
    }

    private void setEqualizerEnabled(final boolean enabled) {
        model.setEqualizerEnabled(enabled);
        CustomExoPlayerActivity.mEqualizer.setEnabled(true);
        binding.equalizerView.setEqualizer(CustomExoPlayerActivity.mEqualizer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private final Observer mAudioEffectsObserver = new Observer() {
        @Override
        public void update(Observable observable, Object data) {
            binding.equalizerView.setEqualizer(CustomExoPlayerActivity.mEqualizer);
            binding.switchEqualizer.setEnabled(sessionId != 0);
        }
    };

}