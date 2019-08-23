package com.supcon.mes.module_olxj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.CommonEntity;

import java.util.Map;

@ContractFactory(entites = {CommonEntity.class})
public interface OLXJEamCreateTempPotrolTaskAPI {

    void createTempPotrolTaskByEam(Map<String, Object> paramMap);

}
