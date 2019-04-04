package com.mt.waveformdemo.Algorithim;

/**
 * Created by macbook on 4/3/19.
 */

public class ZeroCrossingRate {
    public static float getZCR(short buffer[]) {
        int numberOfZeroCrossings = 0;
        for (int i = 1; i < buffer.length; i++) {
            if (buffer[i] * buffer[i - 1] < 0) {
                numberOfZeroCrossings++;
            }
        }

        float zeroCrossingRate = numberOfZeroCrossings / (float) (buffer.length - 1);
        return zeroCrossingRate;
    }
}
