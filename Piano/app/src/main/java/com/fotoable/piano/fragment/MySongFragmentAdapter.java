package com.fotoable.piano.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.activity.SelectDifficultyLevelActivity;
import com.fotoable.piano.entity.CategoryUI;
import com.fotoable.piano.entity.CategoryUIItem;
import com.fotoable.piano.entity.PlayedData;
import com.fotoable.piano.entity.PlayedSong;
import com.fotoable.piano.entity.SongData;
import com.fotoable.piano.entity.UserData;
import com.fotoable.piano.fragment.SuggestFragment.OnSongPlayChangListener;
import com.fotoable.piano.game.shared.SharedPlayed;
import com.fotoable.piano.game.shared.SharedSongs;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.http.MyHttpManager;
import com.fotoable.piano.midi.MyMidiPlayer;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.utils.ToastUtils;
import com.fotoable.piano.view.GetSongDialog;
import com.fotoable.piano.view.StarLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fotoable on 2017/6/28.
 */

class MySongFragmentAdapter extends BaseAdapter
        implements OnSongPlayChangListener {
    private Activity context;
    private ArrayList<PlayedSong> allPlayedlist;
    private ArrayList<SongData> songDatas;
    private LayoutInflater inflater;
    private SongData mSongInfo;
    private PlayedSong playedSong;

    public MySongFragmentAdapter(Activity context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setSongDatas(ArrayList<PlayedSong> allPlayedlist, ArrayList<SongData> songDatas) {
        this.allPlayedlist = allPlayedlist;
        this.songDatas = songDatas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return allPlayedlist == null ? 0 : allPlayedlist.size();
    }

    @Override
    public SongData getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
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
            holder.easy_star = (StarLayout) convertView.findViewById(R.id.easy_star);
            holder.medium_star = (StarLayout) convertView.findViewById(R.id.medium_star);
            holder.hard_star = (StarLayout) convertView.findViewById(R.id.hard_star);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        playedSong = allPlayedlist.get(position);
//        Log.e("playedSong","playedSong===="+playedSong.toString());
        ArrayList<PlayedData> easyDatas = playedSong.easyDatas;
        ArrayList<PlayedData> middleDatas = playedSong.middleDatas;
        ArrayList<PlayedData> hardDatas = playedSong.hardDatas;

        mSongInfo = songDatas.get(position);
        if (mSongInfo != null) {
            holder.tv_song_name.setText(mSongInfo.name);
            holder.tv_singer.setText(mSongInfo.singerName);

            if (mSongInfo.isNew) {
                holder.img_new.setVisibility(View.VISIBLE);
            } else {
                holder.img_new.setVisibility(View.GONE);
            }

            if (mSongInfo.isVip) {
                holder.img_is_vip.setVisibility(View.VISIBLE);
            } else {
                holder.img_is_vip.setVisibility(View.GONE);
            }

            holder.ll_play.setVisibility(View.GONE);
            holder.ll_star.setVisibility(View.VISIBLE);
            holder.easy_star.setStarNum(SharedSongs.starNum(easyDatas));
            holder.medium_star.setStarNum(SharedSongs.starNum(middleDatas));
            holder.hard_star.setStarNum(SharedSongs.starNum(hardDatas));

            holder.img_playing.setImageResource(R.drawable.playing_animlist);
            AnimationDrawable animationDrawable = (AnimationDrawable) holder.img_playing.getDrawable();
            if (!mSongInfo.isPlay) {
                holder.img_play.setImageResource(R.drawable.play);
                holder.img_playing.setVisibility(View.GONE);
                if (animationDrawable.isRunning()) {
                    animationDrawable.stop();
                }
            } else {
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
                    SongData mSongInfo = songDatas.get(position);
                    if (!mSongInfo.isPlay) {              //不是播放状态
                        for (SongData songInfo : songDatas) {
                            songInfo.isPlay = false;
                        }
                        mSongInfo.isPlay = true;
                        //停止上首音乐的播放
                        MyMidiPlayer.stop();
                        //播放当前音乐
                        String url = mSongInfo.hard.pathServer;
                        MyHttpManager.downloadMid(url, myCallback);
                    } else {                                 //是播放状态
                        mSongInfo.isPlay = false;
                        //停止播放当前音乐
                        MyMidiPlayer.stop();
                    }
                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }

    @Override
    public void onSongPlayChang() {
        if (songDatas != null && !songDatas.isEmpty()) {
            for (SongData songInfo : songDatas) {
                if (songInfo != null)
                    songInfo.isPlay = false;
            }
            MyMidiPlayer.stop();
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        public LinearLayout ll_play;
        public LinearLayout ll_star;
        public ImageView img_play;
        public ImageView img_new;
        public ImageView img_playing;
        public ImageView img_is_vip;
        public TextView tv_song_name;
        public TextView tv_singer;
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
                    SongData mSongInfo = songDatas.get(playPosition);
                    mSongInfo.isPlay = false;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onSuccess(final String midPath) {
            //midPath 音乐文件地址
            Log.e("onSuccess", "midPath=====" + midPath);
            MyMidiPlayer.start(midPath);

        }
    };
}


