package com.fotoable.piano.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.fotoable.piano.MyApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 对assets中的文件进行存储转换成file文件
 * Created by houfutian on 2017/6/14.
 */

public class ResourceUtils {
    static final String TAG = ResourceUtils.class.getName();

    public static String applicationFilesDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static String cacheDir(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    public static boolean isCached(String filename, Context context) {
        return new File(cacheDir(context) + "/" + filename).exists();
    }

    public static long getCacheSize(Context context) {
        return context.getCacheDir().getFreeSpace();
    }

    public static File fileForAsset(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists() || extractAsset(context, fileName, file)) {
            return file;
        }
        Log.e(TAG, "Couldn't extract asset: " + fileName);
        return null;
    }

    public static boolean extractNamedResource(Context context, String name, File sfFile) {
        return false;
    }

    public static boolean extractAsset(Context context, String path, File file) {
        try {
            extractStreamToFile(context.getAssets().open(path), file, true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void extractStreamToFile(InputStream in, File file, boolean overwrite) throws IOException {
        byte[] buffer = new byte[2048];
        BufferedInputStream bin = new BufferedInputStream(in, 2048);
        if (overwrite || !file.exists()) {
            file.getParentFile().mkdirs();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), 2048);
            while (true) {
                int nRead = bin.read(buffer, 0, 2048);
                if (nRead <= 0) {
                    break;
                }
                bos.write(buffer, 0, nRead);
            }
            bos.flush();
            bos.close();
        }
        in.close();
    }

    public static File createJPGInPublicPicturesDirectory() {
        return createFileInPublicDirectory(Environment.DIRECTORY_PICTURES, "IMG_", ".jpg");
    }

    public static File createFileInPublicDirectory(String directoryType, String fileNamePrefix, String fileExtension) {
        File storageDirectory = new File(Environment.getExternalStoragePublicDirectory(directoryType), "smule");
        if (storageDirectory.exists() || storageDirectory.mkdirs()) {
            return new File(storageDirectory.getPath() + File.separator + fileNamePrefix + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + fileExtension);
        }
        Log.d(TAG, "Failed to create directory that would contain file!");
        return null;
    }

    public static String readRawResource(Context context, int resourceId) throws IOException {
        InputStream is = context.getResources().openRawResource(resourceId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while (true) {
                int n = reader.read(buffer);
                if (n == -1) {
                    break;
                }
                writer.write(buffer, 0, n);
            }
            return writer.toString();
        } finally {
            is.close();
        }
    }

    public static String sdcardDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    public static boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) MyApplication.application.getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (android.app.ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(MyApplication.application.getPackageName())) {
                if (appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                }else{
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
}
