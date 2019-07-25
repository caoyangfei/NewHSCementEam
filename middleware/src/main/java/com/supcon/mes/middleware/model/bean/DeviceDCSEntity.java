package com.supcon.mes.middleware.model.bean;

import com.google.gson.annotations.Expose;
import com.supcon.common.com_http.BaseEntity;

/**
 * Created by wangshizhan on 2019/5/28
 * Email:wangshizhan@supcom.com
 */
public class DeviceDCSEntity extends BaseEntity {

    public String name;
    public String itemNumber;
    public String valueType;
    public String latestValue;
    public String maxValue;
    public String minValue;

    @Expose
    public boolean isFinished;
}
