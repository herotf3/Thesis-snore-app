package com.mt.waveformdemo.Audio.processor;

import android.util.Log;

import com.mt.waveformdemo.Audio.data.AudioFrame;
import com.mt.waveformdemo.Audio.utils.Converter;

import static com.mt.waveformdemo.Audio.utils.Converter.linearToDecibel;


/**
 * Created by macbook on 4/1/19.
 */

public class HighEnergyChecker implements AudioCheckingProcessor {
    private final float DEFAULT_THRESHOLD = 0;
    private float threshold = 0f;   //db

    public HighEnergyChecker() {
        this.threshold = DEFAULT_THRESHOLD;
    }

    public HighEnergyChecker(float threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean check(AudioFrame frame) {
        return localEnergy(frame) > threshold;
    }

    private double soundPressureLevel(final AudioFrame frame) {
        double e = localEnergy(frame);
        double value = Math.pow(e, 0.5);
        value = value / frame.getnSamples();
        double db = linearToDecibel(value);
        Log.d("Energy Checker", "frame db = " + db + ", E= "+e );
        return db;
    }

    private double localEnergy(final AudioFrame frame) {
        double e = 0;

        for (int i = 0; i < frame.getnSamples(); i++) {
            double s = frame.getFloatSample(i);
            e += s * s;
        }
        double db = linearToDecibel(e);
        Log.d("Energy Checker", "frame db = " + db + ", E= "+e );
        return db;
    }
}

