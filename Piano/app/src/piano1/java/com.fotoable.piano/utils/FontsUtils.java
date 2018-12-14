package com.fotoable.piano.utils;

import android.content.Context;
import android.graphics.Typeface;


/**
 * Created by fotoable on 2017/8/8.
 */

public class FontsUtils {
    public static Typeface typeAller, typeArial, typeSan;

    public static final String TITLE_FONT = "title_font";
    public static final String SONGS_TITLE_FONT = "songs_title_font";
    public static final String SUB_TITLE_FONT = "subtitle_font";
    public static final String SONGS_NAME_FONT = "songs_name_font";
    public static final String SONGS_SINGER_FONT = "songs_singer_font";
    public static final String BUTTON_FONT = "button_font";
    public static final String STATE_INFO_FONT = "state_font";
    public static final String STATE_INFO1_FONT = "state1_font";
    public static final String STATE_INFO2_FONT = "state2_font";
    public static final String DRAWER_FONT = "drawer_font";

    /**
     * 初始化字体
     */
    public static void initFonts(Context context){
        typeAller = Typeface.createFromAsset(context.getAssets(), "fonts/Aller_Lt.ttf");
        typeArial = Typeface.createFromAsset(context.getAssets(), "fonts/Arial_Rounded_Bold.ttf");
        typeSan = Typeface.createFromAsset(context.getAssets(), "fonts/SansitaOne.ttf");

    }

    public static Typeface getType(String s){
        switch (s){
            case TITLE_FONT:
                return typeSan;

            case SONGS_TITLE_FONT:
                return typeArial;

            case SUB_TITLE_FONT:
                return typeAller;

            case SONGS_NAME_FONT:
                return typeArial;

            case SONGS_SINGER_FONT:
                return typeAller;

            case BUTTON_FONT:
                return typeArial;

            case STATE_INFO_FONT:
                return typeAller;

            case STATE_INFO1_FONT:
                return typeArial;

            case STATE_INFO2_FONT:
                return typeArial;

            case DRAWER_FONT:
                return null;

            default :
                return null;

        }

    }


}
