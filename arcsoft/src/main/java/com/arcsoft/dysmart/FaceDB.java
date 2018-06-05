package com.arcsoft.dysmart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.dysmart.FaceConstant.FACE_TAG;

/**
 * Created by gqj3375 on 2017/7/11.
 */

public class FaceDB {
    private final String TAG = this.getClass().toString();

    public static String appid = "F8FoZsZXFJTrbG1xtRY8ghqYi8UFqpAq9F7Nbo2v6jK8";//APPID
    public static String ft_key = "79kKgHjTgz27bkMMkKA8G6TjzSKnRN46cS7ad5JnQNdc";//人脸追踪(FT) Key :
    public static String fd_key = "79kKgHjTgz27bkMMkKA8G6Ts9qaxt7NsLEmKwBBH84TF";//人脸检测(FD) Key :
    public static String fr_key = "79kKgHjTgz27bkMMkKA8G6TzKEr6TYfhDa6UAuqxKK5r";//人脸识别(FR) Key :
    public static String age_key = "79kKgHjTgz27bkMMkKA8G6Uc8F9xjy9bcFCL3o9w8k6m";//年龄识别(Age) Key :
    public static String gender_key = "79kKgHjTgz27bkMMkKA8G6UjHeRAg5TZNJ4u8sPEKj9m";//性别识别(Gender) Key :

    String mDBPath;
    public List<FaceRegist> mRegister;
    AFR_FSDKEngine mFREngine;
    AFR_FSDKVersion mFRVersion;
    boolean mUpgrade;

    public class FaceRegist {
        public String mName;
        public List<AFR_FSDKFace> mFaceList, mIDFaceList;

        public FaceRegist(String name) {
            mName = name;
            mFaceList = new ArrayList<>();
            mIDFaceList = new ArrayList<>();
        }
    }

    public FaceDB(String path) {
        mDBPath = path;
        if(mDBPath!=null){
            File fm = new File(mDBPath);
            if(!fm.exists()){
                fm.mkdirs();
            }
            File ff = new File(mDBPath+"/face.txt");
            if(!ff.exists()){
                try {
                    ff.createNewFile();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        mRegister = new ArrayList<>();
        mFRVersion = new AFR_FSDKVersion();
        mUpgrade = false;
        mFREngine = new AFR_FSDKEngine();
        AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
        if (error.getCode() != AFR_FSDKError.MOK) {
        } else {
            mFREngine.AFR_FSDK_GetVersion(mFRVersion);
        }
    }

    public void destroy() {
        if (mFREngine != null) {
            mFREngine.AFR_FSDK_UninitialEngine();
        }
    }

    private boolean saveInfo() {
        try {
            FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt");
            ExtOutputStream bos = new ExtOutputStream(fs);
            bos.writeString(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel());
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean loadInfo() {
        if (!mRegister.isEmpty()) {
            return false;
        }
        try {
            FileInputStream fs = new FileInputStream(mDBPath + "/face.txt");
            ExtInputStream bos = new ExtInputStream(fs);
            //load version
            String version_saved = bos.readString();
            if(version_saved == null || version_saved.length()<=0){
                return false;
            }
            if (version_saved.equals(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel())) {
                mUpgrade = true;
            }
            //load all regist name.
            if (version_saved != null) {
                for (String name = bos.readString(); name != null; name = bos.readString()) {
                    if (new File(mDBPath + "/" + name + ".data").exists()) {
                        mRegister.add(new FaceRegist(new String(name)));
                    }
                }
            }
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveBitmap(String name, Bitmap bitmap) {
        boolean bool = false;
        File file = new File(mDBPath + "/" + name + ".png");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            Bitmap bmp = BitmapUtils.zoomImg(bitmap, 80, 80);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            bool = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bool = false;
        } catch (IOException e) {
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }

    public Bitmap getFaceBitmap(String name) {
        Bitmap bitmap = null;
        try {
            File file = new File(mDBPath + "/" + name + ".png");
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getPath());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public boolean loadFaces() {
        if (loadInfo()) {
            try {
                for (FaceRegist face : mRegister) {
                    FileInputStream fs = new FileInputStream(mDBPath + "/" + face.mName + ".data");
                    ExtInputStream bos = new ExtInputStream(fs);
                    AFR_FSDKFace afr = null;
                    do {
                        if (afr != null) {
                            if (mUpgrade) {
                                //upgrade data.
                            }
                            if (face.mName.length() < 32) { //身份证
                                face.mIDFaceList.add(afr);
                            } else { //手机录取人脸
                                face.mFaceList.add(afr);
                            }
                        }
                        afr = new AFR_FSDKFace();
                    } while (bos.readBytes(afr.getFeatureData()));
                    bos.close();
                    fs.close();
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean addFace(String name){
        boolean isadd = false;
        try{
            if(saveInfo()){
                FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                ExtOutputStream bos = new ExtOutputStream(fs);
                for (FaceRegist frface : mRegister) {
                    bos.writeString(frface.mName);
                }
                bos.writeString(name);
                bos.close();
                fs.close();
            }
            mRegister.clear();
            loadFaces();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean addFace(String name, AFR_FSDKFace face) {
        boolean bool = false;
        try {
            //check if already registered.
            boolean add = true;
            for (FaceRegist frface : mRegister) { //循环
                if (frface.mName.equals(name)) {
                    if (name.length() > 11) {
                        frface.mIDFaceList.add(face);
                    } else {
                        frface.mFaceList.add(face);
                    }
                    add = false;
                    break;
                }
            }
            if (add) { // not registered.
                FaceRegist frface = new FaceRegist(name);
                if (name.length() > 11) {
                    frface.mIDFaceList.add(face);
                } else {
                    frface.mFaceList.add(face);
                }
                mRegister.add(frface);
            }
            bool = saveInfo();
            if (bool) {
                //update all names
                FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                ExtOutputStream bos = new ExtOutputStream(fs);
                for (FaceRegist frface : mRegister) {
                    bos.writeString(frface.mName);
                }
                bos.close();
                fs.close();

                //save new feature
                fs = new FileOutputStream(mDBPath + "/" + name + ".data", true);
                bos = new ExtOutputStream(fs);
                bos.writeBytes(face.getFeatureData());
                bos.close();
                fs.close();

                bool = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bool = false;
        } catch (IOException e) {
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }

    public boolean delete(String name) {
        try {
            //check if already registered.
            boolean find = false;
            for (FaceRegist frface : mRegister) {
                if (frface.mName.equals(name)) {
                    File delfile = new File(mDBPath + "/" + name + ".data");
                    if (delfile.exists()) {
                        delfile.delete();
                    }
                    mRegister.remove(frface);
                    find = true;
                    break;
                }
            }

            if (find) {
                if (saveInfo()) {
                    //update all names
                    FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                    ExtOutputStream bos = new ExtOutputStream(fs);
                    for (FaceRegist frface : mRegister) {
                        bos.writeString(frface.mName);
                    }
                    bos.close();
                    fs.close();
                }
            }
            return find;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean upgrade() {
        return false;
    }
}
