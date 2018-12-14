package com.fotoable.piano.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.fotoable.piano.R;
import com.fotoable.piano.constant.Constant;
import com.fotoable.piano.game.shared.SharedOther;
import com.fotoable.piano.game.shared.SharedRate;
import com.fotoable.piano.game.shared.SharedSongs;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.http.MyHttpManager;
import com.fotoable.piano.view.LoadingView;

import java.util.ArrayList;

import payment.PaymentManager;
import payment.utils.IabHelper;
import payment.utils.IabResult;
import payment.utils.Inventory;

/**
 * Created by fotoable on 2017/7/3.
 */

public class GuideActivity extends AppCompatActivity {
    private LoadingView dialog;
    private ArrayList<String> purchareItems;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                restorePurchase();
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        setContentView(R.layout.activity_guide);
        dialog = (LoadingView) findViewById(R.id.dialog);
        purchareItems = new ArrayList<>();
        purchareItems.add(Constant.PUCHASE_WEEKLY_ITEM);
        purchareItems.add(Constant.PUCHASE_MONTHLY_ITEM_NEW);
        purchareItems.add(Constant.PUCHASE_YEARLY_ITEM);
        PaymentManager.getInstance().setupPurchases(this, Constant.IPA_KEY, purchareItems);
        showDialog();
    }


    private void showDialog() {
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                if (SharedOther.getLastUpdateSongJsonTime() == 0) {
                    //初始化本地的song json 文件
                    SharedSongs.getAllSongs();
                }
                //检查是否需要下载song json 文件
                MyHttpManager.checkRefreshSongJson();
                //只有首次安装时大于2秒
                if ((System.currentTimeMillis() - startTime) < 2000) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void restorePurchase() {
        PaymentManager.getInstance().checkPurchases(purchareItems, new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                try {
                    if (result == null) {
                        SharedUser.updateVipData(false);
                    } else {
                        if (result.isSuccess() && inv != null) {
                            for (String item : purchareItems) {
                                if (inv.hasPurchase(item)) {
//                                    payment.utils.MyLog.i("purchase:" + inv.getPurchase(item).toString());
                                    if (inv.getPurchase(item) != null) {
                                        SharedUser.updateVipData(true);
                                    } else {
                                        SharedUser.updateVipData(false);
                                    }
                                }
//                                if (inv.hasDetails(item)) {
//                                    payment.utils.MyLog.i("skudetail:" + inv.getSkuDetails(item).toString());
//                                }
                            }
                        } else {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }
}
