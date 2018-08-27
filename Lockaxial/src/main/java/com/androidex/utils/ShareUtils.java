package com.androidex.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2018/8/24.
 */

public class ShareUtils {

    private static ShareUtils shareUtils;
    private static SharedPreferences sharedPreferences;
    private ShareUtils(Context context){
        sharedPreferences = context.getSharedPreferences("com.xiao.share_utils",Context.MODE_PRIVATE);
    }

    public static ShareUtils getInstance(Context context){
         if(shareUtils == null){
             shareUtils = new ShareUtils(context);
         }
         return shareUtils;
    }

    public void saveBooleanVal(String key,boolean val){
        sharedPreferences.edit().putBoolean(key,val).commit();
    }
    public boolean getBooleanVal(String key){
        return sharedPreferences.getBoolean(key,true);
    }
}
