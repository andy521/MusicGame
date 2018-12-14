package com.ytwd.midiengine.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.flurry.android.FlurryAgent;

import piano.tiles.music.keyboard.song.am.Constants;
import piano.tiles.music.keyboard.song.am.L;


/**
 * Created by yanwei on 6/6/16.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        L.v("AlarmBroadcastReceiver", "onReceiver:" + intent.toString());

        BadgeManager.addBadge(context);

        long offset = AlarmUtils.getLastAccessTime(context);
        if(offset > 0){
            offset = System.currentTimeMillis() - offset;
            if(offset > (60*60*24*3 - 100)* 1000)
            {
                L.v("AlarmBroadcastReceiver", "broadcast alarm is ok and it's very useful!");
                FlurryAgent.logEvent(Constants.ACT_ALARM_AWAKE);
            }
        }
    }
}
