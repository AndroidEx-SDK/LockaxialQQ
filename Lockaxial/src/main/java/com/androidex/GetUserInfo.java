package com.androidex;

import android.content.Context;

import com.androidex.aexapplibs.appLibsService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cts on 17/2/15.
 * 得到userinfo里面的信息
 */

public class GetUserInfo {
    public static JSONObject get_authinfo(Context context)
    {
        appLibsService aexparams = new appLibsService(context);
        JSONObject userinfo = new JSONObject();
        try {
            int flag0 = aexparams.get_flag0();
            if(flag0 != 0 && flag0 != 0xFF) {
                String ui = aexparams.getUserInfo();
                if (ui != null)
                    userinfo = new JSONObject(ui);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userinfo;
    }
    /**
     * 得到pid
     */
    public static String getPid(Context context){
     return get_authinfo(context).optString("pid");
    }

    /**
     * 得到 sn
     */
    public static String getSn(Context context){
        return get_authinfo(context).optString("sn");
    }
    /**
     * 得到liense
     */
    public static String getLicense(Context context){
        return get_authinfo(context).optString("license");
    }
    /**
     * 得到pubkey
     */
    public static String getPubkey(Context context){
        return  get_authinfo(context).optString("pubkey");
    }
}
