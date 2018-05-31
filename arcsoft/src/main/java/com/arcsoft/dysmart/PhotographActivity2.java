package com.arcsoft.dysmart;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.arcsoft.dysmart.FaceConstant.FACE_TAG;
import static com.arcsoft.dysmart.FaceConstant.PIC_PREFIX;

public class PhotographActivity2 extends AppCompatActivity implements Camera.PictureCallback, View.OnClickListener, CameraSurfaceView.OnCameraListener {

    private static final String TAG = PhotographActivity.class.getSimpleName();

    private ImageView mCaptureButton;
//    private ImageView mDeleteButton;

    private File pictureFile;
    private HandlerThread handlerThread;
    private Handler handler;

    private CameraSurfaceView mSurfaceView;
    private CameraGLSurfaceView mGLSurfaceView;
    private Camera mCamera;
    private AFT_FSDKVersion version = new AFT_FSDKVersion();
    private AFT_FSDKEngine engine = new AFT_FSDKEngine();
    private List<AFT_FSDKFace> result = new ArrayList<>();
    private int mWidth;
    private int mHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photograph2);

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        mWidth = dm.widthPixels/4;
        mHeight = dm.heightPixels/4;
        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(this);
        //mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, true, 0);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, true, 0);
        mSurfaceView.debug_print_fps(true, false);

        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

        // Add a listener to the Capture button
        mCaptureButton = (ImageView) findViewById(R.id.capture);
//        mDeleteButton = (ImageView) findViewById(R.id.delete);
        mCaptureButton.setOnClickListener(this);
//        mDeleteButton.setOnClickListener(this);

        //创建一个线程,线程名字：pictureHandlerThread
        handlerThread = new HandlerThread("pictureHandlerThread");
        //开启一个线程
        handlerThread.start();

        //在这个线程中创建一个handler对象
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if (savePicture((byte[]) msg.obj)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PhotographActivity2.this, "拍照成功", Toast.LENGTH_LONG).show();
                                    finishActivity();
                                }
                            });
                        }
                        break;
//                    case 1:
//                        deletePicture();
//                        break;
                }
            }
        };
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //save the picture to sdcard
        pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }

        Message message = new Message();
        message.what = 0;
        message.obj = data;
        handler.sendMessageDelayed(message, 100);

        // Restart the preview and re-enable the shutter button so that we can take another picture
        camera.startPreview();

//        //See if need to enable or not
//        mCaptureButton.setEnabled(true);
//        mCaptureButton.setVisibility(View.GONE);
//        mDeleteButton.setVisibility(View.VISIBLE);
        Log.v(FACE_TAG, "onPictureTaken3-->" + "拍照成功");
    }

    @Override
    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.capture) {
//            mCaptureButton.setEnabled(false);
//            // get an image from the camera
//            mCameraSurPreview.takePicture(this);
//        } else if (i == R.id.delete) {
//            handler.sendEmptyMessageDelayed(1, 100);
//        }
    }

    private File getOutputMediaFile() {
        //get the mobile Pictures directory
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //get the current time
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String path = picDir.getPath() + File.separator + PIC_PREFIX + timeStamp + ".jpg";
        ///storage/sdcard/Pictures/arcsoft_20180315154351.jpg
        Log.v(FACE_TAG, "getOutputMediaFile-->" + path);
        return new File(path);
    }

    private boolean savePicture(byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Log.v(FACE_TAG, "savePicture1-->" + pictureFile);
            return true;
        } catch (FileNotFoundException e) {
            Log.v(FACE_TAG, "savePicture2-->" + e.getMessage());
        } catch (IOException e) {
            Log.v(FACE_TAG, "savePicture3-->" + e.getMessage());
        }
        return false;
    }

//    private void deletePicture() {
//        if (pictureFile != null && !pictureFile.exists()) {
//            Toast.makeText(this, "图片不存在", Toast.LENGTH_LONG).show();
//            Log.v(FACE_TAG, "deletePicture1-->");
//        }
//        if (pictureFile.delete()) {
//            Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
//            mDeleteButton.post(new Runnable() {
//                @Override
//                public void run() {
//                    mDeleteButton.setVisibility(View.GONE);
//                    mCaptureButton.setVisibility(View.VISIBLE);
//                }
//            });
//        } else {
//            Toast.makeText(this, "删除失败", Toast.LENGTH_LONG).show();
//        }
//        Log.v(FACE_TAG, "deletePicture2-->");
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());

        result.clear();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mCaptureButton.getVisibility() == View.VISIBLE) {
                mCaptureButton.setEnabled(false);
                // get an image from the camera
                if (mCamera != null) {
                    mCamera.takePicture(null, null, this);
                }
            }
//            else if (mDeleteButton.getVisibility() == View.VISIBLE) {
//                handler.sendEmptyMessageDelayed(1, 100);
//            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            finishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finishActivity() {
        if (pictureFile != null) {
            Intent intent = new Intent(this, FaceRegisterActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("imagePath", pictureFile.getPath());
            intent.putExtras(bundle);
            startActivity(intent);
        }
        this.finish();
    }

    @Override
    public Camera setupCamera() {
        // TODO Auto-generated method stub
        mCamera = Camera.open();
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(1280, 720);
//            Camera.Size size = getBestSize(800,600,parameters.getSupportedPreviewSizes());
//            parameters.setPreviewSize(size.width, size.height);
            parameters.setPreviewFormat(ImageFormat.NV21);

            List<int[]> fps = parameters.getSupportedPreviewFpsRange();
            for (int[] count : fps) {
                Log.d(TAG, "T:");
                for (int data : count) {
                    Log.d(TAG, "V=" + data);
                }
            }
            //parameters.setPreviewFpsRange(15000, 30000);
            //parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
            //parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            //parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
            //parmeters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            //parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
            mCamera.setParameters(parameters);

            mCamera.autoFocus(null);

            int width = mCamera.getParameters().getPreviewSize().width;
            int height = mCamera.getParameters().getPreviewSize().height;
            Log.v(FACE_TAG, "setupCamera-->SIZE:" + width + "x" + height);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(FACE_TAG, "setupCamera-->" + e.getMessage());
        }

        return mCamera;
    }

    @Override
    public void setupChanged(int format, int width, int height) {
        Log.v(FACE_TAG, "setupChanged-->" + width + "/" + height);
    }

    @Override
    public boolean startPreviewLater() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
        Log.d(TAG, "Face=" + result.size());
        for (AFT_FSDKFace face : result) {
            Log.d(TAG, "Face:" + face.toString());
        }
        //copy rects
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        //clear result.
        result.clear();
        //return the rects for render.
        return rects;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.GREEN, 2);
    }

    private Camera.Size getBestSize(int width,int height,List<Camera.Size> list){
        Camera.Size size = null;
        double targetRatio = width*1.0/height*1.0;
        double minDiff = targetRatio;
        for(Camera.Size cSize : list){
            if(cSize.width == width && cSize.height == height){
                size = cSize;
                break;
            }
            double ratio = (cSize.width*1.0)/cSize.height;
            if(Math.abs(ratio - targetRatio) < minDiff){
                minDiff = Math.abs(ratio - targetRatio);
                size = cSize;
            }
        }
        return size;
    }
}