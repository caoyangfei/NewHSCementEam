package com.supcon.mes.module_main.model.bean;

import com.supcon.common.com_http.BaseEntity;

/**
 * @author yangfei.cao
 * @ClassName hongShiCementEam
 * @date 2019/7/24
 * ------------- Description -------------
 */
public class WaitDealtEntity extends BaseEntity {

    public Long dataid;     //dataid

    public String eamcode;     //设备编码

    public String eamname;   //设备名

    public Long excutetime;   //下次执行时间

    public String soucretype;  //来源

    public Long nextduration;  //下次执行时长

    public String overdateflag;//是否超期 1 超期  0 正常

    public String state;//状态  派工  执行  验收

    public String processkey;//工作流编码

}
