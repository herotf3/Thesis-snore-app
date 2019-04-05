package com.mt.waveformdemo.Audio;

import java.util.LinkedList;

/**
 * Created by macbook on 4/3/19.
 */

public class Converter {
    public static double linearToDecibel(final double value) {
        return 20.0 * Math.log10(value);
    }

    public static LinkedList<Short> PCMFloatToShortBuffer(LinkedList<Float> floatBuffer) {
        return null;
    }
}
