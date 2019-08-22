package com.supcon.mes.module_olxj.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.com_http.util.RxSchedulers;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.listener.OnRefreshListener;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.common.view.view.loader.base.OnLoaderFinishListener;
import com.supcon.mes.mbap.beans.FilterBean;
import com.supcon.mes.mbap.beans.SheetEntity;
import com.supcon.mes.mbap.constant.ListType;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.utils.controllers.SinglePickController;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomFilterView;
import com.supcon.mes.mbap.view.CustomGalleryView;
import com.supcon.mes.mbap.view.CustomSheetDialog;
import com.supcon.mes.mbap.view.CustomSpinner;
import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.controller.AttachmentDownloadController;
import com.supcon.mes.middleware.controller.ModifyController;
import com.supcon.mes.middleware.model.bean.CommonListEntity;
import com.supcon.mes.middleware.model.bean.DeviceDCSEntity;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.model.bean.WXGDEam;
import com.supcon.mes.middleware.model.bean.XJHistoryEntity;
import com.supcon.mes.middleware.model.bean.XJHistoryEntityDao;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.model.listener.OnSuccessListener;
import com.supcon.mes.middleware.ui.view.CustomRadioSheetDialog;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_olxj.IntentRouter;
import com.supcon.mes.module_olxj.R;
import com.supcon.mes.module_olxj.constant.OLXJConstant;
import com.supcon.mes.module_olxj.controller.OLXJCameraController;
import com.supcon.mes.module_olxj.controller.OLXJTitleController;
import com.supcon.mes.module_olxj.model.api.OLXJExemptionAPI;
import com.supcon.mes.module_olxj.model.api.OLXJWorkSubmitAPI;
import com.supcon.mes.module_olxj.model.bean.OLXJAreaEntity;
import com.supcon.mes.module_olxj.model.bean.OLXJExemptionEntity;
import com.supcon.mes.module_olxj.model.bean.OLXJHistorySheetEntity;
import com.supcon.mes.module_olxj.model.bean.OLXJWorkItemEntity;
import com.supcon.mes.module_olxj.model.contract.OLXJExemptionContract;
import com.supcon.mes.module_olxj.model.contract.OLXJWorkSubmitContract;
import com.supcon.mes.module_olxj.model.event.AreaRefreshEvent;
import com.supcon.mes.module_olxj.presenter.OLXJExemptionPresenter;
import com.supcon.mes.module_olxj.presenter.OLXJWorkSubmitPresenter;
import com.supcon.mes.module_olxj.ui.adapter.OLXJHistorySheetAdapter;
import com.supcon.mes.module_olxj.ui.adapter.OLXJWorkListAdapter;
import com.supcon.mes.sb2.model.event.ThermometerEvent;
import com.supcon.mes.viber_mogu.controller.MGViberController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Publisher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Created by wangshizhan on 2019/1/16
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.OLXJ_WORK_LIST_UNHANDLED)
@Controller(value = {OLXJTitleController.class, OLXJCameraController.class})
@Presenter(value = {OLXJWorkSubmitPresenter.class, OLXJExemptionPresenter.class})
public class OLXJWorkListUnHandledActivity extends BaseRefreshRecyclerActivity<OLXJWorkItemEntity> implements OLXJWorkSubmitContract.View, OLXJExemptionContract.View {

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("leftBtn")
    ImageButton leftBtn;

    @BindByTag("listDeviceFilter")
    CustomFilterView<FilterBean> listDeviceFilter;

    @BindByTag("xjBtnLayout")
    LinearLayout xjBtnLayout;

    @BindByTag("finishedBottomBtn")
    TextView finishedBottomBtn;

    @BindByTag("submitBottomBtn")
    TextView submitBottomBtn;

    @BindByTag("oneKeyFinishBottomBtn")
    TextView oneKeyFinishBottomBtn;

    private ModifyController<OLXJAreaEntity> mModifyController;

    OLXJWorkListAdapter mOLXJWorkListAdapter;
    private SinglePickController<String> mSinglePickController;
    private OLXJAreaEntity mXJAreaEntity;

    private List<String> resultList = new ArrayList<>();  //结果列表
    private List<SystemCodeEntity> conclusionList = new ArrayList<>(); //结论列表
    private List<String> conclusionStrList = new ArrayList<>(); //结论列表Str
    private List<SystemCodeEntity> passReasonList = new ArrayList<>(); //跳过原因
    private List<String> passReasonStrList = new ArrayList<>(); //跳过原因Str

    private String thermometervalue = ""; // 全局测温值

    WXGDEam mEam;
    private OLXJCameraController mCameraController;
    private CustomGalleryView customGalleryView;
    boolean isSmoothScroll = false;

    String mFilterDeviceName = null;
    private Map<String, Integer> devicePositions = new HashMap<>();

    RecyclerView.SmoothScroller smoothScroller;

    private MGViberController mViberController;
    private CustomDialog mViberDialog;

    private AttachmentDownloadController mDownloadController;
    public Map<String, Boolean> isColse = new LinkedHashMap<>();
    private OLXJTitleController titleController;

    @Override
    protected IListAdapter<OLXJWorkItemEntity> createAdapter() {
        mOLXJWorkListAdapter = new OLXJWorkListAdapter(context);
        return mOLXJWorkListAdapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.ac_olxj_work_item_unhandled;
    }

    @Override
    protected void onInit() {
        super.onInit();
        mXJAreaEntity = (OLXJAreaEntity) getIntent().getSerializableExtra(Constant.IntentKey.XJ_AREA_ENTITY);
        EventBus.getDefault().register(this);
        mSinglePickController = new SinglePickController<>(this);
        mSinglePickController.textSize(18);
        mSinglePickController.setCanceledOnTouchOutside(true);

//        cameraManager = new CameraManager(this, Constant.PicType.XJ_PIC);

//        mCameraController = getController(OLXJCameraController.class);
//        mCameraController.init(Constant.IMAGE_SAVE_XJPATH,Constant.PicType.XJ_PIC);

        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        deviceName = null; //清空,已完成页面返回后，因为刷新会重新初始化设备过滤条件
        getWindow().setWindowAnimations(R.style.activityAnimation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        super.initView();
        titleText.setText(mXJAreaEntity.name);
        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(1, context)));

        initEmptyView();
        titleController = getController(OLXJTitleController.class);
    }


    @SuppressLint("CheckResult")
    private void initFilterView() {
        List<FilterBean> filterBeans = new ArrayList<>();
        Flowable.fromIterable(mOLXJWorkListAdapter.getList())
                .subscribe(new Consumer<OLXJWorkItemEntity>() {
                    @Override
                    public void accept(OLXJWorkItemEntity workItemEntity) throws Exception {
                        if (mFilterDeviceName == null && !TextUtils.isEmpty(workItemEntity.title) || mFilterDeviceName != null && !TextUtils.isEmpty(workItemEntity.title) && !mFilterDeviceName.equals(workItemEntity.title)) {
                            mFilterDeviceName = workItemEntity.title;
                            FilterBean filterBean = new FilterBean();
                            filterBean.name = workItemEntity.title;
                            filterBeans.add(filterBean);
                            devicePositions.put(workItemEntity.title, mOLXJWorkListAdapter.getList().indexOf(workItemEntity));
                        }
                    }
                }, throwable -> {
                }, () -> {
                    mFilterDeviceName = null;
                    listDeviceFilter.setData(filterBeans);
                });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();

        smoothScroller = new LinearSmoothScroller(this) {

            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;

            }

        };

        RxView.clicks(leftBtn)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> onBackPressed());

        RxView.clicks(finishedBottomBtn)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> goFinishedXJActivity());

        RxView.clicks(submitBottomBtn)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> submitAreaData());

        RxView.clicks(oneKeyFinishBottomBtn)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> showOneKeyDialog());

        listDeviceFilter.setFilterSelectChangedListener((CustomFilterView.FilterSelectChangedListener) filterBean -> {
            int position = devicePositions.get(filterBean.name);
//                contentView.smoothScrollToPosition(position);
            LinearLayoutManager mManager = (LinearLayoutManager) contentView.getLayoutManager();

            smoothScroller.setTargetPosition(position);

            mManager.startSmoothScroll(smoothScroller);

            isSmoothScroll = true;
            titleController.isCilck(true);
        });

        contentView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isSmoothScroll) {
                        contentView.smoothScrollBy(0, -DisplayUtil.dip2px(110, context));
                        isSmoothScroll = false;
//                        titleController.isCilck(isSmoothScroll);
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    titleController.isCilck(false);
                }
            }
        });


        refreshListController.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                doRefresh(false);
            }
        });


        mOLXJWorkListAdapter.setOnItemChildViewClickListener((childView, position, action, obj) -> {

            OLXJWorkItemEntity xjWorkItemEntity;

            if (obj instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) obj;

                xjWorkItemEntity = (OLXJWorkItemEntity) map.get("obj");

            } else {
                xjWorkItemEntity = (OLXJWorkItemEntity) obj;

            }

            String tag = (String) childView.getTag();

            switch (tag) {
                case "vibrationBtn":
                    showViberDialog(position, xjWorkItemEntity);

                    break;
                case "ufItemSelectResult":
                    if (action == -1) {
                        xjWorkItemEntity.result = null;
                    } else {
                        if (OLXJConstant.MobileEditType.CHECKBOX.equals(xjWorkItemEntity.inputStandardID.editTypeMoblie.id)) {//多选
                            dialogMoreChoice(xjWorkItemEntity, position);
                        } else if (OLXJConstant.MobileEditType.RADIO.equals(xjWorkItemEntity.inputStandardID.editTypeMoblie.id)) {//单选
                            dialogRadioChoice(xjWorkItemEntity, position);
                        } else {
                            showResultPicker(((CustomSpinner) childView).getSpinnerValue(), xjWorkItemEntity, position);
                        }
                    }

                    break;
                case "ufItemConclusion":
                    if (action == -1) {
                        xjWorkItemEntity.conclusionID = null;
                        xjWorkItemEntity.conclusionName = null;
                    } else {
                        showConclusionPicker(xjWorkItemEntity, position);
                    }

                    break;

                case "ufItemSkipBtn":
                    if (!xjWorkItemEntity.ispass) {
                        SnackbarHelper.showError(rootView, "该巡检项不允许跳过");
                    } else {
                        showSkipReasonPicker(xjWorkItemEntity);
                    }
                    break;


                case "thermometerBtn":
                    xjWorkItemEntity.result = thermometervalue;
                    mOLXJWorkListAdapter.notifyItemChanged(position);
                    break;

                case "fHistoryBtn":

                    showHistories(xjWorkItemEntity);

                    break;

                case "ufItemPartEndBtn":

                    showPartFinishDialog(xjWorkItemEntity);
                    break;

                default:
            }

        });

    }

    private void showViberDialog(int position, OLXJWorkItemEntity xjWorkItemEntity) {

        if (mViberDialog == null) {
            mViberDialog = new CustomDialog(context)
                    .layout(R.layout.ly_viber_dialog)
                    .bindClickListener(R.id.viberFinishBtn, null, true);
            mViberController = new MGViberController(mViberDialog.getDialog().getWindow().getDecorView());
            mViberController.onInit();
            mViberController.initView();
            mViberController.initListener();
            mViberController.initData();
        } else {
            mViberController.reset();
        }

        mViberDialog.bindClickListener(R.id.viberFinishBtn, v -> {

            mViberController.stopTest();
            xjWorkItemEntity.realValue = mViberController.getData();
            mOLXJWorkListAdapter.notifyItemChanged(position);
        }, true).show();

    }

    private void goFinishedXJActivity() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.IntentKey.XJ_AREA_ENTITY, mXJAreaEntity);
        IntentRouter.go(context, Constant.Router.OLXJ_WORK_LIST_HANDLED, bundle);
    }

    boolean isAllFinished = true;

    @SuppressLint("CheckResult")
    private void submitAreaData() {
        List<OLXJWorkItemEntity> xjWorkItemEntityList = mOLXJWorkListAdapter.getList(); // 通过列表获取可以保证默认值已经回填到结果上
        Set<Long> set = new HashSet<>();
        Flowable.fromIterable(xjWorkItemEntityList)
                .filter(xjWorkItemEntity -> {
                    if (xjWorkItemEntity.viewType == 0) {
                        return true;
                    }
                    return false;
                })
                .subscribe(xjWorkItemEntity -> set.add(xjWorkItemEntity.id), throwable -> {
                }, () -> {
                    if (set.size() <= 0) {
                        ToastUtils.show(context, "列表无巡检数据完成！");
                    } else {
                        //一键完成
                        new CustomDialog(context)
                                .twoButtonAlertDialog("是否提交完成巡检项？")
                                .bindView(R.id.grayBtn, "否")
                                .bindView(R.id.redBtn, "是")
                                .bindClickListener(R.id.grayBtn, null, true)
                                .bindClickListener(R.id.redBtn, v -> {
                                    try {
                                        for (OLXJWorkItemEntity xjWorkItemEntity : mXJAreaEntity.workItemEntities) {
                                            if (set.contains(xjWorkItemEntity.id)) {
                                                if (!doFinish(xjWorkItemEntity)) {
                                                    return;
                                                }
                                            }
                                        }
                                        onLoading("正在打包并上传巡检数据，请稍后...");
                                        presenterRouter.create(OLXJWorkSubmitAPI.class).uploadOLXJAreaData(mXJAreaEntity);
                                    } catch (Exception e) {
                                        onLoadFailed("完成操作失败！" + e.getMessage());
                                        e.printStackTrace();
                                    } finally {
                                    }
                                }, true)
                                .show();
                    }
                });


//        isAllFinished = true;
//        Flowable.fromIterable(mXJAreaEntity.workItemEntities)
//                .subscribe(new Consumer<OLXJWorkItemEntity>() {
//                    @Override
//                    public void accept(OLXJWorkItemEntity olxjWorkItemEntity) throws Exception {
//                        if (!olxjWorkItemEntity.isFinished) {
//                            isAllFinished = false;
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                }, new Action() {
//                    @Override
//                    public void run() throws Exception {
//
//                        if (isAllFinished)
//                            showSubmitDialog("确定提交巡检数据？");
//                        else
//                            ToastUtils.show(context, "还存在未完成的巡检项，请先完成!");
//                    }
//                });
    }

    private void showSubmitDialog(String msg) {
        new CustomDialog(context)
                .twoButtonAlertDialog(msg)
                .bindView(R.id.grayBtn, "否")
                .bindView(R.id.redBtn, "是")
                .bindClickListener(R.id.grayBtn, null, true)
                .bindClickListener(R.id.redBtn, v -> {
                    onLoading("正在打包并上传巡检数据，请稍后...");
                    presenterRouter.create(OLXJWorkSubmitAPI.class).uploadOLXJAreaData(mXJAreaEntity);
                }, true)
                .show();
    }

    @SuppressLint("CheckResult")
    private void showOneKeyDialog() {
        List<OLXJWorkItemEntity> xjWorkItemEntityList = mOLXJWorkListAdapter.getList(); // 通过列表获取可以保证默认值已经回填到结果上
        Set<Long> set = new HashSet<>();
        Flowable.fromIterable(xjWorkItemEntityList)
                .filter(xjWorkItemEntity -> {
                    if (xjWorkItemEntity.viewType == 0) {
                        return true;
                    }
                    return false;
                })
                .subscribe(xjWorkItemEntity -> set.add(xjWorkItemEntity.id), throwable -> {

                }, () -> {
                    if (set.size() <= 0) {
                        ToastUtils.show(context, "列表无巡检数据完成！");
                    } else {
                        //一键完成
                        new CustomDialog(context)
                                .twoButtonAlertDialog("是否一键完成巡检项？")
                                .bindView(R.id.grayBtn, "否")
                                .bindView(R.id.redBtn, "是")
                                .bindClickListener(R.id.grayBtn, null, true)
                                .bindClickListener(R.id.redBtn, v -> {
                                    try {
                                        for (OLXJWorkItemEntity xjWorkItemEntity : mXJAreaEntity.workItemEntities) {
                                            if (set.contains(xjWorkItemEntity.id)) {
                                                if (!doFinish(xjWorkItemEntity)) {
                                                    return;
                                                }
                                            }
                                        }
                                        onLoading("正在打包并上传巡检数据，请稍后...");
                                        presenterRouter.create(OLXJWorkSubmitAPI.class).uploadOLXJAreaData(mXJAreaEntity);
//                                        onLoadSuccess("完成");
//                                        doRefresh();
//                                        mOLXJWorkListAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        onLoadFailed("完成操作失败！" + e.getMessage());
                                        e.printStackTrace();
                                    } finally {
                                    }
                                }, true)
                                .show();
                    }
                });

    }

    @SuppressLint("CheckResult")
    private void doRefresh(boolean isDcs) {

        if (mDownloadController == null) {
            mDownloadController = new AttachmentDownloadController(Constant.IMAGE_SAVE_PATH);
        }

        if (mXJAreaEntity.eamInspectionGuideImageDocument != null) {
            mXJAreaEntity.eamInspectionGuideImageDocument.name = mXJAreaEntity.eamInspectionGuideImageAttachementInfo;
            mDownloadController.downloadEamPic(mXJAreaEntity.eamInspectionGuideImageDocument, "mobileEAM_1.0.0_work", new OnSuccessListener<File>() {
                @Override
                public void onSuccess(File result) {
                    LogUtil.e("区域指导图片加载成功！");
                    mOLXJWorkListAdapter.notifyItemChanged(0);
                }
            });
        }

        List<OLXJWorkItemEntity> workItems = new ArrayList<>();
        Flowable.fromIterable(mXJAreaEntity.workItemEntities)
                .filter(new Predicate<OLXJWorkItemEntity>() {
                    @Override
                    public boolean test(OLXJWorkItemEntity olxjWorkItemEntity) throws Exception {
                        return !olxjWorkItemEntity.isFinished;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(olxjWorkItemEntity -> {
                    workItems.add(olxjWorkItemEntity);
                }, throwable -> {

                }, () -> {
                    initWorkItemList(workItems);
                    if (workItems.size() == 0 && isDcs) {
                        ToastUtils.show(OLXJWorkListUnHandledActivity.this, "当前设备已关机,无巡检设备!");
                        back();
                        Flowable.timer(300, TimeUnit.MILLISECONDS)
                                .subscribe(v -> {
                                    EventBus.getDefault().post(mXJAreaEntity);
                                    EventBus.getDefault().post(new AreaRefreshEvent());
                                });
                    }
                });

    }

    @SuppressLint("CheckResult")
    private void showPartFinishDialog(OLXJWorkItemEntity olxjWorkItemEntity) {
        List<OLXJWorkItemEntity> xjWorkItemEntityList = mOLXJWorkListAdapter.getList(); // 通过列表获取可以保证默认值已经回填到结果上
        Set<Long> set = new HashSet<>();
        Flowable.fromIterable(xjWorkItemEntityList)
                .filter(xjWorkItemEntity -> {
                    if (xjWorkItemEntity.viewType == 0
                            && xjWorkItemEntity.part != null && xjWorkItemEntity.part.equals(olxjWorkItemEntity.part)
                            && xjWorkItemEntity.eamID != null && olxjWorkItemEntity.eamID != null && xjWorkItemEntity.eamID.name.equals(olxjWorkItemEntity.eamID.name)) {
                        return true;
                    }
                    return false;
                })
                .subscribe(xjWorkItemEntity -> set.add(xjWorkItemEntity.id), throwable -> {

                }, () -> {
                    if (set.size() <= 0) {
                        ToastUtils.show(context, "列表无巡检数据！");
                    } else {
                        //一键完成
                        new CustomDialog(context)
                                .twoButtonAlertDialog("是否完成该部位所有巡检项？")
                                .bindView(R.id.grayBtn, "否")
                                .bindView(R.id.redBtn, "是")
                                .bindClickListener(R.id.grayBtn, null, true)
                                .bindClickListener(R.id.redBtn, v -> {
                                    try {
//                                        onLoading("完成中...");

                                        for (OLXJWorkItemEntity xjWorkItemEntity : mXJAreaEntity.workItemEntities) {
                                            if (set.contains(xjWorkItemEntity.id)) {
                                                if (!doFinish(xjWorkItemEntity)) {
                                                    return;
                                                }
                                            }
                                        }
//                                        onLoadSuccess("完成");
                                        doRefresh(false);

                                    } catch (Exception e) {
                                        onLoadFailed("完成操作失败！" + e.getMessage());
                                        e.printStackTrace();
                                    } finally {
                                    }
                                }, true)
                                .show();
                    }
                });

    }

    @Override
    protected void initData() {
        super.initData();
        conclusionList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.REAL_VALUE);
        passReasonList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.PASS_REASON);
        //解析ObjList 到 StrList
        initStrList(conclusionList, passReasonList);

        mModifyController = new ModifyController<>(mXJAreaEntity);
    }


    /**
     * @param
     * @return
     * @description 一键完成巡检项
     * @author zhangwenshuai1 2018/12/29
     */
    private boolean doFinish(OLXJWorkItemEntity xjWorkItemEntity) {
        if (/*!OLXJConstant.MobileWiLinkState.EXEMPTION_STATE.equals(xjWorkItemEntity.linkState) && */!xjWorkItemEntity.isFinished) { //免检项过滤掉，因为在后续的循环中被免检的项没有从当前列表中移除
            if (xjWorkItemEntity.isphone && !xjWorkItemEntity.isPhonere) {
                SnackbarHelper.showError(rootView, "该巡检项要求拍照");
                return false;
            }
            xjWorkItemEntity.result = TextUtils.isEmpty(xjWorkItemEntity.result) ? xjWorkItemEntity.defaultVal : xjWorkItemEntity.result; //若列表无滚动直接一键完成，默认值不会回填到结果
            if (TextUtils.isEmpty(xjWorkItemEntity.result)) {
                SnackbarHelper.showError(rootView, "巡检部位“" + xjWorkItemEntity.part + "”需要填写结果");
                return false;
            }
            xjWorkItemEntity.endTime = DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
            xjWorkItemEntity.isFinished = true;
            xjWorkItemEntity.linkState = OLXJConstant.MobileWiLinkState.FINISHED_STATE;
            xjWorkItemEntity.staffId = EamApplication.getAccountInfo().staffId;
            //处理结论自动判定
            if (xjWorkItemEntity.autoJudge) {
                xjWorkItemEntity.conclusionName = TextUtils.isEmpty(xjWorkItemEntity.conclusionName) ? "正常" : xjWorkItemEntity.conclusionName;
                xjWorkItemEntity.conclusionID = TextUtils.isEmpty(xjWorkItemEntity.conclusionID) ? "realValue/01" : xjWorkItemEntity.conclusionID;
            }
            if (xjWorkItemEntity.eamID == null) {
                xjWorkItemEntity.eamID = new WXGDEam();
            }
        }
        return true;
    }

    /**
     * @author zhangwenshuai1
     * @date 2018/3/31
     * @description 结果筛选框  单选/是否
     */
    private void showResultPicker(String selectedValue, OLXJWorkItemEntity xjWorkItemEntity, int position) {

        if (xjWorkItemEntity.inputStandardID.valueName == null) {
            ToastUtils.show(context, "结果候选值为空！");
            return;
        }
        resultList = Arrays.asList(xjWorkItemEntity.inputStandardID.valueName.split(","));
        int current = findPosition(selectedValue, resultList);
        mSinglePickController.list(resultList).listener((index, item) -> {
            xjWorkItemEntity.result = resultList.get(index);

            if (xjWorkItemEntity.autoJudge && xjWorkItemEntity.normalRange != null) {

                String[] normalRangeArr = xjWorkItemEntity.normalRange.split(",");
                if (Arrays.asList(normalRangeArr).contains(xjWorkItemEntity.result)) {
                    xjWorkItemEntity.conclusionID = "realValue/01";
                    xjWorkItemEntity.conclusionName = "正常";
                } else {
                    xjWorkItemEntity.conclusionID = "realValue/02";
                    xjWorkItemEntity.conclusionName = "异常";
                }

            }

            mOLXJWorkListAdapter.notifyItemChanged(position);

        }).show(current);

    }

    /**
     * @description 多选
     * @author zhangwenshuai1
     * @date 2018/5/2
     */
    private void dialogMoreChoice(OLXJWorkItemEntity xjWorkItemEntity, int xjPosition) {
        if (xjWorkItemEntity.inputStandardID.valueName == null || xjWorkItemEntity.inputStandardID.valueName.isEmpty()) {
            SnackbarHelper.showError(rootView, "无结果候选值");
            return;
        }


        String[] items = xjWorkItemEntity.inputStandardID.valueName.split(",");  //候选值列表
        List<SheetEntity> list = new ArrayList<>();
        for (String item : items) {
            SheetEntity sheetEntity = new SheetEntity();
            sheetEntity.name = item;
            list.add(sheetEntity);
        }

        List<Boolean> checkedList = new ArrayList<>();
        for (String s : items) {
            if (xjWorkItemEntity.result != null && xjWorkItemEntity.result.contains(s)) {
                checkedList.add(true);
            } else {
                checkedList.add(false);
            }
        }

        new CustomSheetDialog(context)
                .multiSheet("多选列表", list, checkedList)
                .setOnItemChildViewClickListener((childView, position, action, obj) -> {

                    List<SheetEntity> sheetEntities = GsonUtil.jsonToList(obj.toString(), SheetEntity.class);

                    if (sheetEntities != null && sheetEntities.size() > 0) {

                        xjWorkItemEntity.result = "";
                        for (SheetEntity sheetEntity : sheetEntities) {
                            xjWorkItemEntity.result += sheetEntity.name + ",";
                        }

                        xjWorkItemEntity.result = xjWorkItemEntity.result.substring(0, xjWorkItemEntity.result.length() - 1);

                        mOLXJWorkListAdapter.notifyItemChanged(xjPosition);

                    }
                }).show();

    }

    /**
     * @description 单选
     * @author zhangwenshuai1
     * @date 2018/5/2
     */
    private void dialogRadioChoice(OLXJWorkItemEntity xjWorkItemEntity, int xjPosition) {
        if (xjWorkItemEntity.inputStandardID.valueName == null || xjWorkItemEntity.inputStandardID.valueName.isEmpty()) {
            SnackbarHelper.showError(rootView, "无结果候选值");
            return;
        }
        String[] items = xjWorkItemEntity.inputStandardID.valueName.split(",");  //候选值列表
        List<SheetEntity> list = new ArrayList<>();
        for (String item : items) {
            SheetEntity sheetEntity = new SheetEntity();
            sheetEntity.name = item;
            list.add(sheetEntity);
        }
        List<Boolean> checkedList = new ArrayList<>();
        for (String s : items) {
            if (xjWorkItemEntity.result != null && xjWorkItemEntity.result.contains(s)) {
                checkedList.add(true);
            } else {
                checkedList.add(false);
            }
        }
        new CustomRadioSheetDialog(context)
                .multiSheet("单选列表", list, checkedList)
                .setOnItemChildViewClickListener((childView, position, action, obj) -> {
                    List<SheetEntity> sheetEntities = GsonUtil.jsonToList(obj.toString(), SheetEntity.class);
                    if (sheetEntities != null && sheetEntities.size() > 0) {
                        xjWorkItemEntity.result = "";
                        for (SheetEntity sheetEntity : sheetEntities) {
                            xjWorkItemEntity.result += sheetEntity.name + ",";
                        }
                        xjWorkItemEntity.result = xjWorkItemEntity.result.substring(0, xjWorkItemEntity.result.length() - 1);

                        if (xjWorkItemEntity.autoJudge && xjWorkItemEntity.normalRange != null) {
                            String[] normalRangeArr = xjWorkItemEntity.normalRange.split(",");
                            if (Arrays.asList(normalRangeArr).contains(xjWorkItemEntity.result)) {
                                xjWorkItemEntity.conclusionID = "realValue/01";
                            } else {
                                xjWorkItemEntity.conclusionID = "realValue/02";
                            }
                            xjWorkItemEntity.conclusionName = xjWorkItemEntity.result;
                        }
                        mOLXJWorkListAdapter.notifyItemChanged(xjPosition);
                    }
                }).show();
    }

    /**
     * @author zhangwenshuai1
     * @date 2018/3/29
     * @description 结论筛选框
     */
    private void showConclusionPicker(OLXJWorkItemEntity xjWorkItemEntity, int position) {
        int current = findPosition(xjWorkItemEntity, conclusionList);
        if (conclusionStrList.size() <= 0) {
            return;
        }
        mSinglePickController.list(conclusionStrList).listener((index, item) -> {
            SystemCodeEntity realValueInfo = conclusionList.get(index);  //两个List的index位置一样
            xjWorkItemEntity.conclusionID = realValueInfo.id;
            xjWorkItemEntity.conclusionName = realValueInfo.value;
            mOLXJWorkListAdapter.notifyItemChanged(position);
        }).show(current);

    }


    /**
     * @author zhangwenshuai1
     * @date 2018/4/4
     * @description 跳过原因筛选框
     */
    private void showSkipReasonPicker(OLXJWorkItemEntity xjWorkItemEntity) {
        if (passReasonStrList.size() <= 0) {
            return;
        }
        mSinglePickController.list(passReasonStrList).listener((index, item) -> {
            SystemCodeEntity passReasonInfo = passReasonList.get(index);
            xjWorkItemEntity.skipReasonID = passReasonInfo.id;
            xjWorkItemEntity.skipReasonName = passReasonInfo.value;
            xjWorkItemEntity.realispass = true;
            xjWorkItemEntity.isFinished = true;
            xjWorkItemEntity.linkState = OLXJConstant.MobileWiLinkState.SKIP_STATE;//跳检
            xjWorkItemEntity.endTime = DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
            xjWorkItemEntity.staffId = EamApplication.getAccountInfo().staffId;
            xjWorkItemEntity.conclusionID = null;
            xjWorkItemEntity.conclusionName = null;
            xjWorkItemEntity.result = null;
            EventBus.getDefault().post(new RefreshEvent());
        }).show();
    }

    /**
     * @author zhangwenshuai1
     * @date 2018/4/2
     * @description 选择的位置index
     */
    private int findPosition(String value, List<String> list) {

        for (int i = 0; i < list.size(); i++) {
            if (value.equals(list.get(i))) {
                return i;
            }
        }
        return 0;
    }

    /**
     * @author zhangwenshuai1
     * @date 2018/4/4
     * @description 选择的位置index
     */
    private int findPosition(OLXJWorkItemEntity xjWorkItemEntity, List<SystemCodeEntity> list) {

        for (int i = 0; i < list.size(); i++) {
            SystemCodeEntity realValueInfo = list.get(i);
            if (xjWorkItemEntity.conclusionID == null) {
                return 0;
            }
            if (xjWorkItemEntity.conclusionID.equals(realValueInfo.id)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * @author zhangwenshuai1
     * @date 2018/4/9
     * @description 解析上拉菜单数据
     */
    private void initStrList(List<SystemCodeEntity> conclusionList, List<SystemCodeEntity> passReasonList) {
        for (SystemCodeEntity realValueInfo : conclusionList) {
            conclusionStrList.add(realValueInfo.value);
        }
        for (SystemCodeEntity passReasonInfo : passReasonList) {
            passReasonStrList.add(passReasonInfo.value);
        }

    }

    /**
     * @description 历史弹框
     * @author zhangwenshuai1
     * @date 2018/5/8
     */
    @SuppressLint("CheckResult")
    private void showHistories(OLXJWorkItemEntity xjWorkItemEntity) {
        //查询最近三条（本地数据库会因为数据的下载导致历史增多）
        List<XJHistoryEntity> histories = EamApplication.dao().getXJHistoryEntityDao().queryBuilder()
                .where(XJHistoryEntityDao.Properties.WorkItemId.eq(xjWorkItemEntity.id), XJHistoryEntityDao.Properties.Ip.eq(EamApplication.getIp()))
                .orderDesc(XJHistoryEntityDao.Properties.DateTime).limit(3).list();

        if (histories.size() <= 0) {
            SnackbarHelper.showMessage(rootView, "无历史数据可查看");
            return;
        }
        OLXJHistorySheetAdapter adapter = new OLXJHistorySheetAdapter(context);
        List<OLXJHistorySheetEntity> historySheetEntities = new ArrayList<>();

        Flowable.fromIterable(histories)
                .compose(RxSchedulers.io_main())
                .map(xjHistoryEntity -> {

                    OLXJHistorySheetEntity entity = new OLXJHistorySheetEntity();
                    entity.id = xjHistoryEntity.id;
                    entity.content = xjHistoryEntity.content;
                    entity.result = xjHistoryEntity.result;
                    entity.conclusion = xjHistoryEntity.conclusion;
                    entity.dateTime = xjHistoryEntity.dateTime;
                    entity.eamName = xjHistoryEntity.eamName;

                    return entity;
                })
                .subscribe(sheetEntity -> historySheetEntities.add(sheetEntity),
                        throwable -> {
                        },
                        () -> new CustomSheetDialog(context)
                                .list("历史记录", historySheetEntities, adapter)
                                .show());


    }

    /**
     * @author zhangwenshuai1
     * @date 2018/4/9
     * @description 初始化无数据
     */
    private void initEmptyView() {
        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, ""));

    }

    public void removeItem(int pos) {
        mOLXJWorkListAdapter.notifyItemRemoved(pos);
        mOLXJWorkListAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getThermometerVal(ThermometerEvent thermometerEvent) {
        LogUtil.i("ThermometerEvent", thermometerEvent.getThermometerVal());
        thermometervalue = thermometerEvent.getThermometerVal().replace("℃", "");

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(RefreshEvent event) {
        titleController.initView();
        refreshListController.refreshBegin();
    }

    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(CommonListEntity commonListEntity) {
        List<DeviceDCSEntity> deviceDCSEntities = commonListEntity.result;
        if (deviceDCSEntities.size() == 0) {
            return;
        }
        List<OLXJWorkItemEntity> olxjWorkItemEntities = new ArrayList<>();
        Flowable.fromIterable(deviceDCSEntities)
                .flatMap(new Function<DeviceDCSEntity, Publisher<OLXJWorkItemEntity>>() {
                    @Override
                    public Publisher<OLXJWorkItemEntity> apply(DeviceDCSEntity deviceDCSEntity) throws Exception {
                        Flowable<OLXJWorkItemEntity> filter = Flowable.fromIterable(mXJAreaEntity.workItemEntities)
                                .filter(new Predicate<OLXJWorkItemEntity>() {
                                    @Override
                                    public boolean test(OLXJWorkItemEntity olxjWorkItemEntity) throws Exception {
                                        if (!TextUtils.isEmpty(olxjWorkItemEntity.itemnumber)
                                                && olxjWorkItemEntity.itemnumber.trim().equals(deviceDCSEntity.itemNumber.trim())) {
                                            if (!TextUtils.isEmpty(deviceDCSEntity.valueType)) {
                                                if (!TextUtils.isEmpty(deviceDCSEntity.latestValue)) {
                                                    if (TextUtils.isEmpty(olxjWorkItemEntity.defaultVal) || !deviceDCSEntity.latestValue.equals(olxjWorkItemEntity.defaultVal)) {
                                                        olxjWorkItemEntity.result = deviceDCSEntity.latestValue;
                                                        olxjWorkItemEntity.defaultVal = deviceDCSEntity.latestValue;
                                                        //开关机
                                                        if (deviceDCSEntity.valueType.equals("BEAM053/01")) {
                                                            if (deviceDCSEntity.latestValue.equals("开")) {
                                                                olxjWorkItemEntity.isFinished = false;
                                                                isColse.put(olxjWorkItemEntity.itemnumber.trim(), false);
                                                            } else if (deviceDCSEntity.latestValue.equals("关")) {
                                                                olxjWorkItemEntity.isFinished = true;
                                                                isColse.put(olxjWorkItemEntity.itemnumber.trim(), true);
                                                            }
                                                        }
                                                        return true;
                                                    }
                                                }
                                            }

                                        }
                                        return false;
                                    }
                                });
                        return filter;
                    }
                })
                .subscribe(new Consumer<OLXJWorkItemEntity>() {
                    @Override
                    public void accept(OLXJWorkItemEntity olxjWorkItemEntity) throws Exception {
                        if (olxjWorkItemEntity.inputStandardID != null
                                && olxjWorkItemEntity.inputStandardID.editTypeMoblie != null
                                && olxjWorkItemEntity.workItemID != null
                                && (olxjWorkItemEntity.inputStandardID.editTypeMoblie.id.equals("mobileEAM054/02")
                                || olxjWorkItemEntity.inputStandardID.editTypeMoblie.id.equals("mobileEAM054/04"))) {
                            olxjWorkItemEntities.add(olxjWorkItemEntity);
                        }
                    }
                }, throwable -> {
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if (olxjWorkItemEntities.size() > 0) {
                            presenterRouter.create(OLXJExemptionAPI.class).getExemptionEam(olxjWorkItemEntities);
                        } else {
                            doRefresh(true);
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void getExemptionEamSuccess(List entity) {
        Flowable.fromIterable(((List<OLXJExemptionEntity>) entity))
                .flatMap(new Function<OLXJExemptionEntity, Publisher<OLXJWorkItemEntity>>() {
                    @Override
                    public Publisher<OLXJWorkItemEntity> apply(OLXJExemptionEntity olxjExemptionEntity) throws Exception {
                        Flowable<OLXJWorkItemEntity> filter = Flowable.fromIterable(mXJAreaEntity.workItemEntities)
                                .filter(olxjWorkItemEntity -> {
                                    if (!TextUtils.isEmpty(olxjExemptionEntity.getNextItemID().content) && !TextUtils.isEmpty(olxjWorkItemEntity.content)) {
                                        if (olxjExemptionEntity.getNextItemID().content.equals(olxjWorkItemEntity.content)) {
                                            olxjWorkItemEntity.isFinished = isColse.get(olxjExemptionEntity.getItemID().itemNumber.trim());
                                            return true;
                                        }
                                    }
                                    return false;
                                });
                        return filter;
                    }
                })
                .subscribe(olxjWorkItemEntity -> {

                }, throwable -> {
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        doRefresh(true);
                    }
                });
    }

    @Override
    public void getExemptionEamFailed(String errorMsg) {
        LogUtil.e(ErrorMsgHelper.msgParse(errorMsg));
    }

    public void refreshData() {
        Log.i("mXJAreaEntity:", mXJAreaEntity.toString());
        initWorkItemList(mXJAreaEntity.workItemEntities);
    }

    @Override
    public void onBackPressed() {
        if (mModifyController.isModifyed(mXJAreaEntity)) {
            new CustomDialog(context)
                    .twoButtonAlertDialog("页面已经被修改，是否要保存数据?")
                    .bindView(R.id.grayBtn, "保存")
                    .bindView(R.id.redBtn, "离开")
                    .bindClickListener(R.id.grayBtn, v -> {
//                        onLoading("正在打包并上传巡检数据，请稍后...");
//                        presenterRouter.create(OLXJWorkSubmitAPI.class).uploadOLXJAreaData(mXJAreaEntity);
                        back();
                        EventBus.getDefault().post(mXJAreaEntity);
                        EventBus.getDefault().post(new AreaRefreshEvent());
                    }, true)
                    .bindClickListener(R.id.redBtn, v3 -> back(), true)
                    .show();
        } else
            super.onBackPressed();
    }


    @SuppressLint("CheckResult")
    public void initWorkItemList(List<OLXJWorkItemEntity> entity) {

        List<OLXJWorkItemEntity> xjWorkItemEntities = new ArrayList<>();
        OLXJWorkItemEntity headerEntity = new OLXJWorkItemEntity();
        headerEntity.headerPicPath = TextUtils.isEmpty(mXJAreaEntity.eamInspectionGuideImageAttachementInfo) ? "" : Constant.IMAGE_SAVE_PATH + mXJAreaEntity.eamInspectionGuideImageAttachementInfo;
        headerEntity.viewType = ListType.HEADER.value();
        xjWorkItemEntities.add(headerEntity);
        Flowable.fromIterable(entity)
                .filter(workItemEntity -> !workItemEntity.isFinished)
                .subscribe(workItemEntity -> {
                    if (workItemEntity.eamID != null) {
                        if (TextUtils.isEmpty(workItemEntity.eamID.name)) {
                            OLXJWorkItemEntity titleEntity = new OLXJWorkItemEntity();
                            titleEntity.title = workItemEntity.eamID.name;
                            titleEntity.eamID = workItemEntity.eamID;
                            titleEntity.viewType = ListType.TITLE.value();
                            xjWorkItemEntities.add(titleEntity);
                        }

                        if (mEam == null && !TextUtils.isEmpty(workItemEntity.eamID.name)
                                || mEam != null && !mEam.name.equals(workItemEntity.eamID.name)
                                || mEam != null && !mEam.code.equals(workItemEntity.eamID.code)) {
                            mEam = workItemEntity.eamID;
                            OLXJWorkItemEntity titleEntity = new OLXJWorkItemEntity();
                            titleEntity.title = workItemEntity.eamID.name;
                            titleEntity.eamID = workItemEntity.eamID;
                            titleEntity.viewType = ListType.TITLE.value();
                            xjWorkItemEntities.add(titleEntity);
                        }
                    }

                    xjWorkItemEntities.add(workItemEntity);
                }, throwable -> {

                }, () -> {
                    mEam = null;
                    refreshListController.refreshComplete(xjWorkItemEntities);
//                    final Flowable flowable = Flowable.timer(1000, TimeUnit.MILLISECONDS);
//                    flowable.compose(RxSchedulers.io_main())
//                            .subscribe(new Consumer<Long>() {
//                                @Override
//                                public void accept(Long aLong) throws Exception {
//                                    mModifyController = new ModifyController<>(mXJAreaEntity);
//                                }
//                            });
                    initFilterView();
                });

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAreaUpdate(OLXJAreaEntity areaEntity) {
        mXJAreaEntity = areaEntity;
        refreshListController.refreshBegin();
    }

    @Override
    public void uploadOLXJAreaDataSuccess() {
        onLoadSuccessAndExit("数据上传成功！", new OnLoaderFinishListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onLoaderFinished() {
                back();
                Flowable.timer(300, TimeUnit.MILLISECONDS)
                        .subscribe(v -> {
                            EventBus.getDefault().post(mXJAreaEntity);
                            EventBus.getDefault().post(new AreaRefreshEvent());
                        });
            }
        });
    }

    @Override
    public void uploadOLXJAreaDataFailed(String errorMsg) {
        onLoadFailed("上传失败：" + ErrorMsgHelper.msgParse(errorMsg));
    }


}
