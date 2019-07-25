package com.supcon.mes.middleware.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.common.com_http.NullEntity;

/**
 * Created by wangshizhan on 2019/4/29
 * Email:wangshizhan@supcom.com
 */
@ContractFactory(entites = {NullEntity.class, NullEntity.class})
public interface DeviceTokenAPI {

    void sendLoginDeviceToken(String deviceToken);
    void sendLogoutDeviceToken(String deviceToken);
}
