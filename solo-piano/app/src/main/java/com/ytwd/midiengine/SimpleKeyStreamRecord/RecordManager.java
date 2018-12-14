package com.ytwd.midiengine.SimpleKeyStreamRecord;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ytwd.midiengine.MidiHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import piano.tiles.music.keyboard.song.am.L;
import piano.tiles.music.keyboard.song.am.PianoActivity;
import piano.tiles.music.keyboard.song.am.R;
import piano.tiles.music.keyboard.song.am.SaveFileDialog;

/**
 * Created by yanwei on 4/19/16.
 */
public class RecordManager {
    private static Date date = null;
    private static ArrayList<RecordItem> array_record_items = new ArrayList<RecordItem>();
    private static Context context;
    private static long last_item_time;
    private static boolean recording = false;
    private static boolean replay = false;

    private static final String TAG = "RecordManager";

    public static boolean isRecording() {
        return recording;
    }

    public static boolean isReplay() {
        return replay;
    }

    public static void startRecord(Context ct)
    {
        L.v(TAG, "startRecord");
       array_record_items.clear();
        context = ct;
        last_item_time = new Date().getTime();
        recording = true;
    }

    public static void addRecord(RecordItem item)
    {
        if(item != null)
            array_record_items.add(item);
    }

    public static void addRecord(boolean is_white_key, int key_index, int key_action)
    {
        RecordItem item = new RecordItem();
        item.setPiano_type(0);
        item.setPiano_key_is_white(is_white_key);
        item.setPiano_key_index(key_index);
        item.setPiano_key_action(key_action);

        long now = new Date().getTime();
        item.setPiano_key_time(now - last_item_time);
        last_item_time = now;

        array_record_items.add(item);
    }





    /**
     *
     * @param isUpKeyPosinton 双键位置,上还是下,true:上,false:下
     * @param is_white_key
     * @param key_index
     * @param key_action
     */
    public static void addRecord(boolean isUpKeyPosinton, boolean is_white_key, int key_index, int key_action) {
        RecordItem item = new RecordItem();
        item.setPiano_position(isUpKeyPosinton);
        item.setPiano_type(0);
        item.setPiano_key_is_white(is_white_key);
        item.setPiano_key_index(key_index);
        item.setPiano_key_action(key_action);

        long now = new Date().getTime();
        item.setPiano_key_time(now - last_item_time);
        last_item_time = now;

        array_record_items.add(item);
    }








    public static void stopRecord()
    {
        L.v(TAG, "stopRecord");
        recording = false;

        // TODO: save these info into file.
        final SaveFileDialog dlg = new SaveFileDialog(context);
        dlg.setContentView(R.layout.save_file_dialog);
        final EditText edFileName = (EditText)dlg.findViewById(R.id.etFileName);
        Button btnSave = (Button)dlg.findViewById(R.id.btnSave);
        Button btnCancel = (Button)dlg.findViewById(R.id.btnCancel);

        edFileName.setText("R_" + getDateFormatString() + ".midi");
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        L.v("file",array_record_items.size()+"++++++++++array_record_items size");
                        File file = new File(getRecordDirectory(context), edFileName.getText().toString());
                        MidiHelper midiHelper = new MidiHelper();
                        midiHelper.writeMidi(file,array_record_items);
                        L.v("file","+++++++++++++++++finish!!");
                    }
                });
                thread.start();
//                try {
//                    FileOutputStream fileOutputStream = new FileOutputStream(file);
//                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//                    objectOutputStream.writeObject(array_record_items);
//                    objectOutputStream.close();
//                    fileOutputStream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                dlg.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        dlg.show();
    }

    public static void loadRecord(String path)
    {
        File file = new File(path);
        if(path.endsWith(".dat")){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                array_record_items.clear();
                array_record_items = (ArrayList<RecordItem>) objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {

                MidiHelper midiHelper = new MidiHelper();
                array_record_items.clear();
                array_record_items = midiHelper.getSoundList(file);
                L.v("file",array_record_items.size()+"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static String getDateFormatString()
    {
        return new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
    }
    public static String getRecordDirectory(Context cxt)
    {
        File file = Environment.getExternalStorageDirectory();
        if(file.exists())
        {
            File sub = new File(file, "PianoFiles");
            if(sub.exists() || sub.isDirectory() || sub.mkdirs())
                return sub.getPath();
        }

        if(cxt != null)
            return cxt.getFilesDir().getPath();//#10 RecordManager.getRecordDirectory 可能出现空指针

        return Environment.getDataDirectory().getPath();
    }

    public static FilenameFilter recordFileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            if(filename != null && (filename.endsWith(".dat")||filename.endsWith(".midi")||filename.endsWith(".mid")))
                return true;
            return false;
        }
    };

//    public static class MyRunnable implements Runnable{
//        @Override
//        public void run() {
//            if(recordEvent != null)
//            {
//                L.v("midi","runnable_begin_time"+new Date().getTime());
//                if(current_record_index < array_record_items.size())
//                    recordEvent.RecordEventComing(array_record_items.get(current_record_index));
//                current_record_index++;
//                    //end_time = new Date().getTime();
//                if(current_record_index < array_record_items.size())
//                {
//                    long next_offset = array_record_items.get(current_record_index).getPiano_key_time();
//                    L.v("midi","delay_time"+next_offset+"       now_time"+new Date().getTime());
//                    //start_time = new Date().getTime();
//                   // handler.postDelayed(new MyRunnable(), next_offset);
//                    handler.sendMessageDelayed();
//                }
//                else
//                {
//                    // TODO: null is the end of record.
//                    recordEvent.RecordEventComing(null);
//                }
//            }
//        }
//    }
    private static android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            // do something

            L.v("midi","begin_time---"+new Date().getTime());

            try {
//                Message message1 = mPlayHandler.obtainMessage(0);
//                message1.arg1 = current_record_index;
//                mPlayHandler.sendMessageDelayed(message1, 0);

                start_time = new Date().getTime();
                L.v("midi", "send------time" + ((start_time - end_time) > 10 ? (start_time - end_time) + "+++++++++" : (start_time - end_time)));
                if (recordEvent != null) {
                    if (current_record_index < array_record_items.size())
                        recordEvent.RecordEventComing(array_record_items.get(current_record_index));
                    current_record_index++;
                    //end_time = new Date().getTime();
                    if (current_record_index < array_record_items.size()) {
                        long next_offset = array_record_items.get(current_record_index).getPiano_key_time();

                        //start_time = new Date().getTime();
                        // handler.postDelayed(new MyRunnable(), next_offset);

                        Message message2 = handler.obtainMessage(0);
                        sendMessageDelayed(message2, next_offset);
                        L.v("midi", "ui------time" + ((new Date().getTime() - start_time) > 10 ? (new Date().getTime() - start_time) + "+++++++++" : (new Date().getTime() - start_time)));
                        //L.v("midi","time++offset"+(new Date().getTime()+next_offset));
                        end_time = new Date().getTime() + next_offset;
                    } else {
                        // TODO: null is the end of record.
                        recordEvent.RecordEventComing(null);
                    }
                }
            }catch (Exception e){
                L.v("midi","++++++"+e.toString());
            }
        }
    };



    private static int current_record_index = 0;
    private static RecordEvent recordEvent = null;
    private static long end_time = 0;
    private static long start_time = 0;
    private static PianoActivity.PlayHandler mPlayHandler;

    public static void startReplay(Context ct, PianoActivity.PlayHandler playHandler , RecordEvent event)
    {
        L.v(TAG, "startReplay");
        context = ct;
        recordEvent = event;
        current_record_index = 0;
        replay = true;
        mPlayHandler = playHandler;



        Message message1 = handler.obtainMessage(0);
        if(array_record_items.size() > 0)
        {
            long next_offset = array_record_items.get(current_record_index).getPiano_key_time();



           // handler.postDelayed(new MyRunnable(), next_offset);
            handler.sendMessageDelayed(message1,next_offset);
        }
        else
           // handler.postDelayed(new MyRunnable(), 500);
            handler.sendMessageDelayed(message1,500);
    }
    public static void stopReplay()
    {
        L.v(TAG, "stopReplay");
        replay = false;
        recordEvent = null;
    }


    public static void setArray(ArrayList<RecordItem> sound_list){

        array_record_items = sound_list;
    }



}
