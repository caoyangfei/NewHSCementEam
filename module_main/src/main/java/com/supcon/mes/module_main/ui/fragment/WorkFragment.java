package com.supcon.mes.module_main.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.supcon.common.com_http.util.RxSchedulers;
import com.supcon.common.view.base.fragment.BaseControllerFragment;
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.listener.OnItemChildViewClickListener;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.beans.GalleryBean;
import com.supcon.mes.mbap.beans.LoginEvent;
import com.supcon.mes.mbap.listener.OnTextListener;
import com.supcon.mes.mbap.view.CustomAdView;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.api.EamAPI;
import com.supcon.mes.middleware.model.bean.BapResultEntity;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.CommonEntity;
import com.supcon.mes.middleware.model.bean.CommonListEntity;
import com.supcon.mes.middleware.model.bean.CommonSearchStaff;
import com.supcon.mes.middleware.model.bean.EamType;
import com.supcon.mes.middleware.model.contract.EamContract;
import com.supcon.mes.middleware.model.event.CommonSearchEvent;
import com.supcon.mes.middleware.model.event.NFCEvent;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.presenter.EamPresenter;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_login.model.bean.WorkInfo;
import com.supcon.mes.module_main.IntentRouter;
import com.supcon.mes.module_main.R;
import com.supcon.mes.module_main.model.api.EamAnomalyAPI;
import com.supcon.mes.module_main.model.api.ScoreStaffAPI;
import com.supcon.mes.module_main.model.api.WaitDealtAPI;
import com.supcon.mes.module_main.model.bean.ScoreEntity;
import com.supcon.mes.module_main.model.bean.WaitDealtEntity;
import com.supcon.mes.module_main.model.bean.WorkNumEntity;
import com.supcon.mes.module_main.model.contract.EamAnomalyContract;
import com.supcon.mes.module_main.model.contract.ScoreStaffContract;
import com.supcon.mes.module_main.model.contract.WaitDealtContract;
import com.supcon.mes.module_main.presenter.EamAnomalyPresenter;
import com.supcon.mes.module_main.presenter.ScoreStaffPresenter;
import com.supcon.mes.module_main.presenter.WaitDealtPresenter;
import com.supcon.mes.module_main.ui.MainActivity;
import com.supcon.mes.module_main.ui.adaper.WaitDealtAdapter;
import com.supcon.mes.module_main.ui.adaper.WorkAdapter;
import com.supcon.mes.module_main.ui.util.MenuHelper;
import com.supcon.mes.module_main.ui.view.MenuPopwindow;
import com.supcon.mes.module_main.ui.view.MenuPopwindowBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by wangshizhan on 2017/8/11.
 */
@Presenter(value = {WaitDealtPresenter.class, EamPresenter.class, ScoreStaffPresenter.class, EamAnomalyPresenter.class})
public class WorkFragment extends BaseControllerFragment implements WaitDealtContract.View, EamContract.View, ScoreStaffContract.View
        , MainActivity.WorkOnTouchListener, EamAnomalyContract.View {

    @BindByTag("workCustomAd")
    CustomAdView workCustomAd;

    @BindByTag("titleText")
    TextView titleText;
    @BindByTag("eamTv")
    CustomTextView eamTv;

    //待办
    @BindByTag("waitDealtLayout")
    LinearLayout waitDealtLayout;
    @BindByTag("waitDealtRecycler")
    RecyclerView waitDealtRecycler;
    @BindByTag("workRecycler")
    RecyclerView workRecycler;
    @BindByTag("scoreLayout")
    RelativeLayout scoreLayout;

    @BindByTag("rank")
    TextView rank;
    @BindByTag("score")
    TextView score;

    private boolean hidden;
    private WorkAdapter workAdapter;
    private MenuPopwindow menuPopwindow;
    private WaitDealtAdapter waitDealtAdapter;

    private List<MenuPopwindowBean> aewMenu;
    private List<MenuPopwindowBean> lubricateMenu;
    private List<MenuPopwindowBean> repairMenu;
    private List<MenuPopwindowBean> formMenu;
    private ArrayList<WorkInfo> workInfos;
    private ScoreEntity scoreEntity;
    private CommonSearchStaff proxyStaff;
    private CustomDialog customDialog;
    private String reason;

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
        presenterRouter.create(WaitDealtAPI.class).getWaitDealt(1, 3, new HashMap<>());
        presenterRouter.create(ScoreStaffAPI.class).getPersonScore(String.valueOf(EamApplication.getAccountInfo().getStaffId()));
        presenterRouter.create(EamAnomalyAPI.class).getMainWorkCount(String.valueOf(EamApplication.getAccountInfo().getStaffId()));
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initView() {
        super.initView();
        Flowable.timer(20, TimeUnit.MILLISECONDS)
                .compose(RxSchedulers.io_main())
                .subscribe(aLong -> initAd());
        View waitTitle = rootView.findViewById(R.id.hs_wait_title);
        ((TextView) waitTitle.findViewById(R.id.contentTitleLabel)).setText("工作提醒");
        ImageView waitMore = waitTitle.findViewById(R.id.contentTitleSettingIc);
        waitMore.setVisibility(View.VISIBLE);
        waitMore.setOnClickListener(v -> IntentRouter.go(getActivity(), Constant.Router.WAIT_DEALT));
        View workTitle = rootView.findViewById(R.id.hs_work_title);
        ((TextView) workTitle.findViewById(R.id.contentTitleLabel)).setText("我的工作");

        waitDealtRecycler.setLayoutManager(new LinearLayoutManager(context));
        waitDealtAdapter = new WaitDealtAdapter(getActivity());
        waitDealtRecycler.setAdapter(waitDealtAdapter);

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
        workInfos = new ArrayList<>();
        WorkInfo workInfo1 = new WorkInfo();
        workInfo1.name = "巡检预警";
        workInfo1.iconResId = R.drawable.menu_aew_selector;
        workInfos.add(workInfo1);
        WorkInfo workInfo2 = new WorkInfo();
        workInfo2.name = "设备润滑";
        workInfo2.iconResId = R.drawable.menu_lubricate_selector;
        workInfos.add(workInfo2);
        WorkInfo workInfo3 = new WorkInfo();
        workInfo3.name = "维修执行";
        workInfo3.iconResId = R.drawable.menu_repair_selector;
        workInfos.add(workInfo3);
        WorkInfo workInfo4 = new WorkInfo();
        workInfo4.name = "工作报表";
        workInfo4.iconResId = R.drawable.menu_form_selector;
        workInfos.add(workInfo4);
        workAdapter.setList(workInfos);
        workAdapter.notifyDataSetChanged();

        aewMenu = MenuHelper.getAewMenu(getActivity());
        lubricateMenu = MenuHelper.getLubricateMenu(getActivity());
        repairMenu = MenuHelper.getRepairMenu(getActivity());
        formMenu = MenuHelper.getFormMenu(getActivity());
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

    @SuppressLint({"CheckResult", "ClickableViewAccessibility"})
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
                    menuPopwindow.changeWindowAlfa(1f);
                    return;
                }
                switch (position) {
                    case 0:
                        menuPopwindow.refreshList(aewMenu);
                        menuPopwindow.showPopupWindow(childView, MenuPopwindow.right, 1);
                        childView.setSelected(true);
                        break;
                    case 1:
                        menuPopwindow.refreshList(lubricateMenu);
                        menuPopwindow.showPopupWindow(childView, MenuPopwindow.right, 0);
                        childView.setSelected(true);
                        break;
                    case 2:
                        menuPopwindow.refreshList(repairMenu);
                        menuPopwindow.showPopupWindow(childView, MenuPopwindow.left, 0);
                        childView.setSelected(true);
                        break;
                    case 3:
                        menuPopwindow.refreshList(formMenu);
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
                menuPopwindow.changeWindowAlfa(1f);
                oldPosition = -1;
                MenuPopwindowBean menuPopwindowBean = (MenuPopwindowBean) obj;
                if (!TextUtils.isEmpty(menuPopwindowBean.getRouter())) {
                    IntentRouter.go(getContext(), menuPopwindowBean.getRouter());
                } else {
                    ToastUtils.show(getActivity(), menuPopwindowBean.getName());
                }
            }
        });


        menuPopwindow.setOnDismissListener(new MenuPopwindow(getActivity(), new ArrayList<>()) {
            @Override
            public void onDismiss() {
                //在ontouch中切换透明度 防止点击图标闪动
//                super.onDismiss();
                if (oldPosition != -1)
                    workRecycler.getChildAt(oldPosition).setSelected(false);
            }
        });
        waitDealtAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
                WaitDealtEntity waitDealtEntity = (WaitDealtEntity) obj;
                if (childView.getId() == R.id.waitDealtEntrust) {
                    proxyDialog(waitDealtEntity);
                } else {

                }
            }
        });

        eamTv.setOnChildViewClickListener(new OnChildViewClickListener() {
            @Override
            public void onChildViewClick(View childView, int action, Object obj) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.IntentKey.IS_MAIN_EAM, true);
                IntentRouter.go(getActivity(), Constant.Router.EAM, bundle);
            }
        });

        scoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scoreEntity != null && !TextUtils.isEmpty(scoreEntity.type)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.IntentKey.RANKING, scoreEntity.ranking != null ? scoreEntity.ranking : -1);
                    bundle.putString(Constant.IntentKey.TYPE, scoreEntity.type);
                    IntentRouter.go(getActivity(), Constant.Router.RANKING, bundle);
                } else {
                    ToastUtils.show(getActivity(), "未获取到当前用户评分，不能查看排名！");
                }
            }
        });
    }

    /**
     * 委托代办
     *
     * @param waitDealtEntity
     */
    private void proxyDialog(WaitDealtEntity waitDealtEntity) {
        customDialog = new CustomDialog(context).layout(R.layout.proxy_dialog,
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
                            ToastUtils.show(getActivity(), "请选择委托人");
                            return;
                        }
                        if (waitDealtEntity.pendingid == null) {
                            ToastUtils.show(getActivity(), "未获取当前代办信息");
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
    public void search(CommonSearchEvent commonSearchEvent) {
        if (commonSearchEvent.commonSearchEntity != null) {
            if (commonSearchEvent.commonSearchEntity instanceof CommonSearchStaff) {
                proxyStaff = (CommonSearchStaff) commonSearchEvent.commonSearchEntity;
                CustomTextView person = customDialog.getDialog().findViewById(R.id.proxyPerson);
                person.setContent(Util.strFormat(proxyStaff.name));
            } else if (commonSearchEvent.commonSearchEntity instanceof EamType) {
                EamType eamType = (EamType) commonSearchEvent.commonSearchEntity;
                eamTv.setContent(Util.strFormat(eamType.name));
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.IntentKey.EAM, eamType);
                IntentRouter.go(getActivity(), Constant.Router.EAM_DETAIL, bundle);
            }
        }
    }

    @Override
    public boolean onTouch(MotionEvent ev) {
        boolean isClickWorkRecycler = inRangeOfView(workRecycler, ev);
        if (!isClickWorkRecycler) {
            menuPopwindow.changeWindowAlfa(1f);
            oldPosition = -1;
        }
        return false;
    }

    /**
     * 判断是不是点击在控件上
     *
     * @param view
     * @param ev
     * @return
     */
    public boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
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
            eamTv.setContent((String) nfcJson.get("textRecord"));
            Map<String, Object> params = new HashMap<>();
            params.put(Constant.IntentKey.EAM_CODE, nfcJson.get("textRecord"));
            presenterRouter.create(EamAPI.class).getEam(params, 1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {
        presenterRouter.create(WaitDealtAPI.class).getWaitDealt(1, 3, new HashMap<>());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        presenterRouter.create(WaitDealtAPI.class).getWaitDealt(1, 3, new HashMap<>());
    }

    @Override
    public void getWaitDealtSuccess(CommonBAPListEntity entity) {
        if (entity.result.size() > 0) {
            waitDealtLayout.setVisibility(View.GONE);
        } else {
            waitDealtLayout.setVisibility(View.VISIBLE);
        }
        waitDealtAdapter.setList(entity.result);
        waitDealtAdapter.notifyDataSetChanged();
    }

    @Override
    public void getWaitDealtFailed(String errorMsg) {
        LogUtil.e("获取待办失败:" + errorMsg);
        if (errorMsg.contains("401")) {
            SnackbarHelper.showError(rootView, ErrorMsgHelper.msgParse(errorMsg));
        }
        waitDealtLayout.setVisibility(View.VISIBLE);
        waitDealtAdapter.setList(new ArrayList<>());
        waitDealtAdapter.notifyDataSetChanged();
    }

    @Override
    public void proxyPendingSuccess(BapResultEntity entity) {
        onLoadSuccess("待办委托成功");
        presenterRouter.create(WaitDealtAPI.class).getWaitDealt(1, 3, new HashMap<>());
    }

    @Override
    public void proxyPendingFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }


    @Override
    public void getEamSuccess(CommonListEntity entity) {
        if (entity.result.size() > 0) {
            EamType eamType = (EamType) entity.result.get(0);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.IntentKey.EAM, eamType);
            IntentRouter.go(getActivity(), Constant.Router.EAM_DETAIL, bundle);
            return;
        }
        SnackbarHelper.showError(rootView, "未查询到设备");
    }

    @Override
    public void getEamFailed(String errorMsg) {
        SnackbarHelper.showError(rootView, errorMsg);
    }

    @Override
    public void getPersonScoreSuccess(CommonEntity entity) {
        scoreEntity = (ScoreEntity) entity.result;
        rank.setText(Util.strFormat(scoreEntity.ranking));
        score.setText(Util.big2(scoreEntity.score));
    }

    @Override
    public void getPersonScoreFailed(String errorMsg) {
        LogUtil.e(errorMsg);
    }

    @Override
    public void getMainWorkCountSuccess(CommonBAPListEntity entity) {
        List result = entity.result;
        if (result.size() > 0) {
            updateNum(aewMenu, result, workInfos.get(0));
            updateNum(lubricateMenu, result, workInfos.get(1));
            updateNum(repairMenu, result, workInfos.get(2));
            updateNum(formMenu, result, workInfos.get(3));
            workAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getMainWorkCountFailed(String errorMsg) {
        LogUtil.e(errorMsg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @SuppressLint("CheckResult")
    private void updateNum(List<MenuPopwindowBean> menuPopwindowBeans, List<WorkNumEntity> workNumEntities, WorkInfo workInfo) {
        workInfo.num = 0;
        Flowable.fromIterable(workNumEntities).flatMap(new Function<WorkNumEntity, Publisher<?>>() {
            @Override
            public Publisher<?> apply(WorkNumEntity workNumEntity) throws Exception {
                Flowable<MenuPopwindowBean> filter = Flowable.fromIterable(menuPopwindowBeans)
                        .filter(new Predicate<MenuPopwindowBean>() {
                            @Override
                            public boolean test(MenuPopwindowBean menuPopwindowBean) throws Exception {
                                if (TextUtils.isEmpty(menuPopwindowBean.getTag()) || TextUtils.isEmpty(workNumEntity.tagName)) {
                                    return false;
                                }
                                if (menuPopwindowBean.getTag().equals(workNumEntity.tagName)) {
                                    menuPopwindowBean.setNum(workNumEntity.num);
                                    workInfo.num += workNumEntity.num;
                                }
                                return true;
                            }
                        });
                return filter;
            }
        }).subscribe();
    }
}
