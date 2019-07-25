package com.supcon.mes.module_sbda_online.presenter;

import android.text.TextUtils;

import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.BaseSubcondEntity;
import com.supcon.mes.middleware.model.bean.FastQueryCondEntity;
import com.supcon.mes.middleware.model.bean.JoinSubcondEntity;
import com.supcon.mes.middleware.util.BAPQueryParamsHelper;
import com.supcon.mes.module_sbda_online.model.bean.StopPoliceListEntity;
import com.supcon.mes.module_sbda_online.model.contract.StopPoliceContract;
import com.supcon.mes.module_sbda_online.model.network.SBDAOnlineHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StopPolicePresenter extends StopPoliceContract.Presenter {
    @Override
    public void runningGatherList(Map<String, Object> params, int page) {
        FastQueryCondEntity fastQuery = BAPQueryParamsHelper.createSingleFastQueryCond(new HashMap());
        if (params.containsKey(Constant.BAPQuery.OPEN_TIME_START) || params.containsKey(Constant.BAPQuery.OPEN_TIME_STOP)) {
            Map<String, Object> timeParam = new HashMap();
            timeParam.put(Constant.BAPQuery.OPEN_TIME_START, params.get(Constant.BAPQuery.OPEN_TIME_START));
            timeParam.put(Constant.BAPQuery.OPEN_TIME_STOP, params.get(Constant.BAPQuery.OPEN_TIME_STOP));
            List<BaseSubcondEntity> baseSubcondEntities = BAPQueryParamsHelper.crateSubcondEntity(timeParam);
            fastQuery.subconds.addAll(baseSubcondEntities);
        }

        if (params.containsKey(Constant.BAPQuery.ON_OR_OFF)) {
            Map<String, Object> orParam = new HashMap();
            orParam.put(Constant.BAPQuery.ON_OR_OFF, params.get(Constant.BAPQuery.ON_OR_OFF));
            List<BaseSubcondEntity> baseSubcondEntities = BAPQueryParamsHelper.crateSubcondEntity(orParam);
            fastQuery.subconds.addAll(baseSubcondEntities);
        }

        if (params.containsKey(Constant.BAPQuery.EAM_NAME)) {
            Map<String, Object> nameParam = new HashMap();
            nameParam.put(Constant.BAPQuery.EAM_NAME, params.get(Constant.BAPQuery.EAM_NAME));
            nameParam.put(Constant.BAPQuery.IS_MAIN_EQUIP, "1");
            JoinSubcondEntity joinSubcondEntity = BAPQueryParamsHelper.crateJoinSubcondEntity(nameParam, "EAM_BaseInfo,EAM_ID,BEAM2_RUNNING_GATHERS,EAMID");
            fastQuery.subconds.add(joinSubcondEntity);
        }

        fastQuery.modelAlias = "runningGathers";
        Map<String, Object> pageQueryParams = new HashMap<>();
        pageQueryParams.put("page.pageNo", page);
        pageQueryParams.put("page.pageSize", 20);
        pageQueryParams.put("page.maxPageSize", 500);
        mCompositeSubscription.add(SBDAOnlineHttpClient.runningGatherList(fastQuery, pageQueryParams)
                .onErrorReturn(throwable -> {
                    StopPoliceListEntity stopPoliceListEntity = new StopPoliceListEntity();
                    stopPoliceListEntity.errMsg = throwable.toString();
                    return stopPoliceListEntity;
                }).subscribe(stopPoliceListEntity -> {
                    if (TextUtils.isEmpty(stopPoliceListEntity.errMsg)) {
                        getView().runningGatherListSuccess(stopPoliceListEntity);
                    } else {
                        getView().runningGatherListFailed(stopPoliceListEntity.errMsg);
                    }
                }));
    }
}
