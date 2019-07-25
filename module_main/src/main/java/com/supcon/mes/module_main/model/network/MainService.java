package com.supcon.mes.module_main.model.network;

import com.app.annotation.apt.ApiFactory;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.FastQueryCondEntity;
import com.supcon.mes.module_main.model.bean.WaitDealtEntity;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2016/3/23.
 */
@ApiFactory(name = "MainClient")
public interface MainService {

    /**
     * 获取所有待办数量
     *
     * @return
     */
    @GET("/BEAM2/personWork/personworkinfo/personWorkList-query.action")
    Flowable<CommonBAPListEntity<WaitDealtEntity>> getWaitDealt(@Query("fastQueryCond") FastQueryCondEntity fastQueryCondEntity, @QueryMap Map<String, Object> pageQueryMap);

}
