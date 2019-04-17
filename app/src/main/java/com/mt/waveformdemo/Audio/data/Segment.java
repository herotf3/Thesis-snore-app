package com.mt.waveformdemo.Audio.data;

/**
 * Created by nmman on 4/8/2019.
 */

public interface Segment<T> extends Iterable<T> {
    void combineWith(Segment<T> segment);

    Segment<T> subSegment(int start, int length);

    void pushBack(T e);

    void pushFront(T e);

    int length();

    boolean isEmpty();

    void clear();

    SegmentNode<T> getNode(int i);

    SegmentNode<T> getHead();

    SegmentNode<T> getTail();

    void stretch(int numNode);

    float getSampleRate();

    int getFrameSize();

    int getNumberSample();

    SignalFormat sourceSignalFormat();

    void mergeWith(Segment<T> segment);
}
