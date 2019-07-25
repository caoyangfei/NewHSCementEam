package com.supcon.mes.middleware.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdInventoryData;
import com.speedata.libuhf.interfaces.OnSpdInventoryListener;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;

import java.util.ArrayList;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * EM55UHFRFIDHelper 超高频读取
 * created by zhangwenshuai1 2018/8/1
*/

public class EM55UHFRFIDHelper extends BaseEM55 {

    private final String TAG = "EM55UHFRFIDHelper";

    private IUHFService iuhfService;

//    private SoundPool soundPool;
//    private int soundId;
    private OnUHFRFIDListener onUHFRFIDListener;

    private boolean isStart = false;  //开始读取 UHF RFID
    private volatile boolean setupStart = false;  //初始化服务，才能开始读取 UHF RFID

    private HandlerThread mHandlerThread;
    private Handler handler;

    public static class EM55UHFRFIDHelperHolder{
        public static EM55UHFRFIDHelper instance = new EM55UHFRFIDHelper();
    }

    @SuppressLint("CheckResult")
    private EM55UHFRFIDHelper() {
        Flowable.just(true)
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mHandlerThread = new HandlerThread("M55UHFThread");
                        mHandlerThread.start();
                        handler = new Handler(mHandlerThread.getLooper()){

                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                switch (msg.what){

                                    case 1:
                                        ArrayList<SpdInventoryData> cx = (ArrayList<SpdInventoryData>) msg.obj;
                                        if(cx!=null && cx.size()!=0){
                                            SpdInventoryData spdInventoryData= cx.get(0);
//                                            soundPool.play(soundId, 1, 1, 0, 0, 1);
                                            if (onUHFRFIDListener != null) {
                                                onUHFRFIDListener.onUHFRFIDEpcReceived(spdInventoryData.getEpc());
                                            }
                                        }
                                        break;

                                    case 2:
                                        int errCode = (int) msg.obj;
                                        LogUtil.e("errCode:"+errCode);
                                        break;

                                }


                            }
                        };
                    }
                });
    }

    public static EM55UHFRFIDHelper getInstance() {
        return EM55UHFRFIDHelperHolder.instance;
    }

/**
     * @param
     * @return
     * @description 初始化配置超高频服务
     * @author zhangwenshuai1 2018/8/1
*/

    @Override
    public void setupUHFServices(Context context) {
        super.setupUHFServices(context);

        //em55_URX  功能：R2000 UHF超高频 ，旗联超高频/ 红外测温
        if (!"81".equals(getEM55Model())) {
            ToastUtils.show(this.context, "设备KT55背夹不支持【超高频读取RFID】或不支持【红外测温】");
            isReady = false;
            return;
        }

        //清理下背夹模块缓存，不清理默认上次使用的背夹模块
//        SharedXmlUtil.getInstance(activity).write("modle", "");

        try {
            iuhfService = UHFManager.getUHFService(this.context);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(this.context, "设备超高频模块不存在");
            isReady = false;
            return;
        }


        iuhfService.reg_handler(handler);

    }

    public boolean open(){
        if(isStart){
            return false;
        }

        if (openDevice()){
            isReady = false;
            return false;
        }

//        initSound();
        openUHF();
        isReady = true;

        return true;
    }

    public void close(){
        if(!isReady){
            return;
        }

        closeDevice();
//        closeSoundPool();
//        closeUHFServices();
    }

    public void release(){
        super.release();
        if(!isReady){
            return;
        }
        isReady = false;
        mHandlerThread.quit();

//        closeSoundPool();
        closeUHFServices();
    }

//    private void initSound() {
//        soundPool = new SoundPool(2, AudioManager.STREAM_RING, 0);
//        if (soundPool == null) {
//            LogUtil.e("Open sound failed");
//        }
//        soundId = soundPool.load("/system/media/audio/ui/VideoRecord.ogg", 0);
//        LogUtil.w( "id is " + soundId);
//    }

//    private void closeSoundPool() {
//        if (soundPool != null)
//            soundPool.release();
//    }

/**
     * @param
     * @return
     * @description 打开超高频读取RFID
     * @author zhangwenshuai1 2018/8/1
*/

    private void openUHF() {
        iuhfService.setOnInventoryListener(new OnSpdInventoryListener() {
            @Override
            public void getInventoryData(SpdInventoryData var1) {
//                soundPool.play(soundId, 1, 1, 0, 0, 1);
//                Log.d("EM55UHFRFIDHelper", "SpdInventoryData:{ EPC" + var1.getEpc() + ", Tid:" + var1.getTid() + ", Rssi:" + var1.getRssi() + "}");
                if (onUHFRFIDListener != null) {
                    onUHFRFIDListener.onUHFRFIDEpcReceived(var1.getEpc());
                }

            }
        });
        //取消掩码
        iuhfService.selectCard(1, "", false);
//        inventoryStart();
        isReady = true;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void inventoryStart() {
        if (!isStart && isReady){
            iuhfService.inventoryStart();
            isStart = true;
        }

    }


    public void inventoryStop() {
        if (isStart){
            iuhfService.inventoryStop();
            isStart = false;
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean isReady){
        this.isReady = isReady;
    }

    public boolean isStart(){
        return isStart;
    }

/**
     * @param
     * @return
     * @description
     * @author zhangwenshuai1 2018/8/1
*/

    private boolean openDevice() {
        try {
            if (iuhfService != null) {
                if (iuhfService.openDev() != 0) {
//                    ToastUtils.show(context, "设备打开失败");
                    return true;
                }
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public void closeDevice() {
        isReady = false;
        try {
            if (iuhfService != null) {
                iuhfService.closeDev();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


/**
     * @param
     * @return
     * @description 检测被接入的背夹具体型号
     * @author zhangwenshuai1 2018/8/1
*/

//    public String getEM55Model() {
//        String state = null;
//        File file = new File("/sys/class/misc/aw9523/gpio");
//        try {
//            FileReader fileReader = new FileReader(file);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            state = bufferedReader.readLine();
//            bufferedReader.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "readEm55state: " + state);
//        return state;
//    }

/**
     * @param
     * @return
     * @description 关闭超高频服务
     * @author zhangwenshuai1 2018/8/1
*/

    public void closeUHFServices() {
        UHFManager.closeUHFService();
    }

    public void setOnUHFRFIDListener(OnUHFRFIDListener onUHFRFIDListener) {
        this.onUHFRFIDListener = onUHFRFIDListener;
    }

    public interface OnUHFRFIDListener {
        void onUHFRFIDEpcReceived(String epcCode);
    }



}
