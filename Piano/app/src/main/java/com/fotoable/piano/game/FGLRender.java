package com.fotoable.piano.game;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by damon on 05/06/2017.
 */

public class FGLRender extends BaseRender {
    public GameRender gameRender;

    public FGLRender(GLSurfaceView mView) {
        super(mView);
        init();
    }
    public void init(){
        gameRender = new GameRender(mView);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        GLES20.glClearColor(1, 1, 1, 0);
        GLES20.glClearColor(0,0,0,0f);
        Log.e("wuwang", "onSurfaceCreated");
        gameRender.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e("wuwang", "onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
        gameRender.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        Log.e("wuwang", "onDrawFrame");
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        gameRender.onDrawFrame(gl);
    }
}
