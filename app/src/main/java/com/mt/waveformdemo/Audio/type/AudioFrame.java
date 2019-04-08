package com.mt.waveformdemo.Audio.type;

import com.mt.waveformdemo.Audio.AudioFormatConfig;
import com.mt.waveformdemo.Audio.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by macbook on 3/28/19.
 */

public class AudioFrame {
    private int timeIndex;
    public int nSampleInFrame;
    private LinkedList<Float> floatSamples;
    private LinkedList<Short> shortSamples;

    public AudioFrame(Short[] data, int index) {
        this.timeIndex = index;
        nSampleInFrame = data.length;
        shortSamples = new LinkedList<>(Arrays.asList(data));
    }

    public AudioFrame(Float[] data, int index) {
        this.timeIndex = index;
        nSampleInFrame = data.length;
        floatSamples = new LinkedList<>(Arrays.asList(data));
        shortSamples = Converter.PCMFloatToShortBuffer(floatSamples);
    }

    public AudioFrame(List<Float> samples, int timeIndex) {
        this.timeIndex = timeIndex;
        this.floatSamples = new LinkedList<>(samples);
        this.nSampleInFrame = samples.size();
    }

    public AudioFrame(AudioFrame frame)  {
        this.floatSamples = new LinkedList<>(frame.floatSamples);
        this.timeIndex = frame.getTimeIndex();
        this.nSampleInFrame = frame.nSampleInFrame;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public LinkedList<Float> getFloatSamples() {
        return floatSamples;
    }

    public LinkedList<Short> getShortSamples() {
        return shortSamples;
    }

    public ArrayList<Short> getShortList() {
        return new ArrayList<>(this.shortSamples);
    }

    public ArrayList<Float> getFloatList() {
        return new ArrayList<>(this.floatSamples);
    }

    public short getShortSample(int index) {
        return this.shortSamples.get(index);
    }

    public float getFloatSample(int index) {
        return this.floatSamples.get(index);
    }

    public final AudioFrame cropFrame(int start, int end){
        List<Float> samples = floatSamples.subList(start,end);
        return new AudioFrame(samples,this.timeIndex);
    }

    public void append(LinkedList<Float> floatSamples) {
        this.floatSamples.addAll(floatSamples);
        this.nSampleInFrame += floatSamples.size();
    }
}
