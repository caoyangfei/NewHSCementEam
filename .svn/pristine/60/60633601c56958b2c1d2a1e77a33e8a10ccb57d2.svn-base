package com.supcon.mes.module_login.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.app.annotation.Presenter;
import com.supcon.common.view.base.controller.BasePresenterController;
import com.supcon.common.view.base.controller.BaseViewController;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.beans.LoginEvent;
import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.module_login.model.api.SilentLoginAPI;
import com.supcon.mes.module_login.model.bean.LoginEntity;
import com.supcon.mes.module_login.model.contract.SilentLoginContract;
import com.supcon.mes.module_login.presenter.SilentLoginPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2018/9/6
 * Email:wangshizhan@supcom.com
 */
@Presenter(SilentLoginPresenter.class)
public class SilentLoginController extends BasePresenterController implements SilentLoginContract.View, SilentLoginAPI{

    @SuppressLint("CheckResult")
    @Override
    public void dologinSuccess(LoginEntity entity) {
        LogUtil.d("后台登陆成功！");
        Flowable.timer(650, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                    EventBus.getDefault().post(new LoginEvent());
                });
    }

    @Override
    public void dologinFailed(String errorMsg) {
        LogUtil.e("后台登陆错误："+errorMsg);
        Toast.makeText(EamApplication.getAppContext(), "后台登陆错误："+errorMsg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void dologin(String username, String pwd) {
        presenterRouter.create(SilentLoginAPI.class).dologin(username, pwd);
    }

    public void silentLogin(){
        dologin(EamApplication.getUserName(), EamApplication.getPassword());
    }
}
