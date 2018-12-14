package com.fotoable.piano.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.SlidingTab.SlidingTabLayout;
import com.fotoable.piano.ad.AdConstant;
import com.fotoable.piano.ad.InterstitialAdUtil;
import com.fotoable.piano.ad.PopWindowManager;
import com.fotoable.piano.constant.Constant;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.entity.CategoryItem;
import com.fotoable.piano.entity.ClassifyBean;
import com.fotoable.piano.entity.UserData;
import com.fotoable.piano.fragment.SuggestFragment;
import com.fotoable.piano.game.entity.LevelItem;
import com.fotoable.piano.game.shared.SharedLevel;
import com.fotoable.piano.game.shared.SharedSongs;
import com.fotoable.piano.game.shared.SharedUser;
import com.fotoable.piano.http.MyHttpManager;
import com.fotoable.piano.midi.MyMidiPlayer;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.view.LevelUpDialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by houfutian on 2017/6/14.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";
    private int currentIndex = 0;
    //导航条，ViewPager+fragment
    private List<ClassifyBean> classifyList;
    private SlidingTabLayout tab;
    private ViewPager mViewPager;
    private SongListPageAdapter adapter;
    //侧滑栏
    private String[] mPlanetTitles;
    private DrawerLayout drawer;
    private ListView mDrawerList;
    //title
    private RelativeLayout mLevel, mCoin,snackbar;
    private ProgressBar mLevelPro;
    private TextView tvNickName, mLevelNum, mExpNum, mCoinNum, tv_title;
    private ImageView mImageView, mPiano, mSearch;

    private UserData userData;
    private View rootView;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //“内存重启”时保存当前的fragment名字
        if (mViewPager != null) {
            currentIndex = mViewPager.getCurrentItem();
            outState.putInt(CURRENT_FRAGMENT, currentIndex);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT);
        }
        initView();
//        showInterstitialAd();
    }

    /**
     * 加载view
     */
    private void initView() {
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
        TextView vip_text = (TextView) listViewHeader.findViewById(R.id.vip_text);
        vip_text.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setItemChecked(1, true);                //侧滑栏默认选中第一个

        //导航条+ViewPage
        tab = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new SongListPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        //title图标
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
        mLevelNum.setTypeface(FontsUtils.getType(FontsUtils.SUB_TITLE_FONT));
        mCoinNum.setTypeface(FontsUtils.getType(FontsUtils.SONGS_NAME_FONT));
        snackbar.setOnClickListener(this);
        mPiano.setOnClickListener(this);
        mSearch.setOnClickListener(this);
        mLevel.setOnClickListener(this);
        mCoin.setOnClickListener(this);

        rootView = findViewById(R.id.root_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        //在升级时不弹广告
        if (MyApplication.isShowLevelUpDialog){
            showLevelUpDialog();
        }else {
            showGameFinishAd();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        classifyList = new ArrayList<>();
        LinkedHashMap<Integer, CategoryItem> category = SharedSongs.getAllSongs().category;
        for (Map.Entry<Integer, CategoryItem> entry : category.entrySet()) {
            ClassifyBean classifyBean = new ClassifyBean();
            classifyBean.setClassifyName(entry.getValue().name);
            classifyBean.setClassifyId(entry.getKey());
            classifyList.add(classifyBean);
        }
        //本地 My song分类 插入到第 二条
        ClassifyBean classifyBean = new ClassifyBean();
        classifyBean.setClassifyName(getString(R.string.action_my_songs));
        classifyBean.setClassifyId(-1);             // 分类Id为-1
        classifyList.add(1, classifyBean);
        adapter.setData(classifyList);
        mViewPager.setOffscreenPageLimit(1);
        tab.setViewPager(mViewPager);
        mViewPager.setCurrentItem(currentIndex);

        userData = SharedUser.getDefaultUser();
        tvNickName.setText(userData.nickName);
        mLevelNum.setText(userData.level + "");
        int xp = SharedLevel.getLevel().get(userData.level).xp;
        int x = (userData.xpLevel * 100) / xp;
        mLevelPro.setProgress(x);
        mExpNum.setText(userData.xpLevel + "/" + xp + "xp");
        mCoinNum.setText(userData.coin + "");
    }

    /**
     * 升级提示弹窗
     */
    public void showLevelUpDialog(){
        ArrayList<LevelItem> levelData = SharedLevel.getLevel();
        LevelUpDialog levelDialog = new LevelUpDialog(this, userData.level, levelData.get(userData.level - 1).coins);
        levelDialog.show();
        MyApplication.isShowLevelUpDialog = false;
    }

    /**
     * 游戏结束广告
     */
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

    /**
     * 开启广告
     */
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
                    Intent intent = new Intent(MainActivity.this,TermServiceActivity.class);
                    intent.putExtra("title",getString(R.string.action_service));
                    intent.putExtra("url", Constant.TERMS_OF_SERVICE_URL);
                    startActivity(intent);
                    break;
                case 5:
                    //协议条款
                    Intent intent0 = new Intent(MainActivity.this,TermServiceActivity.class);
                    intent0.putExtra("title",getString(R.string.action_setting));
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

    /**
     * startActivity
     * @param T
     */
    public void startActivitys(Class T) {
        Intent intent = new Intent(this, T);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user:
                //用户登录
                drawer.closeDrawer(mDrawerList);
//                startActivitys(LoginActivity.class);
//                startActivitys(TestActivity.class);
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
            case R.id.img_search://暂时隐藏

                break;
            case R.id.rl_coin:
                AnalyzeConstant.event(AnalyzeConstant.click_coin_btn);
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

    /**
     * FragmentAdapter
     */
    private class SongListPageAdapter extends FragmentStatePagerAdapter {
        private List<ClassifyBean> list = new ArrayList();
        public SongListPageAdapter(FragmentManager fm) {
            super(fm);
        }
        public void setData(List<ClassifyBean> list){
            this.list.clear();
            this.list = list;
            notifyDataSetChanged();
        }
        @Override
        public Fragment getItem(int position) {
            return SuggestFragment.newInstance(list.get(position).getClassifyName(),list.get(position).getClassifyId());
        }
        @Override
        public CharSequence getPageTitle(int position) {
            Log.e("SongListPageAdapter","getPageTitle(SongListPageAdapter.java:309)"+list.get(position).getClassifyName());
            return list.get(position).getClassifyName();
        }
        @Override
        public int getCount() {
            return list.size();
        }
    }
}
