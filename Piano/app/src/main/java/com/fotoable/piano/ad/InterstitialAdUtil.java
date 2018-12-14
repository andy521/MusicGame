package com.fotoable.piano.ad;

import android.content.Context;

import com.fotoable.adloadhelper.ads.NativeAdViewManager;

/**
 * Created by damon on 27/07/2017.
 */

public class InterstitialAdUtil {

    /**
     * 弹出插屏广告
     * 弹出时机: 间隔时间为24小时
     *
     * @param applicationContext applicationContext
     */
    public static void loadInterstitialAd(Context applicationContext) {
        /** 是否移除广告 **/
//        if (SharedPreferenceHelper.hasRemoveAd()) return;
        try {
            NativeAdViewManager.getInstance().loadInterstitialAd(
                    applicationContext,
                    AdConstant.AD_INTERSTITIAL);
        } catch (Exception | OutOfMemoryError e) {
//            MobileUtil.dataCollectLog(DataCollectConstant.EXCEPTION_INTERSTITIA_AD);
            e.printStackTrace();
        }
    }
}
