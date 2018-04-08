package com.arcsoft.dysmart;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.widget.ExtImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.dysmart.FaceConstant.FACE_TAG;

public class FaceRegisterActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private final String TAG = this.getClass().toString();
    private final static int MSG_CODE = 0x1000;
    private final static int MSG_EVENT_REG = 0x1001;
    private final static int MSG_EVENT_NO_FACE = 0x1002;
    private final static int MSG_EVENT_NO_FEATURE = 0x1003;
    private final static int MSG_EVENT_FD_ERROR = 0x1004;
    private final static int MSG_EVENT_FR_ERROR = 0x1005;
    private UIHandler mUIHandler;
    // Intent data.
    private String mFilePath;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Bitmap mBitmap;
    private Rect src = new Rect();
    private Rect dst = new Rect();
    private Thread view;
    //    private HListView mHListView;
//    private RegisterViewAdapter mRegisterViewAdapter;
    private AFR_FSDKFace mAFR_FSDKFace;
    private String houseNumber, phoneNumber;
    private Bitmap faceBitmap;
    private boolean focus = true;

    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_face_register);
        //initial data.
        if (!getIntentData(getIntent().getExtras())) {
            Log.e(TAG, "getIntentData fail!");
            this.finish();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setLogo(R.mipmap.ic_launcher);
//            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(false);
        }

//        mRegisterViewAdapter = new RegisterViewAdapter(this);
//        mHListView = (HListView) findViewById(R.id.hlistView);
//        mHListView.setAdapter(mRegisterViewAdapter);
//        mHListView.setOnItemClickListener(mRegisterViewAdapter);

        ///storage/sdcard/Pictures/arcsoft_20180322094710.jpg
        ///data/data/com.tencent.devicedemo/files/photo.bmp
        Log.v(FACE_TAG, "onCreate-->" + mFilePath);
//        mFilePath = "/data/data/com.tencent.devicedemo/files/photo.bmp";

        mUIHandler = new UIHandler();
        mBitmap = BitmapUtils.decodeImage(mFilePath);
        src.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mSurfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(this);
        view = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mSurfaceHolder == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
                ImageConverter convert = new ImageConverter();
                convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
                if (convert.convert(mBitmap, data)) {
                    Log.d(TAG, "convert ok!");
                }
                convert.destroy();

                AFD_FSDKEngine engine = new AFD_FSDKEngine();
                AFD_FSDKVersion version = new AFD_FSDKVersion();
                List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
                AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
                Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
                if (err.getCode() != AFD_FSDKError.MOK) {
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_FD_ERROR;
                    reg.arg2 = err.getCode();
                    mUIHandler.sendMessage(reg);
                }
                err = engine.AFD_FSDK_GetVersion(version);
                Log.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
                err = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
                Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());
                while (mSurfaceHolder != null) {
                    Canvas canvas = mSurfaceHolder.lockCanvas();
                    if (canvas != null) {
                        Paint mPaint = new Paint();
                        boolean fit_horizontal = canvas.getWidth() / (float) src.width() < canvas.getHeight() / (float) src.height() ? true : false;
                        float scale = 1.0f;
                        if (fit_horizontal) {
                            scale = canvas.getWidth() / (float) src.width();
                            dst.left = 0;
                            dst.top = (canvas.getHeight() - (int) (src.height() * scale)) / 2;
                            dst.right = dst.left + canvas.getWidth();
                            dst.bottom = dst.top + (int) (src.height() * scale);
                        } else {
                            scale = canvas.getHeight() / (float) src.height();
                            dst.left = (canvas.getWidth() - (int) (src.width() * scale)) / 2;
                            dst.top = 0;
                            dst.right = dst.left + (int) (src.width() * scale);
                            dst.bottom = dst.top + canvas.getHeight();
                        }
                        canvas.drawBitmap(mBitmap, src, dst, mPaint);
                        canvas.save();
                        canvas.scale((float) dst.width() / (float) src.width(), (float) dst.height() / (float) src.height());
                        canvas.translate(dst.left / scale, dst.top / scale);
                        for (AFD_FSDKFace face : result) {
                            mPaint.setColor(Color.RED);
                            mPaint.setStrokeWidth(1.0f);
                            mPaint.setStyle(Paint.Style.STROKE);
                            canvas.drawRect(face.getRect(), mPaint);
                        }
                        canvas.restore();
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        break;
                    }
                }

                if (!result.isEmpty()) {
                    AFR_FSDKVersion version1 = new AFR_FSDKVersion();
                    AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
                    AFR_FSDKFace result1 = new AFR_FSDKFace();
                    AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
                    Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
                    if (error1.getCode() != AFD_FSDKError.MOK) {
                        Message reg = Message.obtain();
                        reg.what = MSG_CODE;
                        reg.arg1 = MSG_EVENT_FR_ERROR;
                        reg.arg2 = error1.getCode();
                        mUIHandler.sendMessage(reg);
                    }
                    error1 = engine1.AFR_FSDK_GetVersion(version1);
                    Log.d("com.arcsoft", "FR=" + version.toString() + "," + error1.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
                    error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
                    Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());
                    if (error1.getCode() == error1.MOK) {
                        mAFR_FSDKFace = result1.clone();
                        int width = result.get(0).getRect().width();
                        int height = result.get(0).getRect().height();
                        Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        Canvas face_canvas = new Canvas(face_bitmap);
                        face_canvas.drawBitmap(mBitmap, result.get(0).getRect(), new Rect(0, 0, width, height), null);
                        Message reg = Message.obtain();
                        reg.what = MSG_CODE;
                        reg.arg1 = MSG_EVENT_REG;
                        reg.obj = face_bitmap;
                        mUIHandler.sendMessage(reg);
                    } else {
                        Message reg = Message.obtain();
                        reg.what = MSG_CODE;
                        reg.arg1 = MSG_EVENT_NO_FEATURE;
                        mUIHandler.sendMessage(reg);
                    }
                    error1 = engine1.AFR_FSDK_UninitialEngine();
                    Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
                } else {
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_NO_FACE;
                    mUIHandler.sendMessage(reg);
                }
                err = engine.AFD_FSDK_UninitialFaceEngine();
                Log.d(TAG, "AFD_FSDK_UninitialFaceEngine =" + err.getCode());
            }
        });
        view.start();

        //创建一个线程,线程名字：pictureHandlerThread
        handlerThread = new HandlerThread("registerHandlerThread");
        //开启一个线程
        handlerThread.start();

        //在这个线程中创建一个handler对象
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.v(FACE_TAG, "handleMessage1-->" + msg.what + "/" + Thread.currentThread().getName());
//                boolean bool1 = ArcsoftManager.getInstance().mFaceDB.saveBitmap(phoneNumber, faceBitmap);
                boolean bool2 = ArcsoftManager.getInstance().mFaceDB.addFace(phoneNumber, mAFR_FSDKFace);
                Log.v(FACE_TAG, "handleMessage2-->" + true + "/" + bool2);
                deletePicture();

                Message message = new Message();
                message.what = -1;
                message.obj = new Object[]{msg.what, bool2};
                mUIHandler.sendMessageDelayed(message, 100);
            }
        };
    }

    /**
     * @param bundle
     * @note bundle data :
     * String imagePath
     */
    private boolean getIntentData(Bundle bundle) {
        try {
            mFilePath = bundle.getString("imagePath");
            if (mFilePath == null || mFilePath.isEmpty()) {
                return false;
            }
            ///storage/sdcard/Pictures/arcsoft_20180315154351.jpg
            ///storage/sdcard/Pictures/1521093628457.jpg
            Log.v(FACE_TAG, "getIntentData:" + mFilePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deletePicture() {
        File file = new File(mFilePath);
        if (file != null && !file.exists()) {
            return;
        }
        if (file.delete()) {
            Log.v(FACE_TAG, "deletePicture2-->");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(FACE_TAG, "surfaceChanged-->" + width + "/" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
        try {
            view.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class UIHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CODE) {
                if (msg.arg1 == MSG_EVENT_REG) {
                    LayoutInflater inflater = LayoutInflater.from(FaceRegisterActivity.this);
                    View layout = inflater.inflate(R.layout.dialog_register, null);
                    final ExtImageView mExtImageView = (ExtImageView) layout.findViewById(R.id.extimageview);
                    final EditText mEditText1 = (EditText) layout.findViewById(R.id.number_editview);
                    final EditText mEditText2 = (EditText) layout.findViewById(R.id.phone_editview);
//                    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

                    mEditText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            focus = hasFocus;
                        }
                    });

                    mEditText1.setInputType(InputType.TYPE_NULL);//设置有光标不显示输入法
                    mEditText2.setInputType(InputType.TYPE_NULL);

                    faceBitmap = (Bitmap) msg.obj;
                    mExtImageView.setImageBitmap(faceBitmap);

                    new AlertDialog.Builder(FaceRegisterActivity.this)
                            .setTitle("注册人脸信息")
                            .setIcon(R.mipmap.ic_launcher)
//                            .setIcon(R.mipmap.ic_launcher)
                            .setView(layout)
//                            .setPositiveButton("确定", null)
//                            .setNegativeButton("取消", null)
                            .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                        houseNumber = mEditText1.getText().toString().trim();
                                        phoneNumber = mEditText2.getText().toString().trim();
                                        int key = convertKeyCode(keyCode);
                                        Log.v(FACE_TAG, "onKey1-->" + keyCode + "/" + houseNumber + "/" + phoneNumber + "/" + key + "/" + focus);
                                        if (key >= 0) {
                                            if (focus) {//输入账号
                                                callInput(key, mEditText1);
                                            } else {//输入密码
                                                callInput(key, mEditText2);
                                            }
                                        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                                            Log.v(FACE_TAG, "onKey7-->" + keyCode + "/" + houseNumber + "/" + phoneNumber + "/" + key + "/" + focus);
                                            if (TextUtils.isEmpty(houseNumber) && TextUtils.isEmpty(phoneNumber)) {
                                                dialog.dismiss();
                                                FaceRegisterActivity.this.finish();
                                                return true;
                                            }
                                            if (focus) {
                                                if (!TextUtils.isEmpty(houseNumber)) {
                                                    setTextValue(mEditText1, backKey(mEditText1.getText().toString().trim()));
                                                }
                                            } else {
                                                if (!TextUtils.isEmpty(phoneNumber)) {
                                                    setTextValue(mEditText2, backKey(mEditText2.getText().toString().trim()));
                                                    return true;
                                                }
                                                mEditText1.setFocusable(true);
                                                mEditText1.setFocusableInTouchMode(true);
                                                mEditText1.requestFocus();
                                                mEditText1.requestFocusFromTouch();
                                            }
                                        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {//确认键
                                            if (focus) {
                                                if (TextUtils.isEmpty(houseNumber)) {
                                                    Toast.makeText(FaceRegisterActivity.this, "请输入房屋编号", Toast.LENGTH_SHORT).show();
                                                } else if (houseNumber.length() < 4) {
                                                    Toast.makeText(FaceRegisterActivity.this, "请输入4位房屋编号", Toast.LENGTH_SHORT).show();
                                                }
                                                mEditText1.setFocusable(true);
                                                mEditText1.setFocusableInTouchMode(true);
                                                mEditText1.requestFocus();
                                                mEditText1.requestFocusFromTouch();
                                            }
                                            if (!TextUtils.isEmpty(houseNumber) && houseNumber.length() == 4) {
                                                if (!focus && TextUtils.isEmpty(phoneNumber)) {
                                                    Toast.makeText(FaceRegisterActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                                                } else if (!focus && phoneNumber.length() < 11) {
                                                    Toast.makeText(FaceRegisterActivity.this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
                                                }
                                                mEditText2.setFocusable(true);
                                                mEditText2.setFocusableInTouchMode(true);
                                                mEditText2.requestFocus();
                                                mEditText2.requestFocusFromTouch();
                                            }
                                            if (!TextUtils.isEmpty(houseNumber) && houseNumber.length() == 4
                                                    && !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() == 11) {
                                                Log.v(FACE_TAG, "onKey3-->" + keyCode + "/" + houseNumber + "/" + phoneNumber + "/" + key + "/" + focus);
                                                if (inputFaceInfo()) {
                                                    dialog.dismiss();
                                                }
                                            }
                                        }
                                    }
                                    return true;
                                }
                            })
                            .show();
                } else if (msg.arg1 == MSG_EVENT_NO_FEATURE) {
                    Toast.makeText(FaceRegisterActivity.this, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_NO_FACE) {
                    Toast.makeText(FaceRegisterActivity.this, "没有检测到人脸，请换一张图片", Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_FD_ERROR) {
                    Toast.makeText(FaceRegisterActivity.this, "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_FR_ERROR) {
                    Toast.makeText(FaceRegisterActivity.this, "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == -1) {
                Object[] objects = (Object[]) msg.obj;
                boolean bool = (boolean) objects[1];
                if ((int) objects[0] == 0) {
                    Toast.makeText(FaceRegisterActivity.this, bool ? "注册成功" : "注册失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FaceRegisterActivity.this, bool ? "更新成功" : "更新失败", Toast.LENGTH_SHORT).show();
                }
                if (bool) {
                    FaceRegisterActivity.this.finish();
                }
            }
        }
    }

//    class Holder {
//        ExtImageView siv;
//        TextView tv;
//    }
//
//    class RegisterViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
//        Context mContext;
//        LayoutInflater mLInflater;
//
//        public RegisterViewAdapter(Context c) {
//            // TODO Auto-generated constructor stub
//            mContext = c;
//            mLInflater = LayoutInflater.from(mContext);
//        }
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return ArcsoftManager.getInstance().mFaceDB.mRegister.size();
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            // TODO Auto-generated method stub
//            Holder holder = null;
//            if (convertView != null) {
//                holder = (Holder) convertView.getTag();
//            } else {
//                convertView = mLInflater.inflate(R.layout.item_sample, null);
//                holder = new Holder();
//                holder.siv = (ExtImageView) convertView.findViewById(R.id.imageView1);
//                holder.tv = (TextView) convertView.findViewById(R.id.textView1);
//                convertView.setTag(holder);
//            }
//
//            if (!ArcsoftManager.getInstance().mFaceDB.mRegister.isEmpty()) {
//                FaceDB.FaceRegist face = ArcsoftManager.getInstance().mFaceDB.mRegister.get(position);
//                holder.tv.setText(face.mName);
//                Bitmap bitmap = ArcsoftManager.getInstance().mFaceDB.getFaceBitmap(face.mName);
//                if (bitmap != null) {
//                    holder.siv.setImageBitmap(bitmap);
//                }
//                //holder.siv.setImageResource(R.mipmap.ic_diyu);
//                convertView.setWillNotDraw(false);
//            }
//
//            return convertView;
//        }
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Log.d("onItemClick", "onItemClick = " + position + "pos=" + mHListView.getScroll());
//            final String name = ArcsoftManager.getInstance().mFaceDB.mRegister.get(position).mName;
////            final int count = ArcsoftManager.getInstance().mFaceDB.mRegister.get(position).mFaceList.size();
//            new AlertDialog.Builder(FaceRegisterActivity.this)
////                    .setMessage("包含:" + count + "个注册人脸特征信息")
//                    .setMessage("删除手机号码(" + name + ")的人脸信息")
////                    .setPositiveButton("确定", null)
////                    .setNegativeButton("取消", null)
//                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
//                        @Override
//                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                                    ArcsoftManager.getInstance().mFaceDB.delete(name);
//                                    mRegisterViewAdapter.notifyDataSetChanged();
//                                    dialog.dismiss();
//                                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
//                                    dialog.dismiss();
//                                }
//                            }
//                            return false;
//                        }
//                    })
//                    .show();
//        }
//    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (faceBitmap != null) {
            faceBitmap.recycle();
            faceBitmap = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

    private boolean inputFaceInfo() {
        if (fileOperation(phoneNumber)) {
            new AlertDialog.Builder(this)
                    .setMessage("手机号码(" + phoneNumber + ")的人脸信息已注册，是否更新?")
//                    .setPositiveButton("确定", null)
//                    .setNegativeButton("取消", null)
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                    handler.sendEmptyMessageDelayed(1, 100);
//                                    mRegisterViewAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                                    dialog.dismiss();
                                }
                            }
                            return false;
                        }
                    })
                    .show();
        } else {
            handler.sendEmptyMessageDelayed(0, 100);
//            mRegisterViewAdapter.notifyDataSetChanged();
        }
        return true;
    }

    private boolean fileOperation(String name) {
        boolean bool = false;
        String path = getExternalCacheDir().getPath();
//        Log.v(FACE_TAG, "fileOperation-->" + path);
        File file = new File(path);
        if (file != null && file.exists()) {
            File[] files = file.listFiles();// 读取文件夹下文件
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {//检查此路径名的文件是否是一个目录(文件夹)
                        continue;
                    }
                    String fileName = file1.getName();
                    Log.v(FACE_TAG, "fileOperation1-->" + fileName + "/" + file1.getPath());
                    if (fileName.endsWith(".data")) {
                        bool = fileName.contains(name);
                        if (fileName.contains(name)) {
                            bool = true;
                            break;
                        }
                    }
//                    String str = "";
//                    if (fileName.endsWith(".png")) {
//                        str = fileName.substring(0, fileName.lastIndexOf(".")).toString();
//                        Log.v(FACE_TAG, "fileOperation1-->" + str);
//                    }
//                    if (fileName.endsWith(".data")) {
//                        str = fileName.substring(0, fileName.lastIndexOf(".")).toString();
//                        Log.v(FACE_TAG, "fileOperation2-->" + str);
//                    }
                }
            }
        }
        return bool;
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
