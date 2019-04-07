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
    private int frameSize;
    private int nFrameInSegment;



    public SegmentStandardlizer(float standardDuration) {
        this.standardDuration = standardDuration;
        this.standardNSampleInSegment = (int) (standardDuration*AudioFormatConfig.SAMPLE_RATE);
    }

    public ArrayList<AudioSegment> standardlize(final AudioSegment segment)
    {
        ArrayList<AudioSegment> standardedSegments = new ArrayList<>();

        int k = segment.getNumberOfFrame()* AudioFormatConfig.FRAME_SIZE - standardNSampleInSegment;
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

    private ArrayList<AudioSegment> splitSegment(final AudioSegment needSplitSegment, int excessSample) {
        AudioSegment segment = null;
        try {
            segment = (AudioSegment) needSplitSegment.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        ArrayList<AudioSegment> resList = new ArrayList<>();
        // Index of frame to split
        int splFrameIndex = standardNSampleInSegment / AudioFormatConfig.FRAME_SIZE;
        // Number of excess samples in splitted sample
        int nExSample =   standardNSampleInSegment % AudioFormatConfig.FRAME_SIZE;

        List<AudioFrame> leftFrames, rightFrames, orgFrames;
        orgFrames = segment.getFrames();
        leftFrames = new LinkedList<>(orgFrames.subList(0,splFrameIndex-1));
        rightFrames = new LinkedList<>(orgFrames.subList(splFrameIndex+1,orgFrames.size()-1)) ;

        AudioFrame splitFrame = orgFrames.get(splFrameIndex);
        orgFrames.clear();
        AudioFrame tmp = splitFrame.cropFrame( splitFrame.nSampleInFrame-nExSample,splitFrame.nSampleInFrame);
        rightFrames.add( tmp );
        tmp = splitFrame.cropFrame(0,splitFrame.nSampleInFrame-nExSample-1);
        leftFrames.add(tmp );


        resList.add(new AudioSegment(leftFrames));
        if (rightFrames.size()>0)
            resList.addAll(this.standardlize(new AudioSegment(rightFrames)));

        return  resList;
    }

    private AudioSegment paddingSegment(AudioSegment segment, int nSample) {
        AudioFrame paddingFrame = new AudioFrame(new Float[nSample],segment.endTimeIndex());
        segment.add(paddingFrame);
        return segment;
    }

    public static float calculateOverlap(int frameSize, int frameDuration){
        return 0;
    }
}
