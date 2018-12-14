package com.fotoable.piano.ad;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fotoable.adloadhelper.ads.AdViewBaseLayout;
import com.fotoable.adloadhelper.ads.adsbean.NativeAdBase;
import com.fotoable.piano.R;

/**
 * Created by damon on 27/07/2017.
 */

public class BigAdViewPopLayout extends AdViewBaseLayout {
    private String TAG = "BigAdViewPopLayout";

    private LinearLayout mNativeAdMediaView;
    private ImageView mNativeAdTitleIcon;
    private TextView mNativeAdTitle;
    private TextView mNativeAdBody;
    private Button mNativeAdBtn;


    public BigAdViewPopLayout(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.big_ad_view_pop, this, true);
        mNativeAdMediaView = (LinearLayout) findViewById(R.id.ad_mediaview_content);
        mNativeAdTitleIcon = (ImageView) findViewById(R.id.ad_little_icon_content);
        mNativeAdTitle = (TextView) findViewById(R.id.ad_adview_title);
        mNativeAdBody = (TextView) findViewById(R.id.ad_adview_body);
        mNativeAdBtn = (Button) findViewById(R.id.ad_view_btn);

        setLogoView(mNativeAdTitleIcon);
        setTitleView(mNativeAdTitle);
        setBodyView(mNativeAdBody);
        setMediaView(mNativeAdMediaView);
        setActionView(mNativeAdBtn);
        setRegisterView(mNativeAdBtn);
    }

    @Override
    public void updateLayout(NativeAdBase nativeAdBase) {
        mNativeAdTitle.setText(nativeAdBase.getTitle());
        mNativeAdBody.setText(nativeAdBase.getTextBody());
        mNativeAdBtn.setText(nativeAdBase.getButton());
        nativeAdBase.setMediaView(mNativeAdMediaView);
        nativeAdBase.displayImageIcon(mNativeAdTitleIcon);
        nativeAdBase.registerViewGroup(mNativeAdBtn);
        super.updateLayout(nativeAdBase);
    }

}
