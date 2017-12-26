package com.androidex.utils;

import android.util.Log;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jni.http.HttpManager;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by cts on 17/12/25.
 */

public class HttpApi {

    private static final String TAG = "xiao_";
    private static boolean DEBUG = true;

    public static void setDEBUG(boolean DEBUG) {
        HttpApi.DEBUG = DEBUG;
    }

    public static void i(String m){
        if(DEBUG){
            Log.i(TAG,m);
        }
    }
    public static void i(String t,String m){
        if(DEBUG){
            Log.i(t,m);
        }
    }
    public static void e(String m){
        if(DEBUG){
            Log.e(TAG,m);
        }
    }
    public static void e(String t,String m){
        if(DEBUG){
            Log.e(t,m);
        }
    }


    private static HttpApi api;
    private static OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static HttpApi getInstance(){
        if(api == null){
            api = new HttpApi();
            client = new OkHttpClient();
        }
        return api;
    }

    public String loadHttpforPost(String u, JSONObject j,String t) throws Exception{
        try {
            RequestBody body = RequestBody.create(JSON, j.toString());
            Call call = client.newCall(BuildRequest(body, u, t));
            return call.execute().body().string();
        }catch(Exception e){
            return null;
        }
    }

    public String loadHttpforGet(String u,String t){
        try {
            Call call = client.newCall(BuildRequest(null, u, t));
            return call.execute().body().string();
        }catch(Exception e){
            return null;
        }
    }

    public Calendar loadTime(){
        try {
            URL url = new URL("http://www.baidu.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            long ld=conn.getDate();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(ld);
            return c;
        }catch(Exception e){
            return null;
        }
    }

    private Request BuildRequest(RequestBody body, String url, String token){
        Request.Builder builder= new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
        if(token!=null && token.length()>0){
            builder.header("Authorization","Bearer " + token);
        }
        if(body!=null){
            builder.post(body);
        }
        return builder.build();
    }


}
