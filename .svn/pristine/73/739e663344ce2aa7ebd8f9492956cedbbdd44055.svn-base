package com.supcon.mes.middleware.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;

/**
 * Created by wangshizhan on 2017/10/13.
 * Email:wangshizhan@supcon.com
 */

public class SB2BarcodeHelper {

    private final static String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";//接受广播
    private final static String START_SCAN_ACTION = "com.geomobile.se4500barcode";//调用扫描广播
    private final static String STOP_SCAN="com.geomobile.se4500barcode.poweroff";
    private final static String STOP_SCAN_ACTION = "com.geomobile.se4500barcodestop"; //停止扫描广播,此广播实现停止扫描

    private boolean isRepeat = false;

    private static class SB2BarcodeHelperHolder{
        private static SB2BarcodeHelper instance = new SB2BarcodeHelper();
    }


    public static SB2BarcodeHelper getInstance() {

        return SB2BarcodeHelperHolder.instance;
    }

    private SB2BarcodeHelper() {
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(android.content.Context context,
                              Intent intent) {
            String action = intent.getAction();
            if (action.equals(RECE_DATA_ACTION)) {
                String data = intent.getStringExtra("se4500");
                if(listener!=null){
                    listener.onBarcodeReceived(data);
                }
            }
        }

    };


    private Activity app;
    private OnBarcodeListener listener;

    public void setup(Activity app){
        this.app = app;
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(RECE_DATA_ACTION); //注册系统广播  接受扫描到的数据
        app.registerReceiver(receiver, iFilter);

        if(!Build.MODEL.equals("SD60")){
            judgePropert();
        }


    }

    private boolean isInited(){
        return app != null;
    }

    /**
     * 判断快捷扫描是否勾选   不勾选跳转到系统设置中进行设置
     */
    private void judgePropert() {
        String result = SystemProperties.get("persist.sys.keyreport", "true");
        if (result.equals("false")) {
            new AlertDialog.Builder(app)
                    .setTitle( com.supcon.mes.mbap.R.string.key_test_back_title)
                    .setMessage(com.supcon.mes.mbap.R.string.action_dialog_setting_config)
                    .setPositiveButton(
                            com.supcon.mes.mbap.R.string.action_dialog_setting_config_sure_go,
                            (dialog, which) -> {
                                Intent intent = new Intent(
                                        Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                app.startActivityForResult(intent, 1);
                            })
                    .setNegativeButton(com.supcon.mes.mbap.R.string.action_exit_cancel,
                            (dialog, which) -> {
                                app.finish();
                            }

                    ).show();
        }
    }

    public void release() {
        if(!isInited()){
            return;
        }
        Intent intent =new Intent();
        intent.setAction("com.geomobile.se4500barcode.poweroff");
        app.sendBroadcast(intent);
        Intent intent2 =new Intent();
        intent2.setAction(STOP_SCAN_ACTION);
        app.sendBroadcast(intent2);
        app.unregisterReceiver(receiver);
    }

    /**
     * 发送广播  调用系统扫描
     */
    public void startScan() {
        if(!isInited()){
            return;
        }
        Intent intent = new Intent();
        intent.setAction(START_SCAN_ACTION);
        app.sendBroadcast(intent, null);
    }


    public void setOnBarcodeListener(OnBarcodeListener listener){

        this.listener = listener;
    }

    public interface OnBarcodeListener{
        void onBarcodeReceived(String barcode);
    }


}
