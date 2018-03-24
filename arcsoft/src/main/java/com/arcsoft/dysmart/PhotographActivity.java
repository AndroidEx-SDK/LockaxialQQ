package com.arcsoft.dysmart;

import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.service.carrier.CarrierService;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.conn.scheme.HostNameResolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.arcsoft.dysmart.FaceConstant.FACE_TAG;
import static com.arcsoft.dysmart.FaceConstant.PIC_PREFIX;

public class PhotographActivity extends AppCompatActivity implements Camera.PictureCallback, View.OnClickListener {

    private static final String TAG = PhotographActivity.class.getSimpleName();
    private SurfacePreview mCameraSurPreview;
    private FaceView faceView;
    private ImageView mCaptureButton, mDeleteButton;

    private File pictureFile;

    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photograph);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setLogo(R.mipmap.ic_diyu);
//            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        // Create our Preview view and set it as the content of our activity.
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        mCameraSurPreview = new SurfacePreview(this);
        preview.addView(mCameraSurPreview);

        faceView = (FaceView) findViewById(R.id.face_view);

        // Add a listener to the Capture button
        mCaptureButton = (ImageView) findViewById(R.id.capture);
        mDeleteButton = (ImageView) findViewById(R.id.delete);
        mCaptureButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);

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
                        savePicture((byte[]) msg.obj);
                        break;
                    case 1:
                        deletePicture();
                        break;
                }
            }
        };

        mCameraSurPreview.setListener(new SurfacePreview.CustomFaceDetectionListener() {
            @Override
            public void onFaceDetection(final Camera.Face[] faces) {
//                Camera.Face[] faces = (Camera.Face[]) msg.obj;
//                Log.v(FACE_TAG, "onFaceDetection-->" + faces);
//                faceView.setFaces(faces);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Camera.Face[] faces = (Camera.Face[]) msg.obj;
                        faceView.setFaces(faces);
                    }
                });
            }
        });
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

        //See if need to enable or not
        mCaptureButton.setEnabled(true);
        mCaptureButton.setVisibility(View.GONE);
        mDeleteButton.setVisibility(View.VISIBLE);
        Toast.makeText(this, "拍照成功", Toast.LENGTH_LONG).show();
        Log.v(FACE_TAG, "onPictureTaken3-->" + "拍照成功");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.capture) {
            mCaptureButton.setEnabled(false);
            // get an image from the camera
            mCameraSurPreview.takePicture(this);
        } else if (i == R.id.delete) {
            handler.sendEmptyMessageDelayed(1, 100);
        }
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

    private void savePicture(byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Log.v(FACE_TAG, "savePicture1-->" + pictureFile);
        } catch (FileNotFoundException e) {
            Log.v(FACE_TAG, "savePicture2-->" + e.getMessage());
        } catch (IOException e) {
            Log.v(FACE_TAG, "savePicture1-->" + e.getMessage());
        }
    }

    private void deletePicture() {
        if (pictureFile != null && !pictureFile.exists()) {
            Toast.makeText(this, "图片不存在", Toast.LENGTH_LONG).show();
            Log.v(FACE_TAG, "deletePicture1-->");
        }
        if (pictureFile.delete()) {
            Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
            mDeleteButton.post(new Runnable() {
                @Override
                public void run() {
                    mDeleteButton.setVisibility(View.GONE);
                    mCaptureButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Toast.makeText(this, "删除失败", Toast.LENGTH_LONG).show();
        }
        Log.v(FACE_TAG, "deletePicture2-->");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finishActivity();
//                this.finish(); // back button
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mCaptureButton.getVisibility() == View.VISIBLE) {
                mCaptureButton.setEnabled(false);
                // get an image from the camera
                mCameraSurPreview.takePicture(this);
            } else if (mDeleteButton.getVisibility() == View.VISIBLE) {
                handler.sendEmptyMessageDelayed(1, 100);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            finishActivity();
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void finishActivity() {
        if (pictureFile != null) {
            Intent intent = new Intent();
            intent.putExtra("imagePath", pictureFile.getPath());
            setResult(RESULT_OK, intent);
        }
    }
}
