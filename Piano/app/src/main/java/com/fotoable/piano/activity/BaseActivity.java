package com.fotoable.piano.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.fotoable.piano.R;
import com.fotoable.piano.constant.Constant;

/**
 * Created by houhou on 2017/6/20.
 */

public abstract class BaseActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(getLayoutId());
        this.initView();
        this.initData();

    }

    /**
     * 在这里 setContentView
     */
    protected abstract int getLayoutId();

    /**
     * 在这里初始化需要的view
     */
    protected abstract void initView();

    /**
     * 在这里接收上页面的data或者从本地读取需要的data
     */
    protected abstract void initData();

    /**
     * Constant.DEBUG 时才会输出log
     *
     * @param key
     * @param content
     */
    public void logD(String key, String content) {
        if (Constant.DEBUG) {
            Log.d(key, content);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //带有顶部actionbar
    public void setActionBar(int resId) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(resId);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }
}
