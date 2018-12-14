package com.ytwd.midiengine.events;

import com.ytwd.midiengine.VariableLengthInt;

import java.io.InputStream;

/**
 * Created by yanwei on 4/18/16.
 */
public class MidiPianoOffEvent extends  MidiChannelEvent{
    private int event_key_index;

    public int getEvent_key_pressure() {
        return event_key_pressure;
    }

    public void setEvent_key_pressure(int event_key_pressure) {
        this.event_key_pressure = event_key_pressure;
    }

    private int event_key_pressure;

    public int getEvent_key_index() {
        return event_key_index;
    }

    public void setEvent_key_index(int event_key_index) {
        this.event_key_index = event_key_index;
    }

    public static MidiEvent buildSubEvent(InputStream inputStream, int origin_byte)
    {
        try {
            int key_index = inputStream.read();
            int key_pressure = inputStream.read();

            MidiPianoOffEvent event = new MidiPianoOffEvent();
            event.event_key_index = key_index;
            event.event_key_pressure = key_pressure;
            return event;
        }catch (Exception e){e.printStackTrace();}

        return null;
    }
}
