package com.supcon.mes.module_sbda_online.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.BapResultEntity;
import com.supcon.mes.middleware.model.bean.FastQueryCondEntity;
import com.supcon.mes.middleware.model.bean.ResultEntity;
import com.supcon.mes.module_sbda_online.model.bean.StatusResultEntity;
import com.supcon.mes.module_sbda_online.model.bean.StopPoliceListEntity;

import java.util.Map;

import retrofit2.http.Query;
import retrofit2.http.QueryMap;

@ContractFactory(entites = {StopPoliceListEntity.class, StatusResultEntity.class})
public interface StopPoliceAPI {
    void runningGatherList(Map<String, Object> params, int page);
    void updateStopPoliceItem(Map<String, String> params);
}
