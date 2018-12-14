package com.ytwd.midiengine;

import android.util.Log;

import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordItem;
import com.ytwd.midiengine.events.MidiEvent;

import java.util.ArrayList;
import java.util.List;
import piano.tiles.music.keyboard.song.am.L;

/**
 * Created by lk on 16/5/13.
 */
public class MidiToWhiteBlack {


    public final static int tick = 120;
    public final static int piano_offset= 12;

    public final static String[][] KEY_NAMES = {
            {"1", "1","1"}, {"0", "1","2"},{"1", "2","3"},{"1", "3","4"},
            {"0", "2","5"}, {"1", "4","6"},{"0", "3","7"},{"1", "5","8"},
            {"1", "6","9"}, {"0", "4","10"},{"1", "7","11"},{"0", "5","12"},
            {"1", "8","13"}, {"0", "6","14"},{"1", "9","15"},{"1", "10","16"},
            {"0", "7","17"}, {"1", "11","18"},{"0", "8","19"},{"1", "12","20"},
            {"1", "13","21"}, {"0", "9","22"},{"1", "14","23"},{"0", "10","24"},
            {"1", "15","25"}, {"0", "11","26"},{"1", "16","27"},{"1", "17","28"},
            {"0", "12","29"}, {"1", "18","30"},{"0", "13","31"},{"1", "19","32"},
            {"1", "20","33"}, {"0", "14","34"},{"1", "21","35"},{"0", "15","36"},
            {"1", "22","37"}, {"0", "16","38"},{"1", "23","39"},{"1", "24","40"},
            {"0", "17","41"}, {"1", "25","42"},{"0", "18","43"},{"1", "26","44"},
            {"1", "27","45"}, {"0", "19","46"},{"1", "28","47"},{"0", "20","48"},
            {"1", "29","49"}, {"0", "21","50"},{"1", "30","51"},{"1", "31","52"},
            {"0", "22","53"}, {"1", "32","54"},{"0", "23","55"},{"1", "33","56"},
            {"1", "34","57"}, {"0", "24","58"},{"1", "35","59"},{"0", "25","60"},
            {"1", "36","61"}, {"0", "26","62"},{"1", "37","63"},{"1", "38","64"},
            {"0", "27","65"}, {"1", "39","66"},{"0", "28","67"},{"1", "40","68"},
            {"1", "41","69"}, {"0", "29","70"},{"1", "42","71"},{"0", "30","72"},
            {"1", "43","73"}, {"0", "31","74"},{"1", "44","75"},{"1", "45","76"},
            {"0", "32","77"}, {"1", "46","78"},{"0", "33","79"},{"1", "47","80"},
            {"1", "48","81"}, {"0", "34","82"},{"1", "49","83"},{"0", "35","84"},
            {"1", "50","85"}, {"0", "36","86"},{"1", "51","87"},{"1", "52","88"}

    };


    public final static String[][] KEY_WHITE = {

            {"1", "1","1"},{"1", "2","3"},{"1", "3","4"},{"1", "4","6"},
            {"1", "5","8"},{"1", "6","9"},{"1", "7","11"},{"1", "8","13"},
            {"1", "9","15"},{"1", "10","16"},{"1", "11","18"},{"1", "12","20"},
            {"1", "13","21"},{"1", "14","23"},{"1", "15","25"},{"1", "16","27"},
            {"1", "17","28"},{"1", "18","30"},{"1", "19","32"},{"1", "20","33"},
            {"1", "21","35"},{"1", "22","37"},{"1", "23","39"},{"1", "24","40"},
            {"1", "25","42"},{"1", "26","44"},{"1", "27","45"},{"1", "28","47"},
            {"1", "29","49"},{"1", "30","51"},{"1", "31","52"},{"1", "32","54"},
            {"1", "33","56"},{"1", "34","57"},{"1", "35","59"},{"1", "36","61"},
            {"1", "37","63"},{"1", "38","64"},{"1", "39","66"},{"1", "40","68"},
            {"1", "41","69"},{"1", "42","71"},{"1", "43","73"},{"1", "44","75"},
            {"1", "45","76"},{"1", "46","78"},{"1", "47","80"},{"1", "48","81"},
            {"1", "49","83"}, {"1", "50","85"},{"1", "51","87"},{"1", "52","88"}


    };

    public final static String[][] KEY_BLACK = {

            {"0", "1","2"},{"0", "2","5"},{"0", "3","7"},
            {"0", "4","10"},{"0", "5","12"},{"0", "6","14"},
            {"0", "7","17"},{"0", "8","19"},{"0", "9","22"},
            {"0", "10","24"},{"0", "11","26"},{"0", "12","29"},
            {"0", "13","31"},{"0", "14","34"},{"0", "15","36"},
            {"0", "16","38"},{"0", "17","41"},{"0", "18","43"},
            {"0", "19","46"},{"0", "20","48"},{"0", "21","50"},
            {"0", "22","53"},{"0", "23","55"},{"0", "24","58"},
            {"0", "25","60"},{"0", "26","62"},{"0", "27","65"},
            {"0", "28","67"},{"0", "29","70"},{"0", "30","72"},
            {"0", "31","74"},{"0", "32","77"},{"0", "33","79"},
            {"0", "34","82"},{"0", "35","84"},{"0", "36","86"}

    };

    public final static String[][] INDEX53_TO_KEY_BLACK = {

            {"0", "1","2"},{"0", "1","1"},
            {"0", "2","5"},{"0", "3","7"},{"0", "1","1"},
            {"0", "4","10"},{"0", "5","12"},{"0", "6","14"},{"0", "1","1"},
            {"0", "7","17"},{"0", "8","19"},{"0", "1","1"},
            {"0", "9","22"}, {"0", "10","24"},{"0", "11","26"},{"0", "1","1"},
            {"0", "12","29"}, {"0", "13","31"},{"0", "1","1"},
            {"0", "14","34"},{"0", "15","36"}, {"0", "16","38"},{"0", "1","1"},
            {"0", "17","41"},{"0", "18","43"},{"0", "1","1"},
            {"0", "19","46"},{"0", "20","48"},{"0", "21","50"},{"0", "1","1"},
            {"0", "22","53"},{"0", "23","55"},{"0", "1","1"},
            {"0", "24","58"}, {"0", "25","60"},{"0", "26","62"},{"0", "1","1"},
            {"0", "27","65"}, {"0", "28","67"},{"0", "1","1"},
            {"0", "29","70"},{"0", "30","72"}, {"0", "31","74"},{"0", "1","1"},
            {"0", "32","77"},{"0", "33","79"},{"0", "1","1"},
            {"0", "34","82"},{"0", "35","84"},{"0", "36","86"},{"0", "1","1"},
            {"0", "1","1"},{"0", "1","1"}

    };

    public final static String[][] KEY_BLACK_TO_INDEX53 = {

            {"0", "1","0"},
            {"0", "2","2"},{"0", "3","3"},
            {"0", "4","5"},{"0", "5","6"},{"0", "6","7"},
            {"0", "7","9"},{"0", "8","10"},
            {"0", "9","12"}, {"0", "10","13"},{"0", "11","14"},
            {"0", "12","16"}, {"0", "13","17"},
            {"0", "14","19"},{"0", "15","20"}, {"0", "16","21"},
            {"0", "17","23"},{"0", "18","24"},
            {"0", "19","26"},{"0", "20","27"},{"0", "21","28"},
            {"0", "22","30"},{"0", "23","31"},
            {"0", "24","33"}, {"0", "25","34"},{"0", "26","35"},
            {"0", "27","37"}, {"0", "28","38"},
            {"0", "29","40"},{"0", "30","41"}, {"0", "31","42"},
            {"0", "32","44"},{"0", "33","45"},
            {"0", "34","47"},{"0", "35","48"}, {"0", "36","49"}

    };
    public ArrayList<RecordItem>  indexToWhiteBlack(ArrayList<MidiEvent> midievent_List){

        ArrayList<RecordItem> array_record_items = new ArrayList<RecordItem>();
        try {


            for (int i = 0; i < midievent_List.size(); i++) {

                MidiEvent midiEvent = midievent_List.get(i);
                RecordItem recordItem = new RecordItem();

                int key = Integer.valueOf(midiEvent.getKey_value()) - 1 - piano_offset;
                String isWhite = KEY_NAMES[key][0];
                String index = KEY_NAMES[key][1];


                recordItem.setPiano_key_action(midiEvent.getKey_action());
                if("1".equals(isWhite)){
                    recordItem.setPiano_key_index(Integer.valueOf(index)-1);
                    recordItem.setPiano_key_is_white(true);
                }else{
                    recordItem.setPiano_key_index(Integer.valueOf(KEY_BLACK_TO_INDEX53[Integer.valueOf(index)-1][2]));
                    recordItem.setPiano_key_is_white(false);
                }
                recordItem.setPiano_key_time(Long.valueOf(midiEvent.getTime_diffence()));
                recordItem.setPiano_position(true);
                recordItem.setPiano_type(0);

                L.v("midi","time++"+Long.valueOf(midiEvent.getTime_diffence())+"Piano_key_action++"+recordItem.getPiano_key_action()+"setPiano_key_index++"+index);

                array_record_items.add(recordItem);
            }

        }catch (Exception e){
            e.printStackTrace();
            L.v("midi","exception+++++"+e.toString());
        }

        return array_record_items;
    }



    public ArrayList<MidiEvent> whiteBlackToIndex(ArrayList<RecordItem> record_list){

        ArrayList<MidiEvent> midiEvent_list = new ArrayList<MidiEvent>();

          for(int i = 0;i<record_list.size();i++) {

              RecordItem recordItem = record_list.get(i);
              MidiEvent midiEvent = new MidiEvent();
              int index  =  recordItem.getPiano_key_index();

              if(recordItem.isPiano_key_is_white()){
                  midiEvent.setKey_value(Integer.parseInt(KEY_WHITE[index][2])+piano_offset+"");
              }else{
                  midiEvent.setKey_value(Integer.parseInt(INDEX53_TO_KEY_BLACK[index][2])+piano_offset+"");
              }
              midiEvent.setKey_action(recordItem.getPiano_key_action());
              midiEvent.setTime_diffence(Long.toString(recordItem.getPiano_key_time()));

              midiEvent_list.add(midiEvent);

              L.v("file","Piano_key_index++++"+recordItem.getPiano_key_index()+"");
              L.v("file","Key_value+++++"+midiEvent.getKey_value());
          }


       return  midiEvent_list;
    }


}