package com.supcon.mes.module_login.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.mes.mbap.MBapApp;
import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.module_login.IntentRouter;
import com.supcon.mes.module_login.R;
import com.supcon.mes.module_login.service.HeartBeatService;

/**
 * Created by wangshizhan on 2017/8/11.
 */

public class WelcomeActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean isHS = true;
        if (EamApplication.isHongshi()) {
            isHS = true;
            getWindow().getDecorView().setBackgroundResource(R.drawable.layer_welcome_hs);
        } else {
            isHS = false;
            getWindow().getDecorView().setBackgroundResource(R.drawable.layer_welcome_hl);
        }
        super.onCreate(savedInstanceState);
        StatusBarUtils.setWindowStatusBarColor(this, R.color.white);
        boolean finalIsHS = isHS;
        new Handler().postDelayed(() -> {
            if (MBapApp.isIsLogin()) {
                if (EamApplication.isHongshi()) {
                    IntentRouter.go(WelcomeActivity.this, Constant.Router.MAIN_REDLION);
                } else {
                    IntentRouter.go(WelcomeActivity.this, Constant.Router.MAIN_REDLION);
                }
                //在线模式使用心跳防止session过期
                HeartBeatService.startLoginLoop(this);
            } else {
                Bundle bundle = new Bundle();
                if (!finalIsHS) {
                    bundle.putInt(Constant.IntentKey.LOGIN_BG_ID, R.drawable.bg_login_hl);
                    bundle.putInt(Constant.IntentKey.LOGIN_LOGO_ID, R.drawable.ic_login_logo_hl);
                }
                bundle.putBoolean(Constant.IntentKey.FIRST_LOGIN, true);
                IntentRouter.go(WelcomeActivity.this, Constant.Router.LOGIN, bundle);
            }

            finish();
        }, 100);
    }


}
