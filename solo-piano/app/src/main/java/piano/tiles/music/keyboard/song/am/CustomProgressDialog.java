
package piano.tiles.music.keyboard.song.am;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;





public class CustomProgressDialog extends Dialog {
    private Context context = null;
    private static CustomProgressDialog customProgressDialog = null;
    private PianoActivity mParentActivity;
    public CustomProgressDialog(Context context) {
        super(context);
        this.mParentActivity = (PianoActivity) context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static CustomProgressDialog createDialog(Context context) {
        customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
        customProgressDialog.setContentView(R.layout.progress_dialog);
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return customProgressDialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        if (customProgressDialog == null) {
            return;
        }

//        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        TextView textView = (TextView) customProgressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) textView.getBackground();
        animationDrawable.start();
    }

    /**
     * [Summary]
     * setTitile 标题
     *
     * @param strTitle
     * @return
     */
    public CustomProgressDialog setTitile(String strTitle) {
        return customProgressDialog;
    }

    /**
     * [Summary]
     * setMessage 提示内容
     *
     * @param strMessage
     * @return
     */
    public CustomProgressDialog setMessage(String strMessage) {
        TextView tvMsg = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);

        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }

        return customProgressDialog;
    }

    public CustomProgressDialog setText(String strText) {
        TextView tvText = (TextView) customProgressDialog.findViewById(R.id.loadingImageView);
        if (tvText != null) {
            tvText.setText(strText);
        }
        return customProgressDialog;
    }



}
