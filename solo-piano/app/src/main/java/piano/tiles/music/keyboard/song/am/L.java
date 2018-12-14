package piano.tiles.music.keyboard.song.am;

import android.util.Log;

/**
 * Created by yanwei on 4/27/16.
 */
public class L {

    public static void v(String tag, String msg)
    {
        if(BuildConfig.LOG_DEBUG)
            Log.v(tag, msg);
    }

    public static void d(String tag, String msg)
    {
        if(BuildConfig.LOG_DEBUG)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg)
    {
        if(BuildConfig.LOG_DEBUG)
            Log.i(tag, msg);
    }
}
