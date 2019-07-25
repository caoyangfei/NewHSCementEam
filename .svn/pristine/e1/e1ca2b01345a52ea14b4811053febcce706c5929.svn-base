package com.supcon.mes.module_login.ui.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.supcon.common.BaseConstant;
import com.supcon.common.com_http.util.RxSchedulers;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.base.fragment.BaseRefreshRecyclerFragment;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.beans.GalleryBean;
import com.supcon.mes.mbap.beans.LoginEvent;
import com.supcon.mes.mbap.view.CustomAdView;
import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_login.IntentRouter;
import com.supcon.mes.module_login.R;
import com.supcon.mes.module_login.model.api.WorkAPI;
import com.supcon.mes.module_login.model.bean.WorkEntity;
import com.supcon.mes.module_login.model.bean.WorkInfo;
import com.supcon.mes.module_login.model.bean.WorkNumEntity;
import com.supcon.mes.module_login.model.contract.WorkContract;
import com.supcon.mes.module_login.model.event.WorkRefreshEvent;
import com.supcon.mes.module_login.presenter.WorkPresenter;
import com.supcon.mes.module_login.ui.MainActivity;
import com.supcon.mes.module_login.ui.adapter.WorkAdapter;
import com.supcon.mes.module_login.util.WorkHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2017/8/11.
 */
@Presenter(value = {WorkPresenter.class})
public class WorkFragment extends BaseRefreshRecyclerFragment<WorkInfo> implements WorkContract.View{

    WorkAdapter mWorkAdapter;

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("leftBtn")
    ImageButton leftBtn;

    @BindByTag("rightBtn")
    ImageButton rightBtn;

    @BindByTag("workLayout")
    LinearLayout workLayout;

    @BindByTag("workCustomAd")
    CustomAdView workCustomAd;

    GridLayoutManager layoutManager;
    List<WorkInfo> defaultList;
    List<WorkInfo> zzApps;
    List<String> pendingQueryParams = new ArrayList<>();

    int height;
    volatile int count;
    private final int LINE_COUT = 4;
    private final int ITEM_HEIGHT = 90;


    public WorkFragment(){

    }


    @Override
    protected int getLayoutID() {
        return R.layout.frag_work;
    }

    @Override
    protected void onInit() {
        super.onInit();
        refreshListController.setPullDownRefreshEnabled(false);
        refreshListController.setAutoPullDownRefresh(false);
        EventBus.getDefault().register(this);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        super.initView();
        layoutManager = new GridLayoutManager(context, LINE_COUT);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = mWorkAdapter.getItemViewType(position);
                if(viewType == 0){
                    return 1;
                }
                else{
                    return LINE_COUT;
                }
            }
        });
        contentView.setLayoutManager(layoutManager);
//        contentView.addItemDecoration(new GridSpaceItemDecoration(DisplayUtil.dip2px(1, context), LINE_COUT));


        titleText.setText("水泥移动设备");

        leftBtn.setImageResource(R.drawable.sl_top_menu);
//        rightBtn.setImageResource(R.drawable.sl_top_pending);
//        rightBtn.setVisibility(View.VISIBLE);

        defaultList = WorkHelper.getDefaultWorkList(context);

        Flowable.timer(20, TimeUnit.MILLISECONDS)
                .compose(RxSchedulers.io_main())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        initAd();
                    }
                });

    }

    private void initAd() {
        List<GalleryBean> ads = new ArrayList<>();
        GalleryBean galleryBean = new GalleryBean();
//        galleryBean.resId = R.drawable.banner_hssn;
        galleryBean.resId = R.drawable.pic_banner03;
        ads.add(galleryBean);
        workCustomAd.setGalleryBeans(ads);
    }



    @SuppressLint("CheckResult")
    private void refreshList() {

        List<WorkInfo> list = new ArrayList<>();
        if(zzApps!=null){
            list.addAll(zzApps);
        }

        WorkInfo info = new WorkInfo();
        info.viewType = 1;
        info.name = "设备管理";
        info.type = -2;
        info.isOpen = true;
        list.add(info);

//        if(sbWorkInfos.size() != 0){
//            list.addAll(sbWorkInfos);
//        }
        list.addAll(defaultList);
        refreshListController.refreshComplete(list);
        height =  DisplayUtil.dip2px(12, context);
        count = 0;
        Flowable.fromIterable(list)
                .compose(RxSchedulers.io_main())
                .filter(workInfo -> {
                    if(workInfo.viewType == WorkInfo.VIEW_TYPE_TITLE){
                        height += DisplayUtil.dip2px(20, context);
                        count = 0;
                    }
                    return workInfo.viewType == WorkInfo.VIEW_TYPE_CONTENT;
                })
                .subscribe(workInfo -> {

                    if(count == LINE_COUT){
                        count = 0;
                    }

                    if(count == 0 ){
                        height += DisplayUtil.dip2px(ITEM_HEIGHT, context);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) workLayout.getLayoutParams();
                        lp.height = height;
                        workLayout.setLayoutParams(lp);
                    }

                    count++;
                });
    }

    private void startAnimation(View v, int start, int end){
        ValueAnimator animator = ValueAnimator.ofInt(start, end).setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(arg0 -> {
            int value = (int) arg0.getAnimatedValue();
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) v.getLayoutParams();
            lp.height = value;
            v.setLayoutParams(lp);
//            if (value == end) {
//                refreshListController.refreshComplete(defaultList);
//            }

        });

        animator.start();
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();
        refreshListController.setOnRefreshListener(() -> {
//            presenterRouter.create(WorkAPI.class).getAllPendings(DepotApplication.getUserName());
//            presenterRouter.create(WorkAPI.class).getPendingsByModule(pendingQueryParams);

        });
        mWorkAdapter.setOnItemChildViewClickListener((childView, position, action, obj) -> {

            if(childView.getId() == R.id.contentTitleSettingIc){
                if(position == 0){
                    IntentRouter.go(context, Constant.Router.WORK_SETTING);
                }
            }
            else{
                WorkInfo workInfo = (WorkInfo) obj;

                if(TextUtils.isEmpty(workInfo.router) && TextUtils.isEmpty(workInfo.iconUrl)){
                    ToastUtils.show(context, "暂无数据！");
                    return;
                }

                if(Constant.Router.SD.equals(workInfo.router)){
                    String url = "http://"+EamApplication.getIp()+":"+EamApplication.getPort()
                            +Constant.WebUrl.SD_LIST
                            /*+"&mobileMacAddr=" + PhoneUtil.getMacAddressFromIp(context)*/;
                    Bundle bundle = new Bundle();
                    bundle.putString(BaseConstant.WEB_AUTHORIZATION, EamApplication.getAuthorization());
                    bundle.putString(BaseConstant.WEB_COOKIE, EamApplication.getCooki());
                    bundle.putBoolean(BaseConstant.WEB_HAS_REFRESH, true);
                    bundle.putBoolean(BaseConstant.WEB_IS_LIST, true);
//                    bundle.putString(BaseConstant.WEB_URL, EamApplication.getIp()+":"+EamApplication.getPort()+"/BEAMEle/onOrOff/onoroff/eleOffList.action?${getPowerCode('BEAMEle_1.0.0_onOrOff_eleOffList_self')}&&workFlowMenuCode=BEAMEle_1.0.0_onOrOff_eleOffList&openType=page&clientType=mobile");
                    bundle.putString(BaseConstant.WEB_URL, url);

                    IntentRouter.go(context, workInfo.router, bundle);
                }
                else if(Constant.Router.TD.equals(workInfo.router)){
                    String url = "http://"+EamApplication.getIp()+":"+EamApplication.getPort()
                            +Constant.WebUrl.TD_LIST
                            /*+"&mobileMacAddr=" + PhoneUtil.getMacAddressFromIp(context)*/;
                    Bundle bundle = new Bundle();
                    bundle.putString(BaseConstant.WEB_AUTHORIZATION, EamApplication.getAuthorization());
                    bundle.putString(BaseConstant.WEB_COOKIE, EamApplication.getCooki());
//                    bundle.putString(BaseConstant.WEB_URL, EamApplication.getIp()+":"+EamApplication.getPort()+"/BEAMEle/onOrOff/onoroff/eleOnList.action?${getPowerCode('BEAMEle_1.0.0_onOrOff_eleOnList_self')}&workFlowMenuCode=BEAMEle_1.0.0_onOrOff_eleOnList&openType=page&clientType=mobile");
                    bundle.putString(BaseConstant.WEB_URL, url);
                    bundle.putBoolean(BaseConstant.WEB_HAS_REFRESH, true);
                    bundle.putBoolean(BaseConstant.WEB_IS_LIST, true);
                    IntentRouter.go(context, workInfo.router, bundle);
                }
                else
                    IntentRouter.go(getContext(), workInfo.router);
            }

        });


        leftBtn.setOnClickListener(v -> {

            ((MainActivity)getActivity()).toggleDrawer();

        });
    }


    @SuppressLint("CheckResult")
    @Override
    protected void initData() {
        super.initData();
        for(WorkInfo workInfo : defaultList){
            if(TextUtils.isEmpty(workInfo.pendingUrl)){
                continue;
            }
            pendingQueryParams.add(workInfo.pendingUrl);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onResume() {
        super.onResume();
        refreshList();
        presenterRouter.create(WorkAPI.class).getPendingsByModule(pendingQueryParams);

    }

    @Override
    protected IListAdapter<WorkInfo> createAdapter() {
        mWorkAdapter = new WorkAdapter(getContext());
        return mWorkAdapter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent){
//        refreshListController.refreshBegin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWorkRefresh(WorkRefreshEvent loginEvent){
        defaultList = WorkHelper.getDefaultWorkList(context);
        if (zzApps!=null) {
            defaultList.addAll(zzApps);
        }
        refreshList();
    }

    @SuppressLint("CheckResult")
    @Override
    public void getAllPendingsSuccess(WorkEntity workEntity) {
/*        defaultList.clear();
        Flowable.fromIterable(WorkHelper.getDefaultWorkList(context))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(workInfo -> {
                    if (workInfo.isOpen) {
                        defaultList.add(workInfo);
                    }
                }, throwable -> {

                }, () -> getActivity().runOnUiThread(() -> refreshList()));*/

    }

    @SuppressLint("CheckResult")
    @Override
    public void getAllPendingsFailed(String errorMsg) {
        SnackbarHelper.showError(rootView, errorMsg);

    }

    @SuppressLint("CheckResult")
    @Override
    public void getPendingsByModuleSuccess(WorkNumEntity entity) {

        if(mWorkAdapter.getList()!=null)
            Flowable.fromIterable(mWorkAdapter.getList())
                    .subscribeOn(Schedulers.newThread())
                    .filter(workInfo -> entity.url.equals(workInfo.pendingUrl))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            workInfo -> {
                                if(workInfo.num != entity.totalCount) {
                                    workInfo.num = entity.totalCount;
                                    mWorkAdapter.notifyItemChanged(mWorkAdapter.getList().indexOf(workInfo));
                                }}
                    );
    }


    @Override
    public void getPendingsByModuleFailed(String errorMsg) {
        LogUtil.e(ErrorMsgHelper.msgParse(errorMsg));
    }
}
