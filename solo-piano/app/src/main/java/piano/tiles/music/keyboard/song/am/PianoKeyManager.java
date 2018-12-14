package piano.tiles.music.keyboard.song.am;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordManager;

/**
 * Created by yanwei on 4/18/16.
 */
public class PianoKeyManager {
    // total = 52 white keys; 36 black keys
    public final static int WHITE_KEY_TOTAL_NUM = 52;
    public final static int BLACK_KEY_TOTAL_NUM = 52;

    public final static String TAG = "PianoKeyManager";

    public final static String[] KEYBOARD_STRING_TYPES = {
            "A B C", "Do Re Mi"
    };

    public final static String[][] WHITE_KEY_NAMES = {{
            "A0", "B0",
            "C1", "D1", "E1", "F1", "G1", "A1", "B1",
            "C2", "D2", "E2", "F2", "G2", "A2", "B2",
            "C3", "D3", "E3", "F3", "G3", "A3", "B3",
            "C4", "D4", "E4", "F4", "G4", "A4", "B4",
            "C5", "D5", "E5", "F5", "G5", "A5", "B5",
            "C6", "D6", "E6", "F6", "G6", "A6", "B6",
            "C7", "D7", "E7", "F7", "G7", "A7", "B7",
            "C8"
            },
            {
            "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do", "re", "mi", "fa", "sol", "la", "si",
            "do"
            }
    };

    public final static int[] WHITE_KEY_SOUNDS = {
            R.raw.aa_1, R.raw.ab_1,
            R.raw.ac0, R.raw.ad0, R.raw.ae0, R.raw.af0, R.raw.ag0, R.raw.aa0, R.raw.ab0,
            R.raw.ac1, R.raw.ad1, R.raw.ae1, R.raw.af1, R.raw.ag1, R.raw.aa1, R.raw.ab1,
            R.raw.ac2, R.raw.ad2, R.raw.ae2, R.raw.af2, R.raw.ag2, R.raw.aa2, R.raw.ab2,
            R.raw.ac3, R.raw.ad3, R.raw.ae3, R.raw.af3, R.raw.ag3, R.raw.aa3, R.raw.ab3,
            R.raw.ac4, R.raw.ad4, R.raw.ae4, R.raw.af4, R.raw.ag4, R.raw.aa4, R.raw.ab4,
            R.raw.ac5, R.raw.ad5, R.raw.ae5, R.raw.af5, R.raw.ag5, R.raw.aa5, R.raw.ab5,
            R.raw.ac6, R.raw.ad6, R.raw.ae6, R.raw.af6, R.raw.ag6, R.raw.aa6, R.raw.ab6,
            R.raw.ac7
    };
    public final static int[] BLACK_KEY_SOUNDS = {
            R.raw.aad_1, -1,
            R.raw.acd0, R.raw.add0, -1, R.raw.afd0, R.raw.agd0, R.raw.aad0, -1,
            R.raw.acd1, R.raw.add1, -1, R.raw.afd1, R.raw.agd1, R.raw.aad1, -1,
            R.raw.acd2, R.raw.add2, -1, R.raw.afd2, R.raw.agd2, R.raw.aad2, -1,
            R.raw.acd3, R.raw.add3, -1, R.raw.afd3, R.raw.agd3, R.raw.aad3, -1,
            R.raw.acd4, R.raw.add4, -1, R.raw.afd4, R.raw.agd4, R.raw.aad4, -1,
            R.raw.acd5, R.raw.add5, -1, R.raw.afd5, R.raw.agd5, R.raw.aad5, -1,
            R.raw.acd6, R.raw.add6, -1, R.raw.afd6, R.raw.agd6, R.raw.aad6, -1,
            -1, -1
    };

    private static int[] white_key_sound_id = new int[WHITE_KEY_TOTAL_NUM];
    private static int[] black_key_sound_id = new int[BLACK_KEY_TOTAL_NUM];

    private static int[] bk_white_key_sound_id = new int[WHITE_KEY_TOTAL_NUM];
    private static int[] bk_black_key_sound_id = new int[BLACK_KEY_TOTAL_NUM];

    private static int keyboard_string_type = 0;

    public static int getKeyboard_from_index() {
        return keyboard_from_index;
    }

    public static void setKeyboard_from_index(int keyboard_from_index) {
        PianoKeyManager.keyboard_from_index = keyboard_from_index;
    }

    private static int keyboard_from_index = 0;

    public static int getKeyboard_string_type() {
        return keyboard_string_type;
    }

    public static void setKeyboard_string_type(int keyboard_string_type) {
        PianoKeyManager.keyboard_string_type = keyboard_string_type;
    }

    private static volatile int load_finished = 0x00;
    public static boolean getLoadFinished()
    {
        return load_finished == 0x3;
    }

    public static SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    private static SoundPool bk_soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

    //public static SoundPool soundPool = new SoundPool.Builder().setMaxStreams(10).build();

    public static CustomProgressDialog mDialog = null;
    public static void startPlayKey(Context context, boolean isWhiteKey, int index)
    {
        //L.v("piano engine", "start play " + isWhiteKey + " " + index);
        if(isWhiteKey) {
            if(getLoadFinished())
            {
                bk_soundPool.play(bk_white_key_sound_id[index], 1F, 1F, 0, 0, 1F);
            }
            else {
//                if (white_key_sound_id[index] == 0) {
//                    L.v("piano engine", "load " + isWhiteKey + " " + index);
//                    white_key_sound_id[index] = soundPool.load(context, WHITE_KEY_SOUNDS[index], 0);
//                }
                soundPool.play(white_key_sound_id[index], 1F, 1F, 0, 0, 1F);
            }
        }
        else
        {
            if(getLoadFinished())
            {
                bk_soundPool.play(bk_black_key_sound_id[index], 1F, 1F, 0, 0, 1F);
            }
            else {
//                if (BLACK_KEY_SOUNDS[index] != -1) {
//                    if (black_key_sound_id[index] == 0) {
//                        L.v("piano engine", "load " + isWhiteKey + " " + index);
//                        black_key_sound_id[index] = soundPool.load(context, BLACK_KEY_SOUNDS[index], 0);
//                    }
//                    soundPool.play(black_key_sound_id[index], 1F, 1F, 0, 0, 1F);
//                }
            }
        }
        if(RecordManager.isRecording())
            RecordManager.addRecord(isWhiteKey, index, 0);
    }

    public static void stopPlayKey(Context context, boolean isWhiteKey, int index)
    {
        //L.v("piano engine", "stop play " + isWhiteKey + " " + index);
        if(getLoadFinished())
        {
            if (isWhiteKey)
                bk_soundPool.stop(bk_white_key_sound_id[index]);
            else
                bk_soundPool.stop(bk_black_key_sound_id[index]);
        }
        else {
            if (isWhiteKey)
                soundPool.stop(white_key_sound_id[index]);
            else
                soundPool.stop(black_key_sound_id[index]);
        }
        if(RecordManager.isRecording())
            RecordManager.addRecord(isWhiteKey, index, 1);
    }





    /**
     *
     * @param context
     * @param isUpKeyPosinton 双键位置,上还是下,true:上,false:下
     * @param isWhiteKey
     * @param index
     */
    public static void startPlayKey(Context context, boolean isUpKeyPosinton, boolean isWhiteKey, int index){
        if (isUpKeyPosinton){//上键盘

            if(isWhiteKey) {
                if(getLoadFinished())
                {
                    bk_soundPool.play(bk_white_key_sound_id[index], 1F, 1F, 0, 0, 1F);
                }
                else {
                    if (white_key_sound_id[index] == 0) {
                        L.v("piano engine", "load " + isWhiteKey + " " + index);
                        white_key_sound_id[index] = soundPool.load(context, WHITE_KEY_SOUNDS[index], 0);
                    }
                    soundPool.play(white_key_sound_id[index], 1F, 1F, 0, 0, 1F);
                }
            } else {
                if(getLoadFinished()) {
                    bk_soundPool.play(bk_black_key_sound_id[index], 1F, 1F, 0, 0, 1F);
                } else {
                    if (BLACK_KEY_SOUNDS[index] != -1) {
                        if (black_key_sound_id[index] == 0) {
                            L.v("piano engine", "load " + isWhiteKey + " " + index);
                            black_key_sound_id[index] = soundPool.load(context, BLACK_KEY_SOUNDS[index], 0);
                        }
                        soundPool.play(black_key_sound_id[index], 1F, 1F, 0, 0, 1F);
                    }
                }
            }
            if(RecordManager.isRecording())
                RecordManager.addRecord(isUpKeyPosinton, isWhiteKey, index, 0);

        }else {//下键盘

            if(isWhiteKey) {
                if(getLoadFinished()) {
                    bk_soundPool.play(bk_white_key_sound_id[index], 1F, 1F, 0, 0, 1F);
                } else {
                    if (white_key_sound_id[index] == 0) {
                        L.v("piano engine", "load " + isWhiteKey + " " + index);
                        white_key_sound_id[index] = soundPool.load(context, WHITE_KEY_SOUNDS[index], 0);
                    }
                    soundPool.play(white_key_sound_id[index], 1F, 1F, 0, 0, 1F);
                }
            } else {
                if(getLoadFinished()) {
                    bk_soundPool.play(bk_black_key_sound_id[index], 1F, 1F, 0, 0, 1F);
                } else {
                    if (BLACK_KEY_SOUNDS[index] != -1) {
                        if (black_key_sound_id[index] == 0) {
                            L.v("piano engine", "load " + isWhiteKey + " " + index);
                            black_key_sound_id[index] = soundPool.load(context, BLACK_KEY_SOUNDS[index], 0);
                        }
                        soundPool.play(black_key_sound_id[index], 1F, 1F, 0, 0, 1F);
                    }
                }
            }
            if(RecordManager.isRecording())
                RecordManager.addRecord(isUpKeyPosinton, isWhiteKey, index, 0);
        }
//
//        if(RecordManager.isRecording())
//                RecordManager.addRecord(isUpKeyPosinton, isWhiteKey, index, 0);
    }

    /**
     *
     * @param context
     * @param isUpKeyPosinton 双键位置,上还是下,true:上,false:下
     * @param isWhiteKey
     * @param index
     */
    public static void stopPlayKey(Context context, boolean isUpKeyPosinton, boolean isWhiteKey, int index){
        if (isUpKeyPosinton) {//上键盘

            if(getLoadFinished()) {
                if (isWhiteKey)
                    bk_soundPool.stop(bk_white_key_sound_id[index]);
                else
                    bk_soundPool.stop(bk_black_key_sound_id[index]);
            } else {
                if (isWhiteKey)
                    soundPool.stop(white_key_sound_id[index]);
                else
                    soundPool.stop(black_key_sound_id[index]);
            }
            if(RecordManager.isRecording())
//                RecordManager.addRecord(isWhiteKey, index, 1);
            RecordManager.addRecord(isUpKeyPosinton, isWhiteKey, index, 1);

        }else {//下键盘

            if(getLoadFinished()) {
                if (isWhiteKey)
                    bk_soundPool.stop(bk_white_key_sound_id[index]);
                else
                    bk_soundPool.stop(bk_black_key_sound_id[index]);
            } else {
                if (isWhiteKey)
                    soundPool.stop(white_key_sound_id[index]);
                else
                    soundPool.stop(black_key_sound_id[index]);
            }
            if(RecordManager.isRecording())
//                RecordManager.addRecord(isWhiteKey, index, 1);
            RecordManager.addRecord(isUpKeyPosinton, isWhiteKey, index, 1);
        }

//        if(RecordManager.isRecording())
//        RecordManager.addRecord(isUpKeyPosinton, isWhiteKey, index, 1);
    }










    public static void startInitAllKeys(Context context)
    {
        startInitAllKeys(context,0);
    }

    public static void startInitAllKeys(Context context,int priorityIndex)
    {
        PlayKeyLoaderTask loaderTask = new PlayKeyLoaderTask(context, true);
        loaderTask.execute(priorityIndex);

//        PlayKeyLoaderTask loaderBlackTask = new PlayKeyLoaderTask(context, false);
//        loaderBlackTask.execute(priorityIndex);
    }

    public static void startInitAllKeys(Context context, int priorityIndex, boolean isShow) {
        PlayKeyLoaderTask loaderTask = new PlayKeyLoaderTask(context, true, isShow);
        loaderTask.execute(priorityIndex);

//        PlayKeyLoaderTask loaderBlackTask = new PlayKeyLoaderTask(context, false);
//        loaderBlackTask.execute(priorityIndex);
    }

    public static void loadSettings(Context context)
    {
        SharedPreferences mySharedPreferences= context.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        setKeyboard_string_type(mySharedPreferences.getInt("kb_type", 0));
        setKeyboard_from_index(mySharedPreferences.getInt("kb_fromindex", 28));
    }

    public static void saveSettings(Context context)
    {
        SharedPreferences mySharedPreferences= context.getSharedPreferences(TAG, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("kb_type", getKeyboard_string_type());
        editor.putInt("kb_fromindex", getKeyboard_from_index());
        editor.commit();
    }

    static class PlayKeyLoaderTask extends AsyncTask<Integer, Integer, Boolean>
    {
        Context mContext = null;
        boolean mIsWhite;
        static boolean mIsShow = true;
        int loaded_num = 0;
       // CustomProgressDialog dialog = null;

        PlayKeyLoaderTask(Context context, boolean isWhite)
        {
            mContext = context;
            mIsWhite = isWhite;
        }

        PlayKeyLoaderTask(Context context, boolean isWhite, boolean isShow) {
            mContext = context;
            mIsWhite = isWhite;
            mIsShow = isShow;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!((Activity)mContext).isFinishing()){
                mDialog = CustomProgressDialog.createDialog(mContext);
                mDialog.setTitile("");
                mDialog.setText("0%");
                mDialog.setCancelable(false);
                if (mIsShow) {
                    mDialog.show();
                }
            }
            L.v("piano engine", "start init..." + mIsWhite);
//            dlg = ProgressDialog.show(mContext, "loading...", "", true, false);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            L.v("piano engine", "finished init..." + mIsWhite);
//            if(mIsWhite)
//                load_finished |= 0x1;
//            else
//                load_finished |= 0x2;
//            load_finished = 0x03;
//            dlg.cancel();
            try {
                if (!((Activity)mContext).isFinishing() && mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }catch (Exception e){
                e.printStackTrace();
                Crashlytics.logException(e);
                Crashlytics.setBool("isActivityFinish",((Activity)mContext).isFinishing());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            dlg.setMessage("" + loaded_num + " files");
            if(mDialog != null && mDialog.isShowing())
                mDialog.setText(String.valueOf(values[0]) + "%");
        }

        public static void setmIsShow(boolean isShow) {
            mIsShow = isShow;
            if (mIsShow && mDialog != null && !mDialog.isShowing()) {
                mDialog.show();
            }

        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            //android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
//            loaded_num = 0;

//            int endIndex = params[0] + 10;
//            if(endIndex > WHITE_KEY_SOUNDS.length)
//                endIndex = WHITE_KEY_SOUNDS.length;
//
//            for(int startIndex = params[0]; startIndex < endIndex; startIndex++) {
//                if(white_key_sound_id[startIndex] == 0)
//                {
//                    white_key_sound_id[startIndex] = soundPool.load(mContext, WHITE_KEY_SOUNDS[startIndex], 1);
//                    loaded_num++;
//                }
//                if(black_key_sound_id[startIndex] == 0 && BLACK_KEY_SOUNDS[startIndex] != -1)
//                {
//                    black_key_sound_id[startIndex] = soundPool.load(mContext, BLACK_KEY_SOUNDS[startIndex], 1);
//                    loaded_num++;
//                }
//                publishProgress(null);
//            }
//            L.v("piano engine", "priority queue finished " + mIsWhite);
//            for(int startIndex = 0; startIndex < WHITE_KEY_SOUNDS.length; startIndex++) {
//                if(white_key_sound_id[startIndex] == 0)
//                {
//                    white_key_sound_id[startIndex] = soundPool.load(mContext, WHITE_KEY_SOUNDS[startIndex], 1);
//                    loaded_num++;
//                }
//                if(black_key_sound_id[startIndex] == 0 && BLACK_KEY_SOUNDS[startIndex] != -1)
//                {
//                    black_key_sound_id[startIndex] = soundPool.load(mContext, BLACK_KEY_SOUNDS[startIndex], 1);
//                    loaded_num++;
//                }
//                publishProgress(null);
//            }
//            Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);

                  for (int startIndex = 0; startIndex < WHITE_KEY_SOUNDS.length; startIndex++) {
                        if (bk_white_key_sound_id[startIndex] == 0) {
                            bk_white_key_sound_id[startIndex] = bk_soundPool.load(mContext, WHITE_KEY_SOUNDS[startIndex], 1);
                           //loaded_num++;
                       }
                        if (bk_black_key_sound_id[startIndex] == 0 && BLACK_KEY_SOUNDS[startIndex] != -1) {
                            bk_black_key_sound_id[startIndex] = bk_soundPool.load(mContext, BLACK_KEY_SOUNDS[startIndex], 1);
                           //loaded_num++;
                        }
                      publishProgress((startIndex-1) * 2);
                    }

                   load_finished = 0x03;
//                    L.v("piano engine", "all files loaded ...");
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
//
//                    for (int startIndex = 0; startIndex < WHITE_KEY_SOUNDS.length; startIndex++) {
//                        if (bk_white_key_sound_id[startIndex] == 0) {
//                            bk_white_key_sound_id[startIndex] = bk_soundPool.load(mContext, WHITE_KEY_SOUNDS[startIndex], 1);
//                            //loaded_num++;
//                        }
//                        if (bk_black_key_sound_id[startIndex] == 0 && BLACK_KEY_SOUNDS[startIndex] != -1) {
//                            bk_black_key_sound_id[startIndex] = bk_soundPool.load(mContext, BLACK_KEY_SOUNDS[startIndex], 1);
//                            //loaded_num++;
//                        }
//                    }
//
//                    load_finished = 0x03;
//                    L.v("piano engine", "all files loaded ...");
//                }
//                });
//            thread.start();

            return null;
            }

    }
}
