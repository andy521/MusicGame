package com.ytwd.midiengine.events;

import android.renderscript.ScriptGroup;

import com.ytwd.midiengine.VariableLengthInt;

import java.io.InputStream;

/**
 * Created by yanwei on 4/15/16.
 */
public class MidiEvent {
    public enum TYPE{
        MIDI_EVENT_TYPE_MIDI,
        MIDI_EVENT_TYPE_META,
        MIDI_EVENT_TYPE_SYSTEM,
    }

    public int event_delta_tick_count; // offset count to the previous event
    public int event_command;
    public int event_tick_from_zero; // tick from zero. important.

    public String key_value;
    public String time_diffence;
    public int key_action; //0: pressed. 1: up and left

    public void setKey_action(int key_action) {
        this.key_action = key_action;
    }

    public int getKey_action() {
        return key_action;
    }

    public void setTime_diffence(String time_diffence) {
        this.time_diffence = time_diffence;
    }

    public String getTime_diffence() {
        return time_diffence;
    }

    public void setKey_value(String key_value) {
        this.key_value = key_value;
    }

    public String getKey_value() {
        return key_value;
    }

    public static MidiEvent buildSubEvent(InputStream inputStream, int origin_byte)
    {
        return null;
    }

    public static MidiEvent buildEvent(InputStream inputStream){
        int event_delta_tick_count;

        VariableLengthInt integer = new VariableLengthInt(inputStream);
        event_delta_tick_count = integer.getIntegerValue();

        MidiEvent event = null;
        try {
            int type_byte = inputStream.read();
            MidiEvent.TYPE event_type = getMidiEventType(type_byte);
            // TODO
            if(event_type == TYPE.MIDI_EVENT_TYPE_META)
            {
                event = new MidiMetaEvent();
                event.buildSubEvent(inputStream, type_byte);
            }

        }catch (Exception e){e.printStackTrace();}
        return event;
    }

    private static MidiEvent.TYPE getMidiEventType(int type_value)
    {
        // TODO: not exactly correct.
        if(type_value >= 0x80 && type_value <= 0xef)
            return MidiEvent.TYPE.MIDI_EVENT_TYPE_MIDI;
        if(type_value == 0xff)
            return MidiEvent.TYPE.MIDI_EVENT_TYPE_META;

        return MidiEvent.TYPE.MIDI_EVENT_TYPE_SYSTEM;
    }
}
