package com.supcon.mes.module_yhgl.util;

import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.FastQueryCondEntity;
import com.supcon.mes.middleware.model.bean.JoinSubcondEntity;
import com.supcon.mes.middleware.util.BAPQueryParamsHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangshizhan on 2018/7/18
 * Email:wangshizhan@supcom.com
 */
public class YHQueryParamsHelper {

    public static FastQueryCondEntity createFastCondEntity(Map<String, Object> queryMap) {
        FastQueryCondEntity singleFastQueryCond = BAPQueryParamsHelper.createSingleFastQueryCond(new HashMap<>());
        if (queryMap.containsKey(Constant.BAPQuery.YH_AREA) || queryMap.containsKey(Constant.BAPQuery.EAM_NAME)) {
            JoinSubcondEntity joinSubcondEntity = BAPQueryParamsHelper.crateJoinSubcondEntity(queryMap, "EAM_BaseInfo,EAM_ID,BEAM2_FAULT_INFOS,EAMID");
            singleFastQueryCond.subconds.add(joinSubcondEntity);
        }
        return singleFastQueryCond;
    }

}
