package com.arcsoft.dysmart;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Method;
import java.util.List;

import static com.arcsoft.dysmart.FaceConstant.FACE_TAG;

/**
 * Created by hejunlin on 2016/10/5.
 */
public class SurfacePreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = SurfacePreview.class.getSimpleName();
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    public SurfacePreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(FACE_TAG, "surfaceCreated() is called");
//        try {
//            // Open the Camera in preview mode
////            mCamera = Camera.open(0);
//            mCamera = Camera.open();
////            mCamera.setDisplayOrientation(90);
//            mCamera.setPreviewDisplay(holder);
//            mCamera.startPreview();
//        } catch (IOException e) {
//            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
//        }

        mCamera = Camera.open();
        try {
            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewSize(1280, 720);
            parameters.setPreviewFormat(ImageFormat.NV21);

            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                Log.v(FACE_TAG, "SIZE:" + size.width + "x" + size.height);
            }
            for (Integer format : parameters.getSupportedPreviewFormats()) {
                Log.v(FACE_TAG, "FORMAT:" + format);
            }

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
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(parameters);

            if (mCamera != null) {
                int mWidth = mCamera.getParameters().getPreviewSize().width;
                int mHeight = mCamera.getParameters().getPreviewSize().height;
//                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
//                mCamera.autoFocus(null);
                mCamera.cancelAutoFocus();
                Log.v(FACE_TAG, "SIZE:" + mWidth + "x" + mHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(FACE_TAG, "surfaceCreated528-->" + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d(TAG, "surfaceChanged() is called");
        try {
            mCamera.startPreview();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
//                        initCamera();
                        camera.cancelAutoFocus();
                    }
                }
            });
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumDetectedFaces() > 0) {
//                if (faceView != null) {
//                    faceView.clearFaces();
//                    faceView.setVisibility(View.VISIBLE);
//                }
                mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                    @Override
                    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
//                        Log.v(FACE_TAG, "onFaceDetection-->" + faces);
                        if (listener != null) {
                            listener.onFaceDetection(faces);
                        }
                    }
                });
                mCamera.startFaceDetection();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void initCamera() {
//        mParameters = mCamera.getParameters();
//        mParameters.setPictureFormat(PixelFormat.JPEG);
////        mParameters.setPictureSize(1080, 1920);
//        mParameters.setPictureSize(800, 600);
//        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        setDispaly(mParameters, mCamera);
//        mCamera.setParameters(mParameters);
//        mCamera.startPreview();
//        mCamera.cancelAutoFocus();

        mCamera = Camera.open();
        try {
            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewSize(800, 600);
            parameters.setPreviewFormat(ImageFormat.NV21);

            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                Log.v(FACE_TAG, "SIZE:" + size.width + "x" + size.height);
            }
            for (Integer format : parameters.getSupportedPreviewFormats()) {
                Log.v(FACE_TAG, "FORMAT:" + format);
            }

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

            parameters.setJpegQuality(100); // 设置照片质量
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
            }
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(FACE_TAG, "setupCamera-->" + e.getMessage());
        }
        if (mCamera != null) {
            int mWidth = mCamera.getParameters().getPreviewSize().width;
            int mHeight = mCamera.getParameters().getPreviewSize().height;
            mCamera.startPreview();
            mCamera.autoFocus(null);
            mCamera.cancelAutoFocus();

            Log.v(FACE_TAG, "SIZE:" + mWidth + "x" + mHeight);
        }
    }

    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }
    }

    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e(TAG, "image error");
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        Log.d(TAG, "surfaceDestroyed() is called");
    }

    public void takePicture(Camera.PictureCallback imageCallback) {
        mCamera.takePicture(null, null, imageCallback);
    }

    public void setListener(CustomFaceDetectionListener listener) {
        this.listener = listener;
    }

    private CustomFaceDetectionListener listener;

    public interface CustomFaceDetectionListener {
        public void onFaceDetection(Camera.Face[] faces);
    }
}
