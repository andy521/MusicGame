package com.fotoable.piano.utils;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Created by fotoable on 2017/8/3.
 */

public class AnimatorUtils {

    /**
     * 抖动动画
     * @param view
     * @return
     */

    public static void trembleAnim(View view) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f));

        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -4f ),
                Keyframe.ofFloat(.2f, -4f ),
                Keyframe.ofFloat(.3f, 4f ),
                Keyframe.ofFloat(.4f, -4f ),
                Keyframe.ofFloat(.5f, 4f ),
                Keyframe.ofFloat(.6f, -4f ),
                Keyframe.ofFloat(.7f, 4f ),
                Keyframe.ofFloat(.8f, -4f),
                Keyframe.ofFloat(.9f, 4f),
                Keyframe.ofFloat(1f, 0));
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY, pvhRotate);
        animator.setDuration(800);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    /**
     * 渐变动画
     * @param view
     */
    public static void changeAlpha(View view,int time) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        view.startAnimation(alphaAnimation);
        //动画结束后保持状态
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(time);
    }

    /**
     * 平移动画
     * @param view
     * @param endX
     * @param endY
     * @return
     */
    public static ObjectAnimator translationAnim(View view,int endX,int endY){
        PropertyValuesHolder transXHolder = PropertyValuesHolder.ofFloat("translationX", 0, endX);
        PropertyValuesHolder transYHolder = PropertyValuesHolder.ofFloat("translationY", 0, endY);
        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 1, .3f);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", 1, .3f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, transXHolder, transYHolder, scaleXHolder, scaleYHolder);
        objectAnimator.setDuration(1500);
        objectAnimator.setRepeatCount(0);
        return objectAnimator;
    }

    /**
     * 缩放动画
     * @param view
     */

    public static void propertyAnim(View view) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, .1f),
                Keyframe.ofFloat(.2f, .2f),
                Keyframe.ofFloat(.3f, .3f),
                Keyframe.ofFloat(.4f, .4f),
                Keyframe.ofFloat(.5f, .5f),
                Keyframe.ofFloat(.6f, .6f),
                Keyframe.ofFloat(.7f, .7f),
                Keyframe.ofFloat(.8f, .8f),
                Keyframe.ofFloat(.9f, .9f),
                Keyframe.ofFloat(1f, 1f)
        );
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, .1f),
                Keyframe.ofFloat(.2f, .2f),
                Keyframe.ofFloat(.3f, .3f),
                Keyframe.ofFloat(.4f, .4f),
                Keyframe.ofFloat(.5f, .5f),
                Keyframe.ofFloat(.6f, .6f),
                Keyframe.ofFloat(.7f, .7f),
                Keyframe.ofFloat(.8f, .8f),
                Keyframe.ofFloat(.9f, .9f),
                Keyframe.ofFloat(1f, 1f)
        );
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY);
        anim.setRepeatCount(0);
        anim.setDuration(1500);
        anim.start();
    }




}
