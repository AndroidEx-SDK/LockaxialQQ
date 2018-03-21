package com.arcsoft.dysmart;

import android.app.Application;
import android.util.Log;

import static com.arcsoft.dysmart.FaceConstant.FACE_TAG;

/**
 * Created by Administrator on 2018/3/10.
 */

public class ArcsoftManager {

    // Singleton instance
    private static final ArcsoftManager INSTANCE = new ArcsoftManager();

    /**
     * get singleton instance of CallManager
     *
     * @return CallManager
     */
    public static ArcsoftManager getInstance() {
        return INSTANCE;
    }

    private ArcsoftManager() {
    }

    public FaceDB mFaceDB;
//    public Uri mImage;

    public void initArcsoft(Application application) {
        String path = application.getExternalCacheDir().getPath();
        Log.v(FACE_TAG, "initArcsoft-->" + path);
        mFaceDB = new FaceDB(path);
//        mImage = null;

//        SharedPreferencesUtil.getInstance(application, "face_detect");
    }

//    public void setCaptureImage(Uri uri) {
//        mImage = uri;
//    }
//
//    public Uri getCaptureImage() {
//        return mImage;
//    }
//
//    public void saveFaceModelData(String name, AFR_FSDKFace face) {
//        Map<String, AFR_FSDKFace> faceMap = (Map<String, AFR_FSDKFace>) SharedPreferencesUtil.getHashMapData("face_map", AFR_FSDKFace.class);
//        if (faceMap == null) {
//            faceMap = new HashMap<>();
//        }
//        faceMap.put(name, face);
//        SharedPreferencesUtil.putHashMapData("face_map", faceMap);
//    }
//
//    public AFR_FSDKFace getFaceData(String name) {
//        AFR_FSDKFace face = null;
//        Map<String, AFR_FSDKFace> faceMap = (Map<String, AFR_FSDKFace>) SharedPreferencesUtil.getHashMapData("face_map", AFR_FSDKFace.class);
//        if (faceMap == null) {
//            faceMap = new HashMap<>();
//        }
//        if (faceMap.containsKey(name)) {
//            face = faceMap.get(name);
//        }
//        return face;
//    }
}
