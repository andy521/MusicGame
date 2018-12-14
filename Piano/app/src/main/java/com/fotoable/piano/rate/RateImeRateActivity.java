package com.fotoable.piano.rate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fotoable.piano.BuildConfig;
import com.fotoable.piano.R;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.game.shared.SharedRate;


/**
 * Created by zzl on 2016/5/6.
 */
public class RateImeRateActivity extends Activity {
    private final static String KEYBOARD_STORE = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    private TextView rate_dialog_rate;
    private TextView rate_dialog_rate_cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.rate_dialog_rate);
        rate_dialog_rate = (TextView) findViewById(R.id.rate_dialog_rate);
        rate_dialog_rate_cancel = (TextView) findViewById(R.id.rate_dialog_rate_cancel);
        rate_dialog_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AnalyzeConstant.event(AnalyzeConstant.RATE_DIALOG_RATE_GO_TO_GOOGLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SharedRate.setPianoPreHasRate(true);
                goToGooglePlay();
                RateImeRateActivity.this.finish();
            }
        });
        rate_dialog_rate_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AnalyzeConstant.event(AnalyzeConstant.RATE_DIALOG_RATE_CANCEL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SharedRate.setPianoRateDialogLater(-1);
                RateImeRateActivity.this.finish();
            }
        });
    }

    public void goToGooglePlay() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(KEYBOARD_STORE));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.vending");
            RateImeRateActivity.this.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(KEYBOARD_STORE));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                RateImeRateActivity.this.startActivity(intent);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}