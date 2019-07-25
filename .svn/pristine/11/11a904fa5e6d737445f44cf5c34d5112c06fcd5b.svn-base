package com.supcon.mes.module_yhgl.util;

import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.FastQueryCondEntity;
import com.supcon.mes.middleware.util.BAPQueryParamsHelper;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangshizhan on 2018/7/18
 * Email:wangshizhan@supcom.com
 */
public class YHQueryParamsHelper {

    public static FastQueryCondEntity createFastCondEntity(Map<String, Object> queryMap){


        if(queryMap.containsKey(Constant.BAPQuery.YH_AREA) || queryMap.containsKey(Constant.BAPQuery.EAM_NAME)){
            return BAPQueryParamsHelper.createJoinFastQueryCond(queryMap);
        }

        return BAPQueryParamsHelper.createSingleFastQueryCond(queryMap);

    }


}
