package com.projection.car;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.projection.car.Utils.REQUEST_CODE;
import static com.projection.car.Utils.getRootAhth;
import static com.projection.car.Utils.log;
import static com.projection.car.Utils.logAll;
import static com.projection.car.Utils.pauseSong;

public class MainActivity extends AppCompatActivity {


    private static final String ACTION_USB_PERMISSION = "org.ammlab.android.app.helloadk.action.USB_PERMISSION";


    private Context mContext;

    private UsbManager mUsbManager;
    private UsbAccessory mUsbAccessory;
    private ParcelFileDescriptor mFileDescriptor;


    private int mVideoBit = 0;
    private int mVideoFrame = 0;

    private PowerManager.WakeLock mWakeLock;

    private MsgProcess mMsgProcess;


    private TextView mLog;
    private EditText bitTxt, frameTxt;
    private TextView wTxt, hTxt, serialTxt;


    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            log("receive accessory_filter connect broadcast:" + action);

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                    //获取accessory句柄成功
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        log("prepare to open accessory_filter stream");

                        mUsbAccessory = accessory;
                        openAccessory(mUsbAccessory);

                    } else {
                        log("permission denied for accessory " + accessory);

                        mUsbAccessory = null;

                    }
                }
            } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {

                Intent stopintent = new Intent(mContext, ForgroundService.class);
                stopintent.setAction("service_stop");
                mContext.startService(stopintent);

                if (mWakeLock.isHeld()) {
                    mWakeLock.release();
                }

                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                log("USB_ACCESSORY_DETACHED " + accessory);
                mUsbAccessory = null;

                mMsgProcess.resetUsb();

                System.exit(0);

            } else if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(action)) {
                UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                openAccessory(mUsbAccessory);
                //检测到us连接
                log("USB_ACCESSORY_ATTACHED " + accessory);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        checkPermission();
        if(!getRootAhth()){
            checkAccessibilitySettingsOn(mContext, ForgroundService.class.getCanonicalName());
        }

        findViewById(R.id.set_plugin1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream inputStream = getAssets().open("app-debug.apk");
                    byte [] readb = new byte[1024];
                    String p = "/sdcard/plugin.apk";
                    FileOutputStream fileOutputStream = new FileOutputStream(p);
                    int len = 0;
                    while ((len = inputStream.read(readb)) > 0){
                        fileOutputStream.write(readb,0,len);
                    }
                    if(Utils.installPlugin(p)){
                        log("install ok");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        findViewById(R.id.set_plugin2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.delete_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.deletPlugin();
            }
        });

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, Utils.TAG);


        mLog = findViewById(R.id.log);
        bitTxt = findViewById(R.id.bit);
        frameTxt = findViewById(R.id.frame);
        wTxt = findViewById(R.id.w);
        hTxt = findViewById(R.id.h);
        serialTxt = findViewById(R.id.serial);

        final SharedPreferences sharedPreferences = getSharedPreferences("set", MODE_PRIVATE);
        mVideoBit = sharedPreferences.getInt("bit", 30);
        mVideoFrame = sharedPreferences.getInt("frame", 3000000);
        bitTxt.setText(mVideoBit + "");
        frameTxt.setText(mVideoFrame + "");

        findViewById(R.id.config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bit = bitTxt.getText().toString();
                String frame = frameTxt.getText().toString();
                sharedPreferences.edit().putInt("bit", Integer.parseInt(bit)).commit();
                sharedPreferences.edit().putInt("frame", Integer.parseInt(frame)).commit();
                Utils.installPlugin("");


            }
        });
        mMsgProcess = new MsgProcess(this, mVideoBit, mVideoFrame, new MsgProcess.InfoListener() {
            @Override
            public void onVISSize(int x, int y) {
                wTxt.setText("车机屏幕宽度" + x);
                hTxt.setText("车机屏幕高度" + y);
            }

            @Override
            public void onVISID(String id) {
                serialTxt.setText("车机id :" + id);
            }
        });


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
//        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        mContext.registerReceiver(mUsbReceiver, filter);
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);


        checkUSBDevice();
        logAll();

    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        if (paramInt1 == REQUEST_CODE) {

            mMsgProcess.mediaPermissionOk(this, paramInt2, paramIntent);
        }
        mMsgProcess.startReadAudio();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mUsbReceiver);
    }


    private void openAccessory(UsbAccessory accessory) {
        log("openAccessory");
        mFileDescriptor = mUsbManager.openAccessory(accessory);

        if (mFileDescriptor != null) {
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();
            log("now usb fd" + fd);
            if (fd != null) {
                FileInputStream mInputStream = new FileInputStream(fd);
                log("accessory opened DataTranPrepared");
                FileOutputStream mOutputStream = new FileOutputStream(fd);

                mMsgProcess.startProjection(mInputStream, mOutputStream);

                mWakeLock.acquire();//保持屏幕唤醒


                Intent intent = new Intent(this, ForgroundService.class);
                intent.setAction("service_start");
                startService(intent);
            }

            log("accessory opened");
        } else {
            log("accessory open fail");
        }
    }

    private void checkUSBDevice() {
        log("checkUSBDevice");
        UsbAccessory[] accessories = mUsbManager.getAccessoryList();

        if (accessories == null) {
            log("accessories list is null");
            return;
        }

        log("accessories length " + accessories.length);

        UsbAccessory accessory = accessories[0];
        if (accessory != null) {
            log("accessories not null");
            if (mUsbManager.hasPermission(accessory)) {

                mUsbAccessory = accessory;
                openAccessory(mUsbAccessory);
            } else {
                log("accessories null per");
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                mUsbManager.requestPermission(accessory, mPermissionIntent);
            }
        } else {
            log("accessories null");
        }
    }


    private boolean checkAccessibilitySettingsOn(Context mContext, String serviceName) {
        int accessibilityEnabled = 0;
        // 对应的服务
        final String service = getPackageName() + "/" + serviceName;
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            log("accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            log("Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            log("***ACCESSIBILITY IS ENABLED*** -----------------");

            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    log("-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        log("We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            log("***ACCESSIBILITY IS DISABLED***");
        }
        //跳转设置打开无障碍
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle("权限申请");
        builder.setMessage("应用需要开启辅助功能,如果取消部分功能不可用");
        builder.setPositiveButton("确定开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setCancelable(false);
        builder.show();

        return false;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < 21) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Theme_AppCompat_DayNight_Dialog);
//            builder.setTitle("权限申请");
            builder.setMessage("应用需要android 5.1 版本以上运行");
//            builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finish();
//                }
//            });
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setCancelable(false);
            builder.show();
        }

        if (Build.VERSION.SDK_INT < 24) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Theme_AppCompat_DayNight_Dialog);
//            builder.setTitle("权限申请");
            builder.setMessage("车机反控功能需要android 7.0版本及以上");
            builder.setPositiveButton("了解", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setCancelable(false);
            builder.show();
        }
    }

}
