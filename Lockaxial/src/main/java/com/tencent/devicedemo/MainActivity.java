package com.tencent.devicedemo;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.DoorLock;
import com.androidex.GetUserInfo;
import com.androidex.NetWork;
import com.androidex.SoundPoolUtil;
import com.androidex.Zxing;
import com.androidex.callback.AdverErrorCallBack;
import com.androidex.common.AndroidExActivityBase;
import com.androidex.config.DeviceConfig;
import com.androidex.service.MainService;
import com.androidex.utils.AdvertiseHandler;
import com.androidex.utils.Ajax;
import com.androidex.utils.HttpApi;
import com.androidex.utils.HttpUtils;
import com.androidex.utils.NfcReader;
import com.androidex.utils.UploadUtil;
import com.arcsoft.dysmart.ArcsoftManager;
import com.arcsoft.dysmart.DetecterActivity;
import com.arcsoft.dysmart.FaceDB;
import com.arcsoft.dysmart.FaceRegisterActivity;
import com.arcsoft.dysmart.PhotographActivity2;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.ble.BTTempDevice;
import com.brocast.NotifyReceiverQQ;
import com.entity.Banner;
import com.example.seriport.SerialPort;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.synjones.dysmart.IdCardUtil;
import com.synjones.idcard.IDCard;
import com.tencent.device.TXBinderInfo;
import com.tencent.device.TXDeviceService;
import com.tencent.devicedemo.interfac.NetworkCallBack;
import com.tencent.devicedemo.interfac.TakePictureCallback;
import com.util.DialogUtil;
import com.util.InstallUtil;
import com.util.Intenet;
import com.util.ShellUtils;
import com.viewpager.AutoScrollViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import jni.util.Utils;

import static com.androidex.NetWork.NETWOKR_TYPE_ETHERNET;
import static com.androidex.NetWork.NETWORK_TYPE_WIFI;
import static com.androidex.service.MainService.MSG_UPDATE_VERSION;
import static com.androidex.service.MainService.communityId;
import static com.androidex.service.MainService.httpServerToken;
import static com.androidex.service.MainService.lockId;
import static com.androidex.utils.NfcReader.ACTION_NFC_CARDINFO;
import static com.arcsoft.dysmart.FaceConstant.FACE_TAG;
import static com.arcsoft.dysmart.FaceConstant.MSG_FACE_DETECT_CONTRAST;
import static com.arcsoft.dysmart.FaceConstant.MSG_FACE_DETECT_INPUT;
import static com.arcsoft.dysmart.FaceConstant.MSG_FACE_DETECT_PAUSE;
import static com.arcsoft.dysmart.FaceConstant.MSG_ID_CARD_DETECT_INPUT;
import static com.arcsoft.dysmart.FaceConstant.MSG_ID_CARD_DETECT_PAUSE;
import static com.arcsoft.dysmart.FaceConstant.MSG_ID_CARD_DETECT_RESTART;
import static com.ble.BTTempBLEService.ACTION_GATT_CONNECTED;
import static com.ble.BTTempBLEService.ACTION_GATT_DISCONNECTED;
import static com.ble.BTTempBLEService.ACTION_LOCK_BATTERY;
import static com.ble.BTTempBLEService.ACTION_LOCK_STARTS_CLOSE;
import static com.ble.BTTempBLEService.ACTION_LOCK_STARTS_CLOSE_BACK;
import static com.ble.BTTempBLEService.ACTION_LOCK_STARTS_OPEN;
import static com.util.Constant.CALLING_MODE;
import static com.util.Constant.CALL_CANCEL_MODE;
import static com.util.Constant.CALL_MODE;
import static com.util.Constant.DIRECT_CALLING_MODE;
import static com.util.Constant.DIRECT_CALLING_TRY_MODE;
import static com.util.Constant.DIRECT_MODE;
import static com.util.Constant.ERROR_MODE;
import static com.util.Constant.MSG_ADVERTISE_IMAGE;
import static com.util.Constant.MSG_ADVERTISE_REFRESH;
import static com.util.Constant.MSG_CALLMEMBER_DIRECT_COMPLETE;
import static com.util.Constant.MSG_CALLMEMBER_DIRECT_DIALING;
import static com.util.Constant.MSG_CALLMEMBER_DIRECT_FAILED;
import static com.util.Constant.MSG_CALLMEMBER_DIRECT_SUCCESS;
import static com.util.Constant.MSG_CALLMEMBER_DIRECT_TIMEOUT;
import static com.util.Constant.MSG_CALLMEMBER_ERROR;
import static com.util.Constant.MSG_CALLMEMBER_NO_ONLINE;
import static com.util.Constant.MSG_CALLMEMBER_SERVER_ERROR;
import static com.util.Constant.MSG_CALLMEMBER_TIMEOUT;
import static com.util.Constant.MSG_CALLMEMBER_TIMEOUT_AND_TRY_DIRECT;
import static com.util.Constant.MSG_CANCEL_CALL_COMPLETE;
import static com.util.Constant.MSG_CHECK_BLOCKNO;
import static com.util.Constant.MSG_CONNECT_ERROR;
import static com.util.Constant.MSG_CONNECT_SUCCESS;
import static com.util.Constant.MSG_FINGER_CHECK;
import static com.util.Constant.MSG_INPUT_CARDINFO;
import static com.util.Constant.MSG_INPUT_CARDINFO_FAIL;
import static com.util.Constant.MSG_INPUT_CARDINFO_REPETITION;
import static com.util.Constant.MSG_INPUT_CARDINFO_SUCCEED;
import static com.util.Constant.MSG_INSTALL_SUCCEED;
import static com.util.Constant.MSG_INVALID_CARD;
import static com.util.Constant.MSG_LOCK_OPENED;
import static com.util.Constant.MSG_PASSWORD_CHECK;
import static com.util.Constant.MSG_REFRESH_COMMUNITYNAME;
import static com.util.Constant.MSG_REFRESH_DATA;
import static com.util.Constant.MSG_REFRESH_LOCKNAME;
import static com.util.Constant.MSG_RTC_DISCONNECT;
import static com.util.Constant.MSG_RTC_NEWCALL;
import static com.util.Constant.MSG_RTC_ONVIDEO;
import static com.util.Constant.ONVIDEO_MODE;
import static com.util.Constant.ON_YUNTONGXUN_INIT_ERROR;
import static com.util.Constant.ON_YUNTONGXUN_LOGIN_FAIL;
import static com.util.Constant.ON_YUNTONGXUN_LOGIN_SUCCESS;
import static com.util.Constant.PASSWORD_CHECKING_MODE;
import static com.util.Constant.PASSWORD_MODE;
import static com.util.Constant.RE_SYNC_SYSTEMTIME;
import static com.util.Constant.START_FACE_CHECK;

public class MainActivity extends AndroidExActivityBase implements NfcReader.AccountCallback, NfcAdapter.ReaderCallback, TakePictureCallback, NotifyReceiverQQ.CallBack, View.OnClickListener, CameraSurfaceView.OnCameraListener, IdCardUtil.BitmapCallBack {
    private static final String TAG = "MainActivity";
    public static final int INPUT_CARDINFO_RESULTCODE = 0X01;
    public static final int INPUT_CARDINFO_REQUESTCODE = 0X02;
    public static final int INPUT_SYSTEMSET_REQUESTCODE = 0X03;
    public static final int INPUT_FACE_REQUESTCODE = 0X04;
    public static final String address = "67:C2:B2:2F:72:FC";//马总
    //public static final String address = "1F:1C:32:AF:66:23";//寄出去的mac=== 1F:1C:32:AF:66:23
    private static final long SCAN_PERIOD = 12000;//扫描时间
    private static final String ACTION_SCAN_DEVICE = "ACTION_SCAN_DEVICE";//扫描到设备
    private static final String SCAN_DEVICE_FAIL = "SCAN_DEVICE_FAIL";//扫描失败
    private static final String REFRESH_RSSI = "REFRESH_RSSI";//获取蓝牙信号强度
    public static int currentStatus = CALL_MODE;
    public static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    private View container;
    private LinearLayout videoLayout;
    private RelativeLayout rl_nfc, rl;
    private GridView mGridView;
    private ImageView iv_setting, bluetooth_image, iv_bind, imageView, wifi_image;
    private TextView headPaneTextView,tv_message;
    private EditText tv_input, et_blackno, et_unitno,  tv_input_text;
    private BinderListAdapter mAdapter;
    private NotifyReceiverQQ mNotifyReceiver;
    private NfcReader nfcReader;
    private AutoScrollViewPager viewPager;
    private Banner banner;
    private Bg_Adapter bgAdapter;
    private DisplayImageOptions options;
    private WifiInfo wifiInfo = null;        //获得的Wifi信息
    private WifiManager wifiManager = null;    //Wifi管理器
    private Handler handler;
    private int level;                        //信号强度值
    private int blockId = 0;
    private int keyVoiceIndex = 0;
    private int dialogtime = 0;
    private boolean nfcFlag = false;
    private boolean isFlag = true;
    private boolean flag = false;//控制开始接通时，相机为空则再接通
    private boolean mScanning = false;//控制蓝牙扫描
    private boolean isConnectBLE = false;//蓝牙是否连接
    private Messenger serviceMessenger;
    private Messenger dialMessenger;
    private AdvertiseHandler advertiseHandler = null;
    private HashMap<String, String> uuidMaps = new HashMap<String, String>();
    private String lastImageUuid = "";
    private String blockNo = "";
    private String guestPassword = "";
    private String cardId;
    private String nfcMessage = "请将卡片放到感应区域，按确认键\n确定录入卡片，按删除键取消录入卡片";
    private SurfaceView localView = null;
    private SurfaceView remoteView = null;
    private SurfaceHolder autoCameraHolder = null;
    private Thread passwordTimeoutThread = null;
    private Thread clockRefreshThread = null;
    private SoundPool soundPool = null;
    private SurfaceView videoView = null;
    private SurfaceView autoCameraSurfaceView = null;
    private Parcelable[] listTemp1;
    private AlertDialog dialog;
    private Camera camera = null;
    private GoogleApiClient client;
    private AdverErrorCallBack adverErrorCallBack;
    private JSONArray rows;
    private Receive receive;
    private BluetoothAdapter mBtAdapter;
    private BTTempDevice device;
    private BluetoothDevice bluetooth_dev;
    private Handler handle = new Handler();
    private Timer timer_scanBle;// 扫描蓝牙时定时器
    private Runnable bleRunnable;//蓝牙
    private Handler bleHandler = new Handler();//蓝牙
    Timer timer = new Timer();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handle.postDelayed(runnable, 1000);
            if (dialog != null && dialog.isShowing()) {
                dialogtime++;
                if (dialogtime >= 30) {
                    handle.removeCallbacks(runnable);
                    dialog.setMessage("呼叫失败");
                    dialog.dismiss();
                    dialogtime = 0;
                }
            }
        }
    };
    //扫描回调
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            if (device.getAddress().equals(address)) {
                Log.d(TAG, "搜索到设备:" + device.getAddress() + ", address = " + address + ",rssi=" + rssi);
                sendMessage(ACTION_SCAN_DEVICE);
            }
        }
    };
    private TextView tv_battery;
    private TextView version_text;
    private boolean mCamerarelease = true;
    private Handler cameraHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x01){
                if(mCamerarelease){
                    cameraHandler.removeMessages(0x01);
                    buildVideo();
                }else{
                    cameraHandler.sendEmptyMessageDelayed(0x01,200);
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏设置，隐藏窗口所有装饰
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//清除FLAG
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效
        //requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        {
            ActionBar ab = getActionBar();
            if (ab != null)
                ab.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_main);
        hwservice.EnterFullScreen();
        initView();//初始化View
        initScreen();
        initHandler();
        initAexNfcReader();//初始化本地广播
        initServer();//初始化服务类
        initTXD();
        initQQReceiver();//初始化QQ物联广播
        initVoiceHandler();//
        initVoiceVolume();//
        initAdvertiseHandler();//初始化广告
        initAutoCamera();//
        startClockRefresh();//
        //initBLE();//初始化蓝牙  //稍微退后初始化一点，防止蓝牙共享程序停止运行bug
        getRssi();//使用定时器,每隔5秒获得一次信号强度值
        setNetWork();
        setAutioVolume();//获取系统相关音量
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            setDialStatus("请输入楼栋编号");
        }
        if (false) {
            AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent;
            PendingIntent pendingIntent;
            intent = new Intent(getApplicationContext(), AlarmReciver.class);
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, pendingIntent);
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //初始化人脸相关与身份证识别
        initFaceDetectAndIDCard();
    }

    /**
     * 初始化view
     */
    public void initView() {
        version_text = (TextView) findViewById(R.id.version_text);
        version_text.setText(getVersionName());
        container = findViewById(R.id.container);//根View
        rl_nfc = (RelativeLayout) findViewById(R.id.rl_nfc);
        et_blackno = (EditText) findViewById(R.id.et_blockno);
        et_unitno = (EditText) findViewById(R.id.et_unitno);
        iv_bind = (ImageView) findViewById(R.id.user_bind);
        imageView = (ImageView) findViewById(R.id.iv_erweima);
        wifi_image = (ImageView) findViewById(R.id.wifi_image); //wifi图标控件初始化
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        bluetooth_image = (ImageView) findViewById(R.id.bluetooth_image);
        tv_message = (TextView) findViewById(R.id.tv_message);
        viewPager = (AutoScrollViewPager) findViewById(R.id.vp_main);
        tv_input_text = (EditText) findViewById(R.id.tv_input_text);
        tv_battery = (TextView) findViewById(R.id.tv_battery);//显示蓝牙锁的电量
        mGridView = (GridView) findViewById(R.id.gridView_binderlist);//getBgBanners();//网络获得轮播背景图片数据
        rl = (RelativeLayout) findViewById(R.id.net_view_rl);
        rl.setOnClickListener(this);
        showMacText = (TextView) findViewById(R.id.show_mac);
        iv_setting.setOnClickListener(this);
        mAdapter = new BinderListAdapter(this);
        mGridView.setAdapter(mAdapter);
        sendBroadcast(new Intent("com.android.action.hide_navigationbar"));//隱藏底部導航
        setFullScreenView(container);
        setFullScreen(true);//禁止頂部下拉
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/GBK.TTF");
        tv_input = (EditText) findViewById(R.id.tv_input);
        tv_input.setTypeface(typeFace);// com_log.setTypeface(typeFace);
        et_blackno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isFlag = true;
                } else {
                    isFlag = false;
                }
            }
        });

    }

    private void initTXD(){
        getQR();//生成二维码
        Intent startIntent = new Intent(MainActivity.this, TXDeviceService.class);
        startService(startIntent);
    }

    /**
     * 初始化蓝牙
     */
    public void initBLE() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SCAN_DEVICE);//BLE搜索到设备
        intentFilter.addAction(SCAN_DEVICE_FAIL);//搜索失败
        intentFilter.addAction(DoorLock.DoorLockOpenDoor_BLE);//开门指令
        intentFilter.addAction(ACTION_GATT_CONNECTED);//连接成功
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);//断开连接
        intentFilter.addAction(REFRESH_RSSI);//获取信号值
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//设备蓝牙状态监控
        intentFilter.addAction(ACTION_LOCK_BATTERY);//蓝牙锁的电量
        intentFilter.addAction(ACTION_LOCK_STARTS_OPEN);//常开
        intentFilter.addAction(ACTION_LOCK_STARTS_CLOSE);//锁着
        intentFilter.addAction(ACTION_LOCK_STARTS_CLOSE_BACK);//反锁

        registerReceiver(dataUpdateRecevice, intentFilter);
        // 初始化蓝牙adapter
        if (!mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
            Log.d(TAG, "打开蓝牙");
        }
        if (mScanning) {
            scanLeDevice(false);//停止扫描
        }
        scanLeDevice(true);//开始扫描
        startGetDoorStarts();//启动定时器读取蓝牙锁状态
        Log.d(TAG, "开始扫描蓝牙");
    }

    /**
     * 搜索到设备后注册服务
     */
    private void bindDevice() {
        bluetooth_dev = mBtAdapter.getRemoteDevice(address);
        if (bluetooth_dev != null) {
            device = new BTTempDevice(MainActivity.this, bluetooth_dev);
        }
        if (device != null) {
            if (!device.isRegisterReceiver) {
                device.disconnectedDevice2();//注销服务
                Log.d(TAG, "设备已注册，取消服务重新注册");
                connectGatt();
            }
        } else {
            Log.d(TAG, "开始注册设备绑定服务");
            device = new BTTempDevice(MainActivity.this, bluetooth_dev);
            //device.setBLEBroadcastDelegate();
            connectGatt();
        }
    }

    private void connectGatt() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                device.setBLEBroadcastDelegate();//设置连接，绑定服务
            }
        }, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {//跳转网络或网络设置
            case R.id.net_view_rl: {
                Intenet.system_set(MainActivity.this, INPUT_SYSTEMSET_REQUESTCODE);
            }
            break;
            case R.id.iv_setting:
                initMenu();//初始化左上角弹出框
                break;
        }
    }

    /**
     * 初始化左上角弹出框
     */
    private void initMenu() {
        PopupMenu popup = new PopupMenu(MainActivity.this, iv_setting);
        popup.getMenuInflater()
                .inflate(R.menu.poupup_menu_home, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings1:
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);//跳轉到系統設置
                        intent.putExtra("back", true);
                        startActivityForResult(intent, INPUT_SYSTEMSET_REQUESTCODE);
                        break;

                    case R.id.action_catIP:
                        Toast.makeText(MainActivity.this, "本机的IP：" + Intenet.getHostIP(), Toast.LENGTH_LONG).show();
                        break;

                    case R.id.action_catVersion:
                        Toast.makeText(MainActivity.this, "本机的固件版本：" + hwservice.getSdkVersion(), Toast.LENGTH_LONG).show();
                        break;
                    case R.id.action_updateVersion:
                        Message message = Message.obtain();
                        message.what = MSG_UPDATE_VERSION;
                        try {
                            serviceMessenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.action_settings2://解除绑定
                        Toast.makeText(MainActivity.this, "该功能暂未开放", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.action_settings3://上传日志
                        TXDeviceService.getInstance().uploadSDKLog();
                        break;

                    case R.id.action_settings4://打开主门
                        int status = 2;
                        Intent ds_intent = new Intent();
                        ds_intent.setAction(DoorLock.DoorLockOpenDoor);
                        ds_intent.putExtra("index", 0);
                        ds_intent.putExtra("status", status);
                        sendBroadcast(ds_intent);

                        Intent intent_ble = new Intent();
                        intent_ble.setAction(DoorLock.DoorLockOpenDoor_BLE);
                        sendBroadcast(intent_ble);
                        break;

                    case R.id.action_settings5://打开副门
                        int status1 = 2;
                        Intent ds_intent1 = new Intent();
                        ds_intent1.setAction(DoorLock.DoorLockOpenDoor);
                        ds_intent1.putExtra("index", 1);
                        ds_intent1.putExtra("status", status1);
                        sendBroadcast(ds_intent1);
                        break;
                    case R.id.action_ble_open://打开蓝牙
                        if (!mBtAdapter.isEnabled()) {
                            mBtAdapter.enable();
                            Toast.makeText(MainActivity.this, "打开蓝牙", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "蓝牙未打开，打开蓝牙");
                        }
                        if (mScanning) {
                            scanLeDevice(false);//停止扫描
                        }
                        scanLeDevice(true);//开始扫描
                        Log.d(TAG, "开始扫描蓝牙");
                        break;
                    case R.id.action_ble_close://关闭蓝牙
                        if (device != null) {
                            device.disconnectedDevice(address);
                        }
                        if (mScanning) {
                            scanLeDevice(false);//停止扫描
                        }
                        Toast.makeText(MainActivity.this, "关闭蓝牙", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.action_settings6://设置定时开机
                        long wakeupTime = SystemClock.elapsedRealtime() + 240000;       //唤醒时间,如果是关机唤醒时间不能低于3分钟,否则无法实现关机定时重启
                        DoorLock.getInstance().runSetAlarm(wakeupTime);
                        break;

                    case R.id.action_settings7://重启
                        DoorLock.getInstance().runReboot();
                        break;

                    case R.id.action_settings8://关机
                        DoorLock.getInstance().runShutdown();
                        break;
                    case R.id.action_settings9://设置拔电关机
                        DoorLock.getInstance().setPlugedShutdown();
                        break;
                    case R.id.action_settings10://退出
                        setResult(RESULT_OK);
                        MainActivity.this.stopService(new Intent(MainActivity.this,MainService.class));
                        finish();
                        sendBroadcast(new Intent("com.android.action.display_navigationbar"));
                        break;
                    case R.id.action_settings11://语音开门
                        //initSpeech(MainActivity.this);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    private TimerTask task_scanBle;

    /**
     * 开启重复扫描，
     */
    private void startScanBle() {
        if (!isConnectBLE) {
            if (task_scanBle == null) {
                task_scanBle = new TimerTask() {// 扫描蓝牙时的定时任务
                    @Override
                    public void run() {// 通过消息更新
                        sendMessage(SCAN_DEVICE_FAIL);
                        Log.d(TAG, " 未扫描到设备，重新开始扫描 ");
                    }
                };
                if (timer_scanBle == null) {
                    timer_scanBle = new Timer();
                }
                timer_scanBle.schedule(task_scanBle, 20 * 1000, 12 * 1000);// 执行心跳包任务
            }
        } else {
            Log.d(TAG, "蓝牙已连接 ");
        }
    }

    /**
     * 停止重复扫描
     */
    private void stopScanBle() {
        if (task_scanBle != null) {
            task_scanBle.cancel();
            task_scanBle = null;
        }
        if (timer_scanBle != null) {
            timer_scanBle.cancel();
            timer_scanBle = null;
        }
        Log.i(TAG, "停止重复扫描");
    }


    private void setNetWork() {
        boolean bNetworkSetted = this.getSharedPreferences("TXDeviceSDK", 0).getBoolean("NetworkSetted", false);
        SharedPreferences sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
        String networkSettingMode = sharedPreferences.getString("NetworkSettingMode", "");
        if ("".equals(networkSettingMode) && bNetworkSetted == false) {
            SharedPreferences mySharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString("NetworkSettingMode", "true");
            editor.commit();
        } else if ("true".equals(networkSettingMode)) {

        }
    }

    /**
     * 注册QQ物联回调
     */
    private void initQQReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TXDeviceService.OnEraseAllBinders);
        filter.addAction(TXDeviceService.wifisetting);
        filter.addAction(DoorLock.DoorLockStatusChange);
        filter.addAction(DoorLock.DoorLockOpenDoor);
        filter.addAction(TXDeviceService.voicereceive);
        filter.addAction(TXDeviceService.isconnected);
        filter.addAction(TXDeviceService.BinderListChange);
        filter.addAction(TXDeviceService.OnEraseAllBinders);
        mNotifyReceiver = new NotifyReceiverQQ(MainActivity.this, mAdapter, iv_bind, dialog);
        registerReceiver(mNotifyReceiver, filter);
        mNotifyReceiver.setmCallBack(MainActivity.this);
    }

    /**
     * 使用定时器,每隔5秒获得一次信号强度值
     */
    @SuppressLint("WifiManagerLeak")
    private void getRssi() {
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);//获得WifiManager
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //获取网络连接状态
                /**if (!isNetworkAvailable(MainActivity.this)) {//无可用网络
                 Message message = new Message();
                 message.what = 6;
                 mHandler.sendMessage(message);
                 } else {
                 Message message = new Message();
                 message.what = 7;
                 mHandler.sendMessage(message);
                 }*/
                switch (NetWork.getCurrentNetType(MainActivity.this)) {
                    case NETWORK_TYPE_WIFI:
                        wifiInfo = wifiManager.getConnectionInfo();
                        //获得信号强度值
                        level = wifiInfo.getRssi();
                        //根据获得的信号强度发送信息
                        if (level <= 0 && level >= -50) {
                            Message msg = new Message();
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                        } else if (level < -50 && level >= -70) {
                            Message msg = new Message();
                            msg.what = 2;
                            mHandler.sendMessage(msg);
                        } else if (level < -70 && level >= -80) {
                            Message msg = new Message();
                            msg.what = 3;
                            mHandler.sendMessage(msg);
                        } else if (level < -80 && level >= -100) {
                            Message msg = new Message();
                            msg.what = 4;
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = 5;
                            mHandler.sendMessage(msg);
                        }
                        break;
                    case NETWOKR_TYPE_ETHERNET:
                        Message msg = new Message();
                        msg.what = 11;
                        mHandler.sendMessage(msg);
                        break;
                }

            }

        }, 1000, 5000);
    }

    /**
     * 网络获得轮播背景图片数据
     */
    private void getBgBanners() {
        XUtilsNetwork.getInstance().getBgBanners(new NetworkCallBack() {//网络请求
            // *网络获得轮播背景图片数据
            @Override
            public void onSuccess(Object o) {
                Gson gson = new Gson();
                banner = gson.fromJson(o.toString(), Banner.class);
                bgAdapter = new Bg_Adapter(MainActivity.this, banner.getData());
                viewPager.setAdapter(bgAdapter);
                viewPager.setCycle(true);
                //viewPager.setSwipeScrollDurationFactor(2000);//设置ViewPager滑动动画间隔时间的倍率，达到减慢动画或改变动画速度的效果
                viewPager.setInterval(10000);//设置自动滚动的间隔时间，单位为毫秒
                viewPager.startAutoScroll();
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    /**
     * 获取系统相关音量
     */
    private void setAutioVolume() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //通话音量
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        Log.d("VIOCE_CALL", "max : " + max + " current : " + current);
        //系统音量
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        Log.d("SYSTEM", "max : " + max + " current : " + current);
        //铃声音量
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        Log.d("RING", "max : " + max + " current : " + current);
        //音乐音量
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("MUSIC", "max : " + max + " current : " + current);
        //提示声音音量
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        current = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        Log.d("ALARM", "max : " + max + " current : " + current);
    }

    /**
     * 生成一个可以绑定设备的二维码，qq物联绑定的二维码
     */
    private void getQR() {
        Bitmap bitmap = null;
        try {
            //生成一个可以绑定设备的二维码
            Log.d("mainactivity", GetUserInfo.getSn(this));
            bitmap = Zxing.createQRImage("http://iot.qq.com/add?pid=1700003316&sn=" + GetUserInfo.getSn(this), 200, 200, null);
            if (bitmap == null) {
                options = new DisplayImageOptions.Builder()
                        .showImageOnFail(R.mipmap.fail)
                        .showImageOnLoading(R.mipmap.loading)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.ARGB_8888)
                        .build();

                BaseApplication.getApplication().getImageLoader().displayImage("http://www.tyjdtzjc.cn/resource/kindeditor/attached/image/20150831/20150831021658_90595.png", imageView, options);
                Log.i("xiao_", "未生成QQ二维码");
            } else {
                imageView.setImageBitmap(bitmap);
                Log.i("xiao_", "生成二维码");
            }
        } catch (Exception e) {
            Log.i("xiao_", "生成QQ二维码出错");
            if (bitmap == null) {
                options = new DisplayImageOptions.Builder()
                        .showImageOnFail(R.mipmap.fail)
                        .showImageOnLoading(R.mipmap.loading)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.ARGB_8888)
                        .build();

                BaseApplication.getApplication().getImageLoader().displayImage("http://www.tyjdtzjc.cn/resource/kindeditor/attached/image/20150831/20150831021658_90595.png", imageView, options);
            }
        }
    }

    /**
     * 初始化系统服务类
     */
    protected void initServer() {
        Log.i("xiao_", "开始初始化服务");
        Intent i = new Intent(MainActivity.this, MainService.class);
        bindService(i, connection, Service.BIND_AUTO_CREATE);

        Intent startIntent = new Intent(MainActivity.this, TXDeviceService.class);
        //startService(startIntent);

         /*Intent i = new Intent(this, SpeechService.class);
        startService(i);*/

        Intent dlIntent = new Intent(MainActivity.this, DoorLock.class);
        startService(dlIntent);
    }

    protected void initScreen() {
        //callLayout=(LinearLayout) findViewById(R.id.call_pane);
        //guestLayout=(LinearLayout) findViewById(R.id.guest_pane);
        headPaneTextView = (TextView) findViewById(R.id.header_pane);
        videoLayout = (LinearLayout) findViewById(R.id.ll_video);

//        videoPane = (LinearLayout) findViewById(R.id.video_pane);
//        imagePane = (LinearLayout) findViewById(R.id.image_pane);
//        remoteLayout = (LinearLayout) findViewById(R.id.ll_remote);

        setTextView(R.id.tv_community, MainService.communityName);
        setTextView(R.id.tv_lock, MainService.lockName);
    }

    /**
     * 检测锁状态
     */
    private void startClockRefresh() {
        clockRefreshThread = new Thread() {
            public void run() {
                try {
                    setNewTime();
                    while (true) {
                        sleep(1000 * 60); //等待指定的一个等待时间
                        if (!isInterrupted()) { //检查线程没有被停止
                            setNewTime();
                        }
                    }
                } catch (InterruptedException e) {
                }
                clockRefreshThread = null;
            }
        };
        clockRefreshThread.start();
    }

    private void setNewTime() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("E");
                String dayStr = dateFormat.format(now);
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = dateFormat.format(now);
                dateFormat = new SimpleDateFormat("HH:mm");
                String timeStr = dateFormat.format(now);

                setTextView(R.id.tv_day, dayStr);
                setTextView(R.id.tv_date, dateStr);
                setTextView(R.id.tv_time, timeStr);
            }
        });
    }

    protected void initAutoCamera() {
        Log.v("MainActivity", "initAutoCamera-->");
        autoCameraSurfaceView = (SurfaceView) findViewById(R.id.autoCameraSurfaceview);
        autoCameraHolder = autoCameraSurfaceView.getHolder();
        autoCameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    protected void initAdvertiseHandler() {
        if (advertiseHandler == null) {
            advertiseHandler = new AdvertiseHandler();
        }
        videoView = (SurfaceView) findViewById(R.id.surface_view);
        imageView = (ImageView) findViewById(R.id.image_view);
        Log.v("UpdateAdvertise", "------>start Update Advertise<------");
        advertiseHandler.init(videoView, imageView);
        adverErrorCallBack = new AdverErrorCallBack() {
            @Override
            public void ErrorAdver() {
                imageView.setVisibility(View.VISIBLE);
            }
        };
    }

    private void initVoiceHandler() {
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        keyVoiceIndex = soundPool.load(this, R.raw.key, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
    }

    protected void initVoiceVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);
        initVoiceVolume(audioManager, AudioManager.STREAM_MUSIC, DeviceConfig.VOLUME_STREAM_MUSIC);
        initVoiceVolume(audioManager, AudioManager.STREAM_RING, DeviceConfig.VOLUME_STREAM_RING);
        initVoiceVolume(audioManager, AudioManager.STREAM_SYSTEM, DeviceConfig.VOLUME_STREAM_SYSTEM);
        initVoiceVolume(audioManager, AudioManager.STREAM_VOICE_CALL, DeviceConfig.VOLUME_STREAM_VOICE_CALL);
    }

    protected void initVoiceVolume(AudioManager audioManager, int type, int value) {
        int thisValue = audioManager.getStreamMaxVolume(type);
        thisValue = thisValue * value / 10;
        audioManager.setStreamVolume(type, thisValue, AudioManager.FLAG_PLAY_SOUND);
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_RTC_NEWCALL) {
                    onRtcConnected();
                } else if (msg.what == MSG_RTC_ONVIDEO) {
                    //onRtcVideoOn();
                    onRtcVideoOn();
                } else if (msg.what == MSG_RTC_DISCONNECT) {
                    onRtcDisconnect();
                    //人脸识别对比
                    handler.sendEmptyMessage(START_FACE_CHECK);
                } else if (msg.what == MSG_PASSWORD_CHECK) {
                    onPasswordCheck((Integer) msg.obj);
                } else if (msg.what == MSG_LOCK_OPENED) {//门开了
                    onLockOpened();
                    final Dialog weituoDialog = DialogUtil.showBottomDialog(MainActivity.this);
                    final TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {      // UI thread
                                @Override
                                public void run() {
                                    weituoDialog.dismiss();
                                }
                            });
                        }
                    };
                    timer.schedule(task, 500, 5000);
                } else if (msg.what == MSG_CALLMEMBER_ERROR) {
                    onCallMemberError(msg.what);
                } else if (msg.what == MSG_CALLMEMBER_SERVER_ERROR) {
                    onCallMemberError(msg.what);
                } else if (msg.what == MSG_CALLMEMBER_NO_ONLINE) {
                    onCallMemberError(msg.what);
                } else if (msg.what == MSG_CALLMEMBER_TIMEOUT) {
                    onCallMemberError(msg.what);
                } else if (msg.what == MSG_CALLMEMBER_TIMEOUT_AND_TRY_DIRECT) {
                    Utils.DisplayToast(MainActivity.this, "可视对讲无法拨通，尝试直拨电话");
                    setCurrentStatus(DIRECT_CALLING_TRY_MODE);
                } else if (msg.what == MSG_CALLMEMBER_DIRECT_TIMEOUT) {
                    onCallMemberError(msg.what);
                } else if (msg.what == MSG_CALLMEMBER_DIRECT_DIALING) {
                    Utils.DisplayToast(MainActivity.this, "开始直拨电话");
                    setCurrentStatus(DIRECT_CALLING_MODE);
                } else if (msg.what == MSG_CALLMEMBER_DIRECT_SUCCESS) {
                    Utils.DisplayToast(MainActivity.this, "电话已接通，请让对方按#号键开门");
                    onCallDirectlyBegin();
                } else if (msg.what == MSG_CALLMEMBER_DIRECT_FAILED) {
                    Utils.DisplayToast(MainActivity.this, "电话未能接通，重试中..");
                } else if (msg.what == MSG_CALLMEMBER_DIRECT_COMPLETE) {
                    onCallDirectlyComplete();
                } else if (msg.what == MSG_CONNECT_ERROR) {
                    onConnectionError();
                } else if (msg.what == MSG_CONNECT_SUCCESS) {
                    onConnectionSuccess();
                } else if (msg.what == ON_YUNTONGXUN_INIT_ERROR) {
                    Utils.DisplayToast(MainActivity.this, "直拨电话初始化异常");
                } else if (msg.what == ON_YUNTONGXUN_LOGIN_SUCCESS) {
                    Utils.DisplayToast(MainActivity.this, "直拨电话服务器连接成功");
                } else if (msg.what == ON_YUNTONGXUN_LOGIN_FAIL) {
                    Utils.DisplayToast(MainActivity.this, "直拨电话服务器连接失败");
                } else if (msg.what == MSG_CANCEL_CALL_COMPLETE) {
                    setCurrentStatus(CALL_MODE);
                } else if (msg.what == MSG_ADVERTISE_REFRESH) {
                    onAdvertiseRefresh(msg.obj);
                    Log.d(TAG, "UpdateAdvertise: 7");
                } else if (msg.what == MSG_ADVERTISE_IMAGE) {
                    onAdvertiseImageChange(msg.obj);
                } else if (msg.what == MSG_INVALID_CARD) {
                    Utils.DisplayToast(MainActivity.this, "无效房卡");
                } else if (msg.what == MainService.MSG_ASSEMBLE_KEY) {
                    int keyCode = (Integer) msg.obj;
                    onKeyDown(keyCode);
                } else if (msg.what == MSG_CHECK_BLOCKNO) {
                    blockId = (Integer) msg.obj;
                    onCheckBlockNo();
                } else if (msg.what == MSG_FINGER_CHECK) {
                    boolean result = (Boolean) msg.obj;
                    //onFingerCheck(result);
                } else if (msg.what == MSG_REFRESH_DATA) {
                    onFreshData((String) msg.obj);
                } else if (msg.what == MSG_REFRESH_COMMUNITYNAME) {
                    onFreshCommunityName((String) msg.obj);
                } else if (msg.what == MSG_REFRESH_LOCKNAME) {
                    onFreshLockName((String) msg.obj);
                } else if (msg.what == MSG_INPUT_CARDINFO_SUCCEED) {//录入成功
                    rl_nfc.setVisibility(View.GONE);
                    nfcFlag = false;
                    showToast("录入成功");
                } else if (msg.what == MSG_INPUT_CARDINFO_REPETITION) {//重复录入
                    showToast("重复录入");
                } else if (msg.what == MSG_INPUT_CARDINFO_FAIL) {
                    showToast("录入失败");
                } else if (msg.what == MSG_INPUT_CARDINFO) {
                    String obj = (String) msg.obj;
                    tv_message.setText(obj);
                } else if (msg.what == MSG_INSTALL_SUCCEED) {
                    String fileName = (String) msg.obj;
                    final String filePath = fileName.replace("/storage", "");
                    Log.i(TAG, "UpdateService:" + filePath);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String cmd = "pm install -r " + filePath;
                            ShellUtils.execCommand(cmd,false);
                        }
                    }).start();
//                    ShellUtils shellUtils = new ShellUtils();
//                    shellUtils.run("pm -r install " + filePath, 5000);
                    //hwservice.execRootCommand("pm -r install /sdcard/LockaxialQQ.a.2.apk");
                    Log.i(TAG, "UpdateService:" + filePath);

                    /************xiaozd add********************/
                } else if (msg.what == InitActivity.MSG_LOGIN) {
                    Log.i("xiao_", "登录成功");
                    if (msg.obj != null) {
                        JSONObject result = (JSONObject) msg.obj;
                        try {
                            int code = result.getInt("code");
                            if (code == 0) {//登录成功
                                //初始化token
                                sendMainMessager(MainService.MSG_REGISTER, null);
                                //初始化社区信息
                                JSONObject user = result.getJSONObject("user");
                                setCommunityName(user.getString("communityName"));
                                setLockName(user.getString("lockName"));
                            } else if (code == 1) { //登录失败,MAC地址不存在服务器
                                //显示MAC地址并提示添加
                                showMacaddress(result.getString("mac"));
                            }
                        } catch (Exception e) {

                        }
                    }
                } else if (msg.what == MainService.MSG_REGISTER_ACTIVITY_DIAL) {
                    sendMainMessager(MainService.REGISTER_ACTIVITY_DIAL, null);
                    //开始读卡
                    enableReaderMode();
                    Log.i("xiao_", "收到消息MSG_REGISTER_ACTIVITY_DIAL -》开始MainActivity初始化-》》》可以读卡");
                    //人脸识别对比
                    handler.sendEmptyMessage(START_FACE_CHECK);
                } else if (msg.what == MainService.MSG_LOADLOCAL_DATA) {
                    //加载本地数据显示到界面
                    setCommunityName(MainService.communityName);
                    setLockName(MainService.lockName);
                }else if(msg.what == START_FACE_CHECK){
                    if (faceHandler != null && mCamerarelease) {
                        HttpApi.i("相机释放成功，开启人脸识别");
                        handler.removeMessages(START_FACE_CHECK);
                        faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 1000);
                    }else{
                        HttpApi.i("相机未释放，继续等待");
                        handler.sendEmptyMessageDelayed(START_FACE_CHECK,200);
                    }
                }else if(msg.what == RE_SYNC_SYSTEMTIME){
                    initSystemtime();
                }
            }
        };
        dialMessenger = new Messenger(handler);
    }

    private void onFreshLockName(String lockName) {
        if (lockName != null) {
            setLockName(lockName);
        }
    }

    private void onFreshCommunityName(String communityName) {
        if (communityName != null) {
            setCommunityName(communityName);
        }
    }

    private void onFreshData(String type) {
        if ("card".equals(type)) {
            Utils.DisplayToast(MainActivity.this, "更新卡数据");
        } else if ("finger".equals(type)) {
            Utils.DisplayToast(MainActivity.this, "更新指纹数据");
        }
    }

    /**
     * 检查楼栋编号
     */
    private void onCheckBlockNo() {
        if (blockId == 0) {
            blockNo = "";
            setDialValue(blockNo);
            Utils.DisplayToast(MainActivity.this, "楼栋编号不存在");
            return;
        }
        if (blockId < 0) {
            blockNo = "";
            blockId = 0;
            setDialValue(blockNo);
            Utils.DisplayToast(MainActivity.this, "获取楼栋数据失败，请联系管理处");
        } else {
            setDialValue(blockNo);
        }
    }

    public void onRtcConnected() {
        setCurrentStatus(ONVIDEO_MODE);
        setDialValue("");
        advertiseHandler.pause(adverErrorCallBack);
    }

    public void onRtcVideoOn() {
        setDialValue("正在和" + blockNo + "视频通话");
        initVideoViews();
        if(mCamerarelease){
            buildVideo();
        }else{
            cameraHandler.sendEmptyMessageDelayed(0x01,200);
        }
    }

    private void buildVideo(){
        if(MainService.callConnection!=null){
            MainService.callConnection.buildVideo(remoteView);//此处接听过快的会导致崩溃
        }
        // java.lang.RuntimeException: Fail to connect to camera service
        videoLayout.setVisibility(View.VISIBLE);
        setVideoSurfaceVisibility(View.VISIBLE);
    }

    public void onRtcDisconnect() {
        blockNo = "";
        setDialValue(blockNo);
        setCurrentStatus(CALL_MODE);
        advertiseHandler.start(adverErrorCallBack);
        //callLayout.setVisibility(View.VISIBLE);
        //guestLayout.setVisibility(View.INVISIBLE);
        videoLayout.setVisibility(View.INVISIBLE);
        setVideoSurfaceVisibility(View.INVISIBLE);
    }

    private void onPasswordCheck(int code) {
        setCurrentStatus(PASSWORD_MODE);
        setTempkeyValue("");
        if (code == 0) {
            Utils.DisplayToast(MainActivity.this, "您输入的密码验证成功");
        } else {
            if (code == 1) {
                Utils.DisplayToast(MainActivity.this, "您输入的密码不存在");
            } else if (code == 2) {
                Utils.DisplayToast(MainActivity.this, "您输入的密码已经过期");
            } else if (code < 0) {
                Utils.DisplayToast(MainActivity.this, "密码验证不成功，请联系管理员");
            }
        }
        HttpApi.i("密码验证成功，启动人脸");
        //开启人脸
        handler.sendEmptyMessage(START_FACE_CHECK);
    }

    private void onLockOpened() {
        blockNo = "";
        setDialValue("");
        setTempkeyValue("");
        if (currentStatus != PASSWORD_MODE && currentStatus != PASSWORD_CHECKING_MODE) {
            setCurrentStatus(CALL_MODE);
        }
        Utils.DisplayToast(MainActivity.this, "门开了");

        identification = false;
        if (faceHandler != null) {
            faceHandler.removeMessages(-1);
            faceHandler.sendEmptyMessageDelayed(-1, 10 * 1000);
        }
    }

    protected void onCallMemberError(int reason) {
        blockNo = "";
        setDialValue("");
        setCurrentStatus(CALL_MODE);
        if (reason == MSG_CALLMEMBER_ERROR) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号错误或者无注册用户");
            Log.v("MainActivity", "无用户取消呼叫");
            clearImageUuidAvaible(lastImageUuid);
        } else if (reason == MSG_CALLMEMBER_NO_ONLINE) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号无人在线");
        } else if (reason == MSG_CALLMEMBER_TIMEOUT) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号无人应答");
        } else if (reason == MSG_CALLMEMBER_DIRECT_TIMEOUT) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间直拨电话无人应答");
        } else if (reason == MSG_CALLMEMBER_SERVER_ERROR) {
            Utils.DisplayToast(MainActivity.this, "无法从服务器获取住户信息，请联系管理处");
        }
        handler.sendEmptyMessage(START_FACE_CHECK);
    }

    public void onCallDirectlyBegin() {
        setCurrentStatus(DIRECT_MODE);
        advertiseHandler.pause(adverErrorCallBack);
    }

    public void onCallDirectlyComplete() {
        setCurrentStatus(CALL_MODE);
        blockNo = "";
        setDialValue(blockNo);
        advertiseHandler.start(adverErrorCallBack);
    }

    private void onConnectionError() {
        setCurrentStatus(ERROR_MODE);
        setTextView(R.id.header_pane, "可视对讲设备异常，网络连接已断开");
        headPaneTextView.setVisibility(View.VISIBLE);
    }

    private void onConnectionSuccess() {
        if (currentStatus == ERROR_MODE) {
            initDialStatus();
            setTextView(R.id.header_pane, "");
            headPaneTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void onAdvertiseRefresh(Object obj) {
        rows = (JSONArray) obj;
        Log.d(TAG, "UpdateAdvertise: 8");
        advertiseHandler.initData(rows, dialMessenger, (currentStatus == ONVIDEO_MODE), adverErrorCallBack);
    }

    protected void onAdvertiseImageChange(Object obj) {
        String source = (String) obj;
        source = HttpUtils.getLocalFileFromUrl(source);
        Bitmap bm = BitmapFactory.decodeFile(source);
        imageView.setImageBitmap(bm);
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        Log.e("MainActivity", "===" + str);
    }

    private void callInput(int key) {
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            if (blockId == 0) {
                if (blockNo.length() < DeviceConfig.BLOCK_LENGTH) {
                    blockNo = blockNo + key;
                    setDialValue(blockNo);
                    Log.e("blockNo", "1===" + blockNo);
                }
                if (blockNo.length() == DeviceConfig.BLOCK_NO_LENGTH) {
                    setDialValue(blockNo);
                    Message message = Message.obtain();
                    message.what = MainService.MSG_CHECK_BLOCKNO;
                    message.obj = blockNo;
                    Log.e("blockNo", "2===" + blockNo);
                    try {
                        serviceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                unitNoInput(key);
            }
        } else {
            unitNoInput(key);
        }
    }

    /**
     * 自动呼叫
     *
     * @param key
     */
    private void unitNoInput(int key) {
        blockNo = blockNo + key;
        setDialValue(blockNo);
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            if (blockNo.length() == DeviceConfig.BLOCK_LENGTH) {
                startDialing(blockNo);
            }
        } else {
            if (blockNo.length() == DeviceConfig.UNIT_NO_LENGTH) {
                startDialing(blockNo);
            }
        }
    }

    /**
     * 开始呼叫
     */
    private void startDialing(final String num) {
        //呼叫前，确认摄像头不被占用
        if (faceHandler != null) {
            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 0);
        }
        delayDialing(num);
    }


    private void delayDialing(String num){
        Log.v(FACE_TAG, "开始呼叫1" + num);
        setCurrentStatus(CALLING_MODE);
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            blockId = 0;
            setDialStatus("请输入楼栋编号");
        }
        Log.v(FACE_TAG, "开始呼叫2" + num);
        takePicture(num, true, MainActivity.this);//开启拍照，并开始呼叫
    }

    private int convertKeyCode(int keyCode) {
        int value = -1;
        if ((keyCode == KeyEvent.KEYCODE_0)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 0);
            value = 0;
        } else if ((keyCode == KeyEvent.KEYCODE_1)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 1);
            value = 1;
        } else if ((keyCode == KeyEvent.KEYCODE_2)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 2);
            value = 2;
        } else if ((keyCode == KeyEvent.KEYCODE_3)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 3);
            value = 3;
        } else if ((keyCode == KeyEvent.KEYCODE_4)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 4);
            value = 4;
        } else if ((keyCode == KeyEvent.KEYCODE_5)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 5);
            value = 5;
        } else if ((keyCode == KeyEvent.KEYCODE_6)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 6);
            value = 6;
        } else if ((keyCode == KeyEvent.KEYCODE_7)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 7);
            value = 7;
        } else if ((keyCode == KeyEvent.KEYCODE_8)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 8);
            value = 8;
        } else if ((keyCode == KeyEvent.KEYCODE_9)) {
            SoundPoolUtil.getSoundPoolUtil().loadVoice(MainActivity.this, 9);
            value = 9;
        }
        return value;
    }

    private void passwordInput(int key) {
        guestPassword = guestPassword + key;
        setTempkeyValue(guestPassword);
        if (guestPassword.length() == 6) {
            if(faceHandler!=null){
                faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE,0);
            }
            checkPassword();
        }
    }

    private void checkPassword() {
        setCurrentStatus(PASSWORD_CHECKING_MODE);
        String thisPassword = guestPassword;
        guestPassword = "";
        takePicture(thisPassword, false, this);
    }

    private void initPasswordStatus() {
        stopPasswordTimeoutChecking();
        setDialStatus("请输入访客密码");
        //callLayout.setVisibility(View.INVISIBLE);
        //guestLayout.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.INVISIBLE);
        setCurrentStatus(PASSWORD_MODE);
        guestPassword = "";
        setTempkeyValue(guestPassword);
        startTimeoutChecking();
    }

    private void startTimeoutChecking() {
        passwordTimeoutThread = new Thread() {
            public void run() {
                try {
                    sleep(DeviceConfig.PASSWORD_WAIT_TIME); //等待指定的一个等待时间
                    if (!isInterrupted()) { //检查线程没有被停止
                        if (currentStatus == PASSWORD_MODE) { //如果现在是密码输入状态
                            if (TextUtils.isEmpty(guestPassword)) { //如果密码一直是空白的
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        initDialStatus();
                                    }
                                });
                                stopPasswordTimeoutChecking();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                }
                passwordTimeoutThread = null;
            }
        };
        passwordTimeoutThread.start();
    }

    protected void stopPasswordTimeoutChecking() {
        if (passwordTimeoutThread != null) {
            passwordTimeoutThread.interrupt();
            passwordTimeoutThread = null;
        }
    }

    private void passwordInput() {
        guestPassword = backKey(guestPassword);
        setTempkeyValue(guestPassword);
    }

    private void callInput() {
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            if (blockId > 0) {
                if (blockNo.equals("")) {
                    blockId = 0;
                    blockNo = backKey(blockNo);
                    setDialStatus("请输入楼栋编号");
                    setDialValue(blockNo);
                } else {
                    blockNo = backKey(blockNo);
                    setDialValue(blockNo);
                }
            } else {
                blockNo = backKey(blockNo);
                setDialValue(blockNo);
            }
        } else {
            blockNo = backKey(blockNo);
            setDialValue(blockNo);
        }
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

    /**
     * 录入卡片
     */
    private void receiveCard() {
        String bla = et_blackno.getText().toString();
        String uin = et_unitno.getText().toString();
        Log.d(TAG, "receiveCard: bla=" + bla);
        Log.d(TAG, "receiveCard: uni=" + uin);
        try {
            String url = DeviceConfig.SERVER_URL + "/app/rfid/bind?cardNo=" + this.cardId;
            url = url + "&communityId=" + communityId;
            url = url + "&blockNo=" + bla;
            url = url + "&unitNo=" + uin;
            url = url + "&userId=" + lockId;
            Log.d(TAG, "login: url=" + url);
            try {
                URL thisUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) thisUrl.openConnection();
                conn.setRequestMethod("GET");
                Log.d(TAG, "login: token=" + httpServerToken);
                if (httpServerToken != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + httpServerToken);
                }
                Log.d(TAG, "receiveCard: token=" + httpServerToken);
                conn.setConnectTimeout(5000);
                int code = conn.getResponseCode();
                Log.d(TAG, "login: code=" + code);
                if (code == 200) {
                    InputStream is = conn.getInputStream();
                    String result = HttpUtils.readMyInputStream(is);
                    Log.d(TAG, "login: result=" + result);
                    JSONObject resultObj = Ajax.getJSONObject(result);
                    int resultCode = resultObj.getInt("code");
                    Log.d(TAG, "login: code=" + resultCode);
                    Message message = Message.obtain();
                    if (resultCode == 0) {
                        message.what = MSG_INPUT_CARDINFO_SUCCEED;    //录入成功
                    } else if (resultCode == 2) {
                        message.what = MSG_INPUT_CARDINFO_REPETITION; //重复录入
                    } else {
                        message.what = MSG_INPUT_CARDINFO_FAIL; //录入失败
                    }
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
    }

    private void callInput(int key, int id) {
        blockNo = blockNo + key;
        setTextValue(id, blockNo);
    }

    void setTextValue(final int id, String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(id, thisValue);
            }
        });
    }

    private void onKeyDown(int keyCode) {
        if (nfcFlag) {
            inputCardInfo(keyCode);//录入卡片信息
        } else {
            int key = convertKeyCode(keyCode);
            if (currentStatus == CALL_MODE || currentStatus == PASSWORD_MODE) {
                if (key >= 0) {
                    if (currentStatus == CALL_MODE) {
                        callInput(key);
                    } else {
                        passwordInput(key);//密码开门
                    }
                } else if (keyCode == KeyEvent.KEYCODE_POUND || keyCode == DeviceConfig.DEVICE_KEYCODE_POUND) {
                    if (currentStatus == CALL_MODE) {//呼叫模式下，按确认键
                        initPasswordStatus();
                    } else {
                        initDialStatus();
                    }
                } else if (keyCode == KeyEvent.KEYCODE_STAR || keyCode == DeviceConfig.DEVICE_KEYCODE_STAR) {
                    if (currentStatus == CALL_MODE) {
                        callInput();
                    } else {
                        passwordInput();
                    }
                    String str = tv_input_text.getText().toString();
                    if (str == null || str.equals("")) {
                        //跳转到登录界面
                        Intent intent = new Intent(this, InputCardInfoActivity.class);
                        startActivityForResult(intent, INPUT_CARDINFO_REQUESTCODE);
                    }
                }
            } else if (currentStatus == ERROR_MODE) {
            } else if (currentStatus == CALLING_MODE) {
                if (keyCode == KeyEvent.KEYCODE_STAR || keyCode == DeviceConfig.DEVICE_KEYCODE_STAR) {
                    startCancelCall();//取消呼叫
                }
            } else if (currentStatus == ONVIDEO_MODE) {
                if (keyCode == KeyEvent.KEYCODE_STAR || keyCode == DeviceConfig.DEVICE_KEYCODE_STAR) {
                    startDisconnectVideo();
                }
            } else if (currentStatus == DIRECT_CALLING_MODE) {
                if (keyCode == KeyEvent.KEYCODE_STAR || keyCode == DeviceConfig.DEVICE_KEYCODE_STAR) {
                    resetDial();
                    startCancelDirectCall();
                }
            } else if (currentStatus == DIRECT_CALLING_TRY_MODE) {
                if (keyCode == KeyEvent.KEYCODE_STAR || keyCode == DeviceConfig.DEVICE_KEYCODE_STAR) {
                    resetDial();
                    startCancelDirectCall();

                }
            } else if (currentStatus == DIRECT_MODE) {
                if (keyCode == KeyEvent.KEYCODE_STAR || keyCode == DeviceConfig.DEVICE_KEYCODE_STAR) {
                    resetDial();
                    startDisconnectDirectCall();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(FACE_TAG, "onActivityResult-->" + requestCode + "/" + resultCode);
        switch (requestCode) {
            case INPUT_CARDINFO_REQUESTCODE:
                switch (resultCode) {
                    case INPUT_CARDINFO_RESULTCODE://从录卡登录页面返回回来
                        //imageView.setVisibility(View.VISIBLE);
                        rl_nfc.setVisibility(View.VISIBLE);
                        tv_message.setText(nfcMessage);
                        nfcFlag = true;
                        cardId = null;
                        isFlag = true;
                        et_blackno.setFocusable(true);
                        et_blackno.setFocusableInTouchMode(true);
                        et_blackno.requestFocus();
                        et_blackno.setText("");
                        et_unitno.setText("");
                        break;
                    case INPUT_FACE_REQUESTCODE:
                        faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 100);
                        faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_INPUT, 100);
                        break;
                    default:
                        //imageView.setVisibility(View.VISIBLE);
                        cardId = null;
                        showToast("取消登录");
                        break;
                }
                break;
            case INPUT_SYSTEMSET_REQUESTCODE:
                //imageView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 录入卡片信息
     *
     * @param keyCode
     */
    private void inputCardInfo(int keyCode) {
        String black = et_blackno.getText().toString();
        String unit = et_unitno.getText().toString();
        if (keyCode == KeyEvent.KEYCODE_STAR || keyCode == DeviceConfig.DEVICE_KEYCODE_STAR) {//取消
            if (!isFlag && (unit == null || unit.equals(""))) {//返回到房屋输入框
                et_blackno.setFocusable(true);
                et_blackno.setFocusableInTouchMode(true);
                et_blackno.requestFocus();
            }
            if (isFlag && !(black == null || black.equals(""))) {//删除输入的房屋数字
                blockNo = backKey(blockNo);
                setTextValue(R.id.et_blockno, blockNo);
            }
            if (!isFlag && !(unit == null || unit.equals(""))) {//删除输入的门数字
                blockNo = backKey(blockNo);
                setTextValue(R.id.et_unitno, blockNo);
            }
            if (isFlag && (black == null || black.equals(""))) {
                rl_nfc.setVisibility(View.GONE);
                nfcFlag = false;
                initDialStatus();
            }
        } else if (keyCode == KeyEvent.KEYCODE_POUND || keyCode == DeviceConfig.DEVICE_KEYCODE_POUND) {//录入卡片
            if (TextUtils.isEmpty(cardId)) {
                showToast("未检测到卡片信息");
            } else {
                if (isFlag) {//切换edittext焦点到房屋编号输入框
                    if (!blockNo.equals("")) {
                        blockNo = "";
                    }
                    et_unitno.setFocusable(true);
                    et_unitno.setFocusableInTouchMode(true);
                    et_unitno.requestFocus();
                } else {
                    if (TextUtils.isEmpty(black) || TextUtils.isEmpty(unit)) {
                        showToast("楼栋编号或者房屋编号不能为空");
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            receiveCard();
                        }
                    }.start();
                }
            }
        } else {
            int key = convertKeyCode(keyCode);
            if (key >= 0) {
                if (isFlag) {
                    callInput(key, R.id.et_blockno);
                } else {
                    callInput(key, R.id.et_unitno);
                }
            }
        }
    }

    private void startDisconnectDirectCall() {
        Message message = Message.obtain();
        message.what = MainService.MSG_DISCONNECT_DIRECT;
        try {
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startCancelDirectCall() {
        Message message = Message.obtain();
        message.what = MainService.MSG_CANCEL_DIRECT;
        try {
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startDisconnectVideo() {
        Message message = Message.obtain();
        message.what = MainService.MSG_DISCONNECT_VIEDO;
        try {
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void startCancelCall() {
        new Thread() {
            public void run() {
                stopCallCamera();
                try {
                    sleep(1000);
                } catch (Exception e) {
                }
                doCancelCall();
                handler.sendEmptyMessage(START_FACE_CHECK);
                try {
                    sleep(1000);
                } catch (Exception e) {
                }
                toast("您已经取消拨号");
                resetDial();
            }
        }.start();
    }

    protected void stopCallCamera() {
        setDialValue("正在取消拨号");
        setCurrentStatus(CALL_CANCEL_MODE);
        clearImageUuidAvaible(lastImageUuid);
        Log.v("MainActivity", "取消拍照" + lastImageUuid);
    }

    protected void doCancelCall() {
        Message message = Message.obtain();
        message.what = MainService.MSG_CANCEL_CALL;
        try {
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected void resetDial() {
        blockNo = "";
        setDialValue(blockNo);
        setCurrentStatus(CALL_MODE);
    }

    private void setImageUuidAvaibale(String uuid) {
        Log.v("MainActivity", "加入UUID" + uuid);
        uuidMaps.put(uuid, "Y");
    }

    private void clearImageUuidAvaible(String uuid) {
        Log.v("MainActivity", "清除UUID" + uuid);
        uuidMaps.remove(uuid);
    }

    private void setCommunityName(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_community, thisValue);
            }
        });
    }

    private void setLockName(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_lock, thisValue);
            }
        });
    }

    private void initVideoViews() {
        if (localView != null) return;
        if (MainService.callConnection != null)
            localView = (SurfaceView) MainService.callConnection.createVideoView(true, this, true);
        if(localView!=null){
            localView.setVisibility(View.INVISIBLE);
            videoLayout.addView(localView);
            localView.setKeepScreenOn(true);
            localView.setZOrderMediaOverlay(true);
            localView.setZOrderOnTop(true);
        }
        if (MainService.callConnection != null)
            remoteView = (SurfaceView) MainService.callConnection.createVideoView(false, this, true);
        if(remoteView!=null){
            remoteView.setVisibility(View.INVISIBLE);
            remoteView.setKeepScreenOn(true);
            remoteView.setZOrderMediaOverlay(true);
            remoteView.setZOrderOnTop(true);
        }
        //remoteLayout.addView(remoteView);
    }

    void setVideoSurfaceVisibility(int visible) {
        if (localView != null)
            localView.setVisibility(visible);
        if (remoteView != null)
            remoteView.setVisibility(visible);
    }

    synchronized void setCurrentStatus(int status) {
        currentStatus = status;
    }
    //初始化桌面显示呼叫模式
    private void initDialStatus() {
        //callLayout.setVisibility(View.VISIBLE);
        //guestLayout.setVisibility(View.INVISIBLE);
        videoLayout.setVisibility(View.INVISIBLE);
        setCurrentStatus(CALL_MODE);
        blockNo = "";
        blockId = 0;
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            setDialStatus("请输入楼栋编号");
        } else {
            setDialStatus("请输入房屋编号");
        }
        setDialValue(blockNo);
    }

    private void setDialStatus(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_input_label, thisValue);
            }
        });
    }

    private void setDialValue(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_input_text, thisValue);
            }
        });
    }

    private void toast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Utils.DisplayToast(MainActivity.this, message);
            }
        });
    }

    private void setTempkeyValue(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_input_text, thisValue);
            }
        });
    }

    private void setTextView(int id, String txt) {
        ((TextView) findViewById(id)).setText(txt);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取Service端的Messenger
            serviceMessenger = new Messenger(service);
            netWorkFlag = NetWork.isNetworkAvailable(MainActivity.this) ? 1 : 0;
            if (netWorkFlag == 0) {
                enableReaderMode();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        rl.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                setStatusBarIcon(true);
                initSystemtime();
            }
            sendMainMessager(MainService.REGISTER_ACTIVITY_INIT, netWorkFlag == 1 ? true : false);
            initNetListen();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    /**********xiaozd add****************************/
    private int netWorkFlag = -1;
    private TextView showMacText;
    private Timer netTimer = new Timer();

    /**
     * 校时
     */
    private void initSystemtime() {
        if (NetWork.isNetworkAvailable(this)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Calendar c = HttpApi.getInstance().loadTime();
                    if (c != null) {
                        if (checkTime(c)) {
                            SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd.HHmmss");
                            final String time = d.format(c.getTime());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String cmd = "date -s '[_update_time]'";
                                    cmd = cmd.replace("[_update_time]", time);
                                    ShellUtils.CommandResult result = InstallUtil.executeCmd(cmd);
                                }
                            }).start();
                            HttpApi.e("时间更新：" + time);
                        } else {
                            HttpApi.e("系统与服务器时间差小，不更新");
                        }
                    } else {
                        HttpApi.i("获取服务器时间出错！");
                    }
                   handler.sendEmptyMessageDelayed(RE_SYNC_SYSTEMTIME,30*1000); //每30s同步一次系统时间
                }
            }).start();
        }
    }

    private boolean checkTime(Calendar c) {
        Calendar c1 = Calendar.getInstance();
        long abs = Math.abs(c.getTimeInMillis() - c1.getTimeInMillis());
        if (abs > 1 * 60 * 1000) {
            return true;
        }
        return false;
    }

    private void initNetListen() {
        netTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int s = NetWork.isNetworkAvailable(MainActivity.this) ? 1 : 0;
                if (s != netWorkFlag) {
                    if (s == 1) {
                        //关闭读卡
                        disableReaderMode();
                        //时间更新
                        initSystemtime();
                    } else {
                        //打开读卡
                        enableReaderMode();
                    }
                    sendMainMessager(MainService.MSG_UPDATE_NETWORKSTATE, s == 1 ? true : false);
                    netWorkFlag = s;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (netWorkFlag == 1) {
                                setStatusBarIcon(true);
                                rl.setVisibility(View.GONE);
                            } else {
                                setStatusBarIcon(false);
                                rl.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        }, 500, 1000);
    }

    private void setStatusBarIcon(boolean state) {
        if (state) {
            //显示
            if (wifi_image.getVisibility() == View.INVISIBLE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        wifi_image.setVisibility(View.VISIBLE);
                    }
                });
            }
            if (iv_bind.getVisibility() == View.INVISIBLE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iv_bind.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            //隐藏
            if (wifi_image.getVisibility() == View.VISIBLE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        wifi_image.setVisibility(View.INVISIBLE);
                    }
                });
            }
            if (iv_bind.getVisibility() == View.VISIBLE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iv_bind.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }

    private void showMacaddress(String mac) {
        if (showMacText != null && mac != null && mac.length() > 0) {
            showMacText.setVisibility(View.VISIBLE);
            showMacText.setText("MAC地址未注册，请添加\nMac地址：" + mac);
        }
    }

    /**
     * 通过ServiceMessenger将注册消息发送到Service中的Handler
     */
    private void sendMainMessager(int what, Object o) {
        Message message = Message.obtain();
        message.what = what;
        message.replyTo = dialMessenger;
        message.obj = o;
        try {
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * MainActivity初始化
     */
    private void sendInitDIALMessage() {
        Message message = Message.obtain();
        message.what = MainService.REGISTER_ACTIVITY_DIAL;
        message.replyTo = dialMessenger;
        try {
            //通过ServiceMessenger将注册消息发送到Service中的Handler
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化控制设备&组合设备&获取token
     */
    private void sendInitMessage() {
        Message message = Message.obtain();
        message.what = MainService.REGISTER_ACTIVITY_INIT;
        message.replyTo = dialMessenger;
        try {
            //通过ServiceMessenger将注册消息发送到Service中的Handler
            serviceMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    /**********xiaozd end****************************/

    /**
     * 开始启动拍照
     */
    protected void takePicture(final String thisValue, final boolean isCall, final TakePictureCallback callback) {
        if (currentStatus == CALLING_MODE || currentStatus == PASSWORD_CHECKING_MODE) {
            final String uuid = getUUID(); //随机生成UUID
            lastImageUuid = uuid;
            setImageUuidAvaibale(uuid);
            callback.beforeTakePickture(thisValue, isCall, uuid); //校验房间号是否存在
            new Thread() {
                public void run() {
                    final String thisUuid = uuid;
                    if (checkTakePictureAvailable(thisUuid)) {
                        doTakePicture(thisValue, isCall, uuid, callback);
                    } else {
                        Log.v("MainActivity", "取消拍照");
                    }
                }
            }.start();
        }
    }

    private synchronized void doTakePicture(final String thisValue, final boolean isCall, final String uuid, final TakePictureCallback callback) {
        HttpApi.i("开始拍照");
        mCamerarelease = false;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            camera = Camera.open();
            HttpApi.i("获取到相机open()");
        } catch (Exception e) {
            HttpApi.i("拨号拍照照片获取异常open()");
            e.printStackTrace();
        }
        if (camera == null) {
            try {
                camera = Camera.open(0);
                HttpApi.i("获取到相机open(0)");
            } catch (Exception e) {
                HttpApi.i("拨号拍照照片获取异常open(0)");
                e.printStackTrace();
            }
        }
        if (camera != null) {
            HttpApi.i("相机获取成功");
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(320, 240);
                try {
                    camera.setParameters(parameters);
                } catch (Exception err) {
                    err.printStackTrace();
                }
                camera.setPreviewDisplay(autoCameraHolder);
                camera.startPreview();
                camera.autoFocus(null);
                HttpApi.i("开始拍照");
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            HttpApi.i("拍照成功");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            final File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                            FileOutputStream outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.close();
                            if(camera!=null){
                                camera.setPreviewCallback(null) ;
                                camera.stopPreview();
                                camera.release();
                                camera = null;
                                mCamerarelease = true;
                            }
                            final String url = DeviceConfig.SERVER_URL + "/app/upload/image";
                            if (checkTakePictureAvailable(uuid)) {
                                new Thread() {
                                    public void run() {
                                        String fileUrl = null;
                                        try {
                                            HttpApi.i("开始上传照片");
                                            fileUrl = UploadUtil.uploadFile(file, url);
                                            HttpApi.i("照片上传成功");
                                        } catch (Exception e) {
                                        }
                                        if (checkTakePictureAvailable(uuid)) {
                                            callback.afterTakePickture(thisValue, fileUrl, isCall, uuid);
                                        } else {
                                            Log.v("MainActivity", "上传照片成功,但已取消");
                                        }
                                        clearImageUuidAvaible(uuid);
                                        Log.v("MainActivity", "正常清除" + uuid);
                                        try {
                                            if (file != null) {
                                                file.deleteOnExit();
                                            }
                                        } catch (Exception e) {
                                        }
                                    }
                                }.start();
                            } else {
                                Log.v("MainActivity", "拍照成功，但已取消");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if(camera!=null){
                                camera.setPreviewCallback(null) ;
                                camera.stopPreview();
                                camera.release();
                                camera = null;
                                mCamerarelease = true;
                            }
                        }
                    }
                });
            } catch (Exception e) {
                callback.afterTakePickture(thisValue, null, isCall, uuid);
                Log.v("MainActivity", "照相出异常清除UUID");
                clearImageUuidAvaible(uuid);
                if(camera!=null){
                    camera.setPreviewCallback(null) ;
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    mCamerarelease = true;
                }
            }
        }
    }

    private boolean checkTakePictureAvailable(String uuid) {
        String thisValue = uuidMaps.get(uuid);
        boolean result = false;
        if (thisValue != null && thisValue.equals("Y")) {
            result = true;
        }
        Log.v("MainActivity", "检查UUID" + uuid + result);
        return result;
    }

    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        String result = UUID.randomUUID().toString();
        return result;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            onKeyDown(keyCode);
        }
        return false;
    }

    public void eraseAllBinders(View v) {
        AlertDialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setTitle(R.string.unbind).setMessage(R.string.q_unbind_all).setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.unbind, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                TXDeviceService.eraseAllBinders();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void uploadDeviceLog(View v) {
        TXDeviceService.getInstance().uploadSDKLog();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setCurrentStatus(CALL_MODE);
    }

    protected void onResume() {
        super.onResume();
        Log.v(FACE_TAG, "MainActivity/onResume-->");
        if (faceHandler != null) {
            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 3000);
            faceHandler.sendEmptyMessageDelayed(MSG_ID_CARD_DETECT_RESTART, 1000);
        }

        Intent intent = getIntent();
        String bindnum = intent.getStringExtra("bindnmu");
        if (!"".equals(bindnum) && "havenum".equals(bindnum)) {
            iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.binder_default_head));
        } else if (!"".equals(bindnum) && "nullnum".equals(bindnum)) {
            iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.bind_offline));
        }
        if (dialog != null && dialog.isShowing()) {/*去掉呼叫中弹出框*/
            dialog.dismiss();
        }

        TXBinderInfo[] arrayBinder = TXDeviceService.getBinderList();
        if (arrayBinder != null) {
            List<TXBinderInfo> binderList = new ArrayList<TXBinderInfo>();
            for (int i = 0; i < arrayBinder.length; ++i) {
                binderList.add(arrayBinder[i]);
            }
            if (mAdapter != null) {
                mAdapter.freshBinderList(binderList);
            }
            if (binderList.size() > 0) {
                iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.binder_default_head));
            } else {
                iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.bind_offline));
            }
        }
    }


    private boolean isRestartPlay = false;



    protected void onPause() {
        super.onPause();
        Log.v(FACE_TAG, "MainActivity/onPause-->");

        //unbindService(mConn);
        //advertiseHandler.onDestroy();
        //videoView.setVisibility(View.GONE);
        advertiseHandler.pause(adverErrorCallBack);
        isRestartPlay = true;
    }





    protected void onDestroy() {
        Log.v(FACE_TAG, "MainActivity/onDestroy-->");
        try {
            isConnectBLE = false;
            stopGetDoorStarts();//停止定时获取蓝牙锁状态
            unbindService(connection);
            disableReaderMode();
            unregisterReceiver(receive);
            unregisterReceiver(mNotifyReceiver);
            unregisterReceiver(dataUpdateRecevice);
            if (netTimer != null) {
                netTimer.cancel();
                netTimer = null;
            }
            sendBroadcast(new Intent("com.android.action.display_navigationbar"));
            if (device != null) {
                device.disconnectedDevice(address);
                Log.e(TAG, "onDestroy 开始注销蓝牙服务");//绑定服务结果
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSurfaceView.setVisibility(View.GONE);
        mGLSurfaceView.setVisibility(View.GONE);
        identification = false;
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
        if (faceHandler != null) {
            faceHandler.removeCallbacksAndMessages(null);
        }
//        if (faceThread != null) {
//            faceThread.quit();
//        }

        if (mSerialPort != null) {
            mSerialPort.onDestroy();
            mSerialPort.close();
            mSerialPort = null;
        }
        if (mIdCardUtil != null) {
            mIdCardUtil.close();
        }

        super.onDestroy();
    }

    private void initAexNfcReader() {
        if (DeviceConfig.IS_NFC_AVAILABLE) {
            nfcReader = new NfcReader(this);
            //enableReaderMode(); //xiaozd add
            receive = new Receive();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_NFC_CARDINFO);//NFC读取到卡片信息
            registerReceiver(receive, intentFilter);
        }
    }

    private void enableReaderMode() {
        if (DeviceConfig.IS_NFC_AVAILABLE) {
            NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
            if (nfc != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (this instanceof NfcAdapter.ReaderCallback) {
                        if (!this.isDestroyed()) {
                            nfc.enableReaderMode(this, this, NfcReader.READER_FLAGS, null);
                        }
                    }
                }
            }
        }
    }

    private void disableReaderMode() {
        Log.i("", "禁用读卡模式");
        if (DeviceConfig.IS_NFC_AVAILABLE) {
            NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);
            if (nfc != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (!this.isDestroyed()) {
                        nfc.disableReaderMode(this);
                    }
                }
            }
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ((nfcReader != null) && (nfcReader instanceof NfcAdapter.ReaderCallback)) {
                NfcAdapter.ReaderCallback nfcReader = (NfcAdapter.ReaderCallback) this.nfcReader;
                nfcReader.onTagDiscovered(tag);
            }
        }
    }

    @Override
    public void callBackList(Parcelable[] list) {
        listTemp1 = list;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
        if(isRestartPlay){
            isRestartPlay = false;
            advertiseHandler.start(adverErrorCallBack);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(FACE_TAG, "MainActivity/onStop-->");
        if (faceHandler != null) {
            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 1000);
            faceHandler.sendEmptyMessageDelayed(MSG_ID_CARD_DETECT_PAUSE, 1000);
        }

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onAccountReceived(String account) {
        cardId = account;
        if (!nfcFlag) {
            Message message = Message.obtain();
            message.what = MainService.MSG_CARD_INCOME;
            message.obj = account;
            try {
                serviceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Message message = Message.obtain();
            message.what = MSG_INPUT_CARDINFO;
            message.obj = account;
            handler.sendMessage(message);
        }
    }


    private void showAlert(String strTitle, String strMsg) {
        // TODO Auto-generated method stub
        AlertDialog dialogError;
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(strTitle).setMessage(strMsg).setPositiveButton("取消", null).setNegativeButton("确定", null);
        dialogError = builder.create();
        dialogError.show();
    }

    /**
     * 开始呼叫
     */
    protected void startDialorPasswordDirectly(final String thisValue, final String fileUrl, final boolean isCall, String uuid) {
        if (currentStatus == CALLING_MODE || currentStatus == PASSWORD_CHECKING_MODE) {
            Message message = Message.obtain();
            String[] parameters = new String[3];
            if (isCall) {
                setDialValue("呼叫" + thisValue + "，取消请按删除键");
                message.what = MainService.MSG_START_DIAL;
                if (DeviceConfig.DEVICE_TYPE.equals("C")) {
                    parameters[0] = thisValue.substring(2);
                } else {
                    parameters[0] = thisValue;
                }
            } else {
                setTempkeyValue("准备验证密码" + thisValue + "...");
                message.what = MainService.MSG_CHECK_PASSWORD;
                parameters[0] = thisValue;
            }
            parameters[1] = fileUrl;
            parameters[2] = uuid;
            message.obj = parameters;
            try {
                serviceMessenger.send(message);
            } catch (RemoteException er) {
                er.printStackTrace();
            }
        }
    }

    protected void startSendPictureDirectly(final String thisValue, final String fileUrl, final boolean isCall, String uuid) {
        if (fileUrl == null || fileUrl.length() == 0) {
            return;
        }
        Message message = Message.obtain();
        if (isCall) {
            message.what = MainService.MSG_START_DIAL_PICTURE;
        } else {
            message.what = MainService.MSG_CHECK_PASSWORD_PICTURE;
        }
        String[] parameters = new String[3];
        parameters[0] = thisValue;
        parameters[1] = fileUrl;
        parameters[2] = uuid;
        message.obj = parameters;
        try {
            serviceMessenger.send(message);
        } catch (RemoteException er) {
            er.printStackTrace();
        }
    }

    @Override
    public void beforeTakePickture(String thisValue, boolean isCall, String uuid) {
        startDialorPasswordDirectly(thisValue, null, isCall, uuid);
    }

    @Override
    public void afterTakePickture(String thisValue, String fileUrl, boolean isCall, String uuid) {
        startSendPictureDirectly(thisValue, fileUrl, isCall, uuid);
    }

    /**
     * 使用Handler实现UI线程与Timer线程之间的信息传递,每5秒告诉UI线程获得wifi Info
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 如果收到正确的消息就获取WifiInfo，改变图片并显示信号强度
                case 11:
                    wifi_image.setImageResource(R.mipmap.ethernet);
                    if (listTemp1 != null && listTemp1.length > 0) {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.binder_default_head));
                    } else {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.bind_offline));
                    }

                    break;
                case 1:
                    wifi_image.setImageResource(R.mipmap.wifi02);
                    if (listTemp1 != null && listTemp1.length > 0) {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.binder_default_head));
                    } else {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.bind_offline));
                    }

                    break;
                case 2:
                    wifi_image.setImageResource(R.mipmap.wifi02);
                    if (listTemp1 != null && listTemp1.length > 0) {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.binder_default_head));
                    } else {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.bind_offline));
                    }
                    break;
                case 3:
                    wifi_image.setImageResource(R.mipmap.wifi03);
                    if (listTemp1 != null && listTemp1.length > 0) {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.binder_default_head));
                    } else {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.bind_offline));
                    }
                    break;
                case 4:
                    wifi_image.setImageResource(R.mipmap.wifi04);
                    if (listTemp1 != null && listTemp1.length > 0) {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.binder_default_head));
                    } else {
                        iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.bind_offline));
                    }
                    break;
                case 5:
                    wifi_image.setImageResource(R.mipmap.wifi05);
                    iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.bind_offline));
                    break;
                case 6://无网络连接
                    rl.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    //提示用户无网络连接
                    rl.setVisibility(View.GONE);
                    break;
                default:
                    //以防万一
                    wifi_image.setImageResource(R.mipmap.wifi_05);
                    rl.setVisibility(View.VISIBLE);
                    iv_bind.setImageDrawable(getResources().getDrawable(R.mipmap.bind_offline));
            }
        }

    };

    BroadcastReceiver dataUpdateRecevice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            switch (intent.getAction()) {
                case ACTION_SCAN_DEVICE://搜索到设备
                    Log.d(TAG, "搜索到设备,开始绑定设备,mScanning=" + mScanning);
                    bindDevice();//绑定设备
                    break;
                case SCAN_DEVICE_FAIL://搜索失败
                    if (mScanning) {
                        scanLeDevice(false);//停止扫描
                    }
                    Log.e(TAG, "扫描失败继续扫描");
                    scanLeDevice(true);
                    break;
                case ACTION_GATT_CONNECTED:
                    isConnectBLE = true;
                    tv_battery.setVisibility(View.VISIBLE);
                    bluetooth_image.setImageResource(R.mipmap.ble_pressed);
                    toast("蓝牙连接");
                    Log.e(TAG, "蓝牙连接" + "isConnectBLE=" + isConnectBLE + "  mScanning=" + mScanning);
                    if (mScanning) {
                        scanLeDevice(false);//停止扫描
                    }
                    break;

                case ACTION_GATT_DISCONNECTED://断开连接
                    isConnectBLE = false;
                    tv_battery.setVisibility(View.GONE);
                    bluetooth_image.setImageResource(R.mipmap.ble_button);
                    toast("蓝牙断开，重新开始扫描");
                    Log.e(TAG, "蓝牙连接" + "isConnectBLE=" + isConnectBLE + "  mScanning=" + mScanning);
                    if (mScanning) {
                        scanLeDevice(false);
                    }
                    scanLeDevice(true);
                    break;
                case REFRESH_RSSI://获取信号强度
                    break;
                case DoorLock.DoorLockOpenDoor_BLE://开门指令
                    Log.e(TAG, "收到开门指令");
                    if (isConnectBLE) {
                        device.openLock();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                device.closeLock();//关闭锁
                            }
                        }, 5000);
                    } else {
                        toast("蓝牙未连接");
                        if (!mScanning) {
                            scanLeDevice(true);
                        }
                    }
                    break;
                case DoorLock.DoorLockStatusChange://门锁状态发生变化
                    Log.v(FACE_TAG, "onReceive-->" + 987);
                    break;

                case ACTION_LOCK_BATTERY://电量
                    int battery = (int) (intent.getDoubleExtra("battery", 0) * 100);
                    tv_battery.setText(battery + "%");
                    break;

                case ACTION_LOCK_STARTS_OPEN://常开
                    if (isConnectBLE && device != null) {
                        device.closeLock();
                    }
                    break;

                case ACTION_LOCK_STARTS_CLOSE://锁着
                    break;
                case ACTION_LOCK_STARTS_CLOSE_BACK://反锁
                    break;

                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                        case BluetoothAdapter.STATE_ON:
                            toast("蓝牙打开，自动开始连接");
                            //开始扫描
                            scanLeDevice(true);
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            scanLeDevice(false);
                            bluetooth_image.setImageResource(R.mipmap.ble_button);
                            isConnectBLE = false;
                            Log.e(TAG, "蓝牙连接" + "isConnectBLE=" + isConnectBLE + "  mScanning=" + mScanning);
                            if (mScanning) {
                                scanLeDevice(false);//停止扫描
                            }
                            toast("蓝牙已关闭");
                            break;
                    }
                    break;
            }
        }
    };

    private Timer timer_doorStarts = new Timer();// 设计定时器
    private TimerTask timerTask;

    /**
     * 启动心跳获取蓝牙锁的状态
     */
    private void startGetDoorStarts() {
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (isConnectBLE && device != null) {
                        device.getLockStarts();
                    }
                }
            };
        }
        if (timer_doorStarts == null) timer_doorStarts = new Timer();
        timer_doorStarts.schedule(timerTask, 1000, 1000 * 60 * 20);//20分钟
    }

    /**
     * 停止获取蓝牙锁状态
     */
    private void stopGetDoorStarts() {
        if (timerTask != null) {
            timerTask.cancel();
            timer_doorStarts.cancel();
            timerTask = null;
            timer_doorStarts = null;
        }
    }

    /**
     * 扫描蓝牙
     *
     * @param enable
     */
    public void scanLeDevice(boolean enable) {
        if (enable) {//开始扫描
            bleRunnable = new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    try {//魅族手机（MX4）会出现异常
                        mBtAdapter.stopLeScan(mLeScanCallback);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            startScanBle();
            bleHandler.postDelayed(bleRunnable, SCAN_PERIOD);
            mScanning = true;
            mBtAdapter.startLeScan(mLeScanCallback);
            Log.d(TAG, "scanLeDevice  startLeScan  ");
        } else {//停止扫描
            if (bleRunnable != null && bleHandler != null) {
                bleHandler.removeCallbacks(bleRunnable);
            }
            stopScanBle();
            mScanning = false;
            mBtAdapter.stopLeScan(mLeScanCallback);
            Log.d(TAG, "scanLeDevice  stopLeScan  ");
        }
    }

    public class Receive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionName = intent.getAction();
            switch (actionName) {
                case ACTION_NFC_CARDINFO:
                    String cardInfo = intent.getStringExtra("cardinfo");
                    Log.i(TAG, "onReceive: cardinfo=" + cardInfo);
                    break;
            }
        }
    }

    public void sendMessage(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    //人脸识别
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

    byte[] mImageNV21 = null;
    FRAbsLoop mFRAbsLoop = null;

    private boolean identification = false;

    //    private HandlerThread faceThread;
    private Handler faceHandler;

    private void initFaceDetectAndIDCard() {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        Log.v(FACE_TAG, "initFaceDetect-->" + mWidth + "/" + mHeight + "/" + density);

        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, true, 0);
        mSurfaceView.debug_print_fps(true, false);

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

//        //创建一个线程,线程名字：faceHandlerThread
//        faceThread = new HandlerThread("faceHandlerThread");
//        //开启一个线程
//        faceThread.start();
//        //在这个线程中创建一个handler对象
//        faceHandler = new Handler(faceThread.getLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                Log.v(FACE_TAG, "handleMessage-->" + msg.what + "/" + Thread.currentThread().getName());
//            }
//        };

        initIDCard();

        faceHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.v(FACE_TAG, "handleMessage-->" + msg.what + "/" + Thread.currentThread().getName());
                switch (msg.what) {
                    case -1:
                        idOperation = identification = true;
                        break;
                    case MSG_FACE_DETECT_INPUT:
                        faceDetectInput();
                        break;
                    case MSG_FACE_DETECT_CONTRAST:
                        identification = true;
                        if (mFRAbsLoop != null) {
                            mFRAbsLoop.resumeThread();
                        }
                        if (mSurfaceView.getVisibility() != View.VISIBLE) {
                            mGLSurfaceView.setVisibility(View.VISIBLE);
                            mSurfaceView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MSG_FACE_DETECT_PAUSE:
                        identification = false;
                        if (mFRAbsLoop != null) {
                            mFRAbsLoop.pauseThread();
                        }
                        if (mSurfaceView.getVisibility() != View.GONE) {
                            mGLSurfaceView.setVisibility(View.GONE);
                            mSurfaceView.setVisibility(View.GONE);
                        }
                        break;
                    case MSG_ID_CARD_DETECT_RESTART:
                        idOperation = true;
                        if (mIdCardUtil != null) {
                            mIdCardUtil.setReading(true);
                        }
                        break;
                    case MSG_ID_CARD_DETECT_PAUSE:
                        idOperation = false;
                        if (mIdCardUtil != null) {
                            mIdCardUtil.setReading(false);
                        }
                        break;
                    case MSG_ID_CARD_DETECT_INPUT:
                        inputIDCard((IDCard) msg.obj);
                        break;
                }
                return false;
            }
        });

        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();

//        //在主线程给handler发送消息
//        faceHandler.sendEmptyMessage(1);

//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.setTitle("loading face data...");
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.v(FACE_TAG, "initFaceDetect-->" + 111);
                ArcsoftManager.getInstance().mFaceDB.loadFaces();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        //在子线程给handler发送数据
//                        faceHandler.sendEmptyMessage(2);
                        Log.v(FACE_TAG, "initFaceDetect-->" + 222);
//                        mProgressDialog.cancel();
                        if (ArcsoftManager.getInstance().mFaceDB.mRegister.isEmpty()) {
                            Log.v(FACE_TAG, "initFaceDetect-->" + 333);
                            Utils.DisplayToast(MainActivity.this, "没有注册人脸，请先注册");
                            return;
                        }
                        identification = true;
                        Utils.DisplayToast(MainActivity.this, "人脸数据加载完成");
                    }
                });
            }
        }).start();
    }

    @Override
    public Camera setupCamera() {
        // TODO Auto-generated method stub
        mCamera = Camera.open();
        try {
            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(mWidth, mHeight);
            parameters.setPreviewSize(800, 600);
            parameters.setPreviewFormat(ImageFormat.NV21);

            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                //Log.v(FACE_TAG, "SIZE:" + size.width + "x" + size.height);
            }
            for (Integer format : parameters.getSupportedPreviewFormats()) {
                //Log.v(FACE_TAG, "FORMAT:" + format);
            }

            List<int[]> fps = parameters.getSupportedPreviewFpsRange();
            for (int[] count : fps) {
                //Log.d(TAG, "T:");
                for (int data : count) {
                    //Log.d(TAG, "V=" + data);
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
        } catch (Exception e) {
            e.printStackTrace();
            //Log.v(FACE_TAG, "setupCamera-->" + e.getMessage());
        }
        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
            mCamera.autoFocus(null);
            //Log.v(FACE_TAG, "SIZE:" + mWidth + "x" + mHeight);
        }
        return mCamera;
    }

    @Override
    public void setupChanged(int format, int width, int height) {
        //Log.v(FACE_TAG, "setupChanged-->" + width + "/" + height);
    }

    @Override
    public boolean startPreviewLater() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        //Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
        //Log.d(TAG, "Face=" + result.size());
        for (AFT_FSDKFace face : result) {
            //Log.d(TAG, "Face:" + face.toString());
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

    private void faceDetectInput() {
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        ContentValues values = new ContentValues(1);
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        Log.v(FACE_TAG, "faceDetectInput:" + uri.toString());
//        ArcsoftManager.getInstance().setCaptureImage(uri);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//        startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);

        startActivity(new Intent(this, PhotographActivity2.class));
    }

    /**
     * @param uri
     * @return
     */
    private String getFacePath(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(this, contentUri, null, null);
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(this, contentUri, selection, selectionArgs);
                }
            }
        }
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = this.getContentResolver().query(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        String end = img_path.substring(img_path.length() - 4);
        if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
            return null;
        }
        return img_path;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    class FRAbsLoop extends AbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();
        List<FaceDB.FaceRegist> mResgist = ArcsoftManager.getInstance().mFaceDB.mRegister;
//        List<ASAE_FSDKFace> face1 = new ArrayList<>();
//        List<ASGE_FSDKFace> face2 = new ArrayList<>();

        private final Object lock = new Object();
        private boolean pause = false;

        /**
         * 调用这个方法实现暂停线程
         */
        void pauseThread() {
            pause = true;
        }

        /**
         * 调用这个方法实现恢复线程的运行
         */
        void resumeThread() {
            pause = false;
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        /**
         * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
         */
        void onPause() {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            //Log.v(FACE_TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            //Log.v(FACE_TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        @Override
        public void loop() {
//            Log.v(FACE_TAG, "loop1:" + mImageNV21 + "/" + identification);
            while (pause) {
                onPause();
            }
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mImageNV21 != null && identification) {
                long time = System.currentTimeMillis();
                AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight, AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
                //Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
                //Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," + result.getFeatureData()[2] + "," + error.getCode());
                AFR_FSDKMatching score = new AFR_FSDKMatching();
                float max = 0.0f;
                String name = null;
                for (FaceDB.FaceRegist fr : mResgist) {
                    Log.v(FACE_TAG, "loop:" + mResgist.size() + "/" + fr.mFaceList.size());
                    if (fr.mName.length() > 11) {
                        continue;
                    }
                    for (AFR_FSDKFace face : fr.mFaceList) {
                        error = engine.AFR_FSDK_FacePairMatching(result, face, score);
                        //Log.d(TAG, "Score:" + score.getScore() + ", AFR_FSDK_FacePairMatching=" + error.getCode());
                        if (max < score.getScore()) {
                            max = score.getScore();
                            name = fr.mName;
                            if (max > 0.80f) {
                                break;
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

                //Log.v(FACE_TAG, "fit Score:" + max + ", NAME:" + name);
                if (max > 0.80f) {
                    //fr success.
                    final float max_score = max;
                    //Log.v(FACE_TAG, "置信度：" + (float) ((int) (max_score * 1000)) / 1000.0);
                    Message message = Message.obtain();
                    message.what = MainService.MSG_FACE_OPENLOCK;
                    try {
                        serviceMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mImageNV21 = null;
            }
        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
            //Log.v(FACE_TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }
    }

    private boolean fileOperation(String name) {
        boolean bool = false;
        String path = getExternalCacheDir().getPath();
        Log.v(FACE_TAG, "fileOperation-->" + path);
        File file = new File(path);
        if (file != null && file.exists()) {
            File[] files = file.listFiles();// 读取文件夹下文件
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {//检查此路径名的文件是否是一个目录(文件夹)
                        continue;
                    }
                    String fileName = file1.getName();
                    Log.v(FACE_TAG, "fileOperation-->" + fileName + "/" + file1.getPath());
                    if (fileName.endsWith(".data")) {
                        bool = fileName.contains(name);
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

    private IdCardUtil mIdCardUtil;

    private void initIDCard() {
        //打开阅读器
        try {
            mSerialPort = getSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(FACE_TAG, "initIDCard-->" + e.getMessage());
        }
        if (mSerialPort != null) {
            mIdCardUtil = new IdCardUtil(this, mSerialPort, this);
            mIdCardUtil.openIdCard();
            mIdCardUtil.readIdCard();
        }
    }

    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */
            SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
            sp.edit().putString("DEVICE", "/dev/ttyS2").commit();
            sp.edit().putString("BAUDRATE", "115200").commit();

            String path = sp.getString("DEVICE", "");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

			/* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        Log.v(FACE_TAG, "getSerialPort-->" + mSerialPort);
        return mSerialPort;
    }

    @Override
    public void callBack(int a) {
//        Log.v(FACE_TAG, "callBack-->" + a);
        if (a == IdCardUtil.READ) {
            IDCard idCard = mIdCardUtil.getIdCard();
            Log.v(FACE_TAG, "callBack-->" + idCard + "/" + idOperation);
            if (idCard != null && idCard.getPhoto() != null && idOperation) {
                idOperation = false;
                if (faceHandler != null) {
                    //身份证识别录入
                    Message message = new Message();
                    message.what = MSG_ID_CARD_DETECT_INPUT;
                    message.obj = idCard;
                    faceHandler.sendMessageDelayed(message, 1000);
                }
            }
        }
    }

    private boolean idOperation = true;

    private void inputIDCard(final IDCard idCard) {
//        Log.v(FACE_TAG, "inputIDCard-->" + idCard.getName());
//        Log.v(FACE_TAG, "inputIDCard-->" + idCard.getSex());
//        Log.v(FACE_TAG, "inputIDCard-->" + idCard.getBirthday());
//        Log.v(FACE_TAG, "inputIDCard-->" + idCard.getNation());
//        Log.v(FACE_TAG, "inputIDCard-->" + idCard.getAddress());
        Log.v(FACE_TAG, "inputIDCard-->" + idCard.getIDCardNo());
//        Log.v(FACE_TAG, "inputIDCard-->" + idCard.getPhoto());
        Log.v(FACE_TAG, "inputIDCard-->" + IdCardUtil.bmpPath);
        List<String> list = ArcsoftManager.getInstance().getIDCardData();
        if (list.contains(idCard.getIDCardNo())) {
//            Toast.makeText(this, "身份证已录入", Toast.LENGTH_SHORT).show();
            Message message = Message.obtain();
            message.what = MainService.MSG_FACE_OPENLOCK;
            try {
                serviceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("检测到身份证，是否录入")
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (keyCode == KeyEvent.KEYCODE_DEL) {
                                idOperation = true;
                                dialog.dismiss();
                            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {//确认键
                                faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 100);
                                Intent intent = new Intent(MainActivity.this, DetecterActivity.class);
                                intent.putExtra("ID", idCard.getIDCardNo());
                                intent.putExtra("path", IdCardUtil.bmpPath);
                                intent.putExtra("avatar", idCard.getPhoto());
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        }
                        return true;
                    }
                }).show();
    }


    private String getVersionName(){
        String verName = "";
        try {
            verName = this.getPackageManager().
                    getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verName;
    }
}


