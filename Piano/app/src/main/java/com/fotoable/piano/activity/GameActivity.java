package com.fotoable.piano.activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.fotoable.piano.R;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.entity.SongData;
import com.fotoable.piano.game.FGLView;
import com.fotoable.piano.game.entity.TrackAnimationData;
import com.fotoable.piano.game.shared.SharedSongs;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.game.utils.GLUtils;
import com.fotoable.piano.midi.SoundPoolSynth;
import com.fotoable.piano.midi.bean.MidiEventBean;
import com.fotoable.piano.utils.ToastUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

/**
 * Created by damon on 12/06/2017.
 */

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    @IntDef({GameDifficulty.easy, GameDifficulty.middle, GameDifficulty.hard})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GameDifficulty {
        int easy = 0;
        int middle = 1;
        int hard = 2;
    }

    //    public static final String BUNDLE_KEY_DIFFICULTY = "bundle_key_difficulty";
    public static final String BUNDLE_KEY_SONG_ID = "bundle_key_song_id";
    private static final String TAG = "GameActivity";
    private FGLView fglview;
    public RelativeLayout pause_layout;
    private RelativeLayout play_layout;
    private RelativeLayout start_layout;
    private RelativeLayout quit_layout;


    /**
     * mid 解析数据
     */
    public static MidiEventBean midiEventBean;
    /**
     * 界面所需的数据
     */
    public static TrackAnimationData animationDatas;

    /**
     * 播放音频的类
     */
    public static SoundPoolSynth mSoundPoolSynth;

    /**
     * 歌曲的id
     */
    public static Integer songId;
    /**
     * 难度
     */
    public static Integer difficulty;

    /**
     * 0:游戏模式  1:预览模式
     * -1:为默认值
     */
    public static Integer gameType = -1;

    public SongData songData;

    /**
     * true:不显示引导文字
     * false: 显示引导文字
     */
    public boolean isShowGuide0;

    public View guideLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检测耳机是否插入
        if (((AudioManager) getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn()) {
            SoundPoolSynth.setVolumeScaleForHeadphones(1);
        } else {
            SoundPoolSynth.setVolumeScaleForHeadphones(0);
        }
        checkIntent();
        setContentView(R.layout.activity_game);
        fglview = (FGLView) findViewById(R.id.fglview);
        pause_layout = (RelativeLayout) findViewById(R.id.pause_layout);
        play_layout = (RelativeLayout) findViewById(R.id.play_layout);
        start_layout = (RelativeLayout) findViewById(R.id.start_layout);
        quit_layout = (RelativeLayout) findViewById(R.id.quit_layout);
        play_layout.setOnClickListener(this);
        start_layout.setOnClickListener(this);
        quit_layout.setOnClickListener(this);
        pause_layout.setVisibility(View.GONE);
        pause_layout.setClickable(true);
        guideLayout = findViewById(R.id.guide_layout);
        isShowGuide0 = SharedUser.getIsShowGuide0();
        if (gameType != null && gameType == 1) {
            //预览模式,不显示引导文字
            isShowGuide0 = true;
        }
        guideLayout.setVisibility(isShowGuide0 ? View.GONE : View.VISIBLE);


    }


    private void checkIntent() {
        if (animationDatas == null || midiEventBean == null) {
            Log.e(TAG, "======error animationDatas==null||midiEventBean==null");
            ToastUtils.showToast(this, getResources().getString(R.string.midi_analysis_fail));
            finish();
            return;
        }
        if (animationDatas.nodeList == null || animationDatas.nodeList.size() == 0) {
            Log.e(TAG, "======error animationDatas.nodeList==null||animationDatas.nodeList.size()==0");
            finish();
            return;
        }
        if (songId == null) {
            Log.e(TAG, "======error songId==null");
            finish();
            return;
        }
        if (gameType == null || gameType == -1) {
            Log.e(TAG, "======error gameType==null");
            finish();
            return;
        }
        if (difficulty == null) {
            Log.e(TAG, "======error difficulty null-->>");
            finish();
            return;
        }
        if (difficulty != GameActivity.GameDifficulty.easy && difficulty != GameDifficulty.middle && difficulty != GameDifficulty.hard) {
            Log.e(TAG, "======error difficulty error-->>" + difficulty);
            finish();
            return;
        }
        songData = SharedSongs.getSongFromSongId(songId);
        if (songData == null) {
            Log.e(TAG, "======error songData==null");
            finish();
            return;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        fglview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fglview.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_layout:
                Log.d(TAG, "click start_layout");
                start_layout.setClickable(false);
                fglview.renderer.gameRender.cleanData();
                GameActivity.animationDatas = GLUtils.convertNoteData(GameActivity.midiEventBean, 1.0f);
                fglview.renderer.gameRender.trackManager.startTrack();
                pause_layout.setVisibility(View.GONE);
                start_layout.setClickable(true);
                break;
            case R.id.play_layout:
                Log.d(TAG, "click play_layout");
                pause_layout.setVisibility(View.GONE);
                fglview.renderer.gameRender.trackManager.startTrack();
                break;
            case R.id.quit_layout:
                Log.d(TAG, "click quit_layout");
                HashMap map = new HashMap<>();
                map.put("item", GameActivity.gameType + "");
                map.put("hasfinish","false");
                AnalyzeConstant.event(AnalyzeConstant.finish_game, map);
                finish();
                break;
            default:
                //don't forget default
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (pause_layout.getVisibility() == View.VISIBLE) {
            fglview.renderer.gameRender.trackManager.startTrack();
            pause_layout.setVisibility(View.GONE);
        } else {
            fglview.renderer.gameRender.trackManager.stopTrack();
            pause_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        midiEventBean = null;
        animationDatas = null;
        songId = null;
        difficulty = null;
        gameType = -1;
        if (this.mSoundPoolSynth != null) {
            this.mSoundPoolSynth.onStop();
        }
    }
}
