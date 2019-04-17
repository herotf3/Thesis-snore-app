package com.mt.waveformdemo.Audio.data;

/**
 * Created by nmman on 4/7/2019.
 */

public class SignalFormat {
    private float sampleRate;
    private int sampleSizeInByte;

    public SignalFormat(float sampleRate, int sampleSizeInByte) {
        this.sampleRate = sampleRate;
        this.sampleSizeInByte = sampleSizeInByte;
    }

    public static SignalFormat standard() {
        return new SignalFormat(16000, 2);
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public int getSampleSizeInByte() {
        return sampleSizeInByte;
    }
}
