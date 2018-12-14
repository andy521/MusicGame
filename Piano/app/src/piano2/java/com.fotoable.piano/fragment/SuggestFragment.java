package com.fotoable.piano.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.activity.SelectDifficultyLevelActivity;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.entity.AllPlayedData;
import com.fotoable.piano.entity.CategoryUI;
import com.fotoable.piano.entity.CategoryUIItem;
import com.fotoable.piano.entity.PlayedSong;
import com.fotoable.piano.entity.SongData;
import com.fotoable.piano.entity.UserData;
import com.fotoable.piano.game.shared.SharedPlayed;
import com.fotoable.piano.game.shared.SharedSongs;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.midi.MyMidiPlayer;
import com.fotoable.piano.utils.ToastUtils;
import com.fotoable.piano.view.GetSongDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by houfutian on 2017/6/14.
 */

public class SuggestFragment extends BaseFragment {
    private static final String TAG = "SuggestFragment";
    private static final String KEY_SELECT="key_classifyName";
    private static final String KEY_POSION="key_classifyId";
    private boolean userIsVip;
    private ListView mListView;
    private CategoryUI categoryUI;
    private String classifyName;
    private ListFragmentAdapter listFragmentAdapter;
    private MySongFragmentAdapter mySongAdapter;
    private int classifyId;
    private ArrayList<CategoryUIItem> listData;
    private ArrayList<PlayedSong> allPlayedlist = null;
    private ArrayList <SongData> songDatas = null;
    private Map map = new HashMap<String,String>();

    public static Fragment newInstance(String classifyName,int classifyId){
        Fragment f=new SuggestFragment();
        Bundle bundle=new Bundle();
        bundle.putString(KEY_SELECT,classifyName);
        bundle.putInt(KEY_POSION,classifyId);
        f.setArguments(bundle);
        return f;
    }


    /**
     * onFragmentVisibleChange中调用
     * 歌曲试听发生变化回掉
     * 在切换所有页面，以及弹奏完成后刷新数据
     */
    public OnSongPlayChangListener mPlayListener = null;
    public interface OnSongPlayChangListener {
        public void onSongPlayChang();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //“内存重启”时保存当前的fragment名字
        outState.putInt(KEY_POSION, classifyId);
        outState.putString(KEY_SELECT, classifyName);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.suggest_fragment, container, false);
            initView(rootView,savedInstanceState);
            listFragmentAdapter = new ListFragmentAdapter(mActivity);
            mySongAdapter = new MySongFragmentAdapter(mActivity);
            if(classifyId == -1){
                mListView.setAdapter(mySongAdapter);
            }else{
                mListView.setAdapter(listFragmentAdapter);
            }
        }
        return rootView;
    }

    /**
     * fragment isVisible
     * @param isVisible true  不可见 -> 可见
     */
    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (classifyId == -1) {
            mPlayListener = mySongAdapter;
        }else {
            mPlayListener = listFragmentAdapter;
        }
        if (mPlayListener != null){
            mPlayListener.onSongPlayChang();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        dataLoad();
    }


    protected void initView(View view, Bundle savedInstanceState) {
        if(savedInstanceState==null){
            Bundle bundle = getArguments();
            if(bundle!=null){
                classifyName=bundle.getString(KEY_SELECT);
                classifyId=bundle.getInt(KEY_POSION);
            }
        } else {
            classifyId = savedInstanceState.getInt(KEY_POSION);
            classifyName = savedInstanceState.getString(KEY_SELECT);
        }
        userIsVip = SharedUser.getVipData();
        mListView = (ListView) view.findViewById(R.id.mlist_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int songId;
                long startTime = System.currentTimeMillis();
                if (classifyId == -1) {
                    songId = allPlayedlist.get(position).id;
                    StarGameActivity(songId, true);
                }else {
                    CategoryUIItem item = listData.get(position);
                    songId = item.songId;
                    List<SongData> songDataList = categoryUI.categoryItem.songList;
                    SongData mSongInfo = songDataList.get(position);
                    if (userIsVip || item.flag == 2 || item.flag == 1 || mSongInfo.cost == 0) {
                        StarGameActivity(songId, item.flag == 2 || item.flag == 1);
                    }else if (item.flag == 0 && mSongInfo.cost != 0){
                        ShowBuySongsDialog(mSongInfo);
                    }
                }
                map.clear();
                map.put(AnalyzeConstant.songId, songId+"");
                AnalyzeConstant.event(AnalyzeConstant.click_song_item,map);
                Log.e(TAG,"OpenActivityTime-->>"+(System.currentTimeMillis()-startTime));
            }
        });
    }

    /**
     * 跳转开始游戏页面
     * @param songId
     */
    public void StarGameActivity(int songId, boolean hasplay) {
        Intent intent = new Intent(mActivity, SelectDifficultyLevelActivity.class);
        intent.putExtra("songId", songId);
        intent.putExtra("hasplay", hasplay);
        startActivity(intent);
    }

    /**
     * 购买dialog
     * @param mSongInfo
     */
    public void ShowBuySongsDialog(final SongData mSongInfo){
        GetSongDialog dialog = new GetSongDialog(mActivity,mSongInfo.name,mSongInfo.cost,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserData userData = SharedUser.getDefaultUser();
                        if (userData.coin < mSongInfo.cost){
                            ToastUtils.showToast(MyApplication.application, getResources().getString(R.string.action_coins_not));
                        }else {
                            userData.coin -= mSongInfo.cost;
                            SharedUser.updateDefaultUser(userData);
                            SharedPlayed.addPaidSong(mSongInfo.id);
                            //停止音乐播放
                            MyMidiPlayer.stop();
                            //直接打开game页面
                            StarGameActivity(mSongInfo.id,false);
                        }
                    }
                });
        dialog.show();
    }

    protected void dataLoad() {
        long startTime = System.currentTimeMillis();
        //  MySongS 分类列表
        if (classifyId == -1){
            AllPlayedData allPlayedData = SharedPlayed.getAllPlayedData();
            if (allPlayedData != null){
                allPlayedlist = allPlayedData.playedSongList;
                songDatas = new ArrayList<>();
                for (PlayedSong playedSong : allPlayedlist){
                    SongData mSongInf = SharedSongs.getSongFromSongId(playedSong.id);
                    songDatas.add(mSongInf);
                }
                mySongAdapter.setSongDatas(allPlayedlist,songDatas);
            }
        }
        //   其他分类列表
        else {
            categoryUI = SharedSongs.getCategoryUI(classifyId);
            if(categoryUI != null) {
                listData = categoryUI.listData;
                listFragmentAdapter.setCategoryUI(categoryUI);
            }
            Log.e(TAG,"OpenTime-->>"+(System.currentTimeMillis()-startTime));
        }
    }
}
