package com.mt.waveformdemo.Audio;

import com.mt.waveformdemo.Audio.type.AudioSegment;

/**
 * Created by macbook on 3/31/19.
 */

public interface AudioProcessorDelegate {
    void processorDidReceiveSegment(AudioSegment audioSegment);
}
