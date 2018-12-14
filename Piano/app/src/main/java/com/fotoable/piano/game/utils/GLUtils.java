package com.fotoable.piano.game.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.util.Log;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.game.entity.TextureMode;
import com.fotoable.piano.game.entity.TrackAnimationData;
import com.fotoable.piano.game.entity.TrackNodeData;
import com.fotoable.piano.midi.bean.MidiEventBean;
import com.fotoable.piano.midi.bean.MidiEventList;
import com.fotoable.piano.midi.bean.MidiNote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by damon on 08/06/2017.
 */

public class GLUtils {
    //顶点坐标
    public static final float pos[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f,
    };


    //纹理坐标
    public static final float[] coord = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    public static float[] originMatrix0 =
            {1, 0, 0, 0,
                    0, 1, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1};

    private static final String TAG = "GLUtils";

    public static void loadTexture(TextureMode model, boolean isAssets) {
        loadTexture(model, isAssets, false);
    }

    public static void loadTexture(TextureMode model, boolean isAssets, boolean isOval) {
        Log.d(TAG, "绑定纹理：" + model.pictureName);
        Bitmap bitmap = null;
        try {
            // 打开图片资源
            if (isAssets) {//如果是从assets中读取
                bitmap = BitmapFactory.decodeStream(MyApplication.application.getAssets().open(model.pictureName));
            } else {//否则就是从SD卡里面读取
                bitmap = BitmapFactory.decodeFile(model.pictureName);
            }
            if (isOval) {
                bitmap = getOvalBitmap(bitmap);
            }

            int[] textures = new int[1];
            //创建纹理
            GLES20.glGenTextures(1, textures, 0);
            model.textureId = textures;
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
////            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //根据以上指定的参数，生成一个2D纹理
            android.opengl.GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        } catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null)
                bitmap.recycle();

        }
    }

    public static Bitmap GetFromAssets(Context view, String name) {
        Bitmap img = null;
        //get asset manager
        AssetManager assetManager = view.getAssets();
        InputStream istr;
        try {
            //open image to input stream
            istr = assetManager.open(name);
            //decode input stream
            img = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    static public FloatBuffer CreateVertexArray(float[] coord) {
        FloatBuffer fb = ByteBuffer.allocateDirect(coord.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(coord).position(0);
        return fb;
    }

    public static int LoadTexture(Context view, int imgResID) {
        Log.d("Utils", "Loadtexture");
        Bitmap img = null;
        int textures[] = new int[1];
        try {
            img = BitmapFactory.decodeResource(view.getResources(), imgResID);
            GLES20.glGenTextures(1, textures, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

            android.opengl.GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0);
            Log.d("LoadTexture", "Loaded texture" + ":H:" + img.getHeight() + ":W:" + img.getWidth());
        } catch (Exception e) {
            Log.d("LoadTexture", e.toString() + ":" + e.getMessage() + ":" + e.getLocalizedMessage());
        }
        img.recycle();
        return textures[0];
    }

    static public int LoadTexture(Context view, String name) {
        Log.d("Utils", "Loadtexture");
        int textures[] = new int[1];
        Bitmap img = GetFromAssets(view, name);
        try {
            GLES20.glGenTextures(1, textures, 0);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            //            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
////            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            android.opengl.GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0);
            Log.d("LoadTexture", "Loaded texture" + ":H:" + img.getHeight() + ":W:" + img.getWidth());
        } catch (Exception e) {
            Log.d("LoadTexture", e.toString() + ":" + e.getMessage() + ":" + e.getLocalizedMessage());
        }
        img.recycle();
        return textures[0];
    }

    public static int LoadShader(String strSource, int iType) {
        Log.d("Utils", "LoadShader");
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }

    public static int LoadProgram(String strVSource, String strFSource) {
        Log.d("Utils", "LoadProgram");
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = LoadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        iFShader = LoadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }

        iProgId = GLES20.glCreateProgram();

        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);

        GLES20.glLinkProgram(iProgId);

        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }

    public static float rnd(float min, float max) {
        float fRandNum = (float) Math.random();
        return min + (max - min) * fRandNum;
    }

    public static int LoadProgram(Context ctx, String strVertShader, String strFragShader) {
        String strVShader = "";
        String strFShader = "";
        try {
            AssetManager assetManager = ctx.getAssets();
            InputStream is = assetManager.open(strVertShader);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                String line = br.readLine();
                while (line != null) {
                    strVShader += line + "\n";
                    line = br.readLine();
                }
            } catch (IOException e) {
                strVShader = "";
                e.printStackTrace();
            }
//            Log.d("VSHADER", strVShader);
            is = assetManager.open(strFragShader);
            br = new BufferedReader(new InputStreamReader(is));


            try {
                String line = br.readLine();
                while (line != null) {
                    strFShader += line;
                    line = br.readLine();
                }
            } catch (IOException e) {
                strFShader = "";
                e.printStackTrace();
            }
//            Log.d("FSHADER", strFShader);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoadTexture", e.toString() + ":" + e.getMessage() + ":" + e.getLocalizedMessage());
        }
        return LoadProgram(strVShader, strFShader);
    }

    public static float[] touchXY2Position(float touchX, float touchY) {
        float widthP = GLConstants.getScreenRatioWh() * 2;
        float heightP = 2;
        float[] result = new float[2];
        result[0] = touchX / GLConstants.getScreenWidth() * widthP - GLConstants.getScreenRatioWh();
        result[1] = -(touchY / GLConstants.getScreenHeight() * heightP - 1);
//        result[1] = 1 - touchY / GLConstants.getScreenHeight() * heightP;
        return result;

    }

    public static float[] position2touchXY(float posX, float posY) {
        float[] result = new float[2];
        float widthP = GLConstants.getScreenRatioWh() * 2;
        float heightP = 2;

        result[0] = (posX + GLConstants.getScreenRatioWh()) / widthP * GLConstants.getScreenWidth();
        result[1] = (1 - posY) / heightP * GLConstants.getScreenHeight();

        return result;

    }

    public static TrackAnimationData convertNoteData(MidiEventBean midiEventBean, float initAlpha) {
        if (midiEventBean == null) {
            Log.e(TAG, "error-->>midiEventBean==null");
            return null;
        }

        int allColumns = midiEventBean.getNodeMaxIndex() - midiEventBean.getNodeMinIndex() + 1;
        //每列的像素数是动态
        int nodePixel = (int) ((GLConstants.getScreenWidth() - GLConstants.getOffsetLeftPx() - GLConstants.getOffsetRightPx()) / allColumns);

        //最小的时间间隔
        GLConstants.updateSpeed(midiEventBean.minDeltaTime);
        CopyOnWriteArrayList<TrackNodeData> nodeList = new CopyOnWriteArrayList<>();
        List<MidiEventList> midiEventList = midiEventBean.getMidiEventList();
        if (midiEventList == null || midiEventList.size() == 0) {
            Log.e(TAG, "error-->>midiEventList==null||midiEventList.size()==0");
            return null;
        }

        long lastNodeStartTime = 0;

        for (int i = 0; i < midiEventList.size(); i++) {
            MidiEventList midiEventList1 = midiEventList.get(i);
            List<MidiNote> mMidiNote = midiEventList1.getmMidiNote();
            if (mMidiNote == null || mMidiNote.size() == 0) {
                Log.e(TAG, "error-->>mMidiNote==null||mMidiNote.size()==0");
                continue;
            }
            long realColumnIndex = 0;

            for (int j = 0; j < mMidiNote.size(); j++) {
                MidiNote midiNode = mMidiNote.get(j);
                if (j == 0) {
                    int firstColumn = midiNode.getColumnIndex() - 1;
                    if (firstColumn < 0 || firstColumn > (GLConstants.NOTE_COLUMNS - 1)) {
                        Log.e(TAG, "error-->>001 firstColumn-->>" + firstColumn + ", GLConstants.NOTE_COLUMNS-->>" + GLConstants.NOTE_COLUMNS);
                        continue;
                    }
                    realColumnIndex = firstColumn - midiEventBean.getNodeMinIndex();
                }
                if (midiEventList1.getmTime() < 0 || midiEventList1.getmTime() > midiEventBean.getGameTime()) {
                    Log.e(TAG, "error-->>midiEventList1.getmTime() -->>" + midiEventList1.getmTime() + ", midiEventBean.getGameTime()-->>" + midiEventBean.getGameTime());
                    continue;
                }
                if (nodeList.size() > 0) {
                    TrackNodeData lastItem = nodeList.get(nodeList.size() - 1);
                    if (lastItem.startTime > midiEventList1.getmTime()) {
                        Log.e(TAG, "error-->>lastItem.startTime -->>" + lastItem.startTime + ", midiEventList1.getmTime()-->>" + midiEventList1.getmTime());
                        continue;
                    }
                }
                TrackNodeData item = new TrackNodeData();
                item.startTime = midiEventList1.getmTime();

                float realLength = GLConstants.getScreenHeight() + GLConstants.getOneCircleNodeWidthPx();
//                item.endTime = (long) (item.startTime + realLength / (float) speed * 1000L);//注意float运算
                item.index0 = i;
                item.index1 = j;
                item.alphaCircle = initAlpha;
                item.alphaSolid = initAlpha;
                item.scaleCircleX = GLConstants.getInitCircleNodeScale();
                item.scaleCircleY = GLConstants.getInitCircleNodeScale();


                if (mMidiNote.size() > 1) {
                    if (j != mMidiNote.size() - 1) {
                        //平行音节 && 不是最后一个音节
                        item.hasLine = true;
                    }
                }
                item.countParallel = mMidiNote.size();
                item.indexParallel = j;
                if (mMidiNote.size() == 1) {
                    item.nodeType = NodeCircleManager.NodeType.NODE_TYPE_1;
                } else if (mMidiNote.size() == 2) {
                    item.nodeType = NodeCircleManager.NodeType.NODE_TYPE_2;
                } else if (mMidiNote.size() == 3) {
                    item.nodeType = NodeCircleManager.NodeType.NODE_TYPE_3;
                } else if (mMidiNote.size() == 4) {
                    item.nodeType = NodeCircleManager.NodeType.NODE_TYPE_4;
                }


                //逻辑: 当firstColumn 超过总点数的一半时.从右向左排列, 否则从左向右排列.
                float xPosition;
                if (realColumnIndex < allColumns / 2) {

                    //x轴的真是位置: 左边偏移+绘制的index+平行音节偏移
                    //从左向右排列
                    xPosition = GLConstants.getOffsetLeftPx() + (realColumnIndex + 0.5f) * nodePixel + j * GLConstants.getGapTwoNodePx();//0.5表示键的中间
                } else {
                    //从右向左排列
                    xPosition = GLConstants.getOffsetLeftPx() + (realColumnIndex + 0.5f) * nodePixel - (mMidiNote.size() - 1 - j) * GLConstants.getGapTwoNodePx();//0.5表示键的中间
                }
                float[] translate = GLUtils.touchXY2Position(xPosition, 0);
                item.translateCircleX = translate[0];
                item.translateCircleY = GLConstants.getAppearTopY();
                //根据上一个点计算出该点出现的时机
                if (item.startTime == lastNodeStartTime) {
                    item.deltaLast = item.translateCircleY;
                } else {
                    item.deltaLast = GLConstants.getNodeAppearY(item.startTime - lastNodeStartTime);
                }
                if(item.deltaLast<GLConstants.getDismissBottomY()){
                    item.deltaLast = GLConstants.getDismissBottomY();
                }
                int tempSize = nodeList.size();
                if (tempSize < 12) {
                    Log.d(TAG, "TrackNodeData tempSize-->>" + tempSize + " ,i -->>" + i + ", " + item.toString());
                } else if (tempSize > (midiEventList.size() - 6)) {
                    Log.d(TAG, "TrackNodeData tempSize-->>" + tempSize + " ,i -->>" + i + ", " + item.toString());
                } else if (nodeList.size() > 284 && tempSize < 304) {
                    Log.d(TAG, "TrackNodeData tempSize-->>" + tempSize + " ,i -->>" + i + ", " + item.toString());
                }
                nodeList.add(item);

                lastNodeStartTime = midiEventList1.getmTime();
            }
        }
        if (nodeList.size() == 0) {
            Log.e(TAG, "error-->>TrackNodeData size-->>" + nodeList.size());
            return null;
        }
        TrackAnimationData trackAnimationData = new TrackAnimationData();
        trackAnimationData.nodeList = nodeList;

        Log.d(TAG, "TrackNodeData size-->>" + nodeList.size() + ", nodeMinIndex" + midiEventBean.getNodeMinIndex() + ", nodeMaxIndex-->>" + midiEventBean.getNodeMaxIndex() + ", minDeltaTime-->>" + midiEventBean.minDeltaTime);
        return trackAnimationData;
    }

    public static Bitmap getOvalBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static String getFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(MyApplication.application.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    public static float getDynamicSpeed(long deltaTime) {
//        float percent = (translateY) * (GLConstants.getAppearTopY() - GLConstants.getDismissBottomY());
        float percent0 = (deltaTime) / (float) GLConstants.getOneNodeDropTimeFootsteps();
        if (percent0 > 1) {
            percent0 = 1;
        }
        float percent1 = getInterpolation(percent0);
        float result = (1 - percent1) * (GLConstants.getAppearTopY() - GLConstants.getNodeStopPosition1()) + GLConstants.getNodeStopPosition1();
//        Log.d(TAG, "percent0-->>" + percent0 + ", percent1-->>" + percent1 + ", result-->>" + result);
        return result;
    }


    public static int getDynamicStartTime(float translateY) {
        float percent1 = (GLConstants.getAppearTopY()-translateY)/(GLConstants.getAppearTopY() - GLConstants.getNodeStopPosition1());
        float percent0 = getInterpolation1(percent1);

        return (int) (GLConstants.getOneNodeDropTimeFootsteps()*percent0);

    }

    public static float getInterpolation(float input) {
        return 1.0f - (1.0f - input) * (1.0f - input);
    }

    public static float getInterpolation1(float output) {
        return (float) (1.0f - Math.sqrt(1-output));
    }
}
