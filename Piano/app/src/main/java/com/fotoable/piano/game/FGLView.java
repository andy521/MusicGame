package com.fotoable.piano.game;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.fotoable.piano.activity.GameActivity;

/**
 * Created by damon on 05/06/2017.
 */

public class FGLView extends GLSurfaceView {
    private static final String TAG = "FGLView";
    //damontodo 注意: 如果将FGLRender 这一中间步骤给省去直接setRenderer(OvalRender) 则会显示不出来, 不知道为什么
    public FGLRender renderer;


    public FGLView(Context context) {
        super(context, null);

    }

    public FGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public void init() {

        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        renderer = new FGLRender(this);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                //damontodo 这里不能 renderer.gameRender.touchEvent = event 这种直接复制的形式,因为当使用touchEvent.getY()会
                //固定偏移状态栏的高度,不知道原因.所以只能通过obtain来重新copy一个对象
                if (GameActivity.gameType!=null&&GameActivity.gameType == 0) {
                    renderer.gameRender.touchManager.handleTouch(MotionEvent.obtain(event));
                }
                break;

            default:
                //don't forget default
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "FGLView onResume");
        //不在这里开始trackManager.startTrack 是因为当回调onResume 时,onSurfaceCreated 还未回调
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "FGLView onPause");
            renderer.gameRender.trackManager.stopTrack();
    }
}
