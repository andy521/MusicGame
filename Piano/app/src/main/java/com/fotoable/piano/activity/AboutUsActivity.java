package com.fotoable.piano.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.utils.FontsUtils;

/**
 * Created by fotoable on 2017/6/20.
 */

public class AboutUsActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }
    TextView tv_code;
    @Override
    protected void initView() {
        TextView mTitle = (TextView) findViewById(R.id.tv_title);
        tv_code = (TextView) findViewById(R.id.tv_code);
        mTitle.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        mTitle.setText(R.string.action_help);
        LinearLayout mBack = (LinearLayout) findViewById(R.id.ll_back);
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.ll_back){
            finish();
        }
    }

    @Override
    protected void initData() {
        tv_code.setText(getVersion());
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
