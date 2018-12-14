package com.fotoable.piano.game.utils;

/**
 * Created by damon on 02/08/2017.
 */

public class GameResource {

    public static final float INIT_WHITE_ALPHA = 0.3f;
    //五线谱 设置颜色，依次为红绿蓝和透明通道 R77 G46 B46
    public static float[] COLOR_FOOT_STEPS = {0.301961f, 0.180392f, 0.180392f, INIT_WHITE_ALPHA};


    //右上角 游戏分数的字体颜色 R88 G29 B29
    public static float[] COLOR_TEXT_INTEGRAL = new float[]{0.345098f, 0.113725f, 0.113725f, 0.61f};


    //1个音节连线  白色
    public static float NODE_LINE_COLOR1[] = {1.0f, 1.0f, 1.0f, 1.0f};
    //2个音节连线 黄色 R226 G39  B39
    public static float NODE_LINE_COLOR2[] = {0.886275f, 0.152941f, 0.152941f, 1.0f};
    //3个音节连线 蓝色 R234 G114 B14
    public static float NODE_LINE_COLOR3[] = {0.917647f, 0.447059f, 0.054901f, 1.0f};
    //4个音节连线 粉红 R255 G197 B0
    public static float NODE_LINE_COLOR4[] = {1.0f, 0.772549f, 0f, 1.0f};
}
