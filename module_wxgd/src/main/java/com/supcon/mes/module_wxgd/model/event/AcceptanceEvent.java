package com.supcon.mes.module_wxgd.model.event;

import com.supcon.mes.middleware.model.bean.AcceptanceCheckEntity;
import com.supcon.mes.middleware.model.bean.SparePartEntity;

import java.util.List;

/**
 * SparePartEvent 备件EventBus回调
 * created by zhangwenshuai1 2018/9/6
 */
public class AcceptanceEvent {
    private List<AcceptanceCheckEntity> list;
    private List<Long> dgDeletedIds;

    public AcceptanceEvent(List<AcceptanceCheckEntity> list, List<Long> dgDeletedIds) {
        this.list = list;
        this.dgDeletedIds = dgDeletedIds;
    }

    public List<AcceptanceCheckEntity> getList() {
        return list;
    }

    public List<Long> getDgDeletedIds() {
        return dgDeletedIds;
    }
}
