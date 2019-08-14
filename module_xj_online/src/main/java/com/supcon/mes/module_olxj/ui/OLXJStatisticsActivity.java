package com.supcon.mes.module_olxj.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.mes.mbap.beans.LoginEvent;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.controller.DatePickController;
import com.supcon.mes.middleware.util.AnimatorUtil;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.module_olxj.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/8/14
 * ------------- Description -------------
 */
@Router(Constant.Router.XJ_STATISTICS)
public class OLXJStatisticsActivity extends BaseRefreshRecyclerActivity {

    @BindByTag("leftBtn")
    ImageButton leftBtn;
    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("contentView")
    RecyclerView contentView;


    private View timeStart, timeEnd;
    private DatePickController datePickController;
    private ImageView startExpend, endExpend;
    private TextView startDate, endDate;

    Map<String, Object> queryParam = new HashMap<>();

    @Override
    protected IListAdapter createAdapter() {
        return null;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.ac_olxj_statistics;
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
        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(5, context)));
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(true);
        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, ""));

        timeStart = findViewById(R.id.statisticsStart);
        timeEnd = findViewById(R.id.statisticsEnd);
        startExpend = timeStart.findViewById(R.id.expend);
        startDate = timeStart.findViewById(R.id.dateTv);
        endExpend = timeEnd.findViewById(R.id.expend);
        endDate = timeEnd.findViewById(R.id.dateTv);

        datePickController.setCycleDisable(true);
        datePickController.setCanceledOnTouchOutside(true);
        datePickController.setSecondVisible(false);
        datePickController.textSize(18);
    }

    @Override
    protected void initData() {
        super.initData();
        titleText.setText("巡检统计");

        queryParam.put(Constant.BAPQuery.CREATE_DATE_START, DateUtil.dateFormat(getTimeOfMonthStart(), "yyyy-MM-dd 00:00:00"));
        queryParam.put(Constant.BAPQuery.CREATE_DATE_END, DateUtil.dateFormat(System.currentTimeMillis(), "yyyy-MM-dd 23:59:59"));
        startDate.setText(DateUtil.dateFormat(getTimeOfMonthStart(), "yyyy-MM-dd"));
        endDate.setText(DateUtil.dateFormat(System.currentTimeMillis(), "yyyy-MM-dd"));

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();
        RxView.clicks(leftBtn)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> onBackPressed());

        timeStart.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                AnimatorUtil.rotationExpandIcon(startExpend, 0, 180);
                datePickController.listener((year, month, day, hour, minute, second) -> {
                    startDate.setText(year + "-" + month + "-" + day);
                    queryParam.put(Constant.BAPQuery.CREATE_DATE_START, year + "-" + month + "-" + day + " 00:00:00");
                    refreshListController.refreshBegin();
                }).show(DateUtil.dateFormat(startDate.getText().toString()), startExpend);
            }
        });
        timeEnd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                AnimatorUtil.rotationExpandIcon(endExpend, 0, 180);
                datePickController.listener((year, month, day, hour, minute, second) -> {
                    startDate.setText(year + "-" + month + "-" + day);
                    queryParam.put(Constant.BAPQuery.CREATE_DATE_END, year + "-" + month + "-" + day + " 00:00:00");
                    refreshListController.refreshBegin();
                }).show(DateUtil.dateFormat(endDate.getText().toString()), endExpend);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {

        refreshListController.refreshBegin();
    }


    public static long getTimeOfMonthStart() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.clear(Calendar.MINUTE);
        ca.clear(Calendar.SECOND);
        ca.clear(Calendar.MILLISECOND);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        return ca.getTimeInMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
