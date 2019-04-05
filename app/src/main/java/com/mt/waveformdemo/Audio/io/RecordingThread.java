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
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.mt.waveformdemo.Audio.AudioFormatConfig;

import java.util.Arrays;
import java.util.List;

public class RecordingThread {
    private static final String LOG_TAG = RecordingThread.class.getSimpleName();
    private static final int SAMPLE_RATE = AudioFormatConfig.SAMPLE_RATE;

    public RecordingThread(AudioDataReceivedListener listener) {
        mListener = listener;
    }

    private boolean mShouldContinue;
    private AudioDataReceivedListener mListener;
    private Thread mThread;

    public boolean recording() {
        return mThread != null;
    }

    public void startRecording() {
        if (mThread != null)
            return;

        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                recordPCM_Float();
            }
        });
        mThread.start();
    }

    public void stopRecording() {
        if (mThread == null)
            return;

        mShouldContinue = false;
        mThread = null;
    }

    private void record() {
        Log.v(LOG_TAG, "Start");
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        bufferSize = Math.min(bufferSize, AudioFormatConfig.FRAME_SIZE);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        short[] audioBuffer = new short[bufferSize];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!");
            return;
        }
        record.startRecording();

        Log.v(LOG_TAG, "Start recording");

        long shortsRead = 0;
        while (mShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;

            // Notify waveform
            Short[] arr = new Short[audioBuffer.length];
            for (int i=0;i<audioBuffer.length;i++){
                arr[i] = audioBuffer[i];
            }
            mListener.onAudioDataReceived(arr);
        }

        record.stop();
        record.release();

        Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void recordPCM_Float() {
        Log.v(LOG_TAG, "Start");
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_FLOAT);

        bufferSize = Math.min(bufferSize, AudioFormatConfig.FRAME_SIZE);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        float[] audioBuffer = new float[bufferSize];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_FLOAT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!");
            return;
        }
        record.startRecording();

        Log.v(LOG_TAG, "Start recording");

        long shortsRead = 0;
        while (mShouldContinue) {
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length, AudioRecord.READ_BLOCKING);

            shortsRead += numberOfShort;
            Float[] floats = new Float[audioBuffer.length];
            for (int i=0;i<audioBuffer.length;i++){
                floats[i] = audioBuffer[i];
            }

            mListener.onAudioDataReceived(floats);
        }

        record.stop();
        record.release();

        Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
    }
}
