package com.fotoable.piano.view;

import android.app.Activity;
import android.app.Dialog;
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

public class LevelUpDialog {
    private Dialog dialog;

    public LevelUpDialog(Activity context, int upLevel, int coins) {
        dialog = new Dialog(context, R.style.MyDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewDialog = inflater.inflate(R.layout.dialog_level_up, null);
        viewDialog.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        RelativeLayout mExchange = (RelativeLayout) viewDialog.findViewById(R.id.rl_exchange);
        TextView mTVExchange = (TextView) viewDialog.findViewById(R.id.tv_exchange);
        TextView mLevel = (TextView) viewDialog.findViewById(R.id.tv_level);
        TextView mCoins = (TextView) viewDialog.findViewById(R.id.tv_coin);
        mTVExchange.setTypeface(FontsUtils.getType(FontsUtils.BUTTON_FONT));
        mLevel.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        mCoins.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        mLevel.setText(upLevel+ "");
        mCoins.setText("+ "+coins+ context.getResources().getString(R.string.action_coins));
        mExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
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
