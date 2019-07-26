package com.supcon.mes.module_main.ui.fragment;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.base.fragment.BaseControllerFragment;
import com.supcon.common.view.base.fragment.BaseRefreshRecyclerFragment;
import com.supcon.common.view.listener.OnRefreshPageListener;
import com.supcon.mes.mbap.beans.LoginEvent;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.module_main.R;
import com.supcon.mes.module_main.model.api.EamAPI;
import com.supcon.mes.module_main.model.bean.EamEntity;
import com.supcon.mes.module_main.model.contract.EamContract;
import com.supcon.mes.module_main.presenter.EamPresenter;
import com.supcon.mes.module_main.ui.adaper.EamListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 设备
 */
@Presenter(value = EamPresenter.class)
public class EamFragment extends BaseRefreshRecyclerFragment<EamEntity> implements EamContract.View {

    @BindByTag("contentView")
    RecyclerView contentView;

    @Override
    protected int getLayoutID() {
        return R.layout.hs_frag_eam;
    }

    @Override
    protected IListAdapter<EamEntity> createAdapter() {
        EamListAdapter eamListAdapter = new EamListAdapter(getActivity());
        return eamListAdapter;
    }

    @Override
    protected void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        super.initView();
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(true);
        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, null));
        contentView.setLayoutManager(new LinearLayoutManager(context));
    }

    @SuppressLint("CheckResult")
    private void refreshList() {

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();
        refreshListController.setOnRefreshPageListener(new OnRefreshPageListener() {
            @Override
            public void onRefresh(int pageIndex) {
                presenterRouter.create(EamAPI.class).getEams(pageIndex);
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();

    }

    @SuppressLint("CheckResult")
    @Override
    public void onResume() {
        super.onResume();
        refreshList();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {

        refreshListController.refreshBegin();
    }

    @Override
    public void getEamsSuccess(CommonBAPListEntity entity) {
        if (entity.result.size() > 0) {
            refreshListController.refreshComplete(entity.result);
        } else {
            refreshListController.refreshComplete(null);
        }
    }

    @Override
    public void getEamsFailed(String errorMsg) {
        SnackbarHelper.showError(rootView, ErrorMsgHelper.msgParse(errorMsg));
        refreshListController.refreshComplete(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
