package com.supcon.mes.middleware.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.Staff;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;

/**
 * AcceptanceCheckEntity 验收
 * created by zhangwenshuai1 2018/8/15
 */
public class AcceptanceCheckEntity extends BaseEntity {
    public Long id;
    public Staff checkStaff;//验收人
    public SystemCodeEntity checkResult;//验收结论
    public Long checkTime;//验收时间
    public String remark;//备注

    public Staff getCheckStaff() {
        if (checkStaff == null) {
            checkStaff = new Staff();
        }
        return checkStaff;
    }

    public SystemCodeEntity getCheckResult() {
        if (checkResult == null) {
            checkResult = new SystemCodeEntity();
        }
        return checkResult;
    }
}
