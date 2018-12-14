package com.fotoable.piano;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.fotoable.adloadhelper.ads.NativeAdViewManager;
import com.fotoable.piano.activity.MainActivity;
import com.fotoable.piano.ad.AdConstant;
import com.fotoable.piano.constant.Constant;
import com.fotoable.piano.game.shared.SharedRate;
import com.fotoable.piano.game.utils.GLConstants;
import com.fotoable.piano.utils.FontsUtils;

import io.fabric.sdk.android.Fabric;

/**
 * Created by damon on 06/06/2017.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    public static boolean isShowLevelUpDialog = false;
    public static MyApplication application;
    public int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        long startTime = System.currentTimeMillis();
        application = this;
        //初始化常量
        GLConstants.initConstants();
        //加载字体库文件
        FontsUtils.initFonts(this);

        FlurryAgent.init(this, Constant.FLURRY_APPKEY);
        new FlurryAgent.Builder().withLogEnabled(false).withContinueSessionMillis(90 * 1000).build(this, Constant.FLURRY_APPKEY);
        FlurryAgent.setFlurryAgentListener(new FlurryAgentListener() {
            @Override
            public void onSessionStarted() {

            }
        });
        Fabric.with(this, new Crashlytics(), new Answers());
        //facebook Analytics
        FacebookSdk.sdkInitialize(getApplicationContext());

        AppEventsLogger.activateApp(this, getString(R.string.facebook_id));

        NativeAdViewManager.getInstance().init(getApplicationContext(), AdConstant.AD_CONFIG_SERVER_URL, AdConstant.AD_CONFIG_LOCAL_NAME);
        registerActivityLife();
        Log.e(TAG, "MyApplicationOpenTime-->>" + (System.currentTimeMillis() - startTime));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        application = null;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void registerActivityLife() {
        if (application == null) {
            return;
        }
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                Log.v(TAG, activity + "onActivityStopped count-->>" + count);
                count--;
                if (count == 0) {
                    Log.v(TAG, ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.v(TAG, activity + "onActivityStarted count-->>" + count);
                if (count == 0) {
                    PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                    boolean isScreenOn = pm.isScreenOn();
                    Log.v(TAG, ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle, isScreenOn-->>" + isScreenOn);
                    if (isScreenOn) {
                        //屏幕亮 && 回到前台 才记录统计次数 (因为有些手机在锁屏后也会回调)
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            SharedRate.addPianoOpenCount();//统计启动次数
                        }
                        if(activity instanceof MainActivity){
                            //当该Activity是MainActivity时,才可能弹出评分框
                            SharedRate.checkRateApp(activity);//检查评分
                        }
                    }
                }
                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.v(TAG, activity + "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.v(TAG, activity + "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.v(TAG, activity + "onActivityPaused");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.v(TAG, activity + "onActivityDestroyed");
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.v(TAG, activity + "onActivityCreated");
            }
        });

    }
}
