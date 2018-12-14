package com.ytwd.midiengine;

import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordItem;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by lk on 16/5/13.
 */
public class MidiHelper {



    public ArrayList<RecordItem> getSoundList(InputStream paramInputStream){


        MifiFileUtil mifiFileUtil = new MifiFileUtil();
        MidiToWhiteBlack midiToWhiteBlack = new MidiToWhiteBlack();
        MidiFileInfo midiFileInfo = mifiFileUtil.loadFromFile(paramInputStream);


        return  midiToWhiteBlack.indexToWhiteBlack(midiFileInfo.midi_track_array.get(0).track_event_set);
    }


    public ArrayList<RecordItem> getSoundList(File file){


        MifiFileUtil mifiFileUtil = new MifiFileUtil();
        MidiToWhiteBlack midiToWhiteBlack = new MidiToWhiteBlack();
        MidiFileInfo midiFileInfo = mifiFileUtil.loadFromFile(file);


        return  midiToWhiteBlack.indexToWhiteBlack(midiFileInfo.midi_track_array.get(0).track_event_set);
    }



    public void writeMidi(File file,ArrayList<RecordItem> array_record_items){

        MifiFileUtil mifiFileUtil = new MifiFileUtil();
        MidiToWhiteBlack midiToWhiteBlack = new MidiToWhiteBlack();
        mifiFileUtil.writeMidiFile(file,midiToWhiteBlack.whiteBlackToIndex(array_record_items));

    }

}
