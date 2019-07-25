package com.supcon.mes.viber_mogu.model.event;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.viber_mogu.config.ViberMode;

/**
 * Created by wangshizhan on 2019/1/25
 * Email:wangshizhan@supcom.com
 */
public class MGViberResultEvent extends BaseEntity {

    private ViberMode mViberMode;
    private String data;

    public MGViberResultEvent(ViberMode mode, String data){
        this.mViberMode = mode;
        this.data = data;
    }

    public ViberMode getViberMode() {
        return mViberMode;
    }

    public String getData() {
        return data;
    }
}
