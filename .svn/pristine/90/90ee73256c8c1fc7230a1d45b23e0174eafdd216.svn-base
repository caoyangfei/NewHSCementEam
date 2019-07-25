package com.supcon.mes.middleware.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.Area;
import com.supcon.mes.middleware.model.bean.AttachmentEntity;
import com.supcon.mes.middleware.model.bean.PendingEntity;
import com.supcon.mes.middleware.model.bean.RepairGroupEntity;
import com.supcon.mes.middleware.model.bean.Staff;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.model.bean.WXGDEam;

import java.util.List;
import java.util.Map;

/**
 * Created by wangshizhan on 2018/8/14
 * Email:wangshizhan@supcom.com
 */
public class YHEntity extends BaseEntity {
    public Map attrMap;
    public Area areaInstall;
    public Staff findStaffID;
    public long cid;
    public String describe;
    public WXGDEam eamID;
    public SystemCodeEntity faultInfoType;
    public SystemCodeEntity faultState;
    public long findTime;
    public long id;
    public PendingEntity pending;
    public String remark;
    public SystemCodeEntity repairType;
    public String tableNo;
    public long tableInfoId;
    public int status;
    public SystemCodeEntity priority;
    public RepairGroupEntity repiarGroup;

    public List<AttachmentEntity> attachmentEntities;

    public WXGDEam getEamID() {
        if (eamID == null) {
            eamID = new WXGDEam();
        }
        return eamID;
    }
}
