# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/fookwood/android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# http://developer.android.com/google/play-services/setup.html#Proguard

-dontwarn com.flurry.sdk.*

-keepclasseswithmembers class * {            # 保持自定义控件类不被混淆
    public <init>(android.content.Context);
}

-keepclasseswithmembers class * {            # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {            # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keepattributes SourceFile,LineNumberTable

-keepattributes Signature

-dontnote org.apache.http.conn.*
-dontnote org.apache.http.params.HttpParams
-dontnote org.apache.http.conn.scheme.*
-dontnote android.net.http.*

-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.android.gms.ads.identifier.AdvertisingIdClient
-dontnote com.google.android.gms.common.GooglePlayServicesUtil
-dontnote com.google.android.gms.ads.identifier.AdvertisingIdClient$Info

-dontnote io.fabric.sdk.android.services.common.AdvertisingInfoReflectionStrategy
-dontnote android.support.v4.text.*
-dontnote android.support.v7.internal.widget.DrawableUtils

-dontwarn com.viewpagerindicator.LinePageIndicator

# for Fresco
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**