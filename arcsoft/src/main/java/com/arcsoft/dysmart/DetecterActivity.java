package com.arcsoft.dysmart;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.guo.android_extend.widget.ExtImageView;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.phoneNumber;
import static com.arcsoft.dysmart.FaceConstant.FACE_TAG;

/**
 * Created by gqj3375 on 2017/4/28.
 */

//public class DetecterActivity extends AppCompatActivity implements CameraSurfaceView.OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback {
public class DetecterActivity extends AppCompatActivity implements CameraSurfaceView.OnCameraListener, Camera.AutoFocusCallback {

    private final String TAG = this.getClass().getSimpleName();

    AFT_FSDKFace mAFT_FSDKFace = null;
    private int mWidth, mHeight;
    private CameraSurfaceView mSurfaceView;
    private CameraGLSurfaceView mGLSurfaceView;
    private Camera mCamera;

    AFT_FSDKVersion version = new AFT_FSDKVersion();
    AFT_FSDKEngine engine = new AFT_FSDKEngine();
    //    ASAE_FSDKVersion mAgeVersion = new ASAE_FSDKVersion();
//    ASAE_FSDKEngine mAgeEngine = new ASAE_FSDKEngine();
//    ASGE_FSDKVersion mGenderVersion = new ASGE_FSDKVersion();
//    ASGE_FSDKEngine mGenderEngine = new ASGE_FSDKEngine();
    List<AFT_FSDKFace> result = new ArrayList<>();
//    List<ASAE_FSDKAge> ages = new ArrayList<>();
//    List<ASGE_FSDKGender> genders = new ArrayList<>();

    //    int mCameraID;
//    int mCameraRotate;
//    boolean mCameraMirror;
    byte[] mImageNV21 = null;
    FRAbsLoop mFRAbsLoop = null;
//    Handler mHandler;

//    Runnable hide = new Runnable() {
//        @Override
//        public void run() {
//            mTextView.setAlpha(0.5f);
//            mImageView.setImageAlpha(128);
//        }
//    };

    class FRAbsLoop extends AbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();
        List<FaceDB.FaceRegist> mResgist = ArcsoftManager.getInstance().mFaceDB.mRegister;
//        List<ASAE_FSDKFace> face1 = new ArrayList<>();
//        List<ASGE_FSDKFace> face2 = new ArrayList<>();

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            Log.d(TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            Log.d(TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        @Override
        public void loop() {
//            Log.v(FACE_TAG, "loop:" + mImageNV21);
            if (mImageNV21 != null) {
                long time = System.currentTimeMillis();
                AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
                Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
                AFR_FSDKMatching score = new AFR_FSDKMatching();
                float max = 0.0f;
                String name = null;
                for (FaceDB.FaceRegist fr : mResgist) {
                    if (fr.mName.length() > 11) {
                        for (AFR_FSDKFace face : fr.mIDFaceList) {
                            error = engine.AFR_FSDK_FacePairMatching(result, face, score);
                            Log.d(TAG, "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                            if (max < score.getScore()) {
                                max = score.getScore();
                                name = fr.mName;
                                if (max > 0.50f) {
                                    break;
                                }
                            }
                        }
                    }
                }

//                //age & gender
//                face1.clear();
//                face2.clear();
//                face1.add(new ASAE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
//                face2.add(new ASGE_FSDKFace(mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree()));
//                ASAE_FSDKError error1 = mAgeEngine.ASAE_FSDK_AgeEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face1, ages);
//                ASGE_FSDKError error2 = mGenderEngine.ASGE_FSDK_GenderEstimation_Image(mImageNV21, mWidth, mHeight, AFT_FSDKEngine.CP_PAF_NV21, face2, genders);
//                Log.d(TAG, "ASAE_FSDK_AgeEstimation_Image:" + error1.getCode() + ",ASGE_FSDK_GenderEstimation_Image:" + error2.getCode());
//                Log.d(TAG, "age:" + ages.get(0).getAge() + ",gender:" + genders.get(0).getGender());
//                final String age = ages.get(0).getAge() == 0 ? "年龄未知" : ages.get(0).getAge() + "岁";
//                final String gender = genders.get(0).getGender() == -1 ? "性别未知" : (genders.get(0).getGender() == 0 ? "男" : "女");
//
//                //crop
//                byte[] data = mImageNV21;
//                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
//                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
//                yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 80, ops);
//                final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
//                try {
//                    ops.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Log.v(FACE_TAG, "fit Score1:" + max + ", NAME:" + name);
                if (max > 0.5f) {
                    //fr success.
                    final float max_score = max;
                    Log.v(FACE_TAG, "fit Score2:" + max + ", NAME:" + name);
                    comparison = true;
                    inputIDCardInfo();
//                    final String mNameShow = name;
//                    mHandler.removeCallbacks(hide);
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mTextView.setAlpha(1.0f);
//                            mTextView.setText(mNameShow);
//                            mTextView.setTextColor(Color.RED);
//                            mTextView1.setVisibility(View.VISIBLE);
//                            mTextView1.setText("置信度：" + (float) ((int) (max_score * 1000)) / 1000.0);
//                            mTextView1.setTextColor(Color.RED);
////                            mImageView.setRotation(mCameraRotate);
//                            mImageView.setRotation(180);
//                            if (mCameraMirror) {
//                                mImageView.setScaleY(-1);
//                            }
//                            mImageView.setImageAlpha(255);
//                            mImageView.setImageBitmap(bmp);
//                        }
//                    });
                } else {
//                    final String mNameShow = "未识别";
//                    DetecterActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mTextView.setAlpha(1.0f);
//                            mTextView1.setVisibility(View.VISIBLE);
//                            mTextView1.setText(gender + "," + age);
//                            mTextView1.setTextColor(Color.RED);
//                            mTextView.setText(mNameShow);
//                            mTextView.setTextColor(Color.RED);
//                            mImageView.setImageAlpha(255);
////                            mImageView.setRotation(mCameraRotate);
//                            mImageView.setRotation(180);
//                            if (mCameraMirror) {
//                                mImageView.setScaleY(-1);
//                            }
//                            mImageView.setImageBitmap(bmp);
//                        }
//                    });
                }
                mImageNV21 = null;
            }
        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
            Log.d(TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }
    }

//    private TextView mTextView;
//    private TextView mTextView1;
//    private ImageView mImageView;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        Log.v(FACE_TAG, "onCreate-->" + mWidth + "/" + mHeight + "/" + density);

//        mCameraID = getIntent().getIntExtra("Camera", 0) == 0 ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
//        mCameraRotate = getIntent().getIntExtra("Camera", 0) == 0 ? 90 : 270;
//        mCameraMirror = getIntent().getIntExtra("Camera", 0) == 0 ? false : true;
//        mWidth = 1280;
//        mHeight = 960;
//        mHandler = new Handler();

//        Log.v(FACE_TAG, "onCreate-->" + mCameraID + "/" + mCameraRotate);
        Log.v(FACE_TAG, "onCreate-->" + 0 + "/" + true);

        setContentView(R.layout.activity_detecter);
        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
//        mGLSurfaceView.setOnTouchListener(this);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(this);
//        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, true, 0);
        mSurfaceView.debug_print_fps(true, false);

//        //snap
//        mTextView = (TextView) findViewById(R.id.textView);
//        mTextView.setText("");
//        mTextView1 = (TextView) findViewById(R.id.textView1);
//        mTextView1.setText("");
//
//        mImageView = (ImageView) findViewById(R.id.imageView);

        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, 16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

//        ASAE_FSDKError error = mAgeEngine.ASAE_FSDK_InitAgeEngine(FaceDB.appid, FaceDB.age_key);
//        Log.d(TAG, "ASAE_FSDK_InitAgeEngine =" + error.getCode());
//        error = mAgeEngine.ASAE_FSDK_GetVersion(mAgeVersion);
//        Log.d(TAG, "ASAE_FSDK_GetVersion:" + mAgeVersion.toString() + "," + error.getCode());
//
//        ASGE_FSDKError error1 = mGenderEngine.ASGE_FSDK_InitgGenderEngine(FaceDB.appid, FaceDB.gender_key);
//        Log.d(TAG, "ASGE_FSDK_InitgGenderEngine =" + error1.getCode());
//        error1 = mGenderEngine.ASGE_FSDK_GetVersion(mGenderVersion);
//        Log.d(TAG, "ASGE_FSDK_GetVersion:" + mGenderVersion.toString() + "," + error1.getCode());

        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();

        inputIDCard();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.v(FACE_TAG, "DetecterActivity/onDestroy-->");

        mSurfaceView.setVisibility(View.GONE);
        mGLSurfaceView.setVisibility(View.GONE);

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mFRAbsLoop != null) {
            mFRAbsLoop.shutdown();
        }
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        Log.d(TAG, "AFT_FSDK_UninitialFaceEngine =" + err.getCode());

//        ASAE_FSDKError err1 = mAgeEngine.ASAE_FSDK_UninitAgeEngine();
//        Log.d(TAG, "ASAE_FSDK_UninitAgeEngine =" + err1.getCode());
//
//        ASGE_FSDKError err2 = mGenderEngine.ASGE_FSDK_UninitGenderEngine();
//        Log.d(TAG, "ASGE_FSDK_UninitGenderEngine =" + err2.getCode());
    }

    @Override
    public Camera setupCamera() {
        // TODO Auto-generated method stub
//        mCamera = Camera.open(mCameraID);
        mCamera = Camera.open();
        try {
            Camera.Parameters parameters = mCamera.getParameters();
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
            mCamera.setParameters(parameters);
            Log.v(FACE_TAG, "setupCamera2-->" + mWidth + "/" + mHeight);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(FACE_TAG, "setupCamera-->" + e.getMessage());
        }
        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
            Log.v(FACE_TAG, "setupCamera3-->" + mWidth + "/" + mHeight);
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
        if (mImageNV21 == null) {
            if (!result.isEmpty()) {
                mAFT_FSDKFace = result.get(0).clone();
                mImageNV21 = data.clone();
            } else {
//                mHandler.postDelayed(hide, 3000);
            }
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

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        CameraHelper.touchFocus(mCamera, event, v, this);
//        return false;
//    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success) {
            Log.d(TAG, "Camera Focus SUCCESS!");
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                this.finish(); // back button
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            this.finish(); // back button
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler handler;
    private String id;

    private void inputIDCard() {
        id = getIntent().getStringExtra("ID");
        String path = getIntent().getStringExtra("path");
        Log.v(FACE_TAG, "inputIDCard-->" + id + "/" + path);
        if (!TextUtils.isEmpty(path)) {
            final Bitmap bitmap = BitmapUtils.decodeImage(path);
//            src.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] data = new byte[bitmap.getWidth() * bitmap.getHeight() * 3 / 2];
                    ImageConverter convert = new ImageConverter();
                    convert.initial(bitmap.getWidth(), bitmap.getHeight(), ImageConverter.CP_PAF_NV21);
                    if (convert.convert(bitmap, data)) {
                        Log.v(FACE_TAG, "convert ok!");
                    }
                    convert.destroy();

                    AFD_FSDKEngine engine = new AFD_FSDKEngine();
                    AFD_FSDKVersion version = new AFD_FSDKVersion();
                    List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
                    AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
                    Log.v(FACE_TAG, "AFD_FSDK_InitialFaceEngine1 = " + err.getCode());
//                    if (err.getCode() != AFD_FSDKError.MOK) {
//                        Message reg = Message.obtain();
//                        reg.what = MSG_CODE;
//                        reg.arg1 = MSG_EVENT_FD_ERROR;
//                        reg.arg2 = err.getCode();
//                        mUIHandler.sendMessage(reg);
//                    }
                    err = engine.AFD_FSDK_GetVersion(version);
                    Log.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
                    err = engine.AFD_FSDK_StillImageFaceDetection(data, bitmap.getWidth(), bitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
                    Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());
//                    while (mSurfaceHolder != null) {
//                        Canvas canvas = mSurfaceHolder.lockCanvas();
//                        if (canvas != null) {
//                            Paint mPaint = new Paint();
//                            boolean fit_horizontal = canvas.getWidth() / (float) src.width() < canvas.getHeight() / (float) src.height() ? true : false;
//                            float scale = 1.0f;
//                            if (fit_horizontal) {
//                                scale = canvas.getWidth() / (float) src.width();
//                                dst.left = 0;
//                                dst.top = (canvas.getHeight() - (int) (src.height() * scale)) / 2;
//                                dst.right = dst.left + canvas.getWidth();
//                                dst.bottom = dst.top + (int) (src.height() * scale);
//                            } else {
//                                scale = canvas.getHeight() / (float) src.height();
//                                dst.left = (canvas.getWidth() - (int) (src.width() * scale)) / 2;
//                                dst.top = 0;
//                                dst.right = dst.left + (int) (src.width() * scale);
//                                dst.bottom = dst.top + canvas.getHeight();
//                            }
//                            canvas.drawBitmap(mBitmap, src, dst, mPaint);
//                            canvas.save();
//                            canvas.scale((float) dst.width() / (float) src.width(), (float) dst.height() / (float) src.height());
//                            canvas.translate(dst.left / scale, dst.top / scale);
//                            for (AFD_FSDKFace face : result) {
//                                mPaint.setColor(Color.RED);
//                                mPaint.setStrokeWidth(1.0f);
//                                mPaint.setStyle(Paint.Style.STROKE);
//                                canvas.drawRect(face.getRect(), mPaint);
//                            }
//                            canvas.restore();
//                            mSurfaceHolder.unlockCanvasAndPost(canvas);
//                            break;
//                        }
//                    }

                    Log.v(FACE_TAG, "run-->" + 1111);
                    if (!result.isEmpty()) {
                        AFR_FSDKVersion version1 = new AFR_FSDKVersion();
                        AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
                        AFR_FSDKFace result1 = new AFR_FSDKFace();
                        AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
                        Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
//                        if (error1.getCode() != AFD_FSDKError.MOK) {
//                            Message reg = Message.obtain();
//                            reg.what = MSG_CODE;
//                            reg.arg1 = MSG_EVENT_FR_ERROR;
//                            reg.arg2 = error1.getCode();
//                            mUIHandler.sendMessage(reg);
//                        }
                        error1 = engine1.AFR_FSDK_GetVersion(version1);
                        Log.d("com.arcsoft", "FR=" + version.toString() + "," + error1.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
                        error1 = engine1.AFR_FSDK_ExtractFRFeature(data, bitmap.getWidth(), bitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
                        Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());
                        if (error1.getCode() == error1.MOK) {
                            Log.v(FACE_TAG, "run-->" + 3333333);
//                            boolean bool1 = ArcsoftManager.getInstance().mFaceDB.saveBitmap(phoneNumber, faceBitmap);
                            boolean bool2 = ArcsoftManager.getInstance().mFaceDB.addFace(id, result1.clone());
                            handler.sendEmptyMessageDelayed(0, 30 * 1000);
                            Log.v(FACE_TAG, "run-->" + 44444 + "/" + bool2);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DetecterActivity.this, "身份证人脸特征无法检测，请换一张身份证", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        error1 = engine1.AFR_FSDK_UninitialEngine();
                        Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetecterActivity.this, "身份证没有检测到人脸，请换一张身份证", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    err = engine.AFD_FSDK_UninitialFaceEngine();
                    Log.d(TAG, "AFD_FSDK_UninitialFaceEngine =" + err.getCode());
                }
            }).start();
        }

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (!comparison) {
                    Toast.makeText(DetecterActivity.this, "身份证录入失败，请重试", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return false;
            }
        });
    }

    private boolean input = false, comparison = false;

    private void inputIDCardInfo() {
        if (!input) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LayoutInflater inflater = LayoutInflater.from(DetecterActivity.this);
                    View layout = inflater.inflate(R.layout.dialog_register, null);
                    final ExtImageView mExtImageView = (ExtImageView) layout.findViewById(R.id.extimageview);
                    final EditText mEditText1 = (EditText) layout.findViewById(R.id.number_editview);
                    final EditText mEditText2 = (EditText) layout.findViewById(R.id.phone_editview);
//                    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

                    mEditText2.setVisibility(View.GONE);

                    mEditText1.setInputType(InputType.TYPE_NULL);

//                faceBitmap = (Bitmap) msg.obj;
                    mExtImageView.setImageBitmap((Bitmap) getIntent().getParcelableExtra("avatar"));

                    new AlertDialog.Builder(DetecterActivity.this)
                            .setTitle("注册身份证信息")
                            .setIcon(R.mipmap.ic_diyu)
//                            .setIcon(R.mipmap.ic_launcher)
                            .setView(layout)
                            .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    final String houseNumber = mEditText1.getText().toString().trim();
                                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                        int key = convertKeyCode(keyCode);
                                        Log.v(FACE_TAG, "onKey1-->" + keyCode + "/" + houseNumber + "/" + 5 + "/" + key + "/" + 8);
                                        if (key >= 0) {
                                            callInput(key, mEditText1);
                                        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                                            Log.v(FACE_TAG, "onKey7-->" + keyCode + "/" + houseNumber + "/" + 5 + "/" + key + "/" + 5);
                                            if (TextUtils.isEmpty(houseNumber)) {
                                                dialog.dismiss();
                                                DetecterActivity.this.finish();
                                                return true;
                                            }
                                            if (!TextUtils.isEmpty(houseNumber)) {
                                                setTextValue(mEditText1, backKey(mEditText1.getText().toString().trim()));
                                            }
                                        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {//确认键
                                            if (TextUtils.isEmpty(houseNumber)) {
                                                Toast.makeText(DetecterActivity.this, "请输入房屋编号", Toast.LENGTH_SHORT).show();
                                            } else if (houseNumber.length() < 4) {
                                                Toast.makeText(DetecterActivity.this, "请输入4位房屋编号", Toast.LENGTH_SHORT).show();
                                            }
                                            mEditText1.setFocusable(true);
                                            mEditText1.setFocusableInTouchMode(true);
                                            mEditText1.requestFocus();
                                            mEditText1.requestFocusFromTouch();

                                            if (!TextUtils.isEmpty(houseNumber) && houseNumber.length() == 4) {
                                                Log.v(FACE_TAG, "onKey3-->" + keyCode + "/" + houseNumber + "/" + 8 + "/" + key + "/" + 8);
                                                ArcsoftManager.getInstance().saveIDCardData(houseNumber, id);
                                                ArcsoftManager.getInstance().saveIDCardData(id);
                                                Toast.makeText(DetecterActivity.this, "身份证录入成功", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                DetecterActivity.this.finish();
                                            }
                                        }
                                    }
                                    return true;
                                }
                            }).show();
                }
            });
            input = true;
        }
    }

    private int convertKeyCode(int keyCode) {
        int value = -1;
        if ((keyCode == KeyEvent.KEYCODE_0)) {
            value = 0;
        } else if ((keyCode == KeyEvent.KEYCODE_1)) {
            value = 1;
        } else if ((keyCode == KeyEvent.KEYCODE_2)) {
            value = 2;
        } else if ((keyCode == KeyEvent.KEYCODE_3)) {
            value = 3;
        } else if ((keyCode == KeyEvent.KEYCODE_4)) {
            value = 4;
        } else if ((keyCode == KeyEvent.KEYCODE_5)) {
            value = 5;
        } else if ((keyCode == KeyEvent.KEYCODE_6)) {
            value = 6;
        } else if ((keyCode == KeyEvent.KEYCODE_7)) {
            value = 7;
        } else if ((keyCode == KeyEvent.KEYCODE_8)) {
            value = 8;
        } else if ((keyCode == KeyEvent.KEYCODE_9)) {
            value = 9;
        }
        return value;
    }

    private void callInput(int key, EditText editText) {
        setTextValue(editText, editText.getText().toString().trim() + key);
    }

    private void setTextValue(final EditText editText, String value) {
        final String thisValue = value;
        editText.post(new Runnable() {
            @Override
            public void run() {
                setTextView(editText, thisValue);
            }
        });
    }

    private void setTextView(EditText editText, String txt) {
        editText.setText(txt);
    }

    private String backKey(String code) {
        if (code != null && code != "") {
            int length = code.length();
            if (length == 1) {
                code = "";
            } else {
                code = code.substring(0, (length - 1));
            }
        }
        return code;
    }
}
