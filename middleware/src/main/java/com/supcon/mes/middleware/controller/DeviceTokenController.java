package com.supcon.mes.middleware.controller;

import android.content.Context;
import android.text.TextUtils;

import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.supcon.common.view.App;
import com.supcon.common.view.base.controller.BaseDataController;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.api.DeviceTokenAPI;
import com.supcon.mes.middleware.model.contract.DeviceTokenContract;
import com.supcon.mes.middleware.presenter.DeviceTokenPresenter;
import com.supcon.mes.push.event.DeviceTokenEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by wangshizhan on 2019/4/29
 * Email:wangshizhan@supcom.com
 */

@Presenter(DeviceTokenPresenter.class)
public class DeviceTokenController extends BaseDataController implements DeviceTokenContract.View {

    private String mDeviceToken = "";

    public DeviceTokenController(Context context) {
        super(context);
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    public void initData() {
        super.initData();
        mDeviceToken = SharedPreferencesUtils.getParam(context, Constant.SPKey.DEVICE_TOKEN, "");
        if(!TextUtils.isEmpty(mDeviceToken)){
            EventBus.getDefault().post(new DeviceTokenEvent(mDeviceToken));
        }
    }

    public String getDeviceToken() {
        return mDeviceToken;
    }

    public void cleanDeviceToken(){
        if(!TextUtils.isEmpty(mDeviceToken)){
            presenterRouter.create(DeviceTokenAPI.class).sendLogoutDeviceToken(mDeviceToken);
        }
    }

    public void sendLoginDeviceToken(String deviceToken){
        if(TextUtils.isEmpty(deviceToken)){
            return;
        }
        this.mDeviceToken = deviceToken;
//        SharedPreferencesUtils.setParam(context, Constant.SPKey.DEVICE_TOKEN, deviceToken);

        presenterRouter.create(DeviceTokenAPI.class).sendLoginDeviceToken(deviceToken);
    }

    public void sendLogoutDeviceToken(String deviceToken){

        presenterRouter.create(DeviceTokenAPI.class).sendLogoutDeviceToken(deviceToken);
    }


    @Override
    public void sendLoginDeviceTokenSuccess() {
        LogUtil.e("DEVICE_TOKEN发送成功:"+mDeviceToken);
//        ToastUtils.show(context, "DEVICE_TOKEN发送成功");
    }

    @Override
    public void sendLoginDeviceTokenFailed(String errorMsg) {
        LogUtil.e(""+errorMsg);
    }

    @Override
    public void sendLogoutDeviceTokenSuccess() {
        LogUtil.e("DEVICE_TOKEN清除成功:"+mDeviceToken);
        //do nothing
//        SharedPreferencesUtils.setParam(context, Constant.SPKey.DEVICE_TOKEN, "");
//        ToastUtils.show(context, "DEVICE_TOKEN清除成功");
    }

    @Override
    public void sendLogoutDeviceTokenFailed(String errorMsg) {
        LogUtil.e(""+errorMsg);
    }
}
