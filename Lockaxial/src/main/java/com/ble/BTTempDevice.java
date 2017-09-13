package com.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.util.Byte2HexUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BTTempDevice extends Bledevice {
    public static final String Temp_ReceiveCharateristicUUID = "fff1";//服务特征UUID：Receive：接收数据
    public static final String Temp_SendCharateristicUUID = "fff2";//服务特征UUID：Send：发送数据
    public static final String Lost_CharateristicUUID = "fff3";//特殊服务特征UUID：0x2A06
    public static final String SERVER_UUID = "fff0";//主服务特征UUID

    // 获取的特征值
    public static BluetoothGattCharacteristic TEMP_ReceiveCharateristic;               //接收数据特征值
    public static BluetoothGattCharacteristic TEMP_SendCharateristic;                  //发送数据特征值
    public static BluetoothGattCharacteristic LOST_Charateristic;                      //防丢特征

    public BTTempDevice(Context context, BluetoothDevice device) {
        // TODO Auto-generated constructor stub
        super(context, device);
    }

    public static Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("BTTempDevice", msg.what + "=======初始化======");
            if (msg.what == 11) {
                // SynSystemTime();
            }
        }
    };

    /**
     * 初始化特征值 从服务中扫描特征值
     */
    @Override
    public void discoverCharacteristicsFromService(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        for (BluetoothGattService service : gattServices) {
            Log.e("BTTempDevice", "service uuid:" + service.getUuid().toString());// 迭代服务
            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) { // 迭代特征值

                if (characteristic.getUuid().toString().contains(Temp_ReceiveCharateristicUUID)) {//
                    TEMP_ReceiveCharateristic = characteristic;//Receive：接收数据
                    Log.e("BTTempDevice", "接收特征：" + TEMP_ReceiveCharateristic.getUuid().toString());
                    this.setCharacteristicNotification(TEMP_ReceiveCharateristic, true);
                } else if (characteristic.getUuid().toString().contains(Temp_SendCharateristicUUID)) {
                    Log.e("BTTempDevice", "设备mac：" + device.getAddress());
                    TEMP_SendCharateristic = characteristic;//Send：发送数据
                    Log.e("BTTempDevice", "可写特征：" + TEMP_SendCharateristic.getUuid().toString());
                } else if (characteristic.getUuid().toString().contains(Lost_CharateristicUUID)) {
                    Log.d("BTTempDevice", ": " + characteristic);
                    LOST_Charateristic = characteristic;//防丢服务特征
                    this.readValue(LOST_Charateristic);
                }
            }
        }
    }

    /**
     * 开锁
     * 命令格式:[叫醒码][DZF_CMD:][长度][0x07][ID0][ID1][ID2][CMD_0][CMD1][参数]
     * 叫醒码: [0x00 0xff] 可选.如果用BLE_BCTS管脚叫醒,则可不用叫醒码.
     * 返回值: [房号][楼层号][命令']
     * <p>
     * 1.开门: [叫醒码] "DZF_CMD:" Length,0x07,0x00,0x00,0x03,'S','O'-->
     * [ 44 5A 46 5F 43 4D 44 3A 06 07 00 00 03 53 4F]
     * 44 5A 46 5F 43 4D 44 3A 06 07 00 00 03 53 4F
     * 返回1:0x01 02 44 5A 46 4C 3A 0C 52 2F 09 00 00 00 03 35 17 1D 21 01 //开门记录
     * 返回2:0x01 02 6F 00 命令正确执行
     */
    public void openLock() {//0xaa  0x0a  0x1a 0x01  0x01 0x01 0x01 0x01 0x01  0x010 0x0 0x0b  开锁
        String result = "445A465F434D443A0607000003534F";
        String lock_starts = "445A465F434D443A06070000035253";

        if (TEMP_SendCharateristic != null) {
            Log.e("BTTempDevice", "openLock 发送开锁指令");
            TEMP_SendCharateristic.setValue(Byte2HexUtil.decodeHex(result.toCharArray()));
            this.writeValue(TEMP_SendCharateristic);
            Log.e("BTTempDevice", "TEMP_SendCharateristic ：" + TEMP_SendCharateristic.getUuid().toString());

        } else {
            Log.e("BTTempDevice", "TEMP_SendCharateristic is null");

        }


        if (TEMP_SendCharateristic != null) {
            Log.e("BTTempDevice", "openLock 发送读取门锁状态指令");
            TEMP_SendCharateristic.setValue(Byte2HexUtil.decodeHex(lock_starts.toCharArray()));
            this.writeValue(TEMP_SendCharateristic);
            Log.e("BTTempDevice", "TEMP_SendCharateristic ：" + TEMP_SendCharateristic.getUuid().toString());
        } else {
            Log.e("BTTempDevice", "TEMP_SendCharateristic is null");
        }

        Log.d("BTTempDevice", "openLock write cmd : " + Byte2HexUtil.byte2Hex(Byte2HexUtil.decodeHex(result.toCharArray())));
    }

    public void closeLock() {
        String result_close = "445A465F434D443A06070000035343";
        if (TEMP_SendCharateristic != null) {
            Log.e("BTTempDevice", "openLock 发送关锁指令");
            TEMP_SendCharateristic.setValue(Byte2HexUtil.decodeHex(result_close.toCharArray()));
            this.writeValue(TEMP_SendCharateristic);
            Log.e("BTTempDevice", "TEMP_SendCharateristic ：" + TEMP_SendCharateristic.getUuid().toString());
        } else {
            Log.e("BTTempDevice", "TEMP_SendCharateristic is null");
        }
    }

    /**
     * 心跳
     */
    public void sentHeartBeat(int rssi) {
        String result = "aa0a1c" + Integer.toHexString(Math.abs(rssi)) + getLocalMac().replaceAll(":", "") + "000b";
        if (TEMP_SendCharateristic != null) {
            Log.e("BTTempDevice", "发送心跳 rssi:" + Math.abs(rssi));
            TEMP_SendCharateristic.setValue(Byte2HexUtil.decodeHex(result.toCharArray()));
            this.writeValue(TEMP_SendCharateristic);
        }
        Log.d("BTTempDevice", "sentHeartBeat  result ==" + result);
    }

    /**
     * 获取本机蓝牙地址
     *
     * @return
     */
    private String getLocalMac() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String address = bluetoothAdapter.getAddress();
        return address;
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public byte[] getSystemTime() {
        byte[] cal = new byte[7];
        Calendar calendar = Calendar.getInstance();
        cal[0] = (byte) (calendar.get(Calendar.YEAR) & 0xff);
        cal[1] = (byte) (calendar.get(Calendar.YEAR) >> 8 & 0xff);
        cal[2] = (byte) ((calendar.get(Calendar.MONTH) + 1) & 0xff);
        cal[3] = (byte) (calendar.get(Calendar.DAY_OF_MONTH) & 0xff);
        cal[4] = (byte) (calendar.get(Calendar.HOUR_OF_DAY) & 0xff);
        cal[5] = (byte) (calendar.get(Calendar.MINUTE) & 0xff);
        cal[6] = (byte) (calendar.get(Calendar.SECOND) & 0xff);
        Log.d("BTTempDevice", "！！！！！！   " + Byte2HexUtil.byte2Hex(cal));
        return cal;
    }


    /**
     * 1.同步系统时间(0x00)每次手机和设备连接上 要主动发送同步系统时间  进行时间设定和校准
     * APP:0xF1+0x00+0x06（1bytes）+时间(6字节)+校验+0x55
     * 固件:0xA1+0x00+0x01（1bytes）+返回值(0x01:成功 / 0x00:失败)+校验+0x55
     */
    public void SynSystemTime() {
        byte[] time = new byte[11];
        Calendar calendar = Calendar.getInstance();
        time[0] = (byte) 0xF1;
        time[1] = (byte) 0x00;
        time[2] = (byte) 0x06;

        //		time[3] = (byte) (calendar.get(Calendar.YEAR) & 0xff);
        time[3] = (byte) (calendar.get(Calendar.YEAR) >> 7 & 0xff);
        time[4] = (byte) ((calendar.get(Calendar.MONTH) + 1) & 0xff);
        time[5] = (byte) (calendar.get(Calendar.DAY_OF_MONTH) & 0xff);
        time[6] = (byte) (calendar.get(Calendar.HOUR_OF_DAY) & 0xff);
        time[7] = (byte) (calendar.get(Calendar.MINUTE) & 0xff);
        time[8] = (byte) (calendar.get(Calendar.SECOND) & 0xff);

        time[9] = (byte) (time[1] + time[2] + time[3] + time[4] + time[5] + time[6] + time[7] + time[8]);
        time[10] = (byte) 0x55;
        if (TEMP_SendCharateristic != null) {
            Log.e("BTTempDevice", "SynSystemTime 同步系统时间");
            TEMP_SendCharateristic.setValue(time);
            this.writeValue(TEMP_SendCharateristic);
        }
        Log.i("BTTempDevice", "SynSystemTime ==" + Byte2HexUtil.byte2Hex(time));
        //		SynSystemTime ==F1 00 06 0F 05 1A 10 36 15 8F 55
    }

    /**
     * 同步当天的历史数据(0x01)
     * 发送请求历史数据的时间
     *
     * @return 0xF1+0x01+0x06（1byte）+时间（6bytes）年-月-日-时-分-秒+校验（1byte）+0x55
     */
    public void getHistoryTime() {
        byte[] time = new byte[11];
        Calendar calendar = Calendar.getInstance();
        time[0] = (byte) 0xF1;
        time[1] = (byte) 0x01;
        time[2] = (byte) 0x06;

        SimpleDateFormat format = new SimpleDateFormat("yy");
        String year = format.format(new Date());

        time[3] = (byte) (Integer.parseInt(year) & 0xff);
        time[4] = (byte) ((calendar.get(Calendar.MONTH) + 1) & 0xff);
        time[5] = (byte) (calendar.get(Calendar.DAY_OF_MONTH) & 0xff);
        time[6] = (byte) (calendar.get(Calendar.HOUR_OF_DAY) & 0xff);
        time[7] = (byte) (calendar.get(Calendar.MINUTE) & 0xff);
        time[8] = (byte) (calendar.get(Calendar.SECOND) & 0xff);

        time[9] = (byte) (time[1] + time[2] + time[3] + time[4] + time[5] + time[6] + time[7] + time[8]);
        time[10] = (byte) 0x55;

        if (TEMP_SendCharateristic != null) {
            TEMP_SendCharateristic.setValue(time);
            this.writeValue(TEMP_SendCharateristic);
        }
    }

    /**
     * 同步指定天的历史数据(0x01)
     * 发送请求历史数据的时间
     *
     * @return 0xF1+0x01+0x06（1byte）+时间（6bytes）年-月-日-时-分-秒+校验（1byte）+0x55
     */
    public void getHistoryTime(int year, int month, int day) {
        byte[] time = new byte[11];
        Calendar calendar = Calendar.getInstance();
        time[0] = (byte) 0xF1;
        time[1] = (byte) 0x01;
        time[2] = (byte) 0x06;

        time[3] = (byte) year;
        time[4] = (byte) month;
        time[5] = (byte) day;
        time[6] = (byte) (calendar.get(Calendar.HOUR_OF_DAY) & 0xff);
        time[7] = (byte) (calendar.get(Calendar.MINUTE) & 0xff);
        time[8] = (byte) (calendar.get(Calendar.SECOND) & 0xff);

        time[9] = (byte) (time[1] + time[2] + time[3] + time[4] + time[5] + time[6] + time[7] + time[8]);
        time[10] = (byte) 0x55;

        if (TEMP_SendCharateristic != null) {
            TEMP_SendCharateristic.setValue(time);
            this.writeValue(TEMP_SendCharateristic);
        }
    }
}




