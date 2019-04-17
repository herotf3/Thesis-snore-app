package com.mt.waveformdemo.Audio;

import com.mt.waveformdemo.Audio.data.AudioSegment;
import com.mt.waveformdemo.Audio.segmentation.SegmentStandardizer;
import com.mt.waveformdemo.Audio.utils.AudioFormatConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by macbook on 3/31/19.
 */

public class AudioSaver {
    private static AudioSaver instance;
    private float[] savedAudioBuffer;
    private SegmentStandardizer standardlizer;

    public ArrayList<AudioSegment> segmentsHolder;


    private AudioSaver() {
        segmentsHolder = new ArrayList<AudioSegment>();
        standardlizer = new SegmentStandardizer(AudioFormatConfig.SEGMENT_DURATION);
    }

    public static AudioSaver getInstance() {
        if (instance == null)
            instance = new AudioSaver();
        return instance;
    }

    // saving functions
    public void append(AudioSegment segment) {
        segmentsHolder.addAll(standardlizer.standardlize(segment));
    }

    public void saveCurrentData(String name) throws FileNotFoundException {
        File file = new File(name);
        FileOutputStream os = new FileOutputStream(file);
    }
}
