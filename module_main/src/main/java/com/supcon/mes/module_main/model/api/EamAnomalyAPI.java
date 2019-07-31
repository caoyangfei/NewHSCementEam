package com.supcon.mes.module_main.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/30
 * ------------- Description -------------
 */
@ContractFactory(entites = {CommonBAPListEntity.class})
public interface EamAnomalyAPI {
    void getMainWorkCount(String staffID);
}
