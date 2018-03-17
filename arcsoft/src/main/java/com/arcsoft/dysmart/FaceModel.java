package com.arcsoft.dysmart;

import android.graphics.Bitmap;

import com.arcsoft.facerecognition.AFR_FSDKFace;

/**
 * Created by Administrator on 2018/3/12.
 */

public class FaceModel {

    public String name;
    public AFR_FSDKFace face;

    @Override
    public String toString() {
        return "FaceModel{" +
                "name='" + name + '\'' +
                ", face=" + face +
                '}';
    }
}
