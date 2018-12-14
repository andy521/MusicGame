package com.fotoable.piano.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.constant.Constant;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.utils.ToastUtils;
import com.fotoable.piano.view.zoom_hover.OnItemSelectedListener;
import com.fotoable.piano.view.zoom_hover.OnZoomAnimatorListener;
import com.fotoable.piano.view.zoom_hover.ZoomHoverAdapter;
import com.fotoable.piano.view.zoom_hover.ZoomHoverGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import payment.PaymentManager;
import payment.utils.IabResult;
import payment.utils.Purchase;

/**
 * Created by fotoable on 2017/6/26.
 */

public class BalanceActivity extends BaseActivity {

    private List<String> purchareItems;
    private String purchaseId = "";
    public @interface PurchaseType {
        int weekly = 0;
        int mounthly = 1;
        int yearly = 2;
    }

    private Map map = new HashMap<String, String>();
    private int purchaseType = PurchaseType.weekly;
    private ZoomHoverGridView mZoomHoverGridView;
    private BalanceAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_balanc;
    }

    @Override
    protected void initView() {
        TextView mTitle = (TextView) findViewById(R.id.tv_title);
        mTitle.setText(R.string.action_balance_title);
        mTitle.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        LinearLayout mBack = (LinearLayout) findViewById(R.id.ll_back);
        mBack.setOnClickListener(this);
        TextView text1 = (TextView) findViewById(R.id.text1);
        TextView text2 = (TextView) findViewById(R.id.text2);
        RelativeLayout mUnlock = (RelativeLayout) findViewById(R.id.rl_exchange);
        TextView mTVUnlock = (TextView) findViewById(R.id.tv_exchange);
        mUnlock.setOnClickListener(this);
        mTVUnlock.setTypeface(FontsUtils.getType(FontsUtils.BUTTON_FONT));
        text1.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        text2.setTypeface(FontsUtils.getType(FontsUtils.STATE_INFO1_FONT));

        mZoomHoverGridView = (ZoomHoverGridView) findViewById(R.id.zoom_hover_grid_view);
    }

    @Override
    protected void initData() {
        purchareItems = new ArrayList<>();
        purchareItems.add(Constant.PUCHASE_WEEKLY_ITEM);
        purchareItems.add(Constant.PUCHASE_MONTHLY_ITEM_NEW);
        purchareItems.add(Constant.PUCHASE_YEARLY_ITEM);
        PaymentManager.getInstance().setupPurchases(this, Constant.IPA_KEY, purchareItems);

        mAdapter = new BalanceAdapter(getData());
        mZoomHoverGridView.setAdapter(mAdapter);
        mZoomHoverGridView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position,boolean isUserClick) {

                if (isUserClick && position == purchaseType) {
                    buySubscription();
                }
                if (position == 0) {
                    purchaseType = PurchaseType.weekly;
                } else if (position == 1) {
                    purchaseType = PurchaseType.mounthly;
                } else if (position == 2) {
                    purchaseType = PurchaseType.yearly;
                }
            }
        });
        mZoomHoverGridView.setSelectedItem(0);
        mZoomHoverGridView.setOnZoomAnimatorListener(new OnZoomAnimatorListener() {
            @Override
            public void onZoomInStart(View view) {
                view.findViewById(R.id.tv_coin).setVisibility(View.VISIBLE);
            }
            @Override
            public void onZoomInEnd(View view) {
                view.findViewById(R.id.tv_coin).setVisibility(View.VISIBLE);
            }
            @Override
            public void onZoomOutStart(View view) {
                view.findViewById(R.id.tv_coin).setVisibility(View.GONE);
            }
            @Override
            public void onZoomOutEnd(View view) {
                view.findViewById(R.id.tv_coin).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_back:
                this.finish();
                break;

            case R.id.rl_exchange:
                buySubscription();
                break;
        }
    }

    /**
     * 购买
     */
    private void buySubscription() {
        switch (purchaseType) {
            case PurchaseType.weekly:
                purchaseId = Constant.PUCHASE_WEEKLY_ITEM;
                break;
            case PurchaseType.mounthly:
                purchaseId = Constant.PUCHASE_MONTHLY_ITEM_NEW;
                break;
            case PurchaseType.yearly:
                purchaseId = Constant.PUCHASE_YEARLY_ITEM;
                break;
        }
        //统计栏目点击数
        map.clear();
        map.put("item", purchaseId);
        AnalyzeConstant.event(AnalyzeConstant.click_unlock_btn, map);

        if (!TextUtils.isEmpty(purchaseId)) {
            PaymentManager.getInstance().purchase(this, purchaseId, true, new PaymentManager.PurchaseFinishedListener() {
                @Override
                public void success(IabResult result, Purchase purchase) {
                    payment.utils.MyLog.i(purchase + ",success " + result);
                    Log.e("BalanceActivity", "success(BalanceActivity.java:161)" + result);
                    map.clear();
                    map.put("item", purchaseId);
                    AnalyzeConstant.event(AnalyzeConstant.pay_success, map);
                    SharedUser.updateVipData(true);
                    ToastUtils.showToast(getApplicationContext(), R.string.purchase_successed);
                }

                @Override
                public void failure(IabResult result, Purchase purchase) {
                    payment.utils.MyLog.i(purchase + ",fail " + result);
                    if (result != null) {
                        map.clear();
                        map.put("failType", result.getMessage());
                        AnalyzeConstant.event(AnalyzeConstant.pay_fail, map);
                        SharedUser.updateVipData(false);
                        ToastUtils.showToast(getApplicationContext(), R.string.purchase_error);
                    } else {
                        ToastUtils.showToast(getApplicationContext(), R.string.purchase_not_support);
                    }

                }
            });
        }
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("img", R.drawable.days_bg);
        map.put("money", getResources().getString(R.string.action_free));
        map.put("type_info", R.string.action_week_info);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("img", R.drawable.one_month_bg);
        map.put("money", Constant.MONTHLY_MONEY);
        map.put("type_info", null);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("img", R.drawable.one_year_bg);
        map.put("money", Constant.YEARLY_MONEY);
        map.put("type_info", null);
        list.add(map);
        return list;
    }

    class BalanceAdapter extends ZoomHoverAdapter<Map<String, Object>> {

        public BalanceAdapter(List<Map<String, Object>> list) {
            super(list);
        }

        @Override
        public View getView(ViewGroup parent, int position, Map<String, Object> s) {
            View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_balacf_card, parent, false);
            ImageView img_item_bg = (ImageView) contentView.findViewById(R.id.img_item_bg);
            TextView tv_money = (TextView) contentView.findViewById(R.id.tv_money);
            TextView tv_coin = (TextView) contentView.findViewById(R.id.tv_coin);
            tv_money.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
            tv_coin.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
            img_item_bg.setImageResource((Integer) s.get("img"));
            tv_money.setText((String) s.get("money"));
            if (s.get("type_info") != null) {
                tv_coin.setText((Integer) s.get("type_info"));
            }
            return contentView;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PaymentManager.getInstance().handleActivityResult(requestCode, resultCode, data);
    }
}
