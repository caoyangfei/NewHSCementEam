package com.supcon.mes.module_sbda_online.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.beans.LoginEvent;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.utils.controllers.DatePickController;
import com.supcon.mes.mbap.view.CustomFilterView;
import com.supcon.mes.mbap.view.CustomTitleBar;
import com.supcon.mes.mbap.view.CustomVerticalDateView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.module_sbda_online.R;
import com.supcon.mes.module_sbda_online.model.api.StopPoliceAPI;
import com.supcon.mes.middleware.model.bean.ScreenEntity;
import com.supcon.mes.module_sbda_online.model.bean.StopPoliceEntity;
import com.supcon.mes.module_sbda_online.model.bean.StopPoliceListEntity;
import com.supcon.mes.module_sbda_online.model.contract.StopPoliceContract;
import com.supcon.mes.module_sbda_online.presenter.StopPolicePresenter;
import com.supcon.mes.module_sbda_online.screen.FilterHelper;
import com.supcon.mes.module_sbda_online.ui.adapter.StopPoliceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Router(Constant.Router.STOP_POLICE)
@Presenter(value = StopPolicePresenter.class)
public class StopPoliceListActivity extends BaseRefreshRecyclerActivity<StopPoliceEntity> implements StopPoliceContract.View {

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("titleBar")
    CustomTitleBar titleBar;

    @BindByTag("listEamNameFilter")
    CustomFilterView listEamNameFilter;

    @BindByTag("stopPoliceStartTime")
    CustomVerticalDateView stopPoliceStartTime;
    @BindByTag("stopPoliceStopTime")
    CustomVerticalDateView stopPoliceStopTime;

    private Map<String, Object> queryParam = new HashMap<>();
    private DatePickController datePickController;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    @Override
    protected IListAdapter createAdapter() {
        StopPoliceAdapter stopPoliceAdapter = new StopPoliceAdapter(this);
        return stopPoliceAdapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.ac_stop_police_list;
    }

    @Override
    protected void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(true);
        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, null));

        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(5));

        listEamNameFilter.setData(FilterHelper.createEamNameFilter());

        datePickController = new DatePickController(this);
        datePickController.setCycleDisable(true);
        datePickController.setCanceledOnTouchOutside(true);
        datePickController.setSecondVisible(false);
        datePickController.textSize(18);
        stopPoliceStartTime.setDate("2019-04-01 00:00");
        stopPoliceStopTime.setDate(dateFormat1.format(System.currentTimeMillis()));
        queryParam.put(Constant.BAPQuery.OPEN_TIME_START, "2019-04-01 00:00:00");
        queryParam.put(Constant.BAPQuery.OPEN_TIME_STOP, dateFormat.format(System.currentTimeMillis()));
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

        refreshListController.setOnRefreshPageListener((page) -> {
            if (!queryParam.containsKey(Constant.BAPQuery.ON_OR_OFF)) {
                queryParam.put(Constant.BAPQuery.ON_OR_OFF, "BEAM020/02");
            }
            presenterRouter.create(StopPoliceAPI.class).runningGatherList(queryParam, page);
        });
        listEamNameFilter.setFilterSelectChangedListener(filterBean -> {
            queryParam.put(Constant.BAPQuery.EAM_NAME, !((ScreenEntity) filterBean).name.equals("设备不限") ? ((ScreenEntity) filterBean).name : "");
            doRefresh();
        });
        stopPoliceStartTime.setOnChildViewClickListener((childView, action, obj) -> {
            datePickController.listener((year, month, day, hour, minute, second) -> {
                stopPoliceStartTime.setDate(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                if (compareTime(stopPoliceStartTime.getContent(), stopPoliceStopTime.getContent())) {
                    if (!queryParam.containsKey(Constant.BAPQuery.OPEN_TIME_START)) {
                        queryParam.remove(Constant.BAPQuery.OPEN_TIME_START);
                    }
                    queryParam.put(Constant.BAPQuery.OPEN_TIME_START, year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00");
                    doRefresh();
                }

            }).show(System.currentTimeMillis());
        });

        stopPoliceStopTime.setOnChildViewClickListener((childView, action, obj) ->
                datePickController.listener((year, month, day, hour, minute, second) -> {
                    stopPoliceStopTime.setDate(year + "-" + month + "-" + day + " " + hour + ":" + minute);
                    if (compareTime(stopPoliceStartTime.getContent(), stopPoliceStopTime.getContent())) {
                        if (!queryParam.containsKey(Constant.BAPQuery.OPEN_TIME_STOP)) {
                            queryParam.remove(Constant.BAPQuery.OPEN_TIME_STOP);
                        }
                        queryParam.put(Constant.BAPQuery.OPEN_TIME_STOP, year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00");
                        doRefresh();
                    }
                }).show(System.currentTimeMillis()));
    }

    private void doRefresh() {
        refreshListController.refreshBegin();
    }

    @Override
    public void runningGatherListSuccess(StopPoliceListEntity entity) {
        refreshListController.refreshComplete(entity.result);
    }

    @Override
    public void runningGatherListFailed(String errorMsg) {
        SnackbarHelper.showError(rootView, ErrorMsgHelper.msgParse(errorMsg));
        refreshListController.refreshComplete(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {

        refreshListController.refreshBegin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(RefreshEvent event) {

        refreshListController.refreshBegin();
    }

    private boolean compareTime(String start, String stop) {
        if (TextUtils.isEmpty(start) || TextUtils.isEmpty(stop)) {
            return false;
        }
        try {
            long startTime = dateFormat1.parse(start).getTime();
            long stopTime = dateFormat1.parse(stop).getTime();
            if (stopTime > startTime) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ToastUtils.show(this, "开始时间不能大于结束时间!");
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
