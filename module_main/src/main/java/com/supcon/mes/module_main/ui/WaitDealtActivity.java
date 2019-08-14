package com.supcon.mes.module_main.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
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
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.BapResultEntity;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.CommonSearchStaff;
import com.supcon.mes.middleware.model.event.CommonSearchEvent;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_main.IntentRouter;
import com.supcon.mes.module_main.R;
import com.supcon.mes.module_main.model.api.WaitDealtAPI;
import com.supcon.mes.module_main.model.bean.WaitDealtEntity;
import com.supcon.mes.module_main.model.contract.WaitDealtContract;
import com.supcon.mes.module_main.presenter.WaitDealtPresenter;
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
 */
@Presenter(value = WaitDealtPresenter.class)
@Router(value = Constant.Router.WAIT_DEALT)
public class WaitDealtActivity extends BaseRefreshRecyclerActivity<WaitDealtEntity> implements WaitDealtContract.View {

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("leftBtn")
    ImageButton leftBtn;
    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("waitState")
    RadioGroup waitState;

    private WaitDealtAdapter waitDealtAdapter;
    private CustomDialog customDialog;
    private CommonSearchStaff proxyStaff;
    private String reason;
    private Map<String, Object> queryParam = new HashMap<>();

    @Override
    protected IListAdapter<WaitDealtEntity> createAdapter() {
        waitDealtAdapter = new WaitDealtAdapter(this);
        return waitDealtAdapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.hs_ac_dealt;
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
        titleText.setText("工作提醒");
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

                presenterRouter.create(WaitDealtAPI.class).getWaitDealt(pageIndex, 20, queryParam);
            }
        });
        waitDealtAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
                WaitDealtEntity waitDealtEntity = (WaitDealtEntity) obj;
                if (childView.getId() == R.id.waitDealtEntrust) {
                    proxyDialog(waitDealtEntity);
                }
            }
        });

        waitState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                queryParam.remove(Constant.BAPQuery.OVERALLSTATE);
                queryParam.remove(Constant.BAPQuery.OVERDATEFLAG);
                if (radioButton.isChecked()) {
                    if (radioButton.getText().toString().equals("超期")) {
                        queryParam.put(Constant.BAPQuery.OVERDATEFLAG, "1");
                    } else {
                        queryParam.put(Constant.BAPQuery.OVERDATEFLAG, "0");
                        queryParam.put(Constant.BAPQuery.OVERALLSTATE, radioButton.getText().toString());
                    }
                }
                refreshListController.refreshBegin();
            }
        });
    }

    /**
     * 委托代办
     *
     * @param waitDealtEntity
     */
    private void proxyDialog(WaitDealtEntity waitDealtEntity) {
        customDialog = new CustomDialog(this).layout(R.layout.proxy_dialog,
                DisplayUtil.getScreenWidth(context) * 2 / 3, WRAP_CONTENT)
                .bindView(R.id.blueBtn, "确定")
                .bindView(R.id.grayBtn, "取消")
                .bindChildListener(R.id.proxyPerson, new OnChildViewClickListener() {
                    @Override
                    public void onChildViewClick(View childView, int action, Object obj) {
                        if (action == -1) {
                            proxyStaff = null;
                        }
                        IntentRouter.go(context, Constant.Router.STAFF);
                    }
                })
                .bindTextChangeListener(R.id.proxyReason, new OnTextListener() {
                    @Override
                    public void onText(String text) {
                        reason = text.trim();
                    }
                })
                .bindClickListener(R.id.blueBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v12) {
                        if (proxyStaff == null) {
                            ToastUtils.show(WaitDealtActivity.this, "请选择委托人");
                            return;
                        }
                        if (waitDealtEntity.pendingid == null) {
                            ToastUtils.show(WaitDealtActivity.this, "未获取当前代办信息");
                            return;
                        }
                        onLoading("正在委托...");
                        presenterRouter.create(WaitDealtAPI.class).proxyPending(waitDealtEntity.pendingid, proxyStaff.userId, reason);
                        customDialog.dismiss();
                    }
                }, false)
                .bindClickListener(R.id.grayBtn, null, true);
        ((CustomEditText) customDialog.getDialog().findViewById(R.id.proxyReason)).editText().setScrollBarSize(0);
        customDialog.getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        customDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {

        refreshListController.refreshBegin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void search(CommonSearchEvent commonSearchEvent) {
        if (commonSearchEvent.commonSearchEntity != null) {
            if (commonSearchEvent.commonSearchEntity instanceof CommonSearchStaff) {
                proxyStaff = (CommonSearchStaff) commonSearchEvent.commonSearchEntity;
                CustomTextView person = customDialog.getDialog().findViewById(R.id.proxyPerson);
                person.setContent(Util.strFormat(proxyStaff.name));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        refreshListController.refreshBegin();
    }

    @Override
    public void getWaitDealtSuccess(CommonBAPListEntity entity) {
        if (entity.result.size() > 0) {
            refreshListController.refreshComplete(entity.result);
        } else {
            refreshListController.refreshComplete(null);
        }
    }

    @Override
    public void getWaitDealtFailed(String errorMsg) {
        SnackbarHelper.showError(rootView, ErrorMsgHelper.msgParse(errorMsg));
        refreshListController.refreshComplete(null);
    }

    @Override
    public void proxyPendingSuccess(BapResultEntity entity) {
        onLoadSuccess("待办委托成功");
        refreshListController.refreshBegin();
    }

    @Override
    public void proxyPendingFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
