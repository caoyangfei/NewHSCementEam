package com.supcon.mes.viber_mogu.util;

import android.content.Context;

import java.util.Locale;

/**
 * Created by wangshizhan on 2019/2/15
 * Email:wangshizhan@supcom.com
 */
public class LocationUtil {

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

}
