package com.fotoable.piano.constant;

import android.os.Bundle;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.fotoable.piano.MyApplication;

import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * 埋点
 * Created by fotoable on 2017/7/10.
 */
public abstract class AnalyzeConstant {
    //    首页
    public static final String click_drawer_menu = "click_drawer_menu";
    public static final String click_grade_btn = "click_grade_btn";
    public static final String click_coin_btn = "click_coin_btn";
    public static final String select_song_list = "select_song_list";
    public static final String category = "category";
    //   song list
    public static final String click_song_item = "click_song_item";
    public static final String click_song_audition = "click_song_audition";
    public static final String click_preview = "click_preview";
    public static final String songId = "songId";
    //  song play
    public static final String click_start_game = "click_start_game";
    public static final String type = "type";
    public static final String click_replay_btn = "click_replay_btn";
    public static final String click_paused_resume = "click_paused_resume";
    //    upgrade_to_vip
    public static final String upgrade_to_vip = "upgrade_to_vip";
    public static final String click_unlock_btn = "click_unlock_btn";
    public static final String pay_success = "pay_success";
    public static final String pay_fail = "pay_fail";
    public static final String finish_game = "finish_game";
    public static final String destory_app = "destory_app";


    public static final String login_fb_successed = "login_fb_successed";
    public static final String login_fb_failed = "login_fb_failed";
    public static final String invite_fbfriend_successed = "invite_fbfriend_successed";
    public static final String invite_fbfriend_failed = "invite_fbfriend_failed";
    public static final String fetch_fbfriends_successed = "fetch_fbfriends_successed";
    public static final String fetch_fbfriends_failed = "fetch_fbfriends_failed";


    public static final String RATE_DIALOG_FIRST_SHOW = "RATE_DIALOG_FIRST_SHOW";
    public static final String RATE_DIALOG_FIRST_STARS_NUMBER = "RATE_DIALOG_FIRST_STARS_NUMBER";
    public static final String RATE_DIALOG_FIRST_NO_THANKS = "RATE_DIALOG_FIRST_NO_THANKS";
    public static final String RATE_DIALOG_FIRST_LATER = "RATE_DIALOG_FIRST_LATER";
    public static final String RATE_DIALOG_RATE_GO_TO_GOOGLE = "RATE_DIALOG_RATE_GO_TO_GOOGLE";
    public static final String RATE_DIALOG_RATE_CANCEL = "RATE_DIALOG_RATE_CANCEL";
    public static final String RATE_DIALOG_FEEDBACK_OK = "RATE_DIALOG_FEEDBACK_OK";
    public static final String RATE_DIALOG_FEEDBACK_CLOSE = "RATE_DIALOG_FEEDBACK_CLOSE";
    public static final String RATE_DIALOG_SECOND_SHOW = "RATE_DIALOG_SECOND_SHOW";
    public static final String RATE_DIALOG_SECOND_STARS_NUMBER = "RATE_DIALOG_SECOND_STARS_NUMBER";
    public static final String RATE_DIALOG_SECOND_NO_THANKS = "RATE_DIALOG_SECOND_NO_THANKS";

    /**
     * @param event
     * @param map
     */
    public static void event(String event, Map<String, String> map) {
        try {
            //        flurry
            FlurryAgent.logEvent(event, map);
            if (Fabric.isInitialized()) {
                //        fabric
                CustomEvent viewEvent = new CustomEvent(event);
                //            facebook
                AppEventsLogger logger = AppEventsLogger.newLogger(MyApplication.application);
                Bundle parameters = new Bundle();
                for (String key : map.keySet()) {
                    viewEvent.putCustomAttribute(key, map.get(key));
                    parameters.putString(key, map.get(key));
                }
                Answers.getInstance().logCustom(viewEvent);
                logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * // TODO: Use your own attributes to track content views in your app
     *
     * @param event
     */
    public static void event(String event) {
        try {
            FlurryAgent.logEvent(event);
            if (Fabric.isInitialized()) {
                final CustomEvent customEvent = new CustomEvent(event);
                Answers.getInstance().logCustom(customEvent);
            }
            AppEventsLogger logger = AppEventsLogger.newLogger(MyApplication.application);
            logger.logEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

