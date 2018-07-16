package eu.chainfire.supersu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.androidex.config.DeviceConfig;
import com.tencent.devicedemo.BaseApplication;
import com.tencent.devicedemo.MainActivity;

/**
 * Created by Administrator on 2018/3/21.
 */

public class NativeAccessReceiver extends BroadcastReceiver{

    private static final String Lockaxial_PackageName = "com.tencent.devicedemo";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_PACKAGE_REPLACED)
                || action.equals(Intent.ACTION_PACKAGE_ADDED)){
            String packageName = intent.getData().getSchemeSpecificPart();
            if(packageName.equals(Lockaxial_PackageName)){
                //程序更新完成后，启动app
                startActivity(context, MainActivity.class,null);
            }else if(packageName.equals(DeviceConfig.Lockaxial_Monitor_PackageName)){
                //启动监控程序
                Intent i = new Intent();
                ComponentName cn = new ComponentName(DeviceConfig.Lockaxial_Monitor_PackageName,DeviceConfig.Lockaxial_Monitor_SERVICE);
                i.setComponent(cn);
                i.setPackage(BaseApplication.getApplication().getPackageName());
                context.startService(i);
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
