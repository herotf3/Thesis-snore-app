package com.mt.waveformdemo.Audio.utils;

import android.media.AudioFormat;

import com.mt.waveformdemo.Audio.data.SignalFormat;

/**
 * Created by macbook on 3/29/19.
 */

public class AudioFormatConfig {
    public static final int SAMPLE_RATE = 16000;
    public static final int ENCODE = AudioFormat.ENCODING_PCM_16BIT;
    public static final int FRAME_SIZE = 512;   //samples
    public static final int SEGMENT_SIZE = 48128;   //samples
    public static final int NFRAME_IN_SEGMENT = 94;   //samples
    public static final float FRAME_DURATION = (float) FRAME_SIZE / SAMPLE_RATE;   //second
    public static final float SEGMENT_DURATION = 3.2f;

    public static SignalFormat defaultSignalFormat() {
        return new SignalFormat(SAMPLE_RATE,ENCODE);
    }
}
