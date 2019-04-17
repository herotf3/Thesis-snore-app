package com.mt.waveformdemo.Audio.data;

import android.support.annotation.NonNull;


import com.mt.waveformdemo.Audio.utils.AudioFormatConfig;

import java.util.Iterator;

/**
 * Created by nmman on 4/8/2019.
 */

public class AudioSegment implements Segment<AudioFrame> {
    private SegmentNode<AudioFrame> head;
    private SegmentNode<AudioFrame> tail;
    private SegmentNode<AudioFrame> cursor;
    private int length = 0;

    public AudioSegment() {
        this.head = null;
        this.tail = null;
        this.cursor = null;
    }

    public AudioSegment(AudioSegment segment) {
        for (AudioFrame frame : segment) {
            pushBack(frame);
        }
    }

    public AudioSegment(SegmentNode<AudioFrame> head, SegmentNode<AudioFrame> tail) {
        this.head = head;
        this.tail = tail;
        this.tail.setNextNode(null);
        this.cursor = head;
        initLength();
    }

    private void initLength() {
        SegmentNode p = head;
        int c = 0;
        while (p != null) {
            p = p.next();
            c++;
        }
        length = c;
    }

    @NonNull
    @Override
    public Iterator<AudioFrame> iterator() {
        checkEmpty();
        cursor = head;
        return new Iterator<AudioFrame>() {
            @Override
            public boolean hasNext() {
                return cursor != null;
            }

            @Override
            public AudioFrame next() {
                SegmentNode<AudioFrame> tmp = cursor;
                cursor = cursor.next();
                return tmp.getData();
            }
        };
    }

    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public void combineWith(Segment<AudioFrame> segment) {
        checkEmpty();
        tail.setNextNode(segment.getHead());
        tail = segment.getTail();
    }

    @Override
    public Segment<AudioFrame> subSegment(int start, int length) {
        checkEmpty();
        if (start + length > this.length - 1) {
            throw new ArrayIndexOutOfBoundsException(start + length);
        }
        AudioSegment segment = new AudioSegment();
        SegmentNode<AudioFrame> p = getNode(start);
        int c = 0;
        while (p != null) {
            if (c == start + length) {
                break;
            }
            if (c >= start) {
                segment.pushBack(p.getData());
            }
            p = p.next();
            c++;
        }
        return segment;
    }

    @Override
    public void pushBack(AudioFrame frame) {
        if (frame == null) {
            return;
        }
        SegmentNode<AudioFrame> node = SegmentNode.create(frame);
        node.setNextNode(null);
        if (head == null) {
            head = node;
            tail = node;
        } else if (head == tail) {
            tail = node;
            head.setNextNode(tail);
        } else {
            tail.setNextNode(node);
            tail = node;
        }
        length++;
    }

    @Override
    public void pushFront(AudioFrame frame) {
        if (frame == null) {
            return;
        }
        SegmentNode<AudioFrame> node = SegmentNode.create(frame);
        node.setNextNode(null);

        if (head == null) {
            head = node;
            tail = node;
        } else if (head == tail) {
            head = node;
            head.setNextNode(tail);
        } else {
            node.setNextNode(head);
            head = node;
        }
        length++;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
    }

    @Override
    public SegmentNode<AudioFrame> getNode(int i) {
        checkEmpty();
        SegmentNode<AudioFrame> p = head;
        int c = 0;
        while (p != null) {
            if (c == i) {
                return p;
            }
            p = p.next();
            c++;
        }
        return null;
    }

    /**
     * Increase number of nodes by overlapping
     *
     * @param numNode is desired number of nodes, must be greater than this.length
     */
    @Override
    public void stretch(int numNode) {
        checkEmpty();
        if (numNode <= this.length) {
            return;
        }
        int samplePerFrame = head.getData().getnSamples();
        int totalSamples = samplePerFrame * this.length;
        int overlap = (numNode * samplePerFrame - totalSamples) / (numNode - 1);
        if (overlap < 0 || overlap >= samplePerFrame) {
            throw new IllegalArgumentException("Invalid overlap");
        }
        // Shift left all nodes
        int counter = 0;
        Iterator<AudioFrame> it = iterator();
        int shift = overlap;
        while (it.hasNext()) {
            it.next().shiftLeft(shift);
            shift += overlap;
            counter++;
        }
        // Add more overlap nodes
        int startPos = tail.getData().getEndDataPos() + 1;
        while (counter < numNode) {
            AudioFrame frame = new AudioFrame(head.getData().getSourceSignal(), startPos, samplePerFrame);
            pushBack(frame);
            startPos += (samplePerFrame - overlap);
            counter++;
        }
    }

    @Override
    public SegmentNode<AudioFrame> getHead() {
        return head;
    }

    @Override
    public SegmentNode<AudioFrame> getTail() {
        return tail;
    }

    @Override
    public float getSampleRate() {
        checkEmpty();
        return head.getData().getSourceSignal().getSampleRate();
    }

    @Override
    public int getFrameSize() {
        checkEmpty();
        return head.getData().getnSamples();
    }

    @Override
    public int getNumberSample() {
        return tail.getData().getDataCursor()-head.getData().getDataCursor()+1;
    }

    @Override
    public SignalFormat sourceSignalFormat() {
        checkEmpty();
        return head.getData().getSourceSignal().getSignalFormat();
    }

    @Override
    public void mergeWith(Segment<AudioFrame> segment) {
        if (segment.isEmpty()){
            return;
        }
        if (this.isEmpty()){
            this.head = segment.getHead();
            this.tail = segment.getTail();
            this.length = segment.length();
        }else{
            this.tail.setNextNode(segment.getHead());
            this.tail = segment.getTail();
            this.length+= segment.length();
        }

    }
//------------------------------
    private void checkEmpty() {
        if (isEmpty()) {
            throw new IllegalStateException("Segment is empty");
        }
    }

    public float[] getSamplesArray() {
        Iterator<AudioFrame> it = this.iterator();
        AudioFrame firstFrame = head.getData();
        AudioSignal signal = firstFrame.getSourceSignal();

        return new float[0];
    }

    public void addPadding(int nFrame) {
        AudioSignal signal = AudioSignal.newZeroSignal(nFrame* AudioFormatConfig.FRAME_SIZE);
        for (int i=0;i<nFrame;i++){
            AudioFrame frame = new AudioFrame(signal,0,AudioFormatConfig.FRAME_SIZE);
            this.pushBack(frame);
        }
    }
}
