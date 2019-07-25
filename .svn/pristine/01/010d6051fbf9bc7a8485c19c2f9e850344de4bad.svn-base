package com.supcon.mes.middleware.controller;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.app.annotation.Presenter;
import com.supcon.common.view.base.controller.BaseDataController;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.middleware.IntentRouter;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.api.PendingQueryAPI;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.PendingEntity;
import com.supcon.mes.middleware.model.bean.WXGDEntity;
import com.supcon.mes.middleware.model.bean.YHEntity;
import com.supcon.mes.middleware.model.contract.PendingQueryContract;
import com.supcon.mes.middleware.presenter.PendingQueryPresenter;

/**
 * Created by wangshizhan on 2019/4/30
 * Email:wangshizhan@supcom.com
 */
@Presenter(PendingQueryPresenter.class)
public class PendingController extends BaseDataController implements PendingQueryContract.View {

    private PendingEntity mPendingEntity;

    public PendingController(Context context) {
        super(context);
    }


    public void queryEntieyAndGo(PendingEntity pendingEntity){

        if(TextUtils.isEmpty(pendingEntity.deploymentName)){
            return;
        }

        mPendingEntity = pendingEntity;
        if(pendingEntity.deploymentName.contains("隐患管理")){
            presenterRouter.create(PendingQueryAPI.class).queryYH(pendingEntity.tableNo);
        }
        else if(pendingEntity.deploymentName.contains("工单")){
            presenterRouter.create(PendingQueryAPI.class).queryWXGD(pendingEntity.tableNo);
        }

    }

    @Override
    public void queryYHSuccess(CommonBAPListEntity entity) {
        if(entity.result.size()!=0){
            Bundle bundle =  new Bundle();
            if(mPendingEntity.deploymentName.contains("隐患管理")){
                bundle.putSerializable(Constant.IntentKey.YHGL_ENTITY, (YHEntity)entity.result.get(0));
                if(mPendingEntity.nowStatus.equals("编辑")){
                    IntentRouter.go(context, Constant.Router.YH_EDIT, bundle);
                }
                else if(mPendingEntity.nowStatus.equals("审核")){
                    IntentRouter.go(context, Constant.Router.YH_VIEW, bundle);
                }
            }
        }
    }

    @Override
    public void queryYHFailed(String errorMsg) {
        LogUtil.e("PendingController:"+errorMsg);
    }

    @Override
    public void queryWXGDSuccess(CommonBAPListEntity entity) {
        if(entity.result.size()!=0){
            Bundle bundle =  new Bundle();
            if(mPendingEntity.deploymentName.contains("工单")){
                WXGDEntity wxgdEntity = (WXGDEntity) entity.result.get(0);
                bundle.putSerializable(Constant.IntentKey.WXGD_ENTITY, wxgdEntity);

                switch (mPendingEntity.nowStatus) {
                    case "接单":
                    case "接单(确认)":
                        IntentRouter.go(context, Constant.Router.WXGD_RECEIVE, bundle);
                        break;
                    case "派工":
                        IntentRouter.go(context, Constant.Router.WXGD_DISPATCHER, bundle);
                        break;

                    case "执行":
                        IntentRouter.go(context, Constant.Router.WXGD_EXECUTE, bundle);
                        break;
                    case "验收":
                        IntentRouter.go(context, Constant.Router.WXGD_ACCEPTANCE, bundle);
                        break;
                }
            }
        }
    }

    @Override
    public void queryWXGDFailed(String errorMsg) {
        LogUtil.e("PendingController:"+errorMsg);
    }
}
