package com.projection.car;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjection.Callback;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.projection.car.Utils.log;


public class MediaCodecTool {

    private static final String SCREENCAP_NAME = "screencap";
    private static final String TAG = MediaCodecTool.class.getName();

    private MediaProjection sMediaProjection;
    private MediaProjectionManager mProjectionManager;
    private VirtualDisplay mVirtualDisplay;

    private MediaCodec mMediaCodec;
    private boolean mFirstConfigFrame;
    private byte[] mConfigByte;
    private VideoDataEncodeListener mEncodeCall;

    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mBit, mFrame;

    private boolean testAudio = false;
    private FileOutputStream mOutputStream;

    MediaCodecTool() {

    }

    private void createVirtualDisplay() {
        if (testAudio) {
            try {
                mOutputStream = new FileOutputStream("/sdcard/test.mp4");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        log("VIS w = " + mWidth + ", h = " + mHeight);
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, mFrame);//4000000
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mBit);//10
            mediaFormat.setInteger(MediaFormat.KEY_CAPTURE_RATE, mBit);//10
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mediaFormat.setLong(MediaFormat.KEY_REPEAT_PREVIOUS_FRAME_AFTER, 100000L);
            mediaFormat.setLong(MediaFormat.KEY_DURATION, 100000L);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//2130706433
//          mediaFormat.setInteger("profile", 1);
//          mediaFormat.setInteger("level", 256);
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mMediaCodec.createInputSurface(), null, null);


            mMediaCodec.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {

                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo bufferInfo) {
                    try {
                        ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(index);
                        byte[] outData = new byte[bufferInfo.size];
                        outputBuffer.get(outData);
                        // flags 利用位操作，定义的 flag 都是 2 的倍数
                        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) { // 配置相关的内容，也就是 SPS，PPS
                            if (testAudio) {
                                mOutputStream.write(outData, 0, outData.length);
                                mOutputStream.flush();
                            }


                            mConfigByte = new byte[outData.length];
                            mFirstConfigFrame = true;
                            System.arraycopy(outData, 0, mConfigByte, 0, outData.length);
                            Log.e(TAG, "now CONFIG is" + Arrays.toString(mConfigByte));
                        } else if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) { // 关键帧
                            if (testAudio) {
                                mOutputStream.write(outData, 0, outData.length);
                                mOutputStream.flush();
                            }
                            if (mEncodeCall != null) {
                                Log.e(TAG, "now frame is" + outData.length);
                                if (mFirstConfigFrame) {
                                    mFirstConfigFrame = false;
                                    byte[] t = new byte[mConfigByte.length + outData.length];
                                    System.arraycopy(mConfigByte, 0, t, 0, mConfigByte.length);
                                    System.arraycopy(outData, 0, t, mConfigByte.length, outData.length);
                                    outData = t;
                                }

                                mEncodeCall.onData(outData);
                            }
                        } else {
                            // 非关键帧和SPS、PPS,直接写入文件，可能是B帧或者P帧
                            if (testAudio) {
                                mOutputStream.write(outData, 0, outData.length);
                                mOutputStream.flush();
                            }

                            if (mEncodeCall != null) {
                                mEncodeCall.onData(outData);
                            }
                        }
                        mMediaCodec.releaseOutputBuffer(index, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

                }
            });
            mMediaCodec.start();


//            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//            while (true) {
//                int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
//                // 从输出缓冲区队列中拿到编码好的内容，对内容进行相应处理后在释放
//                while (outputBufferIndex >= 0) {
//                    Log.e(TAG, "outputBufferIndex " + outputBufferIndex);
//                    ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
//                    ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
//                    byte[] outData = new byte[bufferInfo.size];
//                    outputBuffer.get(outData);
//                    // flags 利用位操作，定义的 flag 都是 2 的倍数
//                    if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) { // 配置相关的内容，也就是 SPS，PPS
////                        mOutputStream.write(outData, 0, outData.length);
////                        mOutputStream.flush();
//
//                        mConfigByte = new byte[outData.length];
//                        mFirstConfigFrame = true;
//                        System.arraycopy(outData,0,mConfigByte,0,outData.length);
//                        Log.e(TAG,"now CONFIG is" + Arrays.toString(mConfigByte));
//                    } else if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) { // 关键帧
////                        mOutputStream.write(outData, 0, outData.length);
////                        mOutputStream.flush();
//                        if(mEncodeCall != null){
//                            Log.e(TAG,"now frame is" + outData.length);
//                            if(mFirstConfigFrame){
//                                mFirstConfigFrame = false;
//                                byte[] t = new byte[mConfigByte.length + outData.length];
//                                System.arraycopy(mConfigByte,0,t,0,mConfigByte.length);
//                                System.arraycopy(outData,0,t,mConfigByte.length,outData.length);
//                                outData = t;
//                            }
//
//                            mEncodeCall.onData(outData);
//                        }
//                    } else {
//                        // 非关键帧和SPS、PPS,直接写入文件，可能是B帧或者P帧
////                        mOutputStream.write(outData, 0, outData.length);
////                        mOutputStream.flush();
//                        if(mEncodeCall != null){
//                            mEncodeCall.onData(outData);
//                        }
//                    }
//                    mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
//                    outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
//                }
//            }


        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public MediaProjection getMediaProjection(){
        return sMediaProjection;
    }


    public void startProjection(Activity context, VideoDataEncodeListener encodeCall, int code,float w, float h, int bit, int frame) {
        mEncodeCall = encodeCall;
        mBit = bit;
        mFrame = frame;
        mWidth = (int) w;
        mHeight = (int) h;
        mProjectionManager = ((MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE));
        context.startActivityForResult(mProjectionManager.createScreenCaptureIntent(), code);
    }

    public void stopProjection() {
        if (sMediaProjection != null) {
            try {
                sMediaProjection.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onActivityResult(Activity activity, int paramInt2, Intent paramIntent) {
        sMediaProjection = mProjectionManager.getMediaProjection(paramInt2, paramIntent);
        if (sMediaProjection != null) {
            mDensity = activity.getResources().getDisplayMetrics().densityDpi;
            createVirtualDisplay();
            sMediaProjection.registerCallback(new MediaProjectionStopCallback(), null);
        }

    }


    private class MediaProjectionStopCallback extends Callback {
        private MediaProjectionStopCallback() {
        }

        public void onStop() {

            Log.e(TAG, "stopping projection.");

            mMediaCodec.stop();

            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }
            sMediaProjection.unregisterCallback(MediaCodecTool.MediaProjectionStopCallback.this);
        }
    }


    public interface VideoDataEncodeListener {
        void onData(byte[] data);
    }
}
