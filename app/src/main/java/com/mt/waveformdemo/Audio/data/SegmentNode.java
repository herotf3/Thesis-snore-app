package com.mt.waveformdemo.Audio.data;

/**
 * Created by nmman on 4/8/2019.
 */

public class SegmentNode<T> {
    private T data;
    private SegmentNode<T> nextNode;

    public SegmentNode(T frame, SegmentNode<T> next) {
        this.data = frame;
        this.nextNode = next;
    }

    public static <T> SegmentNode<T> create(T data) {
        return new SegmentNode<>(data, null);
    }

    public T getData() {
        return data;
    }

    public void setFrame(T data) {
        this.data = data;
    }

    public void setNextNode(SegmentNode<T> nextNode) {
        this.nextNode = nextNode;
    }

    public SegmentNode<T> next() {
        return nextNode;
    }
}
