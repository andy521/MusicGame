package com.ytwd.midiengine;

import com.ytwd.midiengine.events.MidiEvent;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by yanwei on 4/15/16.
 */
public class MidiTrackInfo {
    //byte[] track_flag; // 4 bytes. 4D 54 72 6B
    //int    track_info_length; // = sizeof(MidiTrackInfo) - 8
    ArrayList<MidiEvent> track_event_set;
}
