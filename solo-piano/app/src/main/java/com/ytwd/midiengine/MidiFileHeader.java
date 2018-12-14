package com.ytwd.midiengine;

/**
 * Created by yanwei on 4/15/16.
 */
class MidiFileHeader
{
    //byte[] header_flag; // 4 Bytes: 4D 54 68 64
    //int    header_param_length; // =6 normally: 00 00 00 06
    short  header_type; // 0: single track. 1: multi track. 2: multi track with async
    short  header_track_count; //
    short  header_tick_count; // =120 only currently. 00 78

}
