package com.mt.waveformdemo.ListAudioSegment;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mt.waveformdemo.Audio.type.AudioSegment;
import com.mt.waveformdemo.R;

/**
 * Created by macbook on 4/4/19.
 */

class SegmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView tvName;
    Button playBtn;
    AudioSegment segment;

    RecycleViewClickListener clickListener;

    public void setClickListener(RecycleViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public SegmentViewHolder(View itemView) {
        super(itemView);
        // init view
        tvName = itemView.findViewById(R.id.segment_name);
        playBtn = itemView.findViewById(R.id.play_segment_btn);
        playBtn.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    public void bindData(AudioSegment audioSegment) {
        segment = audioSegment;
        tvName.setText("Segment with start time index:" + audioSegment.getSegment().get(0).getTimeIndex());
    }

    @Override
    public void onClick(View view) {
        if (clickListener != null) {
            clickListener.recyclerViewListClicked(view, this.getAdapterPosition());
        }
    }
}
