package com.ytwd.midiengine.events;

import java.io.InputStream;

/**
 * Created by yanwei on 4/18/16.
 */
public class MidiPianoOnEvent extends  MidiChannelEvent{
    private int event_key_index;
    private int event_key_pressure;

    public static MidiEvent buildSubEvent(InputStream inputStream, int origin_byte)
    {
        try {
            int key_index = inputStream.read();
            int key_pressure = inputStream.read();

            if(key_pressure != 0x00) {
                MidiPianoOnEvent event = new MidiPianoOnEvent();
                event.event_key_index = key_index;
                event.event_key_pressure = key_pressure;
                return event;
            }
            else
            {
                MidiPianoOffEvent event = new MidiPianoOffEvent();
                event.setEvent_key_index(key_index);
                event.setEvent_key_pressure(key_pressure);
                return event;
            }
        }catch (Exception e){e.printStackTrace();}

        return null;
    }
}
