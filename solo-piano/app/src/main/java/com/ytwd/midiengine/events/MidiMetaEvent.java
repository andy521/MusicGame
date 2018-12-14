package com.ytwd.midiengine.events;

import com.ytwd.midiengine.VariableLengthInt;

import java.io.InputStream;

/**
 * Created by yanwei on 4/15/16.
 */
public class MidiMetaEvent extends MidiEvent {
    private int event_cmd_code;
    private byte[] event_cmd_parameter;

    public static MidiEvent buildSubEvent(InputStream inputStream, int origin_byte)
    {
        // origin_byte == 0xff
        try {
            int event_sub_type = inputStream.read();

            VariableLengthInt integer = new VariableLengthInt(inputStream);
            int event_sub_data_body_length = integer.getIntegerValue(); // data body length

            byte[] body_data = new byte[event_sub_data_body_length];
            inputStream.read(body_data);

            MidiMetaEvent event = new MidiMetaEvent();
            event.event_cmd_code = event_sub_type;
            event.event_cmd_parameter = body_data;
            return event;
        }catch (Exception e){e.printStackTrace();}

        return null;
    }
}
