package com.supcon.mes.module_warn.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;

import com.app.annotation.BindByTag;
import com.app.annotation.apt.Router;
import com.supcon.common.view.base.activity.BaseFragmentActivity;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomTitleBar;
import com.supcon.mes.mbap.view.NoScrollViewPager;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.event.NFCEvent;
import com.supcon.mes.middleware.util.NFCHelper;
import com.supcon.mes.module_warn.R;
import com.supcon.mes.module_warn.model.event.TabEvent;
import com.supcon.mes.module_warn.ui.adapter.DailyLubricationWarnAdapter;
import com.supcon.mes.module_warn.ui.fragment.DailyLubricateReceiveTaskFragment;
import com.supcon.mes.module_warn.ui.fragment.DailyLubricateTaskFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/4/29
 * ------------- Description -------------
 * 计划润滑
 */
@Router(Constant.Router.PLAN_LUBRICATION_EARLY_WARN)
public class PlanLubricationWarnActivity extends BaseFragmentActivity {
    @BindByTag("titleBar")
    CustomTitleBar titleBar;
    @BindByTag("tablayout")
    TabLayout tablayout;
    @BindByTag("sbdaViewPager")
    NoScrollViewPager sbdaViewPager;

    private NFCHelper nfcHelper;

    @Override
    protected int getLayoutID() {
        return R.layout.ac_daily_list;
    }

    @Override
    protected void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
        nfcHelper = NFCHelper.getInstance();
        if (nfcHelper != null) {
            nfcHelper.setup(this);
            nfcHelper.setOnNFCListener(new NFCHelper.OnNFCListener() {
                @Override
                public void onNFCReceived(String nfc) {
                    LogUtil.d("NFC Received : " + nfc);
                    EventBus.getDefault().post(new NFCEvent(nfc));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nfcHelper != null)
            nfcHelper.onResumeNFC(this);
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleBar.setTitle("计划润滑");
        tablayout.setupWithViewPager(sbdaViewPager);

        DailyLubricationWarnAdapter dailyLubricationWarnAdapter = new DailyLubricationWarnAdapter(getSupportFragmentManager());
        dailyLubricationWarnAdapter.addFragment(DailyLubricateTaskFragment.newInstance(), "计划润滑任务领取");
        dailyLubricationWarnAdapter.addFragment(DailyLubricateReceiveTaskFragment.newInstance(), "计划润滑任务执行");
        sbdaViewPager.setAdapter(dailyLubricationWarnAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnTitleBarListener(new CustomTitleBar.OnTitleBarListener() {
            @Override
            public void onLeftBtnClick() {
                back();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void tabChange(TabEvent tabEvent) {
        tablayout.getTabAt(1).select();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //获取到Tag对象
        if (nfcHelper != null)
            nfcHelper.dealNFCTag(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcHelper != null)
            nfcHelper.onPauseNFC(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (nfcHelper != null) {
            nfcHelper.release();
        }
    }
}
