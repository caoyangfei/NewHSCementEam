package com.supcon.mes.middleware.ui;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.supcon.common.view.base.activity.BasePresenterActivity;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomSearchView;
import com.supcon.mes.middleware.R;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.api.MultiDepartSelectAPI;
import com.supcon.mes.middleware.model.bean.AreaMultiStageEntity;
import com.supcon.mes.middleware.model.contract.MultiDepartSelectContract;
import com.supcon.mes.middleware.presenter.MultiDepartSelectPresenter;
import com.supcon.mes.middleware.ui.view.CustomMultiStageView;

/**
 * @Author xushiyun
 * @Create-time 7/22/19
 * @Pageage com.supcon.mes.middleware.ui
 * @Project eamtest
 * @Email ciruy.victory@gmail.com
 * @Related-classes
 * @Desc
 */
@Router(value = Constant.Router.MULTI_DEPART_SELECT)
@Presenter(MultiDepartSelectPresenter.class)
public class MultiDepartSelectActivity extends BasePresenterActivity implements MultiDepartSelectContract.View {
    @BindByTag("customMultiStageView")
    CustomMultiStageView customMultiStageView;
    @BindByTag("leftBtn")
    ImageButton leftBtn;
    @BindByTag("titleText")
    TextView titleText;
    @BindByTag("rightBtn")
    ImageButton rightBtn;
    @BindByTag("titleBarLayout")
    RelativeLayout titleBarLayout;
    @BindByTag("customSearchView")
    CustomSearchView customSearchView;

    @Override
    protected void initListener() {
        super.initListener();
        leftBtn.setOnClickListener(v -> back());
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        titleText.setText("组织");
    }

    @Override
    protected void initData() {
        super.initData();
        presenterRouter.create(MultiDepartSelectAPI.class).getDepartmentInfoList("");
    }

    @Override
    protected int getLayoutID() {
        return R.layout.ac_multi_depart_select;
    }

    @Override
    public void getDepartmentInfoListSuccess(AreaMultiStageEntity entity) {
        customMultiStageView.setRootEntity(entity);
    }

    @Override
    public void getDepartmentInfoListFailed(String errorMsg) {

    }
}
