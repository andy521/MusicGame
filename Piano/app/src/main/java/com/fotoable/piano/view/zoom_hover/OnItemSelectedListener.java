package com.fotoable.piano.view.zoom_hover;

import android.view.View;

/**
 * item选中回调
 */
public interface OnItemSelectedListener {

    /**
     * item选中回调方法
     *
     * @param view
     * @param position
     */
    void onItemSelected(View view, int position,boolean isUserClick);
}