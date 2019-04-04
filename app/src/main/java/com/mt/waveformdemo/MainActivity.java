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

package com.mt.waveformdemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mt.waveformdemo.Audio.AudioDataCollector;
import com.mt.waveformdemo.Audio.io.AudioDataReceivedListener;
import com.mt.waveformdemo.Audio.AudioProcessorDelegate;
import com.mt.waveformdemo.Audio.AudioSaver;
import com.mt.waveformdemo.Audio.io.RecordingThread;
import com.mt.waveformdemo.Audio.io.PlaybackListener;
import com.mt.waveformdemo.Audio.io.PlaybackThread;
import com.mt.waveformdemo.Audio.processor.HighEnergyChecker;
import com.mt.waveformdemo.Audio.type.AudioSegment;
import com.mt.waveformdemo.WaveFile.WavFile;

import com.mt.waveform.WaveformView;
import com.mt.waveformdemo.WaveFile.WavFileException;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MainActivity extends AppCompatActivity implements AudioDataReceivedListener, AudioProcessorDelegate, PlaybackListener {

    private WaveformView mRealtimeWaveformView;
    WaveformView mPlaybackView;
    FloatingActionButton playFab, viewSegmentFab;

    private RecordingThread mRecordingThread;
    private PlaybackThread mPlaybackThread;
    private static final int REQUEST_RECORD_AUDIO = 13;

    private String filePath;
    float[] samples = null;
    FileInputStream fis;
    Uri uri;

    private AudioDataCollector audioDataCollector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioDataCollector = new AudioDataCollector(this);
        audioDataCollector.addCheckingProcessor(new HighEnergyChecker());
        //selectFileToPlay();

        mPlaybackView = (WaveformView) findViewById(R.id.playbackWaveformView);
        playFab = (FloatingActionButton) findViewById(R.id.playFab);
        viewSegmentFab = findViewById(R.id.view_segment_fab);
        viewSegmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListSegmentActivity.class);
                startActivity(intent);
            }
        });

        mRecordingThread = new RecordingThread(this);
        setupPlayback();
    }

    //Get file path
    private void selectFileToPlay() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Set your required file type
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select wav file"), 1001);
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            Uri currFileURI = data.getData();
            this.uri = currFileURI;

        }
        //setupPlayback();
    }

    private void setupPlayback() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRealtimeWaveformView = findViewById(R.id.waveformView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mRecordingThread.recording()) {
                    Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
                    startAudioRecordingSafe();
                } else {
                    Toast.makeText(MainActivity.this, "Stop Recording.", Toast.LENGTH_SHORT).show();
                    mRecordingThread.stopRecording();
                }
            }
        });


        mPlaybackView.setChannels(2);
        mPlaybackView.setSampleRate(PlaybackThread.SAMPLE_RATE);
        //mPlaybackView.setSamples(samples);

        playFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaybackThread == null) {
                    samples = AudioSaver.getInstance().SavedAudioBuffer();
                    mPlaybackThread = new PlaybackThread(samples, MainActivity.this);
                } else {
                    //just change the samples
                    mPlaybackThread.setmSamples(FloatBuffer.wrap(samples));
                }

                mPlaybackView.setChannels(2);
                mPlaybackView.setSampleRate(PlaybackThread.SAMPLE_RATE);
                //mPlaybackView.setSamples(samples);

                if (!mPlaybackThread.playing()) {
                    Toast.makeText(MainActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
                    mPlaybackThread.startPlayback();
                    playFab.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    Toast.makeText(MainActivity.this, "Stop Playing.", Toast.LENGTH_SHORT).show();
                    mPlaybackThread.stopPlayback();

                    playFab.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        mRecordingThread.stopRecording();
        if (mPlaybackThread != null) {
            mPlaybackThread.stopPlayback();
        }

    }

    private short[] getAudioSample() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.jinglebells);
        byte[] data;
        try {
            data = IOUtils.toByteArray(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        ShortBuffer sb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] samples = new short[sb.limit()];
        sb.get(samples);
        return samples;
    }

    private short[] getAudioSampleFromWav() {
        try {


            WavFile wavFile = WavFile.openWavFile(uri, this);

            wavFile.display();
            //-----
            final int nChanel = wavFile.getNumChannels();
            final long nFrames = wavFile.getNumFrames();

            short[] rawSamples = getAudioSample();

            short[][] samples = new short[nChanel][((int) nFrames)];
            wavFile.readFrames(samples, (int) nFrames);

            wavFile.close();
            return samples[0];
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAudioRecordingSafe() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            mRecordingThread.startRecording();
        } else {
            requestMicrophonePermission();
        }
    }

    private void requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
            // Show dialog explaining why we need record audio
            Snackbar.make(mRealtimeWaveformView, "Microphone access is required in order to record audio",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mRecordingThread.stopRecording();
        }
    }

    // Audio data delegate
    @Override
    public void onAudioDataReceived(short[] data) {
        mRealtimeWaveformView.setSamples(data);

        audioDataCollector.collect(data);

    }

    @Override
    public void onAudioDataReceived(float[] data) {
        //mRealtimeWaveformView.setSamples(data);

        audioDataCollector.collect(data);

    }

    // Segmenting delegate
    @Override
    public void processorDidReceiveSegment(AudioSegment audioSegment) {
        // got a segment

        // padding / cutting
        Log.d("Got a segment!", "-------------------\n------------\n--------");
        AudioSaver.getInstance().append(audioSegment);
    }

    // Audio playback thread listener
    @Override
    public void onProgress(int progress) {
        mPlaybackView.setMarkerPosition(progress);
    }

    @Override
    public void onCompletion() {
        mPlaybackView.setMarkerPosition(mPlaybackView.getAudioLength());
        playFab.setImageResource(android.R.drawable.ic_media_play);

    }
}
