package com.fotoable.piano.activity;

import android.view.View;

import com.fotoable.piano.R;
import com.fotoable.piano.view.SpringProgressView;

/**
 * Created by fotoable on 2017/7/31.
 */

public class TestActivity extends BaseActivity{
    @Override
    protected int getLayoutId() {
       return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        SpringProgressView viewById = (SpringProgressView)findViewById(R.id.test_Progress);
        viewById.setMaxCount(100);
        viewById.setProgress(80);

    }

    @Override
    protected void initData() {

    }
}
