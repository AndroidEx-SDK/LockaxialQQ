package com.ble;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import static com.ble.BTTempBLEService.ACTION_BIND_MAC;
import static com.ble.BTTempBLEService.ACTION_DATA_AVAILABLE;
import static com.ble.BTTempBLEService.ACTION_GATT_CONNECTING;
import static com.ble.BTTempBLEService.ACTION_GATT_SERVICES_DISCOVERED;
import static com.ble.BTTempBLEService.ACTION_GAT_RSSI;
import static com.ble.BTTempBLEService.ACTION_LOCK_STARTS;

/*
 * 蓝牙设备的基类
 *         功能：
 *           1）保存设备属性
 *           2）获取设备属性
 *           3）结束服务，断开连接
 *           4）获取服务
 *           5）监视广播的属性
 *           6）数据加密
 *           7）数据解密
 *           
 * @author Kevin.wu
 * 
 */

public abstract class Bledevice {
    private static final String TAG = "Bledevice";
    Intent serviceIntent;
    protected static final byte[] CRCPASSWORD = {'C', 'h', 'e', 'c', 'k', 'A', 'e', 's'};
    protected Context context = null;
    protected BTTempBLEService bleService = null;
    public BluetoothDevice device = null;
    public RFStarBLEBroadcastReceiver delegate = null;
    public boolean isRegisterReceiver = false;
    private boolean startus = false;
    private boolean isBound = false;

    public Bledevice(Context context, BluetoothDevice device) {
        this.device = device;
        this.context = context;
    }

    /**
     * 设置连接，绑定服务
     */
    public void setBLEBroadcastDelegate() {
        Log.e(TAG, device.getAddress() + " 初始化");
        isRegisterReceiver = true;
        this.registerReceiver();
        if (serviceIntent == null) {
            Log.e(TAG, "serviceIntent is null");
            serviceIntent = new Intent(this.context, BTTempBLEService.class);
            isBound = this.context.bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE);
            Log.e(TAG, "isBound : " + isBound);//绑定服务结果
        }
    }

    public void unBindService() {
        Log.e(TAG, " unBindService: " + isBound);//解绑服务
        if (serviceConnection != null) {
            if (isBound) {
                Log.e(TAG, " unBindService: " + isBound);//解绑服务
                this.context.unbindService(serviceConnection);
                isBound = false;
            }
        }
    }

    /**
     * 连接服务
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            bleService = null;
            Log.w(TAG, "gatt is not init onServiceDisconnected" + name);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.d(TAG, "serviceConnected :   服务启动 ");
            bleService = ((BTTempBLEService.LocalBinder) service).getService();
            // Automatically connects to the device upon successful start-up initialization.
//            bleService.disconnect();
//            //bleService.close();
//            bleService.stopSelf();
            bleService.initBluetoothDevice(device);//初始化BLE 如果已经连接就不用再次连
            Log.w(TAG, "gatt is init onServiceConnected ");
        }
    };

    /**
     * 获取特征值
     *
     * @param characteristic
     */
    public void readValue(BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            Log.w(TAG, "readValue characteristic is null");
        } else {
            Log.w(TAG, "readValue characteristic :" + characteristic.getUuid().toString());
            bleService.readValue(this.device, characteristic);

        }
    }

    /**
     * 根据特征值写入数据
     *
     * @param characteristic
     */
    public void writeValue(BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            Log.e(TAG, "writeValue characteristic is null");
        } else {
            try {
                if (bleService != null) {
                    bleService.writeValue(this.device, characteristic);
                    Log.d("Bledevice", "writeValue characteristic ==" + characteristic.getUuid().toString() + "	address:" + this.device.getAddress());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 消息使能，读取消息
     *
     * @param characteristic
     * @param enable
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (characteristic == null) {
            Log.e(TAG, "Notification characteristic is null");
        } else {
            Log.d(TAG, "Notification characteristic:  " + characteristic.getUuid().toString());
            bleService.setCharacteristicNotification(this.device, characteristic, enable);
        }
    }

    /**
     * 断开所有连接
     */
    public void disconnectedDevice() {
        isRegisterReceiver = false;
        bleService.disconnect();
        this.ungisterReceiver();
        if (serviceConnection != null) {
            if (isBound) {
                this.context.unbindService(serviceConnection);
                isBound = false;
            }
            this.context.stopService(new Intent(this.context, BTTempBLEService.class));
        }
    }

    /**
     * 断开指定设备
     *
     * @param address
     */
    public void disconnectedDevice(String address) {
        Log.e(TAG, " disconnectedDevice 断开连接------1--------" + this.bleService);
        isRegisterReceiver = false;
        try {
            this.bleService.disconnect(address);
            this.closeDevice();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        ungisterReceiver();
        unBindService();

    }

    /**
     * 自动断开时，注销服务
     *
     * @param
     */
    public void disconnectedDevice2() {
        isRegisterReceiver = false;
        Activity activity = (Activity) this.context;
        try {
            if (gattUpdateRecevice.isInitialStickyBroadcast())
                activity.unregisterReceiver(gattUpdateRecevice);
            if (serviceConnection != null) {
                if (isBound) {
                    this.context.unbindService(serviceConnection);
                    isBound = false;
                }
                this.context.stopService(new Intent(this.context, BTTempBLEService.class));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void closeDevice() {
        this.ungisterReceiver();
        if (serviceConnection != null) {
            if (isBound) {
                this.context.unbindService(serviceConnection);
                isBound = false;
            }
            this.context.stopService(new Intent(this.context, BTTempBLEService.class));
        }
    }

    public void sendReadRssi() {
        if (bleService == null) {
            Log.e(TAG, " bleService is  null");
            return;
        }
        if (device == null) {
            Log.e(TAG, " device is  null");
            return;
        }
        this.bleService.readRssi(this.device);
    }

    /**
     * 获取服务
     *
     * @return
     */
    public List<BluetoothGattService> getBLEGattServices() {
        return this.bleService.getSupportedGattServices(this.device);
    }

    /**
     * 监视广播的属性
     *
     * @return
     */
    protected IntentFilter bleIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        intentFilter.addAction(ACTION_GAT_RSSI);
        intentFilter.addAction(ACTION_BIND_MAC);
        intentFilter.addAction(ACTION_GATT_CONNECTING);
        intentFilter.addAction(ACTION_LOCK_STARTS);
        //自定义连接
        return intentFilter;
    }

    public interface RFStarBLEBroadcastReceiver {
        /**
         * 监视蓝牙状态的广播 macData蓝牙地址的唯一识别码
         */
        public void onReceive(Context context, Intent intent, String macData, String uuid);
    }

    /**
     * 注册监视蓝牙设备（返回数据的）广播
     */
    public void registerReceiver() {
        Activity activity = (Activity) this.context;
        if (activity != null) {
            activity.getApplicationContext().registerReceiver(gattUpdateRecevice, this.bleIntentFilter());
        } else {
            Log.e(TAG, "context is null");
        }
    }

    /**
     * 注销监视蓝牙返回的广播
     */
    public void ungisterReceiver() {
        Activity activity = (Activity) this.context;
        if (gattUpdateRecevice.isInitialStickyBroadcast())
            activity.unregisterReceiver(gattUpdateRecevice);
    }

    /**
     * 初始化服务中的特征
     */
    protected abstract void discoverCharacteristicsFromService(List<BluetoothGattService> gattServices);

    /**
     * 接收蓝牙广播
     */
    private BroadcastReceiver gattUpdateRecevice = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            // TODO Auto-generated method stub
            String mac = intent.getStringExtra("BT-MAC");
            if (ACTION_GATT_SERVICES_DISCOVERED.equals(intent.getAction()) && device.getAddress().equals(mac)) {//获取特征值
                Log.e(TAG, "发现服务 获取特征值");
                discoverCharacteristicsFromService(getBLEGattServices());//初始化特征值
            }
        }
    };
}



