package piano.tiles.music.keyboard.song.am;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.facebook.share.widget.ShareDialog;
import com.flurry.android.FlurryAgent;
import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordEvent;
import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordItem;
import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordManager;
import com.ytwd.midiengine.utils.AlarmUtils;
import com.ytwd.midiengine.utils.BadgeManager;
import com.ytwd.midiengine.utils.SavePreferencesUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by jiatao on 16/5/11.
 * 钢琴的主界面
 */
public class PianoActivity extends Activity implements AdListener, View.OnClickListener {
    public static final String TAG = "PianoActivity";
    public static final int REQUSET = 1;
    public static final String TAG_AD_VIEW = "adView";

    private SoundKey[] whiteKeys;//白色按键
    private SoundKey[] blackKeys;//黑色按键

    private SoundKey[] whiteKeys2;//白色按键
    private SoundKey[] blackKeys2;//黑色按键

    private View[] fistViews;
    private View[] secondViews;

    private LinearLayout fistDesktop;
    private LinearLayout fistDesktopBottom;//第一个菜单栏
    private ImageView ivReplay;//播放录制的音乐按钮
    private ImageView ivStartRecord;//录制音乐按钮
    private ImageView ivLoadFile;//展示录制好的音乐按钮
    private ImageView ivSelectOption;//双排单排键转换按钮
    private ImageView ivSetting;//设置
    private ImageView ivLeftPage;//左移键盘
    private ImageView ivRightPage;//右移键盘
    private ImageView ivControlHide;
    private AllKeysScrollBar scrollBar;
    private SwitchButton switchButton;//键盘滑动控制按钮
    private FrameLayout fistWhiteKeys;//第一排钢琴键盘
    private int mCurrentPageIndex = 0;

    private LinearLayout secondDesktopBottom;//第二个菜单栏
    private ImageView ivLeftPage2;//左移键盘
    private ImageView ivRightPage2;//右移键盘
    private AllKeysScrollBar scrollBar2;
    private SwitchButton switchButton2;//键盘滑动控制按钮
    private FrameLayout fistWhiteKeys2;//第二排钢琴键盘
    private int mCurrentPageIndex2 = 0;

    private LinearLayout fistKeyLinearLayout;
    private LinearLayout secondKeyLinearLayout;
    private ImageView tv_fist_piano_keyboard_desktop_top_title;


    private boolean mRecording = false;//是否正在录制
    private boolean mReplaying = false;//是否正在播放
    private int SCROLL_STATE = 0;//按键是的滑动状态,0表示DOWN,1表示MOWE,2表示UP
    private int SCROLL_STATE2 = 0;//按键是的滑动状态,0表示DOWN,1表示MOWE,2表示UP
    private int UP_OR_DOWN = 0;//0表示按下,1表示抬起
    private int MOVE_BUTTON_POSINTION = 3;//0表示左边点击滑动按钮,1表示右边点击滑动按钮,3表示没有点击任何按钮

    public static boolean isSingleKey = true;//false表示单排键,true表示双排键
    public static boolean isDoubleKeyboardConnection = false;//双键盘没有关联,true表示有关联,false表示没有关联

    private HorizontalScrollView horizontalScrollView;

    private float x1 = 0;//记录down时的位置
    private float x2 = 0;//记录move时的位置
    private int a = 0;

    public static int keyNums = 10;//按键的个数,初始化10个
    public float pelKeyLength;//每个按键的长度

    /**
     * 屏幕的宽度
     */
    private int mScreenWitdh;
    /**
     * 屏幕高度
     */
    private int mScreenHeight;

    private boolean isHideControl;
    ArrayList<RecordItem> sound_list;
    private PlayHandler playHandler;
    private boolean isFist = false;//是否是第一次安装启动
    private LinearLayout llPianoRecordTip;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.v("Piano", "onCreate");

        /** 设置全屏*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.piano_main);

        initAd();
//        initDeskShortcut();

        BadgeManager.clearBadge(this);

        PianoKeyManager.loadSettings(this);
        mCurrentPageIndex = PianoKeyManager.getKeyboard_from_index();
        //PianoKeyManager.startInitAllKeys(this, mCurrentPageIndex);

        mCurrentPageIndex2 = mCurrentPageIndex + Integer.parseInt(SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(SettingActivity.KEYBOARD_NUM, "10"));
        L.v("piano", "mCurrentPageIndex2:" + mCurrentPageIndex2 + ";mCurrentPageIndex;" + mCurrentPageIndex);
//        mCurrentPageIndex2 = PianoKeyManager.getKeyboard_from_index();

        //PianoKeyManager.startInitAllKeys(this, mCurrentPageIndex2);
        isHideControl = SavePreferencesUtils.getInstance(getApplicationContext()).sp.getBoolean(SettingActivity.IS_CONTROL_HIDE_RADION_GROUP, false);
        /**读取屏幕尺寸*/
        readScreen();
        /** 获取viewid */
        findViews();
        /** 监听事件 */
        listeners();
        initDeskShortcut();
        /**初始化数据*/
        initDatas();

        refreshAllKeys(mCurrentPageIndex);

        refreshAllKeys2(mCurrentPageIndex2);


        PianoKeyManager.startInitAllKeys(PianoActivity.this);//#9 PianoKeyManager.java line 365 PlayKeyLoaderTask.onPostExecute中的 dialog.dismiss() PianoKeyManager.startInitAllKeys(this)改为PianoKeyManager.startInitAllKeys(PianoActivity.this)

        shareDialog = new ShareDialog(this);

        long offset = AlarmUtils.getLastAccessTime(this);
        if (offset > 0) {
            offset = System.currentTimeMillis() - offset;
            if (offset > (60 * 60 * 24 * 3 - 100) * 1000) {
                L.v("Tag", "broadcast app launch of badgenumber");
                FlurryAgent.logEvent(Constants.ACT_ALARM_LAUNCH_OF_BADGENUMBER);
            }
        }
        AlarmUtils.setLastAccessTimeNow(this);
        AlarmUtils.startScheduleAfterDays(this, 3);

        promoteMusicPiano();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    switchButton2.setChecked(true);
                    break;
                case 1:
                    switchButton2.setChecked(false);
                    break;
                case 2:
                    switchButton.setChecked(true);
                    break;
                case 3:
                    switchButton.setChecked(false);
                    break;

                case 4:
                    replay();
                    break;

                case AD_INTERPOLATE:
                    //change ad

                    if (adContainer == null) {
                        adContainer = (LinearLayout) findViewById(R.id.piano_main_ad_container);
                    }
                    if (InterPolateView == null) {
                        InterPolateView = (LinearLayout) LayoutInflater.from(PianoActivity.this).inflate(R.layout.ad_interpolate, adContainer, false);
                        InterPolateView.setOnClickListener(PianoActivity.this);
                    }
                    if (adView == null) {
                        adView = (RelativeLayout) LayoutInflater.from(PianoActivity.this).inflate(R.layout.ad_start, adContainer, false);
                    }
                    adContainer.removeAllViews();
                    if (adContainer.getTag().equals(TAG_AD_VIEW)) {
                        adContainer.addView(InterPolateView);
                        adContainer.setTag("");
                    } else {
                        adContainer.addView(adView);
                        adContainer.setTag(TAG_AD_VIEW);
                    }

                    break;
                default:
                    break;


            }

        }
    };

    /**
     * 初始化view
     */
    private void findViews() {
        /** 第一批按键 */
        /** 初始化白色按键 */
        whiteKeys = new SoundKey[18];
        whiteKeys[0] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_01);
        whiteKeys[1] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_02);
        whiteKeys[2] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_03);
        whiteKeys[3] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_04);
        whiteKeys[4] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_05);
        whiteKeys[5] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_06);
        whiteKeys[6] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_07);
        whiteKeys[7] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_08);
        whiteKeys[8] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_09);
        whiteKeys[9] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_10);
        whiteKeys[10] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_11);
        whiteKeys[11] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_12);
        whiteKeys[12] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_13);
        whiteKeys[13] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_14);
        whiteKeys[14] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_15);
        whiteKeys[15] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_16);
        whiteKeys[16] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_17);
        whiteKeys[17] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_white_keys_18);
        /** 初始化黑色按键 */
        blackKeys = new SoundKey[17];
        blackKeys[0] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_01);
        blackKeys[1] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_02);
        blackKeys[2] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_03);
        blackKeys[3] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_04);
        blackKeys[4] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_05);
        blackKeys[5] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_06);
        blackKeys[6] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_07);
        blackKeys[7] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_08);
        blackKeys[8] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_09);
        blackKeys[9] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_10);
        blackKeys[10] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_11);
        blackKeys[11] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_12);
        blackKeys[12] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_13);
        blackKeys[13] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_14);
        blackKeys[14] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_15);
        blackKeys[15] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_16);
        blackKeys[16] = (SoundKey) findViewById(R.id.bt_fist_piano_keyboard_black_keys_17);
        /** 钢琴桌面布局 */
        fistDesktop = (LinearLayout) findViewById(R.id.ly_fist_piano_keyboard_desktop);
        fistDesktopBottom = (LinearLayout) findViewById(R.id.ly_fist_piano_keyboard_desktop_bottom);
        ivReplay = (ImageView) findViewById(R.id.iv_fist_piano_keyboard_desktop_top_record_play);
        ivStartRecord = (ImageView) findViewById(R.id.iv_fist_piano_keyboard_desktop_top_record);
        ivLoadFile = (ImageView) findViewById(R.id.iv_fist_piano_keyboard_desktop_top_music_list);
        ivSelectOption = (ImageView) findViewById(R.id.iv_fist_piano_keyboard_desktop_top_switch);
        ivSetting = (ImageView) findViewById(R.id.iv_fist_piano_keyboard_desktop_top_setting);
        ivControlHide = (ImageView) findViewById(R.id.iv_fist_piano_keyboard_desktop_top_control);


        ivLeftPage = (ImageView) findViewById(R.id.iv_fist_piano_keyboard_desktop_bottom_to_left);
        ivRightPage = (ImageView) findViewById(R.id.iv_fist_piano_keyboard_desktop_bottom_to_right);
        scrollBar = (AllKeysScrollBar) findViewById(R.id.iv_fist_piano_keyboard_desktop_bottom_keys_scrollbar);
        switchButton = (SwitchButton) findViewById(R.id.iv_fist_piano_keyboard_desktop_bottom_switch_button);
        fistWhiteKeys = (FrameLayout) findViewById(R.id.fy_fist_piano_keyboard_white_keys);

        /** 第二排按键 */
        whiteKeys2 = new SoundKey[18];
        whiteKeys2[0] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_01);
        whiteKeys2[1] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_02);
        whiteKeys2[2] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_03);
        whiteKeys2[3] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_04);
        whiteKeys2[4] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_05);
        whiteKeys2[5] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_06);
        whiteKeys2[6] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_07);
        whiteKeys2[7] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_08);
        whiteKeys2[8] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_09);
        whiteKeys2[9] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_10);
        whiteKeys2[10] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_11);
        whiteKeys2[11] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_12);
        whiteKeys2[12] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_13);
        whiteKeys2[13] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_14);
        whiteKeys2[14] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_15);
        whiteKeys2[15] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_16);
        whiteKeys2[16] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_17);
        whiteKeys2[17] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_white_keys_18);
        /** 初始化黑色按键 */
        blackKeys2 = new SoundKey[17];
        blackKeys2[0] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_01);
        blackKeys2[1] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_02);
        blackKeys2[2] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_03);
        blackKeys2[3] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_04);
        blackKeys2[4] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_05);
        blackKeys2[5] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_06);
        blackKeys2[6] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_07);
        blackKeys2[7] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_08);
        blackKeys2[8] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_09);
        blackKeys2[9] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_10);
        blackKeys2[10] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_11);
        blackKeys2[11] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_12);
        blackKeys2[12] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_13);
        blackKeys2[13] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_14);
        blackKeys2[14] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_15);
        blackKeys2[15] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_16);
        blackKeys2[16] = (SoundKey) findViewById(R.id.bt_second_piano_keyboard_black_keys_17);

        secondDesktopBottom = (LinearLayout) findViewById(R.id.ly_second_piano_keyboard_desktop);
        ivLeftPage2 = (ImageView) findViewById(R.id.iv_second_piano_keyboard_desktop_to_left);
        ivRightPage2 = (ImageView) findViewById(R.id.iv_second_piano_keyboard_desktop_to_right);
        scrollBar2 = (AllKeysScrollBar) findViewById(R.id.iv_second_piano_keyboard_desktop_keys_scrollbar);
        switchButton2 = (SwitchButton) findViewById(R.id.iv_second_piano_keyboard_desktop_bottom_switch_button);
        fistWhiteKeys2 = (FrameLayout) findViewById(R.id.fy_second_piano_keyboard_white_keys);

        fistKeyLinearLayout = (LinearLayout) findViewById(R.id.ly_fist_piano_keyboard);
        secondKeyLinearLayout = (LinearLayout) findViewById(R.id.ly_second_piano_keyboard);

        fistViews = new View[18];
        fistViews[0] = findViewById(R.id.ly_fist_piano_view01);
        fistViews[1] = findViewById(R.id.ly_fist_piano_view02);
        fistViews[2] = findViewById(R.id.ly_fist_piano_view03);
        fistViews[3] = findViewById(R.id.ly_fist_piano_view04);
        fistViews[4] = findViewById(R.id.ly_fist_piano_view05);
        fistViews[5] = findViewById(R.id.ly_fist_piano_view06);
        fistViews[6] = findViewById(R.id.ly_fist_piano_view07);
        fistViews[7] = findViewById(R.id.ly_fist_piano_view08);
        fistViews[8] = findViewById(R.id.ly_fist_piano_view09);
        fistViews[9] = findViewById(R.id.ly_fist_piano_view10);
        fistViews[10] = findViewById(R.id.ly_fist_piano_view11);
        fistViews[11] = findViewById(R.id.ly_fist_piano_view12);
        fistViews[12] = findViewById(R.id.ly_fist_piano_view13);
        fistViews[13] = findViewById(R.id.ly_fist_piano_view14);
        fistViews[14] = findViewById(R.id.ly_fist_piano_view15);
        fistViews[15] = findViewById(R.id.ly_fist_piano_view16);
        fistViews[16] = findViewById(R.id.ly_fist_piano_view17);
        fistViews[17] = findViewById(R.id.ly_fist_piano_view18);

        secondViews = new View[18];
        secondViews[0] = findViewById(R.id.ly_second_piano_view01);
        secondViews[1] = findViewById(R.id.ly_second_piano_view02);
        secondViews[2] = findViewById(R.id.ly_second_piano_view03);
        secondViews[3] = findViewById(R.id.ly_second_piano_view04);
        secondViews[4] = findViewById(R.id.ly_second_piano_view05);
        secondViews[5] = findViewById(R.id.ly_second_piano_view06);
        secondViews[6] = findViewById(R.id.ly_second_piano_view07);
        secondViews[7] = findViewById(R.id.ly_second_piano_view08);
        secondViews[8] = findViewById(R.id.ly_second_piano_view09);
        secondViews[9] = findViewById(R.id.ly_second_piano_view10);
        secondViews[10] = findViewById(R.id.ly_second_piano_view11);
        secondViews[11] = findViewById(R.id.ly_second_piano_view12);
        secondViews[12] = findViewById(R.id.ly_second_piano_view13);
        secondViews[13] = findViewById(R.id.ly_second_piano_view14);
        secondViews[14] = findViewById(R.id.ly_second_piano_view15);
        secondViews[15] = findViewById(R.id.ly_second_piano_view16);
        secondViews[16] = findViewById(R.id.ly_second_piano_view17);
        secondViews[17] = findViewById(R.id.ly_second_piano_view18);

        llPianoRecordTip = (LinearLayout) findViewById(R.id.ly_piano_record_operate_tip);
    }

    /**
     * 监听事件
     */
    private void listeners() {


        if (SavePreferencesUtils.getInstance(getApplicationContext()).sp.getBoolean("isfrist", true)) {
            L.v("piano", "-----===========--fistKeyLinearLayout--------");
            llPianoRecordTip.setVisibility(View.VISIBLE);
        }


        /**
         * 录制音乐监听
         */
        ivStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PianoKeyManager.getLoadFinished()) {
                    showNotReadyToast();
                    return;
                }
                if (!mRecording) {
                    RecordManager.startRecord(PianoActivity.this);
                    mRecording = true;
                    //ivStartRecord.setBackgroundResource(R.drawable.stop);
                    ivStartRecord.setImageResource(R.drawable.piano_record_stop);
                    ivReplay.setImageResource(R.drawable.piano_record_unplay);
                    ivReplay.setEnabled(false);
                    FlurryAgent.logEvent(Constants.ACT_PIANO_RECORD);
                } else {
                    RecordManager.stopRecord();
                    mRecording = false;
                    ivStartRecord.setImageResource(R.drawable.piano_record);
                    ivReplay.setImageResource(R.drawable.piano_record_play);
                    ivReplay.setEnabled(true);
                }

            }
        });


/****************************************************************************/
        ivReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PianoKeyManager.getLoadFinished()) {
                    showNotReadyToast();
                    return;
                }
//                RecordManager.setArray(sound_list);
//
//                new Thread() {
//                    public void run() {
//
//                        Looper.prepare();
//                        playHandler = new PlayHandler(Looper.myLooper());
//                        mHandler.sendEmptyMessage(4);
//                        Looper.loop();
//
//                    }
//                }.start();


                replay();

//                                    if(item.isPiano_key_is_white()){//白键
//                                        if(item.getPiano_key_index() < mCurrentPageIndex
//                                                || item.getPiano_key_index() >= mCurrentPageIndex + 10) {
//                                            mCurrentPageIndex = item.getPiano_key_index();
//                                            mCurrentPageIndex = scrollBar.setProgress(mCurrentPageIndex);
//                                            L.v("piano","_______播放_______" + mCurrentPageIndex);
//                                            refreshAllKeys(mCurrentPageIndex);
//                                        }//#13
//                                        whiteKeys[item.getPiano_key_index() - mCurrentPageIndex].setPressingStatus(item.getPiano_key_action() == 0 ? true : false, true);
//                                    } else {//黑键
//                                        if(item.getPiano_key_index() < mCurrentPageIndex
//                                                || item.getPiano_key_index() >= mCurrentPageIndex + 9) {
//                                            mCurrentPageIndex = item.getPiano_key_index();
//                                            mCurrentPageIndex = scrollBar.setProgress(mCurrentPageIndex);
//                                            refreshAllKeys(mCurrentPageIndex);
//                                        }//#12
//                                        blackKeys[item.getPiano_key_index() - mCurrentPageIndex].setPressingStatus(item.getPiano_key_action() == 0 ? true : false, true);
//                                    }


            }

        });


        /**
         * 获取录制的音乐列表监听
         */
        ivLoadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PianoKeyManager.getLoadFinished()) {
                    showNotReadyToast();
                    return;
                }
                FlurryAgent.logEvent(Constants.ACT_PIANO_MUSIC_LIST);
                Context cxt = PianoActivity.this;
                File[] fileList = new File(RecordManager.getRecordDirectory(cxt)).listFiles(RecordManager.recordFileFilter);
                if (fileList == null || fileList.length == 0) {
                    Toast.makeText(cxt,
                            String.format(getResources().getString(R.string.no_saved_file), RecordManager.getRecordDirectory(cxt)),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

//                LoadFileListDialog dlg = new LoadFileListDialog(PianoActivity.this, new OnFileSelected() {
//                    @Override
//                    public void onFileSelected(String filepath) {
//
//                       ivReplay.performClick();
//
//                    }
//                });
//
//                dlg.show();
                FlurryAgent.logEvent(Constants.ACT_PIANO_SELECT_FILE);
                Intent i = new Intent(PianoActivity.this, FileSelectActivity.class);
                startActivityForResult(i, REQUSET);
            }
        });

        /**
         * 转换白色按键提示内容监听
         *
         * 双排单排键
         */
        ivSelectOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlurryAgent.logEvent(Constants.ACT_PIANO_SINGLE_AND_DOUBLE_KEYBOARD);
                if (!isSingleKey) {//单排键
                    L.v("piano", "------单排键------" + isSingleKey + ";   isDoubleKeyboardConnection:" + isDoubleKeyboardConnection);
                    isSingleKey = true;
                    secondKeyLinearLayout.setVisibility(View.GONE);
                    ivSelectOption.setImageResource(R.drawable.piano_single_keys_botton);
                    isDoubleKeyboardConnection = true;//单排时没有关联
                } else {//双排键
                    L.v("piano", "------双排键------" + isSingleKey);
                    isSingleKey = false;
                    isDoubleKeyboardConnection = SavePreferencesUtils.getInstance(getApplicationContext()).sp.getBoolean(SettingActivity.IS_DOUBLE_KEYBOARD_CONNECTION, false);//双排有关联
                    secondKeyLinearLayout.setVisibility(View.VISIBLE);
                    scrollBar2.requestLayout();
                    ivSelectOption.setImageResource(R.drawable.piano_keyboard_switch);

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //execute the task
                            scrollBar2.invalidate();
                            scrollBar2.requestLayout();
                        }
                    }, 5);


                    L.v("piano", "-----按键的数量---" + SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(SettingActivity.KEYBOARD_NUM, "10"));

                    initViewState(Integer.parseInt(SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(SettingActivity.KEYBOARD_NUM, "10")));
                }

            }
        });

        /**
         * 设置监听
         */
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlurryAgent.logEvent(Constants.ACT_PIANO_SETTINGS);
                Intent i = new Intent(PianoActivity.this, SettingActivity.class);
                i.putExtra("isSingleKey", isSingleKey);
                startActivityForResult(i, REQUSET);
            }
        });


        ivControlHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llPianoRecordTip.setVisibility(View.GONE);//处理在隐藏的时候出现阴影
                FlurryAgent.logEvent(Constants.ACT_PIANO_HIDE_CONTROL_BAR);
                isHideControl = SavePreferencesUtils.getInstance(getApplicationContext()).sp.getBoolean(SettingActivity.IS_CONTROL_HIDE_RADION_GROUP, true);
                hideOrDisplayControl();
            }
        });
        /**
         * 左移键盘监听
         */
        ivLeftPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PianoKeyManager.getLoadFinished()) {
                    showNotReadyToast();
                    return;
                }
                L.v("piano", ">>>ivLeftPage>>mCurrentPageIndex:" + mCurrentPageIndex + ";mCurrentPageIndex2:" + mCurrentPageIndex2);


                if (isDoubleKeyboardConnection) {//没关联
                    leftMove(0, 2);
                } else {//有关联
//                    if (keyNums == 10) {//10个按键
                    if (mCurrentPageIndex == 0) {//左边顶到头
                        L.v("piano", "---左边顶到头---mCurrentPageIndex:" + mCurrentPageIndex + ";mCurrentPageIndex2:" + mCurrentPageIndex2);
                    } else {
                        MOVE_BUTTON_POSINTION = 0;
                        L.v("piano", "-------上排左移按钮--------mCurrentPageIndex:" + mCurrentPageIndex + ";mCurrentPageIndex2:" + mCurrentPageIndex2);
                        //上排先移动,下来下排跟着移动
                        leftMove(0, 1);//往左移动
                        leftMove(1, 1);
                    }
//                    }else if (keyNums == 8) {//8个按键
//    //
//                    }else if (keyNums == 6) {//6个按键
//    //
//                    }


                }


            }
        });

        /**
         * 右移键盘监听
         */
        ivRightPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PianoKeyManager.getLoadFinished()) {
                    showNotReadyToast();
                    return;
                }
                L.v("piano", ">>>ivRightPage>>mCurrentPageIndex:" + mCurrentPageIndex + ";mCurrentPageIndex2:" + mCurrentPageIndex2);

                if (!isSingleKey) {

                    if (isDoubleKeyboardConnection) {//没有关联
                        L.v("piano", "---------isDoubleKeyboardConnection--没关联--" + isDoubleKeyboardConnection);
                        rightMove(0, 2);
                    } else {//有关联
                        MOVE_BUTTON_POSINTION = 1;
                        if (keyNums == 16) {
                            if (mCurrentPageIndex == 20) {//36-16=20
                                L.v("piano", "-16键--右边顶到头---");
                            } else {
                                rightMove(0, 1);//往右移动
                                rightMove(1, 1);
                            }
                        } else if (keyNums == 14) {//38-14=24
                            if (mCurrentPageIndex == 24) {
                                L.v("piano", "-14键--右边顶到头---");
                            } else {
                                rightMove(0, 1);//往右移动
                                rightMove(1, 1);
                            }

                        } else if (keyNums == 12) {//40-12=28
                            if (mCurrentPageIndex == 28) {
                                L.v("piano", "-12键--右边顶到头---");
                            } else {
                                rightMove(0, 1);//往右移动
                                rightMove(1, 1);
                            }
                        } else if (keyNums == 10) {//10个按键
                            if (mCurrentPageIndex == 32) {//右边顶到头,并且为42-10=32
                                L.v("piano", "-10键--右边顶到头---");

                            } else {
                                rightMove(0, 1);//往右移动
                                rightMove(1, 1);
                            }
                        } else if (keyNums == 8) {//8个按键

                            if (mCurrentPageIndex == 36) {//右边顶到头,并且为44-8=36
                                L.v("piano", "-8键--右边顶到头---");

                            } else {
                                rightMove(0, 1);//往右移动
                                rightMove(1, 1);
                            }

                        } else if (keyNums == 6) {//6个按键
                            if (mCurrentPageIndex == 40) {//右边顶到头,并且为46-6=40
                                L.v("piano", "-6键--右边顶到头---");

                            } else {
                                rightMove(0, 1);//往右移动
                                rightMove(1, 1);
                            }
                        }

                    }

                } else {
                    L.v("piano", "------isSingleKey-------" + isSingleKey);
                    rightMove(0, 2);
                }


            }
        });

        /**
         * 切换键盘是否可以滑动监听
         */
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {//当为true时可以进行滑动,键盘不能进行弹奏
                    fistWhiteKeys.setEnabled(true);
                    mHandler.sendEmptyMessage(0);
                } else {//当为false时不能进行滑动,键盘可以进行弹奏
                    fistWhiteKeys.setEnabled(false);
                    mHandler.sendEmptyMessage(1);
                }

            }
        });
//        scrollBar.setProgress(mCurrentPageIndex);
//        scrollBar2.setProgress(mCurrentPageIndex2);

        scrollBar.setListener(new OnKeysRangeChanged() {
            @Override
            public void onRangeChanged(int new_index, boolean isValidate) {
                L.v("piano", "scrollBar OnKeysRangeChanged new_index:" + new_index);
                mCurrentPageIndex = new_index;
                refreshAllKeys(new_index);
                if (isDoubleKeyboardConnection) {//没关联
                    L.v("piano", "-scrollBar---------------------没有关联-----mCurrentPageIndex--" + mCurrentPageIndex + ";  mCurrentPageIndex2:" + mCurrentPageIndex2);
                } else {//有关联
//                    if(isValidate){
//                        scrollBar2.setProgress(mCurrentPageIndex+keyNums, false);
////                        refreshAllKeys2(new_index);
//                    }
                    scrollBar2.setProgress(mCurrentPageIndex + keyNums);
                    L.v("piano", "-scrollBar----------------===-----有关联-------mCurrentPageIndex--" + mCurrentPageIndex + ";  mCurrentPageIndex2:" + mCurrentPageIndex2);
                    if (MOVE_BUTTON_POSINTION == 0) {//第一排左边按钮点击移动
                        mCurrentPageIndex2 = mCurrentPageIndex + keyNums + 1;
                    } else if (MOVE_BUTTON_POSINTION == 1) {//第一批右边按钮点击移动
                        L.v("piano", "------MOVE_BUTTON_POSINTION == 1------");
                        mCurrentPageIndex2 = mCurrentPageIndex + keyNums - 1;
                    }
                    L.v("piano", "-scrollBar---------------------有关联-------mCurrentPageIndex--" + mCurrentPageIndex + ";  mCurrentPageIndex2:" + mCurrentPageIndex2);

                }

            }
        });
        scrollBar.setProgress(mCurrentPageIndex);
        whiteKeys[0].getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refreshAllKeysBounds();
            }
        });

        fistWhiteKeys.setEnabled(false);//当设置为false时键盘可以进行弹奏
        /**
         * 滑动键盘
         */
        fistWhiteKeys.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        MOVE_BUTTON_POSINTION = 3;

                        SCROLL_STATE = 1;//正在滑动

//                        L.i("piano", "....fistWhiteKeys...onTouchEvent ACTION_MOVE:  x0:" + x0 + ";   x1:" + x1);

                        x2 = motionEvent.getX();

                        float num = (x1 - x2) / pelKeyLength;

                        a = Math.round(Math.abs(num));

                        if (x1 - x2 > 0) {
                            MOVE_BUTTON_POSINTION = 0;
                            //往左滑
                            if (a > 2) {

                                if (isDoubleKeyboardConnection) {//没关联
                                    leftMove(0, 1);
                                } else {//有关联

                                    if (mCurrentPageIndex == 0) {

                                    } else {
                                        L.i("piano", "==fistWhiteKeys===========往左======mCurrentPageIndex:" + mCurrentPageIndex + ";  mCurrentPageIndex2" + mCurrentPageIndex2);
                                        leftMove(0, 1);//左移
                                        leftMove(1, 1);
                                    }

                                }

                            } else {
                                L.i("piano", ">>>" + a);
                            }


                        } else {
                            MOVE_BUTTON_POSINTION = 1;
                            //往右滑
                            if (a > 2) {
                                if (!isSingleKey) {
                                    if (isDoubleKeyboardConnection) {//没关联
                                        rightMove(0, 1);//右移
                                    } else {//有关联
                                        L.i("piano", "==fistWhiteKeys==========往右=======mCurrentPageIndex:" + mCurrentPageIndex + ";  mCurrentPageIndex2" + mCurrentPageIndex2 + ";  isDoubleKeyboardConnection" + isDoubleKeyboardConnection);
//                                    rightMove(0, 1);//右移
//                                    rightMove(1, 1);
                                        if (keyNums == 16) {
                                            if (mCurrentPageIndex == 20) {//36-16=20

                                            } else {
                                                rightMove(0, 1);//往右移动
                                                rightMove(1, 1);
                                            }
                                        } else if (keyNums == 14) {//38-14=24
                                            if (mCurrentPageIndex == 24) {

                                            } else {
                                                rightMove(0, 1);//往右移动
                                                rightMove(1, 1);
                                            }

                                        } else if (keyNums == 12) {//40-12=28
                                            if (mCurrentPageIndex == 28) {

                                            } else {
                                                rightMove(0, 1);//往右移动
                                                rightMove(1, 1);
                                            }
                                        } else if (keyNums == 10) {
                                            if (mCurrentPageIndex == 32) {

                                            } else {
                                                rightMove(0, 1);//右移
                                                rightMove(1, 1);
                                            }
                                        } else if (keyNums == 8) {
                                            if (mCurrentPageIndex == 36) {

                                            } else {
                                                rightMove(0, 1);//右移
                                                rightMove(1, 1);
                                            }
                                        } else if (keyNums == 6) {
                                            if (mCurrentPageIndex == 40) {

                                            } else {
                                                rightMove(0, 1);//右移
                                                rightMove(1, 1);
                                            }
                                        }

                                    }
                                } else {
                                    rightMove(0, 1);
                                }


                            } else {
                                L.i("piano", "===" + a);
                            }

                        }
                        break;
                    case MotionEvent.ACTION_UP:
//                        L.i("piano", "....fistWhiteKeys...onTouchEvent ACTION_POINTER_UP");
                        SCROLL_STATE = 2;//停止滑动

                        break;

                }

                if (SCROLL_STATE == 2) {
                    L.v("piano", "--------停止滑动--------");
                    play(motionEvent);
                } else {
                    L.v("piano", "--------正在滑动----或者按下----");
                }


                return true;
            }
        });

        /**********第二排监听***********************************************************/
        /**
         * 左移键盘监听
         */
        ivLeftPage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PianoKeyManager.getLoadFinished()) {
                    showNotReadyToast();
                    return;
                }
                L.v("piano", ">>>ivLeftPage2>>mCurrentPageIndex:" + mCurrentPageIndex + ";mCurrentPageIndex2:" + mCurrentPageIndex2);

                if (isDoubleKeyboardConnection) {//没关联
                    leftMove(1, 2);
                } else {//有关联


                    if (mCurrentPageIndex == 0) {
                        L.v("piano", "--ivLeftPage2-左边顶到头---");
                    } else {
                        MOVE_BUTTON_POSINTION = 0;
                        //上排先移动,下来下排跟着移动
                        leftMove(0, 1);//往左移动
                        leftMove(1, 1);
                    }

                }

            }
        });

        /**
         * 右移键盘监听
         */
        ivRightPage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PianoKeyManager.getLoadFinished()) {
                    showNotReadyToast();
                    return;
                }
                L.v("piano", ">>>ivRightPage2>>mCurrentPageIndex:" + mCurrentPageIndex + ";mCurrentPageIndex2:" + mCurrentPageIndex2);

                if (isDoubleKeyboardConnection) {//没关联
                    rightMove(1, 2);
                } else {//有关联
                    MOVE_BUTTON_POSINTION = 1;
                    if (keyNums == 16) {
                        if (mCurrentPageIndex == 20) {//36-16=20

                        } else {
                            rightMove(0, 1);//往右移动
                            rightMove(1, 1);
                        }
                    } else if (keyNums == 14) {//38-14=24
                        if (mCurrentPageIndex == 24) {

                        } else {
                            rightMove(0, 1);//往右移动
                            rightMove(1, 1);
                        }

                    } else if (keyNums == 12) {//40-12=28
                        if (mCurrentPageIndex == 28) {

                        } else {
                            rightMove(0, 1);//往右移动
                            rightMove(1, 1);
                        }
                    } else if (keyNums == 10) {
                        if (mCurrentPageIndex == 32) {

                        } else {
                            rightMove(0, 1);
                            rightMove(1, 1);//右移
                        }
                    } else if (keyNums == 8) {
                        if (mCurrentPageIndex == 36) {

                        } else {
                            rightMove(0, 1);
                            rightMove(1, 1);//右移
                        }
                    } else if (keyNums == 6) {
                        if (mCurrentPageIndex == 40) {

                        } else {
                            rightMove(0, 1);
                            rightMove(1, 1);//右移
                        }
                    }
                }

            }
        });


        scrollBar2.setListener(new OnKeysRangeChanged() {
            @Override
            public void onRangeChanged(int new_index, boolean isValidate) {
                mCurrentPageIndex2 = new_index;
                refreshAllKeys2(new_index);
                L.v("piano", "----------scrollBar2-----------------keyOrder--");

                if (isDoubleKeyboardConnection) {//没关联
                    L.v("piano", "--------scrollBar2------没关联--mCurrentPageIndex2-" + mCurrentPageIndex2);
                } else {//有关联
                    if (isValidate) {
                        scrollBar.setProgress(mCurrentPageIndex2 - keyNums, false);
                        refreshAllKeys(new_index - keyNums);
                    }
                    mCurrentPageIndex = mCurrentPageIndex2 - keyNums;
                    L.v("piano", "--------scrollBar2------有关联--mCurrentPageIndex2-" + mCurrentPageIndex2 + ";  mCurrentPageIndex:" + mCurrentPageIndex);
                }

            }
        });
        scrollBar2.setProgress(mCurrentPageIndex2);
        whiteKeys2[0].getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                L.i("piano", ">>>>>>>>>>>>>>>whiteKeys2[0].getViewTreeObserver()>>>>>>>>>>>>>");
                refreshAllKeysBounds2();
            }
        });

        /**
         * 切换键盘是否可以滑动监听
         */
        switchButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {//当为true时可以进行滑动,键盘不能进行弹奏
                    fistWhiteKeys2.setEnabled(true);
                    mHandler.sendEmptyMessage(2);
                } else {//当为false时不能进行滑动,键盘可以进行弹奏
                    fistWhiteKeys2.setEnabled(false);
                    mHandler.sendEmptyMessage(3);
                }
            }
        });

        fistWhiteKeys2.setEnabled(false);//当设置为false时键盘可以进行弹奏
        /**
         * 滑动键盘
         */
        fistWhiteKeys2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        MOVE_BUTTON_POSINTION = 3;
                        SCROLL_STATE2 = 1;

                        x2 = motionEvent.getX();

                        float num = (x1 - x2) / pelKeyLength;

                        a = Math.round(Math.abs(num));

                        if (x1 - x2 > 0) {
                            MOVE_BUTTON_POSINTION = 0;
                            //往左滑
                            if (a > 2) {

                                if (isDoubleKeyboardConnection) {//没关联
                                    leftMove(1, 1);
                                } else {//有关联
                                    if (mCurrentPageIndex == 0) {

                                    } else {
                                        leftMove(0, 1);//左移
                                        leftMove(1, 1);
                                    }

                                }

                            } else {
                                L.i("piano", "......");
                            }
                        } else {
                            MOVE_BUTTON_POSINTION = 1;
                            //往右滑
                            if (a > 2) {
                                if (isDoubleKeyboardConnection) {//没关联
                                    rightMove(1, 1);//右移
                                } else {//有关联
                                    L.i("piano", "==fistWhiteKeys2=================mCurrentPageIndex:" + mCurrentPageIndex + ";  mCurrentPageIndex2" + mCurrentPageIndex2);
                                    if (keyNums == 16) {
                                        if (mCurrentPageIndex == 20) {//36-16=20

                                        } else {
                                            rightMove(0, 1);//往右移动
                                            rightMove(1, 1);
                                        }
                                    } else if (keyNums == 14) {//38-14=24
                                        if (mCurrentPageIndex == 24) {

                                        } else {
                                            rightMove(0, 1);//往右移动
                                            rightMove(1, 1);
                                        }

                                    } else if (keyNums == 12) {//40-12=28
                                        if (mCurrentPageIndex == 28) {

                                        } else {
                                            rightMove(0, 1);//往右移动
                                            rightMove(1, 1);
                                        }
                                    } else if (keyNums == 10) {
                                        L.i("piano", "==fistWhiteKeys2++++++++");
                                        if (mCurrentPageIndex == 32) {

                                        } else {
                                            rightMove(0, 1);
                                            rightMove(1, 1);//右移
                                        }
                                    } else if (keyNums == 8) {
                                        if (mCurrentPageIndex == 36) {

                                        } else {
                                            rightMove(0, 1);
                                            rightMove(1, 1);//右移
                                        }
                                    } else if (keyNums == 6) {
                                        if (mCurrentPageIndex == 40) {

                                        } else {
                                            rightMove(0, 1);
                                            rightMove(1, 1);//右移
                                        }
                                    }


                                }

                            } else {
                                L.i("piano", "=====");
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        SCROLL_STATE2 = 2;

                        break;
                }

                if (SCROLL_STATE2 == 2) {
                    play2(motionEvent);
                } else {

                }

                return true;
            }
        });
    }

    //public static final String PLACEMENT_ID = "1776796919230717_1776801712563571";
    public static final String PLACEMENT_ID = "480740438790913_480744648790492";
    private NativeAd nativeAd;
    private RelativeLayout adView;
    private AdChoicesView adChoicesView;

    private LinearLayout adContainer;
    private LinearLayout InterPolateView;

    private void initAd() {

        nativeAd = new NativeAd(this, PLACEMENT_ID);

        nativeAd.setAdListener(this);
        nativeAd.loadAd();

    }


    @Override
    public void onError(Ad ad, AdError adError) {
        Log.d(TAG, "ad error " + adError.getErrorMessage());
    }


    @Override
    public void onAdLoaded(Ad ad) {
        if (nativeAd == null || nativeAd != ad) {
            // Race condition, load() called again before last ad was displayed
            return;
        }
        Log.d(TAG, "ad onAdLoaded " + nativeAd.getAdTitle());
        nativeAd.unregisterView();
        if (adContainer == null) {
            adContainer = (LinearLayout) findViewById(R.id.piano_main_ad_container);
        }
        if (adView == null) {
            adView = (RelativeLayout) LayoutInflater.from(PianoActivity.this).inflate(R.layout.ad_start, adContainer, false);
        }
        adContainer.removeAllViews();
        adContainer.addView(adView);
        adContainer.setTag(TAG_AD_VIEW);


        TextView titleText = (TextView) adView.findViewById(R.id.ad_title);
        titleText.setText(nativeAd.getAdTitle());

        ImageView adImg = (ImageView) adView.findViewById(R.id.ad_img);
        NativeAd.Image adIcon = nativeAd.getAdIcon();
        NativeAd.downloadAndDisplayImage(adIcon, adImg);

        String callToAction = nativeAd.getAdCallToAction();
        Button actButton = (Button) findViewById(R.id.ad_act_button);
        actButton.setText(callToAction);


        if (adChoicesView == null) {
            adChoicesView = new AdChoicesView(this, nativeAd, true);
            LinearLayout adChoiceLayout = (LinearLayout) adView.findViewById(R.id.ad_choice);
            adChoiceLayout.addView(adChoicesView);
        }
        nativeAd.registerViewForInteraction(adView);
//        nativeAd.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    switch (view.getId()) {
//                        case R.id.ad_act_button:
//                            Log.d(TAG, "Call to action button clicked");
//                            break;
//                        case R.id.ad_img:
//                            Log.d(TAG, "Main image clicked");
//                            break;
//                        default:
//                            Log.d(TAG, "Other ad component clicked");
//                    }
//                }
//                return false;
//            }
//        });
        setAdInterpolateView();
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.d(TAG, "onAdClicked");
        gotoPlayStore();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ad_interpolate_layout:
                gotoPlayStore();
                break;
        }

    }

    public static final String MP3_CONVERTER_PKGNAME = "mp3.converter.video.tubemate.youtube.soundcloud.music.download";

    private void gotoPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + MP3_CONVERTER_PKGNAME));

        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
        if (intent.resolveActivity(getPackageManager()) != null) { //可以接收
            startActivity(intent);
        } else { //没有应用市场，我们通过浏览器跳转到Google Play
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + MP3_CONVERTER_PKGNAME));
            //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
            if (intent.resolveActivity(getPackageManager()) != null) { //有浏览器
                startActivity(intent);
            } else {
//                Toast.makeText(this, "您没安装应用市场，连浏览器也没有", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static final int AD_INTERPOLATE = 101;

    public void setAdInterpolateView() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (!PianoActivity.this.isFinishing()) {
                    try {
                        Thread.sleep(1000 * 30);
                        mHandler.sendEmptyMessage(AD_INTERPOLATE);
                        Thread.sleep(1000 * 10);
                        mHandler.sendEmptyMessage(AD_INTERPOLATE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    /**
     * 初始化数据
     */
    private void initDatas() {
//        L.v("piano","=====initDatas====" + SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(SettingActivity.KEYBOARD_NUM, "10"));
        keyNums = Integer.parseInt(SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(SettingActivity.KEYBOARD_NUM, "10"));//默认按键个数是10
        pelKeyLength = mScreenWitdh / keyNums;
        initViewState(keyNums);
        isDoubleKeyboardConnection = SavePreferencesUtils.getInstance(getApplicationContext()).sp.getBoolean(SettingActivity.IS_DOUBLE_KEYBOARD_CONNECTION, true);


    }

    /**
     * 设置按键数量,出售相应的布局
     *
     * @param value 键盘上按键的数量
     */
    private void initViewState(int value) {
        L.i("piano", " >>>>initViewState>>>>>>" + isSingleKey + ";  value:" + value);

//        scrollBar.setScreen_keys_num(value);
//        scrollBar2.setScreen_keys_num(value);
        scrollBar.setScreen_keys_num(value, 0);
        scrollBar2.setScreen_keys_num(value, 1);

        keyNums = value;
        switch (value) {
            case 16:
                L.v("piano", "----initViewState----16个键--------");
                whiteKeys[15].setVisibility(View.VISIBLE);
                fistViews[14].setVisibility(View.VISIBLE);
                blackKeys[14].setVisibility(View.VISIBLE);

                whiteKeys[14].setVisibility(View.VISIBLE);
                fistViews[13].setVisibility(View.VISIBLE);
                blackKeys[13].setVisibility(View.VISIBLE);

                whiteKeys[13].setVisibility(View.VISIBLE);
                fistViews[12].setVisibility(View.VISIBLE);
                blackKeys[12].setVisibility(View.VISIBLE);

                whiteKeys[12].setVisibility(View.VISIBLE);
                fistViews[11].setVisibility(View.VISIBLE);
                blackKeys[11].setVisibility(View.VISIBLE);

                whiteKeys[11].setVisibility(View.VISIBLE);
                fistViews[10].setVisibility(View.VISIBLE);
                blackKeys[10].setVisibility(View.VISIBLE);

                whiteKeys[10].setVisibility(View.VISIBLE);
                fistViews[9].setVisibility(View.VISIBLE);
                blackKeys[9].setVisibility(View.VISIBLE);

                whiteKeys[9].setVisibility(View.VISIBLE);
                fistViews[8].setVisibility(View.VISIBLE);
                blackKeys[8].setVisibility(View.VISIBLE);

                whiteKeys[8].setVisibility(View.VISIBLE);
                fistViews[7].setVisibility(View.VISIBLE);
                blackKeys[7].setVisibility(View.VISIBLE);

                whiteKeys[7].setVisibility(View.VISIBLE);
                fistViews[6].setVisibility(View.VISIBLE);
                blackKeys[6].setVisibility(View.VISIBLE);

                whiteKeys[6].setVisibility(View.VISIBLE);
                fistViews[5].setVisibility(View.VISIBLE);
                blackKeys[5].setVisibility(View.VISIBLE);

                scrollBar.setProgress(mCurrentPageIndex);
                if (!isSingleKey) {//如果是双排键
                    //第二排键
                    L.v("piano", "---双排-initViewState----16个键--------");
                    whiteKeys2[15].setVisibility(View.VISIBLE);
                    secondViews[14].setVisibility(View.VISIBLE);
                    blackKeys2[14].setVisibility(View.VISIBLE);

                    whiteKeys2[14].setVisibility(View.VISIBLE);
                    secondViews[13].setVisibility(View.VISIBLE);
                    blackKeys2[13].setVisibility(View.VISIBLE);

                    whiteKeys2[13].setVisibility(View.VISIBLE);
                    secondViews[12].setVisibility(View.VISIBLE);
                    blackKeys2[12].setVisibility(View.VISIBLE);

                    whiteKeys2[12].setVisibility(View.VISIBLE);
                    secondViews[11].setVisibility(View.VISIBLE);
                    blackKeys2[11].setVisibility(View.VISIBLE);

                    whiteKeys2[11].setVisibility(View.VISIBLE);
                    secondViews[10].setVisibility(View.VISIBLE);
                    blackKeys2[10].setVisibility(View.VISIBLE);

                    whiteKeys2[10].setVisibility(View.VISIBLE);
                    secondViews[9].setVisibility(View.VISIBLE);
                    blackKeys2[9].setVisibility(View.VISIBLE);

                    whiteKeys2[9].setVisibility(View.VISIBLE);
                    secondViews[8].setVisibility(View.VISIBLE);
                    blackKeys2[8].setVisibility(View.VISIBLE);

                    whiteKeys2[8].setVisibility(View.VISIBLE);
                    secondViews[7].setVisibility(View.VISIBLE);
                    blackKeys2[7].setVisibility(View.VISIBLE);

                    whiteKeys2[7].setVisibility(View.VISIBLE);
                    secondViews[6].setVisibility(View.VISIBLE);
                    blackKeys2[6].setVisibility(View.VISIBLE);

                    whiteKeys2[6].setVisibility(View.VISIBLE);
                    secondViews[5].setVisibility(View.VISIBLE);
                    blackKeys2[5].setVisibility(View.VISIBLE);

                    scrollBar2.setProgress(mCurrentPageIndex + 16);
                }
                break;

            case 14:
                L.v("piano", "----initViewState----14个键--------");
                whiteKeys[15].setVisibility(View.GONE);
                fistViews[14].setVisibility(View.GONE);
                blackKeys[14].setVisibility(View.GONE);

                whiteKeys[14].setVisibility(View.GONE);
                fistViews[13].setVisibility(View.GONE);
                blackKeys[13].setVisibility(View.GONE);

                whiteKeys[13].setVisibility(View.VISIBLE);
                fistViews[12].setVisibility(View.VISIBLE);
                blackKeys[12].setVisibility(View.VISIBLE);

                whiteKeys[12].setVisibility(View.VISIBLE);
                fistViews[11].setVisibility(View.VISIBLE);
                blackKeys[11].setVisibility(View.VISIBLE);

                whiteKeys[11].setVisibility(View.VISIBLE);
                fistViews[10].setVisibility(View.VISIBLE);
                blackKeys[10].setVisibility(View.VISIBLE);

                whiteKeys[10].setVisibility(View.VISIBLE);
                fistViews[9].setVisibility(View.VISIBLE);
                blackKeys[9].setVisibility(View.VISIBLE);

                whiteKeys[9].setVisibility(View.VISIBLE);
                fistViews[8].setVisibility(View.VISIBLE);
                blackKeys[8].setVisibility(View.VISIBLE);

                whiteKeys[8].setVisibility(View.VISIBLE);
                fistViews[7].setVisibility(View.VISIBLE);
                blackKeys[7].setVisibility(View.VISIBLE);

                whiteKeys[7].setVisibility(View.VISIBLE);
                fistViews[6].setVisibility(View.VISIBLE);
                blackKeys[6].setVisibility(View.VISIBLE);

                whiteKeys[6].setVisibility(View.VISIBLE);
                fistViews[5].setVisibility(View.VISIBLE);
                blackKeys[5].setVisibility(View.VISIBLE);

                scrollBar.setProgress(mCurrentPageIndex);
                if (!isSingleKey) {//如果是双排键
                    //第二排键
                    L.v("piano", "---双排-initViewState----14个键--------");
                    whiteKeys2[15].setVisibility(View.GONE);
                    secondViews[14].setVisibility(View.GONE);
                    blackKeys2[14].setVisibility(View.GONE);

                    whiteKeys2[14].setVisibility(View.GONE);
                    secondViews[13].setVisibility(View.GONE);
                    blackKeys2[13].setVisibility(View.GONE);

                    whiteKeys2[13].setVisibility(View.VISIBLE);
                    secondViews[12].setVisibility(View.VISIBLE);
                    blackKeys2[12].setVisibility(View.VISIBLE);

                    whiteKeys2[12].setVisibility(View.VISIBLE);
                    secondViews[11].setVisibility(View.VISIBLE);
                    blackKeys2[11].setVisibility(View.VISIBLE);

                    whiteKeys2[11].setVisibility(View.VISIBLE);
                    secondViews[10].setVisibility(View.VISIBLE);
                    blackKeys2[10].setVisibility(View.VISIBLE);

                    whiteKeys2[10].setVisibility(View.VISIBLE);
                    secondViews[9].setVisibility(View.VISIBLE);
                    blackKeys2[9].setVisibility(View.VISIBLE);

                    whiteKeys2[9].setVisibility(View.VISIBLE);
                    secondViews[8].setVisibility(View.VISIBLE);
                    blackKeys2[8].setVisibility(View.VISIBLE);

                    whiteKeys2[8].setVisibility(View.VISIBLE);
                    secondViews[7].setVisibility(View.VISIBLE);
                    blackKeys2[7].setVisibility(View.VISIBLE);

                    whiteKeys2[7].setVisibility(View.VISIBLE);
                    secondViews[6].setVisibility(View.VISIBLE);
                    blackKeys2[6].setVisibility(View.VISIBLE);

                    whiteKeys2[6].setVisibility(View.VISIBLE);
                    secondViews[5].setVisibility(View.VISIBLE);
                    blackKeys2[5].setVisibility(View.VISIBLE);

                    scrollBar2.setProgress(mCurrentPageIndex + 14);
                }
                break;

            case 12:
                L.v("piano", "----initViewState----12个键--------");
                whiteKeys[15].setVisibility(View.GONE);
                fistViews[14].setVisibility(View.GONE);
                blackKeys[14].setVisibility(View.GONE);

                whiteKeys[14].setVisibility(View.GONE);
                fistViews[13].setVisibility(View.GONE);
                blackKeys[13].setVisibility(View.GONE);

                whiteKeys[13].setVisibility(View.GONE);
                fistViews[12].setVisibility(View.GONE);
                blackKeys[12].setVisibility(View.GONE);

                whiteKeys[12].setVisibility(View.GONE);
                fistViews[11].setVisibility(View.GONE);
                blackKeys[11].setVisibility(View.GONE);

                whiteKeys[11].setVisibility(View.VISIBLE);
                fistViews[10].setVisibility(View.VISIBLE);
                blackKeys[10].setVisibility(View.VISIBLE);

                whiteKeys[10].setVisibility(View.VISIBLE);
                fistViews[9].setVisibility(View.VISIBLE);
                blackKeys[9].setVisibility(View.VISIBLE);

                whiteKeys[9].setVisibility(View.VISIBLE);
                fistViews[8].setVisibility(View.VISIBLE);
                blackKeys[8].setVisibility(View.VISIBLE);

                whiteKeys[8].setVisibility(View.VISIBLE);
                fistViews[7].setVisibility(View.VISIBLE);
                blackKeys[7].setVisibility(View.VISIBLE);

                whiteKeys[7].setVisibility(View.VISIBLE);
                fistViews[6].setVisibility(View.VISIBLE);
                blackKeys[6].setVisibility(View.VISIBLE);

                whiteKeys[6].setVisibility(View.VISIBLE);
                fistViews[5].setVisibility(View.VISIBLE);
                blackKeys[5].setVisibility(View.VISIBLE);

                scrollBar.setProgress(mCurrentPageIndex);
                if (!isSingleKey) {//如果是双排键
                    //第二排键
                    L.v("piano", "---双排-initViewState----12个键--------");
                    whiteKeys2[15].setVisibility(View.GONE);
                    secondViews[14].setVisibility(View.GONE);
                    blackKeys2[14].setVisibility(View.GONE);

                    whiteKeys2[14].setVisibility(View.GONE);
                    secondViews[13].setVisibility(View.GONE);
                    blackKeys2[13].setVisibility(View.GONE);

                    whiteKeys2[13].setVisibility(View.GONE);
                    secondViews[12].setVisibility(View.GONE);
                    blackKeys2[12].setVisibility(View.GONE);

                    whiteKeys2[12].setVisibility(View.GONE);
                    secondViews[11].setVisibility(View.GONE);
                    blackKeys2[11].setVisibility(View.GONE);

                    whiteKeys2[11].setVisibility(View.VISIBLE);
                    secondViews[10].setVisibility(View.VISIBLE);
                    blackKeys2[10].setVisibility(View.VISIBLE);

                    whiteKeys2[10].setVisibility(View.VISIBLE);
                    secondViews[9].setVisibility(View.VISIBLE);
                    blackKeys2[9].setVisibility(View.VISIBLE);

                    whiteKeys2[9].setVisibility(View.VISIBLE);
                    secondViews[8].setVisibility(View.VISIBLE);
                    blackKeys2[8].setVisibility(View.VISIBLE);

                    whiteKeys2[8].setVisibility(View.VISIBLE);
                    secondViews[7].setVisibility(View.VISIBLE);
                    blackKeys2[7].setVisibility(View.VISIBLE);

                    whiteKeys2[7].setVisibility(View.VISIBLE);
                    secondViews[6].setVisibility(View.VISIBLE);
                    blackKeys2[6].setVisibility(View.VISIBLE);

                    whiteKeys2[6].setVisibility(View.VISIBLE);
                    secondViews[5].setVisibility(View.VISIBLE);
                    blackKeys2[5].setVisibility(View.VISIBLE);

                    scrollBar2.setProgress(mCurrentPageIndex + 12);
                }

                break;
            case 10://10个键
                L.v("piano", "----initViewState----10个键--------");
                whiteKeys[15].setVisibility(View.GONE);
                fistViews[14].setVisibility(View.GONE);
                blackKeys[14].setVisibility(View.GONE);

                whiteKeys[14].setVisibility(View.GONE);
                fistViews[13].setVisibility(View.GONE);
                blackKeys[13].setVisibility(View.GONE);

                whiteKeys[13].setVisibility(View.GONE);
                fistViews[12].setVisibility(View.GONE);
                blackKeys[12].setVisibility(View.GONE);

                whiteKeys[12].setVisibility(View.GONE);
                fistViews[11].setVisibility(View.GONE);
                blackKeys[11].setVisibility(View.GONE);

                whiteKeys[11].setVisibility(View.GONE);
                fistViews[10].setVisibility(View.GONE);
                blackKeys[10].setVisibility(View.GONE);

                whiteKeys[10].setVisibility(View.GONE);
                fistViews[9].setVisibility(View.GONE);
                blackKeys[9].setVisibility(View.GONE);

                whiteKeys[9].setVisibility(View.VISIBLE);
                fistViews[8].setVisibility(View.VISIBLE);
                blackKeys[8].setVisibility(View.VISIBLE);

                whiteKeys[8].setVisibility(View.VISIBLE);
                fistViews[7].setVisibility(View.VISIBLE);
                blackKeys[7].setVisibility(View.VISIBLE);

                whiteKeys[7].setVisibility(View.VISIBLE);
                fistViews[6].setVisibility(View.VISIBLE);
                blackKeys[6].setVisibility(View.VISIBLE);

                whiteKeys[6].setVisibility(View.VISIBLE);
                fistViews[5].setVisibility(View.VISIBLE);
                blackKeys[5].setVisibility(View.VISIBLE);

                scrollBar.setProgress(mCurrentPageIndex);
                if (!isSingleKey) {//如果是双排键
                    //第二排键
                    L.v("piano", "---双排-initViewState----10个键--------");
                    whiteKeys2[15].setVisibility(View.GONE);
                    secondViews[14].setVisibility(View.GONE);
                    blackKeys2[14].setVisibility(View.GONE);

                    whiteKeys2[14].setVisibility(View.GONE);
                    secondViews[13].setVisibility(View.GONE);
                    blackKeys2[13].setVisibility(View.GONE);

                    whiteKeys2[13].setVisibility(View.GONE);
                    secondViews[12].setVisibility(View.GONE);
                    blackKeys2[12].setVisibility(View.GONE);

                    whiteKeys2[12].setVisibility(View.GONE);
                    secondViews[11].setVisibility(View.GONE);
                    blackKeys2[11].setVisibility(View.GONE);

                    whiteKeys2[11].setVisibility(View.GONE);
                    secondViews[10].setVisibility(View.GONE);
                    blackKeys2[10].setVisibility(View.GONE);

                    whiteKeys2[10].setVisibility(View.GONE);
                    secondViews[9].setVisibility(View.GONE);
                    blackKeys2[9].setVisibility(View.GONE);

                    whiteKeys2[9].setVisibility(View.VISIBLE);
                    secondViews[8].setVisibility(View.VISIBLE);
                    blackKeys2[8].setVisibility(View.VISIBLE);

                    whiteKeys2[8].setVisibility(View.VISIBLE);
                    secondViews[7].setVisibility(View.VISIBLE);
                    blackKeys2[7].setVisibility(View.VISIBLE);

                    whiteKeys2[7].setVisibility(View.VISIBLE);
                    secondViews[6].setVisibility(View.VISIBLE);
                    blackKeys2[6].setVisibility(View.VISIBLE);

                    whiteKeys2[6].setVisibility(View.VISIBLE);
                    secondViews[5].setVisibility(View.VISIBLE);
                    blackKeys2[5].setVisibility(View.VISIBLE);

                    scrollBar2.setProgress(mCurrentPageIndex + 10);
                }

                break;
            case 8:
                L.v("piano", "----initViewState----8个键--------");
                whiteKeys[15].setVisibility(View.GONE);
                fistViews[14].setVisibility(View.GONE);
                blackKeys[14].setVisibility(View.GONE);

                whiteKeys[14].setVisibility(View.GONE);
                fistViews[13].setVisibility(View.GONE);
                blackKeys[13].setVisibility(View.GONE);

                whiteKeys[13].setVisibility(View.GONE);
                fistViews[12].setVisibility(View.GONE);
                blackKeys[12].setVisibility(View.GONE);

                whiteKeys[12].setVisibility(View.GONE);
                fistViews[11].setVisibility(View.GONE);
                blackKeys[11].setVisibility(View.GONE);

                whiteKeys[11].setVisibility(View.GONE);
                fistViews[10].setVisibility(View.GONE);
                blackKeys[10].setVisibility(View.GONE);

                whiteKeys[10].setVisibility(View.GONE);
                fistViews[9].setVisibility(View.GONE);
                blackKeys[9].setVisibility(View.GONE);

                whiteKeys[9].setVisibility(View.GONE);
                fistViews[8].setVisibility(View.GONE);
                blackKeys[8].setVisibility(View.GONE);

                whiteKeys[8].setVisibility(View.GONE);
                fistViews[7].setVisibility(View.GONE);
                blackKeys[7].setVisibility(View.GONE);

                whiteKeys[7].setVisibility(View.VISIBLE);
                fistViews[6].setVisibility(View.VISIBLE);
                blackKeys[6].setVisibility(View.VISIBLE);

                whiteKeys[6].setVisibility(View.VISIBLE);
                fistViews[5].setVisibility(View.VISIBLE);
                blackKeys[5].setVisibility(View.VISIBLE);
                scrollBar.setProgress(mCurrentPageIndex);
                if (!isSingleKey) {//如果是双排键
                    //第二排键
                    L.v("piano", "---双排-initViewState----8个键--------");
                    whiteKeys2[15].setVisibility(View.GONE);
                    secondViews[14].setVisibility(View.GONE);
                    blackKeys2[14].setVisibility(View.GONE);

                    whiteKeys2[14].setVisibility(View.GONE);
                    secondViews[13].setVisibility(View.GONE);
                    blackKeys2[13].setVisibility(View.GONE);

                    whiteKeys2[13].setVisibility(View.GONE);
                    secondViews[12].setVisibility(View.GONE);
                    blackKeys2[12].setVisibility(View.GONE);

                    whiteKeys2[12].setVisibility(View.GONE);
                    secondViews[11].setVisibility(View.GONE);
                    blackKeys2[11].setVisibility(View.GONE);

                    whiteKeys2[11].setVisibility(View.GONE);
                    secondViews[10].setVisibility(View.GONE);
                    blackKeys2[10].setVisibility(View.GONE);

                    whiteKeys2[10].setVisibility(View.GONE);
                    secondViews[9].setVisibility(View.GONE);
                    blackKeys2[9].setVisibility(View.GONE);

                    whiteKeys2[9].setVisibility(View.GONE);
                    secondViews[8].setVisibility(View.GONE);
                    blackKeys2[8].setVisibility(View.GONE);

                    whiteKeys2[8].setVisibility(View.GONE);
                    secondViews[7].setVisibility(View.GONE);
                    blackKeys2[7].setVisibility(View.GONE);


                    whiteKeys2[7].setVisibility(View.VISIBLE);
                    secondViews[6].setVisibility(View.VISIBLE);
                    blackKeys2[6].setVisibility(View.VISIBLE);

                    whiteKeys2[6].setVisibility(View.VISIBLE);
                    secondViews[5].setVisibility(View.VISIBLE);
                    blackKeys2[5].setVisibility(View.VISIBLE);

                    scrollBar2.setProgress(mCurrentPageIndex + 8);
                }

                break;

            case 6:
                L.v("piano", "----initViewState----6个键--------");
                whiteKeys[15].setVisibility(View.GONE);
                fistViews[14].setVisibility(View.GONE);
                blackKeys[14].setVisibility(View.GONE);

                whiteKeys[14].setVisibility(View.GONE);
                fistViews[13].setVisibility(View.GONE);
                blackKeys[13].setVisibility(View.GONE);

                whiteKeys[13].setVisibility(View.GONE);
                fistViews[12].setVisibility(View.GONE);
                blackKeys[12].setVisibility(View.GONE);

                whiteKeys[12].setVisibility(View.GONE);
                fistViews[11].setVisibility(View.GONE);
                blackKeys[11].setVisibility(View.GONE);

                whiteKeys[11].setVisibility(View.GONE);
                fistViews[10].setVisibility(View.GONE);
                blackKeys[10].setVisibility(View.GONE);

                whiteKeys[10].setVisibility(View.GONE);
                fistViews[9].setVisibility(View.GONE);
                blackKeys[9].setVisibility(View.GONE);

                whiteKeys[9].setVisibility(View.GONE);
                fistViews[8].setVisibility(View.GONE);
                blackKeys[8].setVisibility(View.GONE);

                whiteKeys[8].setVisibility(View.GONE);
                fistViews[7].setVisibility(View.GONE);
                blackKeys[7].setVisibility(View.GONE);

                whiteKeys[7].setVisibility(View.GONE);
                fistViews[6].setVisibility(View.GONE);
                blackKeys[6].setVisibility(View.GONE);

                whiteKeys[6].setVisibility(View.GONE);
                fistViews[5].setVisibility(View.GONE);
                blackKeys[5].setVisibility(View.GONE);
                scrollBar.setProgress(mCurrentPageIndex);
                if (!isSingleKey) {//如果是双排键
                    //第二排键
                    L.v("piano", "---双排-initViewState----6个键--------");
                    whiteKeys2[15].setVisibility(View.GONE);
                    secondViews[14].setVisibility(View.GONE);
                    blackKeys2[14].setVisibility(View.GONE);

                    whiteKeys2[14].setVisibility(View.GONE);
                    secondViews[13].setVisibility(View.GONE);
                    blackKeys2[13].setVisibility(View.GONE);

                    whiteKeys2[13].setVisibility(View.GONE);
                    secondViews[12].setVisibility(View.GONE);
                    blackKeys2[12].setVisibility(View.GONE);

                    whiteKeys2[12].setVisibility(View.GONE);
                    secondViews[11].setVisibility(View.GONE);
                    blackKeys2[11].setVisibility(View.GONE);

                    whiteKeys2[11].setVisibility(View.GONE);
                    secondViews[10].setVisibility(View.GONE);
                    blackKeys2[10].setVisibility(View.GONE);

                    whiteKeys2[10].setVisibility(View.GONE);
                    secondViews[9].setVisibility(View.GONE);
                    blackKeys2[9].setVisibility(View.GONE);

                    whiteKeys2[9].setVisibility(View.GONE);
                    secondViews[8].setVisibility(View.GONE);
                    blackKeys2[8].setVisibility(View.GONE);

                    whiteKeys2[8].setVisibility(View.GONE);
                    secondViews[7].setVisibility(View.GONE);
                    blackKeys2[7].setVisibility(View.GONE);

                    whiteKeys2[7].setVisibility(View.GONE);
                    secondViews[6].setVisibility(View.GONE);
                    blackKeys2[6].setVisibility(View.GONE);

                    whiteKeys2[6].setVisibility(View.GONE);
                    secondViews[5].setVisibility(View.GONE);
                    blackKeys2[5].setVisibility(View.GONE);
                    scrollBar2.setProgress(mCurrentPageIndex + 6);
                }

                break;
        }
    }

    /**
     * 读取屏幕尺寸
     */
    private void readScreen() {
        // 获得屏幕宽度
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWitdh = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    /**
     * 往左移动
     *
     * @param position 键盘的位置 0为上; 1为下
     * @param value    移动的位移大小
     */
    private void leftMove(int position, int value) {
        if (position == 0) {//上
            mCurrentPageIndex -= value;
            L.v("piano", "----------leftMove---上---mCurrentPageIndex--" + mCurrentPageIndex + ";  mCurrentPageIndex2--" + mCurrentPageIndex2);
            mCurrentPageIndex = scrollBar.setProgress(mCurrentPageIndex);
        } else if (position == 1) {//下
            mCurrentPageIndex2 -= value;
            L.v("piano", "----------leftMove---下---mCurrentPageIndex--" + mCurrentPageIndex + ";  mCurrentPageIndex2--" + mCurrentPageIndex2);
            mCurrentPageIndex2 = scrollBar2.setProgress(mCurrentPageIndex2);
        }

    }

    /**
     * 往右移动
     *
     * @param position 键盘的位置 0为上; 1为下
     * @param value    移动的位移大小
     */
    private void rightMove(int position, int value) {
        if (position == 0) {//上
            mCurrentPageIndex += value;
            L.v("piano", "----------rightMove---上---mCurrentPageIndex--" + mCurrentPageIndex + ";  mCurrentPageIndex2--" + mCurrentPageIndex2);
            mCurrentPageIndex = scrollBar.setProgress(mCurrentPageIndex);
        } else if (position == 1) {//下
            mCurrentPageIndex2 += value;
            L.v("piano", "----------rightMove----下--mCurrentPageIndex--" + mCurrentPageIndex + ";  mCurrentPageIndex2--" + mCurrentPageIndex2);
            mCurrentPageIndex2 = scrollBar2.setProgress(mCurrentPageIndex2);
        }
    }


    /**
     * Toast提示
     */
    private void showNotReadyToast() {
        Toast.makeText(PianoActivity.this, R.string.not_ready_toast, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示或隐藏控制区
     */
    private void hideOrDisplayControl() {

        if (isHideControl) {//选中了隐藏控制栏
            SavePreferencesUtils.getInstance(getApplicationContext()).put(SettingActivity.IS_CONTROL_HIDE_RADION_GROUP, false);
            fistDesktopBottom.setVisibility(View.GONE);
            secondDesktopBottom.setVisibility(View.GONE);
            fistKeyLinearLayout.requestLayout();
            ivControlHide.setImageResource(R.drawable.piano_control_hide);
        } else {//选中了显示控制栏
            SavePreferencesUtils.getInstance(getApplicationContext()).put(SettingActivity.IS_CONTROL_HIDE_RADION_GROUP, true);
            fistDesktopBottom.setVisibility(View.VISIBLE);
            secondDesktopBottom.setVisibility(View.VISIBLE);
            ivControlHide.setImageResource(R.drawable.piano_control_display);
        }
    }

    /**
     * @param key_index
     */
    public void refreshAllKeys(int key_index) {
        for (int i = 0; i < keyNums; i++) {
            SoundKey key = whiteKeys[i];
            key.setIndex(true, key_index + i);
        }
        for (int i = 0; i < keyNums - 1; i++) {
            SoundKey key = blackKeys[i];
            key.setIndex(false, key_index + i);
        }
    }

    private void refreshAllKeysBounds() {
        for (int i = 0; i < keyNums; i++) {
            SoundKey key = whiteKeys[i];
            key.refreshBounds();
        }

        for (int i = 0; i < keyNums - 1; i++) {
            SoundKey key = blackKeys[i];
            key.refreshBounds();
        }

    }

    /****第二排键 start ******************************************************************/
    private void refreshAllKeys2(int key_index) {
        for (int i = 0; i < keyNums; i++) {
            SoundKey key = whiteKeys2[i];
            key.setIndex(true, key_index + i);
        }
        for (int i = 0; i < keyNums - 1; i++) {
            SoundKey key = blackKeys2[i];
            key.setIndex(false, key_index + i);
        }
    }

    private void refreshAllKeysBounds2() {
        for (int i = 0; i < keyNums; i++) {
            SoundKey key = whiteKeys2[i];
            key.refreshBounds();
        }
        for (int i = 0; i < keyNums - 1; i++) {
            SoundKey key = blackKeys2[i];
            key.refreshBounds();
        }
    }

    /****第二排键 end ******************************************************************/


    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {


        switch (paramMotionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                L.i("piano", "onTouchEvent ACTION_DOWN");
                llPianoRecordTip.setVisibility(View.GONE);
                break;
            case MotionEvent.ACTION_MOVE:
                L.i("piano", "onTouchEvent ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                L.i("piano", "onTouchEvent ACTION_UP");


                break;
            default:
                L.i("piano", "default");

        }


        for (int j = 0; j < keyNums; j++) {
            whiteKeys[j].setNewPressingStatus(false);
        }
        for (int j = 0; j < keyNums - 1; j++) {
            blackKeys[j].setNewPressingStatus(false);
        }


        int up_index = -1;
        int up_index2 = -1;
        if (paramMotionEvent.getActionMasked() == MotionEvent.ACTION_UP
                || paramMotionEvent.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            up_index = paramMotionEvent.getActionIndex();
            up_index2 = paramMotionEvent.getActionIndex();
        }
        /********* 第一排 ***********/
        int pointer_num = paramMotionEvent.getPointerCount();
        L.i("piano", "pointer_num=" + pointer_num);
        for (int i = 0; i < pointer_num; i++) {
            float x = paramMotionEvent.getX(i);
            float y = paramMotionEvent.getY(i);

            L.v("piano", "---onTouchEvent--第一排---- x:" + x + "; y:" + y + "; width:" + fistWhiteKeys.getWidth() + "; hight:" + fistWhiteKeys.getHeight() + "; mScreenHeight:" + mScreenHeight);

            int index = (int) (x * keyNums / fistWhiteKeys.getWidth());
            if (index < 0)
                index = 0;
            if (index > keyNums - 1)
                index = keyNums - 1;

            int black_index = -1;
            if (index < keyNums - 1 && blackKeys[index].isInsideKey(x, y))
                black_index = index;
            else {
                if (index > 0)
                    if (blackKeys[index - 1].isInsideKey(x, y))
                        black_index = index - 1;
            }

            if (up_index != i) {
                if (black_index != -1)
                    blackKeys[black_index].setNewPressingStatus(true);
                else {
                    if (whiteKeys[index].isInsideKey(x, y))
                        whiteKeys[index].setNewPressingStatus(true);
                }
            } else {
                if (black_index != -1)
                    blackKeys[black_index].setNewPressingStatus(false);
                else {
                    if (whiteKeys[index].isInsideKey(x, y))
                        whiteKeys[index].setNewPressingStatus(false);
                }
            }
        }

        for (int k = 0; k < keyNums; k++) {
            if (whiteKeys[k].getNewPressingStatus()) {
                if (!whiteKeys[k].getPressingStatus()) {
//                    whiteKeys[k].setPressingStatus(true);
                    whiteKeys[k].setPressingStatus(true, true);
                }
            } else {
                if (whiteKeys[k].getPressingStatus()) {
//                    whiteKeys[k].setPressingStatus(false);
                    whiteKeys[k].setPressingStatus(false, true);
                }
            }
        }
        for (int k = 0; k < keyNums - 1; k++) {
            if (blackKeys[k].getNewPressingStatus()) {
                if (!blackKeys[k].getPressingStatus()) {
//                    blackKeys[k].setPressingStatus(true);
                    blackKeys[k].setPressingStatus(true, true);
                }
            } else {
                if (blackKeys[k].getPressingStatus()) {
//                    blackKeys[k].setPressingStatus(false);
                    blackKeys[k].setPressingStatus(false, true);
                }
            }
        }

/********* 第二排 start ************************************************/
        for (int j = 0; j < keyNums; j++) {
            whiteKeys2[j].setNewPressingStatus(false);
        }
        for (int j = 0; j < keyNums - 1; j++) {
            blackKeys2[j].setNewPressingStatus(false);
        }

        int pointer_num2 = paramMotionEvent.getPointerCount();
        for (int i = 0; i < pointer_num2; i++) {
            float x = paramMotionEvent.getX(i);
            float y = paramMotionEvent.getY(i);

            L.v("piano", "---onTouchEvent--第二排--- x:" + x + "; y:" + y + "; width:" + fistWhiteKeys.getWidth() + "; hight:" + fistWhiteKeys.getHeight() + "; mScreenHeight:" + mScreenHeight);

            int index = (int) (x * keyNums / fistWhiteKeys2.getWidth());
            if (index < 0)
                index = 0;
            if (index > keyNums - 1)
                index = keyNums - 1;

            int black_index = -1;
            if (index < keyNums - 1 && blackKeys2[index].isInsideKey(x, y))
                black_index = index;
            else {
                if (index > 0)
                    if (blackKeys2[index - 1].isInsideKey(x, y))
                        black_index = index - 1;
            }

            if (up_index2 != i) {
                if (black_index != -1)
                    blackKeys2[black_index].setNewPressingStatus(true);
                else {
                    if (whiteKeys2[index].isInsideKey(x, y))
                        whiteKeys2[index].setNewPressingStatus(true);
                }
            } else {
                if (black_index != -1)
                    blackKeys2[black_index].setNewPressingStatus(false);
                else {
                    if (whiteKeys2[index].isInsideKey(x, y))
                        whiteKeys2[index].setNewPressingStatus(false);
                }
            }
        }

        for (int k = 0; k < keyNums; k++) {
            if (whiteKeys2[k].getNewPressingStatus()) {
                if (!whiteKeys2[k].getPressingStatus()) {
//                    whiteKeys2[k].setPressingStatus(true);
                    whiteKeys2[k].setPressingStatus(true, false);
                }
            } else {
                if (whiteKeys2[k].getPressingStatus()) {
//                    whiteKeys2[k].setPressingStatus(false);
                    whiteKeys2[k].setPressingStatus(false, false);
                }
            }
        }
        for (int k = 0; k < keyNums - 1; k++) {
            if (blackKeys2[k].getNewPressingStatus()) {
                if (!blackKeys2[k].getPressingStatus()) {
//                    blackKeys2[k].setPressingStatus(true);
                    blackKeys2[k].setPressingStatus(true, false);
                }
            } else {
                if (blackKeys2[k].getPressingStatus()) {
//                    blackKeys2[k].setPressingStatus(false);
                    blackKeys2[k].setPressingStatus(false, false);
                }
            }
        }
/********* 第二排 end  ************************************************/

        return true;
//        return super.onTouchEvent(paramMotionEvent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUSET && resultCode == SettingActivity.RESULT_OK_ABC) {
            PianoKeyManager.setKeyboard_string_type(0);//当为0的时候变成A,B,C
            for (int i = 0; i < whiteKeys.length; i++)
                whiteKeys[i].updateWhiteKeyText();
            for (int i = 0; i < whiteKeys2.length; i++)
                whiteKeys2[i].updateWhiteKeyText();
        } else if (requestCode == REQUSET && resultCode == SettingActivity.RESULT_OK_DORIMI) {
            PianoKeyManager.setKeyboard_string_type(1);//当为1的时候变成do,ri,mi
            for (int i = 0; i < whiteKeys.length; i++)
                whiteKeys[i].updateWhiteKeyText();
            for (int i = 0; i < whiteKeys2.length; i++)
                whiteKeys2[i].updateWhiteKeyText();
        } else if (requestCode == REQUSET && resultCode == SettingActivity.RESULT_OK_CONTROL_HIDE) {
            fistDesktopBottom.setVisibility(View.GONE);
            secondDesktopBottom.setVisibility(View.GONE);
        } else if (requestCode == REQUSET && resultCode == SettingActivity.RESULT_OK_CONTROL_SHOW) {
            fistDesktopBottom.setVisibility(View.VISIBLE);
            secondDesktopBottom.setVisibility(View.VISIBLE);

        } else if (requestCode == REQUSET && resultCode == SettingActivity.RESULT_OK_KEYBOARD_NUM) {
            L.v("piano", "-------onActivityResult---键盘数量----" + SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(SettingActivity.KEYBOARD_NUM, "10"));
            L.v("piano", "-------onActivityResult---键盘数量-=====---" + data.getIntExtra(SettingActivity.KEYBOARD_NUM, 10));
            initViewState(data.getIntExtra(SettingActivity.KEYBOARD_NUM, 10));
        } else if (requestCode == REQUSET && resultCode == SettingActivity.RESULT_OK_DOUBLE_KEYBOARD_NO_CONNECTION) {//双键盘没有关联
            isDoubleKeyboardConnection = true;
        } else if (requestCode == REQUSET && resultCode == SettingActivity.RESULT_OK_DOUBLE_KEYBOARD_YES_CONNECTION) {//双键盘有关联
            isDoubleKeyboardConnection = false;
        } else if (requestCode == REQUSET && resultCode == FileSelectActivity.RESULT_SELECT_FILE) {
            ivReplay.performClick();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        L.v("Piano", "onResume");
//        mCurrentPageIndex2 = mCurrentPageIndex + keyNums;
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.v("Piano", "onPause");
        PianoKeyManager.setKeyboard_from_index(mCurrentPageIndex);
        PianoKeyManager.saveSettings(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        L.v("Piano", "onStart");
        FlurryAgent.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.v("Piano", "onStop");
        FlurryAgent.onEndSession(this);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void play(MotionEvent paramMotionEvent) {

        for (int j = 0; j < keyNums; j++) {
            whiteKeys[j].setNewPressingStatus(false);
        }
        for (int j = 0; j < keyNums - 1; j++) {
            blackKeys[j].setNewPressingStatus(false);
        }


        int up_index = -1;
//        int up_index2 = -1;
        if (paramMotionEvent.getActionMasked() == MotionEvent.ACTION_UP
                || paramMotionEvent.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            up_index = paramMotionEvent.getActionIndex();
//            up_index2 = paramMotionEvent.getActionIndex();
        }
        /********* 第一排 ***********/
        int pointer_num = paramMotionEvent.getPointerCount();
        L.i("piano", "pointer_num=" + pointer_num);
        for (int i = 0; i < pointer_num; i++) {

            float x = paramMotionEvent.getX(i);
            float y = paramMotionEvent.getY(i) + fistDesktop.getHeight();

            L.v("piano", "---play--第一排--- x:" + x + "; y:" + y + "; width:" + fistWhiteKeys.getWidth() + "; hight:" + fistWhiteKeys.getHeight() + "; mScreenHeight:" + mScreenHeight);

            int index = (int) (x * keyNums / fistWhiteKeys.getWidth());
            if (index < 0)
                index = 0;
            if (index > keyNums - 1)
                index = keyNums - 1;

            int black_index = -1;
            if (index < keyNums - 1 && blackKeys[index].isInsideKey(x, y))
                black_index = index;
            else {
                if (index > 0)
                    if (blackKeys[index - 1].isInsideKey(x, y))
                        black_index = index - 1;
            }

            if (up_index != i) {
                if (black_index != -1)
                    blackKeys[black_index].setNewPressingStatus(true);
                else {
                    if (whiteKeys[index].isInsideKey(x, y))
                        whiteKeys[index].setNewPressingStatus(true);
                }
            } else {
                if (black_index != -1)
                    blackKeys[black_index].setNewPressingStatus(false);
                else {
                    if (whiteKeys[index].isInsideKey(x, y))
                        whiteKeys[index].setNewPressingStatus(false);
                }
            }
        }

        for (int k = 0; k < keyNums; k++) {
            if (whiteKeys[k].getNewPressingStatus()) {
                if (!whiteKeys[k].getPressingStatus()) {
                    whiteKeys[k].setPressingStatus(true, true);
                }
            } else {
                if (whiteKeys[k].getPressingStatus()) {
                    whiteKeys[k].setPressingStatus(false, true);
                }
            }
        }
        for (int k = 0; k < keyNums - 1; k++) {
            if (blackKeys[k].getNewPressingStatus()) {
                if (!blackKeys[k].getPressingStatus()) {
                    blackKeys[k].setPressingStatus(true, true);
                }
            } else {
                if (blackKeys[k].getPressingStatus()) {
                    blackKeys[k].setPressingStatus(false, true);
                }
            }
        }


    }


    @TargetApi(Build.VERSION_CODES.FROYO)
    private void play2(MotionEvent paramMotionEvent) {
        /********* 第二排 start ************************************************/
        for (int j = 0; j < keyNums; j++) {
            whiteKeys2[j].setNewPressingStatus(false);
        }
        for (int j = 0; j < keyNums - 1; j++) {
            blackKeys2[j].setNewPressingStatus(false);
        }

//        int up_index = -1;
        int up_index2 = -1;
        if (paramMotionEvent.getActionMasked() == MotionEvent.ACTION_UP
                || paramMotionEvent.getActionMasked() == MotionEvent.ACTION_CANCEL) {
//            up_index = paramMotionEvent.getActionIndex();
            up_index2 = paramMotionEvent.getActionIndex();
        }

        int pointer_num2 = paramMotionEvent.getPointerCount();
        for (int i = 0; i < pointer_num2; i++) {

            float x = paramMotionEvent.getX(i);
            float y = paramMotionEvent.getY(i) + fistKeyLinearLayout.getHeight() + secondDesktopBottom.getHeight();
            if (isHideControl) {//隐藏控制栏
                y = paramMotionEvent.getY(i) + fistKeyLinearLayout.getHeight();
            }

            L.v("piano", "---play2---第二排----- x:" + x + "; y:" + y + "; width:" + fistWhiteKeys2.getWidth() + "; hight:" + fistWhiteKeys2.getHeight() + "; mScreenHeight:" + mScreenHeight);

            int index = (int) (x * keyNums / fistWhiteKeys2.getWidth());
            if (index < 0)
                index = 0;
            if (index > keyNums - 1)
                index = keyNums - 1;

            int black_index = -1;
            if (index < keyNums - 1 && blackKeys2[index].isInsideKey(x, y))
                black_index = index;
            else {
                if (index > 0)
                    if (blackKeys2[index - 1].isInsideKey(x, y))
                        black_index = index - 1;
            }

            if (up_index2 != i) {
                if (black_index != -1)
                    blackKeys2[black_index].setNewPressingStatus(true);
                else {
                    if (whiteKeys2[index].isInsideKey(x, y))
                        whiteKeys2[index].setNewPressingStatus(true);
                }
            } else {
                if (black_index != -1)
                    blackKeys2[black_index].setNewPressingStatus(false);
                else {
                    if (whiteKeys2[index].isInsideKey(x, y))
                        whiteKeys2[index].setNewPressingStatus(false);
                }
            }
        }

        for (int k = 0; k < keyNums; k++) {
            if (whiteKeys2[k].getNewPressingStatus()) {
                if (!whiteKeys2[k].getPressingStatus()) {
                    whiteKeys2[k].setPressingStatus(true, false);
                }
            } else {
                if (whiteKeys2[k].getPressingStatus()) {
                    whiteKeys2[k].setPressingStatus(false, false);
                }
            }
        }
        for (int k = 0; k < keyNums - 1; k++) {
            if (blackKeys2[k].getNewPressingStatus()) {
                if (!blackKeys2[k].getPressingStatus()) {
                    blackKeys2[k].setPressingStatus(true, false);
                }
            } else {
                if (blackKeys2[k].getPressingStatus()) {
                    blackKeys2[k].setPressingStatus(false, false);
                }
            }
        }
    }


    public class PlayHandler extends Handler {


        private PlayHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            RecordItem item = sound_list.get(msg.arg1);

            L.v("midi", "++++++++" + msg.arg1);
            if (item.getPiano_key_action() == 0 ? true : false) {
                if (item.isPiano_key_is_white()) {


                    PianoKeyManager.startPlayKey(PianoActivity.this, true, item.getPiano_key_index());
                } else {

                    PianoKeyManager.startPlayKey(PianoActivity.this, false, item.getPiano_key_index());
                }
            } else {
                if (item.isPiano_key_is_white()) {
                    PianoKeyManager.stopPlayKey(PianoActivity.this, true, item.getPiano_key_index());
                } else {

                    PianoKeyManager.stopPlayKey(PianoActivity.this, false, item.getPiano_key_index());
                }
            }

        }


    }

    public void replay() {


        if (!mReplaying) {
            //开始播放
            FlurryAgent.logEvent(Constants.ACT_PIANO_PLAY);
            RecordManager.startReplay(PianoActivity.this, playHandler, new RecordEvent() {
                @Override
                public void RecordEventComing(RecordItem item) {
                    if (item != null) {//有录制的音乐

                        if (item.getPiano_position()) {//上键盘

                            if (item.isPiano_key_is_white()) {//白键
                                if (item.getPiano_key_index() < mCurrentPageIndex
                                        || item.getPiano_key_index() >= mCurrentPageIndex + 10) {
                                    mCurrentPageIndex = item.getPiano_key_index();
                                    mCurrentPageIndex = scrollBar.setProgress(mCurrentPageIndex);
                                    refreshAllKeys(mCurrentPageIndex);
                                }
                                whiteKeys[item.getPiano_key_index() - mCurrentPageIndex].setPressingStatus(item.getPiano_key_action() == 0 ? true : false, true);
                            } else {//黑键
                                if (item.getPiano_key_index() < mCurrentPageIndex
                                        || item.getPiano_key_index() >= mCurrentPageIndex + 9) {
                                    mCurrentPageIndex = item.getPiano_key_index();
                                    mCurrentPageIndex = scrollBar.setProgress(mCurrentPageIndex);
                                    refreshAllKeys(mCurrentPageIndex);
                                }
                                blackKeys[item.getPiano_key_index() - mCurrentPageIndex].setPressingStatus(item.getPiano_key_action() == 0 ? true : false, true);
                            }

                        } else {//下键盘

                            if (item.isPiano_key_is_white()) {//白键
                                if (item.getPiano_key_index() < mCurrentPageIndex2
                                        || item.getPiano_key_index() >= mCurrentPageIndex2 + 10) {
                                    mCurrentPageIndex2 = item.getPiano_key_index();
                                    mCurrentPageIndex2 = scrollBar2.setProgress(mCurrentPageIndex2);
                                    refreshAllKeys2(mCurrentPageIndex2);
                                }
                                whiteKeys2[item.getPiano_key_index() - mCurrentPageIndex2].setPressingStatus(item.getPiano_key_action() == 0 ? true : false, false);
                            } else {//黑键
                                if (item.getPiano_key_index() < mCurrentPageIndex2
                                        || item.getPiano_key_index() >= mCurrentPageIndex2 + 9) {
                                    mCurrentPageIndex2 = item.getPiano_key_index();
                                    mCurrentPageIndex2 = scrollBar2.setProgress(mCurrentPageIndex2);
                                    refreshAllKeys2(mCurrentPageIndex2);
                                }
                                blackKeys2[item.getPiano_key_index() - mCurrentPageIndex2].setPressingStatus(item.getPiano_key_action() == 0 ? true : false, false);
                            }

                        }

                    } else {//没有录制的音乐
                        RecordManager.stopReplay();
                        mReplaying = false;
                        ivReplay.setImageResource(R.drawable.piano_record_play);
                        ivStartRecord.setImageResource(R.drawable.piano_record);
                        ivStartRecord.setEnabled(true);
                    }
                }
            });
            mReplaying = true;
            ivReplay.setImageResource(R.drawable.piano_music_play);
            ivStartRecord.setImageResource(R.drawable.piano_unrecord);
            ivStartRecord.setEnabled(false);

        } else {//停止播放
            RecordManager.stopReplay();
            mReplaying = false;
            ivReplay.setImageResource(R.drawable.piano_record_play);
            ivStartRecord.setImageResource(R.drawable.piano_record);
            ivStartRecord.setEnabled(true);
        }


    }

    /**
     * 初始化创建桌面快捷方式
     */
    private void initDeskShortcut() {
        L.v("piano", "------------initDeskShortcut--------------");
        isFist = SavePreferencesUtils.getInstance(getApplicationContext()).sp.getBoolean("isfrist", true);
        if (isFist) {//如果第一次运行
            creatDeskShortcut();
            L.v("piano", "-----------如果第一次运行---------------");
        }
        SavePreferencesUtils.getInstance(getApplicationContext()).put("isfrist", false);
    }

    /**
     * 创建桌面快捷方式
     */
    public void creatDeskShortcut() {
        //创建快捷方式的Intent
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        //需要现实的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));
        //快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(
                getApplicationContext(), R.mipmap.icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        Intent intent = new Intent(getApplicationContext(), PianoActivity.class);
        //下面两个属性是为了当应用程序卸载时桌面 上的快捷方式会删除
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        //点击快捷图片，运行的程序主入口
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        //发送广播。OK
        sendBroadcast(shortcutIntent);
    }

    private void promoteMusicPiano() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What'new").setMessage("Congraturation ！You get a free experience for Music Piano, Do you want it?")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = "com.tbs.piano"; // getPackageName() from Context or Activity object
                        Intent intent = null;
                        try {
                            intent = new Intent();
                            intent.setClassName(appPackageName,"com.fotoable.piano.activity.GuideActivity");
                            startActivity(intent);

                        }catch (Exception e){
                            e.printStackTrace();

                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
