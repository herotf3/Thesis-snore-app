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
import android.os.Build;
import android.util.Log;

import com.mt.waveformdemo.Audio.MyAudioFormat;
import com.mt.waveformdemo.Audio.type.AudioSegment;

import java.nio.FloatBuffer;

public class PlaybackThread {
    public static final int SAMPLE_RATE = MyAudioFormat.SAMPLE_RATE;
    private static final String LOG_TAG = PlaybackThread.class.getSimpleName();

    public PlaybackThread(float[] samples, PlaybackListener listener) {
        mSamples = FloatBuffer.wrap(samples);
        mNumSamples = samples.length;
        mListener = listener;
    }

    public PlaybackThread(PlaybackListener listener) {
        mListener = listener;
    }

    private Thread mThread;
    private boolean mShouldContinue;

    public void setmSamples(FloatBuffer mSamples) {
        this.mSamples = mSamples;
        this.mNumSamples = mSamples.array().length;
    }

    private FloatBuffer mSamples;
    private int mNumSamples;
    private PlaybackListener mListener;

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
                play();
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

    private void play() {
        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_FLOAT);
        if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_FLOAT,
                bufferSize,
                AudioTrack.MODE_STREAM);

        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onPeriodicNotification(AudioTrack track) {
                if (mListener != null && track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    mListener.onProgress((track.getPlaybackHeadPosition() * 1000) / SAMPLE_RATE);
                }
            }

            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.v(LOG_TAG, "Audio file end reached");
                track.release();
                if (mListener != null) {
                    mListener.onCompletion();
                }
            }
        });
        audioTrack.setPositionNotificationPeriod(SAMPLE_RATE / 30); // 30 times per second
        audioTrack.setNotificationMarkerPosition(mNumSamples);

        audioTrack.play();

        Log.v(LOG_TAG, "Audio streaming started");

        float[] buffer = new float[bufferSize];
        mSamples.rewind();
        int limit = mNumSamples;
        int totalWritten = 0;
        while (mSamples.position() < limit && mShouldContinue) {
            int numSamplesLeft = limit - mSamples.position();
            int samplesToWrite;
            if (numSamplesLeft >= buffer.length) {
                mSamples.get(buffer);
                samplesToWrite = buffer.length;
            } else {
                for (int i = numSamplesLeft; i < buffer.length; i++) {
                    buffer[i] = 0;
                }
                mSamples.get(buffer, 0, numSamplesLeft);
                samplesToWrite = numSamplesLeft;
            }
            totalWritten += samplesToWrite;
            //audioTrack.write(buffer, 0, samplesToWrite);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                audioTrack.write(buffer, 0, samplesToWrite, AudioTrack.WRITE_BLOCKING);
            } else {
                Log.d("Playback", "Version low than Lollipop! Can't play!");
            }
        }

        if (!mShouldContinue) {
            audioTrack.release();
        }

        Log.v(LOG_TAG, "Audio streaming finished. Samples written: " + totalWritten);
    }

    public void setSegment(AudioSegment segment) {
        float[] buffer = segment.toFloatArray();
        this.mSamples = FloatBuffer.wrap(buffer);
        mNumSamples = buffer.length;
    }
}
