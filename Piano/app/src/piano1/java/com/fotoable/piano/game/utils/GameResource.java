package com.fotoable.piano.game.utils;

/**
 * Created by damon on 02/08/2017.
 */

public class GameResource {

    public static final float INIT_WHITE_ALPHA = 0.5f;
    //五线谱 设置颜色，依次为红绿蓝和透明通道
    public static float[] COLOR_FOOT_STEPS = {1.0f, 1.0f, 1.0f, INIT_WHITE_ALPHA};


    //右上角 游戏分数的字体颜色
    public static float[] COLOR_TEXT_INTEGRAL = new float[]{1.0f, 1.0f, 1.0f, 1.0f};


    //1个音节连线  白色
    public static float NODE_LINE_COLOR1[] = {1.0f, 1.0f, 1.0f, 0.44f};
    //2个音节连线 黄色 R255 G231  B112
    public static float NODE_LINE_COLOR2[] = {1.0f, 0.905882f, 0.439216f, 0.44f};
    //3个音节连线 蓝色 R112 G255 B237
    public static float NODE_LINE_COLOR3[] = {0.439216f, 1.0f, 0.929412f, 0.44f};
    //4个音节连线 粉红 R255 G204 B248
    public static float NODE_LINE_COLOR4[] = {1.0f, 0.8f, 1.0f, 0.972549f};
}
