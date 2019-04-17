package com.mt.waveformdemo.Audio.processor;

import com.mt.waveformdemo.Audio.data.AudioFrame;

/**
 * Created by macbook on 3/28/19.
 */

public interface AudioCheckingProcessor {
    boolean check(AudioFrame frame);
}
