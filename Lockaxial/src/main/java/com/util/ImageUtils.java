package com.util;

import android.graphics.Bitmap;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKFace;

import java.util.List;

public class ImageUtils {


    public static byte[] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {

        int[] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];

        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        return yuv;
    }

    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int R, G, B, Y, U, V;
        int index = 0;

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff);

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));


                if (j % 2 == 0 && index % 2 == 0 && uvIndex < yuv420sp.length - 2) {
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }


                index++;
            }
        }
    }

    public static int findFDMaxAreaFace(List<AFD_FSDKFace> fdFaceList) {
        if (fdFaceList.size() == 0) {
            return -1;
        }
        int index = 0;
        int maxArea = 0;
        int area;
        for (int i = 0; i < fdFaceList.size(); i++) {
            area = fdFaceList.get(i).getRect().width() * fdFaceList.get(i).getRect().height();
            if (area > maxArea) {
                maxArea = area;
                index = i;
            }
        }
        return index;
    }

    public static int findFTMaxAreaFace(List<AFT_FSDKFace> ftFaceList) {
        if (ftFaceList.size() == 0) {
            return -1;
        }
        int index = 0;
        int maxArea = 0;
        int area;
        for (int i = 0; i < ftFaceList.size(); i++) {
            area = ftFaceList.get(i).getRect().width() * ftFaceList.get(i).getRect().height();
            if (area > maxArea) {
                maxArea = area;
                index = i;
            }
        }
        return index;
    }

}
