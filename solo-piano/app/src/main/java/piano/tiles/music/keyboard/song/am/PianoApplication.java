package piano.tiles.music.keyboard.song.am;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.ads.AdSettings;
import com.facebook.samples.ads.debugsettings.DebugSettings;
import com.flurry.android.FlurryAgent;

import io.fabric.sdk.android.Fabric;


public class PianoApplication extends Application {
    private final static String TAG = "PianoApplication";

    //    public static final String HASHED_ID = "87a8864ffaa43b0533ef44b86a04fe8b";//M2
//    public static final String HASHED_ID = "87a8864ffaa43b0533ef44b86a04fe8b";//LG
//    public static final String HASHED_ID = "5ee37527e155eb9446c0d7099e52d381";//Nex
//    public static final String HASHED_ID = "55c729c3e8bb033b84607eb3f9244b18";//M
    public static final String HASHED_ID = "61bdc0ee7c40588ac285892c721af367";//M1
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        L.v(TAG, "app start...");

        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, Constants.FLURRY_KEY);
        FlurryAgent.setContinueSessionMillis(90 * 1000);
        FlurryAgent.setReportLocation(false);


        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(getApplicationContext());

        AdSettings.addTestDevice(HASHED_ID);

        DebugSettings.initialize(this);

    }
   

    @Override
    public void onTerminate() {
        super.onTerminate();

    }
}
