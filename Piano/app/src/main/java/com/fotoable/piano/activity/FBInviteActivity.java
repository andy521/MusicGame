package com.fotoable.piano.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beta.fbinvite.invite.FBFriendsModel;
import com.beta.fbinvite.invite.FBUtils;
import com.beta.fbinvite.invite.FbRequest;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.widget.LoginButton;
import com.fotoable.piano.R;
import com.fotoable.piano.constant.AnalyzeConstant;
import com.fotoable.piano.utils.FontsUtils;
import com.fotoable.piano.utils.MyLog;
import com.fotoable.piano.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 登录页
 * Created by houfutian on 2017/6/14.
 */
public class FBInviteActivity extends BaseActivity {
    private static final int login_type = 0;

    private class MyFackbookCallback implements FacebookCallback {
        private int type;

        public MyFackbookCallback(int type) {
            this.type = type;
        }

        @Override
        public void onSuccess(Object o) {
            payment.utils.MyLog.i("fblogin =" + o);
            ToastUtils.showToast(getApplicationContext(), type == login_type ? getString(R.string.fb_login_success) : "Share in successed!");
            getFbFriends();
            AnalyzeConstant.event(AnalyzeConstant.login_fb_successed);
        }

        @Override
        public void onCancel() {
            ToastUtils.showToast(getApplicationContext(), type == login_type ? getString(R.string.fb_login_cancel) : "Share in Cancel!");
            HashMap map = new HashMap<>();
            map.put("error", "cancel");
            AnalyzeConstant.event(AnalyzeConstant.login_fb_failed);
        }

        @Override
        public void onError(FacebookException error) {
            ToastUtils.showToast(getApplicationContext(), type == login_type ? getString(R.string.fb_login_fail) : "Share in fail!");
            HashMap map = new HashMap<>();
            map.put("error", error);
            AnalyzeConstant.event(AnalyzeConstant.login_fb_failed);
        }
    }


    private CallbackManager fbcallbackMgr = CallbackManager.Factory.create();

    public FBUtils mFbUtils;

    //    private Map<FBFriendsModel.PayloadBean, Boolean> selectFriends = new HashMap<>();
    private Handler handler = new Handler();


//    private EditText mEditTextName;
//    private EditText mEditTextPassword;
//    private Button mLoginButton;
//    private TextInputLayout mTextInputLayoutName;
//    private TextInputLayout mTextInputLayoutPswd;

    private ListView friendListView;
    private Button selectAllBtn;
    private Button inviteBtn;
    private View opreateBtns;
    private FbFriendAdapter adapter;
    private LoginButton loginFbBtn;
    private View empty_view;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        TextView mTitle = (TextView) findViewById(R.id.tv_title);
        mTitle.setTypeface(FontsUtils.getType(FontsUtils.TITLE_FONT));
        mTitle.setText(R.string.facebook_login_note);
        LinearLayout mBack = (LinearLayout) findViewById(R.id.ll_back);
        mBack.setOnClickListener(this);

//        mTextInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
//        mTextInputLayoutPswd = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
//
//        mEditTextName = (EditText) findViewById(R.id.editTextName);
//        mTextInputLayoutName.setErrorEnabled(true);
//        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
//        mTextInputLayoutPswd.setErrorEnabled(true);
//
//        mLoginButton = (Button) findViewById(R.id.buttonLogin);
//        mLoginButton.setOnClickListener(this);
//        mEditTextName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                checkName(s.toString(), false);
//            }
//        });
//
//        mEditTextPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                checkPswd(s.toString(), false);
//            }
//        });


        // TODO: 2017/7/19   login and init
        mFbUtils = new FBUtils(getString(R.string.facebook_id));
        loginFbBtn = (LoginButton) findViewById(R.id.facebook_login);
        loginFbBtn.setReadPermissions("email");
        loginFbBtn.registerCallback(fbcallbackMgr, new MyFackbookCallback(login_type));

        inviteBtn = (Button) findViewById(R.id.bt_invite);
        inviteBtn.setOnClickListener(this);
        friendListView = (ListView) findViewById(R.id.friend_list);
        empty_view = findViewById(R.id.empty_view);

        selectAllBtn = (Button) findViewById(R.id.bt_select_all);
        selectAllBtn.setOnClickListener(this);
        adapter = new FbFriendAdapter(null);
        friendListView.setAdapter(adapter);

        getFbFriends();
    }


    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.buttonLogin) {
//            hideKeyBoard();
//            if (!checkName(mEditTextName.getText(), true))
//                return;
//            if (!checkPswd(mEditTextPassword.getText(), true))
//                return;
//            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
//            finish();
//        } else
        if (v.getId() == R.id.ll_back) {
            finish();
        } else if (v.getId() == R.id.bt_invite) {
            if (adapter != null && adapter.selects != null) {
                if (adapter.selects.size() == 0) {
                    ToastUtils.showToast(getApplicationContext(), R.string.fb_select_friend_note);
                }

                List<FBFriendsModel.PayloadBean> sequenceSet = new ArrayList<>();
                sequenceSet.addAll(adapter.selects);
                StringBuilder ids = new StringBuilder();
                int size = sequenceSet.size();
                for (int i = 0; i < size; i++) {
                    ids.append(sequenceSet.get(i).getUid());
                    ids.append(",");
                    if ((i + 1) % 50 == 0 || i == size - 1) {
                        sendInvite(ids);
                        ids = new StringBuilder();
                    }
                }
            }

        } else if (v.getId() == R.id.bt_select_all) {
            // TODO: 01/08/2017  select all
            if (adapter != null && adapter.models != null) {
                adapter.setSelectAll();
            }
        }
    }

    private void getFbFriends() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        MyLog.i("accessToken=" + accessToken);
        if (accessToken != null) {
            selectAllBtn.setVisibility(View.INVISIBLE);
            inviteBtn.setVisibility(View.INVISIBLE);
            loginFbBtn.setVisibility(View.INVISIBLE);
            empty_view.setVisibility(View.VISIBLE);
            mFbUtils.fetchFriends(new FbRequest.FbFetchFriendCallback() {
                @Override
                public void onFinish(List<FBFriendsModel.PayloadBean> model1) {

                    AnalyzeConstant.event(AnalyzeConstant.fetch_fbfriends_successed);
                    doInvite(model1);
                }

                @Override
                public void onFail(String error) {
                    MyLog.i("onFail=" + error);
                    HashMap map = new HashMap<>();
                    map.put("error", error);
                    AnalyzeConstant.event(AnalyzeConstant.fetch_fbfriends_failed, map);
                }

                @Override
                public void onFetchItem(FBFriendsModel.PayloadBean modelPayload) {
                    MyLog.i("modelPayload=" + modelPayload.getText() + ",uid=" + modelPayload.getUid());
                }
            });
        } else {
            ToastUtils.showToast(getApplicationContext(), R.string.fb_login_note);
            selectAllBtn.setVisibility(View.INVISIBLE);
            inviteBtn.setVisibility(View.INVISIBLE);
            loginFbBtn.setVisibility(View.VISIBLE);
        }
    }

    private void doInvite(final List<FBFriendsModel.PayloadBean> model) {
        if (model != null) {
            int size = model.size();
            StringBuilder ids = new StringBuilder();
            final CharSequence[] items = new CharSequence[size];
            for (int i = 0; i < size; i++) {
//                        ids += model.get(i).getUid() + ",";
                ids.append(model.get(i).getUid());
                ids.append(",");
                items[i] = model.get(i).getText();
            }
            MyLog.i("friendlist=" + ids);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        selectAllBtn.setVisibility(View.VISIBLE);
                        inviteBtn.setVisibility(View.VISIBLE);
                        loginFbBtn.setVisibility(View.INVISIBLE);
                        empty_view.setVisibility(View.GONE);
                        adapter.setModels(model);
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(FBInviteActivity.this);
//                        builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                                selectFriends.put(model.get(which), isChecked);
//                            }
//                        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Set<FBFriendsModel.PayloadBean> sequenceSet = selectFriends.keySet();
//                                StringBuilder ids = new StringBuilder();
//                                for (FBFriendsModel.PayloadBean key : sequenceSet) {
//                                    ids.append(key.getUid());
//                                    ids.append(",");
//                                }
//                                sendInvite(ids);
//                                MyLog.i("friendlist=" + ids);
//                            }
//                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.log(e.getMessage());
                    }
                }
            });
        } else {
            ToastUtils.showToast(getApplicationContext(), R.string.fb_fetch_friend_note);
        }
    }

    private void sendInvite(StringBuilder ids) {
        if (ids.length() == 0) {
            return;
        }
        String substring = ids.substring(0, ids.length() - 1);
        mFbUtils.sendInvites(substring, new FbRequest.FbInviteCallback() {
            @Override
            public void onSuccess(String keys) {
                MyLog.i("发送邀请成功 ,uid=" + keys);
                AnalyzeConstant.event(AnalyzeConstant.invite_fbfriend_successed);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(getApplicationContext(), R.string.fb_share_success);
                        finish();
                    }
                });
            }

            @Override
            public void onFail(String error) {
                MyLog.i("error=" + error);
                HashMap map = new HashMap<>();
                map.put("error", error);
                AnalyzeConstant.event(AnalyzeConstant.invite_fbfriend_failed, map);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(getApplicationContext(), R.string.fb_share_fail);
                    }
                });
            }
        });
    }


//    private boolean checkPswd(CharSequence pswd, boolean isLogin) {
//        if (TextUtils.isEmpty(pswd)) {
//            if (isLogin) {
//                mTextInputLayoutPswd.setError(getString(R.string.error_pswd_empty));
//                return false;
//            }
//        } else {
//            mTextInputLayoutPswd.setError(null);
//        }
//        return true;
//    }
//
//    private boolean checkName(CharSequence name, boolean isLogin) {
//        if (TextUtils.isEmpty(name)) {
//            if (isLogin) {
//                mTextInputLayoutName.setError(getString(R.string.error_login_empty));
//                return false;
//            }
//        } else {
//            mTextInputLayoutName.setError(null);
//        }
//        return true;
//    }

    private void hideKeyBoard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbcallbackMgr.onActivityResult(requestCode, resultCode, data);
    }


    private class FbFriendAdapter extends BaseAdapter {
        List<FBFriendsModel.PayloadBean> models;
        Set<FBFriendsModel.PayloadBean> selects;

        public FbFriendAdapter(List<FBFriendsModel.PayloadBean> models) {
            this.models = models;
            selects = new HashSet<>();
        }

        public void setModels(List<FBFriendsModel.PayloadBean> models) {
            this.models = models;
            notifyDataSetChanged();
        }


        public void setSelectAll() {
            if (selects.size() == models.size()) {
                for (FBFriendsModel.PayloadBean bean : models) {
                    bean.isSelect = false;
                }
                selects.clear();
            } else {
                for (FBFriendsModel.PayloadBean bean : models) {
                    bean.isSelect = true;
                }
                selects.addAll(models);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (models == null)
                return 0;
            return models.size();
        }

        @Override
        public Object getItem(int position) {
            return models.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final FBFriendsModel.PayloadBean bean = models.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(FBInviteActivity.this).inflate(R.layout.friend_item, null);
            }
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(R.id.checkedTextView);
            checkedTextView.setText(TextUtils.isEmpty(bean.getDisplay()) ? bean.getText() : bean.getDisplay());
            checkBox.setChecked(bean.isSelect);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selects.add(bean);
                    } else {
                        selects.remove(bean);
                    }
                }
            });
            return convertView;
        }
    }

}
