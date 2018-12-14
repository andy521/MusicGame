package piano.tiles.music.keyboard.song.am;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytwd.midiengine.utils.SavePreferencesUtils;

/**
 * Created by jiatao on 16/5/14.
 */
public class SettingActivity extends Activity{

    private ImageButton backImageButton;//返回按钮
    private RadioGroup ABCRadioGroup;
    private RadioGroup DoRiRadioGroup;
    private RadioGroup DoubleKeyboardStaus;

    private RelativeLayout keyboardNumButton;//按键数量
    private LinearLayout keyboardNums;//键盘数量显示菜单
    private ImageView controlArrow;//控制箭头
    private LinearLayout doubleKeyboardConnectionState;

    private RadioButton ABCButtonRadioButton;//转换成ABC选中栏
    private RadioButton DoRiButtonRadioButton;//转换成Do,Ri,Mi选中栏
    private RadioButton controlHideRadioButton;//键盘控制区隐藏选中栏
    private RadioButton controlShowRadioButton;//键盘控制区显示选中栏
    private RadioButton noConnectionRadioButton;//当为双键盘时两个键盘相互独立
    private RadioButton yesConnectionRadioButton;//当为双键盘时两个键盘相互连接

    private RadioGroup keyNumRadioGroup;
    private RadioButton keyNumRadioButton10;
    private RadioButton keyNumRadioButton08;
    private RadioButton keyNumRadioButton06;

    private ImageView keyNumAdd;
    private ImageView keyNumSub;
    private TextView keyNumValue;
    private int keyNums = 10;//键盘上按键的数量

    public static final String IS_ABC_RADION_GROUP = "IS_ABC_RADION_GROUP";
    public static final String IS_CONTROL_HIDE_RADION_GROUP = "IS_CONTROL_HIDE_RADION_GROUP";
    public static final String KEYBOARD_NUM = "KEYBOARD_NUM";//按键个数
    public static final String IS_DOUBLE_KEYBOARD_CONNECTION = "IS_DOUBLE_KEYBOARD_CONNECTION";

    private boolean isShowItem = false;//是否出现键盘的数量菜单,false:没有,true:有

    public static final int RESULT_OK_ABC = 1;//转换成A,B,C事件标示
    public static final int RESULT_OK_DORIMI = 2;//转换成Do,Ri,Mi事件标示
    public static final int RESULT_OK_CONTROL_HIDE = 3;//隐藏控制栏事件标示
    public static final int RESULT_OK_CONTROL_SHOW = 4;//显示控制栏事件标示
    public static final int RESULT_OK_KEYBOARD_NUM= 5;
    public static final int RESULT_OK_DOUBLE_KEYBOARD_NO_CONNECTION = 6;//双键盘没关联标示
    public static final int RESULT_OK_DOUBLE_KEYBOARD_YES_CONNECTION = 7;//双键盘有关联标示

    private boolean isSingleKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.setting_activity);

        Intent intent = getIntent();
        isSingleKey = intent.getBooleanExtra("isSingleKey",true);
        L.d("piano","------------------" + isSingleKey);
        /** 获取viewid */
        findViews();

        initRadioButton();

        initViewState();

        /** 监听事件 */
        listeners();

        setViewState();

    }

    /**
     * 初始化view
     */
    private void findViews() {
        backImageButton = (ImageButton)findViewById(R.id.about_icon_back);
        keyboardNumButton = (RelativeLayout)findViewById(R.id.rl_piano_setting_keyboard_num_control_button);
        keyboardNums = (LinearLayout) findViewById(R.id.ll_piano_setting_keyboard_num);
        controlArrow = (ImageView)findViewById(R.id.iv_piano_setting_keyboard_num_control_arrow_button);

        doubleKeyboardConnectionState = (LinearLayout) findViewById(R.id.ll_setting_double_keyboard_connection);

        ABCRadioGroup = (RadioGroup) findViewById(R.id.rg_piano_setting_ABC_button);
        DoRiRadioGroup = (RadioGroup) findViewById(R.id.rg_piano_setting_hide_control_button);
        DoubleKeyboardStaus = (RadioGroup) findViewById(R.id.rg_piano_setting_double_keyboard_staus);

        ABCButtonRadioButton = (RadioButton) findViewById(R.id.piano_setting_ABC_radio_button);
        DoRiButtonRadioButton= (RadioButton) findViewById(R.id.piano_setting_Do_Re_Mi_radio_button);
        controlHideRadioButton = (RadioButton) findViewById(R.id.piano_setting_hide_control_radio_button);
        controlShowRadioButton = (RadioButton) findViewById(R.id.piano_setting_show_control_radio_button);
        noConnectionRadioButton = (RadioButton) findViewById(R.id.piano_setting_no_connection_radio_button);
        yesConnectionRadioButton = (RadioButton) findViewById(R.id.piano_setting_yes_connection_radio_button);

        keyNumRadioGroup = (RadioGroup) findViewById(R.id.piano_setting_key_num_radio_group);
        keyNumRadioButton10 = (RadioButton) findViewById(R.id.piano_setting_key_num_radio_button10);
        keyNumRadioButton08 = (RadioButton) findViewById(R.id.piano_setting_key_num_radio_button08);
        keyNumRadioButton06 = (RadioButton) findViewById(R.id.piano_setting_key_num_radio_button06);

        keyNumAdd = (ImageView) findViewById(R.id.tv_number_of_keys_add);
        keyNumSub = (ImageView) findViewById(R.id.tv_number_of_keys_sub);
        keyNumValue = (TextView) findViewById(R.id.tv_number_of_keys_value);
    }

    /**
     * 监听事件
     */
    private void listeners() {

        /** 返回键监听 */
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SavePreferencesUtils.getInstance(getApplicationContext()).put(KEYBOARD_NUM,keyNums + "");
                Intent intent = new Intent();
                intent.putExtra(KEYBOARD_NUM, keyNums);
                setResult(RESULT_OK_KEYBOARD_NUM, intent);

                finish();
            }
        });

        /** 是否是ABC选中监听 */
        ABCRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == ABCButtonRadioButton.getId()) {//选中了ABC
                    SavePreferencesUtils.getInstance(getApplicationContext()).put(IS_ABC_RADION_GROUP,true);
                    Intent intent = new Intent();
                    setResult(RESULT_OK_ABC, intent);
                }else {//选中了Do,Ri
                    SavePreferencesUtils.getInstance(getApplicationContext()).put(IS_ABC_RADION_GROUP,false);
                    Intent intent = new Intent();
//                    intent.putExtra(IS_DO_RI_RADION_GROUP, true);
                    setResult(RESULT_OK_DORIMI, intent);

                }
                finish();
            }
        });

        /** 控制栏是否显示监听 */
        DoRiRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == controlHideRadioButton.getId()) {//选中了隐藏控制栏
                    SavePreferencesUtils.getInstance(getApplicationContext()).put(IS_CONTROL_HIDE_RADION_GROUP,true);
                    Intent intent = new Intent();
                    setResult(RESULT_OK_CONTROL_HIDE, intent);
                }else {//选中了显示控制栏
                    SavePreferencesUtils.getInstance(getApplicationContext()).put(IS_CONTROL_HIDE_RADION_GROUP,false);
                    Intent intent = new Intent();
                    setResult(RESULT_OK_CONTROL_SHOW, intent);
                }
                finish();
            }
        });

        /** 双键盘键盘关联控制监听 */
        DoubleKeyboardStaus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                L.v("piano","----------DoubleKeyboardStaus---------");
                if (i == noConnectionRadioButton.getId()){//双键盘没有关联
                    L.v("piano","----------双键盘没有关联---------");
                    SavePreferencesUtils.getInstance(getApplicationContext()).put(IS_DOUBLE_KEYBOARD_CONNECTION,true);//没关联为true
                    Intent intent = new Intent();
                    setResult(RESULT_OK_DOUBLE_KEYBOARD_NO_CONNECTION, intent);
                }else {//双键盘有关联
                    L.v("piano","----------双键盘有关联---------");
                    SavePreferencesUtils.getInstance(getApplicationContext()).put(IS_DOUBLE_KEYBOARD_CONNECTION,false);//有关联为false
                    Intent intent = new Intent();
                    setResult(RESULT_OK_DOUBLE_KEYBOARD_YES_CONNECTION, intent);
                }
                finish();
            }
        });

        /**按键数量加监听*/
        keyNumAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.v("piano","-----keyNumAdd-------" + keyNumValue.getText()+";  keyNums" + keyNums);

                if (keyNums == 16) {
                    L.v("piano","-----keyNumAdd--===--" + keyNums);
                }else {
                    keyNums += 2;
                    keyNumValue.setText(String.valueOf(keyNums));
                    L.v("piano","----keyNumAdd-----keyNums--" + keyNums);
                }
            }
        });

        /**按键数量减监听*/
        keyNumSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.v("piano","---keyNumSub-------");

                if (keyNums == 6) {
                    L.v("piano","-----keyNumSub-====---" + keyNums);
                }else {
                    keyNums -= 2;
                    keyNumValue.setText(String.valueOf(keyNums));
                    L.v("piano","-----keyNumSub----keyNums--" + keyNums);
                }
            }
        });


//        /** 键盘数量控制展示监听 */
//        keyboardNumButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!isShowItem){
//                    isShowItem = true;
//                    controlArrow.setImageResource(R.drawable.piano_icon_arrow_right_normal);
//                    keyboardNums.setVisibility(View.GONE);
//                }else {
//                    isShowItem = false;
//                    controlArrow.setImageResource(R.drawable.piano_icon_arrow_down_normal);
//                    keyboardNums.setVisibility(View.VISIBLE);
//                }
//
//            }
//        });


//        /** 按键数量控制 */
//        keyNumRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                if (i == keyNumRadioButton10.getId()){//10个按键
//                    keyNumRadioButton10.setChecked(true);
//                    SavePreferencesUtils.getInstance(getApplicationContext()).put(KEYBOARD_NUM,"10");
//                    Intent intent = new Intent();
//                    intent.putExtra(KEYBOARD_NUM,10);
//                    setResult(RESULT_OK_KEYBOARD_NUM, intent);
//
//                }else if (i == keyNumRadioButton08.getId()) {//8个按键
//                    keyNumRadioButton08.setChecked(true);
//                    SavePreferencesUtils.getInstance(getApplicationContext()).put(KEYBOARD_NUM,"8");
//                    Intent intent = new Intent();
//                    intent.putExtra(KEYBOARD_NUM,8);
//                    setResult(RESULT_OK_KEYBOARD_NUM, intent);
//
//                }else if (i == keyNumRadioButton06.getId()) {//6个按键
//                    keyNumRadioButton06.setChecked(true);
//                    SavePreferencesUtils.getInstance(getApplicationContext()).put(KEYBOARD_NUM,"6");
//                    Intent intent = new Intent();
//                    intent.putExtra(KEYBOARD_NUM,6);
//                    setResult(RESULT_OK_KEYBOARD_NUM, intent);
//
//                }
//
//                finish();
//            }
//        });


    }

    private void initViewState() {
        keyNums = Integer.parseInt(SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(KEYBOARD_NUM,"10"));
        keyNumValue.setText(SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(KEYBOARD_NUM,"10"));
    }

    /**
     * ABC等设置的状态
     */
    private void initRadioButton() {
        /**初始化选中的是哪个标号*/
        Drawable draw1= getResources().getDrawable(R.drawable.piano_setting_choose);
        Drawable draw2= getResources().getDrawable(R.drawable.piano_setting_choose_none);
        if (SavePreferencesUtils.getInstance(getApplicationContext()).sp.getBoolean(IS_ABC_RADION_GROUP,true)){//如果选中的是ABC
            ABCButtonRadioButton.setChecked(true);
            ABCButtonRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, draw1, null);
            DoRiButtonRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
        }else {//选中Do,Ri,Mi
            DoRiButtonRadioButton.setChecked(true);
            ABCButtonRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
            DoRiButtonRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, draw1, null);
        }
        /**状态栏是隐藏还是显示*/
        if (SavePreferencesUtils.getInstance(getApplicationContext()).sp.getBoolean(IS_CONTROL_HIDE_RADION_GROUP,false)){//隐藏
            controlHideRadioButton.setChecked(true);
        }else {//显示
            controlShowRadioButton.setChecked(true);
        }
        /**双键盘情况下两个键盘是否有关联*/
        if (SavePreferencesUtils.getInstance(getApplicationContext()).sp.getBoolean(IS_DOUBLE_KEYBOARD_CONNECTION,false)){//没关联
            L.v("piano","==============================");
            noConnectionRadioButton.setChecked(true);
            noConnectionRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, draw1, null);
            yesConnectionRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
        }else {//有关联
            yesConnectionRadioButton.setChecked(true);
            noConnectionRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
            yesConnectionRadioButton.setCompoundDrawablesWithIntrinsicBounds(null, null, draw1, null);
        }
        /**键盘显示多少个按键*/
        if (Integer.parseInt(SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(SettingActivity.KEYBOARD_NUM, "10"))==10) {//10个键
            keyNumRadioButton10.setChecked(true);
            keyNumRadioButton10.setCompoundDrawablesWithIntrinsicBounds(null, null, draw1, null);
            keyNumRadioButton08.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
            keyNumRadioButton06.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
        }else if (Integer.parseInt(SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(SettingActivity.KEYBOARD_NUM, "10"))==8) {//8个键
            keyNumRadioButton08.setChecked(true);
            keyNumRadioButton10.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
            keyNumRadioButton08.setCompoundDrawablesWithIntrinsicBounds(null, null, draw1, null);
            keyNumRadioButton06.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
        }else if (Integer.parseInt(SavePreferencesUtils.getInstance(getApplicationContext()).sp.getString(SettingActivity.KEYBOARD_NUM, "10"))==6) {//6个键
            keyNumRadioButton06.setChecked(true);
            keyNumRadioButton10.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
            keyNumRadioButton08.setCompoundDrawablesWithIntrinsicBounds(null, null, draw2, null);
            keyNumRadioButton06.setCompoundDrawablesWithIntrinsicBounds(null, null, draw1, null);
        }

    }

    /**
     * 设置view的状态
     */
    private void setViewState() {
        if (!isSingleKey) {//双排键
            doubleKeyboardConnectionState.setVisibility(View.VISIBLE);
        }else {//单排键
            doubleKeyboardConnectionState.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        SavePreferencesUtils.getInstance(getApplicationContext()).put(KEYBOARD_NUM,keyNums + "");
        Intent intent = new Intent();
        intent.putExtra(KEYBOARD_NUM, keyNums);
        setResult(RESULT_OK_KEYBOARD_NUM, intent);

        finish();

        return super.onKeyDown(keyCode, event);
    }
}
