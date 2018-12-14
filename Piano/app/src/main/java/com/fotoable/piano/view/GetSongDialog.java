package com.fotoable.piano.view;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.utils.FontsUtils;


/**
 * 加载Dialog
 * Created by fotoable on 2017/6/26.
 */

public class GetSongDialog {
    private Dialog dialog;

    public GetSongDialog(Activity context, String songName, int coins, final View.OnClickListener listener) {
        dialog = new Dialog(context, R.style.MyDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewDialog = inflater.inflate(R.layout.dialog_get_song, null);
        viewDialog.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        RelativeLayout mExchange = (RelativeLayout) viewDialog.findViewById(R.id.rl_exchange);
        TextView mTVExchange = (TextView) viewDialog.findViewById(R.id.tv_exchange);
        TextView text_get = (TextView) viewDialog.findViewById(R.id.text_get);
        TextView mSongName = (TextView) viewDialog.findViewById(R.id.tv_song_name);
        TextView mCoins = (TextView) viewDialog.findViewById(R.id.tv_coins);
        mTVExchange.setTypeface(FontsUtils.getType(FontsUtils.BUTTON_FONT));
        text_get.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        mCoins.setTypeface(FontsUtils.getType(FontsUtils.STATE_INFO_FONT));
        mSongName.setTypeface(FontsUtils.getType(FontsUtils.STATE_INFO2_FONT));

        mCoins.setText(context.getResources().getString(R.string.action_song_info)
                + " " + coins + " " + context.getResources().getString(R.string.action_song_info1));
        mSongName.setText("'" + songName + "'?");
        mExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    listener.onClick(v);
                    dismissDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Display display = context.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        dialog.setContentView(viewDialog, layoutParams);
    }

    public void show() {
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }


}
