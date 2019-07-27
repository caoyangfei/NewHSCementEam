package com.supcon.mes.module_main.ui.util;

import android.content.Context;

import com.supcon.mes.mbap.MBapApp;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.util.Util;
import com.supcon.mes.module_login.model.bean.WorkInfo;
import com.supcon.mes.module_main.ui.view.MenuPopwindowBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MenuHelper {

    public static List<MenuPopwindowBean> getAewMenu(Context context) {
        List<MenuPopwindowBean> works = new ArrayList<>();
        JSONArray worksArray = null;
        try {
            worksArray = new JSONObject(Util.getJson(MBapApp.getAppContext(), "hswork.json")).getJSONArray("aew");
            works = GsonUtil.jsonToList(worksArray.toString(), MenuPopwindowBean.class);
            for (MenuPopwindowBean menuPopwindowBean : works) {
                switch (menuPopwindowBean.getType()) {
                    case Constant.HSWorkType.JHXJ:
                        menuPopwindowBean.setRouter(Constant.Router.JHXJ_LIST);
                        break;
                    case Constant.HSWorkType.LSXJ:
                        menuPopwindowBean.setRouter(Constant.Router.LSXJ_LIST);
                        break;

                    case Constant.HSWorkType.SPARE_EARLY_WARN:
                        menuPopwindowBean.setRouter(Constant.Router.SPARE_EARLY_WARN);
                        break;
                    case Constant.HSWorkType.MAINTENANCE_EARLY_WARN:
                        menuPopwindowBean.setRouter(Constant.Router.MAINTENANCE_EARLY_WARN);
                        break;
                    case Constant.HSWorkType.YHGL:
                        menuPopwindowBean.setRouter(Constant.Router.YH_LIST);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return works;
    }

    public static List<MenuPopwindowBean> getLubricateMenu(Context context) {
        List<MenuPopwindowBean> works = new ArrayList<>();
        JSONArray worksArray = null;
        try {
            worksArray = new JSONObject(Util.getJson(MBapApp.getAppContext(), "hswork.json")).getJSONArray("lubricate");
            works = GsonUtil.jsonToList(worksArray.toString(), MenuPopwindowBean.class);
            for (MenuPopwindowBean menuPopwindowBean : works) {
                switch (menuPopwindowBean.getType()) {
                    case Constant.HSWorkType.PLAN_LUBRICATION_EARLY_WARN:
                        menuPopwindowBean.setRouter(Constant.Router.PLAN_LUBRICATION_EARLY_WARN);
                        break;
                    case Constant.HSWorkType.TEMPORARY_LUBRICATION_EARLY_WARN:
                        menuPopwindowBean.setRouter(Constant.Router.TEMPORARY_LUBRICATION_EARLY_WARN);
                        break;
                    case Constant.HSWorkType.LUBRICATION_EARLY_WARN:
                        menuPopwindowBean.setRouter(Constant.Router.LUBRICATION_EARLY_WARN);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return works;
    }

    public static List<MenuPopwindowBean> getRepairMenu(Context context) {
        List<MenuPopwindowBean> works = new ArrayList<>();
        JSONArray worksArray = null;
        try {
            worksArray = new JSONObject(Util.getJson(MBapApp.getAppContext(), "hswork.json")).getJSONArray("repair");
            works = GsonUtil.jsonToList(worksArray.toString(), MenuPopwindowBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return works;
    }

    public static List<MenuPopwindowBean> getFormMenu(Context context) {
        List<MenuPopwindowBean> works = new ArrayList<>();
        JSONArray worksArray = null;
        try {
            worksArray = new JSONObject(Util.getJson(MBapApp.getAppContext(), "hswork.json")).getJSONArray("form");
            works = GsonUtil.jsonToList(worksArray.toString(), MenuPopwindowBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return works;
    }
}
