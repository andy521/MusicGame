package com.ytwd.midiengine.utils;

import android.app.Notification;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by dll on 16/1/8.
 */
public class BadgeManager {
    static String lancherActivityClassName = "";

    /**
     * 清理桌面icon的数字角标
     * @param context
     */
    public static void clearBadge(Context context){
        sendBadgeNumber(context,0);
    }

    /**
     * 在桌面icon上添加数字角标
     * @param context
     */
    public static void addBadge(Context context){
        sendBadgeNumber(context,1);
    }

    private static void sendBadgeNumber(Context context,int number) {
        sendBadgeNumber(context,""+number);
    }
    private static void sendBadgeNumber(Context context,String number) {
        try{
            lancherActivityClassName = getLauncherClassName(context);
            if (lancherActivityClassName == number){
                return;
            }
            if (TextUtils.isEmpty(number)) {
                number = "";
            } else {
                int numInt = Integer.valueOf(number);
                number = String.valueOf(Math.max(0, Math.min(numInt, 99)));
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            String currentHomePackage = resolveInfo.activityInfo.packageName;//当前桌面
            String manufacturer = Build.MANUFACTURER;//设备生产商
            if (isXiaomiLauncher(currentHomePackage)) {
                sendToXiaoMi(context,number);
            } else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
                sendToSony(context,number);
            } else if (Build.MANUFACTURER.toLowerCase().contains("bbk")){//vivo

            } else if ("org.adw.launcher".toLowerCase().contains(currentHomePackage) || "org.adwfreak.launcher".toLowerCase().contains(currentHomePackage)){
                sendToAdwLauncher(context,number);
            } else if ("com.anddoes.launcher".toLowerCase().contains(currentHomePackage)){
                sendToApexLauncher(context,number);
            } else if ("com.teslacoilsw.launcher".toLowerCase().contains(currentHomePackage)){
                sendToNovaLauncher(context,number);
            } else if ("com.htc.launcher".toLowerCase().contains(currentHomePackage)){
                sendToNewHtcLauncher(context,number);
            } else if ("com.majeur.launcher".toLowerCase().contains(currentHomePackage)){
                sendToSolidLauncher(context,number);
            }else {//samsung,LG,asus桌面
                sendToDefault(context,number);
            }
        }catch (Exception e) {

        }catch (Error error) {

        }
    }

    private static boolean isXiaomiLauncher(String currentHomePackage){
        boolean value = false;
        value = "com.miui.miuilite".toLowerCase().contains(currentHomePackage) || "com.miui.home".toLowerCase().contains(currentHomePackage) ||
                "com.miui.miuihome".toLowerCase().contains(currentHomePackage) || "com.miui.miuihome2".toLowerCase().contains(currentHomePackage) ||
                "com.miui.mihome".toLowerCase().contains(currentHomePackage) || "com.miui.mihome2".toLowerCase().contains(currentHomePackage);
        return value;
    }

    private static String getLauncherClassName(Context context) {
        try{
            PackageManager packageManager = context.getPackageManager();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            // To limit the components this Intent will resolve to, by setting an
            // explicit package name.
            intent.setPackage(context.getPackageName());
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // All Application must have 1 Activity at least.
            // Launcher activity must be found!
            ResolveInfo info = packageManager
                    .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

            // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
            // if there is no Activity which has filtered by CATEGORY_DEFAULT
            if (info == null) {
                info = packageManager.resolveActivity(intent, 0);
            }

            return info.activityInfo.name;
        }catch (Throwable e){
            return null;
        }
    }

    private static void sendToXiaoMi(Context context,String number) {
        int badgeCount = Integer.valueOf(number);
        try {
            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, String.valueOf(badgeCount == 0 ? "" : badgeCount));
        } catch (Exception e) {
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            ComponentName componentName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent();
            localIntent.putExtra("android.intent.extra.update_application_component_name", componentName.getPackageName() + "/" + componentName.getClassName());
            localIntent.putExtra("android.intent.extra.update_application_message_text", String.valueOf(badgeCount == 0 ? "" : badgeCount));
            context.sendBroadcast(localIntent);
        }
    }

    private static void sendToSony(Context context,String number) {
        boolean isShow = true;
        if ("0".equals(number)) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);//是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",lancherActivityClassName );//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", Integer.valueOf(number));//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());//包名
        context.sendBroadcast(localIntent);
    }

    private static void sendToAdwLauncher(Context context,String number){
        Intent intent = new Intent("org.adw.launcher.counter.SEND");
        intent.putExtra("PNAME", context.getPackageName());
        intent.putExtra("COUNT", Integer.valueOf(number));
        context.sendBroadcast(intent);
    }

    private static void sendToApexLauncher(Context context, String number){
        Intent intent = new Intent("com.anddoes.launcher.COUNTER_CHANGED");
        intent.putExtra("package", context.getPackageName());
        intent.putExtra("count", Integer.valueOf(number));
        intent.putExtra("class", lancherActivityClassName);
        context.sendBroadcast(intent);
    }

    private static void sendToNewHtcLauncher(Context context, String number){

        Intent intent1 = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
        intent1.putExtra("com.htc.launcher.extra.COMPONENT", context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().flattenToShortString());
        intent1.putExtra("com.htc.launcher.extra.COUNT", Integer.valueOf(number));
        context.sendBroadcast(intent1);

        Intent intent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
        intent.putExtra("packagename", context.getPackageName());
        intent.putExtra("count", Integer.valueOf(number));
        context.sendBroadcast(intent);
    }

    private static void sendToNovaLauncher(Context context, String number){
        try {
            ComponentName componentName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent();
            ContentValues contentValues = new ContentValues();
            contentValues.put("tag", componentName.getPackageName() + "/" + componentName.getClassName());
            contentValues.put("count", Integer.valueOf(number));
            context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), contentValues);
        } catch (IllegalArgumentException ex) {
            /* Fine, TeslaUnread is not installed. */
        } catch (Exception ex) {
        }
    }

    private static void sendToSolidLauncher(Context context, String number){
        ComponentName componentName = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent();
        Intent intent = new Intent("com.majeur.launcher.intent.action.UPDATE_BADGE");
        intent.putExtra("com.majeur.launcher.intent.extra.BADGE_PACKAGE", componentName.getPackageName());
        intent.putExtra("com.majeur.launcher.intent.extra.BADGE_COUNT", Integer.valueOf(number));
        intent.putExtra("com.majeur.launcher.intent.extra.BADGE_CLASS", componentName.getClassName());
        context.sendBroadcast(intent);
    }

    private static void sendToDefault(Context context, String number)
    {
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", Integer.valueOf(number));//数字
        localIntent.putExtra("badge_count_package_name", context.getPackageName());//包名
        localIntent.putExtra("badge_count_class_name", lancherActivityClassName ); //启动页
        context.sendBroadcast(localIntent);
    }


    /**
     * 小米改变图标和通知栏通知绑定
     * NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
     * Notification.Builder builder = new Notification.Builder(this).setContentTitle(“title”).setContentText(“text”).setSmallIcon(R.drawable.icon);
     * Notification notification = builder.build();
     * try {
     *    Field field = notification.getClass().getDeclaredField(“extraNotification”);
     *    Object extraNotification = field.get(notification);
     *    Method method = extraNotification.getClass().getDeclaredMethod(“setMessageCount”, int.class);
     *    method.invoke(extraNotification, mCount);
     * } catch (Exception e) {
     *    e.printStackTrace();
     * }
     * mNotificationManager.notify(0,notification);
     */

    /**
     * 小米改变图标和通知栏通知绑定
     * @param notification
     */
    public static void sendToXiaoMiBadge(Notification notification){
        if (!hasJellyBean()){
            return;
        }
        if (!Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")){
            return;
        }
        try {
            Field field = notification.getClass().getDeclaredField("extraNotification");

            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, 1);
        }catch (Exception e){
            e.printStackTrace();
        }catch (Error e) {
            e.printStackTrace();
        }
    }

    public static boolean hasJellyBean(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return true;
        return false;
    }
}
