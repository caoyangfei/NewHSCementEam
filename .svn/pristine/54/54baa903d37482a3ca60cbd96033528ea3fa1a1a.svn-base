package com.supcon.mes.middleware.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.LubricateOil;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;

import java.math.BigDecimal;

/**
 * LubricateOilsEntity 润滑油实体
 * created by zhangwenshuai1 2018/8/15
 */
public class LubricateOilsEntity extends BaseEntity {

    public Long id;
    public LubricateOil lubricate;
    public Float oilQuantity;//数量
    public SystemCodeEntity oilType;//加换油
    public String remark;//备注
    public String version;

    public String content;//内容
    public JWXItem jwxItemID; //业务规则
    public String lubricatingPart;//润滑部位

    public JWXItem getJwxItemID() {
        if (jwxItemID==null) {
            jwxItemID = new JWXItem();
        }
        return jwxItemID;
    }
}
