package com.fotoable.piano.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.adapters.LevelAdapter;
import com.fotoable.piano.clipviewpager.ClipViewPager;
import com.fotoable.piano.clipviewpager.ScalePageTransformer;
import com.fotoable.piano.entity.UserData;
import com.fotoable.piano.game.entity.LevelItem;
import com.fotoable.piano.game.shared.SharedLevel;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.utils.FontsUtils;

import java.util.ArrayList;

/**
 * Created by fotoable on 2017/6/22.
 */

public class LevelActivity extends BaseActivity {
    private  ClipViewPager mViewPager;
    private  LevelAdapter mPagerAdapter;
    private ArrayList<LevelItem> levelList;
    private int userLevel;
    private TextView mExpNum,userCoin,mLevelNum,addCoin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_level;
    }

    @Override
    protected void initView() {
        LinearLayout mBack = (LinearLayout) findViewById(R.id.ll_back);
        RelativeLayout mCoin = (RelativeLayout) findViewById(R.id.rl_coin);
        mCoin.setOnClickListener(this);
        mBack.setOnClickListener(this);
        TextView mTitle = (TextView) findViewById(R.id.tv_title);
        userCoin = (TextView) findViewById(R.id.tv_coin);
        mExpNum = (TextView) findViewById(R.id.tv_exp_num);
        mLevelNum = (TextView) findViewById(R.id.tv_level_num);
        addCoin = (TextView) findViewById(R.id.tv_add_coin);
        mTitle.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        userCoin.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        mExpNum.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        mLevelNum.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        addCoin.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        mViewPager = (ClipViewPager) findViewById(R.id.viewpager);
        mViewPager.setPageTransformer(true, new ScalePageTransformer());
        findViewById(R.id.page_container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mViewPager.dispatchTouchEvent(event);
            }
        });
        mPagerAdapter = new LevelAdapter(this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                LevelItem levelBean = levelList.get(position);
                setData(levelBean);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void initData() {
        UserData userData = SharedUser.getDefaultUser();
        userLevel = userData.level;
        int userXP = userData.xpLevel;
        levelList = SharedLevel.getLevel();
        for (int i = 0; i < levelList.size(); i++){
            if (levelList.get(i).level == userLevel+1){
                levelList.get(i).userxp = userXP;
            }else if (levelList.get(i).level < userLevel+1){
                levelList.get(i).userxp = levelList.get(i).xp;
            }else {
                levelList.get(i).userxp = 0;
            }
        }
        userCoin.setText(""+userData.coin);
        LevelItem levelBean = levelList.get(userLevel);
        setData(levelBean);
        //设置OffscreenPageLimit
        mViewPager.setOffscreenPageLimit(Math.min(levelList.size(), 5));
        mPagerAdapter.addAll(levelList,userLevel);
        mViewPager.setCurrentItem(userLevel);

    }

    public void setData(LevelItem levelBean){
        mExpNum.setText(levelBean.userxp+ "/"
                + levelBean.xp +" XP");
        mLevelNum.setText(levelBean.title
                + getResources().getString(R.string.action_level_rewards));
        addCoin.setText("+ "+levelBean.coins + getResources().getString(R.string.action_coins));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_back:
                this.finish();
                break;
            case R.id.rl_coin:
                Intent intent = new Intent(this, BalanceActivity.class);
                startActivity(intent);
                break;
        }
    }

}
