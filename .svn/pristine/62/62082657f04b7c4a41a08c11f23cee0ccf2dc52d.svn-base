package com.supcon.mes.module_sbda_online.ui;

import android.support.design.widget.TabLayout;

import com.app.annotation.BindByTag;
import com.app.annotation.apt.Router;
import com.supcon.common.view.base.activity.BaseFragmentActivity;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomTitleBar;
import com.supcon.mes.mbap.view.NoScrollViewPager;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.module_sbda_online.R;
import com.supcon.mes.module_sbda_online.ui.adapter.SBDAOnlineViewAdapter;
import com.supcon.mes.module_sbda_online.ui.fragment.LubricationFragment;
import com.supcon.mes.module_sbda_online.ui.fragment.MaintenanceFragment;
import com.supcon.mes.module_sbda_online.ui.fragment.ParamFragment;
import com.supcon.mes.module_sbda_online.ui.fragment.RepairFragment;
import com.supcon.mes.module_sbda_online.ui.fragment.RoutineFragment;
import com.supcon.mes.module_sbda_online.ui.fragment.SparePartFragment;
import com.supcon.mes.module_sbda_online.ui.fragment.SubsidiaryFragment;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/3/29
 * ------------- Description -------------
 */
@Router(Constant.Router.SBDA_ONLINE_VIEW)
public class SBDAOnlineViewActivity extends BaseFragmentActivity {

    @BindByTag("titleBar")
    CustomTitleBar titleBar;
    @BindByTag("tablayout")
    TabLayout tablayout;
    @BindByTag("sbdaViewPager")
    NoScrollViewPager sbdaViewPager;
    private Long eamId;
    private String eamCode;

    @Override
    protected int getLayoutID() {
        return R.layout.ac_sbda_online_detail;
    }

    @Override
    protected void onInit() {
        super.onInit();
        eamId = getIntent().getLongExtra(Constant.IntentKey.SBDA_ONLINE_EAMID, 0L);
        eamCode = getIntent().getStringExtra(Constant.IntentKey.SBDA_ONLINE_EAMCODE);
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);

        tablayout.setupWithViewPager(sbdaViewPager);
        SBDAOnlineViewAdapter sbdaOnlineViewAdapter = new SBDAOnlineViewAdapter(getSupportFragmentManager());
        sbdaOnlineViewAdapter.addFragment(RoutineFragment.newInstance(eamId, eamCode), "常规信息");
        sbdaOnlineViewAdapter.addFragment(SubsidiaryFragment.newInstance(eamId), "附属设备");
        sbdaOnlineViewAdapter.addFragment(SparePartFragment.newInstance(eamId), "备件清单");
        sbdaOnlineViewAdapter.addFragment(ParamFragment.newInstance(eamId), "技术参数");
        sbdaOnlineViewAdapter.addFragment(LubricationFragment.newInstance(eamId), "润滑信息");
        sbdaOnlineViewAdapter.addFragment(MaintenanceFragment.newInstance(eamId), "维保信息");
        sbdaOnlineViewAdapter.addFragment(RepairFragment.newInstance(eamId), "维修信息");
        sbdaViewPager.setAdapter(sbdaOnlineViewAdapter);

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
}
