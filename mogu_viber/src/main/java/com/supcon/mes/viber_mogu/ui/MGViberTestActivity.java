package com.supcon.mes.viber_mogu.ui;

import android.annotation.SuppressLint;

import com.app.annotation.Controller;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.mes.viber_mogu.R;
import com.supcon.mes.viber_mogu.controller.MGViberController;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2019/1/9
 * Email:wangshizhan@supcom.com
 */
@Router("MOGU_VIBER_TEST")
@Controller(MGViberController.class)
public class MGViberTestActivity extends BaseControllerActivity {


    @Override
    protected int getLayoutID() {
        return R.layout.ac_viber;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();
        RxView.clicks(findViewById(R.id.leftBtn))
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
