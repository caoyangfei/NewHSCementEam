package com.supcon.mes.middleware.controller;

import com.app.annotation.Presenter;
import com.supcon.common.view.base.controller.BasePresenterController;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.model.api.ModulePowerAPI;
import com.supcon.mes.middleware.model.api.UserPowerCheckAPI;
import com.supcon.mes.middleware.model.bean.BapResultEntity;
import com.supcon.mes.middleware.model.bean.CommonEntity;
import com.supcon.mes.middleware.model.contract.ModulePowerContract;
import com.supcon.mes.middleware.model.contract.UserPowerCheckContract;
import com.supcon.mes.middleware.model.listener.OnFailListener;
import com.supcon.mes.middleware.model.listener.OnSuccessListener;
import com.supcon.mes.middleware.presenter.ModulePowerPresenter;
import com.supcon.mes.middleware.presenter.UserPowerCheckPresenter;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/19
 * ------------- Description -------------
 */
@Presenter(value = {UserPowerCheckPresenter.class})
public class UserPowerCheckController extends BasePresenterController implements UserPowerCheckContract.View, UserPowerCheckAPI {

    private OnSuccessListener<Map<String, Boolean>> mOnSuccessListener;

    public void checkModulePermission(long companyId, String menuOperateCodes, OnSuccessListener<Map<String, Boolean>> onSuccessListener) {
        this.mOnSuccessListener = onSuccessListener;
        presenterRouter.create(UserPowerCheckAPI.class).checkUserPower(companyId, menuOperateCodes);
    }

    @Override
    public void checkUserPower(long companyId, String menuOperateCodes) {
        presenterRouter.create(UserPowerCheckAPI.class).checkUserPower(companyId, menuOperateCodes);
    }

    @Override
    public void checkUserPowerSuccess(Object object) {
        if (mOnSuccessListener != null) {
            Map<String, Boolean> stringTMap = GsonUtil.gsonToMaps(object.toString());
            mOnSuccessListener.onSuccess(stringTMap);
        }
    }

    @Override
    public void checkUserPowerFailed(String errorMsg) {
        LogUtil.e("获取权限失败:" + errorMsg);
    }
}
