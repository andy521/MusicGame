package com.fotoable.piano.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.adapters.SelectDifficultAdapter;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.constant.Constant;
import com.fotoable.piano.entity.PlayedData;
import com.fotoable.piano.entity.PlayedSong;
import com.fotoable.piano.entity.SongData;
import com.fotoable.piano.game.shared.SharedPlayed;
import com.fotoable.piano.game.shared.SharedSongs;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.game.utils.GLUtils;
import com.fotoable.piano.game.utils.NoDoubleClickListener;
import com.fotoable.piano.http.MyHttpManager;
import com.fotoable.piano.midi.MidiFileAnalysis;
import com.fotoable.piano.midi.SoundPoolSynth;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.utils.ToastUtils;
import com.fotoable.piano.view.LoadingView;
import com.fotoable.piano.view.zoom_hover.OnItemSelectedListener;
import com.fotoable.piano.view.zoom_hover.OnZoomAnimatorListener;
import com.fotoable.piano.view.zoom_hover.ZoomHoverGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fotoable on 2017/6/26.
 */

public class SelectDifficultyLevelActivity extends BaseActivity {
    public static String TAG = "SelectDifficultyL";
    private Activity context = SelectDifficultyLevelActivity.this;
    public SongData songData;
    public int songId,userLevel;
    private Map map = new HashMap<String,String>();
    private ArrayList<PlayedData> easyDatas, middleDatas, hardDatas;
    private LinearLayout shareView;
    private LoadingView dialog;
    private TextView mTitle, mSinger, mTVStart, text;
    private ZoomHoverGridView mZoomHoverGridView;
    private SelectDifficultAdapter mAdapter;
    int difficulty = GameActivity.GameDifficulty.easy;
    private int gameType = 0;
    private boolean stopThread = false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //防止在Loading界面返回
                    if (!stopThread) {
                        //启动游戏界面
                        Intent intent = new Intent(context, GameActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_difficut;
    }

    @Override
    protected void initView() {
        mTitle = (TextView) findViewById(R.id.tv_song_name);
        mSinger = (TextView) findViewById(R.id.tv_singer);
        ImageView img_preview = (ImageView) findViewById(R.id.img_preview);
        img_preview.setOnClickListener(noDoubleClickListenernew);
        RelativeLayout mStart = (RelativeLayout) findViewById(R.id.rl_start);
        mStart.setOnClickListener(noDoubleClickListenernew);
        mTVStart = (TextView) findViewById(R.id.tv_start);
        text = (TextView) findViewById(R.id.text);
        mTitle.setTypeface(FontsUtils.getType(FontsUtils.SONGS_TITLE_FONT));
        mSinger.setTypeface(FontsUtils.getType(FontsUtils.SONGS_SINGER_FONT));
        mTVStart.setTypeface(FontsUtils.getType(FontsUtils.BUTTON_FONT));
        text.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        LinearLayout mBack = (LinearLayout) findViewById(R.id.ll_back);
        mBack.setOnClickListener(noDoubleClickListenernew);
        mZoomHoverGridView = (ZoomHoverGridView) findViewById(R.id.zoom_hover_grid_view);
        mZoomHoverGridView.setOnZoomAnimatorListener(new OnZoomAnimatorListener() {
            @Override
            public void onZoomInStart(View view) {
                view.findViewById(R.id.tv_info).setVisibility(View.VISIBLE);
            }

            @Override
            public void onZoomInEnd(View view) {
                view.findViewById(R.id.tv_info).setVisibility(View.VISIBLE);
            }

            @Override
            public void onZoomOutStart(View view) {
                view.findViewById(R.id.tv_info).setVisibility(View.GONE);
            }

            @Override
            public void onZoomOutEnd(View view) {
                view.findViewById(R.id.tv_info).setVisibility(View.GONE);
            }
        });
        dialog = (LoadingView) findViewById(R.id.dialog);
        //分享VIEW
        shareView = (LinearLayout) findViewById(R.id.rl_challenge_friend);
        shareView.setVisibility(getIntent().getBooleanExtra("hasplay", false) ? View.VISIBLE : View.GONE);
        if (getIntent().getBooleanExtra("hasplay", false)) {
            View inviteBg = findViewById(R.id.fb_invite_bg);
            ObjectAnimator anim = ObjectAnimator.ofFloat(inviteBg, "alpha", 0.2f, 1f, 0.2f);
//            ObjectAnimator anim1 = ObjectAnimator.ofFloat(inviteBg, "alpha", 0.8f, 1f, 0.8f);
            anim.setDuration(1000);
//            anim1.setDuration(1000);
            anim.setRepeatCount(1000);
//            anim1.setRepeatCount(1000);
            anim.start();
//            anim1.start();
        }
        shareView.setOnClickListener(noDoubleClickListenernew);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        songId = intent.getIntExtra("songId", 0);
        songData = SharedSongs.getSongFromSongId(songId);
        mTitle.setText(songData.name);
        mSinger.setText(songData.singerName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayedSong playedSong = SharedPlayed.getPlayedFromSongId(songId);
        if (playedSong != null) {
            easyDatas = playedSong.easyDatas;
            middleDatas = playedSong.middleDatas;
            hardDatas = playedSong.hardDatas;
        }
        mAdapter = new SelectDifficultAdapter(getData());
        mZoomHoverGridView.setAdapter(mAdapter);
        mZoomHoverGridView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position, boolean isUserClick) {
                if (isUserClick && position == difficulty) {
                    mZoomHoverGridView.setClickable(false);
                    startGame();
                }
                if (position == 0) {
                    difficulty = GameActivity.GameDifficulty.easy;
                } else if (position == 1) {
                    difficulty = GameActivity.GameDifficulty.middle;
                } else if (position == 2) {
                    userLevel = SharedUser.getDefaultUser().level;
                    if (userLevel < Constant.USER_LEVEL_UNLOCK_HARD_GATE){
                        ToastUtils.showToast(MyApplication.application,getResources().getString(R.string.action_unLock_hard));
                    }
                    difficulty = GameActivity.GameDifficulty.hard;
                }
            }
        });
        mAdapter.notifyDataChanged();
        mZoomHoverGridView.setSelectedItem(difficulty);

    }



    @Override
    protected void onStop() {
        super.onStop();
        dialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        if (dialog.getVisibility() == View.VISIBLE){
            stopThread = true;
            dialog.dismiss();
        }
       this.finish();
    }

    private NoDoubleClickListener noDoubleClickListenernew = new NoDoubleClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.ll_back:
                    finish();
                    break;
                case R.id.img_preview:
                    shareView.setEnabled(false);
                    dialog.show();
                    stopThread = false;
                    map.clear();
                    map.put(AnalyzeConstant.songId,songId+"");
                    AnalyzeConstant.event(AnalyzeConstant.click_preview,map);
                    gameType = 1;   //预览模式
                    MyHttpManager.downloadMid(songData.middle.pathServer, myCallback);
//                    testPreview();

//                    View rootView = findViewById(R.id.root_view);
//                    //测试显示弹窗广告
//                    PopWindowManager.showPopupWindowView(rootView);
                    break;

                case R.id.rl_start:
                    shareView.setEnabled(false);
                    startGame();
                    break;
                case R.id.rl_challenge_friend:
                    startActivity(new Intent(context, FBInviteActivity.class));
                    break;
            }
        }
    };

    private void testPreview(){
        String url =null;
        if(difficulty== GameActivity.GameDifficulty.easy){
            url = songData.easy.pathServer;
        }else if(difficulty== GameActivity.GameDifficulty.middle){
            url = songData.middle.pathServer;
        }else if(difficulty== GameActivity.GameDifficulty.hard){
            url = songData.hard.pathServer;
        }

        MyHttpManager.downloadMid(url, myCallback);
    }

    private void startGame(){
        stopThread = false;
        String url = null;
        if (difficulty == GameActivity.GameDifficulty.easy) {
            url = songData.easy.pathServer;
        } else if (difficulty == GameActivity.GameDifficulty.middle) {
            url = songData.middle.pathServer;
        } else if (difficulty == GameActivity.GameDifficulty.hard) {
            if (userLevel < Constant.USER_LEVEL_UNLOCK_HARD_GATE){
                ToastUtils.showToast(MyApplication.application,getResources().getString(R.string.action_unLock_hard));
                mZoomHoverGridView.setClickable(true);
                return;
            }
            url = songData.hard.pathServer;
        }
        dialog.show();
        map.clear();
        map.put(AnalyzeConstant.type,difficulty+"");
        map.put(AnalyzeConstant.songId,songId+"");
        AnalyzeConstant.event(AnalyzeConstant.click_start_game,map);
        gameType = 0;   //游戏模式
        MyHttpManager.downloadMid(url, myCallback);
    }


    MyHttpManager.MyCallback myCallback = new MyHttpManager.MyCallback() {
        @Override
        public void onError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    ToastUtils.showToast(MyApplication.application,
                            getResources().getString(R.string.download_failed));
                    mZoomHoverGridView.setClickable(true);
                }
            });
        }

        @Override
        public void onSuccess(final String midPath) {
            //midPath 音乐文件地址
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long timeStar = System.currentTimeMillis();
                    //提前在本页面 初始化SoundPool，避免首音节不响的问题
                    GameActivity.mSoundPoolSynth = new SoundPoolSynth(getApplicationContext());
                    //生成音频数据
                    GameActivity.midiEventBean
                            = new MidiFileAnalysis(context, midPath).getMidiEventBean();
                    //根据音频数据,生成游戏界面所需的数据
                    GameActivity.animationDatas
                            = GLUtils.convertNoteData(GameActivity.midiEventBean, 1.0f);
                    GameActivity.difficulty = difficulty;
                    GameActivity.gameType = gameType;
                    GameActivity.songId = songData.id;
                    long timeEnd = System.currentTimeMillis();
                    long time = timeEnd - timeStar;
                    Log.e(TAG,"OpenActivityTime-->>"+time);
                    if (time < 1500) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendEmptyMessage(0);
                }
            }).start();


        }

    };

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("img", R.drawable.easy);
        map.put("star", SharedSongs.starNum(easyDatas));
        map.put("info", R.string.action_easy_info);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.medium);
        map.put("star", SharedSongs.starNum(middleDatas));
        map.put("info", R.string.action_medium_info);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.hard);
        map.put("star", SharedSongs.starNum(hardDatas));
        map.put("info", R.string.action_hard_info);
        list.add(map);
        return list;
    }

}
