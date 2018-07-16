package com.androidex.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2018/7/16.
 */

public class SystemUtils {

    public static PackageInfo isApplicationAvilible(Context context, String appPackageName) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return pinfo.get(i);
                }
            }
        }
        return null;
    }

    public static PackageInfo getApkInfo(String absPath,Context context) {
        return context.getPackageManager().getPackageArchiveInfo(absPath,PackageManager.GET_ACTIVITIES);
    }
}
