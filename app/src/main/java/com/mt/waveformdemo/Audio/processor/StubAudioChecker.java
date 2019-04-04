package com.mt.waveformdemo.Audio.processor;

import com.mt.waveformdemo.Audio.type.AudioFrame;

/**
 * Created by macbook on 3/28/19.
 */

public class StubAudioChecker implements AudioCheckingProcessor {
    @Override
    public boolean check(AudioFrame frame) {
        //ex check is frame lengh % 2 = 0
        return frame.nSampleInFrame % 2 == 0;
    }
}

