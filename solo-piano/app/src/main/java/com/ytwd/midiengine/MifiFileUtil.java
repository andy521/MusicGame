package com.ytwd.midiengine;

import android.renderscript.ScriptGroup;
import android.util.Log;

import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordItem;
import com.ytwd.midiengine.events.MidiEvent;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TreeSet;

import piano.tiles.music.keyboard.song.am.L;

/**
 * Created by yanwei on 4/15/16.
 */
public class MifiFileUtil {


    private final static String MiDiTitle = "4d546864000000060001000100784d54726b";
    private final static String MiDiEnd= "60ff2f00";

    public MidiFileInfo loadFromFile(File file)
    {
        try {
            return loadFromFile(new FileInputStream(file));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public MidiFileInfo loadFromFile(InputStream paramInputStream)
    {
        MidiFileInfo fileInfo = new MidiFileInfo();
        fileInfo.midi_track_array = new ArrayList<MidiTrackInfo>();

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(paramInputStream);
            StringBuffer midi_stringbuffer = new StringBuffer("");
            byte[] bytes = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = bufferedInputStream.read(bytes)) != -1) {
               midi_stringbuffer.append(byte2hex(bytes));
            }
            paramInputStream.close();
            bufferedInputStream.close();
            String midi_string = midi_stringbuffer.toString();

            L.v("midi",midi_string);



            fileInfo.midi_header = readMidiHeader(midi_string.toString());

            String[] track_array = midi_string.split("4D54");
            L.v("midi",track_array.length+"");

            L.v("midi",fileInfo.midi_header.header_type+"");
            L.v("midi",fileInfo.midi_header.header_tick_count+"");
            L.v("midi",fileInfo.midi_header.header_track_count+"");


            if(fileInfo.midi_header != null)
            {
               // for(int i = 0; i < track_array.length; i++)
               // {
                     //Log.v("midi","track---"+i+"---"+track_array[i].toString());
                if(track_array.length>3) {
                    fileInfo.midi_track_array.add(readTrackInfo(track_array[3], fileInfo.midi_header.header_tick_count));
                }else{
                    fileInfo.midi_track_array.add(readTrackInfo(track_array[2], fileInfo.midi_header.header_tick_count));
                }
               // }
            }

        }catch (Exception e){
            e.printStackTrace();
            L.v("midi","exception+++++"+e.toString());
        }

        return fileInfo;
    }
    private MidiTrackInfo readTrackInfo(InputStream inputStream)
    {
        MidiTrackInfo trackInfo = new MidiTrackInfo();
        byte[] bytes = new byte[8];
        int track_size = bytes2int(bytes, 4, 4);

        byte[] track_buffer = new byte[track_size];
//        L.e("track_size",track_size+"");
        try{
            inputStream.read(track_buffer);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(track_buffer);
            for(;;)
            {
                if(byteArrayInputStream.available() <= 0)
                    break;
                //MidiEvent event = readEvent(byteArrayInputStream);
                MidiEvent event = MidiEvent.buildEvent(byteArrayInputStream);
                // TODO: check if it's EndOfEvent type. if yes, ignore it.
                if(event != null)
                    trackInfo.track_event_set.add(event);
            }
        }
        catch (Exception e){e.printStackTrace();}

        return trackInfo;
    }

    private MidiTrackInfo readTrackInfo(String track_string, short header_tick_count)
    {
        MidiTrackInfo trackInfo = new MidiTrackInfo();
       trackInfo.track_event_set = new ArrayList<MidiEvent>();
        boolean is_note = false;
        boolean is_force = false;
        boolean is_time_difference = false;
        boolean is_first = true;
        boolean is_control = false;

        int control_number = 0;
        int key_action = 0;
        String time_diffence = "";

        for(int i = 0;i<track_string.length()/2;i++){


             if(is_first) {
                 if ("9".equals((track_string.charAt(i * 2) + ""))) {
                     is_note = true;
                     is_first = false;
                 }
             }else{
                 if(is_note){

                         if(!is_control) {
                             if ("8".equals(track_string.charAt(i * 2) + "")) {
                                 key_action = 1;
                             } else if ("9".equals(track_string.charAt(i * 2) + "")) {
                                 key_action = 0;
                             } else if ("A".equals(track_string.charAt(i * 2) + "")||"B".equals(track_string.charAt(i * 2) + "")||"E".equals(track_string.charAt(i * 2) + "")) {
                                 is_control = true;
                                 control_number = 2;

                                 is_force = false;
                                 is_time_difference = false;
                             } else if ("C".equals(track_string.charAt(i * 2) + "")||"D".equals(track_string.charAt(i * 2) + "")) {
                                 is_control = true;
                                 control_number = 1;

                                 is_force = false;
                                 is_time_difference = false;
                             } else if ("FF".equals(track_string.substring(i * 2, i * 2 + 2))) {

                                 break;

                             } else {



                                     int time_diffence_ms = 500;

                                     time_diffence_ms = timeDiffrenceToMsInt(time_diffence,header_tick_count);
//                                     if (!"".equals(time_diffence)) {
//
//                                         if(time_diffence.length()==2){
//                                             time_diffence_ms = 1000 / header_tick_count * Integer.valueOf(time_diffence, 16);
//                                         }else if(time_diffence.length()==4) {
//                                             int time = Integer.valueOf(time_diffence.charAt(1)+"",16)*80+Integer.valueOf(time_diffence.substring(2,4),16);
//                                             time_diffence_ms = 1000 / header_tick_count * time;
//                                         }
//                                     }

                                     L.v("midi", track_string.substring(i * 2, i * 2 + 2) + "-------" + time_diffence);

                                     //L.v("midi",Integer.valueOf(track_string.substring(i*2,i*2+2),16)-20+"-------"+time_diffence_ms);


                                     MidiEvent midiEvent = new MidiEvent();
                                     midiEvent.setKey_value(Integer.valueOf(track_string.substring(i * 2, i * 2 + 2), 16) - 20 + "");
                                     midiEvent.setTime_diffence(time_diffence_ms + "");
                                     midiEvent.setKey_action(key_action);
                                     trackInfo.track_event_set.add(midiEvent);
                                     time_diffence = "";


                                     is_note = false;
                                     is_force = true;
                                     is_time_difference = false;

                                 }


                         }else{
                             if(control_number == 1){
                                 control_number = 0;
                                 time_diffence = "";
                                 is_control = false;
                                 is_note = false;
                                 is_force = false;
                                 is_time_difference = true;
                             }else{
                             control_number--;}
                         }
                 }else if(is_force){

                     is_note = false;
                     is_force = false;
                     is_time_difference = true;

                 }else if(is_time_difference){

                     time_diffence = time_diffence + track_string.substring(i*2,i*2+2);

                     if((track_string.charAt(i*2)+"").compareTo("7")>0){

                     }else{

                         is_time_difference = false;
                         is_force = false;
                         is_note = true;

                     }
                 }
             }
        }


        return trackInfo;
    }




    private MidiFileHeader readMidiHeader(byte[] bytes)
    {
        MidiFileHeader header = new MidiFileHeader();

        header.header_type = (short)getInt(bytes, 8, 2);
        header.header_track_count = (short)getInt(bytes, 10, 2);
        header.header_tick_count = (short)getInt(bytes, 12, 2);


        return header;
    }

    private MidiFileHeader readMidiHeader(String string)
    {
        MidiFileHeader header = new MidiFileHeader();
        if(string.length()>28) {
            header.header_type = (short) Integer.parseInt(string.substring(16, 20), 16);
            header.header_track_count = (short) Integer.parseInt(string.substring(20, 24), 16);
            header.header_tick_count = (short) Integer.parseInt(string.substring(24, 28), 16);
        }
        return header;
    }

    public static int bytes2int(byte[] bytes, int offset, int length)
    {
        int ret = 0;
        int end = offset + length - 1;
        int bit = 8;

        while(end >= offset)
        {
            ret += (0xff & bytes[end]) << bit;
            bit += 8;
            end--;
        }
        return ret;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    public  static int getInt(byte[] buf, int offset,int len) {

        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (len > 4) {
            throw new IllegalArgumentException("byte array size > 4 !");
        }
        int r = 0;

            for (int i = offset; i < len; i++) {
                r <<= 8;
                r |= (buf[i] & 0x000000ff);
            }
        return r;
    }

    public void writeMidiFile(File file,ArrayList<MidiEvent> midiEvent_list){



          String content = MiDiTitle;
          try {

              OutputStreamWriter out = new OutputStreamWriter(
                      new FileOutputStream(file,true),"iso-8859-1");
            //  FileWriter fw  = new FileWriter(file,true);

            BufferedWriter bw = new BufferedWriter(out);

            bw.write(toStringHex1(content));

             L.v("file",midiEvent_list.size()+"");
            for(int i =0;i<midiEvent_list.size();i++){

                MidiEvent midiEvent = midiEvent_list.get(i);
                content = stringToTimeDiffrence(midiEvent.getTime_diffence());
                bw.write(toStringHex1(content));
                content = midiEvent.getKey_action()==0?"90":"80";
                bw.write(toStringHex1(content));
                content = intToHex(Integer.valueOf(midiEvent.getKey_value())+20);
                bw.write(toStringHex1(content));
                content = "64";
                bw.write(toStringHex1(content));


                L.v("file","time_diffirence++++"+midiEvent.getTime_diffence());
                L.v("file","time_diffirence_hex++++"+stringToTimeDiffrence(midiEvent.getTime_diffence()));
                L.v("file","key"+intToHex(Integer.valueOf(midiEvent.getKey_value())+20));

            }
            content = MiDiEnd;
              L.v("file","ff2f00++++"+toStringHex1(content));
              bw.write(toStringHex1(content));
            bw.flush();
            bw.close();
            out.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static int timeDiffrenceToMsInt(String time_diffence,short header_tick_count){

        int time_diffence_ms = 500;
        int time = 0;

            switch (time_diffence.length()){
                case 2:
                    time_diffence_ms = 1000 / header_tick_count * Integer.valueOf(time_diffence, 16);
                    break;
                case 4:
                    time = (Integer.valueOf(time_diffence.substring(0,2),16)-128)*128+Integer.valueOf(time_diffence.substring(2,4),16);
                    time_diffence_ms = 1000 / header_tick_count * time;
                    break;
                case 6:
                    time = (Integer.valueOf(time_diffence.substring(0,2),16)-128)*128*128+(Integer.valueOf(time_diffence.substring(2,4),16)-128)*128+Integer.valueOf(time_diffence.substring(4,6),16);
                    time_diffence_ms = 1000 / header_tick_count * time;
                    break;
                case 8:
                    time = (Integer.valueOf(time_diffence.substring(0,2),16)-128)*128*128*128+(Integer.valueOf(time_diffence.substring(2,4),16)-128)*128*128+(Integer.valueOf(time_diffence.substring(4,6),16)-128)*128+Integer.valueOf(time_diffence.substring(6,8),16);
                    time_diffence_ms = 1000 / header_tick_count * time;
                    break;
                default:
                    break;
            }


       return time_diffence_ms;
    }


    public static String stringToTimeDiffrence(String time_long){

         //max lengh 8f 8f 8f 7f


        int time_difference = Long.valueOf(time_long).intValue();//long to int range
        time_difference = time_difference*MidiToWhiteBlack.tick/1000;
         String time = "";
        int result = time_difference/(128*128*128);
        int remaind  = time_difference%(128*128*128);
        if(result!=0){
            time = "8"+result;
            result = remaind/(128*128);
            remaind = remaind%(128*128);
            if(result!=0){
                time = time + intToHexWith8(result);
                result = remaind/(128);
                remaind = remaind%(128);
                if(result!=0){
                    time = time + intToHexWith8(result);
                    result = remaind;
                    time = time + intToHex(result);
                }else{
                    time = time + "80";
                    result = remaind;
                    time = time + intToHex(result);
                }
            }else{
                time = time + "80";
                result = remaind/(128);
                remaind = remaind%(128);
                if(result!=0){
                    time = time + intToHexWith8(result);
                    result = remaind;
                    time = time + intToHex(result);
                }else{
                    time = time + "80";
                    result = remaind;
                    time = time + intToHex(result);
                }
            }
        }else{
            result = remaind/(128*128);
            remaind = remaind%(128*128);
            if(result!=0){
                time = time + "8"+result;
                result = remaind/(128);
                remaind = remaind%(128);
                if(result!=0){
                    time = time + intToHexWith8(result);
                    result = remaind;
                    time = time + intToHex(result);
                }else{
                    time = time + "80";
                    result = remaind;
                    time = time + intToHex(result);
                }
            }else{
                result = remaind/(128);
                remaind = remaind%(128);
                if(result!=0){
                    time = time + "8"+result;
                    result = remaind;
                    time = time + intToHex(result);
                }else{
                    result = remaind;
                    time = time + intToHex(result);
                }
            }
        }


       return time;
    }


    public static String intToHex(int n){
        String str=Integer.toHexString(n);
        int l=str.length();
        if(l==1)return "0"+str;
        else return str.substring(l-2,l);
    }


    public static String intToHexWith8(int n){
        String str=Integer.toHexString(n);
        int l=str.length();
        if(l==1)return "8"+str;
        else return intToHex(n+128);
    }


    public static String hexToASCII(String hex){
        int code = Integer.parseInt(hex, 16);
        char result = (char) code;
        return result+"";
    }

    public static String stringHexTOASCII(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }

        return sb.toString();


    }

    public static String toStringHex1(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "iso-8859-1");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
}



