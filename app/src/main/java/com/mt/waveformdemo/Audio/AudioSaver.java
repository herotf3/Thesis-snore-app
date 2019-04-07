package com.mt.waveformdemo.Audio;

import com.mt.waveformdemo.Audio.segmentation.SegmentStandardlizer;
import com.mt.waveformdemo.Audio.type.AudioSegment;

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
    private SegmentStandardlizer standardlizer;

    public ArrayList<AudioSegment> segmentsHolder;


    private AudioSaver() {
        segmentsHolder = new ArrayList<AudioSegment>();
        standardlizer = new SegmentStandardlizer(AudioFormatConfig.SEGMENT_DURATION);
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

    public float[] SavedAudioBuffer() {
        ArrayList<Float> buffer = new ArrayList<>();
        for (int i = 0; i < segmentsHolder.size(); i++) {
            buffer.addAll(segmentsHolder.get(i).toFloatArrayList());
        }
        savedAudioBuffer = new float[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            savedAudioBuffer[i] = buffer.get(i);
        }
        return savedAudioBuffer;
    }
}
