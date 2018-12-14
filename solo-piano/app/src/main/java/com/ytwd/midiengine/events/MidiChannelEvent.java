package com.ytwd.midiengine.events;

import com.ytwd.midiengine.VariableLengthInt;

import java.io.InputStream;

/**
 * Created by yanwei on 4/15/16.
 */
public class MidiChannelEvent extends  MidiEvent{
    public static MidiEvent buildSubEvent(InputStream inputStream, int origin_byte)
    {
        // origin_byte == 0xff
        int cmd_code = (byte)((origin_byte & 0xff) >> 4);
        int cmd_channel = (byte)(origin_byte & 0x0f);

        MidiEvent event = null;
        try {
            //int event_sub_type = inputStream.read();

            if(cmd_code == 0x08)
            {
                event = MidiPianoOffEvent.buildSubEvent(inputStream, 0);
            }
            else if(cmd_code == 0x09)
            {
                event = MidiPianoOnEvent.buildSubEvent(inputStream, 0);
            }
        }catch (Exception e){e.printStackTrace();}
        return event;
    }
}
