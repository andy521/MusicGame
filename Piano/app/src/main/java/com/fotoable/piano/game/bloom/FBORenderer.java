package com.fotoable.piano.game.bloom;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.game.GameRender;
import com.fotoable.piano.game.utils.GLUtils;

import java.nio.FloatBuffer;

public class FBORenderer {
    private static final String TAG = "FBORenderer";
    private GameRender gameRender;
    int fboWidth = 256;
    int fboHeight = 256;

    int fboId;
    int fboTex;
    int renderBufferId;

    int fboIdStep1;
    int fboTexStep1;
    int renderBufferIdStep1;

    int fboIdStep2;
    int fboTexStep2;
    int renderBufferIdStep2;

    int iProgIdRTT;
    int iPositionRTT;
    int iTexCoordsRTT;
    int iTexLocRTT;
    int iVPMatrix;


    int iProgIdBlur;


    public int iTexId1;


    FloatBuffer vertexBuffer, texBuffer, texBuffer1, vertexBuffer1;

    //keep dimensions in sync with projection dimensions
    //since rect should of full screen to map fbo
    public final float[] COORDS1 = {
            -1f, -1f, //bottom - left
            1f, -1f, //bottom - right
            -1f, 1f, //top - left
            1f, 1f //top - right
    };

    public final float[] TEX_COORDS = {
            0, 1, //bottom - left
            1, 1, // bottom - right
            0, 0, // top - left
            1, 0 // top - right

    };


    //    float[] m_fViewMatrix = new float[16];
//    float[] m_fProjMatrix = new float[16];
//    float[] m_fVPMatrix = new float[16];
//    float[] m_fModel = new float[16];
//    float[] m_fMVPMatrix = new float[16];


    int iDirection, iBlurAmount;
    int iBlurScale, iBlurStrength;

    private boolean isInit = false;

    public FBORenderer(GameRender gameRender) {
        this.gameRender = gameRender;
        vertexBuffer = GLUtils.CreateVertexArray(GLUtils.pos);
        texBuffer = GLUtils.CreateVertexArray(GLUtils.coord);
        vertexBuffer1 = GLUtils.CreateVertexArray(GLUtils.pos);
        texBuffer1 = GLUtils.CreateVertexArray(GLUtils.coord);
//        vertexBuffer = GLUtils.CreateVertexArray(COORDS1);
//        texBuffer = GLUtils.CreateVertexArray(TEX_COORDS);
//
//        vertexBuffer1 = GLUtils.CreateVertexArray(COORDS1);
//        texBuffer1 = GLUtils.CreateVertexArray(TEX_COORDS);

    }

    public void loadShaders() {
        String strVShader =
                "attribute vec4 a_position;" +
                        "attribute vec2 a_texCoords;" +
                        "uniform mat4 u_ModelViewMatrix;" +
                        "varying vec2 v_texCoords;" +
                        "void main()" +
                        "{" +
                        "gl_Position = u_ModelViewMatrix * a_position;" +
                        "v_texCoords = a_texCoords;" +
                        "}";
        String strFShaderRTT =
                "precision mediump float;" +
                        "varying vec2 v_texCoords;" +
                        "uniform sampler2D u_texId;" +
                        "void main()" +
                        "{" +
                        "gl_FragColor = texture2D(u_texId, v_texCoords);" +
                        "}";


        iProgIdRTT = GLUtils.LoadProgram(strVShader, strFShaderRTT);

        iProgIdBlur = GLUtils.LoadProgram(MyApplication.application, "vertex.vsh", "gaussianblur.fsh");

//        TextureMode temp = new TextureMode();
//        temp.pictureName = "node_logo_normal.png";
//        GLUtils.loadTexture(temp, true);
//        iTexId1 = temp.textureId[0];
        iTexId1 = GLUtils.LoadTexture(MyApplication.application, "node_logo_normal.png");
    }

    public int[] createFrameBuffers() {
//        Matrix.setLookAtM(m_fViewMatrix, 0, 0, 0, 5, 0, 0, 0, 0, 1, 0);

        Matrix.setLookAtM(gameRender.mViewMatrix, 0, 0, 0, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//        Matrix.setLookAtM(m_fViewMatrix, 0, 0, 0, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        int[] ret = new int[2];

        int[] temp = new int[1];
        GLES20.glGenFramebuffers(1, temp, 0);
        fboId = temp[0];
        GLES20.glGenTextures(1, temp, 0);
        fboTex = temp[0];
        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBufferId = temp[0];

        int rtt = initiateFrameBuffer(fboId, fboTex, renderBufferId);

        GLES20.glGenFramebuffers(1, temp, 0);
        fboIdStep1 = temp[0];
        GLES20.glGenTextures(1, temp, 0);
        fboTexStep1 = temp[0];
        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBufferIdStep1 = temp[0];

        initiateFrameBuffer(fboIdStep1, fboTexStep1, renderBufferIdStep1);

        GLES20.glGenFramebuffers(1, temp, 0);
        fboIdStep2 = temp[0];
        GLES20.glGenTextures(1, temp, 0);
        fboTexStep2 = temp[0];
        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBufferIdStep2 = temp[0];

        int finaltex = initiateFrameBuffer(fboIdStep2, fboTexStep2, renderBufferIdStep2);

        ret[0] = rtt;
        ret[1] = finaltex;

        return ret;
    }

    public int initiateFrameBuffer(int fbo, int tex, int rid) {

        //Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
        //Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
        //Define texture parameters
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, fboWidth, fboHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, rid);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, fboWidth, fboHeight);
        //Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, tex, 0);
        //Attach render buffer to depth attachment
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, rid);
        //we are done, reset
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return tex;
    }

    public void renderGaussianBlur(float bloomStrength) {
        GLES20.glViewport(0, 0, fboWidth, fboHeight);
        float ratio = (float) fboWidth / (float) fboHeight;
        //投影矩阵
        Matrix.frustumM(gameRender.mProjectMatrix, 0, -ratio, ratio, -1, 1, 3f, 7);
        //如果不修改摄像机的位置,texture会出现倒立现象
        //详见: http://www.rtsoft.com/forums/showthread.php?5643-The-upside-down-problem
        Matrix.scaleM(gameRender.mProjectMatrix,0,1,-1,1);
//        Matrix.frustumM(gameRender.mProjectMatrix, 0, -ratio, ratio, -1, 1, 3f, 7);
        //设置相机位置 相机z轴 就要结合调用 Matrix.frustumM 时的 near 和 far 参数了，near <= z <= far
        Matrix.setLookAtM(gameRender.mViewMatrix, 0, 0, 0, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//        Matrix.frustumM(mProjectMatrix, 0, -ratioWH, ratioWH, -1, 1, 3f, 7);
        //计算变换矩阵
        Matrix.multiplyMM(gameRender.mMVPMatrix, 0, gameRender.mProjectMatrix, 0, gameRender.mViewMatrix, 0);


//        gameRender.setProjection();
//        //render scene to texture fboTex
        renderToTexture();
        if(isInit){
            //在性能差的手机上会出现卡顿问题, 先去掉.
//            if (bloomStrength > 0f) {
//                blur(bloomStrength);
//            }
        }else{
            //如果不执行一次blur 在三星Galaxy S4上会出现乱码图片
            isInit = true;
            blur(0);
        }

    }

    public void renderToTexture() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        iPositionRTT = GLES20.glGetAttribLocation(iProgIdRTT, "a_position");
        iTexCoordsRTT = GLES20.glGetAttribLocation(iProgIdRTT, "a_texCoords");
        iTexLocRTT = GLES20.glGetUniformLocation(iProgIdRTT, "u_texId");
        iVPMatrix = GLES20.glGetUniformLocation(iProgIdRTT, "u_ModelViewMatrix");

//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(iProgIdRTT);


//        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(iPositionRTT);
        GLES20.glVertexAttribPointer(iPositionRTT, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);



//        texBuffer.position(0);
        GLES20.glEnableVertexAttribArray(iTexCoordsRTT);
        GLES20.glVertexAttribPointer(iTexCoordsRTT, 2, GLES20.GL_FLOAT, false, 0, texBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, iTexId1);
        GLES20.glUniform1i(iTexLocRTT, 0);

        float[] matrix = gameRender.getMatrixFromIndex();
        gameRender.pushMatrix(matrix);
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, gameRender.getFinalMatrix(matrix), 0);
        gameRender.popMatrix();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//		GLES20.glDrawElements(GLES20.GL_TRIANGLES, sphere.m_nIndeces, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glDisableVertexAttribArray(iPositionRTT);
        GLES20.glDisableVertexAttribArray(iTexCoordsRTT);
    }

    /***
     * using Gaussian blur with linear approximation
     * Step1: Apply Vertical Blur on image/texture
     * Step2: apply horizontal blur on Step1
     */

    public void blur(float bloomStrength) {
        iPositionRTT = GLES20.glGetAttribLocation(iProgIdBlur, "a_position");
        iTexCoordsRTT = GLES20.glGetAttribLocation(iProgIdBlur, "a_texCoords");
        iTexLocRTT = GLES20.glGetUniformLocation(iProgIdBlur, "u_texId");
        iVPMatrix = GLES20.glGetUniformLocation(iProgIdBlur, "u_ModelViewMatrix");

        iDirection = GLES20.glGetUniformLocation(iProgIdBlur, "direction");
        iBlurScale = GLES20.glGetUniformLocation(iProgIdBlur, "blurScale");
        iBlurAmount = GLES20.glGetUniformLocation(iProgIdBlur, "blurAmount");
        iBlurStrength = GLES20.glGetUniformLocation(iProgIdBlur, "blurStrength");

        GLES20.glUseProgram(iProgIdBlur);
        //apply horizontal blur on fboTex store result in fboTexStep1
        blurStep(1, bloomStrength);
        //apply horizontal blur on fboTex store result in fboTexStep2
        blurStep(2, bloomStrength);
    }

    public void blurStep(int step, float bloomStrength) {
        if (step == 1)
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIdStep1);
        else if (step == 2)
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboIdStep2);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

//        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(iPositionRTT);
        GLES20.glVertexAttribPointer(iPositionRTT, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer1);

//        texBuffer.position(0);
        GLES20.glEnableVertexAttribArray(iTexCoordsRTT);
        GLES20.glVertexAttribPointer(iTexCoordsRTT, 2, GLES20.GL_FLOAT, false, 0, texBuffer1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        if (step == 1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTex);
        else if (step == 2)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexStep1);

        GLES20.glUniform1i(iTexLocRTT, 0);

//        Matrix.setIdentityM(m_fModel, 0);

        float[] matrix = gameRender.getMatrixFromIndex();
        gameRender.pushMatrix(matrix);
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, gameRender.getFinalMatrix(matrix), 0);
        gameRender.popMatrix();

        if (step == 1)
            GLES20.glUniform1i(iDirection, 0);
        else if (step == 2)
            GLES20.glUniform1i(iDirection, 1);

        GLES20.glUniform1f(iBlurScale, bloomStrength);//动态修改
//		GLES20.glUniform1f(iBlurScale,  1.0f);

        GLES20.glUniform1f(iBlurAmount, 50f);//不变
//		GLES20.glUniform1f(iBlurAmount, 20f);
//        Log.d(TAG,"bloomStrength-->>"+bloomStrength);
        GLES20.glUniform1f(iBlurStrength, 0.2f);//不变
//		GLES20.glUniform1f(iBlurStrength, 0.5f);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glDisableVertexAttribArray(iPositionRTT);
        GLES20.glDisableVertexAttribArray(iTexCoordsRTT);
    }


}
