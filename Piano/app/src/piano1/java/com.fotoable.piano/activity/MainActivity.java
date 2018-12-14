package com.fotoable.piano.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.ad.AdConstant;
import com.fotoable.piano.ad.InterstitialAdUtil;
import com.fotoable.piano.ad.PopWindowManager;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.constant.Constant;
import com.fotoable.piano.entity.CategoryItem;
import com.fotoable.piano.entity.ClassifyBean;
import com.fotoable.piano.entity.UserData;
import com.fotoable.piano.fragment.MainFragmentAdapter;
import com.fotoable.piano.fragment.SuggestFragment;
import com.fotoable.piano.game.entity.LevelItem;
import com.fotoable.piano.game.shared.SharedLevel;
import com.fotoable.piano.game.shared.SharedSongs;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.http.MyHttpManager;
import com.fotoable.piano.midi.MyMidiPlayer;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.view.LevelUpDialog;
import com.fotoable.piano.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by houfutian on 2017/6/14.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "MainActivityTest";

    private UserData userData;
    private String[] mPlanetTitles;
    private DrawerLayout drawer;
    private ListView mDrawerList;
    private RelativeLayout mLevel, mCoin, snackbar;
    private ProgressBar mLevelPro;
    private TextView tvNickName, mLevelNum, mExpNum, mCoinNum, tv_title;

    private ImageView mImageView, mPiano, mSearch;

    private Map map;
    private NoScrollViewPager viewPager;

    private MainFragmentAdapter adapter;
    private List<ClassifyBean> classifyList;
    private int[] titleImg = {R.drawable.mysong, R.drawable.suggest, R.drawable.choice,
            R.drawable.hot, R.drawable.solid, R.drawable.new_img, R.drawable.minu_item_img
//            ,R.drawable.vip
    };
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";
    private ListView title_list;
    private MenuAdapter listAdapter;
    private int currentIndex = 0;

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);
        initView();
        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT);
        }
//        showInterstitialAd();
    }

    private void showInterstitialAd() {
        mPiano.postDelayed(new Runnable() {
            @Override
            public void run() {
                //延时 启动插屏广告
                if (getBaseContext() == null) {
                    Log.e(TAG, "loadInterstitialAd, getBaseContext()==null");
                    return;
                }
                InterstitialAdUtil.loadInterstitialAd(getApplicationContext());
            }
        }, 1000);
    }

    private void showGameFinishAd() {
        if (AdConstant.isShowPlayFinish) {
            PopWindowManager.showPopupWindowView(rootView, new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    Log.d(TAG, "showPopupWindowView onDismiss");
                    if (rootView == null) {
                        return;
                    }
                    AdConstant.isShowPlayFinish = false;

                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //“内存重启”时保存当前的fragment名字
        outState.putInt(CURRENT_FRAGMENT, currentIndex);
        super.onSaveInstanceState(outState);
    }

    /**
     * 加载view
     */
    private void initView() {
        initSnackbarView();
        initTitleListView();
        initFragmentPagerView();
    }

    /**
     * 侧滑栏
     */
    public void initSnackbarView() {
        //初始化侧滑栏
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        View listViewHeader = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        //用户头像
        mImageView = (ImageView) listViewHeader.findViewById(R.id.ivAvatar);
        tvNickName = (TextView) listViewHeader.findViewById(R.id.tvNickName);
        tvNickName.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        mDrawerList.addHeaderView(listViewHeader);
//        View listViewFooter = getLayoutInflater().inflate(R.layout.nav_footer_main, null);
//        mDrawerList.addFooterView(listViewFooter);
        LinearLayout userLogin = (LinearLayout) listViewHeader.findViewById(R.id.ll_user);
        userLogin.setOnClickListener(this);
        RelativeLayout getVIP = (RelativeLayout) listViewHeader.findViewById(R.id.ll_get_vip);
        getVIP.setOnClickListener(this);
        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setItemChecked(1, true);
        //侧滑栏弹出图标
        snackbar = (RelativeLayout) findViewById(R.id.rl_snackbar);
        mPiano = (ImageView) findViewById(R.id.img_piano);
        mSearch = (ImageView) findViewById(R.id.img_search);
        mLevel = (RelativeLayout) findViewById(R.id.rl_level);
        mCoin = (RelativeLayout) findViewById(R.id.rl_coin);
        mLevelPro = (ProgressBar) findViewById(R.id.level_progressbar);
        tv_title = (TextView) findViewById(R.id.tv_title);
        mLevelNum = (TextView) findViewById(R.id.tv_level_num);
        mExpNum = (TextView) findViewById(R.id.tv_exp_num);
        mCoinNum = (TextView) findViewById(R.id.tv_coin);
        tv_title.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        mLevelNum.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        mCoinNum.setTypeface(FontsUtils.getType(FontsUtils.SONGS_TITLE_FONT));
        snackbar.setOnClickListener(this);
        mPiano.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mLevel.setOnClickListener(this);
        mCoin.setOnClickListener(this);

//        Profile profile = Profile.getCurrentProfile();
//        if (profile != null) {
//            tvNickName.setText(profile.getName());
//            Uri uri = profile.getProfilePictureUri(100, 100);
//            MyLog.i("facebook head =" + uri);
//        }
    }

    /**
     * 分类目录
     */
    public void initTitleListView() {
        rootView = findViewById(R.id.root_view);
        title_list = (ListView) findViewById(R.id.title_list);
        listAdapter = new MenuAdapter();
        title_list.setAdapter(listAdapter);
        map = new HashMap<String, String>();
        title_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentIndex = position;
                viewPager.setCurrentItem(currentIndex, false);
                for (int x = 0; x < classifyList.size(); x++) {
                    if (x == position) {
                        classifyList.get(x).setSelectItem(true);
                    } else {
                        classifyList.get(x).setSelectItem(false);
                    }
                }
                map.clear();
                map.put(AnalyzeConstant.category, classifyList.get(position).getClassifyName());
                AnalyzeConstant.event(AnalyzeConstant.select_song_list, map);
                listAdapter.setDatas(classifyList);
            }
        });
    }

    /**
     * viewPager装载Fragment
     */
    public void initFragmentPagerView() {
        viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(0);
        adapter = new MainFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        int i = 0;
        classifyList = new ArrayList<>();
        LinkedHashMap<Integer, CategoryItem> category = SharedSongs.getAllSongs().category;
        for (Map.Entry<Integer, CategoryItem> entry : category.entrySet()) {
            ClassifyBean classifyBean = new ClassifyBean();
            classifyBean.setClassifyName(entry.getValue().name);
            classifyBean.setClassifyId(entry.getKey());
            if (i > 4) {
                classifyBean.setClassifyImg(titleImg[6]);
            } else {
                classifyBean.setClassifyImg(titleImg[i + 1]);
            }
            i++;
            classifyBean.setSelectItem(false);
            classifyList.add(classifyBean);
        }
        //插入第二条My song
        ClassifyBean classifyBean = new ClassifyBean();
        classifyBean.setClassifyName(getResources().getString(R.string.action_my_songs));
        classifyBean.setClassifyId(-1);     //本地 My song分类Id
        classifyBean.setClassifyImg(titleImg[0]);
        classifyBean.setSelectItem(false);
        classifyList.add(1, classifyBean);
        //选中当前条目
        classifyList.get(currentIndex).setSelectItem(true);
        listAdapter.setDatas(classifyList);

        List<Fragment> fragments = new ArrayList<>();
        for (int x = 0; x < classifyList.size(); x++) {
            int classifyId = classifyList.get(x).getClassifyId();
            String classifyName = classifyList.get(x).getClassifyName();
            fragments.add(SuggestFragment.newInstance(classifyName, classifyId));
        }
        adapter.setData(fragments);
        viewPager.setCurrentItem(currentIndex, false);

        userData = SharedUser.getDefaultUser();
        tvNickName.setText(userData.nickName);
        mLevelNum.setText(userData.level + "");
        int xp = SharedLevel.getLevel().get(userData.level).xp;
        int x = (userData.xpLevel * 100) / xp;
        mLevelPro.setProgress(x);
        if (x <= 60) {
            mExpNum.setTextColor(getResources().getColor(R.color.text_green));
        } else {
            mExpNum.setTextColor(getResources().getColor(R.color.white));
        }
        mExpNum.setText(userData.xpLevel + "/" + xp + "xp");
        mCoinNum.setText(userData.coin + "");
        if (SharedUser.getVipData()) {
            mSearch.setVisibility(View.VISIBLE);
            mCoin.setVisibility(View.GONE);
        } else {
            mSearch.setVisibility(View.GONE);
            mCoin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        //在升级时不弹广告
        if (MyApplication.isShowLevelUpDialog) {
            showLevelUpDialog();
        } else {
            showGameFinishAd();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashMap map = new HashMap<>();
        map.put("coins", userData.coin + "");
        map.put("level", userData.xpLevel + "");
        AnalyzeConstant.event(AnalyzeConstant.destory_app, map);
    }

    /**
     * 侧滑目录点击事件
     *
     * @return
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            drawer.closeDrawer(mDrawerList);
            mDrawerList.setItemChecked(position, true);
            switch (position) {
                case 2:
                    //清除缓存
                    MyHttpManager.clear();
                    break;
                case 3:
                    // 必须明确使用mailto前缀来修饰邮件地址
                    Uri uri = Uri.parse("mailto:CoolMusicPiano@outlook.com");
                    Intent data = new Intent(Intent.ACTION_SENDTO, uri);
                    data.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_feedback_type));
                    data.putExtra(Intent.EXTRA_TEXT, getString(R.string.action_feedback_content)); // 正文
                    startActivity(Intent.createChooser(data, getString(R.string.action_feedback_mail)));
                    break;
                case 4:
                    //服务条款
                    Intent intent = new Intent(MainActivity.this, TermServiceActivity.class);
                    intent.putExtra("title", getString(R.string.action_service));
                    intent.putExtra("url", Constant.TERMS_OF_SERVICE_URL);
                    startActivity(intent);
                    break;
                case 5:
                    //协议条款
                    Intent intent0 = new Intent(MainActivity.this, TermServiceActivity.class);
                    intent0.putExtra("title", getString(R.string.action_setting));
                    intent0.putExtra("url", Constant.PRIVACY_POLICY_URL);
                    startActivity(intent0);
                    break;
                case 6:
                    //关于我们
                    startActivitys(AboutUsActivity.class);
                    break;

            }

        }
    }

    public void startActivitys(Class T) {
        Intent intent = new Intent(this, T);
        startActivity(intent);
    }

    /**
     * 升级提示弹窗
     */
    public void showLevelUpDialog() {
        ArrayList<LevelItem> levelData = SharedLevel.getLevel();
        LevelUpDialog levelDialog = new LevelUpDialog(this, userData.level, levelData.get(userData.level - 1).coins);
        levelDialog.show();
        MyApplication.isShowLevelUpDialog = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user:
                //用户登录
                drawer.closeDrawer(mDrawerList);
                startActivitys(FBInviteActivity.class);
                break;
            case R.id.ll_get_vip:
                //购买VIP
                drawer.closeDrawer(mDrawerList);
                AnalyzeConstant.event(AnalyzeConstant.upgrade_to_vip);
                startActivitys(BalanceActivity.class);
                break;
            case R.id.rl_snackbar:
                AnalyzeConstant.event(AnalyzeConstant.click_drawer_menu);
                drawer.openDrawer(mDrawerList);
                break;
            case R.id.rl_level:
                AnalyzeConstant.event(AnalyzeConstant.click_grade_btn);
                startActivitys(LevelActivity.class);
                break;
            case R.id.img_piano: //暂时隐藏
                break;
            case R.id.img_search://暂时换成MY_VIP图标
                break;
            case R.id.rl_coin:
                HashMap map = new HashMap<>();
                map.put("coins", userData.coin + "");
                map.put("level", userData.xpLevel + "");
                AnalyzeConstant.event(AnalyzeConstant.click_coin_btn, map);
                startActivitys(BalanceActivity.class);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(mDrawerList)) {
            drawer.closeDrawer(mDrawerList);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyMidiPlayer.stop();
    }

    private class MenuAdapter extends BaseAdapter {
        List<ClassifyBean> datas;

        public void setDatas(List<ClassifyBean> datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (datas == null) return 0;
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.main_list_item, null);
                holder.im = (ImageView) convertView.findViewById(R.id.img);
                holder.tv = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ClassifyBean map = datas.get(position);
            if (map.isSelectItem()) {
                holder.tv.setTextColor(getResources().getColor(R.color.text_green));
                convertView.setBackgroundResource(R.drawable.list_item_check_bg);
            } else {
                holder.tv.setTextColor(getResources().getColor(R.color.white));
                if (position > 5) {
                    convertView.setBackgroundResource(R.drawable.list_item_check_bg2);
                } else {
                    convertView.setBackgroundResource(R.drawable.list_item_bg);
                }
            }
            holder.tv.setText(map.getClassifyName());
            holder.im.setImageResource(map.getClassifyImg());
            return convertView;
        }

        class ViewHolder {
            ImageView im;
            TextView tv;
        }
    }


}
