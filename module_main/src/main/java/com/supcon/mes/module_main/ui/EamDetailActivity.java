package com.supcon.mes.module_main.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseControllerActivity;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.beans.LoginEvent;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.controller.EamPicController;
import com.supcon.mes.middleware.model.bean.BapResultEntity;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.CommonEntity;
import com.supcon.mes.middleware.model.bean.EamType;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.module_login.model.bean.WorkInfo;
import com.supcon.mes.module_main.IntentRouter;
import com.supcon.mes.module_main.R;
import com.supcon.mes.module_main.model.api.ScoreEamAPI;
import com.supcon.mes.module_main.model.api.WaitDealtAPI;
import com.supcon.mes.module_main.model.contract.ScoreEamContract;
import com.supcon.mes.module_main.model.contract.WaitDealtContract;
import com.supcon.mes.module_main.presenter.ScoreEamPresenter;
import com.supcon.mes.module_main.presenter.WaitDealtPresenter;
import com.supcon.mes.module_main.ui.adaper.AnomalyAdapter;
import com.supcon.mes.module_main.ui.adaper.WorkAdapter;
import com.supcon.mes.module_main.ui.view.SimpleRatingBar;
import com.supcon.mes.module_main.ui.view.TrapezoidView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/30
 * ------------- Description -------------
 * 设备详情
 */
@Router(Constant.Router.EAM_DETAIL)
@Presenter(value = {WaitDealtPresenter.class, ScoreEamPresenter.class})
public class EamDetailActivity extends BaseControllerActivity implements WaitDealtContract.View, ScoreEamContract.View {

    @BindByTag("leftBtn")
    ImageButton leftBtn;

    ImageView eamPic;
    //待办
    @BindByTag("anomalyLayout")
    LinearLayout anomalyLayout;
    @BindByTag("anomalyRecycler")
    RecyclerView anomalyRecycler;
    @BindByTag("eamWorkRecycler")
    RecyclerView eamWorkRecycler;
    @BindByTag("eamName")
    TrapezoidView eamName;

    @BindByTag("starLevel")
    SimpleRatingBar starLevel;
    @BindByTag("eamScore")
    TextView eamScore;

    private EamType eamType;
    private AnomalyAdapter anomalyAdapter;
    private WorkAdapter workAdapter;

    @Override
    protected int getLayoutID() {
        return R.layout.hs_ac_eam_detail;
    }

    @Override
    protected void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
        eamType = (EamType) getIntent().getSerializableExtra(Constant.IntentKey.EAM);
    }

    @Override
    protected void initView() {
        super.initView();
        View waitTitle = rootView.findViewById(R.id.hs_anomaly_title);
        ((TextView) waitTitle.findViewById(R.id.contentTitleLabel)).setText("异常记录");
        ImageView waitMore = waitTitle.findViewById(R.id.contentTitleSettingIc);
        waitMore.setVisibility(View.VISIBLE);
        waitMore.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.IntentKey.EAM, eamType);
            IntentRouter.go(this, Constant.Router.ANOMALY, bundle);
        });
        View workTitle = rootView.findViewById(R.id.hs_eam_work_title);
        ((TextView) workTitle.findViewById(R.id.contentTitleLabel)).setText("我的工作");
        eamPic = findViewById(R.id.eamPic);
        anomalyRecycler.setLayoutManager(new LinearLayoutManager(context));
        anomalyAdapter = new AnomalyAdapter(this);
        anomalyRecycler.setAdapter(anomalyAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
        eamWorkRecycler.setLayoutManager(layoutManager);
        workAdapter = new WorkAdapter(this);
        eamWorkRecycler.setAdapter(workAdapter);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();
        RxView.clicks(leftBtn)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        back();
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();
        List workInfos = new ArrayList<>();
        WorkInfo workInfo1 = new WorkInfo();
        workInfo1.name = "临时巡检";
        workInfo1.iconResId = R.drawable.menu_aew_selector;
        workInfos.add(workInfo1);
        WorkInfo workInfo2 = new WorkInfo();
        workInfo2.name = "临时润滑";
        workInfo2.iconResId = R.drawable.menu_lubricate_selector;
        workInfos.add(workInfo2);
        WorkInfo workInfo3 = new WorkInfo();
        workInfo3.name = "验收评分";
        workInfo3.iconResId = R.drawable.menu_score_selector;
        workInfos.add(workInfo3);
        WorkInfo workInfo4 = new WorkInfo();
        workInfo4.name = "文档记录";
        workInfo4.iconResId = R.drawable.menu_print_selector;
        workInfos.add(workInfo4);
        workAdapter.setList(workInfos);
        workAdapter.notifyDataSetChanged();

        eamName.setText(eamType.name);
        Map<String, Object> param = new HashMap<>();
        param.put(Constant.BAPQuery.EAMCODE, eamType.code);
        presenterRouter.create(WaitDealtAPI.class).getWaitDealt(1, 3, param);
        presenterRouter.create(ScoreEamAPI.class).getEamScore(eamType.id);
        new EamPicController().initEamPic(eamPic, eamType.id);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {
        Map<String, Object> param = new HashMap<>();
        param.put(Constant.BAPQuery.EAMCODE, eamType.code);
        presenterRouter.create(WaitDealtAPI.class).getWaitDealt(1, 3, param);
    }

    @Override
    public void getWaitDealtSuccess(CommonBAPListEntity entity) {
        if (entity.result.size() > 0) {
            anomalyLayout.setVisibility(View.GONE);
        } else {
            anomalyLayout.setVisibility(View.VISIBLE);
        }
        anomalyAdapter.setList(entity.result);
        anomalyAdapter.notifyDataSetChanged();
    }

    @Override
    public void getWaitDealtFailed(String errorMsg) {
        LogUtil.e("获取待办失败:" + errorMsg);
        if (errorMsg.contains("401")) {
            SnackbarHelper.showError(rootView, ErrorMsgHelper.msgParse(errorMsg));
        }
        anomalyLayout.setVisibility(View.VISIBLE);
        anomalyAdapter.setList(null);
        anomalyAdapter.notifyDataSetChanged();
    }

    @Override
    public void proxyPendingSuccess(BapResultEntity entity) {
        ToastUtils.show(this, "待办委托成功");
        Map<String, Object> param = new HashMap<>();
        param.put(Constant.BAPQuery.EAMCODE, eamType.code);
        presenterRouter.create(WaitDealtAPI.class).getWaitDealt(1, 3, param);
    }

    @Override
    public void proxyPendingFailed(String errorMsg) {
        ToastUtils.show(this, ErrorMsgHelper.msgParse(errorMsg));
    }

    @Override
    public void getEamScoreSuccess(CommonEntity entity) {
        eamScore.setText(((String) entity.result));
        float star = Float.valueOf((String) entity.result) / 20;
        starLevel.setRating(star);
    }

    @Override
    public void getEamScoreFailed(String errorMsg) {
        LogUtil.e(errorMsg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
