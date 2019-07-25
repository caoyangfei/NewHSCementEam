package com.supcon.mes.viber_mogu.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.controller.BaseViewController;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.common.view.view.custom.ICustomView;
import com.supcon.mes.mbap.utils.controllers.SinglePickController;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.viber_mogu.R;
import com.supcon.mes.viber_mogu.config.ModuleConfig;
import com.supcon.mes.viber_mogu.config.ViberMode;
import com.supcon.mes.viber_mogu.model.api.MGViberAPI;
import com.supcon.mes.viber_mogu.model.contract.MGViberContract;
import com.supcon.mes.viber_mogu.model.model.BleSearchEntity;
import com.supcon.mes.viber_mogu.presenter.MGViberPresenter;
import com.supcon.mes.viber_mogu.util.LocationUtil;
import com.sytest.app.blemulti.BleDevice;
import com.sytest.app.blemulti.Rat;
import com.sytest.app.blemulti.easy.Recipe;
import com.sytest.app.blemulti.exception.BleException;
import com.sytest.app.blemulti.interfaces.Battery_CB;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2019/1/23
 * Email:wangshizhan@supcom.com
 */
@Presenter(MGViberPresenter.class)
public class MGViberController extends BaseViewController implements MGViberContract.View{

    @BindByTag("viberBtn")
    TextView viberBtn;

    @BindByTag("viberFinishBtn")
    TextView viberFinishBtn;

    @BindByTag("viberStatusIv")
    ImageView viberStatusIv;

    @BindByTag("viberStatus")
    CustomTextView viberStatus;

    @BindByTag("viberData")
    ICustomView viberData;

    @BindByTag("viberDataUnit")
    TextView viberDataUnit;

    @BindByTag("viberBatteryStatusIv")
    ImageView viberBatteryStatusIv;

    @BindByTag("viberBattery")
    CustomTextView viberBattery;

    @BindByTag("viberModeSpinner")
    Spinner viberModeSpinner;

    Rat mRat;
    BleDevice viberDevice;
    private List<String> viberModes = new ArrayList<>();
//    private SinglePickController<String> mSinglePickController;
    private ViberMode currentMode;
    private boolean isStartTest = false;
    private boolean isTemperatureNeed = false;

    public MGViberController(View rootView) {
        super(rootView);
    }

    @Override
    public void onInit() {
        super.onInit();
        Rat.initilize(context);
        mRat = Rat.getInstance();
        viberDevice = mRat.getFirstBleDevice();
        mRat.enableBluetooth();
    }

    @Override
    public void initView() {
        super.initView();
        if(viberDevice!=null) {
            viberStatus.setContent(viberDevice.toString().split(" ")[0]);
            viberStatusIv.setImageResource(R.drawable.ic_device_connect);
            viberBtn.setEnabled(true);
        }
        else{
            viberStatus.setContent(R.string.vibrite_searching);
            viberBtn.setEnabled(false);
            viberBtn.setTextColor(context.getResources().getColor(R.color.bgGray2));
        }
        initViberMode();
        viberData.setContent("0.00");
    }

    private void initViberMode() {

        String modeName = SharedPreferencesUtils.getParam(context, ModuleConfig.CURRENT_MODE, ViberMode.DISPLACEMENT.name());
        currentMode = ViberMode.getMode(modeName);

        for(ViberMode viberMode:ViberMode.values()){

            if(viberMode == ViberMode.TEMPERATURE && !isTemperatureNeed){
                continue;
            }

            if(LocationUtil.isZh(context))
                viberModes.add(viberMode.modeName());
            else
                viberModes.add(viberMode.name());

        }

//        if(mSinglePickController == null) {
//            mSinglePickController = new SinglePickController<>((Activity) context);
//            mSinglePickController.setDividerVisible(true);
//            mSinglePickController.setCycleDisable(true);
//            mSinglePickController.list(viberModes);
//        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, viberModes);  //创建一个数组适配器
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
        viberModeSpinner.setAdapter(adapter);

        if(LocationUtil.isZh(context))
            viberModeSpinner.setSelection(viberModes.indexOf(currentMode.modeName()));
        else
            viberModeSpinner.setSelection(viberModes.indexOf(currentMode.name()));

    }



    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        super.initListener();
//        viberModeSpinner.setOnChildViewClickListener(new OnChildViewClickListener() {
//            @Override
//            public void onChildViewClick(View childView, int action, Object obj) {
//                if(action!= MBapConstant.ViewAction.CONTENT_CLEAN){
//                    mSinglePickController.listener(new SinglePicker.OnItemPickListener<String>() {
//                        @Override
//                        public void onItemPicked(int index, String item) {
//                            currentMode = ViberMode.getMode(item);
//                            viberModeSpinner.setContent(item);
//                            updateViberMode();
//                            stopTest();
//                        }
//                    }).show(currentMode==null?0:viberModeSpinner.getSpinnerValue());
//                }
//                else{
//                    currentMode = null;
//                }
//            }
//        });


        viberModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = viberModes.get(position);
                currentMode = ViberMode.getMode(item);
                SharedPreferencesUtils.setParam(context, ModuleConfig.CURRENT_MODE, currentMode.name());
                updateViberMode();
                viberData.setContent("0.00");
                if(isStartTest){
                    stopTest();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentMode = null;
            }
        });

        RxView.clicks(viberBtn)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {

                        if(isStartTest){
                            stopTest();
                        }
                        else {

                            startTest();
                        }
                    }
                });

//        RxView.clicks(viberFinishBtn)
//                .throttleFirst(2, TimeUnit.SECONDS)
//                .subscribe(o -> {
//
//
//
//                });
    }

    public boolean isTemperatureNeed() {
        return isTemperatureNeed;
    }

    public void setTemperatureNeed(boolean temperatureNeed) {
        isTemperatureNeed = temperatureNeed;
    }

    public void startTest() {
        viberBtn.setText(R.string.stop_test);
        viberBtn.setTextColor(context.getResources().getColor(R.color.customRed));
        presenterRouter.create(MGViberAPI.class).startViberTest(currentMode.mode(), viberDevice);
        isStartTest = true;
    }

    public void stopTest() {
        viberBtn.setText(R.string.start_test);
        viberBtn.setTextColor(context.getResources().getColor(R.color.customBlue4));
        presenterRouter.create(MGViberAPI.class).stopViberTest();
        isStartTest = false;
    }

    private void initBattery() {
        Recipe.newInstance(viberDevice).getBattery(new Battery_CB() {
            @Override
            public void onBattery_UI(float v) {
                updateBatteryView(v);
            }

            @Override
            public void onFail_UI(@NonNull BleException e) {

            }
        });
    }

    private void updateViberMode() {

        switch (currentMode){

            case DISPLACEMENT:
                viberDataUnit.setText("um");
                break;
            case VELOCITY:
                viberDataUnit.setText("mm/s");
                break;
            case ACCELERATION:
                viberDataUnit.setText("m/s2");
                break;
            case TEMPERATURE:
                viberDataUnit.setText("℃");
                break;
            case ENVELOPE:
                viberDataUnit.setText("m/s2");
                break;

        }
    }

    private void updateBatteryView(float battery) {
        float batteryShow =  battery*100;
        if(batteryShow < 25){
            viberBatteryStatusIv.setImageResource(R.drawable.ic_battery1);
        }
        else if(batteryShow < 50){
            viberBatteryStatusIv.setImageResource(R.drawable.ic_battery2);
        }
        else if(batteryShow < 75){
            viberBatteryStatusIv.setImageResource(R.drawable.ic_battery3);
        }
        else{
            viberBatteryStatusIv.setImageResource(R.drawable.ic_battery4);
        }

        viberBattery.setContent(new BigDecimal(battery*100f).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()+"%");

    }

    public void reset(){

        viberData.setContent("0.00");

    }

    @SuppressLint("CheckResult")
    @Override
    public void initData() {
        super.initData();

        Flowable.timer(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        presenterRouter.create(MGViberAPI.class).search();
                    }
                });
    }

    @Override
    public void searchSuccess(BleSearchEntity entity) {
        presenterRouter.create(MGViberAPI.class).connect(entity.address);
    }

    @Override
    public void searchFailed(String errorMsg) {

    }

    @Override
    public void connectSuccess(Boolean entity) {
        viberStatus.setContent(R.string.vibrite_connect);
        mRat = Rat.getInstance();
        viberDevice = mRat.getFirstBleDevice();
        String deviceName = viberDevice.toString().split(" ")[0];
        viberStatus.setContent(deviceName);
        viberStatusIv.setImageResource(R.drawable.ic_device_connect);

        if(viberDevice!=null){
            viberBtn.setEnabled(true);
            viberBtn.setTextColor(context.getResources().getColor(R.color.customBlue4));
//            startTest();
        }
        initBattery();
    }

    @Override
    public void connectFailed(String errorMsg) {

    }

    @Override
    public void startViberTestSuccess(String entity) {

        LogUtil.d("onValue_UI v:"+entity);
        if(isStartTest)
            viberData.setContent(new BigDecimal(entity).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
    }

    @Override
    public void startViberTestFailed(String errorMsg) {
        LogUtil.d("onValue_UI v:"+errorMsg);
        if(!TextUtils.isEmpty(errorMsg) && errorMsg.contains("写数据超时")&&isStartTest){
            ToastUtils.show(context, R.string.vibrite_read_timeout2);
        }

    }

    @Override
    public void stopViberTestSuccess() {

    }

    @Override
    public void stopViberTestFailed(String errorMsg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenterRouter.create(MGViberAPI.class).stopViberTest();
        mRat.disableBluetooth();
    }

    public ViberMode getCurrentMode() {
        return currentMode;
    }

    public String getData(){

        return viberData.getContent();

    }
}





