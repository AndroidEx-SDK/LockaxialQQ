package com.tencent.devicedemo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;
import com.tencent.devicedemo.FaceMessage;
import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
public class PushMessageReceiver extends JPushMessageReceiver {
    private static final String TAG = "PushMessageReceiver";
    //private FaceMessage onMessage;
    Intent intent = new Intent();
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        Log.e(TAG,"[onMessage] "+customMessage);
        intent.setAction("com.androidex.service");
        intent.putExtra("message",customMessage.message);
        context.sendBroadcast(intent);
        //context.sendBroadcastAsUser(intent, UserHandle);
        //FaceMessage(customMessage.message);
    }

}
