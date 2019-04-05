package com.mt.waveformdemo.Audio.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by macbook on 3/28/19.
 * Object hold a list of continuos frames of sound data
 */

public class AudioSegment {
    private SegmentType type;

    private LinkedList<AudioFrame> segment = new LinkedList<>();

    public LinkedList<AudioFrame> getSegment() {
        return segment;
    }

    private int totalSamples = 0;

    public void setSegment(LinkedList<AudioFrame> segment) {
        this.segment = segment;
        this.totalSamples = segment.size() * segment.get(0).nSampleInFrame;
    }

    public void add(AudioFrame audioFrame) {
        this.segment.add(audioFrame);
        this.totalSamples += audioFrame.nSampleInFrame;
    }

    public int getNumberOfFrame() {
        int s = 0;
        return segment.size();
    }

    public void merge(LinkedList<AudioFrame> frames) {
        segment.addAll(frames);
    }

    public ArrayList<Float> toFloatArrayList() {
        ArrayList<Float> list = new ArrayList<>();
        for (int i = 0; i < segment.size(); i++) {
            list.addAll(segment.get(i).getFloatList());
        }
        return list;
    }

    public float[] toFloatArray() {

        ArrayList<Float> list = this.toFloatArrayList();
        float[] buffer = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            buffer[i] = list.get(i);
        }
        return buffer;
    }

    // merge all frame to a single linked list of samples - a single frame
    public AudioFrame mergeToSingleFrame(){
        return null;
    }

}

