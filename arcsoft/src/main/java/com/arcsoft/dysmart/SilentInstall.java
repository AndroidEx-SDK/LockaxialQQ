package com.arcsoft.dysmart;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2018/1/12.
 * SilentInstall.executeCmd("input keyevent 4" + "\n");
 */
public class SilentInstall {
    public final static String COMMAND_SU = "su";
    public final static String COMMAND_SH = "sh";

    public static boolean executeCmd(String command) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        Process process = null;
        try {
            try {
                process = Runtime.getRuntime().exec(COMMAND_SU);
            } catch (Exception e1) {
                Log.e("xiao_", "su失败");
                try {
                    process = Runtime.getRuntime().exec(COMMAND_SH);
                } catch (Exception e2) {
                    Log.e("xiao_", "sh失败");
                    process = Runtime.getRuntime().exec("");
                }
            }
            if (process == null) {
                return false;
            }
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.d("xiao_", "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            Log.e("xiao_", e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e("xiao_", e.getMessage(), e);
            }
        }
        return result;
    }
}
