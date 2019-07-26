package com.supcon.mes.module_main.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.supcon.common.BaseConstant;
import com.supcon.common.com_http.util.RxSchedulers;
import com.supcon.common.view.base.fragment.BaseControllerFragment;
import com.supcon.common.view.listener.OnItemChildViewClickListener;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.beans.GalleryBean;
import com.supcon.mes.mbap.beans.LoginEvent;
import com.supcon.mes.mbap.view.CustomAdView;
import com.supcon.mes.mbap.view.CustomSearchView;
import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.event.NFCEvent;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_login.model.bean.WorkInfo;
import com.supcon.mes.module_main.IntentRouter;
import com.supcon.mes.module_main.R;
import com.supcon.mes.module_main.model.api.WaitDealtAPI;
import com.supcon.mes.module_main.model.contract.WaitDealtContract;
import com.supcon.mes.module_main.presenter.WaitDealtPresenter;
import com.supcon.mes.module_main.ui.adaper.WaitDealtAdapter;
import com.supcon.mes.module_main.ui.adaper.WorkAdapter;
import com.supcon.mes.module_main.ui.util.MenuHelper;
import com.supcon.mes.module_main.ui.view.CustomHorizontalSearchTitleBar;
import com.supcon.mes.module_main.ui.view.MenuPopwindow;
import com.supcon.mes.module_main.ui.view.MenuPopwindowBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2017/8/11.
 */
@Presenter(value = WaitDealtPresenter.class)
public class WorkFragment extends BaseControllerFragment implements WaitDealtContract.View {

    @BindByTag("workCustomAd")
    CustomAdView workCustomAd;

    @BindByTag("leftBtn")
    ImageButton leftBtn;

    @BindByTag("customSearchView")
    CustomSearchView customSearchView;

    @BindByTag("searchTitleBar")
    CustomHorizontalSearchTitleBar searchTitleBar;

    //待办
    @BindByTag("waitDealtLayout")
    LinearLayout waitDealtLayout;
    @BindByTag("waitDealtRecycler")
    RecyclerView waitDealtRecycler;

    @BindByTag("workRecycler")
    RecyclerView workRecycler;

    private boolean hidden;
    private WorkAdapter workAdapter;
    private MenuPopwindow menuPopwindow;
    private WaitDealtAdapter waitDealtAdapter;


    @Override
    protected int getLayoutID() {
        return R.layout.hs_frag_work;
    }

    @Override
    protected void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (menuPopwindow != null && menuPopwindow.isShowing()) {
            menuPopwindow.dismiss();
            if (oldPosition != -1)
                workRecycler.getChildAt(oldPosition).setSelected(false);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onResume() {
        super.onResume();
        presenterRouter.create(WaitDealtAPI.class).getWaitDealt(1, 3);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        super.initView();
        leftBtn.setImageResource(R.drawable.ic_top_menu);
        searchTitleBar.disableRightBtn();

        Flowable.timer(20, TimeUnit.MILLISECONDS)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        initAd();
                    }
                });
        View waitTitle = rootView.findViewById(R.id.hs_wait_title);
        ((TextView) waitTitle.findViewById(R.id.contentTitleLabel)).setText("工作提醒");
        ImageView waitMore = waitTitle.findViewById(R.id.contentTitleSettingIc);
        waitMore.setVisibility(View.VISIBLE);
        waitMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentRouter.go(getActivity(), Constant.Router.WAIT_DEALT);
            }
        });
        View workTitle = rootView.findViewById(R.id.hs_work_title);
        ((TextView) workTitle.findViewById(R.id.contentTitleLabel)).setText("我的工作");

        waitDealtRecycler.setLayoutManager(new LinearLayoutManager(context));
        waitDealtAdapter = new WaitDealtAdapter(getActivity());

        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
        workRecycler.setLayoutManager(layoutManager);
        workAdapter = new WorkAdapter(getActivity());
        workRecycler.setAdapter(workAdapter);
        menuPopwindow = new MenuPopwindow(getActivity(), new LinkedList<>());
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        ArrayList<WorkInfo> workInfos = new ArrayList<>();
        WorkInfo workInfo1 = new WorkInfo();
        workInfo1.name = "巡检预警";
        workInfo1.num = 12;
        workInfo1.iconResId = R.drawable.menu_aew_selector;
        workInfos.add(workInfo1);
        WorkInfo workInfo2 = new WorkInfo();
        workInfo2.name = "设备润滑";
        workInfo2.num = 2;
        workInfo2.iconResId = R.drawable.menu_lubricate_selector;
        workInfos.add(workInfo2);
        WorkInfo workInfo3 = new WorkInfo();
        workInfo3.name = "维修执行";
        workInfo3.num = 7;
        workInfo3.iconResId = R.drawable.menu_repair_selector;
        workInfos.add(workInfo3);
        WorkInfo workInfo4 = new WorkInfo();
        workInfo4.name = "工作报表";
        workInfo4.num = 19;
        workInfo4.iconResId = R.drawable.menu_form_selector;
        workInfos.add(workInfo4);
        workAdapter.setList(workInfos);
        workAdapter.notifyDataSetChanged();
    }


    private void initAd() {
        List<GalleryBean> ads = new ArrayList<>();
        GalleryBean galleryBean = new GalleryBean();
        if (EamApplication.isHongshi()) {
            galleryBean.resId = R.drawable.banner_hssn;
        } else {
            galleryBean.resId = R.drawable.banner_hailuo;
        }
        ads.add(galleryBean);
        workCustomAd.setGalleryBeans(ads);
    }

    int oldPosition = -1;

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();
        workAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
                if (oldPosition != -1)
                    workRecycler.getChildAt(oldPosition).setSelected(false);
                if (oldPosition == position) {
                    oldPosition = -1;
                    return;
                }
                switch (position) {
                    case 0:
                        menuPopwindow.refreshList(MenuHelper.getAewMenu(getActivity()));
                        menuPopwindow.showPopupWindow(childView, MenuPopwindow.right, 1);
                        childView.setSelected(true);
                        break;
                    case 1:
                        menuPopwindow.refreshList(MenuHelper.getLubricateMenu(getActivity()));
                        menuPopwindow.showPopupWindow(childView, MenuPopwindow.right, 0);
                        childView.setSelected(true);
                        break;
                    case 2:
                        menuPopwindow.refreshList(MenuHelper.getRepairMenu(getActivity()));
                        menuPopwindow.showPopupWindow(childView, MenuPopwindow.left, 0);
                        childView.setSelected(true);
                        break;
                    case 3:
                        menuPopwindow.refreshList(MenuHelper.getFormMenu(getActivity()));
                        menuPopwindow.showPopupWindow(childView, MenuPopwindow.left, 1);
                        childView.setSelected(true);
                        break;
                }
                oldPosition = position;
            }
        });
        menuPopwindow.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
                menuPopwindow.dismiss();
                MenuPopwindowBean menuPopwindowBean = (MenuPopwindowBean) obj;

                if (!TextUtils.isEmpty(menuPopwindowBean.getRouter())) {
                    IntentRouter.go(getContext(), menuPopwindowBean.getRouter());
                }

            }
        });
        menuPopwindow.setOnDismissListener(new MenuPopwindow(getActivity(), new ArrayList<>()) {
            @Override
            public void onDismiss() {
                super.onDismiss();
                if (oldPosition != -1)
                    workRecycler.getChildAt(oldPosition).setSelected(false);
                oldPosition = -1;
            }
        });
    }

    /**
     * @param
     * @description NFC事件
     * @author zhangwenshuai1
     * @date 2018/6/28
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getNFC(NFCEvent nfcEvent) {
        if (!hidden) {
            LogUtil.d("NFC_TAG", nfcEvent.getNfc());
            Map<String, Object> nfcJson = Util.gsonToMaps(nfcEvent.getNfc());
            if (nfcJson.get("textRecord") == null) {
                ToastUtils.show(context, "标签内容空！");
                return;
            }
            customSearchView.setInput((String) nfcJson.get("textRecord"));
//            doDeal((String) nfcJson.get("textRecord"));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {
        presenterRouter.create(WaitDealtAPI.class).getWaitDealt(1, 3);
    }

    @Override
    public void getWaitDealtSuccess(CommonBAPListEntity entity) {
        if (entity.result.size() > 0) {
//            waitDealtLayout.setVisibility(View.VISIBLE);
            waitDealtAdapter.setList(entity.result);
            waitDealtRecycler.setAdapter(waitDealtAdapter);
        } else {
//            waitDealtLayout.setVisibility(View.GONE);
            waitDealtRecycler.setAdapter((RecyclerView.Adapter) EmptyAdapterHelper.getRecyclerEmptyAdapter(getActivity(), "暂无待办"));
        }
    }

    @Override
    public void getWaitDealtFailed(String errorMsg) {
        LogUtil.e("获取待办失败:" + errorMsg);
        if (errorMsg.contains("401")) {
            SnackbarHelper.showError(rootView, ErrorMsgHelper.msgParse(errorMsg));
        }
//        waitDealtLayout.setVisibility(View.GONE);
        waitDealtRecycler.setAdapter((RecyclerView.Adapter) EmptyAdapterHelper.getRecyclerEmptyAdapter(getActivity(), "暂无待办"));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
