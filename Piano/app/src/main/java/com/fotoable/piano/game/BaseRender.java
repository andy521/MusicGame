package com.fotoable.piano.game;

import android.opengl.GLSurfaceView;

/**
 * Created by damon on 05/06/2017.
 */

public abstract class BaseRender implements GLSurfaceView.Renderer {
    private static final String TAG = "BaseRender";
    public GLSurfaceView mView;
//    public volatile  MotionEvent touchEvent = null;
    public BaseRender(GLSurfaceView mView) {
        this.mView =  mView;
    }


}
