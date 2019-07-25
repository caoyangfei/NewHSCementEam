package com.supcon.mes.viber_mogu.ui;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.view.custom.ICustomView;
import com.supcon.common.view.view.picker.SinglePicker;
import com.supcon.mes.mbap.MBapConstant;
import com.supcon.mes.mbap.constant.ViewAction;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.utils.controllers.SinglePickController;
import com.supcon.mes.mbap.view.CustomSpinner;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.mbap.view.CustomVerticalTextView;
import com.supcon.mes.viber_mogu.R;
import com.supcon.mes.viber_mogu.config.ModuleConfig;
import com.supcon.mes.viber_mogu.config.ViberMode;
import com.supcon.mes.viber_mogu.model.api.MGViberAPI;
import com.sytest.app.blemulti.BleDevice;
import com.sytest.app.blemulti.Rat;
import com.sytest.app.blemulti.easy.Recipe;
import com.sytest.app.blemulti.exception.BleException;
import com.sytest.app.blemulti.interfaces.Battery_CB;
import com.sytest.app.blemulti.interfaces.Value_CB;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.sytest.app.blemulti.data.B1_SampleData.SignalType;

/**
 * Created by wangshizhan on 2019/1/9
 * Email:wangshizhan@supcom.com
 */
@Router("MOGU_VIBER")
public class MGViberActivity extends BaseControllerActivity {

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("viberBtn")
    Button viberBtn;

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
    CustomSpinner viberModeSpinner;

    Rat mRat;
    BleDevice viberDevice;
    Disposable viberTimer;
    int themeColorResId;

    private List<String> viberModes = new ArrayList<>();
    private SinglePickController<String> mSinglePickController;
    private ViberMode currentMode;

    @Override
    protected int getLayoutID() {
        return R.layout.ac_viber;
    }


    @Override
    protected void onInit() {
        super.onInit();
        mRat = Rat.getInstance();
        themeColorResId = getIntent().getIntExtra(ModuleConfig.THEME_COLOR, R.color.equipmentThemeColor);
        viberDevice = mRat.getFirstBleDevice();

    }


    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, themeColorResId);

        if(viberDevice!=null) {
            viberStatus.setContent(viberDevice.toString().split(" ")[0]);
            viberStatusIv.setImageResource(R.drawable.ic_device_connect);
        }
        initViberMode();
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

    private void initViberMode() {

        for(ViberMode viberMode:ViberMode.values()){
            viberModes.add(viberMode.modeName());
        }

        if(mSinglePickController == null) {
            mSinglePickController = new SinglePickController<>(this);
            mSinglePickController.setDividerVisible(true);
            mSinglePickController.setCycleDisable(true);
            mSinglePickController.list(viberModes);
        }

        currentMode = ViberMode.DISPLACEMENT;
        viberModeSpinner.setContent(currentMode.modeName());
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();

        viberModeSpinner.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                if(action!= ViewAction.CONTENT_CLEAN.value()){
                    mSinglePickController.listener(new SinglePicker.OnItemPickListener<String>() {
                        @Override
                        public void onItemPicked(int index, String item) {
                            currentMode = ViberMode.getMode(item);
                            viberModeSpinner.setContent(item);
                            presenterRouter.create(MGViberAPI.class).stopViberTest();
                            updateViberMode();
                        }
                    }).show(currentMode==null?0:viberModeSpinner.getSpinnerValue());
                }
                else{
                    currentMode = null;
                }
            }
        });


        RxView.clicks(viberBtn)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        if(viberTimer == null){
                            startViber();
                        }
                        else{
                            stopViber();
                        }


                    }
                });

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
                viberDataUnit.setText("mm");
                break;
            case VELOCITY:
            case ACCELERATION:
                viberDataUnit.setText("m/s");
                break;
            case TEMPERATURE:
                viberDataUnit.setText("℃");
                break;
            case ENVELOPE:
                viberDataUnit.setText("mm");
                break;

        }
    }


    private void startViber(){
        viberTimer = Flowable.interval(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        getViberData();
                    }
                });
        viberBtn.setText("正在测振...");
    }

    @SuppressLint("CheckResult")
    private void stopViber(){
        viberBtn.setText("开始测振");
        viberTimer.dispose();
        viberTimer = null;
//        Flowable.just(viberDevice)
//                .subscribe(new Consumer<BleDevice>() {
//                    @Override
//                    public void accept(BleDevice bleDevice) throws Exception {
//                        bleDevice.release();
//                    }
//                });



    }

    /**
     * public static final byte ACCELERATION = 0;加速度
     * public static final byte VELOCITY = 1;   速度
     * public static final byte DISPLACEMENT = 2;位移
     * public static final byte TEMPERATURE = -60;
     * public static final byte ENVELOPE = 3;
     *
     * */
    private void getViberData() {

//        byte mode = -1;
//        if("位移模式".equals(currentMode)){
//            mode = SignalType.DISPLACEMENT;
//            viberDataUnit.setText("mm");
//        }
//        else if("速度模式".equals(currentMode)){
//            mode = SignalType.VELOCITY;
//            viberDataUnit.setText("m/s");
//        }
//        else if("加速度模式".equals(currentMode)){
//            mode = SignalType.ACCELERATION;
//            viberDataUnit.setText("m/s");
//        }
//        else if("测温模式".equals(currentMode)){
//            mode = SignalType.TEMPERATURE;
//            viberDataUnit.setText("℃");
//        }
        byte mode = currentMode.mode();

        if(mode!=-1)
        Recipe.newInstance(viberDevice).getValue_Z(mode , false, new Value_CB() {
            @Override
            public void onValue_UI(float v, float v1, float v2) {
                LogUtil.d("onValue_UI v:"+v+" v1:"+v1+" v2:"+v2);
                viberData.setContent(new BigDecimal(v2).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
            }

            @Override
            public void onFail_UI(@NonNull BleException e) {
                LogUtil.e("onFail_UI");
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(viberTimer!=null)
            viberTimer.dispose();
    }
}
