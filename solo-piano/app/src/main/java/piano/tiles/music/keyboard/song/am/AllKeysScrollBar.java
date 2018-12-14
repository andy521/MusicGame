package piano.tiles.music.keyboard.song.am;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by yanwei on 4/18/16.
 */
public class AllKeysScrollBar extends View {
    private Context mContext;
    private Bitmap bmpAllKeysBk;
    private Rect rectSrc = new Rect();
    private Rect rectDes = new Rect();
    private int progress = 0;
    private int maxKey = 52;
    private int screen_keys_num = 10;
    private int orderNumber = 0;//键盘序号(从上到下0号键盘,1号键盘,2号键盘...)

    private Paint paint = new Paint();
    OnKeysRangeChanged mListener = null;

    public AllKeysScrollBar(Context context) {
        super(context);
        init(context);
    }

    public AllKeysScrollBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AllKeysScrollBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public AllKeysScrollBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    private void init(Context context)
    {
        mContext = context;

        bmpAllKeysBk = BitmapFactory.decodeResource(context.getResources(), R.drawable.piano_keyboard_seekbar);
        if(bmpAllKeysBk != null)
        {
            rectSrc.bottom = bmpAllKeysBk.getHeight();
            rectSrc.right = bmpAllKeysBk.getWidth();
        }
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
//        canvas.drawColor(0x5f432101);
//
//        canvas.drawRect(progress * rectDes.right / maxKey,
//                0,
//                (screen_keys_num + progress) * rectDes.right / maxKey,
//                rectDes.bottom,
//                paint
//        );

        /*
        Rect rect = new Rect();
        rect.left = progress * rectDes.right / maxKey;
        rect.right = (progress + 10) * rectDes.right / maxKey;
        rect.top = 0;
        rect.bottom = rectDes.bottom;
        L.v("piano", rect.toString());
        canvas.drawRect(rect,
                paint
        );
        */

        if(bmpAllKeysBk != null)
        {
//            paint.setColor(0x7feeefff);
//            paint.setColor(0xE0FFFFFF);
            canvas.drawBitmap(bmpAllKeysBk, rectSrc, rectDes, paint);
        }

        canvas.clipRect(progress * rectDes.right / maxKey,
                0,
                (screen_keys_num + progress) * rectDes.right / maxKey,
                rectDes.bottom,
                Region.Op.XOR
        );

//        canvas.drawColor(0x9F4F4F4F);
            canvas.drawColor(0x9F432101);
    }

    protected void onMeasure(int w, int h)
    {
        super.onMeasure(w, h);
        rectDes.bottom = getHeight();
        rectDes.right = getWidth();
        paint.setShader(new LinearGradient(0, getHeight(), 0, 0, -3028811, -395018, Shader.TileMode.CLAMP));
    }

    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        L.v("piano","---------AllKeysScrollBar-------onTouchEvent-----" + motionEvent.getX() + ";     rectDes.right:" + rectDes.right);
        if(!PianoKeyManager.getLoadFinished())
        {
            Toast.makeText(mContext, R.string.not_ready_toast, Toast.LENGTH_SHORT).show();
            return true;
        }

        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN
                || motionEvent.getAction() == MotionEvent.ACTION_MOVE)
        {
            float x = motionEvent.getX();
            if(x >= 0
                    && x <= rectDes.right)
            {
                int index = (int)((x + 10)  * maxKey / rectDes.right - 10f);
                if (PianoActivity.isSingleKey) {//单排键
                    L.v("piano","====单排键==" + index);
                    defaultTouch(index);
                }else {//双排键
                    if (PianoActivity.isDoubleKeyboardConnection) {//没联动
                        defaultTouch(index);
                    }else {//联动
                        L.v("piano","====双排键==" + index);
                        touchPosition(index,orderNumber);
                    }

                }


//                //int index = (int)(x * (10 + maxKey) / rectDes.right - 5f);
//                int index = (int)((x + 10)  * maxKey / rectDes.right - 10f);
//                L.v("piano","=====index=" + index);
//                if(index < 0)
//                    index = 0;
//                if(index > (maxKey - screen_keys_num))
//                    index = maxKey - screen_keys_num;
//                if(index != progress && mListener != null)
//                    mListener.onRangeChanged(index);
//                progress = index;
//                postInvalidate();
            }
        }
        if(progress < 0)
            progress = 0;
        if(progress >= (maxKey - screen_keys_num))
            progress = maxKey - screen_keys_num;
        if(mListener != null)
            mListener.onRangeChanged(progress,true);
        postInvalidate();
        return true;
    }

    public void setListener(OnKeysRangeChanged listener)
    {
        mListener = listener;
    }

    public int setProgress(int value)
    {
        L.v("piano","-------------------------AllKeysScrollBar------------------setProgress---------------------------");
        progress = value;
        if(progress < 0)
            progress = 0;
        if(progress >= (maxKey - screen_keys_num))
            progress = maxKey - screen_keys_num;
        if(mListener != null)
            mListener.onRangeChanged(progress, true);
        postInvalidate();
        return progress;
    }

    public int setProgress(int value, boolean isValidate)
    {
        progress = value;
        if(progress < 0)
            progress = 0;
        if(progress >= (maxKey - screen_keys_num))
            progress = maxKey - screen_keys_num;

        if(mListener != null && isValidate)
            mListener.onRangeChanged(progress, isValidate);
        postInvalidate();
        return progress;
    }

    public void setScreen_keys_num(int num)
    {
        screen_keys_num = num;
        postInvalidate();
    }

    /**
     *
     * @param num 键盘的的按键数量
     * @param orderNumber 键盘序号(从上到下0号键盘,1号键盘,2号键盘...)
     */
    public void setScreen_keys_num(int num,int orderNumber){
        screen_keys_num = num;
        this.orderNumber = orderNumber;
        postInvalidate();
    }

    /**
     *
     * @param index 按键点击的位置
     * @param orderNumber 键盘序号
     */
    private void touchPosition(int index, int orderNumber) {
        try {
            if (orderNumber == 0){
                L.v("piano","---------------0号键盘-------------");
                if (index < 0){
                    index = 0;
                }
                if (screen_keys_num == 16){
                    if (index > 20) {
                        index = 20;
                    }
                }else if (screen_keys_num == 14) {
                    if (index > 24) {
                        index = 24;
                    }
                }else if (screen_keys_num == 12) {
                    if (index > 28) {
                        index = 28;
                    }
                }else if (screen_keys_num == 10) {
                    if (index > 32) {
                        index = 32;
                    }
                }else if (screen_keys_num == 8) {
                    if (index > 36) {
                        index = 36;
                    }
                }else if (screen_keys_num == 6) {
                    if (index > 40) {
                        index = 40;
                    }
                }
                if(index > (maxKey - screen_keys_num))
                    index = maxKey - screen_keys_num;
                if(index != progress && mListener != null)
                    mListener.onRangeChanged(index,true);
                progress = index;
                postInvalidate();
            }else if (orderNumber == 1){
                L.v("piano","-------------1号键盘---------------");
                if (index < 0){
                    index = 0;
                }
                if (screen_keys_num == 16) {
                    if (index < 16) {
                        index = 16;
                    }
                }else if (screen_keys_num == 14) {
                    if (index < 14) {
                        index = 14;
                    }
                }else if (screen_keys_num == 12) {
                    if (index < 12) {
                        index = 12;
                    }
                }else if (screen_keys_num == 10) {
                    L.v("piano","======index====" + index);
                    if (index < 10) {
                        index = 10;
                    }
                }else if (screen_keys_num == 8) {
                    if (index < 8) {
                        index = 8;
                    }
                }else if (screen_keys_num == 6) {
                    if (index < 6) {
                        index = 6;
                    }
                }
                if(index > (maxKey - screen_keys_num))
                    index = maxKey - screen_keys_num;
                if(index != progress && mListener != null)
                    mListener.onRangeChanged(index,true);
                progress = index;
                postInvalidate();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param index 按键点击的位置
     */
    private void defaultTouch(int index){
        try {
            if(index < 0)
                index = 0;
            if(index > (maxKey - screen_keys_num))
                index = maxKey - screen_keys_num;
            if(index != progress && mListener != null)
                mListener.onRangeChanged(index,true);
            progress = index;
            postInvalidate();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
