package com.fotoable.piano.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.clipviewpager.RecyclingPagerAdapter;
import com.fotoable.piano.game.entity.LevelItem;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.view.CircleProgressView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fotoable on 2017/8/1.
 */

public class LevelAdapter extends RecyclingPagerAdapter {
    private final List<LevelItem> mList;
    private final Context mContext;
    private int levelNum;
    private int pro;

    final int TYPE_DONE=0;
    final int TYPE_THIS=1;
    final int TYPE_UNDONE=2;

    public LevelAdapter(Context context) {
        mList = new ArrayList<>();
        mContext = context;
    }

    public void addAll(List<LevelItem> list, int levelNum) {
        mList.addAll(list);
        this.levelNum = levelNum;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < levelNum) {
            return TYPE_DONE;
        }else if(position > levelNum) {
            return TYPE_UNDONE;
        }else {
            return TYPE_THIS;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        ViewHolder1 holder1=null;
        ViewHolder2 holder2=null;
        ViewHolder3 holder3=null;
        int type=getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_DONE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.level_done_item, null);
                    holder1=new ViewHolder1();
                    holder1.textView=(TextView) convertView.findViewById(R.id.level_num);
                    holder1.textView.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
                    convertView.setTag(holder1);
                    break;
                case TYPE_UNDONE:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.level_undone_item, null);
                    holder2 = new ViewHolder2();
                    holder2.textView = (TextView)convertView.findViewById(R.id.level_num);
                    holder2.textView.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
                    convertView.setTag(holder2);
                    break;
                case TYPE_THIS:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.level_item, null);
                    holder3 = new ViewHolder3();
                    holder3.textView = (TextView)convertView.findViewById(R.id.level_num);
                    holder3.textView.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
                    holder3.progressbar = (CircleProgressView)convertView.findViewById(R.id.progressbar);
                    convertView.setTag(holder3);
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case TYPE_DONE:
                    holder1=(ViewHolder1) convertView.getTag();
                    break;
                case TYPE_UNDONE:
                    holder2=(ViewHolder2) convertView.getTag();
                    break;
                case TYPE_THIS:
                    holder3=(ViewHolder3) convertView.getTag();
                    break;
                default:
                    break;
            }
        }

        LevelItem levelBean = mList.get(position);
        //设置资源
        switch (type) {
            case TYPE_DONE:
                holder1.textView.setText(levelBean.level+"");
                break;
            case TYPE_UNDONE:
                holder2.textView.setText(levelBean.level+"");
                break;
            case TYPE_THIS:
                holder3.textView.setText(levelBean.level+"");
                int experience = levelBean.xp;
                int userExperience = levelBean.userxp;
                pro = (userExperience * 100) / experience;
                holder3.progressbar.setProgress(pro);
                break;
            default:
                break;
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * 各个布局的控件资源
     * @author ly
     *
     */
    class ViewHolder1{
        TextView textView;
    }

    class ViewHolder2{
        TextView textView;
    }
    class ViewHolder3{
        TextView textView;
        CircleProgressView progressbar;
    }

}
