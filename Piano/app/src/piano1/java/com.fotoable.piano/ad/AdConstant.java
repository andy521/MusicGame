package com.fotoable.piano.ad;

/**
 * Created by damon on 27/07/2017.
 */

public class AdConstant {

    /**
     * 广告配置文件
     */
    public static final String AD_CONFIG_LOCAL_NAME ="ad_piano1_config_v3.json";

    /**
     * 广告服务器配置路径
     */
    public static final String AD_CONFIG_SERVER_URL ="http://dl.fotoable.net/Keyboard/ad/ad_piano1_config_v3.json";

    /**
     * 应用: 插屏 fbName: a_Wall
     * 1.在多个地方都有调用(具体详见代码)
     */
    public static final String AD_INTERSTITIAL = "472520209777307_476832882679373";
    /**
     * 应用: 原生 fbName: a_PlayFinish
     * 游戏结束时
     */
    public static final String AD_PLAY_FINISH = "472520209777307_475435766152418";

    /**
     * 是否显示游戏解释的广告
     */
    public static boolean isShowPlayFinish = false;
}
