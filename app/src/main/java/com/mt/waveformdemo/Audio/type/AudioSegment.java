package com.mt.waveformdemo.Audio.type;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by macbook on 3/28/19.
 * Object hold a list of continuos frames of sound data
 */

public class AudioSegment implements Cloneable{

    private LinkedList<AudioFrame> frames = new LinkedList<>();

    public AudioSegment(LinkedList<AudioFrame> frames) {
        this.frames =frames;
    }

    public AudioSegment() {

    }

    public AudioSegment(AudioSegment frames) {
        this.frames = frames.frames;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AudioSegment clone = new AudioSegment(this);
        return clone;
    }

    public AudioSegment(List<AudioFrame> frames) {
        this.frames = new LinkedList<>(frames);

    }

    public LinkedList<AudioFrame> getFrames() {
        return frames;
    }

    private int totalSamples = 0;

    public void setFrames(LinkedList<AudioFrame> frames) {
        this.frames = frames;
        this.totalSamples = frames.size() * frames.get(0).nSampleInFrame;
    }

    public void add(AudioFrame audioFrame) {
        this.frames.add(audioFrame);
        this.totalSamples += audioFrame.nSampleInFrame;
    }

    public int getNumberOfFrame() {
        int s = 0;
        return frames.size();
    }

    public void merge(LinkedList<AudioFrame> frames) {
        this.frames.addAll(frames);
    }

    public ArrayList<Float> toFloatArrayList() {
        ArrayList<Float> list = new ArrayList<>();
        for (int i = 0; i < frames.size(); i++) {
            list.addAll(frames.get(i).getFloatList());
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

    public int endTimeIndex() {
        return this.frames.getLast().getTimeIndex();
    }

    public int startTimeIndex(){
        return this.frames.getFirst().getTimeIndex();
    }
}

