package com.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.devicedemo.R;

/**
 * @author liyp
 * @editTime 2017/11/22
 */

public class DialogUtil {

    public static android.app.Dialog showBottomDialog(final Context context) {
        final android.app.Dialog dialog = new android.app.Dialog(context, R.style.DialogStyle);
        dialog.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_weituo, null);
        dialog.setContentView(view);

        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
//        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
//            lp.height = getScreenHeight(context) * 8 / 10;
//        } else {
//            lp.width = getScreenWidth(context);
//        }
        mWindow.setGravity(Gravity.CENTER);
        //mWindow.setWindowAnimations(R.style.dialogAnim);
        mWindow.setAttributes(lp);
        dialog.show();
        return dialog;
    }

}
