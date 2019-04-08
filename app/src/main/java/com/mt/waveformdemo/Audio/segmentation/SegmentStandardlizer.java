package com.mt.waveformdemo.Audio.segmentation;

import android.util.Log;

import com.mt.waveformdemo.Audio.AudioFormatConfig;
import com.mt.waveformdemo.Audio.type.AudioFrame;
import com.mt.waveformdemo.Audio.type.AudioSegment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by macbook on 4/4/19.
 */

public class SegmentStandardlizer {
    private float standardDuration; // sec
    private int standardNSampleInSegment;
    private int nExSample; // Number of excess samples in frame



    public SegmentStandardlizer(float standardDuration) {
        this.standardDuration = standardDuration;
        this.standardNSampleInSegment = (int) (standardDuration*AudioFormatConfig.SAMPLE_RATE);
        nExSample =   standardNSampleInSegment % AudioFormatConfig.FRAME_SIZE;
    }

    public ArrayList<AudioSegment> standardlize(final AudioSegment segment)
    {
        ArrayList<AudioSegment> standardedSegments = new ArrayList<>();

        int k = segment.getTotalSample() - standardNSampleInSegment;
        if (k==0){
            // fixed segment
            standardedSegments.add(segment);
        }else if (k<0){
            // too short segment, need to padding
            standardedSegments.add(this.paddingSegment(segment,abs(k)));
        }else{
            // too long segment, need to split
            standardedSegments.addAll(this.splitSegment(segment,abs(k)));
        }

        return standardedSegments;
    }

    private ArrayList<AudioSegment> splitSegment(final AudioSegment segment, int excessSample) {

        ArrayList<AudioSegment> resList = new ArrayList<>();
        // Index of frame to split
        int splFrameIndex = standardNSampleInSegment / AudioFormatConfig.FRAME_SIZE;

        AudioSegment leftSegment, rightSegment;
        leftSegment = segment.splitFrame(0,splFrameIndex);
        rightSegment = segment.splitFrame(splFrameIndex+1,segment.getNumberOfFrame());

        AudioFrame splitFrame = segment.getFrames().get(splFrameIndex);

        AudioFrame tmp = splitFrame.cropFrame(0,splitFrame.nSampleInFrame-nExSample);
        leftSegment.add(tmp );
        rightSegment.addFirst(splitFrame);

        resList.add(leftSegment);
        if (rightSegment.getNumberOfFrame()>0)
            resList.addAll(this.standardlize(rightSegment));

        return  resList;
    }

    private AudioSegment paddingSegment(AudioSegment segment, int nSample) {
        Float[] paddingSamples =  new Float[nSample];
        for (int i=0;i<nSample;i++){
            paddingSamples[i]=0f;
        }
        AudioFrame paddingFrame = new AudioFrame(paddingSamples,segment.endTimeIndex());
        segment.add(paddingFrame);
        return segment;
    }

    public static float calculateOverlap(int frameSize, int frameDuration){
        return 0;
    }
}
