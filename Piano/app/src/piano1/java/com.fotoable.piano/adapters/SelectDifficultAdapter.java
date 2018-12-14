package com.fotoable.piano.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.constant.Constant;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.view.TagStarLayout;
import com.fotoable.piano.view.zoom_hover.ZoomHoverAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by fotoable on 2017/8/1.
 */

public class SelectDifficultAdapter extends ZoomHoverAdapter<Map<String, Object>> {
    private int userLevel;

    public SelectDifficultAdapter(List<Map<String, Object>> list) {
        super(list);
        userLevel = SharedUser.getDefaultUser().level;
    }

    @Override
    public View getView(ViewGroup parent, int position, Map<String, Object> s) {
        View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_difficult, parent, false);
        ImageView img_unlock = (ImageView) contentView.findViewById(R.id.img_unlock);
        ImageView img_item_bg = (ImageView) contentView.findViewById(R.id.img_item_bg);
        TagStarLayout star = (TagStarLayout) contentView.findViewById(R.id.easy_star);
        TextView tv_info = (TextView) contentView.findViewById(R.id.tv_info);
        tv_info.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        img_item_bg.setImageResource((Integer) s.get("img"));
        tv_info.setText((Integer) s.get("info"));
        star.setStarNum((Integer) s.get("star"));
        if (position==2){
              if (userLevel < Constant.USER_LEVEL_UNLOCK_HARD_GATE){
                img_unlock.setVisibility(View.VISIBLE);
            }else {
                img_unlock.setVisibility(View.GONE);
            }
        }else {
            img_unlock.setVisibility(View.GONE);
        }
        return contentView;
    }
}
