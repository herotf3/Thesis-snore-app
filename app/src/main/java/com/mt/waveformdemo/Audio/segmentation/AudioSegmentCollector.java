package com.mt.waveformdemo.Audio.segmentation;

import android.util.Log;

import com.mt.waveformdemo.Audio.data.AudioFrame;
import com.mt.waveformdemo.Audio.data.AudioSegment;
import com.mt.waveformdemo.Audio.data.AudioSignal;
import com.mt.waveformdemo.Audio.processor.AudioCheckingProcessor;
import com.mt.waveformdemo.Audio.processor.AudioProcessorDelegate;

import java.util.ArrayList;

/**
 * Created by macbook on 3/28/19.
 */

public class AudioSegmentCollector implements IDataCollector<AudioSignal>{

    private ArrayList<AudioCheckingProcessor> checkerList = new ArrayList<>();

    private AudioSegment pendingSegment = new AudioSegment();
    // List of silence frames waiting to merge (if need)
    private AudioSegment spacingPool = new AudioSegment();

    private int timeCounter = 0;
    private boolean isFilter = true;
    public final int MinimumFrameForCollect = 10;
    public final int MaximumFrameForCollect = 80;
    private final int MaxSpaceSize = 5;

    private AudioProcessorDelegate audioProcessorDelegate;

    public AudioSegmentCollector(AudioProcessorDelegate audioProcessorDelegate) {
        this.audioProcessorDelegate = audioProcessorDelegate;
    }

    public void collect(final AudioFrame audioFrame) {

        boolean willGet = true;
        if (isFilter) {
            for (AudioCheckingProcessor checker : checkerList) {
                if (!checker.check(audioFrame)) {
                    willGet = false;
                    break;
                }
            }
        }
        if (willGet) {

            this.addCollectedFrame(audioFrame);
        } else {
            Log.d("Audio Collector", "---- Detect a silence frame! -----");
            this.addSilenceFrameToPool(audioFrame);
        }
    }

    private void addSilenceFrameToPool(AudioFrame audioFrame) {
        this.spacingPool.pushBack(audioFrame);
        // the episode is separated by a too long duration
        if (this.spacingPool.length() > this.MaxSpaceSize) {
            this.handleCollectedSegment();
        }
    }

    private void addCollectedFrame(AudioFrame audioFrame) {
        if (spacingPool.length() == 0)  // in the same episode
            pendingSegment.pushBack(audioFrame);
        else {
            // need to merge with next episode
            spacingPool.pushBack(audioFrame);
            pendingSegment.mergeWith(spacingPool);

            this.renewSpacingPool();
        }

        if (pendingSegment.length() >= this.MaximumFrameForCollect) {
            this.handleCollectedSegment();
        }

    }


    private void handleCollectedSegment() {
        // collect if it have enough frame else ignore(remove)
        if (this.pendingSegment.length() >= this.MinimumFrameForCollect) {
            this.passSegmentToProcess();
        }

        this.resetPool();
    }

    private void passSegmentToProcess() {
        // Need to pass this segment to processor for cut / padding before analise
        Log.d("CollectingSegment", "Passed a segment to process");
        if (this.audioProcessorDelegate != null) {
            audioProcessorDelegate.processorDidReceiveSegment(this.pendingSegment);
        }
    }

    private void renewSpacingPool() {
        this.spacingPool = new AudioSegment();
    }

    private void resetPool(){
        pendingSegment = new AudioSegment();
        spacingPool = new AudioSegment();
    }

    public void addCheckingProcessor(AudioCheckingProcessor checker) {
        checkerList.add(checker);
    }

    public void setWillFilterCollecting(boolean isFilter) {
        this.isFilter = isFilter;
    }

    public AudioProcessorDelegate getAudioProcessorDelegate() {
        return audioProcessorDelegate;
    }

    public void setAudioProcessorDelegate(AudioProcessorDelegate audioProcessorDelegate) {
        this.audioProcessorDelegate = audioProcessorDelegate;
    }

    @Override
    public void collect(AudioSignal signal) {
        for (AudioFrame frame : signal) {
            this.collect(frame);
        }
    }
}
