package com.mt.waveformdemo.Audio.data;

import android.media.AudioRecord;
import android.support.annotation.NonNull;


import com.mt.waveformdemo.Audio.utils.AudioFormatConfig;

import java.util.Iterator;

/**
 * Created by nmman on 4/7/2019.
 * Represent an audio signal
 */

public class AudioSignal implements Iterable<AudioFrame> {

    public static final int MAX_DURATION = 32000;     //ms

    private byte[] rawData;
    private SignalFormat format;
    private float duration;
    private int totalSamples;
    private int dataCursor;
    private int frameCusor;
    private int samplePerFrame;
    private int sampleOverlap;
    private int nFrames;

    /**
     * @param rawData        is raw audio data, length should be 2^n
     * @param format         contains sample rate, sample size in byte,...
     * @param samplePerFrame number of samples of this frame
     */
    public AudioSignal(byte[] rawData, SignalFormat format, int samplePerFrame) {
        this.rawData = rawData;
        this.format = format;
        this.samplePerFrame = samplePerFrame;
        this.totalSamples = rawData.length / format.getSampleSizeInByte();
        this.duration = totalSamples / format.getSampleRate();
        this.dataCursor = 0;
        this.frameCusor = 0;
        split();
    }

    private AudioSignal() {}

    @NonNull
    @Override
    public Iterator<AudioFrame> iterator() {
        dataCursor = 0;
        frameCusor = 0;
        return new Iterator<AudioFrame>() {
            @Override
            public boolean hasNext() {
                return frameCusor < nFrames;
            }

            @Override
            public AudioFrame next() {
                AudioFrame frame = new AudioFrame(AudioSignal.this, dataCursor, samplePerFrame);
                dataCursor += (samplePerFrame - sampleOverlap) * format.getSampleSizeInByte();
                frameCusor++;
                return frame;
            }
        };
    }

    public byte dataAt(int i) {
        return rawData[i];
    }

    /**
     * Split signal to a number of frames
     * Auto calculate overlap base on number of frames
     * After call this method, use iterator()
     *
     * @param numFrame desired number of frames
     */
    public void split(int numFrame) {
        this.nFrames = numFrame;
        this.sampleOverlap = calculateOverlap(numFrame);
    }

    // Split signal to frames without overlap
    public void split() {
        this.nFrames = totalSamples / samplePerFrame;
        this.sampleOverlap = 0;
    }

    public float getSampleRate() {
        return format.getSampleRate();
    }

    public int getSampleSize() {
        return format.getSampleSizeInByte();
    }

    public float getDuration() {
        return duration;
    }

    private int calculateOverlap(int numFrame) {
        int overlap = (numFrame * samplePerFrame - totalSamples) / (numFrame - 1);
        if (overlap < 0)
            throw new IllegalArgumentException("Overlap got negative with current sampleRate, duration & buffer size");
        return overlap;
    }

    private int calculateOverlap2(int numFrame) {
        float bufferDur = samplePerFrame / format.getSampleRate(); // duration of a buffer (sec)
        float overlapDur = (bufferDur * numFrame - duration) / (numFrame - 1);
        if (overlapDur < 0)
            throw new IllegalArgumentException("Overlap got negative with current sampleRate, duration & buffer size");
        return Math.round(format.getSampleRate() * overlapDur);
    }

    public int getSampleOverlap() {
        return sampleOverlap;
    }

    public int getTotalSamples() {
        return totalSamples;
    }

    public int getNumFrames() {
        return nFrames;
    }

    public SignalFormat getSignalFormat() {
        return format;
    }

    public static AudioSignal newEmptySignal(SignalFormat format,int samplePerFrame) {

        final int maxSignalByteLength = (AudioSignal.MAX_DURATION/1000) * (int)format.getSampleRate() * format.getSampleSizeInByte();
        return new AudioSignal(new byte[maxSignalByteLength],format,samplePerFrame);
    }

    public int byteLength(){
        return this.rawData.length;
    }

    public int readFromRecorder(AudioRecord record, int frameIndex) {
        int frameByteSize = format.getSampleSizeInByte()*samplePerFrame;
        return record.read(this.rawData,frameByteSize*frameIndex, frameByteSize);
    }

    public static AudioSignal newZeroSignal(int nSample) {
        return new AudioSignal(new byte[nSample* AudioFormatConfig.FRAME_SIZE],AudioFormatConfig.defaultSignalFormat(),AudioFormatConfig.FRAME_SIZE);
    }
}
