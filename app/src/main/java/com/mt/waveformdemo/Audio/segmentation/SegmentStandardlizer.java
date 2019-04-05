package com.mt.waveformdemo.Audio.segmentation;

import com.mt.waveformdemo.Audio.AudioFormatConfig;
import com.mt.waveformdemo.Audio.type.AudioFrame;
import com.mt.waveformdemo.Audio.type.AudioSegment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

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

    public ArrayList<AudioSegment> standardlize(ArrayList<AudioSegment> segments){
        ArrayList<AudioSegment> standardedSegments = new ArrayList<>();
        int n = segments.size();

        for (int i=0;i<n;i++){
            AudioSegment segment = segments.get(i);
            int k = segment.getNumberOfFrame()* AudioFormatConfig.FRAME_SIZE - standardNSampleInSegment;
            if (k==0){
                // fixed segment
                standardedSegments.add(segment);
                break;
            }else if (k<0){
                // too short segment, need to padding
                standardedSegments.add(this.paddingSegment(segment,abs(k)));
            }else{
                // too long segment, need to split
                standardedSegments.addAll(this.splitSegment(segment,abs(k)));
            }
        }

        return standardedSegments;
    }

    private ArrayList<AudioSegment> splitSegment(AudioSegment segment, int excessSample) {
        ArrayList<AudioSegment> resList = new ArrayList<>();
        // Index of frame to split
        int splFrameIndex = standardNSampleInSegment / AudioFormatConfig.FRAME_SIZE;
        // Number of excess samples in splitted sample
        int nExSample =   standardNSampleInSegment % AudioFormatConfig.FRAME_SIZE;



        LinkedList<AudioFrame> leftFrames, rightFrames, orgFrames;
        orgFrames = segment.getSegment();
        leftFrames = (LinkedList<AudioFrame>) orgFrames.subList(0,splFrameIndex-1);
        rightFrames = (LinkedList<AudioFrame>) orgFrames.subList(splFrameIndex+1,orgFrames.size()-1);

        AudioFrame splitFrame = orgFrames.get(splFrameIndex);
        //AudioFrame frame = new AudioFrame(splitFrame.getFloatBuffer().subList(0,nExSample-1));
        //leftFrames.add()
        return  resList;
    }

    private AudioSegment paddingSegment(AudioSegment segment, int nSample) {
        int lastTimeIndex = segment.getSegment().getLast().getTimeIndex();
        AudioFrame paddingFrame =null;
        segment.add(paddingFrame);
        return segment;
    }

    public static float calculateOverlap(int frameSize, int frameDuration){
        return 0;
    }
}
