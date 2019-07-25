package com.supcon.mes.module_wxgd.util;

import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.FastQueryCondEntity;
import com.supcon.mes.middleware.model.bean.JoinSubcondEntity;
import com.supcon.mes.middleware.util.BAPQueryParamsHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * WXGDFastQueryCondHelper 维修工单快速查询helper
 * created by zhangwenshuai1 2018/9/18
 */
public class WXGDFastQueryCondHelper {

    public static FastQueryCondEntity createFastQueryCond(Map<String, Object> queryMap) {
        if (queryMap.containsKey(Constant.BAPQuery.WXGD_REPAIR_TYPE) || queryMap.containsKey(Constant.BAPQuery.WXGD_PRIORITY) || queryMap.containsKey(Constant.BAPQuery.EAM_NAME)) {
            JoinSubcondEntity joinSubcondEntity = null;
            if (queryMap.containsKey(Constant.BAPQuery.EAM_NAME)) {
                Map<String, Object> nameParam = new HashMap<>();
                nameParam.put(Constant.BAPQuery.EAM_NAME, Objects.requireNonNull(queryMap.get(Constant.BAPQuery.EAM_NAME)));
                joinSubcondEntity = BAPQueryParamsHelper.crateJoinSubcondEntity(nameParam, "EAM_BaseInfo,EAM_ID,BEAM2_WORK_RECORDS,EAMID");
                queryMap.remove(Constant.BAPQuery.EAM_NAME);
            }
            FastQueryCondEntity fastQueryCond = BAPQueryParamsHelper.createJoinFastQueryCond(queryMap);
            if (joinSubcondEntity != null) {
                fastQueryCond.subconds.add(joinSubcondEntity);
            }
            return BAPQueryParamsHelper.createJoinFastQueryCond(queryMap);
        }
        return BAPQueryParamsHelper.createSingleFastQueryCond(queryMap);
    }

}
