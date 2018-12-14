package com.fotoable.piano.fragment;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.entity.CategoryUI;
import com.fotoable.piano.entity.CategoryUIItem;
import com.fotoable.piano.entity.SongData;
import com.fotoable.piano.fragment.SuggestFragment.OnSongPlayChangListener;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.http.MyHttpManager;
import com.fotoable.piano.midi.MyMidiPlayer;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.utils.ToastUtils;
import com.fotoable.piano.view.StarLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fotoable on 2017/6/28.
 */

public class ListFragmentAdapter extends BaseAdapter implements OnSongPlayChangListener {
    private Activity context;
    private CategoryUI categoryUI;
    private LayoutInflater inflater;
    private SongData mSongInfo;
    private boolean userIsVipData;
    private List<SongData> songDataList;
    private Map map = new HashMap<String,String>();

    public ListFragmentAdapter(Activity context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        userIsVipData = SharedUser.getVipData();
    }

    public void setCategoryUI(CategoryUI categoryUI){
        this.categoryUI = categoryUI;
        songDataList = categoryUI.categoryItem.songList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return songDataList == null ? 0 : songDataList.size();
    }

    @Override
    public SongData getItem(int position) {
        return songDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_song, null);
            holder.ll_play = (LinearLayout) convertView.findViewById(R.id.ll_play);
            holder.ll_star = (LinearLayout) convertView.findViewById(R.id.ll_star);
            holder.img_play = (ImageView) convertView.findViewById(R.id.img_play);
            holder.img_new = (ImageView) convertView.findViewById(R.id.img_new);
            holder.img_playing = (ImageView) convertView.findViewById(R.id.img_playing);
            holder.img_is_vip = (ImageView) convertView.findViewById(R.id.img_is_vip);
            holder.tv_song_name = (TextView) convertView.findViewById(R.id.tv_song_name);
            holder.tv_song_name.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
            holder.tv_singer = (TextView) convertView.findViewById(R.id.tv_singer);
            holder.tv_singer.setTypeface(FontsUtils.getType(FontsUtils.SONGS_SINGER_FONT));
            holder.tv_coin = (TextView) convertView.findViewById(R.id.tv_coin);
            holder.tv_coin.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
            holder.bt_play = (TextView) convertView.findViewById(R.id.bt_play);
            holder.bt_play.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
            holder.bt_buy = (LinearLayout) convertView.findViewById(R.id.bt_buy);
            holder.easy_star = (StarLayout) convertView.findViewById(R.id.easy_star);
            holder.medium_star = (StarLayout) convertView.findViewById(R.id.medium_star);
            holder.hard_star = (StarLayout) convertView.findViewById(R.id.hard_star);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        CategoryUIItem item = categoryUI.listData.get(position);
        mSongInfo = songDataList.get(position);
        holder.tv_song_name.setText(mSongInfo.name);
        holder.tv_singer.setText(mSongInfo.singerName);

        if (mSongInfo.isNew){
            holder.img_new.setVisibility(View.VISIBLE);
        }else {
            holder.img_new.setVisibility(View.GONE);
        }

        if (mSongInfo.isVip){
            holder.img_is_vip.setVisibility(View.VISIBLE);
        }else {
            holder.img_is_vip.setVisibility(View.GONE);
        }

        if (item.flag == 0){                     //显示金币
            holder.ll_play.setVisibility(View.VISIBLE);
            holder.ll_star.setVisibility(View.GONE);
            holder.bt_play.setVisibility(View.GONE);
            holder.bt_buy.setVisibility(View.VISIBLE);
            holder.tv_coin.setText(mSongInfo.cost+"");
        }

        //vip 用户
        if (userIsVipData || item.flag == 1 || mSongInfo.cost == 0){  //显示play
            holder.ll_play.setVisibility(View.VISIBLE);
            holder.ll_star.setVisibility(View.GONE);
            holder.bt_play.setVisibility(View.VISIBLE);
            holder.bt_buy.setVisibility(View.GONE);
        }

        if (item.flag == 2){        //星星
            holder.ll_play.setVisibility(View.GONE);
            holder.ll_star.setVisibility(View.VISIBLE);
            holder.easy_star.setStarNum(item.difficultyData.easyStar);
            holder.medium_star.setStarNum(item.difficultyData.middleStar);
            holder.hard_star.setStarNum(item.difficultyData.hardStar);
        }
        holder.img_playing.setImageResource(R.drawable.playing_animlist);
        AnimationDrawable animationDrawable = (AnimationDrawable) holder.img_playing.getDrawable();
        if (!mSongInfo.isPlay){
            holder.img_play.setImageResource(R.drawable.play);
            holder.img_playing.setVisibility(View.GONE);
            if (animationDrawable.isRunning()) {
                animationDrawable.stop();
            }
        }else {
            holder.img_play.setImageResource(R.drawable.pause);
            holder.img_playing.setVisibility(View.VISIBLE);
            if (!animationDrawable.isRunning()) {
                animationDrawable.start();
            }
        }


        holder.img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPosition = position;

                SongData mSongInfo = songDataList.get(position);
                map.clear();
                map.put(AnalyzeConstant.songId, mSongInfo.id+"");
                AnalyzeConstant.event(AnalyzeConstant.click_song_audition,map);

                if (!mSongInfo.isPlay) {              //不是播放状态
                    for (SongData songInfo : songDataList) {
                        songInfo.isPlay = false;
                    }
                    mSongInfo.isPlay = true;
                    //停止上首音乐的播放
                    MyMidiPlayer.stop();
                    //播放当前音乐
                    String url= mSongInfo.hard.pathServer;
                    MyHttpManager.downloadMid(url,myCallback);
                }else {                                 //是播放状态
                    mSongInfo.isPlay = false;
                    //停止播放当前音乐
                    MyMidiPlayer.stop();
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public void onSongPlayChang() {
        if (songDataList!= null && !songDataList.isEmpty()) {
            for (SongData songInfo : songDataList) {
                songInfo.isPlay = false;
            }
            MyMidiPlayer.stop();
            notifyDataSetChanged();
        }
    }

    class ViewHolder{
        public LinearLayout ll_play;
        public LinearLayout ll_star;
        public ImageView img_play;
        public ImageView img_new;
        public ImageView img_playing;
        public ImageView img_is_vip;
        public TextView tv_song_name;
        public TextView tv_singer;
        public TextView tv_coin;
        public TextView bt_play;
        public LinearLayout bt_buy;
        public StarLayout easy_star;
        public StarLayout medium_star;
        public StarLayout hard_star;

    }
    int playPosition;
    MyHttpManager.MyCallback myCallback = new MyHttpManager.MyCallback() {
        @Override
        public void onError() {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(MyApplication.application, context.getResources().getString(R.string.download_failed));
                    SongData mSongInfo = songDataList.get(playPosition);
                    mSongInfo.isPlay = false;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onSuccess(final String midPath) {
            //midPath 音乐文件地址
            Log.e("onSuccess","midPath====="+midPath);

            MyMidiPlayer.start(midPath);

        }
    };
}


