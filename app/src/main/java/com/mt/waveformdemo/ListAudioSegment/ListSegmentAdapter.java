package com.mt.waveformdemo.ListAudioSegment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mt.waveformdemo.Audio.data.AudioSegment;
import com.mt.waveformdemo.R;

import java.util.ArrayList;

/**
 * Created by macbook on 4/4/19.
 */

public class ListSegmentAdapter extends RecyclerView.Adapter<SegmentViewHolder> {
    ArrayList<AudioSegment> segmentList = new ArrayList<>();
    RecycleViewClickListener clickListener;

    public ListSegmentAdapter(ArrayList<AudioSegment> segmentList, RecycleViewClickListener clickListener) {
        this.segmentList = segmentList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SegmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_segment, parent, false);
        SegmentViewHolder vh = new SegmentViewHolder(v);
        vh.setClickListener(this.clickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SegmentViewHolder holder, int position) {
        holder.bindData(segmentList.get(position));
    }


    @Override
    public int getItemCount() {
        return segmentList.size();
    }
}
