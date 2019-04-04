package com.mt.waveformdemo.Audio.type;

import com.mt.waveformdemo.Audio.Converter;

import java.util.ArrayList;

/**
 * Created by macbook on 3/28/19.
 */

public class AudioFrame {
    private int timeIndex;
    public int nSampleInFrame;
    private float[] floatBuffer;
    private short[] shortBuffer;

    public AudioFrame(short[] data, int index) {
        this.timeIndex = index;
        nSampleInFrame = data.length;
        shortBuffer = data.clone();
        floatBuffer = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            floatBuffer[i] = 0f + data[i];
        }
    }

    public AudioFrame(float[] data, int index) {
        this.timeIndex = index;
        nSampleInFrame = data.length;
        floatBuffer = data.clone();
        shortBuffer = new short[data.length];
        shortBuffer = Converter.PCMFloatToShortBuffer(floatBuffer);
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public float[] getFloatBuffer() {
        return floatBuffer;
    }

    public void setFloatBuffer(float[] floatBuffer) {
        this.floatBuffer = floatBuffer;
    }

    public short[] getShortBuffer() {
        return shortBuffer;
    }

    public void setShortBuffer(short[] shortBuffer) {
        this.shortBuffer = shortBuffer;
    }

    public ArrayList<Short> getShortList() {
        ArrayList<Short> list = new ArrayList<>();
        for (int i = 0; i < shortBuffer.length; i++) {
            list.add(shortBuffer[i]);
        }
        return list;
    }

    public ArrayList<Float> getFloatList() {
        ArrayList<Float> list = new ArrayList<>();
        for (int i = 0; i < floatBuffer.length; i++) {
            list.add(floatBuffer[i]);
        }
        return list;
    }

    public short getShortSample(int index) {
        return this.shortBuffer[index];
    }

    public float getFloatSample(int index) {
        return this.floatBuffer[index];
    }
}
