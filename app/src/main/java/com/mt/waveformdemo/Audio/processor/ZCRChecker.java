package com.mt.waveformdemo.Audio.processor;

import com.mt.waveformdemo.Algorithim.ZeroCrossingRate;
import com.mt.waveformdemo.Audio.data.AudioFrame;

/**
 * Created by macbook on 4/16/19.
 */

public class ZCRChecker implements AudioCheckingProcessor {
    public static final float DEFAULT_THRESHOLD = 0;
    private float threshold;

    public ZCRChecker(float threshold) {
        this.threshold = threshold;
    }

    public ZCRChecker() {
        threshold = DEFAULT_THRESHOLD;
    }

    @Override
    public boolean check(AudioFrame frame) {
        return  (ZeroCrossingRate.getZCR(frame) > threshold);
    }
}

