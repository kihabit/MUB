package com.prayosof.yvideo.model;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

/**
 * Created by Yogesh Y. Nikam on 11/07/20.
 */
public final class EffectsFragmentModel {

    private final ObservableBoolean bassBoostEnabled = new ObservableBoolean();
    private final ObservableInt bassBoostStrength = new ObservableInt();

    private final ObservableBoolean equalizerEnabled = new ObservableBoolean();

    public ObservableBoolean isBassBoostEnabled() {
        return bassBoostEnabled;
    }

    public void setBassBoostEnabled(final boolean enabled) {
        bassBoostEnabled.set(enabled);
    }

    public ObservableInt getBassBoostStrength() {
        return bassBoostStrength;
    }

    public void setBassBoostStrength(final int strength) {
        bassBoostStrength.set(strength);
    }

    public ObservableBoolean isEqualizerEnabled() {
        return equalizerEnabled;
    }

    public void setEqualizerEnabled(final boolean equalizerEnabled) {
        this.equalizerEnabled.set(equalizerEnabled);
    }
}
