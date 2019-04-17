/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mt.waveformdemo.Audio.io;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.mt.waveformdemo.Audio.data.AudioFrame;
import com.mt.waveformdemo.Audio.data.AudioSegment;
import com.mt.waveformdemo.Audio.data.AudioSignal;
import com.mt.waveformdemo.Audio.data.SignalFormat;
import com.mt.waveformdemo.Audio.utils.AudioFormatConfig;

import java.util.Iterator;

public class PlaybackThread {
    public static final int SAMPLE_RATE = AudioFormatConfig.SAMPLE_RATE;
    private static final String LOG_TAG = PlaybackThread.class.getSimpleName();

    public void setAudioSegment(AudioSegment audioSegment) {
        this.audioSegment = audioSegment;
//        setupAudioTrack(audioSegment.sourceSignalFormat());
    }

    private AudioSegment audioSegment;
    private AudioTrack audioTrack ;

    private Thread mThread;
    private boolean mShouldContinue;
    private PlaybackListener mListener;

    public PlaybackThread(AudioSegment audioSegment, PlaybackListener listener){
        mListener = listener;
        this.audioSegment = audioSegment;
    }

    public PlaybackThread(PlaybackListener listener) {
        mListener = listener;
    }

    public boolean playing() {
        return mThread != null;
    }

    public void startPlayback() {
        if (mThread != null)
            return;

        // Start streaming in a thread
        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                playSegment();
            }
        });
        mThread.start();
    }

    public void stopPlayback() {
        if (mThread == null)
            return;

        mShouldContinue = false;
        mThread = null;
    }

    private void setupAudioTrack(SignalFormat format){
        int bufferSize = AudioTrack.getMinBufferSize((int) format.getSampleRate(), AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }
        bufferSize = Math.min(bufferSize,AudioFormatConfig.FRAME_SIZE);
        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);

//        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
//            @Override
//            public void onPeriodicNotification(AudioTrack track) {
//                if (mListener != null && track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
//                    mListener.onProgress((track.getPlaybackHeadPosition() * 1000) / SAMPLE_RATE);
//                }
//            }
//
//            @Override
//            public void onMarkerReached(AudioTrack track) {
//                Log.v(LOG_TAG, "Audio file end reached");
//                track.release();
//                if (mListener != null) {
//                    mListener.onCompletion();
//                }
//            }
//        });
//        audioTrack.setPositionNotificationPeriod(SAMPLE_RATE / 30); // 30 times per second
//        audioTrack.setNotificationMarkerPosition(audioSegment.getNumberSample());
    }

    private void playSegment() {
        if (audioSegment ==null){
            Log.v(LOG_TAG, "Segment null");
            return;
        }

        this.setupAudioTrack(audioSegment.sourceSignalFormat());
        audioTrack.play();
        Log.v(LOG_TAG, "Audio streaming started");

        AudioFrame firstFrame = audioSegment.getHead().getData();
        AudioSignal srcSignal = firstFrame.getSourceSignal();
        Iterator<AudioFrame> frameIt = audioSegment.iterator();
        int totalWritten = 0;
        // writing audio data to the track
        while (frameIt.hasNext() && mShouldContinue){
            AudioFrame frame = frameIt.next();
            short[] buffer = frame.to16BitBuffer();
            // write out a frame
            audioTrack.write(buffer,0,frame.getnSamples());
            totalWritten += frame.getnSamples();
        }

        if (!mShouldContinue) {
            audioTrack.release();
            this.audioSegment = null;
        }
        Log.v(LOG_TAG, "Audio streaming finished. Samples written: " + totalWritten);
    }
}
