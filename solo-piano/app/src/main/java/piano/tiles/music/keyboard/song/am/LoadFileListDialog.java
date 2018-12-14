package piano.tiles.music.keyboard.song.am;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yanwei on 4/20/16.
 */
public class LoadFileListDialog extends Dialog {
    private Context context;
    private LinearLayout llList;
    private OnFileSelected callback;
    private TextView tv_file_tips;
    private ShareDialog shareDialog;

    public LoadFileListDialog(Context cxt, OnFileSelected cb)
    {
        super(cxt);

        context = cxt;
        callback = cb;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.load_file_dialog);
        getWindow().setBackgroundDrawable(new BitmapDrawable());
        tv_file_tips = (TextView) findViewById(R.id.tv_file_tips);
        llList = (LinearLayout)findViewById(R.id.llFile);

        File[] fileList = new File(RecordManager.getRecordDirectory(cxt)).listFiles(RecordManager.recordFileFilter);
        if(fileList.length == 0)
        {
            //Toast.makeText(cxt, R.string.no_saved_file + RecordManager.getRecordDirectory(cxt), Toast.LENGTH_SHORT);
            tv_file_tips.setText(R.string.no_saved_file);
            return;
        }

        tv_file_tips.setText("Choose Record file");

        Arrays.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                if(lhs == null || lhs.getPath() == null)
                    return 1;
                if(rhs == null || rhs.getPath() == null)
                    return -1;

                int ret = lhs.getPath().compareTo(rhs.getPath());
                if(ret < 0)
                    return 1;
                else if(ret > 0)
                    return -1;
                return 0;
            }
        });

        shareDialog = new ShareDialog((Activity) context);
        for(int i = 0; i < fileList.length; i++)
        {
            final File file = fileList[i];
            if(file.isFile())
            {

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
                TextView btnFile = new TextView(cxt);
                btnFile.setText(file.getName());
                btnFile.setPadding(20,10,20,10);
                btnFile.setGravity(Gravity.CENTER);
                params2.setMargins(0,25,0,25);


                View view = new View(cxt);
                view.setBackgroundColor(Color.BLACK);
                llList.addView(btnFile);
                llList.addView(view,params2);
                btnFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                            String imgeUrl = "http://cdn.dl.fotoable.net/mobiledev/sc/icon.png";
//
//
//                            String description = "This is a very good listening to client";
//
//
//                            String title = "Music Player";
//
//                        ShareLinkContent mLike = new ShareLinkContent.Builder()
//                                .setContentUrl(Uri.parse(Constants.SHRELINK))
//                                .setContentDescription(description)
//                                .setImageUrl(Uri.parse(imgeUrl))
//                                .setContentTitle(title)
//                                .build();
//                        shareDialog.show((Activity) context, mLike);

                        String path = RecordManager.getRecordDirectory(context) + "/" + file.getName();
                        RecordManager.loadRecord(path);

                        LoadFileListDialog.this.dismiss();

                        if(callback != null)
                            callback.onFileSelected(path);
                    }
                });
            }
        }
    }
}
