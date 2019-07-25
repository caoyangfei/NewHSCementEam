package com.supcon.mes.middleware.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.supcon.common.view.base.controller.BaseDataController;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.middleware.model.event.BarcodeEvent;
import com.supcon.mes.middleware.model.event.ThermometerEvent;
import com.supcon.mes.middleware.model.event.UhfRfidEvent;
import com.supcon.mes.middleware.util.EM55UHFRFIDHelper;
import com.supcon.mes.middleware.util.SB2BarcodeHelper;
import com.supcon.mes.middleware.util.SP2ThermometerHelper;
import com.supcon.mes.middleware.util.SoundHelper;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wangshizhan on 2018/9/10
 * Email:wangshizhan@supcom.com
 */
public class SB2Controller extends BaseDataController {

    private SB2BarcodeHelper sb2BarcodeHelper;
    private SP2ThermometerHelper sp2ThermometerHelper;
    private EM55UHFRFIDHelper mEM55UHFRFIDHelper;
    private Activity app;
    private SoundHelper mSoundHelper;
    public SB2Controller(Context context){
        super(context);
        this.app = (Activity) context;
//        EventBus.getDefault().register(this);
        mSoundHelper = new SoundHelper();
        mSoundHelper.initSoundPool(app);
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

/*    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceAttached(SB2AttachEvent sb2AttachEvent){

        if(mEM55UHFRFIDHelper!=null){
            mEM55UHFRFIDHelper.setReady(false);
        }

//        if(sb2AttachEvent.isAttached()){
//            initEM55();
//        }

    }*/

    private void initSB2(){
        LogUtil.d("thread :"+Thread.currentThread().getName());
        initBarcodeHelper();
        initThermometer();
//        initEM55();
    }

    private void releaseSB2(){
        LogUtil.d("thread :"+Thread.currentThread().getName());
        if (sb2BarcodeHelper != null)
            sb2BarcodeHelper.release();

        if(sp2ThermometerHelper!=null){
            sp2ThermometerHelper.release();
        }

        if(mEM55UHFRFIDHelper!=null){
            mEM55UHFRFIDHelper.release();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void initData() {
        super.initData();
//        Flowable.just(true)
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        initSB2();
//                    }
//                });
        initSB2();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        releaseSB2();
        mSoundHelper.release();

    }

    /**
     * @description 扫描
     * @author zhangwenshuai1
     * @date 2018/4/28
     */
    private void initBarcodeHelper(){
        sb2BarcodeHelper = SB2BarcodeHelper.getInstance();
        sb2BarcodeHelper.setup(app);
        sb2BarcodeHelper.setOnBarcodeListener(barcode -> EventBus.getDefault().post(new BarcodeEvent(barcode)));
    }


    /**
     * @description 测温
     * @author zhangwenshuai1
     * @date 2018/4/28
     */
    private void initThermometer() {
        sp2ThermometerHelper = SP2ThermometerHelper.getInstance();
        sp2ThermometerHelper.setup(app);
        sp2ThermometerHelper.setOnThermometerListener(thermometerVal -> {
            LogUtil.i("EventBus post time :"+ DateUtil.dateTimeFormat(System.currentTimeMillis()));
            mSoundHelper.play(2, 0);
            EventBus.getDefault().post(new ThermometerEvent(thermometerVal));
        });


    }

    int count = 0;
    /**
     * @description 超高频
     * @author zhangwenshuai1
     * @date 2018/4/28
*/
    private void initEM55(){

        mEM55UHFRFIDHelper = EM55UHFRFIDHelper.getInstance();
        mEM55UHFRFIDHelper.setupUHFServices(app);
        mEM55UHFRFIDHelper.setOnUHFRFIDListener(epcCode -> {

            count ++;
            if(count == 10){
                LogUtil.d("EM55UHFRFIDHelper"+"EPC:" + epcCode);
                mSoundHelper.play(4, 0);
                EventBus.getDefault().post(new UhfRfidEvent(epcCode));
                count = 0;
            }
        });
    }

}
