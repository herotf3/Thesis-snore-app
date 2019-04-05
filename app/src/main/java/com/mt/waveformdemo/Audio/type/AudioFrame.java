package com.mt.waveformdemo.Audio.type;

import com.mt.waveformdemo.Audio.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by macbook on 3/28/19.
 */

public class AudioFrame {
    private int timeIndex;
    public int nSampleInFrame;
    private LinkedList<Float> floatBuffer;
    private LinkedList<Short> shortBuffer;

    public AudioFrame(Short[] data, int index) {
        this.timeIndex = index;
        nSampleInFrame = data.length;
        shortBuffer = new LinkedList<>(Arrays.asList(data));

    }

    public AudioFrame(Float[] data, int index) {
        this.timeIndex = index;
        nSampleInFrame = data.length;
        floatBuffer = new LinkedList<>(Arrays.asList(data));
        shortBuffer = Converter.PCMFloatToShortBuffer(floatBuffer);
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public LinkedList<Float> getFloatBuffer() {
        return floatBuffer;
    }

    public void setFloatBuffer(LinkedList<Float> floatBuffer) {
        this.floatBuffer = floatBuffer;
    }

    public LinkedList<Short> getShortBuffer() {
        return shortBuffer;
    }

    public void setShortBuffer(LinkedList<Short> shortBuffer) {
        this.shortBuffer = shortBuffer;
    }

    public ArrayList<Short> getShortList() {
        return new ArrayList<>(this.shortBuffer);
    }

    public ArrayList<Float> getFloatList() {
        return new ArrayList<>(this.floatBuffer);
    }

    public short getShortSample(int index) {
        return this.shortBuffer.get(index);
    }

    public float getFloatSample(int index) {
        return this.floatBuffer.get(index);
    }
}
