package com.mt.waveformdemo.Audio;

/**
 * Created by macbook on 4/3/19.
 */

public class Converter {
    public static double linearToDecibel(final double value) {
        return 20.0 * Math.log10(value);
    }

    public static short[] PCMFloatToShortBuffer(float[] floatBuffer) {
        return new short[floatBuffer.length];
    }
}
