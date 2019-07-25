package com.supcon.mes.middleware.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.SystemCodeListEntity;

/**
 * Created by wangshizhan on 2018/7/18
 * Email:wangshizhan@supcom.com
 */
@ContractFactory(entites = {SystemCodeListEntity.class})
public interface SystemCodeAPI {

    void getSystemCodeList(String entityCode);

}