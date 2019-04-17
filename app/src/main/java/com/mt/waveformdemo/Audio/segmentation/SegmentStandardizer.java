package com.mt.waveformdemo.Audio.segmentation;

import com.mt.waveformdemo.Audio.data.AudioSegment;
import com.mt.waveformdemo.Audio.utils.AudioFormatConfig;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by macbook on 4/4/19.
 */

public class SegmentStandardizer {
    private float standardDuration; // sec
    private int standardNSampleInSegment;
    private int nExSample; // Number of excess samples in frame
    private int nFrameInSegment;


    public SegmentStandardizer(float standardDuration) {
        this.standardDuration = standardDuration;
        this.standardNSampleInSegment = (int) (standardDuration* AudioFormatConfig.SAMPLE_RATE);
        this.nFrameInSegment = standardNSampleInSegment / AudioFormatConfig.FRAME_SIZE;
        nExSample =   standardNSampleInSegment % AudioFormatConfig.FRAME_SIZE;
    }

    public ArrayList<AudioSegment> standardlize(final AudioSegment segment)
    {
        ArrayList<AudioSegment> standardedSegments = new ArrayList<>();

        int k = segment.length() - nFrameInSegment;
        if (k==0){
            // fixed segment
            standardedSegments.add(segment);
        }else if (k<0){
            // too short segment, need to padding
            standardedSegments.add(this.paddingSegment(segment,abs(k)));
        }else{
            // too long segment, need to split
            standardedSegments.addAll(this.splitSegment(segment,k));
        }
        return standardedSegments;
    }

    private ArrayList<AudioSegment> splitSegment(final AudioSegment segment, int nFrameExcess) {

        ArrayList<AudioSegment> resList = new ArrayList<>();

        AudioSegment leftSegment, rightSegment;
        leftSegment = (AudioSegment) segment.subSegment(0,nFrameInSegment);
        rightSegment = (AudioSegment) segment.subSegment(nFrameInSegment+1,nFrameExcess);

        resList.add(leftSegment);
        if (rightSegment!=null && rightSegment.length()>0)
            resList.addAll(this.standardlize(rightSegment));

        return  resList;
    }

    private AudioSegment paddingSegment(AudioSegment segment, int nFrame) {
        segment.addPadding(nFrame);
        return segment;
    }

    public static float calculateOverlap(int frameSize, int frameDuration){
        return 0;
    }
}
