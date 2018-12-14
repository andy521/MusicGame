package com.ytwd.midiengine.SimpleKeyStreamRecord;

import java.io.Serializable;

/**
 * Created by yanwei on 4/19/16.
 */
public class RecordItem implements Serializable{
    int piano_type; // default piano: 0
    boolean piano_key_is_white; // 0: white key; 1: black key;
    int piano_key_index;    // from 0. [0, 51] for whitekey and [0, 51] for blackkey
    int piano_key_action;   // 0: pressed. 1: up and left
    long piano_key_time;     // ms from original time

    boolean piano_position;//true代表上键盘,false代表下键盘

    public boolean getPiano_position() {
        return piano_position;
    }

    public void setPiano_position(boolean piano_position) {
        this.piano_position = piano_position;
    }

    public int getPiano_type() {
        return piano_type;
    }

    public void setPiano_type(int piano_type) {
        this.piano_type = piano_type;
    }

    public boolean isPiano_key_is_white() {
        return piano_key_is_white;
    }

    public void setPiano_key_is_white(boolean piano_key_is_white) {
        this.piano_key_is_white = piano_key_is_white;
    }

    public int getPiano_key_index() {
        return piano_key_index;
    }

    public void setPiano_key_index(int piano_key_index) {
        this.piano_key_index = piano_key_index;
    }

    public int getPiano_key_action() {
        return piano_key_action;
    }

    public void setPiano_key_action(int piano_key_action) {
        this.piano_key_action = piano_key_action;
    }

    public long getPiano_key_time() {
        return piano_key_time;
    }

    public void setPiano_key_time(long piano_key_time) {
        this.piano_key_time = piano_key_time;
    }

    //public static RecordItem createRecordItem(boolean isWhite, int key_index, int key_action, int key_time)
}
