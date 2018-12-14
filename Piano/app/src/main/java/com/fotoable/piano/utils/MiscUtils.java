package com.fotoable.piano.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fotoable.piano.R;

import java.io.File;
import java.io.IOException;

/**
 * 音乐播放 所需util
 * Created by houfutian on 2017/6/14.
 */
public class MiscUtils {
    private static final String TAG = MiscUtils.class.getSimpleName();
    private static boolean sLogException = true;
    private static int sSessions = -1;

    public static int getInstallDays(long installDate) {
        return Math.round((float) Math.floor(((double) ((System.currentTimeMillis() / 1000) - (installDate / 1000))) / 86400.0d)) + 1;
    }

    public static int getSessions() {
        return sSessions;
    }

//    public static void incSessions(Context context) {
//        SharedPreferences pref = context.getSharedPreferences(MagicPreferences.FILE_NAME, 0);
//        if (sSessions == -1) {
//            sSessions = pref.getInt("sessions", 0);
//        }
//        sSessions++;
////        SharedPreferencesCompat.apply(pref.edit().putInt("sessions", sSessions));
//    }

//    public static String createElapsedTimeString(Context context,long timeInMillis) {
////        Context context = MagicNetwork.delegate().getApplicationContext();
//        long elapsedTimeInSecs = (System.currentTimeMillis() - timeInMillis) / 1000;
//        if (elapsedTimeInSecs < 0) {
//            elapsedTimeInSecs = 0;
//        }
//        if (elapsedTimeInSecs < 60) {
//            if (elapsedTimeInSecs <= 1) {
//                return context.getString(R.string.secondAgo);
//            }
//            return String.format(context.getString(R.string.secondAgo), new Object[]{Long.valueOf(elapsedTimeInSecs)});
//        } else if (elapsedTimeInSecs < 3600) {
//            if (elapsedTimeInSecs / 60 <= 1) {
//                return context.getString(R.string.minuteAgo);
//            }
//            return String.format(context.getString(R.string.minuteAgo), new Object[]{Long.valueOf(elapsedTimeInSecs / 60)});
//        } else if (elapsedTimeInSecs < 86400) {
//            if (elapsedTimeInSecs / 3600 <= 1) {
//                return context.getString(R.string.hourAgo);
//            }
//            return String.format(context.getString(R.string.hourAgo), new Object[]{Long.valueOf(elapsedTimeInSecs / 3600)});
//        } else if (elapsedTimeInSecs >= 31536000) {
//            return context.getString(R.string.moreThanAYearAgo);
//        } else {
//            if (elapsedTimeInSecs / 86400 <= 1) {
//                return context.getString(R.string.dayAgo);
//            }
//            return String.format(context.getString(R.string.dayAgo), new Object[]{Long.valueOf(elapsedTimeInSecs / 86400)});
//        }
//    }

    public static String getMopubKeywords() {
        return String.format("market:%s,version:%s,model:%s", new Object[]{"1.0", Build.MODEL});
    }

    public static void showSoftKeyboard(final Activity activity, final EditText editText) {
        editText.postDelayed(new Runnable() {
            public void run() {
                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 0);
            }
        }, 200);
    }

//    public static String getAndroidKeyHash(Context context) {
//        String result = "";
//        try {
//            for (Signature signature : context.getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                result = result + "Sig: " + Base64.encode(md.digest()) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
//            }
//        } catch (NameNotFoundException e) {
//        } catch (NoSuchAlgorithmException e2) {
//        }
//        return result;
//    }

//    public static boolean isFlurryAdEnabled(String fieldName) {
//        if (!AppSettingsManager.getInstance().settingsAvailable()) {
//            return false;
//        }
//        return "FLURRY".equals(AppSettingsManager.getInstance().getStringValue("piandroid.adConfig", fieldName, "").toUpperCase());
//    }
//
//    public static boolean isDFPAdEnabled(String fieldName) {
//        if (!AppSettingsManager.getInstance().settingsAvailable()) {
//            return false;
//        }
//        String adSource = AppSettingsManager.getInstance().getStringValue("piandroid.adConfig", fieldName, "");
//        if ("DFP".equals(adSource.toUpperCase()) || "BURSTLY".equals(adSource.toUpperCase())) {
//            return true;
//        }
//        return false;
//    }
//
//    public static boolean isAdColonyEnabled(String fieldName) {
//        if (!AppSettingsManager.getInstance().settingsAvailable()) {
//            return false;
//        }
//        return "ADCOLONY".equals(AppSettingsManager.getInstance().getStringValue("piandroid.adConfig", fieldName, "").toUpperCase());
//    }

    @TargetApi(11)
    public static void enableStrictMode() {
//        if (hasGingerbread()) {
//            Builder threadPolicyBuilder = new Builder().detectAll().penaltyLog();
//            VmPolicy.Builder vmPolicyBuilder = new VmPolicy.Builder().detectAll().penaltyLog();
//            if (hasHoneycomb()) {
//                threadPolicyBuilder.penaltyFlashScreen();
//                vmPolicyBuilder.setClassInstanceLimit(StartupActivity.class, 1).setClassInstanceLimit(ProductListActivity.class, 1);
//            }
//            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
//            StrictMode.setVmPolicy(vmPolicyBuilder.build());
//        }
    }

    public static boolean hasGingerbread() {
        return VERSION.SDK_INT >= 9;
    }

    public static boolean hasHoneycomb() {
        return VERSION.SDK_INT >= 11;
    }

    public static void setFlurryCookies() {
//        String playerId = UserManager.getInstance().player();
//        if (playerId != null) {
//            Log.i(TAG, "Using Player ID " + playerId + " for Flurry ADs.");
//            Map<String, String> cookies = new HashMap();
//            cookies.put("player_id", playerId);
//            FlurryAds.setUserCookies(cookies);
//            return;
//        }
        Log.w(TAG, "Player ID is not available for Flurry ADs.");
    }

    public static boolean extractNamedResource(Context context, String name, File sfFile) {
        try {
            try {
                try {
                    ResourceUtils.extractStreamToFile(context.getResources().openRawResource(R.raw.class.getField(name).getInt(null)), sfFile, true);
                    return true;
                } catch (IOException e) {
                    Log.e(TAG, "couldn't open stream for " + name);
//                    Crittercism.leaveBreadcrumb("couldn't open stream for " + name + ", " + phone_storage_free() + " bytes free on device.");
                    logExceptionOnce(e);
                    return false;
                }
            } catch (NotFoundException e2) {
                Log.e(TAG, "resource not found: " + name);
//                Crittercism.leaveBreadcrumb("raw resource not found: " + name);
                logExceptionOnce(e2);
                return false;
            }
        } catch (NoSuchFieldException e3) {
            Log.i(TAG, "resource field not found: " + name);
            return false;
        } catch (IllegalArgumentException e4) {
            Log.e(TAG, "IllegalArgumentException: " + name);
//            Crittercism.leaveBreadcrumb("IllegalArgumentException: " + name);
            logExceptionOnce(e4);
            return false;
        } catch (IllegalAccessException e5) {
            Log.e(TAG, "IllegalAccessException:" + name);
//            Crittercism.leaveBreadcrumb("IllegalAccessException:" + name);
            logExceptionOnce(e5);
            return false;
        }
    }

    private static void logExceptionOnce(Exception e) {
        if (sLogException) {
//            Crittercism.logHandledException(e);
            sLogException = false;
        }
    }

    public static Runnable uiRunnable(final Activity activity, final Runnable r) {
        return new Runnable() {
            public void run() {
                activity.runOnUiThread(r);
            }
        };
    }

    public static long phone_storage_free() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        return (long) (stat.getAvailableBlocks() * stat.getBlockSize());
    }

    public static void removeGlobalListenerFromObserver(OnGlobalLayoutListener listener, ViewTreeObserver observer) {
        if (VERSION.SDK_INT >= 16) {
            observer.removeOnGlobalLayoutListener(listener);
        } else {
            observer.removeGlobalOnLayoutListener(listener);
        }
    }

//    public static LinkedList<Pair<String, String>> getSingerInfo(SongV2 song) {
//        ObjectMapper mapper = new ObjectMapper();
//        LinkedList<Pair<String, String>> singers = new LinkedList();
//        if (song.extraData != null) {
//            try {
//                JsonNode rootNode = (JsonNode) mapper.readValue(song.extraData, JsonNode.class);
//                if (rootNode.has("piano")) {
//                    JsonNode pianoNode = rootNode.get("piano");
//                    if (pianoNode.has("singer_info")) {
//                        Iterator<JsonNode> itr = pianoNode.get("singer_info").elements();
//                        while (itr.hasNext()) {
//                            JsonNode singerNode = (JsonNode) itr.next();
//                            if (singerNode != null) {
//                                String handle = null;
//                                String url = null;
//                                if (singerNode.has("handle")) {
//                                    handle = singerNode.get("handle").asText();
//                                }
//                                if (singerNode.has("pic_url")) {
//                                    url = singerNode.get("pic_url").asText();
//                                }
//                                singers.add(new Pair(handle, url));
//                            }
//                        }
//                    }
//                }
//            } catch (IOException e) {
//            }
//        }
//        return singers;
//    }
}
