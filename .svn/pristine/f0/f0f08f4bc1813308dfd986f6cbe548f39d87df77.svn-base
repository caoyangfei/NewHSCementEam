package com.supcon.mes.module_yhgl.util;

import com.supcon.mes.mbap.beans.FilterBean;
import com.supcon.mes.mbap.view.CustomFilterView;
import com.supcon.mes.middleware.EamApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.Area;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.util.SystemCodeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author yangfei.cao
 * @ClassName depot
 * @date 2018/7/6
 * ------------- Description -------------
 */
public class YHFilterHelper {
    public static List<FilterBean> createDateFilter() {

        List<FilterBean> list = new ArrayList<>();

        FilterBean filterBean = new FilterBean();
        filterBean.name = "今天";
        list.add(filterBean);

        filterBean = new FilterBean();
        filterBean.name = "昨天";
        list.add(filterBean);

        filterBean = new FilterBean();
        filterBean.name = "本周";
        list.add(filterBean);

        filterBean = new FilterBean();
        filterBean.name = "本月";
        list.add(filterBean);

        filterBean = new FilterBean();
        filterBean.type = CustomFilterView.VIEW_TYPE_ALL;
        filterBean.name = "时间不限";
        list.add(filterBean);


        return list;
    }

    public static List<FilterBean> createWXTypeFilter() {

        List<FilterBean> list = new ArrayList<>();

//        FilterBean filterBean = new FilterBean();
//        filterBean.name = "日常";
//        list.add(filterBean);
//
//        filterBean = new FilterBean();
//        filterBean.name = "检修";
//        list.add(filterBean);
//
//        filterBean = new FilterBean();
//        filterBean.name = "大修";
//        list.add(filterBean);
        List<SystemCodeEntity> wxTypes = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.YH_WX_TYPE);

        for(SystemCodeEntity entity: wxTypes){
            FilterBean filterBean = new FilterBean();
            filterBean.name = entity.value;
            list.add(filterBean);
        }

        FilterBean filterBean = new FilterBean();
        filterBean.type = CustomFilterView.VIEW_TYPE_ALL;
        filterBean.name = "类型不限";
        list.add(filterBean);


        return list;
    }


    public static List<FilterBean> createAreaFilter() {

        List<FilterBean> list = new ArrayList<>();

        List<Area> areas = EamApplication.dao().getAreaDao().loadAll();

        for(Area area: areas){
            FilterBean filterBean = new FilterBean();
            filterBean.name = area.name;
            list.add(filterBean);
        }

        FilterBean filterBean = new FilterBean();
        filterBean.type = CustomFilterView.VIEW_TYPE_ALL;
        filterBean.name = "区域不限";
        list.add(filterBean);

        return list;
    }

    public static List<FilterBean> createYHTypeFilter() {

        List<FilterBean> list = new ArrayList<>();

        List<SystemCodeEntity> qxTypes = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.QX_TYPE);

        for(SystemCodeEntity entity: qxTypes){
            FilterBean filterBean = new FilterBean();
            filterBean.name = entity.value;
            list.add(filterBean);
        }

        FilterBean filterBean = new FilterBean();
        filterBean.type = CustomFilterView.VIEW_TYPE_ALL;
        filterBean.name = "类型不限";
        list.add(filterBean);

        return list;
    }
    public static List<FilterBean> createStatusFilter(){
        List<String> list = Arrays.asList("编辑中","待上传");
        List<FilterBean> result  =new ArrayList<>();
        FilterBean filterBean;
        for (String entity : list){
            filterBean = new FilterBean();
            filterBean.name = entity;
            result.add(filterBean);
        }
        filterBean = new FilterBean();
        filterBean.name = "状态不限";
        filterBean.type = CustomFilterView.VIEW_TYPE_ALL;
        result.add(filterBean);

        return result;
    }

    public static List<FilterBean> createPriorityFilter(){
        List<FilterBean> list = new ArrayList<>();
        List<SystemCodeEntity> priorityList  = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.YH_PRIORITY);
        FilterBean filterBean;
        for (SystemCodeEntity entity : priorityList){
            filterBean = new FilterBean();
            filterBean.name = entity.value;
            list.add(filterBean);
        }
        filterBean = new FilterBean();
        filterBean.name = "级别不限";
        filterBean.type = CustomFilterView.VIEW_TYPE_ALL;
        list.add(filterBean);

        return list;
    }

}
