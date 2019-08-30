package com.supcon.mes.module_main.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.listener.OnItemChildViewClickListener;
import com.supcon.common.view.listener.OnRefreshPageListener;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.beans.LoginEvent;
import com.supcon.mes.mbap.listener.OnTextListener;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.controller.DatePickController;
import com.supcon.mes.middleware.model.bean.BapResultEntity;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.CommonSearchStaff;
import com.supcon.mes.middleware.model.event.CommonSearchEvent;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.util.AnimatorUtil;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_main.IntentRouter;
import com.supcon.mes.module_main.R;
import com.supcon.mes.module_main.model.api.ProcessedAPI;
import com.supcon.mes.module_main.model.api.WaitDealtAPI;
import com.supcon.mes.module_main.model.bean.ProcessedEntity;
import com.supcon.mes.module_main.model.bean.WaitDealtEntity;
import com.supcon.mes.module_main.model.contract.ProcessedContract;
import com.supcon.mes.module_main.model.contract.WaitDealtContract;
import com.supcon.mes.module_main.presenter.ProcessedPresenter;
import com.supcon.mes.module_main.presenter.WaitDealtPresenter;
import com.supcon.mes.module_main.ui.adaper.ProcessedAdapter;
import com.supcon.mes.module_main.ui.adaper.WaitDealtAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/25
 * ------------- Description -------------
 * 已处理的activity
 */
@Presenter(value = ProcessedPresenter.class)
@Router(value = Constant.Router.PROCESSED)
public class ProcessedActivity extends BaseRefreshRecyclerActivity<ProcessedEntity> implements ProcessedContract.View {

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("leftBtn")
    ImageButton leftBtn;
    @BindByTag("titleText")
    TextView titleText;
    @BindByTag("dateLayout")
    LinearLayout dateLayout;
    @BindByTag("dateTv")
    TextView dateTv;
    @BindByTag("expend")
    ImageView expend;

    private ProcessedAdapter processedAdapter;
    private DatePickController datePickController;
    private Map<String, Object> queryParam = new HashMap<>();

    @Override
    protected IListAdapter<ProcessedEntity> createAdapter() {
        processedAdapter = new ProcessedAdapter(this);
        return processedAdapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.hs_ac_processed;
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(true);
        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, null));
        contentView.setLayoutManager(new LinearLayoutManager(context));
        titleText.setText("我处理过的");

        datePickController = new DatePickController(this);

        datePickController.setCycleDisable(true);
        datePickController.setCanceledOnTouchOutside(true);
        datePickController.setSecondVisible(false);
        datePickController.textSize(18);

        dateTv.setText(DateUtil.dateFormat(System.currentTimeMillis(), "yyyy-MM-dd"));
        queryParam.put(Constant.BAPQuery.SCORE_DATA_START, DateUtil.dateFormat(System.currentTimeMillis(), "yyyy-MM-dd 00:00:00"));
        queryParam.put(Constant.BAPQuery.SCORE_DATA_STOP, DateUtil.dateFormat(System.currentTimeMillis(), "yyyy-MM-dd 23:59:59"));
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
        refreshListController.setOnRefreshPageListener(new OnRefreshPageListener() {
            @Override
            public void onRefresh(int pageIndex) {

                presenterRouter.create(ProcessedAPI.class).workflowHandleList(pageIndex);
            }
        });
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                AnimatorUtil.rotationExpandIcon(expend, 0, 180);
                datePickController.listener((year, month, day, hour, minute, second) -> {
                    dateTv.setText(year + "-" + month + "-" + day);
                    queryParam.put(Constant.BAPQuery.SCORE_DATA_START, year + "-" + month + "-" + day + " 00:00:00");
                    queryParam.put(Constant.BAPQuery.SCORE_DATA_STOP, year + "-" + month + "-" + day + " 23:59:59");
                    refreshListController.refreshBegin();
                }).show(DateUtil.dateFormat(dateTv.getText().toString()), expend);
            }
        });
        processedAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {

            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {

        refreshListController.refreshBegin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        refreshListController.refreshBegin();
    }

    @Override
    public void workflowHandleListSuccess(CommonBAPListEntity entity) {
        refreshListController.refreshComplete(entity.result);
    }

    @Override
    public void workflowHandleListFailed(String errorMsg) {
        SnackbarHelper.showError(rootView, ErrorMsgHelper.msgParse(errorMsg));
        refreshListController.refreshComplete(null);
    }

}
