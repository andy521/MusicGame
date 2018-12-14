package com.fotoable.piano.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.utils.FontsUtils;

/**
 * Created by fotoable on 2017/6/20.
 */

public class TermServiceActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_term_service;
    }

    @Override
    protected void initView() {
        LinearLayout mBack = (LinearLayout) findViewById(R.id.ll_back);
        mBack.setOnClickListener(this);
        TextView mTitle = (TextView) findViewById(R.id.tv_title);
        mTitle.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String url = intent.getStringExtra("url");
        mTitle.setText(title);
        WebView webView = (WebView) findViewById(R.id.webview);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar == null) return;
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setProgress(newProgress);
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.ll_back){
            finish();
        }
    }
}
