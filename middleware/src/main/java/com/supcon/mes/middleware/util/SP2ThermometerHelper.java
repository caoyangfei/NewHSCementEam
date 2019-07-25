package com.supcon.mes.middleware.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.serialport.DeviceControl;
import android.serialport.SerialPort;
import android.util.Log;

import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.mbap.utils.DateUtil;

import java.io.IOException;
import java.text.DecimalFormat;

import static android.serialport.SerialPort.SERIAL_TTYMT1;
import static android.serialport.SerialPort.SERIAL_TTYMT2;

/**
 * Created by zhangwenshuai1 on 2018/4/27.
 * 测温工具类
 */

public class SP2ThermometerHelper {

    SerialPort serialPort;
    DeviceControl control;
    int fd;
    private Thread thread;
    float min = 0;
    boolean isReady = false;
    HandlerThread mHandlerThread;
    Handler handler;
    private volatile boolean interrupted = false;

    private SP2ThermometerHelper() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandlerThread = new HandlerThread("Thermometer Loop");
                mHandlerThread.start();
                handler = new Handler(mHandlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
//                        LogUtil.i("handle message");
                        switch (msg.what) {
                            case 0:
                                byte[] temp = (byte[]) msg.obj;
                                byte[] mubiaotemp = new byte[2];
                                byte[] huanjingtemp = new byte[2];
                                for (int i = 0; i < temp.length; i += 5) {
                                    if (temp[i] == 76 && temp.length > 3) {//目标温度
//                                        LogUtil.i("handle message :" + DateUtil.dateTimeFormat(System.currentTimeMillis()));
                                        byte sum = (byte) (temp[i] + temp[i + 1] + temp[i + 2]);
                                        byte ww = temp[i + 3];
                                        if (sum == ww) {

                                            System.arraycopy(temp, i + 1, mubiaotemp, 0, 2);//copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                                            float mubiao = (float) DataConversionUtils.byteArrayToInt(mubiaotemp);
                                            float ss = mubiao / 16;
                                            float m = (float) (ss - 273.15);
                                            String mresult = saveDecimals(m);
                                /*if (m > 100) {
                                    tvmubian.setTextColor(Color.RED);
                                } else {
                                    tvmubian.setTextColor(Color.WHITE);
                                }
                                tvmubian.setText(mresult + "℃");
                                if (m > min) {
                                    tvMAX.setText(mresult);
                                } else {
                                    tvMIN.setText(mresult);
                                }
                                min = m;*/

//                                LogUtil.i(TAG+"target",mresult + "℃");
                                            //目标温度值回传
                                            if (listener != null) {
                                                listener.onThermometerValReceived(mresult + "℃");
//                                                LogUtil.i("handle message end:" + DateUtil.dateTimeFormat(System.currentTimeMillis()));
                                            }


                                        } else {
                                            LogUtil.e("check error mubiao  " + DataConversionUtils.byteArrayToStringLog(temp, temp.length));
                                            return;
                                        }
                                    } else if (temp[i] == 102) {//环境温度
                                        byte sum = (byte) (temp[i] + temp[i + 1] + temp[i + 2]);
                                        if (sum == temp[i + 3]) {
                                            System.arraycopy(temp, i + 1, huanjingtemp, 0, 2);//copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                                            float huanjing = (float) DataConversionUtils.byteArrayToInt(huanjingtemp);
                                            float ss = huanjing / 16;
                                            float h = (float) (ss - 273.15);
                                            String hresult = saveDecimals(h);
//                                tvhuanjing.setText(hresult + "℃");

//                                LogUtil.i(TAG+"environment",hresult + "℃");
                                            //环境温度值回传,暂不需要回传环境温度
//                                if (listener != null){
//                                    listener.onThermometerValReceived(hresult + "℃");
//                                    LogUtil.i("handle message end:"+ DateUtil.dateTimeFormat(System.currentTimeMillis()));
//                                }

                                        } else {
                                            LogUtil.e("check error huanjing");
                                            return;
                                        }
                                    }
                                }
                                break;
                        }
                    }
                };
            }
        }).start();


    }


    public static class SP2ThermometerHelperHolder {

        public static SP2ThermometerHelper instance = new SP2ThermometerHelper();

    }

    public static SP2ThermometerHelper getInstance() {

        return SP2ThermometerHelperHolder.instance;
    }

    private Activity app;
    private OnThermometerListener listener;


    public boolean startOrEnd(boolean flag) {
        if (!isReady) {
            LogUtil.e("This Device is not a 'Thermometer enabled Device!' ");
            return false;
        }
//        if (control.getCtrlFile() == null){
//            LogUtil.e("This Device is not a 'Thermometer enabled Device!' ");
//            return false;
//        }

        try {
            if (flag) {
                LogUtil.i("start time :" + DateUtil.dateTimeFormat(System.currentTimeMillis()));

                if (Build.MODEL.contains("T50")) {
                    control.PowerOnDevice("94");
                    control.PowerOnDevice("93");
                }
                if (Build.MODEL.contains("T55")) {
                    control.PowerOnDevice();
                }

                if (thread == null) {
                    thread = new ReadThread();
                    thread.start();    //手指按下时触发不停的发送消息
                }

                interrupted = false;

                return true;
            } else {
                if (Build.MODEL.contains("T50")) {
                    control.PowerOffDevice("94");
                    control.PowerOffDevice("93");
                }
                if (Build.MODEL.contains("T55")) {
                    control.PowerOffDevice();
                }

                interrupted = true;
//                thread.interrupt();
                return false;
            }
        } catch (IOException e) {

            e.printStackTrace();
            return false;

        }

//        return true;
    }

    public void setOnThermometerListener(OnThermometerListener listener) {

        this.listener = listener;
    }

    public interface OnThermometerListener {
        void onThermometerValReceived(String thermometerVal);
    }

    public void setup(Activity app) {
        this.app = app;
        try {
            serialPort = new SerialPort();
            if (Build.MODEL.contains("T50")) {
                serialPort.OpenSerial(SERIAL_TTYMT2, 9600);
            }
            if (Build.MODEL.contains("T55")) {
                serialPort.OpenSerial(SERIAL_TTYMT1, 9600);
            }
            fd = serialPort.getFd();
            if (Build.MODEL.contains("T50")) {
                control = new DeviceControl(DeviceControl.POWER_MAIN);
            }
            if (Build.MODEL.contains("T55")) {
                control = new DeviceControl(DeviceControl.PowerType.MAIN_AND_EXPAND, 73, 2);
            }
            isReady = true;
        } catch (IOException e) {
            e.printStackTrace();
            isReady = false;
        }

    }


/*    @SuppressLint("HandlerLeak")
    android.os.Handler handler = new android.os.Handler(mHandlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handle message");
            switch (msg.what) {
                case 0:
                    byte[] temp = (byte[]) msg.obj;
                    byte[] mubiaotemp = new byte[2];
                    byte[] huanjingtemp = new byte[2];
                    for (int i = 0; i < temp.length; i += 5) {
                        if (temp[i] == 76 && temp.length>3) {//目标温度
                            LogUtil.i("handle message :"+ DateUtil.dateTimeFormat(System.currentTimeMillis()));
                            byte sum = (byte) (temp[i] + temp[i + 1] + temp[i + 2]);
                            byte ww = temp[i + 3];
                            if (sum == ww) {

                                System.arraycopy(temp, i + 1, mubiaotemp, 0, 2);//copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                                float mubiao = (float) DataConversionUtils.byteArrayToInt(mubiaotemp);
                                float ss = mubiao / 16;
                                float m = (float) (ss - 273.15);
                                String mresult = saveDecimals(m);
                                *//*if (m > 100) {
                                    tvmubian.setTextColor(Color.RED);
                                } else {
                                    tvmubian.setTextColor(Color.WHITE);
                                }
                                tvmubian.setText(mresult + "℃");
                                if (m > min) {
                                    tvMAX.setText(mresult);
                                } else {
                                    tvMIN.setText(mresult);
                                }
                                min = m;*//*

//                                LogUtil.i(TAG+"target",mresult + "℃");
                                //目标温度值回传
                                if (listener != null){
                                    listener.onThermometerValReceived(mresult + "℃");
                                    LogUtil.i("handle message end:"+ DateUtil.dateTimeFormat(System.currentTimeMillis()));
                                }


                            } else {
                                Log.e(TAG, "check error mubiao  " + DataConversionUtils.byteArrayToStringLog(temp, temp.length));
                                return;
                            }
                        } else if (temp[i] == 102) {//环境温度
                            byte sum = (byte) (temp[i] + temp[i + 1] + temp[i + 2]);
                            if (sum == temp[i + 3]) {
                                System.arraycopy(temp, i + 1, huanjingtemp, 0, 2);//copy数组从第三位开始 cop3个字节 复制放到新数组第零位开始放 copy数组
                                float huanjing = (float) DataConversionUtils.byteArrayToInt(huanjingtemp);
                                float ss = huanjing / 16;
                                float h = (float) (ss - 273.15);
                                String hresult = saveDecimals(h);
//                                tvhuanjing.setText(hresult + "℃");

//                                LogUtil.i(TAG+"environment",hresult + "℃");
                                //环境温度值回传,暂不需要回传环境温度
//                                if (listener != null){
//                                    listener.onThermometerValReceived(hresult + "℃");
//                                    LogUtil.i("handle message end:"+ DateUtil.dateTimeFormat(System.currentTimeMillis()));
//                                }

                            } else {
                                Log.e(TAG, "check error huanjing");
                                return;
                            }
                        }
                    }
                    break;
            }
        }
    };*/

    public String saveDecimals(float f) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String dff = df.format(f);
        return dff;
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
//            LogUtil.w("ReadThread run");
            while (!interrupted())
                if (!interrupted) {
                    try {
                        byte[] temp1 = serialPort.ReadSerial(fd, 2048);
                        if (temp1 != null) {
                            Message msg = handler.obtainMessage(0);
                            msg.obj = temp1;
                            msg.sendToTarget();
                            LogUtil.i("send message :" + DateUtil.dateTimeFormat(System.currentTimeMillis()));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void release() {
        if (!isReady) {
            return;
        }
        if (thread != null) {
            thread.interrupt();
        }

        mHandlerThread.quit();
        try {
            if (Build.MODEL.contains("T50")) {
                control.PowerOffDevice("94");
                control.PowerOffDevice("93");
            }
            if (Build.MODEL.contains("T55")) {
                control.PowerOffDevice();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
