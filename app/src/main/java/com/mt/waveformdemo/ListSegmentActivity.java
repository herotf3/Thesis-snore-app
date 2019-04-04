package com.mt.waveformdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.mt.waveformdemo.Audio.AudioSaver;
import com.mt.waveformdemo.Audio.io.PlaybackListener;
import com.mt.waveformdemo.Audio.io.PlaybackThread;
import com.mt.waveformdemo.Audio.type.AudioSegment;
import com.mt.waveformdemo.ListAudioSegment.ListSegmentAdapter;
import com.mt.waveformdemo.ListAudioSegment.RecycleViewClickListener;

import java.util.ArrayList;

// This activity hold a list of segment received from recording
// click an item to play a specific audio segment
public class ListSegmentActivity extends AppCompatActivity implements RecycleViewClickListener, PlaybackListener {

    ArrayList<AudioSegment> listSegment = new ArrayList<>();
    RecyclerView recyclerView;
    ListSegmentAdapter listSegmentAdapter;

    PlaybackThread playbackThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_segment);

        listSegment = AudioSaver.getInstance().segmentsHolder;
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.list_segment);

        listSegmentAdapter = new ListSegmentAdapter(listSegment, ListSegmentActivity.this);
        recyclerView.setAdapter(listSegmentAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    // Segment clicked -- play
    @Override
    public void recyclerViewListClicked(View v, int position) {
        Toast.makeText(ListSegmentActivity.this, "play a segment..", Toast.LENGTH_SHORT).show();

        if (playbackThread == null) {
            playbackThread = new PlaybackThread(ListSegmentActivity.this);
        }
        playbackThread.setSegment(listSegment.get(position));
        // play the selected segment
        if (playbackThread.playing()) {
            playbackThread.stopPlayback();
        } else {
            playbackThread.startPlayback();
        }
    }


    // Playback listener
    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onCompletion() {
        playbackThread.stopPlayback();
        Toast.makeText(this, "Playback finish.", Toast.LENGTH_SHORT).show();
    }
}
