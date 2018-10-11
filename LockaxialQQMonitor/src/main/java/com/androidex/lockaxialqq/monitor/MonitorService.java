package com.androidex.lockaxialqq.monitor;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Administrator on 2018/7/16.
 */

public class MonitorService extends Service {
    private String TAG = "";
    private Timer activityTimer;
    private ActivityManager activityManager;
    private static final String PACKAGE_NAME = "com.tencent.devicedemo";
    private boolean isPullTime = false;
    private Context mContext;
    private Handler mHandler = new Handler();

    private Runnable startMain = new Runnable() {
        @Override
        public void run() {
            try {
                Intent intent = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(PACKAGE_NAME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MonitorService.this.startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SimpleDateFormat myFmt=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        Log.i("xiao_","监控服务初始化"+myFmt.format(new Date()));
        TAG = UUID.randomUUID().toString();
        mContext = this;
        initCheckTopActivity();
    }


    private void initCheckTopActivity(){
        if (activityTimer != null) {
            activityTimer.cancel();
            activityTimer = null;
        }
        activityTimer = new Timer();
        activityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(activityManager == null){
                    activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                }
                ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
                if (!cn.getPackageName().equals(PACKAGE_NAME)) {
                    showMsg("未打开锁相门禁");
                    if (!isPullTime) {
                        showMsg("倒计时进入锁相门禁：15s");
                        mHandler.postDelayed(startMain, 15 * 1000);
                        isPullTime = true;
                    }
                } else {
                    showMsg("已经打开锁相门禁");
                    mHandler.removeCallbacks(startMain);
                    isPullTime = false;
                }
            }
        },0,1*1000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activityTimer != null) {
            activityTimer.cancel();
            activityTimer = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    private void showMsg(String msg){
        Log.i("Monitor_"+TAG,msg);
    }
}
