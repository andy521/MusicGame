package piano.tiles.music.keyboard.song.am;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yanwei on 4/18/16.
 */
public class SoundKey extends TextView{
    private int mIndex = 0;
    private boolean mIsWhiteKey = true;
    private boolean mIsPressing = false;
    private boolean mNewPressingStatus = false;

    private Rect mRectInScreen = new Rect();

    private Context mContext;
    public boolean getNewPressingStatus() {
        return mNewPressingStatus;
    }

    public void setNewPressingStatus(boolean mNewPressingStatus) {
        this.mNewPressingStatus = mNewPressingStatus;
    }

    public void updateWhiteKeyText()
    {
        if(mIndex < 0)
            mIndex = 0;
        if(mIndex >= 51)
            mIndex = 51;
        if(mIsWhiteKey)
        {
            setText(PianoKeyManager.WHITE_KEY_NAMES[PianoKeyManager.getKeyboard_string_type()][mIndex]);
        }
    }

    public void setIndex(boolean isWhiteKey, int index) {
        this.mIndex = index;
        this.mIsWhiteKey = isWhiteKey;
        if(index < 0)
            index = 0;
        if(index >= 51)
            index = 51;
        if(mIsWhiteKey)
        {

                int type = PianoKeyManager.getKeyboard_string_type();
                setText(PianoKeyManager.WHITE_KEY_NAMES[type][index]);//#8
        }
        else
        {
            if(PianoKeyManager.BLACK_KEY_SOUNDS[index] == -1)
                this.setVisibility(View.INVISIBLE);
            else
                this.setVisibility(View.VISIBLE);
        }
    }

    public SoundKey(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SoundKey(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public SoundKey(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public SoundKey(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    public void refreshBounds()
    {
        int[] pt = new int[2];
        getLocationOnScreen(pt);

        mRectInScreen.left = pt[0];
        mRectInScreen.right = pt[0] + getWidth();
        mRectInScreen.top = pt[1];
        mRectInScreen.bottom = pt[1] + getHeight();
    }
    private void init()
    {
    }

    public void setPressingStatus(boolean isPressing)
    {
        //L.v("piano", "key " + mIsWhiteKey + " " + mIndex + ":" + isPressing);
        mIsPressing = isPressing;

        if(mIsPressing)
        {
            if(mIsWhiteKey) {
                setBackgroundResource(R.drawable.piano_white_press_button);
                PianoKeyManager.startPlayKey(mContext, true, mIndex);
            }
            else
            {
                setBackgroundResource(R.drawable.black_key_pressed);
                PianoKeyManager.startPlayKey(mContext, false, mIndex);
            }
        }
        else
        {
            if(mIsWhiteKey) {
                setBackgroundResource(R.drawable.piano_white_button);
                PianoKeyManager.stopPlayKey(mContext, true, mIndex);
            }
            else
            {
                setBackgroundResource(R.drawable.piano_black_button);
                PianoKeyManager.stopPlayKey(mContext, false, mIndex);
            }
        }
        if(!PianoKeyManager.getLoadFinished())
            invalidate();
    }






    /**
     *
     * @param isPressing 是否按下
     * @param isUpKeyPosinton 双键位置,上还是下,true:上,false:下
     */
    public void setPressingStatus(boolean isPressing, boolean isUpKeyPosinton){
        L.v("piano", "key " + mIsWhiteKey + " " + mIndex + ":" + isPressing + ";isUpKeyPosinton=" + isUpKeyPosinton);
        mIsPressing = isPressing;

        if (mIsPressing){//按下

            if (isUpKeyPosinton) {//上键盘

                if(mIsWhiteKey) {
                    setBackgroundResource(R.drawable.piano_white_press_button);
                   PianoKeyManager.startPlayKey(mContext, true, true, mIndex);
                } else {
                    setBackgroundResource(R.drawable.piano_black_press_button);
                    PianoKeyManager.startPlayKey(mContext, true, false, mIndex);
                }

            }else {//下键盘

                if(mIsWhiteKey) {
                    setBackgroundResource(R.drawable.piano_white_press_button);
                    PianoKeyManager.startPlayKey(mContext, false,true, mIndex);
                } else {
                    setBackgroundResource(R.drawable.piano_black_press_button);
                    PianoKeyManager.startPlayKey(mContext, false, false, mIndex);
                }

            }
            if(!PianoKeyManager.getLoadFinished())
                invalidate();

        }else {//抬起

            if (isUpKeyPosinton) {//上键盘

                if(mIsWhiteKey) {
                    setBackgroundResource(R.drawable.piano_white_button);
                    PianoKeyManager.stopPlayKey(mContext, true, true, mIndex);
                } else {
                    setBackgroundResource(R.drawable.piano_black_button);
                    PianoKeyManager.stopPlayKey(mContext, true, false, mIndex);
                }

            }else {//下键盘

                if(mIsWhiteKey) {
                    setBackgroundResource(R.drawable.piano_white_button);
                    PianoKeyManager.stopPlayKey(mContext, false, true, mIndex);
                } else {
                    setBackgroundResource(R.drawable.piano_black_button);
                    PianoKeyManager.stopPlayKey(mContext, false, false, mIndex);
                }

            }
            if(!PianoKeyManager.getLoadFinished())
                invalidate();

        }

//        if(!PianoKeyManager.getLoadFinished())
//        invalidate();
    }





    public boolean getPressingStatus()
    {
        return mIsPressing;
    }

    public boolean isInsideKey(float x, float y)
    {
        if(x >= mRectInScreen.left
                && x <= mRectInScreen.right
                && y >= mRectInScreen.top
                && y <= mRectInScreen.bottom
                )
            return true;
        return false;
    }
}
