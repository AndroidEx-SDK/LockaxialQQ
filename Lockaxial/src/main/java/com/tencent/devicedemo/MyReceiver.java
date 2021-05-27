package com.tencent.devicedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.json.JSONObject;
import java.util.Iterator;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JIGUANG-Example";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			Logger.d(TAG, "[MyReceiver] 1onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Logger.d(TAG, "[MyReceiver] 2接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 3接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 4接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Logger.d(TAG, "[MyReceiver] 5接收到推送下来的通知的ID: " + notifactionId);


			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 6用户点击打开了通知");

				//打开自定义的Activity
				Intent i = new Intent(context, TestActivity.class);
				i.putExtras(bundle);
				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity(i);

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Logger.w(TAG, "[MyReceiver7]" + intent.getAction() +" connected state change to "+connected);
			} else {
				Logger.d(TAG, "[MyReceiver8] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){

		}

	}
	//send message to mainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		//取出自定义消息内容（自定义消息手机上不显示通知）
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Logger.e(TAG,"--message:"+message+"\nextras:"+extras);
		if (MainActivity.isForeground) {
			Intent intent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			intent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!ExampleUtil.isEmpty(extras)) {
				try {
					JSONObject jsonObject = new JSONObject(extras);
					if (jsonObject.length() > 0) {
						intent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		}
	}

	//打印所有intent extras 数据
	private String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				Logger.e(TAG, "----收到通知");
				sb.append("\nkey:" + key + ",value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				Logger.e(TAG, "----网络发生变化");
				sb.append("\nkey:" + key + ",value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				Logger.e(TAG, "----This message has no Extra data");
				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it = json.keys();
					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ",value:[" + myKey + "-" + json.optString(myKey) + "]");
					}
				} catch (Exception e) {
					Logger.e(TAG, "Get message extra JSON error!");
				}
			} else {
				sb.append("\nkey:" + key + ",value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
}
