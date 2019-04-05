package com.mt.waveformdemo.Audio;

import android.util.Log;

import com.mt.waveformdemo.Audio.processor.AudioCheckingProcessor;
import com.mt.waveformdemo.Audio.processor.AudioProcessorDelegate;
import com.mt.waveformdemo.Audio.type.AudioFrame;
import com.mt.waveformdemo.Audio.type.AudioSegment;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by macbook on 3/28/19.
 */

public class AudioDataCollector {

    private ArrayList<AudioCheckingProcessor> checkerList = new ArrayList<>();

    private AudioSegment pendingSegment = new AudioSegment();
    // List of silence frames waiting to merge (if need)
    private LinkedList<AudioFrame> spacingPool = new LinkedList<>();

    private int timeCounter = 0;
    private boolean isFilter = true;
    public final int MinimumFrameForCollect = 10;
    public final int MaximumFrameForCollect = 80;
    private final int MaxSpaceSize = 5;

    private AudioProcessorDelegate audioProcessorDelegate;

    public AudioDataCollector(AudioProcessorDelegate audioProcessorDelegate) {
        this.audioProcessorDelegate = audioProcessorDelegate;
    }

    public void collect(final Short[] data) {
        AudioFrame audioFrame = new AudioFrame(data, timeCounter++);
        boolean willGet = true;
        if (isFilter) {
            for (AudioCheckingProcessor checker : checkerList) {
                if (!checker.check(audioFrame)) {
                    willGet = false;
                    break;
                }
            }
            if (willGet) {

                this.addCollectedFrame(audioFrame);
            } else {
                Log.d("Audio Collector", "---- Detect a silence frame! -----");
                this.addSilenceFrameToPool(audioFrame);
            }
        }
    }

    private void addSilenceFrameToPool(AudioFrame audioFrame) {
        this.spacingPool.add(audioFrame);
        // the episode is separated by a too long duration
        if (this.spacingPool.size() > this.MaxSpaceSize) {
            this.handleCollectedSegment();
        }
    }

    private void addCollectedFrame(AudioFrame audioFrame) {
        if (spacingPool.size() == 0)  // in the same episode
            pendingSegment.add(audioFrame);
        else {
            // need to merge with next episode
            spacingPool.add(audioFrame);
            pendingSegment.merge(spacingPool);

            this.renewSpacingPool();
        }

        if (pendingSegment.getNumberOfFrame() >= this.MaximumFrameForCollect) {
            this.handleCollectedSegment();
        }

    }


    private void handleCollectedSegment() {
        // collect if it have enough frame else ignore(remove)
        if (this.pendingSegment.getNumberOfFrame() >= this.MinimumFrameForCollect) {
            this.passSegmentToProcess();
        }

        this.pendingSegment = new AudioSegment();
        this.renewSpacingPool();
    }

    private void passSegmentToProcess() {
        // Need to pass this segment to processor for cut / padding before analise
        Log.d("CollectingSegment", "Passed a segment to process");
        if (this.audioProcessorDelegate != null) {
            audioProcessorDelegate.processorDidReceiveSegment(this.pendingSegment);
        }
    }

    private void renewSpacingPool() {
        this.spacingPool = new LinkedList<>();
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

    public void collect(final Float[] data) {
        AudioFrame audioFrame = new AudioFrame(data, timeCounter++);
        boolean willGet = true;
        if (isFilter) {
            for (AudioCheckingProcessor checker : checkerList) {
                if (!checker.check(audioFrame)) {
                    willGet = false;
                    break;
                }
            }
            if (willGet) {

                this.addCollectedFrame(audioFrame);
            } else {
                Log.d("Audio Collector", "---- Detect a silence frame! -----");
                this.addSilenceFrameToPool(audioFrame);
            }
        }
    }
}
