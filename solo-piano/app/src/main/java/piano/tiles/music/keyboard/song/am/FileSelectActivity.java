package piano.tiles.music.keyboard.song.am;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.flurry.android.FlurryAgent;
import com.ytwd.midiengine.MidiHelper;
import com.ytwd.midiengine.SimpleKeyStreamRecord.RecordManager;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by lk on 16/6/8.
 */
public class FileSelectActivity extends Activity {

    private ImageButton backImageButton;
    private ShareDialog shareDialog;
    private ListView fileListView;
    private Context context;
    private FileListViewAdapter fileListViewAdapter;
    public static final int RESULT_SELECT_FILE = 10;
    private File[] fileList;
    private String[] items1 = new String[] { "Set As Phone Call Ringtone", "Set As Notifycation Sound","Set As Alarm Clock Rings"};
    private String[] items2 = new String[] { "Share To Facebook"};
    private String[] items3 = new String[] { "Rename", "Delete" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.file_select_activity);

        context = this;
        backImageButton = (ImageButton)findViewById(R.id.about_icon_back);
        backImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putExtra(KEYBOARD_NUM, keyNums);
//                setResult(RESULT_OK_KEYBOARD_NUM, intent);
                finish();
            }
        });
        fileListView = (ListView) findViewById(R.id.file_list);


        fileList = new File(RecordManager.getRecordDirectory(this)).listFiles(RecordManager.recordFileFilter);
        if(fileList.length == 0)
        {
            //Toast.makeText(cxt, R.string.no_saved_file + RecordManager.getRecordDirectory(cxt), Toast.LENGTH_SHORT);
            //tv_file_tips.setText(R.string.no_saved_file);
            return;
        }



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


        shareDialog = new ShareDialog(this);

        fileListViewAdapter = new FileListViewAdapter(this,fileList);
        fileListView.setAdapter(fileListViewAdapter);
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FlurryAgent.logEvent(Constants.ACT_FILE_SELECTED);
                File file = fileList[position];
                String path = RecordManager.getRecordDirectory(context) + "/" + file.getName();
                RecordManager.loadRecord(path);
                Intent intent = new Intent();
                setResult(RESULT_SELECT_FILE, intent);
                finish();

         //       setMyRingtone(path);
            }
        });
//        for(int i = 0; i < fileList.length; i++)
//        {
//            final File file = fileList[i];
//            if(file.isFile())
//            {
//
//                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
//                TextView btnFile = new TextView(cxt);
//                btnFile.setText(file.getName());
//                btnFile.setPadding(20,10,20,10);
//                btnFile.setGravity(Gravity.CENTER);
//                params2.setMargins(0,25,0,25);
//
//
//                View view = new View(cxt);
//                view.setBackgroundColor(Color.BLACK);
//                llList.addView(btnFile);
//                llList.addView(view,params2);
//                btnFile.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                            String imgeUrl = "http://cdn.dl.fotoable.net/mobiledev/sc/icon.png";
////
////
////                            String description = "This is a very good listening to client";
////
////
////                            String title = "Music Player";
////
////                        ShareLinkContent mLike = new ShareLinkContent.Builder()
////                                .setContentUrl(Uri.parse(Constants.SHRELINK))
////                                .setContentDescription(description)
////                                .setImageUrl(Uri.parse(imgeUrl))
////                                .setContentTitle(title)
////                                .build();
////                        shareDialog.show((Activity) context, mLike);
//
//                        String path = RecordManager.getRecordDirectory(context) + "/" + file.getName();
//                        RecordManager.loadRecord(path);
//
//                        LoadFileListDialog.this.dismiss();
//
//                        if(callback != null)
//                            callback.onFileSelected(path);
//                    }
//                });
//            }
//        }




    }




    public class FileListViewAdapter extends BaseAdapter {




        private File[]  coll;
        private LayoutInflater mInflater;
        Context context;
        public FileListViewAdapter(Context context, File[] fileList) {

            this.coll = fileList;
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        public void setItemList(File[] fileList) {
            this.coll = fileList;
        }


        @Override
        public int getCount() {
            return coll.length;
        }

        @Override
        public Object getItem(int position) {
            return coll[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final File file = coll[position];
            ViewHolder viewHolder = null;
            convertView = mInflater.inflate(R.layout.file_select_item,
                    null);

            viewHolder = new ViewHolder();
            viewHolder.tv_file_name = (TextView) convertView
                    .findViewById(R.id.tv_file_name);
            viewHolder.iv_set_ring = (ImageView) convertView
                    .findViewById(R.id.iv_set_ring);
            viewHolder.iv_share = (ImageView) convertView
                    .findViewById(R.id.iv_share);
            viewHolder.iv_delete = (ImageView) convertView
                    .findViewById(R.id.iv_delete);

            viewHolder.tv_file_name.setText(file.getName());

            viewHolder.iv_set_ring.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FlurryAgent.logEvent(Constants.ACT_SET_RING);
                    String path = RecordManager.getRecordDirectory(context) + "/" + file.getName();
                    showDialog1(path);
                }
            });

            viewHolder.iv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FlurryAgent.logEvent(Constants.ACT_SHARE);
                    showDialog2();
                }
            });

            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FlurryAgent.logEvent(Constants.ACT_RENAME_DELETE);
                    showDialog3(file);

                }
            });

            return convertView;
        }

         class ViewHolder {
            public TextView tv_file_name;
            public ImageView iv_set_ring,iv_share,iv_delete;
        }


    }

    //设置--铃声
    public void setMyRingtone(String path)
    {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = this.getContentResolver().insert(uri, values);
        RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);
    }

    //设置--提示音
    public void setMyNotification(String path)
    {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, false);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = this.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION, newUri);
    }
    //设置--闹铃音
    public void setMyAlarm(String path)
    {
        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = this.getContentResolver().insert(uri, values);
        RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM, newUri);
    }


    private void showDialog1(final String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setItems(items1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 自动生成的方法存根

                switch (arg1) {
                    case 0:

                         doSetVoice(path,Constants.RINGTONE);

                        break;
                    case 1:

                         doSetVoice(path,Constants.NOTIFICATION);

                        break;
                    case  2:

                        doSetVoice(path,Constants.ALARM);

                        break;
                }
                arg0.dismiss();
            }
        });

        builder.show();


    }

    private void showDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setItems(items2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 自动生成的方法存根
                switch (arg1) {
                    case 0:

                        String imgeUrl = "https://lh3.googleusercontent.com/DUNUHu9uMiuK801knBB669e2ybOxLUlKjInYZYs-revObFNb82PUv143RS3SWzfsO5M=w300-rw";
                        String description = "The best virtual and perfect instrument app for piano hobbyist.";
                        String title = "Piano";

                        ShareLinkContent mLike = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse(Constants.SHRELINK))
                                .setContentDescription(description)
                                .setImageUrl(Uri.parse(imgeUrl))
                                .setContentTitle(title)
                                .build();
                        shareDialog.show(mLike, ShareDialog.Mode.WEB);
                        break;

                }
                arg0.dismiss();
            }
        });

        builder.show();


    }


    private void showDialog3(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setItems(items3, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 自动生成的方法存根
                switch (arg1) {
                    case 0:

                        final SaveFileDialog dlg = new SaveFileDialog(context);
                        dlg.setContentView(R.layout.save_file_dialog);
                        final EditText edFileName = (EditText)dlg.findViewById(R.id.etFileName);
                        Button btnSave = (Button)dlg.findViewById(R.id.btnSave);
                        Button btnCancel = (Button)dlg.findViewById(R.id.btnCancel);
                        TextView tvFileTips = (TextView) dlg.findViewById(R.id.tv_file_tips);
                        tvFileTips.setText("Rename");
                        edFileName.setText(getFileNameNoEx(file.getName()));
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(edFileName.getText().toString()!=null&&!"".equals(edFileName.getText().toString())) {
                                    if (file.getName().endsWith(".midi")) {
                                        File newfile = new File(RecordManager.getRecordDirectory(context), edFileName.getText().toString() + ".midi");
                                        file.renameTo(newfile);
                                    } else if (file.getName().endsWith(".dat")) {
                                        File newfile = new File(RecordManager.getRecordDirectory(context), edFileName.getText().toString() + ".dat");
                                        file.renameTo(newfile);
                                    }


                                    fileList = new File(RecordManager.getRecordDirectory(context)).listFiles(RecordManager.recordFileFilter);
                                    Arrays.sort(fileList, new Comparator<File>() {
                                        @Override
                                        public int compare(File lhs, File rhs) {
                                            if (lhs == null || lhs.getPath() == null)
                                                return 1;
                                            if (rhs == null || rhs.getPath() == null)
                                                return -1;

                                            int ret = lhs.getPath().compareTo(rhs.getPath());
                                            if (ret < 0)
                                                return 1;
                                            else if (ret > 0)
                                                return -1;
                                            return 0;
                                        }
                                    });
                                    fileListViewAdapter.setItemList(fileList);
                                    fileListViewAdapter.notifyDataSetChanged();
                                    dlg.dismiss();
                                }else{
                                    Toast.makeText(FileSelectActivity.this,"File Name Can Not Be Empty", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlg.dismiss();
                            }
                        });

                        dlg.show();

                        break;
                    case 1:

                        final SaveFileDialog dlgDelete = new SaveFileDialog(context);
                        dlgDelete.setContentView(R.layout.save_file_dialog);
                        final EditText edFileNameDelete = (EditText)dlgDelete.findViewById(R.id.etFileName);
                        Button btnSaveDelete = (Button)dlgDelete.findViewById(R.id.btnSave);
                        btnSaveDelete.setText("Confirm");
                        Button btnCancelDelete = (Button)dlgDelete.findViewById(R.id.btnCancel);
                        TextView tvFileTipsDelete = (TextView) dlgDelete.findViewById(R.id.tv_file_tips);
                        tvFileTipsDelete.setText("Confirm to delete this file?");
                        edFileNameDelete.setVisibility(View.GONE);
                        btnSaveDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (file.exists()) {
                                    if (file.isFile()) {
                                        file.delete();
                                    }
                                }
                                fileList = new File(RecordManager.getRecordDirectory(context)).listFiles(RecordManager.recordFileFilter);
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
                                fileListViewAdapter.setItemList(fileList);
                                fileListViewAdapter.notifyDataSetChanged();
                                dlgDelete.dismiss();

                            }
                        });

                        btnCancelDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dlgDelete.dismiss();
                            }
                        });

                        dlgDelete.show();


                        break;
                }
                arg0.dismiss();
            }
        });

        builder.show();


    }


    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    private void doSetVoice(final String path2,final int id){

            MediaScannerConnection.scanFile(this, new String[] {path2}, null, null);

                  new Handler().postDelayed(new Runnable(){
                      public void run() {
                     setVoice(path2,id);
                      }
                   }, 100);


    }

    private void setVoice(String path2,int id)

                 {


                ContentValues cv = new ContentValues();

                Uri newUri = null;

                Uri uri = MediaStore.Audio.Media.getContentUriForPath(path2);

                // 查询音乐文件在媒体库是否存在

                Cursor cursor = this.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[] { path2 },null);

                if (cursor.moveToFirst() && cursor.getCount() > 0)

                {

                        String _id = cursor.getString(0);

                        switch (id) {

                               case Constants.RINGTONE:

                                       cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);

                                       cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);

                                       cv.put(MediaStore.Audio.Media.IS_ALARM, false);

                                       cv.put(MediaStore.Audio.Media.IS_MUSIC, false);

                                       break;

                               case Constants.NOTIFICATION:

                                       cv.put(MediaStore.Audio.Media.IS_RINGTONE, false);

                                       cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);

                                       cv.put(MediaStore.Audio.Media.IS_ALARM, false);

                                       cv.put(MediaStore.Audio.Media.IS_MUSIC, false);

                                       break;

                               case Constants.ALARM:

                                       cv.put(MediaStore.Audio.Media.IS_RINGTONE, false);

                                       cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);

                                       cv.put(MediaStore.Audio.Media.IS_ALARM, true);

                                       cv.put(MediaStore.Audio.Media.IS_MUSIC, false);

                                       break;

                               case Constants.ALL:

                                       cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);

                                       cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);

                                       cv.put(MediaStore.Audio.Media.IS_ALARM, true);

                                       cv.put(MediaStore.Audio.Media.IS_MUSIC, false);

                                       break;



                               default:

                                       break;

                            }



                        // 把需要设为铃声的歌曲更新铃声库

                        getContentResolver().update(uri, cv, MediaStore.MediaColumns.DATA + "=?",new String[] { path2 });

                        newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));



                      // 设置：

                        switch (id) {

                               case Constants.RINGTONE:

                                       RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);

                                       break;

                               case Constants.NOTIFICATION:

                                       RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION, newUri);

                                       break;

                               case Constants.ALARM:

                                      RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM, newUri);

                                       break;

                               case Constants.ALL:

                                      RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALL, newUri);

                                       break;



                               default:

                                       break;

                            }



                        //播放铃声
                     // Ringtone rt = RingtoneManager.getRingtone(this, newUri);
                     // rt.play();
                    Toast.makeText(FileSelectActivity.this, R.string.set_success_toast, Toast.LENGTH_SHORT).show();
                    }else{
                    Toast.makeText(FileSelectActivity.this, R.string.set_fail_toast, Toast.LENGTH_SHORT).show();
                }

             }

}
