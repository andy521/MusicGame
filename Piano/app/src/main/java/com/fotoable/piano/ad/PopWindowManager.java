package com.fotoable.piano.ad;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.fotoable.adloadhelper.ads.AnimateNativeAdViewLayout;
import com.fotoable.adloadhelper.ads.IAdViewCallBackListener;
import com.fotoable.adloadhelper.ads.adsbean.NativeAdBase;
import com.fotoable.piano.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by damon on 27/07/2017.
 */

public class PopWindowManager {
    private static final String TAG = "PopWindowManager";
    public static final long POP_AD_DETAY_TIME = 2000;
    private static PopupWindow popupWindow = null;
    private static boolean waitingAdLoadFlag = true;
    private static AnimateNativeAdViewLayout mAnimateNativeAdViewLayout;

    public static void showPopupWindowView(View view, PopupWindow.OnDismissListener listener) {
        if (view == null) {
            Log.e(TAG, "showPopupWindowView view == null");
            return;
        }
        Context context = view.getContext();
        if (popupWindow != null && popupWindow.isShowing()) {
            try {
                popupWindow.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mAnimateNativeAdViewLayout != null) {
            mAnimateNativeAdViewLayout.updateNativeAd();
        } else {
            mAnimateNativeAdViewLayout = new AnimateNativeAdViewLayout(context.getApplicationContext(),
                    new BigAdViewPopLayout(context.getApplicationContext()), AdConstant.AD_PLAY_FINISH,
                    iAdViewCallBackListener);
        }

        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_window_layout, null);
        RelativeLayout adContent = (RelativeLayout) contentView.findViewById(R.id.gift_content_adview);
        ViewGroup viewGroup = (ViewGroup) mAnimateNativeAdViewLayout.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        adContent.addView(mAnimateNativeAdViewLayout, params);
        ImageView close_imageview = (ImageView) contentView.findViewById(R.id.ad_close_index);
        close_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        waitingAdLoadFlag = true;
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (popupWindow != null) {
                    waitingAdLoadFlag = false;
                    timer.cancel();
                }
            }
        }, POP_AD_DETAY_TIME);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!waitingAdLoadFlag) {
                    timer.cancel();
//                    if (popupWindow != null && popupWindow.isShowing()) {
//                        try {
//                            popupWindow.dismiss();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
                }
            }
        });

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        try {
            popupWindow.showAtLocation(view, Gravity.TOP, location[0], location[1]);
            if (listener != null) {
                popupWindow.setOnDismissListener(listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "showPopupWindowView");
    }

    public static void hidePopupWindowView() {
        try {
            if (!waitingAdLoadFlag) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    AdConstant.isShowPlayFinish = false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static IAdViewCallBackListener iAdViewCallBackListener = new IAdViewCallBackListener() {
        @Override
        public void adviewLoad(NativeAdBase nativeAdBase) {

        }

        @Override
        public void adviewClick(NativeAdBase nativeAdBase) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }

        @Override
        public void adviewLoadError(NativeAdBase nativeAdBase) {

        }
    };


}
