package com.fotoable.piano.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotoable.piano.R;
import com.fotoable.piano.utils.FontsUtils;

/**
 * Created by fotoable on 2017/6/19.
 */

public class CheckableTextView extends android.support.v7.widget.AppCompatTextView implements Checkable {
    private boolean mChecked;

    public CheckableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(FontsUtils.getType(FontsUtils.DRAWER_FONT));
    }
    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        setSelected(checked);
    }
    @Override
    public boolean isChecked() {
        return mChecked;
    }
    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}