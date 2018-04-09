package eu.chainfire.supersu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.devicedemo.MainActivity;

/**
 * Created by Administrator on 2018/3/21.
 */

public class NativeAccessReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_PACKAGE_REPLACED)
                || action.equals(Intent.ACTION_PACKAGE_ADDED)){
            String packageName = intent.getData().getSchemeSpecificPart();
            if(packageName.equals("com.tencent.devicedemo")){
                startActivity(context, MainActivity.class,null);
            }
        }else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            startActivity(context, MainActivity.class,null);
        }
    }

    public void startActivity(Context context,Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
}
