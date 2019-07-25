package com.supcon.mes.middleware.model.event;

import com.supcon.common.com_http.BaseEntity;

/**
 * Created by wangshizhan on 2018/3/24.
 * Email:wangshizhan@supcon.com
 */

public class BarcodeEvent extends BaseEntity {

    private String code;

    public BarcodeEvent(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
