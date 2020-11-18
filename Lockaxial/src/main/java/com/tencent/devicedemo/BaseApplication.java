package com.tencent.devicedemo;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;

import com.androidex.utils.HttpApi;
import com.arcsoft.dysmart.ArcsoftManager;
import com.lidroid.xutils.HttpUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by xinshuhao on 16/7/24.
 */
public class BaseApplication extends Application implements Thread.UncaughtExceptionHandler {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private static BaseApplication application;
    private HttpUtils httpUtils;
    public  static String path;
    @Override
    public void onCreate() {
        // setStrictMode();

        application = this;

        Thread.setDefaultUncaughtExceptionHandler(this);

        initHttp();

        initImageLoader();

        initJPush();

        path = ArcsoftManager.getInstance().initArcsoft(this);

        super.onCreate();
    }

    public static BaseApplication getApplication() {
        return application;
    }


    public void initHttp() {
        httpUtils = new HttpUtils("utf-8");
        httpUtils.configRequestThreadPoolSize(5);
        httpUtils.configSoTimeout(30000);
        httpUtils.configResponseTextCharset("utf-8");
        httpUtils.configRequestRetryCount(3);
    }
    //初始化极光
    public  void initJPush(){
       try{
           JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
           JPushInterface.init(this);     		// 初始化 JPush
           System.out.println("极光注册成功");
       }catch (Exception e){
           Logger.e("极光注册失败！",e.getMessage());
       }

    }


    private void initImageLoader() {
        imageLoader = ImageLoader.getInstance();

        String cachePath = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cachePath =
                    Environment.getExternalStorageDirectory().getPath()
                            + "/qqlock/cache";
        } else {
            cachePath = getCacheDir().getPath() + "/qqlock/cache";
        }

        File cacheFile = new File(cachePath);

        cacheFile.mkdirs();

        int memoryCacheSize = (int) Runtime.getRuntime().maxMemory() / 8;

        ImageLoaderConfiguration configuration =
                new ImageLoaderConfiguration.Builder(getApplicationContext())
                        .threadPoolSize(5)
                        .memoryCacheSize(memoryCacheSize)
                        .diskCacheFileCount(500)
                        .diskCache(new UnlimitedDiskCache(cacheFile))
                        .build();

        imageLoader.init(configuration);

        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.mipmap.bg1)
                .showImageOnLoading(R.mipmap.bg1)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();


    }

    public HttpUtils getHttpUtils() {
        return this.httpUtils;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public DisplayImageOptions getDisplayOptions() {
        return options;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setStrictMode() {
        if (Integer.valueOf(Build.VERSION.SDK) > 3) {
            // Log.d(LOG_TAG, "Enabling StrictMode policy over Sample application");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectNetwork().penaltyLog()
                    // .penaltyDeath()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                    .penaltyLog().build());

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                    .penaltyLog().penaltyDeath().build());
        }
    }


    // 捕获系统运行停止错误
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // System.exit(0);
        HttpApi.i("捕获到异常，重启程序");
        ex.printStackTrace();

        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());

        PendingIntent restartIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 退出程序
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, restartIntent); // 1秒钟后重启应用

        System.exit(0);

    }
}
