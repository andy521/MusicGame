package com.fotoable.piano.view;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fotoable.piano.R;
import com.fotoable.piano.activity.GameActivity;
import com.fotoable.piano.utils.AnimatorUtils;

/**
 * Created by fotoable on 2017/6/20.
 */

public class GameOverStarLayout extends RelativeLayout {

    private ImageView star1, star2, star3;
    private int lightStarResource;
    private int darkStarResource;

    public GameOverStarLayout(Context context) {
        super(context);
    }

    public GameOverStarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainAttributes(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.tag_star_bar, this, true);
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
    }

    public GameOverStarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StarLayout);
        lightStarResource = ta.getResourceId(R.styleable.StarLayout_light_star, R.drawable.tag_star0);
        darkStarResource = ta.getResourceId(R.styleable.StarLayout_dark_star, R.drawable.tag_star1);
        ta.recycle();
    }

    /**
     * 设置星星显示个数
     *
     * @param starNum
     */
    public void setStarNum(int starNum) {
        if (starNum == 1) {
            star1.setImageResource(lightStarResource);
            star2.setImageResource(darkStarResource);
            star3.setImageResource(darkStarResource);
            AnimatorUtils.propertyAnim(star1);
        } else if (starNum == 2) {
            star1.setImageResource(lightStarResource);
            star2.setImageResource(lightStarResource);
            star3.setImageResource(darkStarResource);
            AnimatorUtils.propertyAnim(star1);
            AnimatorUtils.propertyAnim(star2);
        } else if (starNum == 3) {
            star1.setImageResource(lightStarResource);
            star2.setImageResource(lightStarResource);
            star3.setImageResource(lightStarResource);
            AnimatorUtils.propertyAnim(star1);
            AnimatorUtils.propertyAnim(star2);
            AnimatorUtils.propertyAnim(star3);
        } else {
            star1.setImageResource(darkStarResource);
            star2.setImageResource(darkStarResource);
            star3.setImageResource(darkStarResource);
        }
    }

    /**
     * 设置星星资源文件
     */
    public void setStarResource(int difficult) {
        if (difficult == GameActivity.GameDifficulty.easy){
            this.lightStarResource = R.drawable.tag_star0;
            this.darkStarResource = R.drawable.tag_star1;
        }else if (difficult == GameActivity.GameDifficulty.middle){
            this.lightStarResource = R.drawable.silver_star_fg;
            this.darkStarResource = R.drawable.silver_star_bg;
        }else if (difficult == GameActivity.GameDifficulty.hard){
            this.lightStarResource = R.drawable.gold_star_fg;
            this.darkStarResource = R.drawable.gold_star_bg;
        }
    }


}
