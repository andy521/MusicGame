package piano.tiles.music.keyboard.song.am;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by maming on 16/6/16.
 */
public class StartActivity extends Activity implements AdListener, InterstitialAdListener {
    private final static String TAG = "StartActivity";


    public static final int PIANO_LOAD_FINISHED = 0;
    public static final int PIANO_LOAD_UNFINISHED = 1;

    public static final String PLACEMENT_ID = "480740438790913_480744395457184";
    //public static final String PLACEMENT_ID = "1776796919230717_1777186975858378";//LaunchInterstitialEditGet


    private InterstitialAd interstitialAd;
    private boolean isInterDisplayed = false;
    private boolean isReadyShow = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PIANO_LOAD_FINISHED:
                    if (!isInterDisplayed) {
                        Intent intent = new Intent(StartActivity.this, PianoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        StartActivity.this.finish();
                    }
                    break;
                case PIANO_LOAD_UNFINISHED:
                    findViewById(R.id.start_img).setVisibility(View.GONE);
                    if (PianoKeyManager.mDialog != null && !PianoKeyManager.mDialog.isShowing()) {
                        PianoKeyManager.PlayKeyLoaderTask.setmIsShow(true);
                        new Thread() {

                            @Override
                            public void run() {
                                super.run();
                                while (!PianoKeyManager.getLoadFinished()) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                handler.sendEmptyMessage(PIANO_LOAD_FINISHED);
                            }
                        }.start();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);


        loadInterstitialAd();
        PianoKeyManager.startInitAllKeys(this, 0, false);

        Log.d(TAG, "loadInterstitialAd----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                if (isReadyShow) {
                    interstitialAd.show();
                    isInterDisplayed = true;
                }

                if (PianoKeyManager.getLoadFinished()) {//loading完成
                    if (!isInterDisplayed) {
                        Intent intent = new Intent(StartActivity.this, PianoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        StartActivity.this.finish();
                    }
                } else {//loading未完成
                    handler.sendEmptyMessage(PIANO_LOAD_UNFINISHED);
                }

            }
        };
        timer.schedule(task, 2000);
    }

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(this, PLACEMENT_ID);
        interstitialAd.setAdListener(this);
        interstitialAd.loadAd();
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        L.d(TAG, "start ad error " + adError.getErrorMessage());

    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (ad == interstitialAd) {
            isReadyShow = true;
            L.d(TAG, "onAdLoaded ");
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.d(TAG, "onAdClicked");
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        Log.d(TAG, "displayed");
        isInterDisplayed = true;
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        Log.d(TAG, "dismiss ");

        isInterDisplayed = false;
        isReadyShow = false;

        if (!PianoKeyManager.getLoadFinished()) {
            handler.sendEmptyMessage(PIANO_LOAD_UNFINISHED);
            return;
        }
        Intent intent = new Intent(StartActivity.this, PianoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        StartActivity.this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
        super.onDestroy();
    }

}
