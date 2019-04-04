package com.mt.waveformdemo.Audio;

import android.media.AudioFormat;

/**
 * Created by macbook on 3/29/19.
 */

public class MyAudioFormat {
    public static final int SAMPLE_RATE = 16000;
    public static final int ENCODE = AudioFormat.ENCODING_PCM_16BIT;
    public static final int FRAME_SIZE = 1024;
    public static final int MAX_SEGMENT_SIZE = 10000;
}
