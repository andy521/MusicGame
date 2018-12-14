package piano.tiles.music.keyboard.song.am;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordManager;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by lk on 16/5/23.
 */
public class SaveFileDialog extends Dialog {

    private Context context;
    private LinearLayout llList;
    private OnFileSelected callback;
    private TextView tv_file_tips;
    public SaveFileDialog(Context cxt) {
        super(cxt);

        context = cxt;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.load_file_dialog);
        getWindow().setBackgroundDrawable(new BitmapDrawable());


    }
}
