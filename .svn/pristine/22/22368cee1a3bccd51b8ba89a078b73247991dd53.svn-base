package com.supcon.mes.viber_mogu.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.common.com_http.NullEntity;
import com.supcon.mes.viber_mogu.model.model.BleSearchEntity;
import com.sytest.app.blemulti.BleDevice;

/**
 * Created by zhangwenshuai1 on 2018/3/12.
 */

@ContractFactory(entites = {BleSearchEntity.class, Boolean.class, String.class, NullEntity.class})
public interface MGViberAPI {

    /**
     * 连接蓝牙
     */
    void search();

    /**
     * 连接蓝牙
     */
    void connect(String address);


    /**
     * 开始测振
     */
    void startViberTest(byte mode, BleDevice bleDevice);


    /**
     * 停止测振
     */
    void stopViberTest();


}
