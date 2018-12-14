package com.fotoable.piano.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.entity.PlayedData;
import com.fotoable.piano.entity.PlayedSong;
import com.fotoable.piano.entity.SongData;
import com.fotoable.piano.entity.UserData;
import com.fotoable.piano.game.entity.LevelItem;
import com.fotoable.piano.game.shared.SharedLevel;
import com.fotoable.piano.game.shared.SharedPlayed;
import com.fotoable.piano.game.shared.SharedSongs;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.game.utils.GLUtils;
import com.fotoable.piano.http.MyHttpManager;
import com.fotoable.piano.midi.MidiFileAnalysis;
import com.fotoable.piano.midi.SoundPoolSynth;
import com.fotoable.piano.utils.AnimatorUtils;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.utils.ToastUtils;
import com.fotoable.piano.view.CircleProgressView;
import com.fotoable.piano.view.GameOverStarLayout;
import com.fotoable.piano.view.LoadingView;
import com.fotoable.piano.view.RiseNumberTextView;
import com.fotoable.piano.view.SpringProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fotoable on 2017/6/27.
 */

public class GameOverActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SONGID = "songId";
    public static final String DIFFICULTY = "difficulty";
    private List<PlayedData> difficultyList;
    private PlayedData playedData;
    private TextView mTitle, mSinger, mType, mType2, mCoins;
    private RiseNumberTextView mScore;
    private CircleProgressView circleProgress;
    private GameOverStarLayout tagStar;
    private LinearLayout ll_coins, ll_jindu;
    private RelativeLayout rl_star, mContinue, shareView;
    private ImageView anim_coins;
    private SpringProgressView notesPro, timePro;
    private int i, z, starNum, maxScore, mNotePro, mXPro, circlePro, songId, difficulty;
    private Timer mTimer;
    private String url = null;
    private String mTypeText;
    private LoadingView dialog;
    private boolean stopThread = false;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //防止在Loading界面返回
                    if (!stopThread) {
                        //启动游戏界面
                        Intent intent = new Intent(GameOverActivity.this, GameActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_game_over);
        initView();
        if (savedInstanceState == null) {
            initData();
            upDataUserData(playedData);//刷新用户数据
        } else {
            initData();
        }
    }


    protected void initView() {
        LinearLayout replay = (LinearLayout) findViewById(R.id.ll_replay);
        replay.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.tv_song_name);
        mSinger = (TextView) findViewById(R.id.tv_singer);
        mType = (TextView) findViewById(R.id.tv_type);
        mType2 = (TextView) findViewById(R.id.tv_type2);
        TextView textHit = (TextView) findViewById(R.id.textHit);
        TextView textTiming = (TextView) findViewById(R.id.textTiming);
        TextView textScore = (TextView) findViewById(R.id.text_Score);
        mScore = (RiseNumberTextView) findViewById(R.id.tv_score);

        mTitle.setTypeface(FontsUtils.getType(FontsUtils.SONGS_TITLE_FONT));
        mSinger.setTypeface(FontsUtils.getType(FontsUtils.SUB_TITLE_FONT));
        mType.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        mType2.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        mScore.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        textHit.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        textTiming.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        textScore.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));

        ll_coins = (LinearLayout) findViewById(R.id.ll_coins);
        mCoins = (TextView) findViewById(R.id.tv_coins);
        mCoins.setTypeface(FontsUtils.getType(FontsUtils.SONGS_TITLE_FONT));
        ImageView img_coins = (ImageView) findViewById(R.id.img_coins);
        AnimatorUtils.trembleAnim(img_coins);

        //星星进度
        rl_star = (RelativeLayout) findViewById(R.id.rl_star);
        circleProgress = (CircleProgressView) findViewById(R.id.progressbar);
        // 加载圆形进度条渐变色，Piano1为固定色，Piano2为渐变色
        int[] colors = getResources().getIntArray(R.array.colors_array);
        circleProgress.setArrilColors(colors);
        tagStar = (GameOverStarLayout) findViewById(R.id.tl_star);
        //进度条
        ll_jindu = (LinearLayout) findViewById(R.id.ll_jindu);
        notesPro = (SpringProgressView) findViewById(R.id.not_pro);
        timePro = (SpringProgressView) findViewById(R.id.time_pro);

        //金币动画
        anim_coins = (ImageView) findViewById(R.id.anim_coins);
        AnimationDrawable animDrawable = (AnimationDrawable) anim_coins.getDrawable();
        animDrawable.start();

        //mContinue Button
        mContinue = (RelativeLayout) findViewById(R.id.rl_Continue);
        shareView = (RelativeLayout)findViewById(R.id.rl_share);
        mContinue.setOnClickListener(this);
        shareView.setOnClickListener(this);
        TextView mtvContinue = (TextView) findViewById(R.id.tv_Continue);
        TextView mtvshare = (TextView) findViewById(R.id.tv_share);
        mtvshare.setTypeface(FontsUtils.getType(FontsUtils.BUTTON_FONT));
        mtvContinue.setTypeface(FontsUtils.getType(FontsUtils.BUTTON_FONT));

        //加载dialog
        dialog = (LoadingView) findViewById(R.id.dialog);
    }

    protected void initData() {
        Intent intent = getIntent();
        songId = intent.getIntExtra(SONGID, -1);
        difficulty = intent.getIntExtra(DIFFICULTY, -1);
        SongData songData = SharedSongs.getSongFromSongId(songId);
        PlayedSong playSongData = SharedPlayed.getPlayedFromSongId(songId);

        mSinger.setText(songData.singerName);
        mTitle.setText(songData.name);
        tagStar.setStarResource(difficulty);
        if (difficulty == GameActivity.GameDifficulty.easy) {
            difficultyList = playSongData.easyDatas;
            url = songData.easy.pathServer;
        } else if (difficulty == GameActivity.GameDifficulty.middle) {
            difficultyList = playSongData.middleDatas;
            url = songData.middle.pathServer;
        } else if (difficulty == GameActivity.GameDifficulty.hard) {
            difficultyList = playSongData.hardDatas;
            url = songData.hard.pathServer;
        }

        playedData = difficultyList.get(difficultyList.size() - 1);
        //同一首歌曲三次后不再送金币
        if (difficultyList.size() <= 3) {
            mCoins.setText("+ " + playedData.coin);
        } else {
            //金币为0的情况
            ll_coins.setVisibility(View.GONE);
        }

        int score = playedData.score;
        if (difficultyList.size() > 1) {
            for (int i = 0; i < difficultyList.size() - 1; i++) {
                PlayedData playedData0 = difficultyList.get(i);
                int thisScore = playedData0.score;
                if (thisScore >= maxScore) {
                    maxScore = thisScore;
                }
            }
        }
        //是否打破最好成绩
        if (score > maxScore) {
            mType2.setVisibility(View.VISIBLE);
        } else {
            mType2.setVisibility(View.INVISIBLE);
        }
        starNum = playedData.star;
        //进度
        if (playedData.touchCount != 0 && starNum > 0) {
            mNotePro = (playedData.hitCount * 100) / playedData.touchCount;
        } else if (score != 0){
            mNotePro = 15;
        }else {
            mNotePro = 0;
        }

        if (mNotePro == 100)
            mXPro = mNotePro;
        else
            mXPro = (int) (mNotePro * 0.96);

        //星星
        if (starNum == 0) {
            circlePro = mNotePro;
            mTypeText = getResources().getString(R.string.action_bad);
        } else if (starNum == 1) {
            circlePro = 33;
            mTypeText = getResources().getString(R.string.action_nice);
        } else if (starNum == 2) {
            circlePro = 66;
            mTypeText = getResources().getString(R.string.action_excellent);
        } else if (starNum == 3) {
            circlePro = 100;
            mTypeText = getResources().getString(R.string.action_perfect);
        }

        mType.setText(mTypeText);
        mScore.withNumber(score).start();
        //进度条动画
        proAnim();
    }

    /**
     * 刷新保存用户数据
     *
     * @param playedData
     */
    public void upDataUserData(PlayedData playedData) {
        UserData userData = SharedUser.getDefaultUser();
        ArrayList<LevelItem> levelData = SharedLevel.getLevel();
        int addCoin = playedData.coin;
        int addXP = playedData.xp;
        //弹奏3次，金币不在累加
        if (difficultyList.size() <= 3) {
            userData.coin += addCoin;
            anim_coins.setVisibility(View.VISIBLE);
            showStarAnim1(anim_coins);
        } else {
            anim_coins.setVisibility(View.GONE);
        }
        userData.xpTotal += addXP;
        int oldLevelXP = 0;
        for (int i = 0; i < userData.level; i++) {
            oldLevelXP += levelData.get(i).xp;
        }
        userData.xpLevel = userData.xpTotal - oldLevelXP;
        if (userData.xpLevel >= levelData.get(userData.level).xp) {
            userData.coin += levelData.get(userData.level).coins;           //升级奖励金币
            userData.level += 1;
            int upDataLevelXP = 0;
            for (int i = 0; i < userData.level; i++) {
                upDataLevelXP += levelData.get(i).xp;
            }
            userData.xpLevel = userData.xpTotal - upDataLevelXP;
            //并且提示升级Dialog
            MyApplication.isShowLevelUpDialog = true;
        }
        //更新 userData
        SharedUser.updateDefaultUser(userData);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_replay:
                dialog.show();
                stopThread = false;
                AnalyzeConstant.event(AnalyzeConstant.click_replay_btn);
                MyHttpManager.downloadMid(url, myCallback);
                break;
            case R.id.rl_Continue:
                finish();
                break;
            case R.id.rl_share:
                startActivity(new Intent(GameOverActivity.this, FBInviteActivity.class));
                finish();
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (dialog.getVisibility() == View.VISIBLE) {
            stopThread = true;
            dialog.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    MyHttpManager.MyCallback myCallback = new MyHttpManager.MyCallback() {
        @Override
        public void onError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(MyApplication.application,
                            getResources().getString(R.string.download_failed));
                    dialog.dismiss();
                    finish();
                }
            });
        }

        @Override
        public void onSuccess(final String midPath) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long timeStar = System.currentTimeMillis();
                    GameActivity.mSoundPoolSynth = new SoundPoolSynth(getApplicationContext());
                    //生成音频数据
                    GameActivity.midiEventBean
                            = new MidiFileAnalysis(GameOverActivity.this, midPath).getMidiEventBean();
                    //根据音频数据,生成游戏界面所需的数据
                    GameActivity.animationDatas
                            = GLUtils.convertNoteData(GameActivity.midiEventBean, 1.0f);
                    GameActivity.songId = songId;
                    GameActivity.difficulty = difficulty;
                    GameActivity.gameType = 0;
                    long time = System.currentTimeMillis() - timeStar;
                    if (time < 1500) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    myHandler.sendEmptyMessage(0);
                }
            }).start();


        }
    };

    /**
     * 收集金币后的进度条，星星渐现动画
     */
    private void proAnim() {
        AnimatorUtils.changeAlpha(rl_star, 2000);//星星
        AnimatorUtils.changeAlpha(ll_jindu, 1500);//进度条
        AnimatorUtils.changeAlpha(mContinue, 1000);//继续按钮
        AnimatorUtils.changeAlpha(shareView, 1000);//继续按钮
        tagStar.setStarNum(starNum);
        circleProgress.setProgress(circlePro);
        mTimer = new Timer();
        setTimerTask();
    }


    /**
     * 进度条动画
     */
    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }
        }, 0, 15);//0ms后   10ms执行一次
    }

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    if (i <= mNotePro) {
                        notesPro.setProgress(i);
                        i++;
                    }
                    if (z <= mXPro) {
                        timePro.setProgress(z);
                        z++;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 收集金币动画效果
     *
     * @param view
     */
    public void showStarAnim1(final View view) {
        int endX = (getResources().getDisplayMetrics().widthPixels) / 2 - 160;
        int endY = (getResources().getDisplayMetrics().heightPixels) / 2 + 100;
        ObjectAnimator objectAnimator = AnimatorUtils.translationAnim(view, endX, -endY);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                ll_coins.setVisibility(View.VISIBLE);
            }

        });
    }

}
