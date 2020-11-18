package com.tencent.devicedemo;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;

import com.androidex.bean.FaceBean;
import com.androidex.config.DeviceConfig;
import com.androidex.service.MainService;
import com.androidex.utils.FaceHelper;
import com.androidex.utils.HttpApi;
import com.androidex.utils.HttpUtils;
import com.arcsoft.dysmart.ArcsoftManager;
import com.util.InstallUtil;
import com.util.ShellUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
//接收消息工具类
public class FaceMessage {

    protected void onMessage(String content) {
        HttpApi.i("content = " + content);
        if(content.equals("refresh face info")){//更新保存脸的信息
            if(content!= null){
                initFaceData();
            }else{
                HttpApi.i("人脸未更新...");
            }
        }else if(content.equals("device reboot")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String cmd = "reboot";
                    HttpApi.i("設備即將重啟...");
                    ShellUtils.CommandResult result = InstallUtil.executeCmd(cmd);
                }
            }).start();
        }
    }
    private FaceHelper faceHelper;
    private void initFaceData(){
        String httpServerToken = "";
        String url = DeviceConfig.SERVER_URL + "/app/rfid/getFaceDataByLockid?lockid=" + MainService.lockId;
        HttpApi.getInstance().loadHttpforGet(url,httpServerToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String result = response.body().string();
                    HttpApi.i("人脸请求结果："+result);
                    faceHelper.registerFace(result);
                    registerFace();
                }
            }
        });
    }
    private void  registerFace(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<FaceBean> data = faceHelper.getAllFace();
                if(data!=null && data.size()>0){
                    for(int i=0;i<data.size();i++){
                        HttpApi.i("开始加载："+data.get(i).faceName);
                        if(data.get(i).loadName == null
                                || data.get(i).loadName.length()<=0
                                || data.get(i).loading == 0){
                            String url = DeviceConfig.SERVER_URL+data.get(i).dataUrl;
                            String fileName = UUID.randomUUID().toString();
                            try {
                                String filePath = downloadFile(BaseApplication.path, url, fileName+".data");
                                if(filePath!=null){
                                    data.get(i).loadName = fileName;
                                    boolean result = ArcsoftManager.getInstance().mFaceDB.addFace(fileName);
                                    data.get(i).loading = result?1:0;
                                    HttpApi.i("("+ data.get(i).faceName +")加载结果："+result);
                                    faceHelper.updateLoading(data.get(i).id,data.get(i).loadName,data.get(i).loading);
                                }else{
                                    data.get(i).loadName = null;
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else{
                            HttpApi.i(data.get(i).faceName+"已经加载过!");
                        }
                    }
                }
            }
        }).start();
    }

    public static String downloadFile(String context, String url, String fileName)throws Exception {
        File file = null;
        OutputStream output=null;
        try {
            URL urlObject=new URL(url);
            HttpURLConnection conn=(HttpURLConnection)urlObject.openConnection();
            file=new File(context+"/"+fileName);//xxx.data
            InputStream input=conn.getInputStream();
            if(!file.exists()){
                String dir=context;
                File dirf = new File(dir);//新建文件夹
                if(!dirf.exists()){
                    dirf.mkdirs();
                }
                file.createNewFile();//新建文件
            }
            output=new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 8];
            BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
            BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
            int count = 0, n = 0;
            try {
                while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
                    out.write(buffer, 0, n);
                    count += n;
                }
                out.flush();
            } catch (IOException e) {
                throw e;
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally{
            try {
                output.close();
                System.out.println("success");
            } catch (IOException e) {
                System.out.println("fail");
                e.printStackTrace();
            }
        }
        return file.toString();
    }


}
