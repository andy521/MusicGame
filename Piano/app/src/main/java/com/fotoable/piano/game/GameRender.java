package com.fotoable.piano.game;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.fotoable.piano.game.entity.ContinuousData;
import com.fotoable.piano.game.entity.TextureMode;
import com.fotoable.piano.game.entity.TouchData;
import com.fotoable.piano.game.utils.GLUtils;
import com.fotoable.piano.game.utils.SceneManager;
import com.fotoable.piano.game.utils.TouchManager;
import com.fotoable.piano.game.utils.TrackManager;
import com.fotoable.piano.utils.MyLog;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by damon on 08/06/2017.
 */

public class GameRender extends BaseRender {
    private static final String TAG = "OvalRender";

    /**
     * 触摸屏幕的总数
     */
    public final AtomicInteger touchCount = new AtomicInteger(0);

    /**
     * 击中音节的总数
     */
    public final AtomicInteger hitNodeCount = new AtomicInteger(0);
    /**
     * 当前的总分数
     */
    public final AtomicInteger totalScore = new AtomicInteger(0);

    /**
     * 连续击中的总数
     */
    public final ContinuousData continuousData = new ContinuousData();


    /**
     * 纹理 顶点着色器
     */
    private final String vertexShaderCode =

            "attribute vec4 vPosition;" +
                    "attribute vec2 vCoord;" +
                    "varying vec2 aCoord;" +
                    "uniform mat4 vMatrix;" +
                    "void main(){" +
                    "    aCoord = vCoord;" +
                    "    gl_Position = vMatrix*vPosition;" +
                    "}";

    /**
     * 纹理 片段着色器
     */
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 aCoord;" +
                    "uniform sampler2D vTexture;" +
                    "uniform sampler2D vTextureAlpha;" +
                    "uniform float Opacity;" +
                    "void main() {" +
                    "    vec4 color=texture2D( vTexture, aCoord);" +
                    "   color.a *= Opacity;" +
                    "    gl_FragColor = color;" +
                    "}";

    public int mProgram;


    /**
     * 摄像机位置
     */
    public float[] mViewMatrix = new float[16];
    //投影
    public float[] mProjectMatrix = new float[16];
    //矩阵
    public float[] mMVPMatrix = new float[16];
    //矩阵引用 uniform mat4 vMatrix
    public int mHMatrix;
    /**
     * 坐标引用 attribute vec2 a_v2TexCoord
     */
    public int mHCoord;
    /**
     * attribute vec4 a_v4Position
     */
    public int mHPosition;
    /**
     * uniform sampler2D u_s2dTexture
     */
    public int mHTexture;
    /**
     * uniform vec1 vAlpha
     */
    public int mOpacity;

    /**
     * 顶点坐标Buffer
     */
    public FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    public FloatBuffer mTexBuffer;


    private float ratioWH, width, height;


    public TouchManager touchManager;

    /**
     * 处理音节下落
     */
    public TrackManager trackManager;
    /**
     * 处理 背景相关 比如: 背景图片 音弦
     */
    public SceneManager sceneManager;


//    /**
//     * 音节 实心圆的纹理
//     */
//    public TextureMode nodeSolid;
//    /**
//     * 音节 圆环的纹理
//     */
//    public TextureMode nodeCircle;
    /**
     * 背景 纹理
     */
    public TextureMode bgTexture;
    /**
     * 背景 纹理
     */
    public TextureMode nodeLogoTexture;


    public GameRender(GLSurfaceView mView) {
        super(mView);
        touchManager = new TouchManager(this);
        trackManager = new TrackManager(this);
        sceneManager = new SceneManager(this);

    }

    private void init() {
        initTexture();
        initBuffer();
        initProgram();

    }

    private void initTexture() {
//        nodeSolid = new TextureMode();
//        nodeSolid.pictureName = "node_solid.png";
//        GLUtils.loadTexture(nodeSolid, true);
//
//        nodeCircle = new TextureMode();
//        nodeCircle.pictureName = "node_circle.png";
//        GLUtils.loadTexture(nodeCircle, true);

        bgTexture = new TextureMode();
        bgTexture.pictureName = "bg_game.png";
        GLUtils.loadTexture(bgTexture, true);

        nodeLogoTexture = new TextureMode();
        nodeLogoTexture.pictureName = "node_logo_normal.png";
        GLUtils.loadTexture(nodeLogoTexture, true);

    }

    private void initProgram() {
        int vertexShader = GLUtils.LoadShader(vertexShaderCode, GLES20.GL_VERTEX_SHADER);
        int fragmentShader = GLUtils.LoadShader(fragmentShaderCode, GLES20.GL_FRAGMENT_SHADER);
        //创建一个空的OpenGLES程序
        mProgram = GLES20.glCreateProgram();
        //将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram, vertexShader);
        //将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);

        mHPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mHCoord = GLES20.glGetAttribLocation(mProgram, "vCoord");
        mHMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        mHTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
        mOpacity = GLES20.glGetUniformLocation(mProgram, "Opacity");

    }


    /**
     * Buffer初始化
     */
    protected void initBuffer() {
        mVerBuffer = GLUtils.CreateVertexArray(GLUtils.pos);
        mTexBuffer = GLUtils.CreateVertexArray(GLUtils.coord);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        handleOnSurfaceCreated();
    }

    public void handleOnSurfaceCreated() {
        init();
        touchManager.init();
        sceneManager.init();
        trackManager.init();
        trackManager.nodeCircleManager.init();
        trackManager.nodeLineManager.init();
        sceneManager.bloomManager.init();
        sceneManager.footstepsManager.init();
        sceneManager.progressBarManager.init();
        sceneManager.pauseBtnManager.init();
        sceneManager.pauseBtnManager.init();
        sceneManager.integralManager.init();

        trackManager.startTrack();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        ratioWH = (float) width / height;
        Log.d(TAG, "width-->>" + width + ", height-->>" + height + ", ratioWH-->>" + ratioWH);

        setProjection();

    }

    public void setProjection() {
        GLES20.glViewport(0, 0, (int) width, (int) height);
        float ratio = width / height;
        //投影矩阵
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3f, 7);
        //设置相机位置 相机z轴 就要结合调用 Matrix.frustumM 时的 near 和 far 参数了，near <= z <= far
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3f, 0f, 0f, 0f, 0f, 1.0f, 0f);
//        Matrix.frustumM(mProjectMatrix, 0, -ratioWH, ratioWH, -1, 1, 3f, 7);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

//        Log.d(TAG,"mMVPMatrix-->>"+Arrays.toString(mMVPMatrix));
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        setProjection();
        //绘制背景相关
        sceneManager.drawScene();
        //绘制触摸消失动画
        touchManager.drawAnimation();
        //绘制下落的音节
        trackManager.drawTrack();

//        MyLog.i("drawframe1");
    }

    public TouchData isHit(float touchX, float touchY) {
        //处理当期触摸 是否击中
        return trackManager.checkIsHit(touchX, touchY);
//        return testHit();
    }

    private boolean testHit() {
        int flag = (int) (Math.random() * 10);
        return flag < 7;
    }

    public float[] getFinalMatrix(float[] current) {
        float[] ans = new float[16];
        Matrix.multiplyMM(ans, 0, mViewMatrix, 0, current, 0);
        Matrix.multiplyMM(ans, 0, mProjectMatrix, 0, ans, 0);
        return ans;
    }

    Stack<float[]> mStack = new Stack<>();

    public void pushMatrix(float[] current) {
        mStack.push(Arrays.copyOf(current, GLUtils.originMatrix0.length));
    }

    public void popMatrix() {
        mStack.pop();
    }

    public float[] getMatrixFromIndex() {
        return Arrays.copyOf(GLUtils.originMatrix0, GLUtils.originMatrix0.length);
    }

    public void cleanData() {
        touchCount.set(0);
        hitNodeCount.set(0);
        totalScore.set(0);
        continuousData.currentContinuousHit.set(0);
        continuousData.lastHitIndex = -1;

        sceneManager.cleanData();
        sceneManager.progressBarManager.cleanData();
        touchManager.cleanData();
        trackManager.cleanData();
    }
}