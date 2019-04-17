package com.mt.waveformdemo.Audio.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by nmman on 4/7/2019.
 * Reference to samples in an AudioSignal
 */

public class AudioFrame {

    private final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
    private AudioSignal sourceSignal;
    private int startDataPos;
    private int nSamples;
    private int dataCursor;
    private int sampleCursor;
    private float duration;

    /**
     * @param sourceSignal signal to reference data
     * @param startDataPos start position in raw byte array
     * @param nSamples     number of samples of this frame
     */
    public AudioFrame(AudioSignal sourceSignal, int startDataPos, int nSamples) {
        this.sourceSignal = sourceSignal;
        this.startDataPos = startDataPos;
        this.nSamples = nSamples;
        this.dataCursor = startDataPos;
        this.duration = nSamples / sourceSignal.getSampleRate();
        this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public boolean hasNextSample() {
        return sampleCursor < nSamples;
    }

    // Only use when sample size is 2 byte
    public short nextSample16() {
        if (sourceSignal.getSampleSize() != 2) {
            throw new IllegalArgumentException("This is PCM16 signal");
        }
        byteBuffer.clear();
        byte b1 = sourceSignal.dataAt(dataCursor);
        byte b2 = sourceSignal.dataAt(dataCursor + 1);
        byteBuffer.put(b1).put(b2);
        dataCursor += 2;
        sampleCursor++;
        return byteBuffer.getShort(0);
    }

    // Only use when sample size is 4 byte
    public float nextSample32() {
        if (sourceSignal.getSampleSize() != 4) {
            throw new IllegalArgumentException("This is PCM32 signal");
        }
        byteBuffer.clear();
        byte b1 = sourceSignal.dataAt(dataCursor);
        byte b2 = sourceSignal.dataAt(dataCursor + 1);
        byte b3 = sourceSignal.dataAt(dataCursor + 2);
        byte b4 = sourceSignal.dataAt(dataCursor + 3);
        byteBuffer.put(b1).put(b2).put(b3).put(b4);
        dataCursor += 4;
        sampleCursor++;
        return byteBuffer.getFloat(0);
    }

    // Next sample and convert to double
    public double nextSample16asDouble() {
        return ((double) nextSample16()) / 32767.0d;
    }

    // Next sample and convert to float
    public float nextSample16asFloat() {
        return ((float) nextSample16()) / 32767.0f;
    }

    public void reset() {
        dataCursor = startDataPos;
        sampleCursor = 0;
    }

    /**
     * Get 16 bit sample at position i and convert to float
     *
     * @param i is sample position
     * @return sample value in float
     */
    public float getSample16asFloat(int i) {
        if (sourceSignal.getSampleSize() != 2) {
            throw new IllegalArgumentException("This is PCM16 signal");
        }
        int p = i * sourceSignal.getSampleSize() + startDataPos;
        byteBuffer.clear();
        byte b1 = sourceSignal.dataAt(p);
        byte b2 = sourceSignal.dataAt(p + 1);
        byteBuffer.put(b1).put(b2);
        return (float) byteBuffer.getShort(0) / 32767.0f; // 32767 is size of short
    }

    public float getFloatSample(int i){
        if (sourceSignal.getSampleSize() != 2) {
            throw new IllegalArgumentException("This is PCM16 signal");
        }
        int p = i * sourceSignal.getSampleSize() + startDataPos;
        byte b1 = sourceSignal.dataAt(p);
        byte b2 = sourceSignal.dataAt(p + 1);

        float x = ((short) ((b1 & 0xFF) | (b2 << 8))) * (1.0f / 32767.0f);
        return x;
    }

    /**
     * Get 16 bit sample at position i and convert to short
     *
     * @param i is sample position
     * @return sample value in short
     */
    public short getSample16asShort(int i) {
        if (sourceSignal.getSampleSize() != 2) {
            throw new IllegalArgumentException("This is PCM16 signal");
        }
        int p = i * sourceSignal.getSampleSize() + startDataPos;
        byteBuffer.clear();
        byte b1 = sourceSignal.dataAt(p);
        byte b2 = sourceSignal.dataAt(p + 1);
        byteBuffer.put(b1).put(b2);
        return byteBuffer.getShort(0);
    }

    public short[] to16BitBuffer() {
        short[] buffer = new short[nSamples];
        reset();
        int i = 0;
        while (hasNextSample()) {
            buffer[i] = nextSample16();
            i++;
        }
        return buffer;
    }

    public float[] to16BitBufferAsFloat() {
        float[] buffer = new float[nSamples];
        reset();
        int i = 0;
        while (hasNextSample()) {
            buffer[i] = nextSample16asFloat();
            i++;
        }
        return buffer;
    }

    public double[] to16BitBufferAsDouble() {
        double[] buffer = new double[nSamples];
        reset();
        int i = 0;
        while (hasNextSample()) {
            buffer[i] = nextSample16asDouble();
            i++;
        }
        return buffer;
    }

    public void shiftLeft(int d) {
        if (startDataPos >= d) {
            startDataPos = startDataPos - d;
        }
        throw new IllegalArgumentException("Cannot shift " + d + " because start pos is " + startDataPos);
    }

    public int getStartDataPos() {
        return startDataPos;
    }

    public int getEndDataPos() {
        return startDataPos + nSamples * sourceSignal.getSampleSize() - 1;
    }

    public int getnSamples() {
        return nSamples;
    }

    public int getDataCursor() {
        return dataCursor;
    }

    public float getDuration() {
        return duration;
    }

    public AudioSignal getSourceSignal() {
        return sourceSignal;
    }
}
