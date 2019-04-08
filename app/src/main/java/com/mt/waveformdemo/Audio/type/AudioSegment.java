package com.mt.waveformdemo.Audio.type;

import com.mt.waveformdemo.Audio.AudioFormatConfig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by macbook on 3/28/19.
 * Object hold a list of continuos frames of sound data
 */

public class AudioSegment implements Cloneable{

    private LinkedList<AudioFrame> frames = new LinkedList<>();
    private int totalSamples = 0;

    public AudioSegment() {}
    public AudioSegment(LinkedList<AudioFrame> frames) {
        this.frames = new LinkedList<>(frames);
        this.totalSamples = frames.size()* AudioFormatConfig.FRAME_SIZE;
    }

    public AudioSegment(AudioSegment segment) {
        this.frames = new LinkedList<>(segment.frames);
        this.totalSamples = segment.totalSamples;
    }

    public AudioSegment(List<AudioFrame> frames) {
        this.frames = new LinkedList<>(frames);
        this.totalSamples = frames.size()* AudioFormatConfig.FRAME_SIZE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return new AudioSegment(this);
    }

    public LinkedList<AudioFrame> getFrames() {
        return frames;
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
        this.totalSamples+= frames.size()* AudioFormatConfig.FRAME_SIZE;
    }

    public ArrayList<Float> toFloatArrayList() {
        ArrayList<Float> list = new ArrayList<>();
        for (int i = 0; i < frames.size(); i++) {
            list.addAll(frames.get(i).getFloatList());
        }
        return list;
    }

    public float[] toFloatArray() {
        AudioFrame mergeFrame = new AudioFrame(frames.getFirst());
        ListIterator<AudioFrame> iterator = this.frames.listIterator(1);
        while (iterator.hasNext()){
            mergeFrame.append(iterator.next().getFloatSamples());
        }

        float[] arr = new float[mergeFrame.nSampleInFrame];
        for (ListIterator<Float> it = mergeFrame.getFloatSamples().listIterator();it.hasNext();){
            arr[it.nextIndex()] = it.next();
        }
        return arr;
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

    public AudioSegment splitFrame(int fromIndex, int nearEndIndex) {
        List<AudioFrame> frames = new LinkedList<>(this.frames.subList(fromIndex,nearEndIndex));
        return new AudioSegment(frames);
    }

    public int getTotalSample() {
        return totalSamples;
    }

    public void setTotalSamples(int totalSamples) {
        this.totalSamples = totalSamples;
    }

    public void addFirst(AudioFrame frame) {
        this.frames.addFirst(frame);
        this.totalSamples += frame.nSampleInFrame;
    }
}

