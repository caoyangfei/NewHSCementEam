package com.supcon.mes.module_score.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.utils.controllers.DatePickController;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.middleware.util.SnackbarHelper;
import com.supcon.mes.module_score.R;
import com.supcon.mes.module_score.model.api.ScoreStaffListAPI;
import com.supcon.mes.module_score.model.contract.ScoreStaffListContract;
import com.supcon.mes.module_score.presenter.ScoreStaffListPresenter;
import com.supcon.mes.module_score.ui.adapter.RankingAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/31
 * ------------- Description -------------
 */
@Router(value = Constant.Router.RANKING)
@Presenter(value = ScoreStaffListPresenter.class)
public class RankingActivity extends BaseRefreshRecyclerActivity implements ScoreStaffListContract.View {

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


    private DatePickController datePickController;
    private RankingAdapter rankingAdapter;

    private final Map<String, Object> queryParam = new HashMap<>();
    private int ranking;
    private String type;

    @Override
    protected IListAdapter createAdapter() {
        rankingAdapter = new RankingAdapter(this);
        return rankingAdapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.ac_rangking;
    }

    @Override
    protected void onInit() {
        super.onInit();
        ranking = getIntent().getIntExtra(Constant.IntentKey.RANKING, -1);
        type = getIntent().getStringExtra(Constant.IntentKey.TYPE);
        rankingAdapter.setRank(ranking);
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(true);
        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, null));
        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(15));
        if (type.equals("BEAM_065/03")) {
            titleText.setText("巡检工排名");
        } else {
            titleText.setText("机修工排名");
        }

        datePickController = new DatePickController(this);
        datePickController.setCycleDisable(true);
        datePickController.setCanceledOnTouchOutside(true);
        datePickController.setSecondVisible(false);
        datePickController.textSize(18);

        dateTv.setText(DateUtil.dateFormat(System.currentTimeMillis(), "yyyy-MM-dd"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        rotationExpandIcon(180, 0);
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
                        onBackPressed();
                    }
                });

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                rotationExpandIcon(0, 180);
                datePickController.listener((year, month, day, hour, minute, second) -> {
                    dateTv.setText(year + "-" + month + "-" + day);
                    rotationExpandIcon(180, 0);
                }).show(System.currentTimeMillis());
            }
        });

        refreshListController.setOnRefreshPageListener(pageIndex -> {
            String url;
            if (type.equals("BEAM_065/03")) {
                url = "/BEAM/patrolWorkerScore/workerScoreHead/repairerScoreList-query.action";
            } else {
                url = "/BEAM/patrolWorkerScore/workerScoreHead/patrolScore-query.action";
            }
            presenterRouter.create(ScoreStaffListAPI.class).patrolScore(url, queryParam, pageIndex);
        });
    }

    @Override
    public void patrolScoreSuccess(CommonBAPListEntity entity) {
        refreshListController.refreshComplete(entity.result);
    }

    @Override
    public void patrolScoreFailed(String errorMsg) {
        SnackbarHelper.showError(rootView, errorMsg);
        refreshListController.refreshComplete(null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void rotationExpandIcon(float from, float to) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);//属性动画
            valueAnimator.setDuration(500);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(valueAnimator1 -> expend.setRotation((Float) valueAnimator1.getAnimatedValue()));
            valueAnimator.start();
        }
    }


}
