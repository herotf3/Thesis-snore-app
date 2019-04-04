package com.mt.waveformdemo.Audio.processor;

import android.util.Log;

import com.mt.waveformdemo.Audio.Converter;
import com.mt.waveformdemo.Audio.type.AudioFrame;

/**
 * Created by macbook on 4/1/19.
 */

public class HighEnergyChecker implements AudioCheckingProcessor {
    private float threshold = 0f;   //db
    private final float DEFAULT_THRESHOLD = 0f;

    public HighEnergyChecker() {
        this.threshold = DEFAULT_THRESHOLD;
    }

    public HighEnergyChecker(float threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean check(AudioFrame frame) {
        double e = 0;

        for (int i = 0; i < frame.nSampleInFrame; i++) {
            float s = frame.getFloatSample(i);
            e += s * s;
        }
        double db = Converter.linearToDecibel(e);
        Log.d("Energy Checker", "frame db = " + db + " , energy=" + e);
        return db > threshold;
    }
}
