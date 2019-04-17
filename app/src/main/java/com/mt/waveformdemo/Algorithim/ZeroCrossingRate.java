package com.mt.waveformdemo.Algorithim;

import com.mt.waveformdemo.Audio.data.AudioFrame;

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

    public static float getZCR(AudioFrame frame){
        if (frame.getnSamples()==0)
            return 0;

        int numberOfZeroCrossings = 0;
        float s1,s2;
        s1 =frame.getSample16asFloat(0);

        for (int i = 1; i < frame.getnSamples(); i++) {
            s2 = frame.getSample16asFloat(i);
            if (s1*s2<0){
                numberOfZeroCrossings++;
            }
            s1 = s2;
        }
        return numberOfZeroCrossings / (float) (frame.getnSamples() - 1);
    }
}
