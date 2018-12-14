package com.fotoable.piano.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fotoable.piano.R;

/**
 * Created by fotoable on 2017/6/20.
 */

public class StarLayout extends RelativeLayout{

    private ImageView star1,star2,star3;
    private int lightStarResource;
    private int darkStarResource;

    public StarLayout(Context context) {
        super(context);
    }

    public StarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainAttributes(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.star_bar, this, true);
        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
    }

    public StarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs,  R.styleable.StarLayout);
        lightStarResource = ta.getResourceId( R.styleable.StarLayout_light_star, R.drawable.small_star);
        darkStarResource = ta.getResourceId( R.styleable.StarLayout_dark_star, R.drawable.small_star0);
        ta.recycle();
    }

    /**
     * 设置星星显示个数
     * @param starNum
     */
    public void setStarNum (int starNum){
        if (starNum == 1){
            star1.setImageResource(lightStarResource);
            star2.setImageResource(darkStarResource);
            star3.setImageResource(darkStarResource);
        }else if (starNum == 2){
            star1.setImageResource(lightStarResource);
            star2.setImageResource(lightStarResource);
            star3.setImageResource(darkStarResource);
        }else if (starNum == 3){
            star1.setImageResource(lightStarResource);
            star2.setImageResource(lightStarResource);
            star3.setImageResource(lightStarResource);
        }else {
            star1.setImageResource(darkStarResource);
            star2.setImageResource(darkStarResource);
            star3.setImageResource(darkStarResource);
        }
    }

    /**
     * 设置星星资源文件
     * @param lightStarResource
     * @param darkStarResource
     */
    public void setStarResource(int lightStarResource, int darkStarResource) {
        this.lightStarResource = lightStarResource;
        this.darkStarResource = darkStarResource;
    }

}
