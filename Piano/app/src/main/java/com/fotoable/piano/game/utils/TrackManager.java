package com.fotoable.piano.game.utils;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.fotoable.piano.activity.GameActivity;
import com.fotoable.piano.activity.GameOverActivity;
import com.fotoable.piano.ad.AdConstant;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.entity.PlayedData;
import com.fotoable.piano.entity.SongData;
import com.fotoable.piano.game.GameRender;
import com.fotoable.piano.game.entity.TouchData;
import com.fotoable.piano.game.entity.TrackNodeData;
import com.fotoable.piano.game.shared.SharedPlayed;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.midi.bean.MidiNote;

import java.util.HashMap;

/**
 * Created by damon on 14/06/2017.
 */

public class TrackManager {
    private static final String TAG = "TrackManager";

    public @interface TrackState {
        int idel = 0;//空闲状态
        int ing = 1;//正在播放
        int pause = 2;//暂停状态
        int end = 3;//结束
    }

    /**
     * 本次音符开始播放的时间
     */
    public volatile long lastPlayTime;
    /**
     * 已经播放了多长时间 单位:毫秒
     */
    public volatile int playingTime;

    public static final float REFRESH_TIME = 16.666667f;

    /**
     * 处于屏幕上绘制的 最下方的音节的 index
     * 默认值为:0
     */
    public volatile int startIndex = 0;
    /**
     * 处于屏幕上绘制的 最上方的音节的 index
     * 默认值为:0
     */
    public volatile int endIndex = -1;

    /**
     * 进入可触摸区域的index
     */
    public volatile int inTouchAreaIndex = -1;
    /**
     * 进度
     */
    public volatile float progressPercent = 0;


    private GameRender gameRender;

//    private Timer timerTracker;
//
//    private TimerTask taskTracker;

    public NodeLineManager nodeLineManager;

    public NodeCircleManager nodeCircleManager;

    /**
     * 游戏进行状态
     */
    public
    @TrackState
    int trackState = TrackState.idel;

    private boolean isShowGuide0;

    public TrackManager(GameRender gameRender) {
        this.gameRender = gameRender;
        nodeLineManager = new NodeLineManager(gameRender);
        nodeCircleManager = new NodeCircleManager(gameRender);
        isShowGuide0 = ((GameActivity) gameRender.mView.getContext()).isShowGuide0;
    }

    public void init() {

    }

    private void testStartTrack0() {

        gameRender.mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTrack();
            }
        }, 1000);
    }

    /**
     * 开始音节下落
     */
    public synchronized void startTrack() {
        Log.d(TAG, "startTrack");
        lastPlayTime = -1;
        trackState = TrackState.ing;
        gameRender.mView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void compute() {
        if (trackState != TrackState.ing) {
//            Log.e(TAG, "error-->>, trackState != TrackState.ing");
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (lastPlayTime == -1) {
            //计时器第一次执行
            Log.e(TAG, "startTrack, Thread-->>" + Thread.currentThread().getName());
            lastPlayTime = currentTime;
            return;
        }
        if (GameActivity.animationDatas == null) {
            Log.e(TAG, "startTrack, GameActivity.animationDatas==null");
            stopTrack();
            return;
        }
//                TrackNodeData lastItem = GameActivity.animationDatas.nodeList.get(0);
        TrackNodeData lastItem = GameActivity.animationDatas.nodeList.get(GameActivity.animationDatas.nodeList.size() - 1);
        long allTime = lastItem.startTime;
        if (progressPercent >= 1f) {
            Log.d(TAG, "game over, playingTime-->>" + playingTime + ", lastItem.endTime-->>" + lastItem.startTime + GLConstants.getOneNodeDropTimeAll());
            endIndex = -1;
            startIndex = -1;
            stopTrack();
            trackState = TrackState.end;
            onTrackFinish();
            return;
        }
        //该方法主要干了这些事儿
        //1.检查屏幕上都需要绘制哪些音节点
        //2.更新绘制音节的y轴上的值
        int tempEndIndex = endIndex;
        int tempStartIndex = startIndex;
        int size = GameActivity.animationDatas.nodeList.size();
//                Log.d(TAG, "ing playingTime-->>" + playingTime);
        if (isStopAtFootsteps()) {
            //逻辑: 只有当最下方的并行的音节全部被点击后,才继续播放.
            if (inTouchAreaIndex != -1 && inTouchAreaIndex >= tempStartIndex) {
                //表示已经有至少一个音节进入了五线谱区域
                boolean isAllHit = true;
                TrackNodeData itemTemp = null;
                for (int i = inTouchAreaIndex; i >= tempStartIndex; i--) {
                    itemTemp = GameActivity.animationDatas.nodeList.get(i);
                    if (!itemTemp.isDismiss) {
//                        Log.w(TAG, "stop-->>!item.isDismiss index-->>" + i);
                        isAllHit = false;
                        break;
                    }
                }
                if (!isAllHit) {
                    //有音节没有被点击掉
                    if (GameActivity.gameType != null && GameActivity.gameType != 1) {
                        stopTrack();
                        return;
                    }
                }
            }

        }

        while (true) {
            //先检查屏幕上的绘制点数
            //startIndex 先比当前的结束时间, 在+1比下一个
            final TrackNodeData item = GameActivity.animationDatas.nodeList.get(tempStartIndex);

//            if (playingTime > item.startTime + GLConstants.getOneNodeDropTimeAll()) {
            if (item.translateCircleY <= GLConstants.getDismissBottomY()) {
                if ((tempStartIndex + 1) > (size - 1)) {
                    break;
                }
                //表示该点已不再屏幕上了.且没消失
                if (!item.isDismiss) {
                    gameRender.mView.post(new Runnable() {
                        @Override
                        public void run() {
                            //如果该点没有被点击,则连续中断
                            gameRender.continuousData.currentContinuousHit.set(0);
                            gameRender.continuousData.lastHitIndex = -1;
                            //播放声音
                            final MidiNote midiNote = GameActivity.midiEventBean.getMidiEventList().get(item.index0).getmMidiNote().get(item.index1);
                            GameActivity.mSoundPoolSynth.noteOn(midiNote.getChannel(), midiNote.getNoteValue(), midiNote.getVelocity(), midiNote.getTime());
                        }
                    });
                }

                tempStartIndex++;
//                        Log.d(TAG, "playingTime0-->>" + playingTime + ", item.endTime-->>" + item.endTime + ", tempStartIndex-->>" + tempStartIndex);
            } else {
                break;
            }
        }
        while (true) {
            //先检查屏幕上的绘制点数
            //endIndex 先+1比下一个开始时间,
            if ((tempEndIndex + 1) > (size - 1)) {
                break;
            }
            float lastTranslateY = GLConstants.getDismissBottomY();
            boolean isDismiss = false;
            if (tempEndIndex != -1) {
                TrackNodeData itemLast = GameActivity.animationDatas.nodeList.get(tempEndIndex);
                lastTranslateY = itemLast.translateCircleY;
                isDismiss = itemLast.isDismiss;
            }
            TrackNodeData item = GameActivity.animationDatas.nodeList.get(tempEndIndex + 1);
//            Log.d(TAG,"lastTranslateY-->>"+lastTranslateY+", item.deltaLast-->>"+item.deltaLast);
            if (item.deltaLast >= lastTranslateY || isDismiss) {
                tempEndIndex++;
            } else {
                break;
            }
        }
        TrackNodeData itemLast = null;
        int startTemp = -1;
        int lastDismissIndex = -1;//最后一个dismiss的index
        for (int i = tempStartIndex; i <= tempEndIndex; i++) {
            //为屏幕上的每个点修改数据
            TrackNodeData item = GameActivity.animationDatas.nodeList.get(i);
//            item.translateCircleY += GLConstants.getDropDeltaY();
            if (item.isDismiss) {
                lastDismissIndex = i;
                continue;
            }
            if (startTemp == -1) {
                startTemp = i;
                if (item.startTime0 == -1) {
                    item.startTime0 = playingTime - GLUtils.getDynamicStartTime(item.translateCircleY);
                    //根据translateCircleY计算出开始时间,因为从位置推倒开始时间得到的结果没有偏差
//                    Log.d(TAG,"item.startTime0-->>"+item.startTime0);
                }
            }
            if (i == startTemp) {
                item.translateCircleY = GLUtils.getDynamicSpeed(playingTime - item.startTime0);

                if (GameActivity.gameType != null && GameActivity.gameType == 1) {
                    if (playingTime >= item.startTime + GLConstants.getOneNodeDropTimeFootsteps()) {
//                        item.isDismiss = true;
//                        item.hasLine = false;
                        MotionEvent tempMotionEvent  = MotionEvent.obtain(0,0,MotionEvent.ACTION_DOWN,-100,-100,0);
                        gameRender.touchManager.handleTouch(tempMotionEvent);


                        final MidiNote midiNote = GameActivity.midiEventBean.getMidiEventList().get(item.index0).getmMidiNote().get(item.index1);
                        GameActivity.mSoundPoolSynth.noteOn(midiNote.getChannel(), midiNote.getNoteValue(), midiNote.getVelocity(), midiNote.getTime());
                        startTrack();
                    }
                }
//                Log.d(TAG, "item.translateCircleY-->>" + item.translateCircleY + ", startTemp-->>" + startTemp + ", playingTime-->>" + playingTime + " item.startTime-->>" + item.startTime);
            } else {
                item.translateCircleY = itemLast.translateCircleY + (GLConstants.getAppearTopY() - item.deltaLast);
            }
            itemLast = item;

            //判断该音节是否进入了五线谱区域
            float[] touchXY = GLUtils.position2touchXY(item.translateCircleX, item.translateCircleY);
            boolean inFootstepsArea = gameRender.sceneManager.footstepsManager.checkInFootstepsArea2(touchXY[0], touchXY[1]);
            if (inFootstepsArea) {
//                        Log.d(TAG,"inFootstepsArea, index-->>"+i);
                inTouchAreaIndex = i;
            }
//            testAutoHit(item,touchXY);//test
//                    Log.d(TAG, ", startTime-->>" + item.startTime + ", endTime-->>" + item.endTime + ", tempStartIndex-->>" + tempStartIndex + ", tempEndIndex-->>" + tempEndIndex + ", translateX-->>" + item.translateCircleX + ", translateY-->>" + item.translateCircleY);
        }


//        playingTime += (currentTime - lastPlayTime);
        lastPlayTime = playingTime;
        playingTime += REFRESH_TIME;
        endIndex = tempEndIndex;
        startIndex = tempStartIndex;

        //更新进度条进度
        if (lastDismissIndex != -1) {
            progressPercent = GameActivity.animationDatas.nodeList.get(lastDismissIndex).startTime / (float) allTime;
            gameRender.sceneManager.progressBarManager.translateX = -GLConstants.getScreenRatioWh() * 2 * (1f - progressPercent);
        }
//        Log.e(TAG,"progressPercent-->>"+progressPercent);
//                Log.d(TAG, ", playingTime-->>" + playingTime + ", startIndex-->>" + startIndex + ", endIndex-->>" + endIndex + ",nodeList.size-->>" + GameActivity.animationDatas.nodeList.size()+", progressPercent-->>"+progressPercent);
//        gameRender.sceneManager.progressBarManager.translateX = -GLConstants.getScreenRatioWh() * 2 * (1f - playingTime / (float) allTime);
        trackState = TrackState.ing;
//        Log.e(TAG, "compute time play-->>" + System.currentTimeMillis() + ", startIndex-->>" + startIndex + ", translateCircleY-->>" + GameActivity.animationDatas.nodeList.get(startIndex).translateCircleY + ", progressBar-->>" + gameRender.sceneManager.progressBarManager.translateX);
//        Log.e(TAG, "compute time play-->>" + System.currentTimeMillis() + ", startIndex-->>" + startIndex + ", endIndex-->>" + endIndex);
//        gameRender.mView.requestRender();
    }


    /**
     * 测试自动被点中,用于录制宣传视频
     */
    public void testAutoHit(TrackNodeData item, float[] touchXY) {
        boolean inFootstepsArea = gameRender.sceneManager.footstepsManager.checkInFootstepsArea3(touchXY[0], touchXY[1]);
        if (!inFootstepsArea) {
            return;
        }
        if (item.isHandleHit) {
            return;
        }
        item.isHandleHit = true;
        MotionEvent motionEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, touchXY[0], touchXY[1], 0);
        gameRender.touchManager.handleTouch(motionEvent);

    }

    /**
     * 暂停音节下落
     */
    public void stopTrack() {
        Log.d(TAG, "stopTrack");
        gameRender.continuousData.lastHitIndex = -1;
        gameRender.continuousData.currentContinuousHit.set(0);
        trackState = TrackState.pause;
    }

    public void drawTrack() {
        compute();
        if (startIndex == -1 || endIndex == -1) {
//            Log.e(TAG, "======error startIndex == -1 || endIndex == -1");
            return;
        }
        if (GameActivity.animationDatas == null) {
            Log.e(TAG, "======error GameActivity.animationDatas == null");
            return;
        }
        if (GameActivity.animationDatas.nodeList == null || GameActivity.animationDatas.nodeList.size() == 0) {
            Log.e(TAG, "======error animationDatas.nodeList==null||animationDatas.nodeList.size()==0");
            return;
        }
        for (int i = startIndex; i <= endIndex; i++) {
            //只绘制在屏幕上的点
            TrackNodeData item = GameActivity.animationDatas.nodeList.get(i);
            drawAnimation1(item);
        }

    }

    private void drawAnimation1(final TrackNodeData item) {
//        Log.d(TAG, "drawAnimation1" + ", item-->>" + item.toString());
        gameRender.setProjection();
        if (item.hasLine) {
            nodeLineManager.drawNodeLine(item.translateCircleX, item.translateCircleY, item.nodeType);
        }
        if (!item.isDismiss) {
            nodeCircleManager.drawNodeCircle(item.translateCircleX, item.translateCircleY, item.scaleCircleX, item.nodeType);
        }
    }


    public TouchData checkIsHit(float touchX, float touchY) {

        //        boolean result = false;
        float radius = GLConstants.getOneCircleNodeWidthPx() * 3;
        if (startIndex == -1 || endIndex == -1) {
            //因为 startIndex endIndex的默认值为-1
            return new TouchData(false, false, NodeCircleManager.NodeType.NODE_TYPE_1, 0, false,null);
        }
        boolean inPauseBtnArea = gameRender.sceneManager.pauseBtnManager.checkInPauseBtnArea(touchX, touchY);
        if (inPauseBtnArea) {
            AnalyzeConstant.event(AnalyzeConstant.click_paused_resume);
            //击中了暂停按钮
            testChangeTrackState();
            return new TouchData(false, true, NodeCircleManager.NodeType.NODE_TYPE_1, 0, false,null);
        }

//        boolean inFootstepsArea = gameRender.sceneManager.footstepsManager.checkInFootstepsArea(touchX, touchY);
//        if (!inFootstepsArea) {
//            //击中的位置不再五线谱区域
//            return new TouchData(false, false, NodeCircleManager.NodeType.NODE_TYPE_1, 0);
//        }


        TrackNodeData targetItem = null;//最下方的那个点的item
        int targetIndex = 0;//最下方的那个点的index
        boolean isHit = false;//是否击中了最下方的那个点
        boolean isDismiss = false;//是否消失最下方的那个点
        for (int i = startIndex; i <= endIndex; i++) {
            //为屏幕上的每个点修改数据
            //第一次循环,找出击中的点
            TrackNodeData item = GameActivity.animationDatas.nodeList.get(i);
            if (item.isHit || item.isDismiss) {
                //只能被击中一次
                continue;
            }
            float[] xy = GLUtils.position2touchXY(item.translateCircleX, item.translateCircleY);
//            boolean inFootstepsArea0 = gameRender.sceneManager.footstepsManager.checkInFootstepsArea(xy[0], xy[1]);
//            if (!inFootstepsArea0) {
//                //过滤掉不再五线谱区域的音节
//                continue;
//            }
            float deltaX0 = Math.abs(xy[0] - touchX);
            float deltaY0 = Math.abs(xy[1] - touchY);
            float deltaX = radius - deltaX0;
            float deltaY = radius - deltaY0;
//            Log.e(TAG, "radius-->>" + radius + ", xy[0]-->>" + xy[0] + ", xy[1]-->>" + xy[1] + ", touchX-->>" + touchY + ", touchY-->>" + touchY);

            targetItem = item;
            targetIndex = i;
            isDismiss = true;
            if (deltaX >= 0 && deltaY >= 0) {
//                float temp = (float) Math.sqrt(deltaX0 * deltaX0 + deltaY0 * deltaY0);
                //damontodo 被击中的点
                Log.w(TAG, "========touch 击中 -->>" + ", targetIndex-->>" + targetIndex + ", i-->>" + i + ", xy[0]-->>" + xy[0] + ", xy[1]-->>" + xy[1] + ", touchX-->>" + touchX + ", touchY-->>" + touchY);
                //只判断最下方的那个点是否在触摸的有效范围内
                isHit = true;
            } else {
                Log.w(TAG, "========touch dismiss -->>" + ", targetIndex-->>" + targetIndex + ", i-->>" + i + ", xy[0]-->>" + xy[0] + ", xy[1]-->>" + xy[1] + ", touchX-->>" + touchX + ", touchY-->>" + touchY);

            }
            break;//只要找到最下方的且未击中的点就结束循环.
        }
        if (isDismiss) {
            targetItem.isHit = isHit;
        }

        if (isDismiss) {
            if (!isHit) {
                gameRender.continuousData.lastHitIndex = -1;
                gameRender.continuousData.currentContinuousHit.set(0);
            }
            targetItem.isDismiss = true;


            MidiNote midiNote = GameActivity.midiEventBean.getMidiEventList().get(targetItem.index0).getmMidiNote().get(targetItem.index1);
            //播放声音
            GameActivity.mSoundPoolSynth.noteOn(midiNote.getChannel(), midiNote.getNoteValue(), midiNote.getVelocity(), midiNote.getTime());
            gameRender.sceneManager.startLogoAnimation();
            //开始
            if (trackState == TrackState.idel || trackState == TrackState.pause) {
                startTrack();
            }

        }
        gameRender.mView.requestRender();
        if (isDismiss && targetItem.countParallel > 1) {
            //表示有音节被击中 && 当前音节是平行音节, 找出哪个需要画线, 哪个不需要画线
            //damontodo 核心逻辑: 剩余未击中的平行音节第一个到最后一个中间点都连线.其余不连线
            //第一步:找出未击中的第一个点的index 和最后一个点的index
            //第二步:循环 依次判断该是否需要画线
            int firstIndex = targetIndex - targetItem.indexParallel;
            int lastIndex = targetIndex + (targetItem.countParallel - 1 - targetItem.indexParallel);

            //没有被击中的开始index
            int notHitStartIndex = -1;
            //没有被击中的结束的index
            int notHitEndIndex = -1;
            for (int i = firstIndex; i <= lastIndex; i++) {
                TrackNodeData item = GameActivity.animationDatas.nodeList.get(i);
                if (!item.isDismiss && notHitStartIndex == -1) {
                    notHitStartIndex = i;
                }
                if (!item.isDismiss) {
                    notHitEndIndex = i;
                }
            }
            for (int i = firstIndex; i <= lastIndex; i++) {
                TrackNodeData item = GameActivity.animationDatas.nodeList.get(i);
                if (i < notHitStartIndex) {
                    //表示已点击过了
                    item.hasLine = false;
                } else if (i < notHitEndIndex) {
                    item.hasLine = true;
                } else {
                    //i >= notHitEndIndex
                    item.hasLine = false;
                }

            }

        }
        @NodeCircleManager.NodeType int nodeType = NodeCircleManager.NodeType.NODE_TYPE_1;
        if (isDismiss) {
            if (targetItem.countParallel == 1) {
                nodeType = NodeCircleManager.NodeType.NODE_TYPE_1;
            } else if (targetItem.countParallel == 2) {
                nodeType = NodeCircleManager.NodeType.NODE_TYPE_2;
            } else if (targetItem.countParallel == 3) {
                nodeType = NodeCircleManager.NodeType.NODE_TYPE_3;
            } else if (targetItem.countParallel == 4) {
                nodeType = NodeCircleManager.NodeType.NODE_TYPE_4;
            }
            checkGuideUi();
        }
        return new TouchData(isHit, false, nodeType, targetIndex, isDismiss,targetItem);
    }

    private void testChangeTrackState() {
        if (trackState == TrackState.ing) {
            stopTrack();
        } else if (trackState == TrackState.idel || trackState == TrackState.pause) {
            startTrack();
        }
    }


    /**
     * 表示当音节落到五线谱那里时是否暂停
     *
     * @return true:暂停
     */
    public boolean isStopAtFootsteps() {
//        if (GameActivity.gameType != null && GameActivity.gameType != 0) {
//            return false;
//        }
//        if (GameActivity.difficulty == GameActivity.GameDifficulty.middle || GameActivity.difficulty == GameActivity.GameDifficulty.hard) {
//            //middle 和 hard的游戏不暂停
//            return false;
//        }

        return true;
    }

    public void cleanData() {
        lastPlayTime = 0;
        playingTime = 0;
        startIndex = 0;
        endIndex = 0;
        trackState = TrackState.idel;
        inTouchAreaIndex = -1;
    }

    /**
     * 游戏结束
     */
    private void onTrackFinish() {
        AdConstant.isShowPlayFinish = true;
        GameActivity activity = (GameActivity) gameRender.mView.getContext();
        if (GameActivity.gameType != null && GameActivity.gameType == 0) {

            PlayedData playedData = new PlayedData();
            playedData.time = System.currentTimeMillis();
            playedData.hitCount = gameRender.hitNodeCount.get();
            playedData.touchCount = gameRender.touchCount.get();
            playedData.score = gameRender.totalScore.get();
            getReward(playedData, activity.songData);
            SharedPlayed.addPlayedData(GameActivity.songId, GameActivity.difficulty, playedData);

            Log.d(TAG, "本次 游戏数据-->>" + playedData.toString());
        }
        delayFinish();
    }

    private void delayFinish() {

        gameRender.mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameRender == null || gameRender.mView == null || gameRender.mView.getContext() == null) {
                    return;
                }
                GameActivity activity = (GameActivity) gameRender.mView.getContext();
                if (activity == null) {
                    return;
                }
                if (GameActivity.gameType == null) {
                    return;
                }
                HashMap map = new HashMap<>();
                map.put("item", GameActivity.gameType + "");
                map.put("hasfinish", GameActivity.gameType == 0 ? "true" : "false");
                AnalyzeConstant.event(AnalyzeConstant.finish_game, map);
                if (GameActivity.gameType == 0) {
                    Intent i = new Intent(activity, GameOverActivity.class);
                    i.putExtra(GameOverActivity.SONGID, GameActivity.songId);
                    i.putExtra(GameOverActivity.DIFFICULTY, GameActivity.difficulty);
                    activity.startActivity(i);
                    activity.finish();
                } else if (GameActivity.gameType == 1) {
                    activity.finish();
                }

            }
        }, 1000);

    }

    private void getReward(PlayedData playedData, SongData songData) {
        int allNode = GameActivity.animationDatas.nodeList.size();
        int totalScore = gameRender.totalScore.get();
        int star;
        if (totalScore < allNode) {
            //评星 0
            star = 0;
        } else if (totalScore < allNode * 2) {
            //评星 1
            star = 1;
        } else if (totalScore < allNode * 3) {
            //评星 2
            star = 2;
        } else {
            //评星 3
            star = 3;
        }
        getCoinAndXp(star, playedData, songData);

    }

    private void getCoinAndXp(int star, PlayedData playedData, SongData songData) {
        playedData.star = star;
        if (GameActivity.difficulty == GameActivity.GameDifficulty.easy) {
            playedData.xp = getXp(songData.easy.xp);
            playedData.coin = songData.easy.reward / 4 * (star + 1);
        } else if (GameActivity.difficulty == GameActivity.GameDifficulty.middle) {
            playedData.xp = getXp(songData.middle.xp);
            playedData.coin = songData.middle.reward / 4 * (star + 1);
        } else if (GameActivity.difficulty == GameActivity.GameDifficulty.hard) {
            playedData.xp = getXp(songData.hard.xp);
            playedData.coin = songData.hard.reward / 4 * (star + 1);
        }

    }

    private int getXp(int maxXp) {
        return (int) (maxXp * (float) gameRender.hitNodeCount.get() / (float) gameRender.touchCount.get());
    }

    private void checkGuideUi() {
        if (isShowGuide0) {
            return;
        }
        gameRender.mView.post(new Runnable() {
            @Override
            public void run() {
                ((GameActivity) gameRender.mView.getContext()).guideLayout.setVisibility(View.GONE);
            }
        });
        SharedUser.setIsShowGuide0(true);

    }
}
