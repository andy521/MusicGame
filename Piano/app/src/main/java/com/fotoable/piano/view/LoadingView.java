package com.fotoable.piano.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.utils.FontsUtils;

/**
 * Created by fotoable on 2017/7/5.
 */

public class LoadingView extends RelativeLayout {

    View loadingView;
    AnimationDrawable animationDrawable;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        loadingView = inflater.inflate(R.layout.dialog_loading, this, true);
        loadingView.setBackgroundResource(R.drawable.loading_bg);
        ImageView imageView = (ImageView) findViewById(R.id.img_loading);
        imageView.setImageResource(R.drawable.lottery_animlist);
        animationDrawable = (AnimationDrawable) imageView.getDrawable();
        TextView text = (TextView) findViewById(R.id.text);
        text.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));

    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void show(){
        if (!animationDrawable.isRunning()) {
            animationDrawable.start();
        }
        loadingView.setVisibility(VISIBLE);
    }

    public void dismiss(){
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        loadingView.setVisibility(GONE);
    }

}
