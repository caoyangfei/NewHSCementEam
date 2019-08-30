package com.supcon.mes.module_sbda_online.presenter;

import android.text.TextUtils;

import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.BaseSubcondEntity;
import com.supcon.mes.middleware.model.bean.FastQueryCondEntity;
import com.supcon.mes.middleware.model.bean.JoinSubcondEntity;
import com.supcon.mes.middleware.util.BAPQueryParamsHelper;
import com.supcon.mes.middleware.util.FormDataHelper;
import com.supcon.mes.module_sbda_online.model.bean.StatusResultEntity;
import com.supcon.mes.module_sbda_online.model.bean.StopPoliceListEntity;
import com.supcon.mes.module_sbda_online.model.contract.StopPoliceContract;
import com.supcon.mes.module_sbda_online.model.network.SBDAOnlineHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.RequestBody;

import static com.supcon.mes.middleware.constant.Constant.BAPQuery.STOP_POLICE_EAM_IDS;
import static com.supcon.mes.middleware.constant.Constant.BAPQuery.STOP_POLICE_ID;
import static com.supcon.mes.middleware.constant.Constant.BAPQuery.STOP_POLICE_STAFF_ID;
import static com.supcon.mes.middleware.constant.Constant.BAPQuery.STOP_POLICE_STOP_EXPLAIN;
import static com.supcon.mes.middleware.constant.Constant.BAPQuery.STOP_POLICE_STOP_REASON;
import static com.supcon.mes.middleware.constant.Constant.BAPQuery.STOP_POLICE_STOP_TYPE;

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
        LogUtil.e("ciruy",fastQuery.toString());
        Map<String, Object> pageQueryParams = new HashMap<>();
        pageQueryParams.put("page.pageNo", page);
        pageQueryParams.put("page.pageSize", 20);
        pageQueryParams.put("page.maxPageSize", 500);
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("fastQueryCond", fastQuery);
        Map<String, RequestBody> formBody =
//                new HashMap<>();
                FormDataHelper.createDataFormBody(contentMap);
//        mCompositeSubscription.add(SBDAOnlineHttpClient.runningGatherList(
//                formBody,
//                pageQueryParams)
        mCompositeSubscription.add(SBDAOnlineHttpClient.gatherMobileList(formBody, pageQueryParams)
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
    
    @Override
    public void updateStopPoliceItem(Map<String, String> params) {
//        Long staffId = Long.valueOf(params.get(STOP_POLICE_STAFF_ID));
//        String id = params.get(STOP_POLICE_ID);
//        String stopType = params.get(STOP_POLICE_STOP_TYPE);
//        String stopReason = params.get(STOP_POLICE_STOP_REASON);
//        String stopExplain = params.get(STOP_POLICE_STOP_EXPLAIN);
//        String eamIds = params.get(STOP_POLICE_EAM_IDS);
        
        
        mCompositeSubscription.add(SBDAOnlineHttpClient.setRunningRecord(params)
                .onErrorReturn(new Function<Throwable, StatusResultEntity>() {
                    @Override
                    public StatusResultEntity apply(Throwable throwable) throws Exception {
                        StatusResultEntity statusResultEntity = new StatusResultEntity();
                        statusResultEntity.errMsg = throwable.toString();
                        statusResultEntity.success = false;
                        return statusResultEntity;
                    }
                }).subscribe(new Consumer<StatusResultEntity>() {
                    @Override
                    public void accept(StatusResultEntity statusResultEntity) throws Exception {
                        if (statusResultEntity.status.trim().equals("200")) {
                            getView().updateStopPoliceItemSuccess(statusResultEntity);
                        } else {
                            getView().updateStopPoliceItemFailed(statusResultEntity.errMsg);
                        }
                    }
                }));
    }
}
