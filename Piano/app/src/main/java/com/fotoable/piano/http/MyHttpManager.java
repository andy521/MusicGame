package com.fotoable.piano.http;

import android.text.TextUtils;
import android.util.Log;

import com.fotoable.piano.MyApplication;
import com.fotoable.piano.R;
import com.fotoable.piano.entity.AllSongData;
import com.fotoable.piano.game.entity.LevelItem;
import com.fotoable.piano.game.shared.SharedLevel;
import com.fotoable.piano.game.shared.SharedOther;
import com.fotoable.piano.game.shared.SharedPlayed;
import com.fotoable.piano.game.shared.SharedSongs;
import com.fotoable.piano.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by damon on 29/06/2017.
 */

public class MyHttpManager {

    private static final String TAG = "MyHttpManager";
    public static final String SONG_JSON_URL = "http://dl.fotoable.net/magic_piano/songbooks.json";
    public static final String LEVEL_JSON_URL = "http://dl.fotoable.net/magic_piano/level.json";


    public interface MyCallback {
        void onError();

        void onSuccess(String midPath);
    }

    public static void checkRefreshSongJson() {
        if (!SharedOther.checkNeedUpdate()) {
            Log.e(TAG, "no need to download son json");
            return;
        }
        OkHttpClient client = new OkHttpClient();
        requestSongJson(client);
        requestLevelJson(client);
    }

    private static void requestSongJson(OkHttpClient client) {
        Request request = new Request.Builder().url(SONG_JSON_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "checkRefreshSongJson onFailure-->>" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        AllSongData allSongData = gson.fromJson(result, AllSongData.class);
                        SharedSongs.updateAllSongs(allSongData);
                        SharedPlayed.dirtyAllSong = true;
                        SharedSongs.getAllSongs();
                        SharedOther.updateDownloadSongJsonTime(System.currentTimeMillis());
                        Log.d(TAG, "sever song json  -->>" + allSongData.toString());
                    } else {
                        Log.e(TAG, "get default song json error-->>" + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void requestLevelJson(OkHttpClient client) {
        Request request = new Request.Builder().url(LEVEL_JSON_URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "checkRefreshSongJson onFailure-->>" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        Gson gson = new Gson();
                        ArrayList<LevelItem> result = gson.fromJson(json, new TypeToken<ArrayList<LevelItem>>() {
                        }.getType());
                        SharedLevel.updateLevel(result);
                        SharedOther.updateDownloadSongJsonTime(System.currentTimeMillis());
                        Log.d(TAG, "sever level json  -->>" + result.toString());
                    } else {
                        Log.e(TAG, "get default level json error-->>" + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param url      mid url
     * @param callback 回调 不能为null
     */
    public static void downloadMid(String url, final MyCallback callback) {

        if (callback == null) {
            Log.e(TAG, "downloadMid-->>callback==null");
            return;
        }
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "downloadMid-->>url==null");
            callback.onError();
            return;
        }

        final String midName = getMD5(url);

        if (TextUtils.isEmpty(midName)) {
            Log.e(TAG, "downloadMid-->>midName==null");
            callback.onError();
            return;
        }


        final File file = MyApplication.application.getFilesDir();
        final File midFile = new File(file, midName);
        if (midFile.exists() && midFile.isFile()) {
            Log.e(TAG, "downloadMid-->>from local-->>" + midFile.getAbsolutePath());
            callback.onSuccess(midFile.getAbsolutePath());
            return;
        }


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "downloadMid-->>checkRefreshSongJson onFailure-->>" + e.getMessage());
                callback.onError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            inputstreamtofile(body.byteStream(), midFile);
                            if (midFile.exists() && midFile.isFile()) {
                                Log.d(TAG, "downloadMid-->>from server-->>" + midFile.getAbsolutePath());
                                callback.onSuccess(midFile.getAbsolutePath());
                            } else {
                                Log.e(TAG, "downloadMid-->>onResponse, download error -->>" + response);
                                callback.onError();
                            }
                        } else {
                            Log.e(TAG, "downloadMid-->>onResponse, body==null -->>" + response);
                            callback.onError();
                        }
                    } else {
                        Log.e(TAG, "downloadMid-->>onResponse not success-->>" + response);
                        callback.onError();
                    }
                } catch (Exception e) {
                    callback.onError();
                    e.printStackTrace();
                }
            }
        });
    }

    public static void inputstreamtofile(InputStream ins, File file) {
        OutputStream os;
        try {
            os = new FileOutputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return null;
        }
    }

    public static synchronized void clear() {
        //
        File file = MyApplication.application.getFilesDir();
        File[] childFiles = file.listFiles();

        for (int i = 0; i < childFiles.length; i++) {
            File temp = childFiles[i];
            if (temp.isFile()) {
                temp.delete();
            }
        }
        ToastUtils.showToast(MyApplication.application, MyApplication.application.getResources().getString(R.string.clear_success));

    }
}
