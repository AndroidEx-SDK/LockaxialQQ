package com.example.seriport;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jni.util.Utils;

/**
 * Created by simon on 2016/7/8.
 */
public class SerialPort {
    static {
        try {
            Utils.PrintLog(5, "JNI", "try to load libserial_port.so");
            System.loadLibrary("serial_port");
            // 加载本地库,也就是JNI生成的libxxx.so文件，下面再说。
        } catch (UnsatisfiedLinkError ule) {
            Utils.PrintLog(5, "JNI", "WARNING: Could not load libserial_port.so");
        }
    }

    private static final String TAG = "SerialPort";

    /*
 * Do not remove or rename the field mFd: it is used by native method close();
 */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		/* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    public void onDestroy() {
        if (mFileInputStream != null) {
            try {
                mFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    //打开读卡器
    public static native FileDescriptor open(String path, int baudrate, int flags);

    // 关闭读卡器
    public static native void close();
}
