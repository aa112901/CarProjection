package com.projection.car;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utils {

    public final static String TAG = "CarProjection";

    public static final int REQUEST_CODE = 100;

    public static final byte CMD = 1;
    public static final byte VIDEO = 2;
    public static final byte MEDIA = 3;
    public static final byte TOUCH = 6;

    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_MOVE = 2;


    public static final int MSG_WRITE_AUDIO = 10;

    public static final int MSG_WRITE_VIDEO = 11;

    public static final int MSG_CMD_HU_PROTOCOL_VERSION = 0x00018001;//98305
    public static final int MSG_CMD_HU_INFO = 0x00018003;//98307
    public static final int MSG_CMD_VIDEO_ENCODER_INIT = 0x00018007;//98311
    public static final int MSG_CMD_VIDEO_ENCODER_START = 0x00018009;//98313
    public static final int MSG_CMD_STATISTIC_INFO = 0x00018027;//98343


    public static final int MSG_CMD_PROTOCOL_VERSION_MATCH_STATUS = 0x00010002;//65538
    public static final int MSG_CMD_MD_INFO = 0x00010004;//65540
    public static final int MSG_CMD_SCREEN_ON = 0x00010018;//65560
    public static final int MSG_CMD_VIDEO_ENCODER_INIT_DONE = 0x00010008;//65544
    public static final int MSG_CMD_MD_AUTHEN_RESULT = 0x0001004B;//65611
    public static final int MSG_CMD_FOREGROUND = 0x0001001B;//65563


    public static final int MSG_CMD_MD_FEATURE_CONFIG_REQUEST = 0x00010051;//65617
    public static final int MSG_CMD_HU_FEATURE_CONFIG_RESPONSE = 0x00018052;//98386


    // Media通道相关消息
    public static final int MSG_MEDIA_INIT = 0x00030001;
    public static final int MSG_MEDIA_STOP = 0x00030002;
    public static final int MSG_MEDIA_PAUSE = 0x00030003;
    public static final int MSG_MEDIA_RESUME_PLAY = 0x00030004;
    public static final int MSG_MEDIA_SEEK_TO = 0x00030005;
    public static final int MSG_MEDIA_DATA = 0x00030006;

    public static final int MSG_VIDEO_DATA = 0x00020001;


    // Touch通道相关消息
    public static final int MSG_TOUCH_ACTION = 0x00068001;
    public static final int MSG_TOUCH_ACTION_DOWN = 0x00068002;
    public static final int MSG_TOUCH_ACTION_UP = 0x00068003;
    public static final int MSG_TOUCH_ACTION_MOVE = 0x00068004;
    public static final int MSG_TOUCH_SINGLE_CLICK = 0x00068005;
    public static final int MSG_TOUCH_DOUBLE_CLICK = 0x00068006;
    public static final int MSG_TOUCH_LONG_PRESS = 0x00068007;
    public static final int MSG_TOUCH_CAR_HARD_KEY_CODE = 0x00068008;
    public static final int MSG_TOUCH_UI_ACTION_SOUND = 0x00060009;
    public static final int MSG_TOUCH_ACTION_BEGIN = 0x0006800A;


    // 硬按键消息
    public static final int KEYCODE_HOME = 0x00000001;
    public static final int KEYCODE_PHONE_CALL = 0x00000002;
    public static final int KEYCODE_PHONE_END = 0x00000003;
    public static final int KEYCODE_PHONE_END_MUTE = 0x00000004;
    public static final int KEYCODE_HFP = 0x00000005;
    public static final int KEYCODE_SELECTOR_NEXT = 0x00000006;
    public static final int KEYCODE_SELECTOR_PREVIOUS = 0x00000007;
    public static final int KEYCODE_SETTING = 0x00000008;
    public static final int KEYCODE_MEDIA = 0x00000009;
    public static final int KEYCODE_RADIO = 0x0000000A;
    public static final int KEYCODE_NAVI = 0x0000000B;
    public static final int KEYCODE_SRC = 0x0000000C;
    public static final int KEYCODE_MODE = 0x0000000D;
    public static final int KEYCODE_BACK = 0x0000000E;
    public static final int KEYCODE_SEEK_SUB = 0x0000000F;
    public static final int KEYCODE_SEEK_ADD = 0x00000010;
    public static final int KEYCODE_VOLUME_SUB = 0x00000011;
    public static final int KEYCODE_VOLUME_ADD = 0x00000012;
    public static final int KEYCODE_MUTE = 0x00000013;
    public static final int KEYCODE_OK = 0x00000014;
    public static final int KEYCODE_MOVE_LEFT = 0x00000015;
    public static final int KEYCODE_MOVE_RIGHT = 0x00000016;
    public static final int KEYCODE_MOVE_UP = 0x00000017;
    public static final int KEYCODE_MOVE_DOWN = 0x00000018;
    public static final int KEYCODE_MOVE_UP_LEFT = 0x00000019;
    public static final int KEYCODE_MOVE_UP_RIGHT = 0x0000001A;
    public static final int KEYCODE_MOVE_DOWN_LEFT = 0x0000001B;
    public static final int KEYCODE_MOVE_DOWN_RIGHT = 0x0000001C;
    public static final int KEYCODE_TEL = 0x0000001D;
    public static final int KEYCODE_MAIN = 0x0000001E;
    public static final int KEYCODE_MEDIA_START = 0x0000001F;
    public static final int KEYCODE_MEDIA_STOP = 0x00000020;
    public static final int KEYCODE_VR_START = 0x00000021;
    public static final int KEYCODE_VR_STOP = 0x00000022;
    public static final int KEYCODE_NUMBER_0 = 0x00000023;
    public static final int KEYCODE_NUMBER_1 = 0x00000024;
    public static final int KEYCODE_NUMBER_2 = 0x00000025;
    public static final int KEYCODE_NUMBER_3 = 0x00000026;
    public static final int KEYCODE_NUMBER_4 = 0x00000027;
    public static final int KEYCODE_NUMBER_5 = 0x00000028;
    public static final int KEYCODE_NUMBER_6 = 0x00000029;
    public static final int KEYCODE_NUMBER_7 = 0x0000002A;
    public static final int KEYCODE_NUMBER_8 = 0x0000002B;
    public static final int KEYCODE_NUMBER_9 = 0x0000002C;
    public static final int KEYCODE_NUMBER_STAR = 0x0000002D; // *
    public static final int KEYCODE_NUMBER_POUND = 0x0000002E; // #
    public static final int KEYCODE_NUMBER_DEL = 0x0000002F;
    public static final int KEYCODE_NUMBER_CLEAR = 0x00000030;
    public static final int KEYCODE_NUMBER_ADD = 0x00000031; // +

    public static byte[] exportCMDMsg(int service, byte[] result) {
        byte[] carlife = null;
        if (result == null) {
            carlife = new byte[8];
            shortToBytes((short) 0, carlife, 0);// data len
            intToBytes2(service, carlife, 4);
        } else {
            carlife = new byte[8 + result.length];
            shortToBytes((short) result.length, carlife, 0);// data len
            intToBytes2(service, carlife, 4);
            System.arraycopy(result, 0, carlife, 8, result.length);
        }

        return carlife;
    }


    public static byte[] exportVideoMsg(int service, byte[] result, int len) {
        byte[] carlife = null;
        carlife = new byte[12 + len];
        intToBytes2(len, carlife, 0);// data len
        //Log.e(TAG, "time = " + (int) (System.currentTimeMillis()));
        intToBytes2((int) (System.currentTimeMillis()), carlife, 4);
        intToBytes2(service, carlife, 8);
        System.arraycopy(result, 0, carlife, 12, len);

        return carlife;
    }

    public static byte[] exportVideoMsg(int service, byte[] result) {
        byte[] carlife = null;
        carlife = new byte[12 + result.length];
        intToBytes2(result.length, carlife, 0);// data len
        //Log.e(TAG, "time = " + (int) (System.currentTimeMillis()));
        intToBytes2((int) (System.currentTimeMillis()), carlife, 4);
        intToBytes2(service, carlife, 8);
        System.arraycopy(result, 0, carlife, 12, result.length);

        return carlife;
    }

    public static int getPCMPackageHeadTimeStamp(byte[] mPCMPacakgeHeadBuffer) {
        int timeStamp;
        int dataHH = (mPCMPacakgeHeadBuffer[4] & 0xff);
        int dataHL = (mPCMPacakgeHeadBuffer[5] & 0xff);
        int dataLH = (mPCMPacakgeHeadBuffer[6] & 0xff);
        int dataLL = (mPCMPacakgeHeadBuffer[7] & 0xff);

        timeStamp =
                ((dataHH << 24) & 0xff000000) | ((dataHL << 16) & 0x00ff0000) | ((dataLH << 8) & 0x0000ff00)
                        | ((dataLL) & 0x000000ff);

        return timeStamp;
    }

    public static short bytesToShort2(byte[] src, int offset) {
        short value;
        value = (short) (((src[offset] & 0xFF) << 8)
                | (src[offset + 1] & 0xFF));
        return value;
    }

    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    //  * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    public static byte[] intToBytes2(int value, byte[] src, int offset) {

        src[offset] = (byte) ((value >> 24) & 0xFF);
        src[offset + 1] = (byte) ((value >> 16) & 0xFF);
        src[offset + 2] = (byte) ((value >> 8) & 0xFF);
        src[offset + 3] = (byte) (value & 0xFF);
        return src;
    }

    public static byte[] shortToBytes(short value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    public static byte[] shortToBytes(short value, byte[] src, int offset) {
        src[offset] = (byte) ((value >> 8) & 0xFF);
        src[offset + 1] = (byte) (value & 0xFF);
        return src;
    }

    public static void logAll() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                Process logcatProcess = null;
                BufferedReader bufferedReader = null;
                try {
                    // 获取系统logcat日志信息
                    String[] running = new String[]{"logcat", "-v",
                            "time"};
                    logcatProcess = Runtime.getRuntime().exec(running);
                    bufferedReader = new BufferedReader(
                            new InputStreamReader(logcatProcess
                                    .getInputStream()));
                    StringBuilder logString = new StringBuilder();
                    String line;
                    File file = new File("/sdcard/log");
                    if (file.length() > 1024 * 1024 * 10) {
                        file.delete();
                        file = new File("/sdcard/log");
                    }
                    FileOutputStream outputStream = new FileOutputStream(file, true);
                    while ((line = bufferedReader.readLine()) != null) {
                        logString.append(" ").append(line).append("\n");
                        // MyLog.logD(TAG, line);
                        outputStream.write((line + "\n").getBytes());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static boolean reboot(){
        try {

            os.writeBytes("reboot\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void root() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("busybox mount -o remount,rw /system\n");
            os.writeBytes("cat /sdcard/log > /system/priv-app/log\n");
            os.writeBytes("busybox mount -o remount,ro /system\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean installPlugin(String str) {
        try {
//            Process process = Runtime.getRuntime().exec("su");
//            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("cat -r " + str + "> /system/priv-app/Projection/projection.apk\n");
            os.writeBytes("busybox mount -o remount,rw /system\n");
            os.writeBytes("rm -r /system/priv-app/projection.apk\n");
            os.writeBytes("cat -r " + str + "> /system/priv-app/projection.apk\n");
            os.writeBytes("busybox mount -o remount,ro /system\n");
            os.writeBytes("rm " + str + "\n");
//            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean deletPlugin() {
        try {
            os.writeBytes("rm -r /system/priv-app/projection.apk\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void nextSong() {
        try {
//            Process process = Runtime.getRuntime().exec("su");
//            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("input keyevent 87\n");
//            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pauseSong() {
        try {
//            Process process = Runtime.getRuntime().exec("su");
//            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("input keyevent 85");
//            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopSong() {
        try {
//            Process process = Runtime.getRuntime().exec("su");
//            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("input keyevent 86");
//            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void previosSong() {
        try {
//            Process process = Runtime.getRuntime().exec("su");
//            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("input keyevent 88 \n");
//            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DataOutputStream os;

    public static void touch(ArrayList<Float> list, long time) {
        try {
            String command = "\n";
            if (list.size() == 2) {
                command = "input touchscreen tap " + list.get(0) + " " + list.get(1) + "\n";
            } else {
                command = "input touchscreen swipe " + list.get(0) + " " + list.get(1) + " " +
                        list.get(list.size() - 2) + " " + list.get(list.size() - 1) + " " + time + "\n";
            }


            if (os == null) {
                Process process = Runtime.getRuntime().exec("su");
                os = new DataOutputStream(process.getOutputStream());

            }
            os.writeBytes(command);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static synchronized boolean getRootAhth() {
        try {
            if (os == null) {
                Process process = Runtime.getRuntime().exec("su");
                os = new DataOutputStream(process.getOutputStream());

            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void log(String str) {
        Log.e(TAG, str);
    }

}
